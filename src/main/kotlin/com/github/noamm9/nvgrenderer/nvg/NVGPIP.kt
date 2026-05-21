package com.github.noamm9.nvgrenderer.nvg

import com.mojang.blaze3d.opengl.GlStateManager
import com.mojang.blaze3d.opengl.GlTexture
import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.client.gui.navigation.ScreenRectangle
import net.minecraft.client.gui.render.pip.PictureInPictureRenderer
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.state.gui.pip.PictureInPictureRenderState
import org.joml.Matrix3x2f
import org.lwjgl.opengl.GL11C
import org.lwjgl.opengl.GL30C
import org.lwjgl.opengl.GL33C

class NVGPIP(buffer: MultiBufferSource.BufferSource): PictureInPictureRenderer<NVGPIP.NVGRenderState>(buffer) {
    private var lastRenderAtNanos = System.nanoTime()

    /** Framebuffer that targets the engine-provided PIP color texture plus a NanoVG-compatible stencil buffer. */
    private var fbo = 0
    private var depthStencil = 0
    private var attachedWidth = 0
    private var attachedHeight = 0

    override fun textureIsReadyToBlit(state: NVGRenderState) = System.nanoTime() - lastRenderAtNanos < FRAME_INTERVAL_NANOS
    override fun getTranslateY(height: Int, guiScale: Int) = height / 2f
    override fun getRenderStateClass() = NVGRenderState::class.java
    override fun getTextureLabel(): String = "nvgrenderer"

    override fun renderToTexture(state: NVGRenderState, poseStack: PoseStack) {
        val window = Minecraft.getInstance().window
        if (window.isIconified) return

        // While renderToTexture runs, the engine points outputColorTextureOverride at this renderer's PIP texture.
        val colorView = RenderSystem.outputColorTextureOverride ?: return
        val width = colorView.getWidth(0).takeIf { it > 0 } ?: return
        val height = colorView.getHeight(0).takeIf { it > 0 } ?: return
        val colorTexId = (colorView.texture() as? GlTexture)?.glId() ?: return

        val rawWidth = width.toFloat()
        val rawHeight = height.toFloat()
        val guiWidth = window.guiScaledWidth.toFloat().coerceAtLeast(1f)
        val dpr = (rawWidth / guiWidth).takeIf { it.isFinite() && it > 0f } ?: 1f

        val previousFbo = GL11C.glGetInteger(GL30C.GL_FRAMEBUFFER_BINDING)
        val previousViewport = IntArray(4)
        GL11C.glGetIntegerv(GL11C.GL_VIEWPORT, previousViewport)

        bindTarget(colorTexId, width, height)

        GlStateManager._viewport(0, 0, width, height)
        GL33C.glBindSampler(0, 0)

        NVG.beginFrame(rawWidth, rawHeight, dpr)
        NVG.push()
        NVG.transform(state.poseMatrix)
        state.callback.run()
        NVG.pop()
        NVG.endFrame()

        GlStateManager._disableDepthTest()
        GlStateManager._disableCull()
        GlStateManager._enableBlend()
        GlStateManager._blendFuncSeparate(770, 771, 1, 0)

        GlStateManager._glBindFramebuffer(GL30C.GL_FRAMEBUFFER, previousFbo)
        GlStateManager._viewport(previousViewport[0], previousViewport[1], previousViewport[2], previousViewport[3])

        lastRenderAtNanos = System.nanoTime()
    }

    /**
     * Binds [fbo] with the engine's PIP color texture as color attachment. NanoVG needs a stencil buffer
     * (NVG_STENCIL_STROKES), and the engine's PIP depth texture is depth-only, so a private depth24-stencil8
     * renderbuffer is attached instead and recreated whenever the target size changes.
     */
    private fun bindTarget(colorTexId: Int, width: Int, height: Int) {
        if (fbo == 0) fbo = GlStateManager.glGenFramebuffers()
        GlStateManager._glBindFramebuffer(GL30C.GL_FRAMEBUFFER, fbo)
        GlStateManager._glFramebufferTexture2D(GL30C.GL_FRAMEBUFFER, GL30C.GL_COLOR_ATTACHMENT0, GL11C.GL_TEXTURE_2D, colorTexId, 0)

        if (depthStencil == 0 || attachedWidth != width || attachedHeight != height) {
            if (depthStencil != 0) GL30C.glDeleteRenderbuffers(depthStencil)
            depthStencil = GL30C.glGenRenderbuffers()
            GL30C.glBindRenderbuffer(GL30C.GL_RENDERBUFFER, depthStencil)
            GL30C.glRenderbufferStorage(GL30C.GL_RENDERBUFFER, GL30C.GL_DEPTH24_STENCIL8, width, height)
            GL30C.glBindRenderbuffer(GL30C.GL_RENDERBUFFER, 0)
            GL30C.glFramebufferRenderbuffer(GL30C.GL_FRAMEBUFFER, GL30C.GL_DEPTH_STENCIL_ATTACHMENT, GL30C.GL_RENDERBUFFER, depthStencil)
            attachedWidth = width
            attachedHeight = height
        }
    }

    override fun close() {
        if (fbo != 0) {
            GlStateManager._glDeleteFramebuffers(fbo)
            fbo = 0
        }
        if (depthStencil != 0) {
            GL30C.glDeleteRenderbuffers(depthStencil)
            depthStencil = 0
        }
        super.close()
    }

    data class NVGRenderState(
        private val width: Int,
        private val height: Int,
        val poseMatrix: Matrix3x2f,
        private val scissor: ScreenRectangle?,
        private val bounds: ScreenRectangle?,
        val callback: Runnable
    ): PictureInPictureRenderState {
        override fun x0() = 0
        override fun y0() = 0
        override fun x1() = width
        override fun y1() = height
        override fun scissorArea() = scissor
        override fun bounds() = bounds
        override fun scale() = 1f
    }

    companion object {
        private const val TARGET_FPS = 144L
        private const val FRAME_INTERVAL_NANOS = 1_000_000_000L / TARGET_FPS

        /**
         * Draws NanoVG content inside the current GuiGraphics transform and scissor state.
         */
        @JvmStatic
        fun GuiGraphicsExtractor.drawNVG(callback: Runnable) {
            val window = Minecraft.getInstance().window
            if (window.isIconified || window.guiScaledWidth <= 0 || window.guiScaledHeight <= 0) return

            val scissor = scissorStack.peek()
            val pose = Matrix3x2f(pose())
            val screenRect = ScreenRectangle(0, 0, guiWidth(), guiHeight()).transformMaxBounds(pose)
            if (screenRect.width <= 0 || screenRect.height <= 0) return

            val bounds = scissor?.intersection(screenRect) ?: screenRect
            if (bounds.width <= 0 || bounds.height <= 0) return

            val state = NVGRenderState(guiWidth(), guiHeight(), pose, scissor, bounds, callback)
            guiRenderState.addPicturesInPictureState(state)
        }
    }
}

package com.github.noamm9.nvgrenderer.demo

import com.github.noamm9.nvgrenderer.helpers.MouseStack
import com.github.noamm9.nvgrenderer.helpers.NvgText
import com.github.noamm9.nvgrenderer.helpers.WorldToScreen
import com.github.noamm9.nvgrenderer.nvg.Image
import com.github.noamm9.nvgrenderer.nvg.NVG
import com.github.noamm9.nvgrenderer.nvg.NVGPIP.Companion.drawNVG
import com.github.noamm9.nvgrenderer.nvg.enums.Gradient
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.screens.Screen
import net.minecraft.network.chat.Component
import java.awt.Color
import kotlin.math.sin

class NVGDemoScreen: Screen(Component.translatable("screen.nvgrenderer.demo")) {
    private val mouseStack = MouseStack(autoUpdateFromMinecraft = false)
    private var iconImage: Image? = null
    private var svgImage: Image? = null

    override fun init() {
        super.init()

        if (iconImage == null) {
            iconImage = NVG.createImage("assets/nvgrenderer/icon.png")
        }

        if (svgImage == null) {
            svgImage = NVG.createImage("assets/nvgrenderer/demo-badge.svg")
        }
    }

    override fun removed() {
        iconImage?.let(NVG::deleteImage)
        svgImage?.let(NVG::deleteImage)
        iconImage = null
        svgImage = null
        super.removed()
    }

    override fun isPauseScreen(): Boolean = false

    override fun render(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTick: Float) {
        super.render(guiGraphics, mouseX, mouseY, partialTick)

        val time = System.nanoTime() / 1_000_000_000f
        val screenWidth = width.toFloat()
        val screenHeight = height.toFloat()
        val panelX = 26f
        val panelY = 28f
        val panelWidth = screenWidth - panelX * 2f
        val panelHeight = screenHeight - panelY * 2f

        guiGraphics.drawNVG {
            NVG.rect(0f, 0f, screenWidth, screenHeight, Color(6, 10, 18, 165))
            NVG.dropShadow(panelX, panelY, panelWidth, panelHeight, (28f), (10f), (20f))
            NVG.gradientRect(
                panelX,
                panelY,
                panelWidth,
                panelHeight,
                Color(16, 21, 33, 240),
                Color(10, 13, 22, 235),
                Gradient.TOP_BOTTOM,
                (20f)
            )
            NVG.hollowRect(panelX, panelY, panelWidth, panelHeight, (1f), Color(90, 122, 184, 110), (20f))

            drawHeader(panelX, panelY, panelWidth)
            drawShapeShowcase(panelX + (20f), panelY + (78f), time)
            drawTextShowcase(panelX + (250f), panelY + (78f))
            drawClipShowcase(panelX + (20f), panelY + (248f), time)
            drawTransformShowcase(panelX + (250f), panelY + (248f), mouseX.toFloat(), mouseY.toFloat(), time)
            drawImageShowcase(panelX + panelWidth - (250f), panelY + (78f), time)
            drawWorldProbe(panelX + panelWidth - (250f), panelY + (248f))
        }
    }

    private fun drawHeader(panelX: Float, panelY: Float, panelWidth: Float) {
        NVG.drawHalfRoundedRect(panelX + (20f), panelY + (18f), (180f), (34f), Color(50, 78, 132, 215), (14f), true)
        NVG.drawHalfRoundedRect(panelX + (20f), panelY + (52f), (180f), (10f), Color(22, 35, 59, 215), (8f), false)

        NvgText.draw("NvgRenderer Demo", panelX + (34f), panelY + (26f), Color.WHITE, (20f))
        NvgText.draw(
            "F8 reopens this screen, ESC closes it",
            panelX + panelWidth - (24f),
            panelY + (24f),
            Color(190, 202, 230),
            (12f),
            align = NvgText.Align.RIGHT
        )
        NvgText.drawGradient(
            "Plain GUI-space rendering",
            panelX + (34f),
            panelY + (58f),
            Color(160, 212, 255),
            Color(132, 255, 208),
            (12f)
        )
    }

    private fun drawShapeShowcase(x: Float, y: Float, time: Float) {
        NvgText.draw("Shapes", x, y, Color.WHITE, (16f))
        val originY = y + (30f)

        NVG.rect(x, originY, (190f), (120f), Color(12, 16, 27, 170), (16f))
        NVG.line(x + (16f), originY + (88f), x + (174f), originY + (88f), (2f), Color(135, 185, 255))
        NVG.circle(x + (42f), originY + (42f), (18f), Color(89, 218, 255))
        NVG.rect(x + (74f), originY + (24f), (42f), (42f), Color(100, 140, 255, 220), (10f))
        NVG.hollowRect(x + (128f), originY + (18f), (42f), (54f), (3f), Color(255, 255, 255, 180), (12f))
        NVG.dropShadow(x + (56f), originY + (90f), (84f), (14f), (12f), (2f), (7f))
        NVG.gradientRect(
            x + (56f),
            originY + (90f),
            (84f),
            (14f),
            Color(255, 115, 115),
            Color(255, 214, 102),
            Gradient.LEFT_RIGHT,
            (7f)
        )

        NVG.push()
        NVG.translate(x + (150f), originY + (92f))
        NVG.rotate(sin(time) * 0.15f)
        NVG.globalAlpha(0.85f)
        NVG.rect(- (18f), - (18f), (36f), (36f), Color(87, 255, 190, 210), (10f))
        NVG.globalAlpha(1f)
        NVG.pop()
    }

    private fun drawTextShowcase(x: Float, y: Float) {
        NvgText.draw("Text", x, y, Color.WHITE, (16f))
        val boxY = y + (30f)

        NVG.rect(x, boxY, (220f), (120f), Color(12, 16, 27, 170), (16f))
        NvgText.draw("Plain text", x + (16f), boxY + (16f), Color.WHITE, (14f))
        NvgText.draw("Centered", x + (110f), boxY + (36f), Color(202, 233, 255), (14f), align = NvgText.Align.CENTER)
        NvgText.draw("Shadow", x + (16f), boxY + (58f), Color(255, 223, 166), (14f), shadow = true)
        NvgText.drawGradient("Gradient", x + (16f), boxY + (82f), Color(157, 212, 255), Color(136, 255, 197), (16f))
        NVG.textGradient(
            "Vertical",
            x + (136f),
            boxY + (80f),
            (16f),
            (70f),
            Color(255, 176, 138),
            Color(255, 244, 165),
            NvgText.font,
            Gradient.TOP_BOTTOM
        )

        val wrapped = NvgText.wrap(
            "This block exercises width, alignment, gradients, wrapping, and formatting cleanup.",
            (188f),
            (12f)
        )

        wrapped.take(3).forEachIndexed { index, line ->
            NvgText.draw(line, x + (16f), boxY + (106f) + index * (13f), Color(190, 202, 230), (12f))
        }
    }

    private fun drawClipShowcase(x: Float, y: Float, time: Float) {
        NvgText.draw("Scissor / clipping", x, y, Color.WHITE, (16f))
        val boxY = y + (30f)

        NVG.rect(x, boxY, (190f), (120f), Color(12, 16, 27, 170), (16f))
        NVG.pushScissor(x + (12f), boxY + (12f), (166f), (96f))
        for (index in 0 until 7) {
            val itemY = boxY + (18f) + index * (22f) + sin(time * 1.8f + index) * (10f)
            val color = if (index % 2 == 0) Color(78, 124, 214, 180) else Color(56, 92, 162, 180)
            NVG.rect(x + (18f), itemY, (148f), (16f), color, (7f))
            NvgText.draw("Row ${index + 1}", x + (28f), itemY + (4f), Color.WHITE, (12f))
            NVG.circle(x + (154f), itemY + (8f), (5f), Color(255, 225, 145))
        }
        NVG.popScissor()
        NVG.hollowRect(x + (12f), boxY + (12f), (166f), (96f), (1f), Color(255, 255, 255, 80), (10f))
    }

    private fun drawTransformShowcase(x: Float, y: Float, mouseX: Float, mouseY: Float, time: Float) {
        NvgText.draw("Transforms / hover", x, y, Color.WHITE, (16f))
        val boxY = y + (30f)
        val scale = 1.15f + (sin(time * 1.4f) * 0.08f)

        mouseStack.update(mouseX.toDouble(), mouseY.toDouble())
        mouseStack.push()
        mouseStack.translate(x + (22f), boxY + (20f))
        mouseStack.scale(scale)

        val hovered = mouseStack.x in 0.0 .. (120f).toDouble() && mouseStack.y in 0f .. (54f)

        NVG.rect(x, boxY, (220f), (120f), Color(12, 16, 27, 170), (16f))
        NVG.push()
        NVG.translate(x + (22f), boxY + (20f))
        NVG.scale(scale)
        NVG.rect(0f, 0f, (120f), (54f), if (hovered) Color(77, 170, 122, 230) else Color(62, 78, 112, 220), (12f))
        NVG.hollowRect(0f, 0f, (120f), (54f), (2f), Color.WHITE, (12f))
        NvgText.draw(if (hovered) "Hovered" else "Move mose here", (60f), (18f), Color.WHITE, (13f), align = NvgText.Align.CENTER)
        NvgText.draw(
            "${mouseStack.x.toInt()}, ${mouseStack.y.toInt()}",
            (60f),
            (34f),
            Color(232, 239, 255),
            (12f),
            align = NvgText.Align.CENTER
        )
        NVG.pop()
        mouseStack.pop()

        NvgText.draw("MouseStack follows translate + scale", x + (16f), boxY + (94f), Color(190, 202, 230), (12f))
    }

    private fun drawImageShowcase(x: Float, y: Float, time: Float) {
        NvgText.draw("Images", x, y, Color.WHITE, (16f))
        val boxY = y + (30f)

        NVG.rect(x, boxY, (220f), (120f), Color(12, 16, 27, 170), (16f))
        iconImage?.let { NVG.image(it, x + (18f), boxY + (18f), (56f), (56f), (14f)) }
        svgImage?.let { NVG.image(it, x + (90f), boxY + (18f), (56f), (56f), (14f)) }

        NVG.push()
        NVG.translate(x + (178f), boxY + (46f))
        NVG.rotate(sin(time * 1.3f) * 0.25f)
        svgImage?.let { NVG.image(it, - (20f), - (20f), (40f), (40f)) }
        NVG.pop()

        NvgText.draw("PNG", x + (46f), boxY + (86f), Color(190, 202, 230), (12f), align = NvgText.Align.CENTER)
        NvgText.draw("SVG", x + (118f), boxY + (86f), Color(190, 202, 230), (12f), align = NvgText.Align.CENTER)
        NvgText.draw("Animated", x + (178f), boxY + (86f), Color(190, 202, 230), (12f), align = NvgText.Align.CENTER)
    }

    private fun drawWorldProbe(x: Float, y: Float) {
        NvgText.draw("World projection", x, y, Color.WHITE, (16f))
        val boxY = y + (30f)

        NVG.rect(x, boxY, (220f), (120f), Color(12, 16, 27, 170), (16f))

        val client = Minecraft.getInstance()
        val probe = client.hitResult?.location?.let(WorldToScreen::project)

        if (probe != null && probe.onScreen) {
            NvgText.draw("Looking at a projected point", x + (16f), boxY + (16f), Color(190, 202, 230), (12f))
            NvgText.draw("Screen: ${probe.x.toInt()}, ${probe.y.toInt()}", x + (16f), boxY + (34f), Color.WHITE, (12f))
            NvgText.draw("Depth: %.2f".format(probe.depth), x + (16f), boxY + (52f), Color.WHITE, (12f))
            NVG.circle(probe.x, probe.y, (7f), Color(255, 96, 96, 220))
            NVG.hollowRect(x + (16f), boxY + (72f), (188f), (28f), (1f), Color(255, 255, 255, 90), (10f))
            NvgText.draw("Red marker shold sit on yor crosshair target", x + (110f), boxY + (80f), Color(255, 220, 190), (12f), align = NvgText.Align.CENTER)
        }
        else {
            NvgText.draw("Look at a block or entity in-world.", x + (16f), boxY + (22f), Color.WHITE, (13f))
            NvgText.draw("If projection is valid, this panel shows", x + (16f), boxY + (46f), Color(190, 202, 230), (12f))
            NvgText.draw("screen coordinates and a red marker.", x + (16f), boxY + (62f), Color(190, 202, 230), (12f))
        }
    }
}

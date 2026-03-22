package com.github.noamm9.nvgrenderer.batchers.multipass

import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics

object MultiPassBatcher {
    private val callbacks = HashSet<(MultiPassContext) -> Unit>()

    fun addCallback(cb: (MultiPassContext) -> Unit) = callbacks.add(cb)
    fun removeCallback(cb: (MultiPassContext) -> Unit) = callbacks.remove(cb)

    @JvmStatic
    fun endRenderHook(context: GuiGraphics, mc: Minecraft) {
        if (callbacks.isEmpty()) return
        val frame = MultiPassFrame()
        callbacks.forEach { it(frame) }
        frame.submit(context, mc)
    }
}
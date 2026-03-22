package com.github.noamm9.nvgrenderer.batchers.multipass

import net.minecraft.client.gui.GuiGraphics

interface MultiPassContext {
    fun push()
    fun pop()
    fun translate(x: Number, y: Number)
    fun scale(x: Number, y: Number)
    fun scale(n: Number)
    fun pushScissor(x: Number, y: Number, w: Number, h: Number)
    fun popScissor()
    fun nvg(block: () -> Unit)
    fun gui(block: (GuiGraphics) -> Unit)
}
package com.github.noamm9.nvgrenderer.batchers.multipass

import net.minecraft.client.gui.GuiGraphics

sealed class PassCommand {
    data object Push: PassCommand()
    data object Pop: PassCommand()
    data class Translate(val x: Float, val y: Float): PassCommand()
    data class Scale(val x: Float, val y: Float): PassCommand()
    data class PushScissor(val x: Float, val y: Float, val w: Float, val h: Float): PassCommand()
    data object PopScissor: PassCommand()
    data class NvgDraw(val block: () -> Unit): PassCommand()
    data class GuiDraw(val block: (GuiGraphics) -> Unit): PassCommand()
}
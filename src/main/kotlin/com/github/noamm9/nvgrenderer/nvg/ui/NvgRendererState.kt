package com.github.noamm9.nvgrenderer.nvg.ui

import com.github.noamm9.nvgrenderer.nvg.NVG

object NvgRendererState {
    fun pushScissor(x: Float, y: Float, w: Float, h: Float) = NVG.pushScissor(x, y, w, h)
    fun popScissor() = NVG.popScissor()
}

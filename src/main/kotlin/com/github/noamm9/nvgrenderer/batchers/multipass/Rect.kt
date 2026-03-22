package com.github.noamm9.nvgrenderer.batchers.multipass

data class Rect(val minX: Float, val minY: Float, val maxX: Float, val maxY: Float) {
    val isEmpty: Boolean = maxX <= minX || maxY <= minY
}
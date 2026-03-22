package com.github.noamm9.nvgrenderer.nvg.ui

import com.github.noamm9.nvgrenderer.nvg.NVG
import java.awt.Color

object NvgTooltip {
    private var text: String? = null
    private var x = 0f
    private var y = 0f

    fun show(text: String, x: Float, y: Float) {
        this.text = text
        this.x = x
        this.y = y
    }

    fun clear() {
        text = null
    }

    fun render() {
        val t = text ?: return
        val padding = 12f
        val maxWidth = 440f
        val lines = NvgText.wrap(t, maxWidth)
        val width = lines.maxOfOrNull { NvgText.width(it) }?.plus(padding * 2f) ?: 0f
        val height = lines.size * NvgText.lineHeight + padding * 2f

        val drawX = x + 12f
        val drawY = y + 8f

        NVG.rect(drawX, drawY, width, height, Color(15, 15, 15, 230), 8f)
        NVG.rect(drawX, drawY, width, 3f, Color(255, 255, 255, 30))

        var ty = drawY + padding
        lines.forEach { line ->
            NvgText.draw(line, drawX + padding, ty, Color.WHITE)
            ty += NvgText.lineHeight
        }
    }
}

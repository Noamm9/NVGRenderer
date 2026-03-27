package com.github.noamm9.nvgrenderer.helpers

import kotlin.test.Test
import kotlin.test.assertEquals

class NvgTextTest {
    @Test
    fun `stripFormatting removes minecraft formatting codes`() {
        assertEquals("Hello World", NvgText.stripFormatting("\u00A7aHello \u00A7lWorld"))
    }

    @Test
    fun `stripFormatting removes malformed replacement plus section sequences`() {
        assertEquals("Hello", NvgText.stripFormatting("\uFFFD\u00A7aHello"))
    }

    @Test
    fun `stripFormatting leaves normal text alone`() {
        assertEquals("Plain text", NvgText.stripFormatting("Plain text"))
    }
}

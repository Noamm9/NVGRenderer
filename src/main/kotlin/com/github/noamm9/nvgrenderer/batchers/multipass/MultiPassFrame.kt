package com.github.noamm9.nvgrenderer.batchers.multipass

import com.github.noamm9.nvgrenderer.nvg.PIPNVG
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics

class MultiPassFrame: MultiPassContext {
    private val commands = ArrayList<PassCommand>(64)

    override fun push() {
        commands.add(PassCommand.Push)
    }

    override fun pop() {
        commands.add(PassCommand.Pop)
    }

    override fun translate(x: Number, y: Number) {
        commands.add(PassCommand.Translate(x.toFloat(), y.toFloat()))
    }

    override fun scale(x: Number, y: Number) {
        commands.add(PassCommand.Scale(x.toFloat(), y.toFloat()))
    }

    override fun scale(n: Number) {
        commands.add(PassCommand.Scale(n.toFloat(), n.toFloat()))
    }

    override fun pushScissor(x: Number, y: Number, w: Number, h: Number) {
        commands.add(PassCommand.PushScissor(x.toFloat(), y.toFloat(), w.toFloat(), h.toFloat()))
    }

    override fun popScissor() {
        commands.add(PassCommand.PopScissor)
    }

    override fun nvg(block: () -> Unit) {
        commands.add(PassCommand.NvgDraw(block))
    }

    override fun gui(block: (GuiGraphics) -> Unit) {
        commands.add(PassCommand.GuiDraw(block))
    }

    fun submit(context: GuiGraphics, mc: Minecraft) {
        if (commands.isEmpty()) return
        PIPNVG.drawNVG(context, 0, 0, mc.window.screenWidth, mc.window.screenHeight) {
            MultiPassHandler.executeNVG(commands)
        }

        MultiPassHandler.executeGui(commands, context)
    }
}
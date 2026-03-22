package com.github.noamm9.nvgrenderer

import com.github.noamm9.nvgrenderer.batchers.MouseBatcher
import com.github.noamm9.nvgrenderer.batchers.multipass.MultiPassBatcher
import com.github.noamm9.nvgrenderer.helpers.MouseStack
import com.github.noamm9.nvgrenderer.nvg.NVG
import com.github.noamm9.nvgrenderer.nvg.PIPNVG
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.rendering.v1.SpecialGuiElementRegistry
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.network.chat.Component
import java.awt.Color

class Nvgrenderer: ClientModInitializer {
    override fun onInitializeClient() {
        SpecialGuiElementRegistry.register { PIPNVG(it.vertexConsumers()) }
    }
}

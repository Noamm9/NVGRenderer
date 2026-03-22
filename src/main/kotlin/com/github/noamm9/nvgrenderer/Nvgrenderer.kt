package com.github.noamm9.nvgrenderer

import com.github.noamm9.nvgrenderer.batchers.NVGBatcher
import com.github.noamm9.nvgrenderer.nvg.NVG
import com.github.noamm9.nvgrenderer.nvg.PIPNVG
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.rendering.v1.SpecialGuiElementRegistry
import java.awt.Color

class Nvgrenderer: ClientModInitializer {
    override fun onInitializeClient() {
        SpecialGuiElementRegistry.register { PIPNVG(it.vertexConsumers()) }

        NVGBatcher.addCallback {
            NVG.rect(0, 0, 100, 100, Color.cyan, 10)
        }
    }
}

package com.github.noamm9.nvgrenderer

import com.github.noamm9.nvgrenderer.demo.NVGDemoScreen
import com.github.noamm9.nvgrenderer.nvg.NVGPIP
import com.mojang.blaze3d.platform.InputConstants
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper
import net.fabricmc.fabric.api.client.rendering.v1.PictureInPictureRendererRegistry
import net.minecraft.client.KeyMapping
import org.lwjgl.glfw.GLFW

object Nvgrenderer: ClientModInitializer {
    private val openDemoKey = KeyMappingHelper.registerKeyMapping(
        KeyMapping("nvg demo", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_F8, KeyMapping.Category.DEBUG)
    )

    override fun onInitializeClient() {
        PictureInPictureRendererRegistry.register { NVGPIP(it.bufferSource()) }

        ClientTickEvents.END_CLIENT_TICK.register { client ->
            while (openDemoKey.consumeClick()) {
                client.setScreen(NVGDemoScreen())
            }
        }
    }
}

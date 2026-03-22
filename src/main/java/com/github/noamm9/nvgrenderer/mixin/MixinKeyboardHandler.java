package com.github.noamm9.nvgrenderer.mixin;

import com.github.noamm9.nvgrenderer.batchers.KeyboardBatcher;
import net.minecraft.client.KeyboardHandler;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(KeyboardHandler.class)
public abstract class MixinKeyboardHandler {
    @Inject(method = "keyPress", at = @At("HEAD"), cancellable = true)
    private void onKey(long l, int i, KeyEvent keyEvent, CallbackInfo ci) {
        if (keyEvent.key() == GLFW.GLFW_KEY_UNKNOWN) return;
        var event = new KeyboardBatcher.KeyEvent(
            keyEvent.key(), keyEvent.modifiers(), keyEvent.scancode(), KeyboardBatcher.KeyState.getEntries().get(i)
        );

        if (KeyboardBatcher.keyboardHookKey(event)) {
            ci.cancel();
        }
    }

    @Inject(method = "charTyped", at = @At("HEAD"), cancellable = true)
    private void onChar(long window, CharacterEvent charEvent, CallbackInfo ci) {
        var event = new KeyboardBatcher.CharEvent(charEvent.codepoint(), charEvent.modifiers());
        if (KeyboardBatcher.keyboardHookChar(event)) {
            ci.cancel();
        }
    }
}

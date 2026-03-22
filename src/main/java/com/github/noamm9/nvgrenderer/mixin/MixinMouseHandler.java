package com.github.noamm9.nvgrenderer.mixin;

import com.github.noamm9.nvgrenderer.batchers.MouseBatcher;
import net.minecraft.client.MouseHandler;
import net.minecraft.client.input.MouseButtonInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MouseHandler.class)
public abstract class MixinMouseHandler {
    @Inject(method = "onButton", at = @At("HEAD"), cancellable = true)
    private void onMouseButton(long l, MouseButtonInfo mouseButtonInfo, int scanCode, CallbackInfo ci) {
        var event = new MouseBatcher.MouseClickEvent(mouseButtonInfo.button(), mouseButtonInfo.modifiers(), scanCode);
        if (MouseBatcher.mouseHookClick(event)) {
            ci.cancel();
        }
    }

    @Inject(method = "onScroll", at = @At("HEAD"), cancellable = true)
    protected void onMouseScroll(long l, double horizontal, double vertical, CallbackInfo ci) {
        var event = new MouseBatcher.MouseScrolledEvent(horizontal, vertical);
        if (MouseBatcher.mouseHookScroll(event)) {
            ci.cancel();
        }
    }
}

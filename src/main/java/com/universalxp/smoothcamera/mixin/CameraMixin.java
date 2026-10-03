package com.universalxp.smoothcamera.mixin;

import com.universalxp.smoothcamera.SmoothCameraController;
import com.universalxp.smoothcamera.SmoothConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Hooks the END of Camera.update(). By then vanilla has computed the camera rotation from the
 * focused entity using the render tickDelta. We replace that rotation with a time-smoothed one.
 * Nothing about the player entity, mouse sensitivity, FOV or tick rate is touched.
 */
@Mixin(Camera.class)
public abstract class CameraMixin {
    @Shadow private float yaw;
    @Shadow private float pitch;

    @Shadow protected abstract void setRotation(float yaw, float pitch);

    @Inject(method = "update", at = @At("TAIL"))
    private void smoothcamera$smooth(CallbackInfo ci) {
        SmoothConfig cfg = SmoothConfig.get();
        if (!cfg.enabled || cfg.smoothness <= 0) {
            SmoothCameraController.reset();
            return;
        }
        // Avoid double smoothing: if vanilla Cinematic Camera is on, let vanilla do the work.
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.options.smoothCameraEnabled) {
            SmoothCameraController.reset();
            return;
        }
        SmoothCameraController.apply(this.yaw, this.pitch, cfg.smoothness);
        this.setRotation(SmoothCameraController.outYaw, SmoothCameraController.outPitch);
    }
}

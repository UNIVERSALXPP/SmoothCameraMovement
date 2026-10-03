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
 * Hooks the END of Camera.update(). Smoothing is applied in FIRST PERSON only:
 * in third person vanilla computes the camera position from the rotation, so changing
 * the rotation afterwards would make the camera shift. Third person stays vanilla.
 */
@Mixin(Camera.class)
public abstract class CameraMixin {
    @Shadow private float yaw;
    @Shadow private float pitch;

    @Shadow protected abstract void setRotation(float yaw, float pitch);

    @Inject(method = "update", at = @At("TAIL"))
    private void smoothcamera$smooth(CallbackInfo ci) {
        SmoothConfig cfg = SmoothConfig.get();
        Camera self = (Camera) (Object) this;

        if (!cfg.enabled || cfg.smoothness <= 0 || self.isThirdPerson()) {
            SmoothCameraController.reset();
            return;
        }
        // Avoid double smoothing: if vanilla Cinematic Camera is on, let vanilla do the work.
        if (MinecraftClient.getInstance().options.smoothCameraEnabled) {
            SmoothCameraController.reset();
            return;
        }
        SmoothCameraController.apply(this.yaw, this.pitch, cfg.smoothness);
        this.setRotation(SmoothCameraController.outYaw, SmoothCameraController.outPitch);
    }
}

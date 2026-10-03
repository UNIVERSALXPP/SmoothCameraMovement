package com.universalxp.smoothcamera.client;

import com.universalxp.smoothcamera.SmoothCameraController;
import com.universalxp.smoothcamera.SmoothConfig;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

public class SmoothCameraClient implements ClientModInitializer {
    public static final String MOD_ID = "smooth_camera_movement";

    private static KeyBinding toggleKey, increaseKey, decreaseKey, settingsKey;

    @Override
    public void onInitializeClient() {
        SmoothConfig.load();

        KeyBinding.Category category = KeyBinding.Category.create(Identifier.of(MOD_ID, "main"));
        toggleKey = register("toggle", GLFW.GLFW_KEY_UNKNOWN, category);
        increaseKey = register("increase", GLFW.GLFW_KEY_UNKNOWN, category);
        decreaseKey = register("decrease", GLFW.GLFW_KEY_UNKNOWN, category);
        settingsKey = register("settings", GLFW.GLFW_KEY_UNKNOWN, category);

        ClientTickEvents.END_CLIENT_TICK.register(this::onTick);
        HudElementRegistry.addLast(Identifier.of(MOD_ID, "hud"), (context, tickCounter) -> renderHud(context));
    }

    private static KeyBinding register(String name, int key, KeyBinding.Category category) {
        return KeyBindingHelper.registerKeyBinding(
                new KeyBinding("key." + MOD_ID + "." + name, InputUtil.Type.KEYSYM, key, category));
    }

    private void onTick(MinecraftClient mc) {
        SmoothConfig cfg = SmoothConfig.get();
        boolean changed = false;
        while (toggleKey.wasPressed()) { cfg.enabled = !cfg.enabled; changed = true; }
        while (increaseKey.wasPressed()) { cfg.setSmoothness(cfg.smoothness + 5); changed = true; }
        while (decreaseKey.wasPressed()) { cfg.setSmoothness(cfg.smoothness - 5); changed = true; }
        while (settingsKey.wasPressed()) { mc.setScreen(new SmoothConfigScreen(mc.currentScreen)); }
        if (changed) {
            SmoothCameraController.reset();
            SmoothConfig.save();
        }
        if (mc.player == null) SmoothCameraController.reset();
    }

    /** Small black box, top-left, text exactly "Smooth: XX%". */
    private void renderHud(DrawContext context) {
        SmoothConfig cfg = SmoothConfig.get();
        MinecraftClient mc = MinecraftClient.getInstance();
        if (!cfg.showHud || !cfg.enabled || mc.options.hudHidden) return;

        String text = "Smooth: " + cfg.smoothness + "%";
        int w = mc.textRenderer.getWidth(text);
        int x = 4, y = 4, pad = 3;
        context.fill(x, y, x + w + pad * 2, y + 9 + pad * 2, 0xB0000000);
        context.drawText(mc.textRenderer, text, x + pad, y + pad, 0xFFFFFFFF, false);
    }
}

package com.universalxp.smoothcamera.client;

import com.universalxp.smoothcamera.SmoothCameraController;
import com.universalxp.smoothcamera.SmoothConfig;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.text.Text;

public class SmoothConfigScreen extends Screen {
    private final Screen parent;

    public SmoothConfigScreen(Screen parent) {
        super(Text.translatable("smooth_camera_movement.screen.title"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        SmoothConfig cfg = SmoothConfig.get();
        int w = 200, x = this.width / 2 - w / 2, y = this.height / 2 - 46;

        // Slider: percentage updates live while dragging.
        this.addDrawableChild(new SliderWidget(x, y, w, 20, label(cfg.smoothness), cfg.smoothness / 100.0) {
            @Override protected void updateMessage() {
                this.setMessage(label((int) Math.round(this.value * 100)));
            }
            @Override protected void applyValue() {
                cfg.setSmoothness((int) Math.round(this.value * 100));
                SmoothCameraController.reset();
            }
        });

        this.addDrawableChild(ButtonWidget.builder(enabledText(cfg), b -> {
            cfg.enabled = !cfg.enabled;
            SmoothCameraController.reset();
            b.setMessage(enabledText(cfg));
        }).dimensions(x, y + 26, w, 20).build());

        this.addDrawableChild(ButtonWidget.builder(hudText(cfg), b -> {
            cfg.showHud = !cfg.showHud;
            b.setMessage(hudText(cfg));
        }).dimensions(x, y + 52, w, 20).build());

        this.addDrawableChild(ButtonWidget.builder(Text.translatable("gui.done"), b -> close())
                .dimensions(x, y + 84, w, 20).build());
    }

    private static Text label(int pct) {
        return Text.translatable("smooth_camera_movement.option.smoothness", pct);
    }
    private static Text onOff(boolean v) {
        return Text.translatable(v ? "smooth_camera_movement.on" : "smooth_camera_movement.off");
    }
    private static Text enabledText(SmoothConfig c) {
        return Text.translatable("smooth_camera_movement.option.enabled", onOff(c.enabled));
    }
    private static Text hudText(SmoothConfig c) {
        return Text.translatable("smooth_camera_movement.option.hud", onOff(c.showHud));
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, this.height / 2 - 70, 0xFFFFFFFF);
    }

    @Override
    public void close() {
        SmoothConfig.save();
        this.client.setScreen(parent);
    }
}

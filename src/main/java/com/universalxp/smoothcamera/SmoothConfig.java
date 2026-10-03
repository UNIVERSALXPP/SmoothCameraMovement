package com.universalxp.smoothcamera;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/** Persistent settings, stored in config/smooth_camera_movement.json. */
public final class SmoothConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path FILE =
            FabricLoader.getInstance().getConfigDir().resolve("smooth_camera_movement.json");

    /** 0..100. 0 = vanilla camera. */
    public int smoothness = 50;
    public boolean enabled = true;
    public boolean showHud = true;

    private static SmoothConfig instance = new SmoothConfig();

    public static SmoothConfig get() { return instance; }

    public void setSmoothness(int value) {
        smoothness = Math.max(0, Math.min(100, value));
    }

    public static void load() {
        try {
            if (Files.exists(FILE)) {
                SmoothConfig loaded = GSON.fromJson(Files.readString(FILE), SmoothConfig.class);
                if (loaded != null) {
                    loaded.setSmoothness(loaded.smoothness);
                    instance = loaded;
                }
            }
        } catch (Exception e) {
            System.err.println("[Smooth Camera Movement!] Could not read config, using defaults: " + e);
        }
    }

    public static void save() {
        try {
            Files.writeString(FILE, GSON.toJson(instance));
        } catch (IOException e) {
            System.err.println("[Smooth Camera Movement!] Could not save config: " + e);
        }
    }
}

# Smooth Camera Movement!

- **Author:** Universal_XP_
- **Minecraft:** Java Edition 1.21.11
- **Loader:** Fabric (Loader 0.18+) with **Fabric API**
- **Java:** 21
- **Side:** client only

Adds an adjustable cinematic camera smoothness from 0% to 100%.

## Install
1. Install Fabric Loader for 1.21.11 and put Fabric API in `.minecraft/mods/`.
2. Put `Smooth-Camera-Movement-<version>.jar` in `.minecraft/mods/`.

## Build
`./gradlew build` (Windows: `gradlew.bat build`). The jar lands in `build/libs/` and is copied to `output/`.
Check https://fabricmc.net/develop for the exact Yarn / Loader / Fabric API versions and edit `gradle.properties` if needed.

## Smoothness
- **0%** = normal vanilla camera (no smoothing at all).
- **100%** = maximum cinematic smoothness (about 0.45 s time constant; still follows the mouse).
- Every integer 0-100 works. This does **not** change mouse sensitivity, FOV, movement or rotation speed.
- If vanilla Cinematic Camera is on, the mod steps aside so smoothing is never doubled.

## Keybinds (Controls menu, category "Smooth Camera Movement!")
Toggle, Increase (+5%), Decrease (-5%), Open Settings. All are unbound by default; bind them yourself.

## Configuration
Settings screen: smoothness slider (live %), Custom Smooth Camera Movement ON/OFF, Show HUD ON/OFF.
Saved to `config/smooth_camera_movement.json`.

## HUD
Small black box in the top-left showing only `Smooth: XX%`.

## Low tick-rate compatibility
The mod **never changes the game's global tick rate**. Smoothing runs per rendered frame using real frame time,
so when another mod/command lowers the tick rate the camera keeps gliding between tick updates instead of stepping.

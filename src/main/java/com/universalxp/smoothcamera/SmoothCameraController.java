package com.universalxp.smoothcamera;

/**
 * Core smoothing maths. No Minecraft types here, so it is cheap and easy to reason about.
 *
 * <h3>How it works</h3>
 * Each rendered frame the camera has a TARGET rotation (what vanilla would show). We keep a
 * SMOOTHED rotation and move it toward the target with an exponential filter:
 *
 * <pre>alpha = 1 - exp(-dt / tau)</pre>
 *
 * <ul>
 *   <li>{@code dt} is REAL elapsed frame time (System.nanoTime), not game ticks. That makes the
 *       result frame-rate independent AND tick-rate independent: at 1 TPS the camera keeps
 *       gliding every rendered frame instead of stepping once per tick.</li>
 *   <li>{@code tau} is the time constant in seconds derived from the smoothness percentage.
 *       0% gives tau = 0 (no smoothing, vanilla passthrough). 100% gives about 0.45 s, which is
 *       very smooth yet still follows the mouse (it never freezes).</li>
 * </ul>
 * Angles are filtered through the shortest signed difference so yaw wrap-around (359 to 1 degree)
 * never causes a spin. Huge jumps (teleport, respawn, perspective swap) snap instead of gliding.
 */
public final class SmoothCameraController {
    private static final double MAX_TAU_SECONDS = 0.45;
    private static final double MAX_DT_SECONDS = 0.1;      // clamp lag spikes
    private static final float SNAP_DEGREES = 120f;        // larger jump = teleport-like, snap

    private static float smoothYaw, smoothPitch;
    private static long lastNanos;
    private static boolean initialised;

    /** Result of the last {@link #apply}; reused so nothing is allocated per frame. */
    public static float outYaw, outPitch;

    private SmoothCameraController() {}

    public static void reset() { initialised = false; }

    /** Time constant (seconds) for a smoothness percentage. Curve is gentle at low values. */
    public static double tauFor(int percent) {
        if (percent <= 0) return 0.0;
        double s = Math.min(100, percent) / 100.0;
        return MAX_TAU_SECONDS * Math.pow(s, 1.5);
    }

    /** Smooths the given target rotation; read the result from outYaw / outPitch. */
    public static void apply(float targetYaw, float targetPitch, int percent) {
        long now = System.nanoTime();
        double tau = tauFor(percent);

        if (!initialised || tau <= 0.0) {
            smoothYaw = targetYaw;
            smoothPitch = targetPitch;
            lastNanos = now;
            initialised = true;
            outYaw = targetYaw;
            outPitch = targetPitch;
            return;
        }

        double dt = Math.min((now - lastNanos) / 1.0e9, MAX_DT_SECONDS);
        lastNanos = now;

        float dYaw = wrap(targetYaw - smoothYaw);
        float dPitch = targetPitch - smoothPitch;

        if (Math.abs(dYaw) > SNAP_DEGREES || Math.abs(dPitch) > SNAP_DEGREES) {
            smoothYaw = targetYaw;
            smoothPitch = targetPitch;
        } else {
            float alpha = (float) (1.0 - Math.exp(-dt / tau));
            smoothYaw += dYaw * alpha;
            smoothPitch += dPitch * alpha;
        }
        outYaw = smoothYaw;
        outPitch = smoothPitch;
    }

    private static float wrap(float degrees) {
        degrees %= 360f;
        if (degrees >= 180f) degrees -= 360f;
        if (degrees < -180f) degrees += 360f;
        return degrees;
    }
}

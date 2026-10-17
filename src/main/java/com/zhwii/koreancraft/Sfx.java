package com.zhwii.koreancraft;

import net.minecraft.client.Minecraft;

import java.lang.reflect.Method;

/**
 * UI sound effects built from vanilla sounds. Everything is looked up reflectively so a small rename in a
 * future Minecraft version only silences the sounds instead of breaking the build. Press M in the dashboard
 * to mute / unmute.
 */
final class Sfx {
    private Sfx() {}

    static boolean enabled = true;
    private static boolean broken;

    static void play(String field, float pitch) {
        if (!enabled || broken) return;
        try {
            Object event = Class.forName("net.minecraft.sounds.SoundEvents").getField(field).get(null);
            Class<?> simple = Class.forName("net.minecraft.client.resources.sounds.SimpleSoundInstance");
            Object instance = null;
            for (Method m : simple.getMethods()) {
                if (!m.getName().equals("forUI") || !java.lang.reflect.Modifier.isStatic(m.getModifiers())) continue;
                Class<?>[] t = m.getParameterTypes();
                if (t.length < 2 || !t[0].isInstance(event)) continue;
                if (t.length == 2 && t[1] == float.class) { instance = m.invoke(null, event, pitch); break; }
                if (t.length == 3 && t[1] == float.class && t[2] == float.class) { instance = m.invoke(null, event, pitch, 1.0f); break; }
            }
            if (instance == null) return;
            Object manager = Minecraft.getInstance().getSoundManager();
            for (Method m : manager.getClass().getMethods()) {
                if (m.getName().equals("play") && m.getParameterCount() == 1 && m.getParameterTypes()[0].isInstance(instance)) {
                    m.invoke(manager, instance);
                    return;
                }
            }
        } catch (Throwable t) {
            broken = true;   // do not retry every frame if the API changed
        }
    }

    static void click() { play("UI_BUTTON_CLICK", 1.0f); }
    static void orb() { play("EXPERIENCE_ORB_PICKUP", 1.0f); }
    static void yes() { play("VILLAGER_YES", 1.0f); }
    static void no() { play("VILLAGER_NO", 1.0f); }
    static void levelUp() { play("PLAYER_LEVELUP", 1.0f); }
    static void achievement() { play("UI_TOAST_CHALLENGE_COMPLETE", 1.0f); }
    /** Pling that climbs with the combo, or a low bass note for a miss. */
    static void answer(boolean ok, int combo) {
        if (ok) play("NOTE_BLOCK_PLING", Math.min(2.0f, 0.9f + combo * 0.1f));
        else play("NOTE_BLOCK_BASS", 0.6f);
    }
}

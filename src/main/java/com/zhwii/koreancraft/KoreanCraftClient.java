package com.zhwii.koreancraft;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;

import java.lang.reflect.Field;
import java.nio.file.Path;
import java.util.Arrays;

/**
 * KoreanCraft (client-side only): learn Korean with a Vietnamese interface.
 * Shift+K opens the dashboard; Shift+J / H / V jump straight to quiz / Hangul / review.
 */
public final class KoreanCraftClient implements ClientModInitializer {
    private Progress progress;
    private final boolean[] previous = new boolean[512];

    @Override public void onInitializeClient() {
        Path file = FabricLoader.getInstance().getConfigDir().resolve("zhwii_koreancraft.properties");
        progress = new Progress(file);
        progress.notifier = this::deliver;

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null) { Arrays.fill(previous, false); return; }
            long window = client.getWindow().handle();
            boolean shift = down(window, GLFW.GLFW_KEY_LEFT_SHIFT) || down(window, GLFW.GLFW_KEY_RIGHT_SHIFT);
            boolean k = edge(window, GLFW.GLFW_KEY_K), j = edge(window, GLFW.GLFW_KEY_J);
            boolean h = edge(window, GLFW.GLFW_KEY_H), v = edge(window, GLFW.GLFW_KEY_V);
            if (!shift) return;
            var gui = client.gui;
            Screen current = gui.screen();
            if (k) {
                if (current instanceof KoreanScreen) gui.setScreen(null);
                else if (current == null) gui.setScreen(new KoreanScreen(progress, KoreanScreen.Page.HOME));
            } else if (current == null) {
                if (j) gui.setScreen(new KoreanScreen(progress, KoreanScreen.Page.QUIZ));
                else if (h) gui.setScreen(new KoreanScreen(progress, KoreanScreen.Page.HANGUL));
                else if (v) gui.setScreen(new KoreanScreen(progress, KoreanScreen.Page.REVIEW));
            }
        });
    }

    private boolean down(long window, int key) { return GLFW.glfwGetKey(window, key) == GLFW.GLFW_PRESS; }
    private boolean edge(long window, int key) {
        boolean now = down(window, key);
        boolean result = now && !previous[key];
        previous[key] = now;
        return result;
    }

    // ------------------------------------------------------------------ messages
    private void deliver(String message) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.gui.screen() instanceof KoreanScreen screen) { screen.showToast(message); return; }
        overlay(mc, message);
    }

    /**
     * Shows an action-bar style message. Minecraft 26.2 moved this from Gui to Gui#hud (Hud), so the
     * lookup is done reflectively to keep the mod compiling across small API shuffles.
     */
    private static void overlay(Minecraft mc, String message) {
        Component text = Component.literal(message);
        Object gui = mc.gui;
        Object[] targets = {fieldValue(gui, "hud"), gui};
        for (Object target : targets) {
            if (target == null) continue;
            try {
                target.getClass().getMethod("setOverlayMessage", Component.class, boolean.class).invoke(target, text, false);
                return;
            } catch (ReflectiveOperationException | RuntimeException ignored) { }
        }
        if (mc.player != null) {
            try {
                mc.player.getClass().getMethod("displayClientMessage", Component.class, boolean.class).invoke(mc.player, text, true);
            } catch (ReflectiveOperationException | RuntimeException ignored) { }
        }
    }

    private static Object fieldValue(Object owner, String name) {
        for (Class<?> c = owner.getClass(); c != null; c = c.getSuperclass()) {
            try {
                Field f = c.getDeclaredField(name);
                f.setAccessible(true);
                return f.get(owner);
            } catch (ReflectiveOperationException | RuntimeException ignored) { }
        }
        return null;
    }
}

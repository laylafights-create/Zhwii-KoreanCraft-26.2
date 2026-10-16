package com.zhwii.koreancraft;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;

/** Client-only KoreanCraft prototype: press K to see the next Korean word. */
public final class KoreanCraftClient implements ClientModInitializer {
    private static final String[][] WORDS = {
        {"나무", "Cây"}, {"물", "Nước"}, {"돌", "Đá"}, {"사과", "Táo"},
        {"책", "Sách"}, {"검", "Kiếm"}, {"집", "Nhà"}, {"불", "Lửa"},
        {"고양이", "Mèo"}, {"소", "Bò"}, {"돼지", "Lợn"}, {"빵", "Bánh mì"}
    };

    private int index;
    private boolean wasKDown;

    @Override
    public void onInitializeClient() {
        // Poll GLFW directly: Fabric API 26.2 no longer includes the old KeyBindingHelper.
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null || client.screen != null) {
                wasKDown = false;
                return;
            }

            boolean kDown = GLFW.glfwGetKey(client.getWindow().handle(), GLFW.GLFW_KEY_K) == GLFW.GLFW_PRESS;
            if (kDown && !wasKDown) {
                String[] word = WORDS[index++ % WORDS.length];
                client.gui.getChat().addMessage(Component.literal("§a[KoreanCraft] §f" + word[0] + " §7= " + word[1]));
            }
            wasKDown = kDown;
        });
    }
}

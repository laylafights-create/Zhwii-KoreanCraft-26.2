package com.zhwii.koreancraft;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;

/** Client-only KoreanCraft prototype: press K for a vocabulary prompt. */
public final class KoreanCraftClient implements ClientModInitializer {
    private static final String[][] WORDS = {
        {"나무", "Cây"}, {"물", "Nước"}, {"돌", "Đá"}, {"사과", "Táo"},
        {"책", "Sách"}, {"검", "Kiếm"}, {"집", "Nhà"}, {"불", "Lửa"},
        {"고양이", "Mèo"}, {"소", "Bò"}, {"돼지", "Lợn"}, {"빵", "Bánh mì"}
    };
    private int index = 0;
    @Override public void onInitializeClient() {
        // Minimal starter: the key mapping registration may need adapting to the 26.2 API patch in use.
        KeyMapping key = KeyBindingHelper.registerKeyBinding(new KeyMapping("key.koreancraft.study", GLFW.GLFW_KEY_K, "category.koreancraft"));
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (key.consumeClick()) {
                String[] word = WORDS[index++ % WORDS.length];
                if (client.player != null) client.player.displayClientMessage(Component.literal("§a[KoreanCraft] §f" + word[0] + " §7= " + word[1]), false);
            }
        });
    }
}

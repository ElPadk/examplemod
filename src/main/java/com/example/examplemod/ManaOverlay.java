package com.example.examplemod;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;

public class ManaOverlay {
    private static int clientMana = 100;
    private static long lastRegenTime = 0;
    private static long lastManaUse = 0;

    public static final IGuiOverlay MANA_OVERLAY = (gui, guiGraphics, partialTick, screenWidth, screenHeight) -> {
        Minecraft minecraft = Minecraft.getInstance();
        Player player = minecraft.player;

        if (player == null || minecraft.options.hideGui) {
            return;
        }

        updateManaRegeneration();

        Font font = minecraft.font;
        String manaText = clientMana + "/100";

        // RICHTIGE POSITION: ÜBER DER FOOD BAR (rechts unten)
        int left = screenWidth / 2 + 91 - 81; // rechte Seite, gleiche Breite wie Food-Bar
        int top = screenHeight - 49;          // direkt über der Hungerleiste

        // Hintergrund-Balken
        guiGraphics.fill(left, top, left + 82, top + 10, 0xFF000000);
        guiGraphics.fill(left + 1, top + 1, left + 81, top + 9, 0xFF555555);

        // Füllung
        int fillWidth = (int)(80 * (clientMana / 100f));
        if (fillWidth > 0) {
            guiGraphics.fill(left + 1, top + 1, left + 1 + fillWidth, top + 9, 0xFF0000FF);
        }

        // Text mittig
        int textX = left + 41 - font.width(manaText) / 2;
        guiGraphics.drawString(font, manaText, textX, top + 1, 0xFFFFFF, false);
    };

    private static void updateManaRegeneration() {
        long currentTime = System.currentTimeMillis();

        if (currentTime - lastManaUse >= 5000 && currentTime - lastRegenTime >= 500) {
            if (clientMana < 100) {
                clientMana++;
                lastRegenTime = currentTime;
            }
        }
    }

    public static void useMana(int amount) {
        if (clientMana >= amount) {
            clientMana -= amount;
            lastManaUse = System.currentTimeMillis();
        }
    }

    public static int getCurrentMana() {
        return clientMana;
    }
}

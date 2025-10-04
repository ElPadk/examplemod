package com.example.examplemod;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ExampleMod.MODID)
public class ManaSystem {
    private static final int MAX_MANA = 100;
    private static final int REGEN_DELAY = 100; // 5 Sekunden (20 ticks = 1 Sekunde)
    private static final int REGEN_AMOUNT = 1;
    private static final int REGEN_INTERVAL = 10; // Jede 0.5 Sekunden
    
    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.END && !event.player.level().isClientSide()) {
            Player player = event.player;
            CompoundTag persistentData = player.getPersistentData();
            CompoundTag modData = persistentData.getCompound(ExampleMod.MODID);
            
            // Standardwert setzen falls nicht vorhanden
            if (!modData.contains("mana")) {
                modData.putInt("mana", MAX_MANA);
            }
            if (!modData.contains("lastManaUse")) {
                modData.putInt("lastManaUse", 0);
            }
            
            int currentMana = modData.getInt("mana");
            int lastManaUseTick = modData.getInt("lastManaUse");
            int ticksSinceLastUse = (int)player.level().getGameTime() - lastManaUseTick;
            
            // Debug: Mana-Wert loggen
            if (player.tickCount % 100 == 0) { // Nur alle 5 Sekunden loggen
                System.out.println("Mana: " + currentMana + ", Ticks since last use: " + ticksSinceLastUse);
            }
            
            // Regeneration nach 5 Sekunden
            if (ticksSinceLastUse >= REGEN_DELAY && player.tickCount % REGEN_INTERVAL == 0) {
                if (currentMana < MAX_MANA) {
                    currentMana = Math.min(MAX_MANA, currentMana + REGEN_AMOUNT);
                    modData.putInt("mana", currentMana);
                    persistentData.put(ExampleMod.MODID, modData);
                    System.out.println("Mana regenerated: " + currentMana);
                }
            }
        }
    }
    
    public static int getMana(Player player) {
        if (player == null) return MAX_MANA;
        
        CompoundTag persistentData = player.getPersistentData();
        CompoundTag modData = persistentData.getCompound(ExampleMod.MODID);
        
        // Immer den aktuellen Wert zurückgeben
        if (!modData.contains("mana")) {
            modData.putInt("mana", MAX_MANA);
            persistentData.put(ExampleMod.MODID, modData);
        }
        
        return modData.getInt("mana");
    }
    
    public static void setMana(Player player, int amount) {
        CompoundTag persistentData = player.getPersistentData();
        CompoundTag modData = persistentData.getCompound(ExampleMod.MODID);
        modData.putInt("mana", Math.max(0, Math.min(MAX_MANA, amount)));
        persistentData.put(ExampleMod.MODID, modData);
    }
    
    public static boolean useMana(Player player, int amount) {
        int currentMana = getMana(player);
        if (currentMana >= amount) {
            setMana(player, currentMana - amount);
            
            CompoundTag persistentData = player.getPersistentData();
            CompoundTag modData = persistentData.getCompound(ExampleMod.MODID);
            modData.putInt("lastManaUse", (int)player.level().getGameTime());
            persistentData.put(ExampleMod.MODID, modData);
            
            return true;
        }
        return false;
    }
    
    public static int getMaxMana() {
        return MAX_MANA;
    }
}
package com.example.examplemod;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.UUID;

@Mod.EventBusSubscriber(modid = ExampleMod.MODID)
public class Events {

    private static final UUID HEALTH_BONUS_UUID = UUID.fromString("11111111-1111-1111-1111-111111111111");
    private static final long COMPASS_COOLDOWN = 1000; // 1 Sekunde Cooldown

    @SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        var player = event.getEntity();

        // Gesundheits-Bonus
        var attribute = player.getAttribute(Attributes.MAX_HEALTH);
        if (attribute != null && attribute.getModifier(HEALTH_BONUS_UUID) == null) {
            AttributeModifier bonus = new AttributeModifier(
                    HEALTH_BONUS_UUID,
                    "ExampleMod health bonus",
                    20.0,
                    AttributeModifier.Operation.ADDITION
            );
            attribute.addPermanentModifier(bonus);
        }

        player.setHealth((float) attribute.getValue());
        
        // Nachtsicht - unsichtbar und unendlich
        player.addEffect(new MobEffectInstance(
            MobEffects.NIGHT_VISION, 
            Integer.MAX_VALUE,
            0, 
            false,
            false
        ));
    }

    @SubscribeEvent
    public static void onPlayerInteract(PlayerInteractEvent.RightClickItem event) {
        Player player = event.getEntity();
        ItemStack mainHandItem = player.getMainHandItem();
        int manaCost = 5;
        
        if (mainHandItem.getItem() == Items.RECOVERY_COMPASS) {
            // Cooldown prüfen
            if (isOnCooldown(player)) {
                return;
            }
            
            // Cooldown setzen
            setCooldown(player);
            
            if (player.level().isClientSide()) {
                // Client-Seite: Mana sofort aktualisieren
                if (ManaOverlay.getCurrentMana() >= manaCost) {
                    ManaOverlay.useMana(manaCost);
                }
            } else {
                // Server-Seite: Mana verbrauchen und Speed-Effekt geben
                if (ManaSystem.useMana(player, manaCost)) {
                    // SPEED-Effekt statt Hast (Movement Speed statt Dig Speed)
                    player.addEffect(new MobEffectInstance(
                        MobEffects.MOVEMENT_SPEED, // ← SPEED Effekt
                        400, // 5 Sekunden Dauer (100 Ticks)
                        1,   // Stufe II (schneller)
                        false, // Keine Partikel
                        false  // Nicht im Inventar anzeigen
                    ));
                }
            }
        }
    }
    
    // Cooldown-Funktionen
    private static boolean isOnCooldown(Player player) {
        CompoundTag persistentData = player.getPersistentData();
        CompoundTag modData = persistentData.getCompound(ExampleMod.MODID);
        
        if (modData.contains("compassCooldown")) {
            long lastUse = modData.getLong("compassCooldown");
            return System.currentTimeMillis() - lastUse < COMPASS_COOLDOWN;
        }
        return false;
    }
    
    private static void setCooldown(Player player) {
        CompoundTag persistentData = player.getPersistentData();
        CompoundTag modData = persistentData.getCompound(ExampleMod.MODID);
        modData.putLong("compassCooldown", System.currentTimeMillis());
        persistentData.put(ExampleMod.MODID, modData);
    }
}
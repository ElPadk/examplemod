package com.example.examplemod;

public class ClientManaData {
    private static int currentMana = 100;
    private static final int MAX_MANA = 100;
    private static long lastUpdateTime = 0;
    private static long lastManaUseTime = 0;
    
    public static int getCurrentMana() {
        // Client-seitige Regeneration simulieren
        updateClientRegeneration();
        return currentMana;
    }
    
    public static void setCurrentMana(int mana) {
        currentMana = Math.max(0, Math.min(MAX_MANA, mana));
        lastUpdateTime = System.currentTimeMillis();
    }
    
    public static void useMana(int amount) {
        if (currentMana >= amount) {
            currentMana -= amount;
            lastManaUseTime = System.currentTimeMillis();
            lastUpdateTime = lastManaUseTime;
        }
    }
    
    private static void updateClientRegeneration() {
        long currentTime = System.currentTimeMillis();
        long timeSinceLastUse = currentTime - lastManaUseTime;
        
        // Regeneration nach 5 Sekunden (5000ms)
        if (timeSinceLastUse >= 5000 && currentTime - lastUpdateTime >= 500) { // Alle 0.5 Sekunden
            if (currentMana < MAX_MANA) {
                currentMana = Math.min(MAX_MANA, currentMana + 1);
                lastUpdateTime = currentTime;
            }
        }
    }
    
    public static int getMaxMana() {
        return MAX_MANA;
    }
    
    // Synchronisation vom Server
    public static void syncFromServer(int serverMana) {
        currentMana = serverMana;
        lastUpdateTime = System.currentTimeMillis();
    }
}
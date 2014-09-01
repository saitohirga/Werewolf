package com.dogonfire.werewolf;

import org.bukkit.inventory.ItemStack;


public class API
{
    public static ItemStack newWerewolfCurePotion() {
        return Werewolf.getPotionManager().createWerewolfCurePotion();
    }
    
    public static ItemStack newWerewolfInfectionPotion() {
        return Werewolf.getPotionManager().createWerewolfInfectionPotion();
    }
    
    public static boolean isWerewolf(final String playerName) {
        return Werewolf.getWerewolfManager().isWerewolf(playerName);
    }
    
    public static int getNumberOfWerewolves() {
        return Werewolf.getWerewolfManager().getNumberOfWerewolves();
    }
}

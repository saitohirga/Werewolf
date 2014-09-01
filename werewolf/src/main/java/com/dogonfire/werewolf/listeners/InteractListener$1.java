package com.dogonfire.werewolf.listeners;

import com.dogonfire.werewolf.ClanManager;
import com.dogonfire.werewolf.Werewolf;
import org.bukkit.entity.Player;

class InteractListener$1 implements Runnable {
    private final /* synthetic */ Player val$targetPlayer;
    
    @Override
    public void run() {
        Werewolf.getWerewolfManager().makeWerewolf(this.val$targetPlayer, true, ClanManager.ClanType.Potion);
    }
}
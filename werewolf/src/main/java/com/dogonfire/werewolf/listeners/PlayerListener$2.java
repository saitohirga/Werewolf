package com.dogonfire.werewolf.listeners;

import com.dogonfire.werewolf.Werewolf;
import org.bukkit.entity.Player;

class PlayerListener$2 implements Runnable {
    private final /* synthetic */ Player val$player;
    
    @Override
    public void run() {
        Werewolf.getSkinManager().showWorldDisguises(this.val$player);
    }
}
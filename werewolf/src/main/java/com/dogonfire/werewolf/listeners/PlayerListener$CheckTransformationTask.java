package com.dogonfire.werewolf.listeners;

import com.dogonfire.werewolf.Werewolf;
import org.bukkit.entity.Player;

public class CheckTransformationTask implements Runnable
{
    private Player player;
    
    public CheckTransformationTask(final Player p) {
        super();
        this.player = p;
    }
    
    @Override
    public void run() {
        if (Werewolf.getWerewolfManager().canTransform(this.player)) {
            PlayerListener.access$0(PlayerListener.this).transform(this.player);
        }
        else if (Werewolf.getWerewolfManager().canUntransform(this.player)) {
            PlayerListener.access$0(PlayerListener.this).untransform(this.player);
        }
    }
}

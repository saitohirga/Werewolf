package com.dogonfire.werewolf;

import org.bukkit.entity.Player;

public class PlayerPositionUpdater implements Runnable
{
    final Werewolf plugin;
    final Player player;
    final WerewolfSkin skin;
    
    public PlayerPositionUpdater(final Werewolf plugin, final Player player, final WerewolfSkin skin) {
        super();
        this.plugin = plugin;
        this.player = player;
        this.skin = skin;
    }
    
    @Override
    public void run() {
    }
}

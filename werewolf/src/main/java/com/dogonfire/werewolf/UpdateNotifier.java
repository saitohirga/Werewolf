package com.dogonfire.werewolf;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class UpdateNotifier implements Runnable
{
    final Werewolf plugin;
    Player player;
    
    public UpdateNotifier(final Werewolf plugin, final Player player) {
        super();
        this.plugin = plugin;
        this.player = player;
    }
    
    @Override
    public void run() {
        if (this.player.isOnline()) {
            final UpdateChecker updateChecker = new UpdateChecker();
            final String latestVersionName = updateChecker.getLatestVersionName();
            if (latestVersionName == null) {
                this.plugin.log("Could not get latest version name!");
                return;
            }
            int thisVersionNumber;
            try {
                thisVersionNumber = Integer.parseInt(this.plugin.getDescription().getVersion().replace(".", ""));
            }
            catch (NumberFormatException e) {
                this.plugin.log("Could not parse this plugin version number (from " + this.plugin.getDescription().getVersion() + ")");
                return;
            }
            int bukkitVersionNumber;
            try {
                bukkitVersionNumber = Integer.parseInt(latestVersionName.replace(" ", "").replace("Beta", "").replace("Werewolf", "").replace(".", ""));
            }
            catch (NumberFormatException e) {
                this.plugin.log("Could not parse latest version number (from " + latestVersionName + ")");
                return;
            }
            try {
                if (thisVersionNumber < bukkitVersionNumber) {
                    this.player.sendMessage(ChatColor.AQUA + "There is a new update for Werewolf available: " + ChatColor.GOLD + latestVersionName + ChatColor.AQUA + " for " + ChatColor.GOLD + updateChecker.getLatestVersionGameVersion());
                    this.player.sendMessage(ChatColor.AQUA + "Download it at " + ChatColor.GOLD + updateChecker.getLatestVersionLink());
                }
            }
            catch (NumberFormatException e) {
                this.plugin.log("Could not compare version numbers!");
            }
        }
    }
}

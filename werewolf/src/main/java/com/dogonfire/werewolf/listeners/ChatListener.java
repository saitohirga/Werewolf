package com.dogonfire.werewolf.listeners;

import org.bukkit.event.EventHandler;
import java.util.Iterator;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import java.util.Random;
import com.dogonfire.werewolf.Werewolf;
import org.bukkit.event.Listener;

public class ChatListener implements Listener
{
    private Werewolf plugin;
    private Random random;
    
    public ChatListener(final Werewolf plugin) {
        super();
        this.plugin = null;
        this.random = new Random();
        this.plugin = plugin;
    }
    
    private String getWerewolfLanguage(String message) {
        message = message.toLowerCase().replace("a", "arr");
        if (this.random.nextInt(2) == 0) {
            message = message.replace("r", "rr");
        }
        if (this.random.nextInt(2) == 0) {
            message = message.replace("f", "woof");
        }
        else {
            message = message.replace("o", "awoo");
        }
        return message;
    }
    
    @EventHandler
    public void onPlayerChat(final AsyncPlayerChatEvent e) {
        if (!Werewolf.pluginEnabled || !this.plugin.wolfChat) {
            return;
        }
        final Player player = e.getPlayer();
        if (Werewolf.getWerewolfManager().hasWerewolfSkin(player.getName())) {
            e.setCancelled(true);
            final Random r = new Random();
            final String alternativeMessage = (String)this.plugin.wolfMessage.toArray()[r.nextInt(this.plugin.wolfMessage.size())];
            final String message = this.getWerewolfLanguage(e.getMessage());
            for (final Player receiver : e.getRecipients()) {
                if (receiver.isOp() || Werewolf.getPermissionsManager().hasPermission(player, "werewolf.listener")) {
                    receiver.sendMessage("<" + Werewolf.getWerewolfManager().getPlayerListName(player) + ">(Werewolf): " + e.getMessage());
                }
                else if (Werewolf.getWerewolfManager().isWerewolf(receiver.getName())) {
                    receiver.sendMessage("<" + Werewolf.getWerewolfManager().getPlayerListName(player) + ">: " + ChatColor.RED + message);
                }
                else {
                    receiver.sendMessage("<Werewolf>: " + alternativeMessage);
                }
            }
            this.plugin.log("<" + Werewolf.getWerewolfManager().getPlayerListName(player) + ">(Werewolf): " + message);
            Werewolf.getWerewolfManager().growl(player);
        }
    }
}

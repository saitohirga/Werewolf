package com.dogonfire.werewolf;

import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.EventPriority;
import org.bukkit.event.EventHandler;
import org.bukkit.entity.Player;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Material;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.Listener;

public class SignManager implements Listener
{
    private Werewolf plugin;
    
    SignManager(final Werewolf p) {
        super();
        this.plugin = p;
    }
    
    private boolean isWerewolfSignEvent(final SignChangeEvent event) {
        final String signTitle = event.getLine(0);
        return signTitle != null && signTitle.trim().equalsIgnoreCase("Werewolf");
    }
    
    private boolean isWerewolfSignPotionEvent(final SignChangeEvent event) {
        final String signTitle = event.getLine(1);
        return signTitle != null && signTitle.trim().equalsIgnoreCase("Infection");
    }
    
    private boolean isWerewolfSignCureEvent(final SignChangeEvent event) {
        final String signTitle = event.getLine(1);
        return signTitle != null && signTitle.trim().equalsIgnoreCase("Cure");
    }
    
    public boolean handleNewWerewolfPotionSign(final SignChangeEvent event) {
        String priceText = "";
        if (this.plugin.vaultEnabled) {
            priceText = Werewolf.getEconomy().format(this.plugin.infectionPrice);
        }
        event.setLine(0, "Werewolf");
        event.setLine(1, "Infection");
        event.setLine(2, priceText);
        event.setLine(3, "");
        return true;
    }
    
    public boolean handleNewWerewolfCureSign(final SignChangeEvent event) {
        String priceText = "";
        if (this.plugin.vaultEnabled) {
            priceText = Werewolf.getEconomy().format(this.plugin.curePrice);
        }
        event.setLine(0, "Werewolf");
        event.setLine(1, "Cure");
        event.setLine(2, priceText);
        event.setLine(3, "");
        return true;
    }
    
    @EventHandler(priority = EventPriority.LOW)
    public void OnSignChange(final SignChangeEvent event) {
        final Player player = event.getPlayer();
        if (!this.isWerewolfSignEvent(event)) {
            return;
        }
        if (this.isWerewolfSignPotionEvent(event)) {
            if (player.isOp() || Werewolf.getPermissionsManager().hasPermission(player, "werewolf.sign.infection.place")) {
                if (!this.handleNewWerewolfPotionSign(event)) {
                    event.setCancelled(true);
                    event.getBlock().setType(Material.AIR);
                    event.getBlock().getWorld().dropItem(event.getBlock().getLocation(), new ItemStack(Material.SIGN, 1));
                }
                else {
                    if (this.plugin.vaultEnabled) {
                        Werewolf.getLanguageManager().setAmount(Werewolf.getEconomy().format(this.plugin.infectionPrice));
                    }
                    else {
                        Werewolf.getLanguageManager().setAmount("free");
                    }
                    this.plugin.sendInfo(player, ChatColor.AQUA + Werewolf.getLanguageManager().getLanguageString(LanguageManager.LANGUAGESTRING.YouPlacedAPotionSign));
                }
            }
            else {
                event.setCancelled(true);
                event.getBlock().setType(Material.AIR);
                event.getBlock().getWorld().dropItem(event.getBlock().getLocation(), new ItemStack(Material.SIGN, 1));
            }
            return;
        }
        if (this.isWerewolfSignCureEvent(event)) {
            if (player.isOp() || Werewolf.getPermissionsManager().hasPermission(player, "werewolf.sign.cure.place")) {
                if (!this.handleNewWerewolfCureSign(event)) {
                    event.setCancelled(true);
                    event.getBlock().setType(Material.AIR);
                    event.getBlock().getWorld().dropItem(event.getBlock().getLocation(), new ItemStack(Material.SIGN, 1));
                }
                else {
                    if (this.plugin.vaultEnabled) {
                        Werewolf.getLanguageManager().setAmount(Werewolf.getEconomy().format(this.plugin.curePrice));
                    }
                    else {
                        Werewolf.getLanguageManager().setAmount("free");
                    }
                    this.plugin.sendInfo(player, ChatColor.AQUA + Werewolf.getLanguageManager().getLanguageString(LanguageManager.LANGUAGESTRING.YouPlacedACureSign));
                }
            }
            else {
                event.setCancelled(true);
                event.getBlock().setType(Material.AIR);
                event.getBlock().getWorld().dropItem(event.getBlock().getLocation(), new ItemStack(Material.SIGN, 1));
            }
        }
    }
    
    @EventHandler(priority = EventPriority.LOW)
    private boolean checkForActivatingSign(final PlayerInteractEvent event) {
        if (!event.getAction().equals((Object)Action.RIGHT_CLICK_BLOCK)) {
            return false;
        }
        if (event.getClickedBlock().getType() != Material.WALL_SIGN) {
            return false;
        }
        final BlockState state = event.getClickedBlock().getState();
        final Sign sign = (Sign)state;
        final String titleLine = sign.getLine(0);
        if (titleLine == null || !titleLine.equals("Werewolf")) {
            return false;
        }
        final Player player = event.getPlayer();
        final String typeLine = sign.getLine(1);
        if (typeLine.equals("Infection")) {
            if (player.isOp() || Werewolf.getPermissionsManager().hasPermission(player, "werewolf.sign.infection.use")) {
                double price = 0.0;
                if (this.plugin.vaultEnabled) {
                    Werewolf.getLanguageManager().setAmount(Werewolf.getEconomy().format(this.plugin.infectionPrice));
                    price = this.plugin.infectionPrice;
                    if (!Werewolf.getEconomy().has(player.getName(), price)) {
                        this.plugin.sendInfo(player, ChatColor.RED + Werewolf.getLanguageManager().getLanguageString(LanguageManager.LANGUAGESTRING.YouDoNotHaveEnoughMoney));
                        return true;
                    }
                    Werewolf.getEconomy().withdrawPlayer(player.getName(), price);
                }
                else {
                    Werewolf.getLanguageManager().setAmount("free");
                }
                event.getPlayer().getWorld().dropItemNaturally(event.getPlayer().getLocation(), API.newWerewolfInfectionPotion());
                this.plugin.sendInfo(player, ChatColor.AQUA + Werewolf.getLanguageManager().getLanguageString(LanguageManager.LANGUAGESTRING.YouBoughtAInfectionPotion));
                this.plugin.log(String.valueOf(event.getPlayer().getName()) + " bought a werewolf infection potion for " + price);
                return true;
            }
        }
        else if (typeLine.equals("Cure") && (player.isOp() || Werewolf.getPermissionsManager().hasPermission(player, "werewolf.sign.cure.use"))) {
            double price = 0.0;
            Werewolf.getLanguageManager().setAmount(Werewolf.getEconomy().format(this.plugin.curePrice));
            if (this.plugin.vaultEnabled) {
                price = this.plugin.curePrice;
                if (!Werewolf.getEconomy().has(player.getName(), price)) {
                    this.plugin.sendInfo(player, ChatColor.RED + Werewolf.getLanguageManager().getLanguageString(LanguageManager.LANGUAGESTRING.YouDoNotHaveEnoughMoney));
                    return true;
                }
                Werewolf.getEconomy().withdrawPlayer(player.getName(), price);
            }
            else {
                Werewolf.getLanguageManager().setAmount("free");
            }
            event.getPlayer().getWorld().dropItemNaturally(event.getPlayer().getLocation(), API.newWerewolfCurePotion());
            this.plugin.sendInfo(player, ChatColor.AQUA + Werewolf.getLanguageManager().getLanguageString(LanguageManager.LANGUAGESTRING.YouBoughtACurePotion));
            this.plugin.log(String.valueOf(event.getPlayer().getName()) + " bought a werewolf cure potion for " + price);
            return true;
        }
        return true;
    }
}

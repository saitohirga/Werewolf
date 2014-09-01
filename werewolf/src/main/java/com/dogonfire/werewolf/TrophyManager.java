package com.dogonfire.werewolf;

import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.EventPriority;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.Location;
import java.text.DateFormat;
import org.bukkit.inventory.meta.ItemMeta;
import java.util.List;
import java.util.ArrayList;
import org.bukkit.ChatColor;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.configuration.file.YamlConfiguration;
import java.io.File;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Listener;

public class TrophyManager implements Listener
{
    private Werewolf plugin;
    private FileConfiguration skullsConfig;
    private File skullsConfigFile;
    
    TrophyManager(final Werewolf plugin) {
        super();
        this.plugin = null;
        this.skullsConfig = null;
        this.skullsConfigFile = null;
        this.plugin = plugin;
    }
    
    public void load() {
        if (this.skullsConfigFile == null) {
            this.skullsConfigFile = new File(this.plugin.getDataFolder(), "trophies.yml");
        }
        this.skullsConfig = (FileConfiguration)YamlConfiguration.loadConfiguration(this.skullsConfigFile);
        this.plugin.log("Loaded " + this.skullsConfig.getKeys(false).size() + " Werewolf trophies.");
        this.plugin.getServer().getPluginManager().registerEvents((Listener)this, (Plugin)this.plugin);
    }
    
    public void save() {
        if (this.skullsConfig == null || this.skullsConfig == null) {
            return;
        }
        try {
            this.skullsConfig.save(this.skullsConfigFile);
        }
        catch (Exception ex) {
            this.plugin.log("Could not save config to " + this.skullsConfig + ": " + ex.getMessage());
        }
    }
    
    public ItemStack getTrophyFromWerewolfPlayer(final String killerName) {
        final ItemStack skullTrophy = new ItemStack(Material.SKULL_ITEM, 1, 3);
        final SkullMeta skullMeta = (SkullMeta)skullTrophy.getItemMeta();
        final Date thisDate = new Date();
        final String pattern = "dd MMMM yyyy";
        final DateFormat formatter = new SimpleDateFormat(pattern);
        skullMeta.setOwner("SomeWerewolf");
        skullMeta.setDisplayName(ChatColor.GOLD + "Werewolf Head");
        Werewolf.getLanguageManager().setPlayerName(killerName);
        Werewolf.getLanguageManager().setAmount(formatter.format(thisDate));
        final String lorePage = ChatColor.WHITE + Werewolf.getLanguageManager().getLanguageString(LanguageManager.LANGUAGESTRING.TrophyDescription);
        final List<String> lorePages = new ArrayList<String>();
        lorePages.add(lorePage);
        skullMeta.setLore((List)lorePages);
        skullTrophy.setItemMeta((ItemMeta)skullMeta);
        return skullTrophy;
    }
    
    private int hashVector(final Location location) {
        return location.getBlockX() * 73856093 ^ location.getBlockY() * 19349663 ^ location.getBlockZ() * 83492791;
    }
    
    public void handlePlaceSkull(final ItemStack skull, final Location location) {
        this.plugin.logDebug("Setting skull at " + location);
        this.skullsConfig.set(String.valueOf(this.hashVector(location)) + ".Description", (Object)skull.getItemMeta().getLore());
        this.save();
    }
    
    public boolean handleBreakSkull(final ItemStack skullItem, final Location location) {
        final String description = this.skullsConfig.getString(String.valueOf(this.hashVector(location)) + ".Description");
        if (description == null) {
            return false;
        }
        final SkullMeta skullMeta = (SkullMeta)skullItem.getItemMeta();
        skullMeta.setDisplayName(ChatColor.GOLD + "Werewolf Head");
        final List<String> lorePages = new ArrayList<String>();
        lorePages.add(description);
        skullMeta.setLore((List)lorePages);
        this.skullsConfig.set(new StringBuilder(String.valueOf(this.hashVector(location))).toString(), (Object)null);
        this.save();
        skullItem.setItemMeta((ItemMeta)skullMeta);
        return true;
    }
    
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = false)
    public void onBlockBreak(final BlockBreakEvent event) {
        if (event.getBlock().getType() != Material.SKULL) {
            return;
        }
        final ItemStack item = (ItemStack)event.getBlock().getDrops().toArray()[0];
        if (this.handleBreakSkull(item, event.getBlock().getLocation())) {
            event.getBlock().setType(Material.AIR);
            event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), item);
            event.setCancelled(true);
        }
    }
    
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = false)
    public void onBlockPlace(final BlockPlaceEvent event) {
        if (event.getItemInHand().getType() != Material.SKULL_ITEM) {
            return;
        }
        this.handlePlaceSkull(event.getItemInHand(), event.getBlockPlaced().getLocation());
    }
}

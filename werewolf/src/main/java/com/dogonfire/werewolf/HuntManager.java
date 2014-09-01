package com.dogonfire.werewolf;

import java.util.Set;
import java.text.DateFormat;
import java.util.Date;
import java.text.SimpleDateFormat;
import org.bukkit.entity.Player;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import java.util.Random;
import java.io.File;
import org.bukkit.configuration.file.FileConfiguration;

public class HuntManager
{
    private Werewolf plugin;
    private int totalBounty;
    private FileConfiguration huntersConfig;
    private File huntersConfigFile;
    private Random random;
    
    HuntManager(final Werewolf p) {
        super();
        this.totalBounty = 0;
        this.huntersConfig = null;
        this.huntersConfigFile = null;
        this.random = new Random();
        this.plugin = p;
    }
    
    public void load() {
        if (this.huntersConfigFile == null) {
            this.huntersConfigFile = new File(this.plugin.getDataFolder(), "hunters.yml");
        }
        this.huntersConfig = (FileConfiguration)YamlConfiguration.loadConfiguration(this.huntersConfigFile);
        this.plugin.log("Loaded " + this.huntersConfig.getKeys(false).size() + " werewolf hunters.");
    }
    
    public void save() {
        if (this.huntersConfig == null || this.huntersConfigFile == null) {
            return;
        }
        try {
            this.huntersConfig.save(this.huntersConfigFile);
        }
        catch (Exception ex) {
            this.plugin.log("Could not save config to " + this.huntersConfigFile + ": " + ex.getMessage());
        }
    }
    
    public boolean isHunting(final String playerName) {
        return CompassTracker.hasWatcher(playerName);
    }
    
    public void setHunting(final String playerName, final boolean hunting) {
        if (hunting) {
            CompassTracker.addWatcher(playerName);
        }
        else {
            CompassTracker.removeWatcher(playerName);
        }
    }
    
    public void addBounty(final String playerName, final int bounty) {
        final Player player = this.plugin.getServer().getPlayer(playerName);
        if (!Werewolf.getEconomy().has(playerName, (double)bounty)) {
            player.sendMessage(ChatColor.RED + "You do not have that much.");
            return;
        }
        Werewolf.getEconomy().withdrawPlayer(playerName, (double)bounty);
        this.totalBounty += bounty;
        Werewolf.getLanguageManager().setPlayerName(playerName);
        Werewolf.getLanguageManager().setAmount(Werewolf.getEconomy().format((double)bounty));
        this.plugin.getServer().broadcastMessage(Werewolf.getLanguageManager().getLanguageString(LanguageManager.LANGUAGESTRING.BountyPlayerAdded));
        Werewolf.getLanguageManager().setAmount(this.getBounty());
        this.plugin.getServer().broadcastMessage(Werewolf.getLanguageManager().getLanguageString(LanguageManager.LANGUAGESTRING.BountyTotal));
        this.plugin.log(String.valueOf(playerName) + " added " + Werewolf.getEconomy().format((double)bounty) + " to the Werewolf bounty. Bounty is now " + Werewolf.getEconomy().format((double)this.totalBounty));
    }
    
    public void autoAddBounty() {
        if (this.random.nextInt(100 + this.totalBounty / 5) > 0) {
            return;
        }
        if (this.totalBounty >= this.plugin.autoBountyMaximum) {
            return;
        }
        if (Werewolf.getWerewolfManager().getOnlineWerewolves().size() == 0) {
            return;
        }
        final int bounty = 10 + this.random.nextInt(10) * 10;
        this.totalBounty += bounty;
        if (this.totalBounty >= this.plugin.autoBountyMaximum) {
            this.totalBounty = this.plugin.autoBountyMaximum;
        }
        Werewolf.getLanguageManager().setAmount(Werewolf.getEconomy().format((double)bounty));
        this.plugin.getServer().broadcastMessage(Werewolf.getLanguageManager().getLanguageString(LanguageManager.LANGUAGESTRING.BountyServerAdded));
        Werewolf.getLanguageManager().setAmount(this.getBounty());
        this.plugin.getServer().broadcastMessage(Werewolf.getLanguageManager().getLanguageString(LanguageManager.LANGUAGESTRING.BountyTotal));
    }
    
    public String getBounty() {
        return Werewolf.getEconomy().format((double)this.totalBounty);
    }
    
    public void handleKill(final String killerName) {
        final String pattern = "HH:mm dd-MM-yyyy";
        final DateFormat formatter = new SimpleDateFormat(pattern);
        final Date thisDate = new Date();
        String message;
        if (this.totalBounty > 0) {
            Werewolf.getLanguageManager().setAmount(Werewolf.getEconomy().format((double)this.totalBounty));
            message = Werewolf.getLanguageManager().getLanguageString(LanguageManager.LANGUAGESTRING.KilledWerewolfBounty);
        }
        else {
            message = Werewolf.getLanguageManager().getLanguageString(LanguageManager.LANGUAGESTRING.KilledWerewolfNoBounty);
        }
        this.plugin.getServer().broadcastMessage(message);
        this.plugin.log(message);
        Werewolf.getEconomy().depositPlayer(killerName, (double)this.totalBounty);
        this.totalBounty = 0;
        final int kills = this.huntersConfig.getInt(String.valueOf(killerName) + ".Kills");
        this.huntersConfig.set(String.valueOf(killerName) + ".Kills", (Object)(kills + 1));
        this.huntersConfig.set(String.valueOf(killerName) + ".LastKill", (Object)formatter.format(thisDate));
        this.save();
    }
    
    public int getHunterKills(final String hunterName) {
        return this.huntersConfig.getInt(String.valueOf(hunterName) + ".Kills");
    }
    
    public Set<String> getHunters() {
        final Set<String> hunters = (Set<String>)this.huntersConfig.getKeys(false);
        return hunters;
    }
}

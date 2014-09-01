package com.dogonfire.werewolf;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Wolf;
import org.bukkit.Location;
import org.bukkit.block.Biome;
import org.bukkit.Sound;
import java.text.DateFormat;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Comparator;
import java.util.Collections;
import java.util.ArrayList;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import java.util.Collection;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import java.util.Set;
import org.bukkit.configuration.file.YamlConfiguration;
import java.util.Iterator;
import org.bukkit.World;
import java.util.UUID;
import java.util.HashMap;
import java.util.Random;
import java.io.File;
import org.bukkit.configuration.file.FileConfiguration;

public class WerewolfManager
{
    private Werewolf plugin;
    private FileConfiguration werewolvesConfig;
    private File werewolvesConfigFile;
    private Random random;
    private long lastSaveTime;
    private long lastFullMoonAnnouncementTime;
    private String datePattern;
    private HashMap<UUID, Long> lastFullMoonAnnouncementTimes;
    private HashMap<String, String> originalGroup;
    private HashMap<String, String> playerlistNames;
    private HashMap<String, Long> playersPouncing;
    private HashMap<String, Integer> packWolves;
    private static /* synthetic */ int[] $SWITCH_TABLE$org$bukkit$block$Biome;
    
    WerewolfManager(final Werewolf p) {
        super();
        this.werewolvesConfig = null;
        this.werewolvesConfigFile = null;
        this.random = new Random();
        this.lastSaveTime = 0L;
        this.lastFullMoonAnnouncementTime = 0L;
        this.datePattern = "HH:mm:ss dd-MM-yyyy";
        this.lastFullMoonAnnouncementTimes = new HashMap<UUID, Long>();
        this.originalGroup = new HashMap<String, String>();
        this.playerlistNames = new HashMap<String, String>();
        this.playersPouncing = new HashMap<String, Long>();
        this.packWolves = new HashMap<String, Integer>();
        this.plugin = p;
        for (final World world : this.plugin.getServer().getWorlds()) {
            this.lastFullMoonAnnouncementTimes.put(world.getUID(), 0L);
        }
    }
    
    public void load() {
        if (this.werewolvesConfigFile == null) {
            this.werewolvesConfigFile = new File(this.plugin.getDataFolder(), "werewolves.yml");
        }
        this.werewolvesConfig = (FileConfiguration)YamlConfiguration.loadConfiguration(this.werewolvesConfigFile);
        this.plugin.log("Loaded " + this.werewolvesConfig.getKeys(false).size() + " werewolves.");
    }
    
    public void saveTimed() {
        if (System.currentTimeMillis() - this.lastSaveTime < 180000L) {
            return;
        }
        this.save();
    }
    
    public void save() {
        this.lastSaveTime = System.currentTimeMillis();
        if (this.werewolvesConfig == null || this.werewolvesConfigFile == null) {
            return;
        }
        try {
            this.werewolvesConfig.save(this.werewolvesConfigFile);
        }
        catch (Exception ex) {
            this.plugin.log("Could not save config to " + this.werewolvesConfigFile + ": " + ex.getMessage());
        }
    }
    
    public int getNumberOfWerewolves() {
        final Set<String> werewolves = (Set<String>)this.werewolvesConfig.getKeys(false);
        if (werewolves == null) {
            return 0;
        }
        return this.werewolvesConfig.getKeys(false).size();
    }
    
    public Player getNearestWerewolf(final String playerName) {
        final Player player = this.plugin.getServer().getPlayer(playerName);
        Player minWerewolf = null;
        float minDist = 999999.0f;
        if (player == null) {
            return null;
        }
        Player[] onlinePlayers;
        for (int length = (onlinePlayers = this.plugin.getServer().getOnlinePlayers()).length, i = 0; i < length; ++i) {
            final Player onlinePlayer = onlinePlayers[i];
            if (this.hasWerewolfSkin(onlinePlayer.getName()) && player.getWorld() == onlinePlayer.getWorld() && player.getEntityId() != onlinePlayer.getEntityId()) {
                final float dist = (float)onlinePlayer.getLocation().toVector().subtract(player.getLocation().toVector()).length();
                if (dist < minDist) {
                    minWerewolf = onlinePlayer;
                    minDist = dist;
                }
            }
        }
        return minWerewolf;
    }
    
    public String getPlayerListName(final Player player) {
        return this.playerlistNames.get(ChatColor.stripColor(player.getName()));
    }
    
    public void pushPlayerData(final Player player) {
        final String groupName = "NoGroup";
        final String cleanName = ChatColor.stripColor(player.getName());
        this.plugin.logDebug("Putting " + cleanName + " into the playerData");
        this.playerlistNames.put(cleanName, ChatColor.stripColor(player.getPlayerListName()));
        this.originalGroup.put(cleanName, groupName);
        this.setWolfForm(cleanName);
    }
    
    public void popPlayerData(final String playerName) {
        final String cleanName = ChatColor.stripColor(playerName);
        if (this.playerlistNames.containsKey(cleanName)) {
            this.playerlistNames.remove(cleanName);
        }
        if (this.originalGroup.containsKey(cleanName)) {
            this.originalGroup.remove(cleanName);
        }
    }
    
    public String getOriginalPermissionGroup(final String playerName) {
        return this.werewolvesConfig.getString(String.valueOf(playerName) + ".OriginalGroup");
    }
    
    public void setOriginalPermissionGroup(final String playerName, final String groupName) {
        this.werewolvesConfig.set(String.valueOf(playerName) + ".OriginalGroup", (Object)groupName);
        this.saveTimed();
    }
    
    public Collection<String> getOnlineWerewolves() {
        return this.playerlistNames.keySet();
    }
    
    public Set<String> getAllWerewolves() {
        return (Set<String>)this.werewolvesConfig.getKeys(false);
    }
    
    public Set<String> getWerewolvesInClan(final ClanManager.ClanType clanType) {
        return (Set<String>)this.werewolvesConfig.getKeys(false);
    }
    
    public boolean makeWerewolf(final Player player, final boolean turnNow, final ClanManager.ClanType clan) {
        if (turnNow) {
            this.setHumanForm(player.getName());
            this.setInfectedThisNight(player.getName(), false);
            this.setWerewolfClan(player.getName(), clan);
            this.setWerewolfSkin(player);
            this.setWolfForm(player.getName());
            this.plugin.log(String.valueOf(player.getName()) + " was made an full werewolf");
        }
        else {
            this.setLastTransformation(player.getName());
            this.setInfectedThisNight(player.getName(), true);
            this.setWerewolfClan(player.getName(), clan);
            this.setInfectedWerewolf(player.getName());
            this.plugin.log(String.valueOf(player.getName()) + " was made a infected werewolf");
        }
        return true;
    }
    
    public void unmakeWerewolf(final String playerName) {
        final Player player = this.plugin.getServer().getPlayer(playerName);
        if (player != null) {
            Werewolf.pu.addPotionEffectNoGraphic(player, new PotionEffect(PotionEffectType.CONFUSION, 100, 1));
        }
        this.plugin.undisguiseWerewolf(playerName, true, true);
        if (this.plugin.useClans) {
            final ClanManager.ClanType clan = Werewolf.getWerewolfManager().getWerewolfClan(playerName);
            Werewolf.getClanManager().assignAlphaInClan(clan, null);
        }
        this.plugin.log(String.valueOf(playerName) + " was unmade from being a werewolf");
    }
    
    public void setWerewolfSkin(final Player player) {
        if (!Werewolf.pluginEnabled) {
            return;
        }
        if (!this.isWerewolf(player)) {
            return;
        }
        if (this.hasWerewolfSkin(player.getName())) {
            return;
        }
        if (this.isInfectedThisNight(player.getName())) {
            return;
        }
        this.plugin.disguiseWerewolf(player);
        this.howl(player);
    }
    
    public void unsetWerewolfSkin(final String playerName, final boolean makeVisible) {
        if (!this.isWerewolf(playerName)) {
            this.plugin.logDebug("unsetWerewolfSkin(): " + playerName + " does not have werewolf skin");
            return;
        }
        this.setInfectedThisNight(playerName, false);
        if (!this.hasWerewolfSkin(playerName)) {
            this.plugin.logDebug("unsetWerewolfSkin(): " + playerName + " does not have a werewolf skin");
            return;
        }
        if (this.isFullWerewolf(playerName)) {
            final Player player = this.plugin.getServer().getPlayer(playerName);
            if (player != null) {
                final String message = Werewolf.getLanguageManager().getLanguageString(LanguageManager.LANGUAGESTRING.Untransform);
                player.sendMessage(ChatColor.LIGHT_PURPLE + message);
            }
        }
        this.plugin.log(String.valueOf(playerName) + " turned into human form!");
        this.plugin.undisguiseWerewolf(playerName, makeVisible, false);
    }
    
    public boolean canTransform(final Player player) {
        return !this.hasWerewolfSkin(player.getName()) && !this.isInfectedThisNight(player.getName()) && this.plugin.isFullMoonInWorld(player.getWorld()) && this.plugin.isWerewolvesAllowedInWorld(player) && this.plugin.isUnderOpenSky(player) && !this.canControlledTransformation(player.getName());
    }
    
    public boolean canUntransform(final Player player) {
        return (this.hasWerewolfSkin(player.getName()) || this.isInfectedThisNight(player.getName())) && !this.plugin.isNightInWorld(player.getWorld()) && !this.canControlledTransformation(player.getName());
    }
    
    public boolean isWerewolf(final Player player) {
        return this.isWerewolf(player.getName());
    }
    
    public boolean isWerewolf(final String playerName) {
        if (this.hasWerewolfSkin(playerName)) {
            return true;
        }
        final String wolfState = this.werewolvesConfig.getString(String.valueOf(playerName) + ".WolfState");
        return wolfState != null;
    }
    
    public boolean isFullWerewolf(final String playerName) {
        if (playerName == null) {
            return false;
        }
        final String wolfType = this.werewolvesConfig.getString(String.valueOf(playerName) + ".WolfState");
        if (wolfType == null) {
            return false;
        }
        WolfState wolfState = WolfState.None;
        try {
            wolfState = WolfState.valueOf(wolfType);
        }
        catch (Exception ex) {
            this.setNoWerewolf(playerName);
            return false;
        }
        return wolfState == WolfState.WolfForm || wolfState == WolfState.HumanForm;
    }
    
    public boolean isInfectedWerewolf(final String playerName) {
        final String wolfType = this.werewolvesConfig.getString(String.valueOf(playerName) + ".WolfState");
        if (wolfType == null) {
            return false;
        }
        WolfState wolfState = WolfState.None;
        try {
            wolfState = WolfState.valueOf(wolfType);
        }
        catch (Exception ex) {
            this.setNoWerewolf(playerName);
            return false;
        }
        return wolfState == WolfState.Infected;
    }
    
    public boolean isWolfForm(final String playerName) {
        if (playerName == null) {
            return false;
        }
        final String wolfType = this.werewolvesConfig.getString(String.valueOf(playerName) + ".WolfState");
        WolfState wolfState = WolfState.None;
        try {
            wolfState = WolfState.valueOf(wolfType);
        }
        catch (Exception ex) {
            this.setNoWerewolf(playerName);
            return false;
        }
        return wolfState == WolfState.WolfForm;
    }
    
    public boolean isHumanForm(final String playerName) {
        if (playerName == null) {
            return false;
        }
        final String wolfType = this.werewolvesConfig.getString(String.valueOf(playerName) + ".WolfState");
        if (wolfType == null) {
            return false;
        }
        WolfState wolfState = WolfState.None;
        try {
            wolfState = WolfState.valueOf(wolfType);
        }
        catch (Exception ex) {
            this.setNoWerewolf(playerName);
            return false;
        }
        return wolfState == WolfState.HumanForm;
    }
    
    public ClanManager.ClanType getWerewolfClan(final String playerName) {
        if (playerName == null) {
            return ClanManager.ClanType.Potion;
        }
        final String clanTypeString = this.werewolvesConfig.getString(String.valueOf(playerName) + ".Clan");
        ClanManager.ClanType clanType = ClanManager.ClanType.WerewolfBite;
        try {
            clanType = ClanManager.ClanType.valueOf(clanTypeString);
        }
        catch (Exception ex) {
            clanType = ClanManager.ClanType.values()[this.random.nextInt(ClanManager.ClanType.values().length)];
            this.werewolvesConfig.set(String.valueOf(playerName) + ".Clan", (Object)clanType.name());
            this.saveTimed();
        }
        return clanType;
    }
    
    public String getAlphaCandidate(final ClanManager.ClanType clan) {
        final Set<String> playerList = (Set<String>)this.werewolvesConfig.getKeys(false);
        final List<String> clanMembers = new ArrayList<String>();
        for (final String playerName : playerList) {
            final String clanName = this.werewolvesConfig.getString(String.valueOf(playerName) + ".Clan");
            if (clanName != null && clanName.equals(clan.name())) {
                clanMembers.add(playerName);
            }
        }
        Collections.sort(clanMembers, new TransformationsComparator());
        if (clanMembers.size() == 0) {
            return null;
        }
        return clanMembers.get(0);
    }
    
    public List<String> getWerewolfClanMembers(final ClanManager.ClanType clan) {
        final Set<String> playerList = (Set<String>)this.werewolvesConfig.getKeys(false);
        final List<String> clanMembers = new ArrayList<String>();
        for (final String playerName : playerList) {
            final String clanString = this.werewolvesConfig.getString(String.valueOf(playerName) + ".Clan");
            if (clanString != null) {
                ClanManager.ClanType clanType = null;
                try {
                    clanType = ClanManager.ClanType.valueOf(clanString);
                }
                catch (Exception ex) {}
                if (clanType == null || clanType != clan) {
                    continue;
                }
                clanMembers.add(playerName);
            }
        }
        return clanMembers;
    }
    
    public boolean isInfectedThisNight(final String playerName) {
        return this.werewolvesConfig.getBoolean(String.valueOf(playerName) + ".InfectedThisNight");
    }
    
    public void setInfectedThisNight(final String playerName, final boolean thisNight) {
        if (thisNight) {
            this.werewolvesConfig.set(String.valueOf(playerName) + ".InfectedThisNight", (Object)thisNight);
        }
        else {
            this.werewolvesConfig.set(String.valueOf(playerName) + ".InfectedThisNight", (Object)thisNight);
        }
        this.saveTimed();
    }
    
    public void setWerewolfClan(final String playerName, final ClanManager.ClanType clan) {
        if (Werewolf.getClanManager().getAlpha(clan) == null) {
            final String alphaName = this.getAlphaCandidate(clan);
            if (alphaName == null) {
                Werewolf.getClanManager().assignAlphaInClan(clan, playerName);
            }
            else {
                Werewolf.getClanManager().assignAlphaInClan(clan, alphaName);
            }
        }
        this.werewolvesConfig.set(String.valueOf(playerName) + ".Clan", (Object)clan.name());
        this.saveTimed();
    }
    
    public void incrementNumberOfFullMoonTransformations(final String playerName) {
        int transforms = this.werewolvesConfig.getInt(String.valueOf(playerName) + ".Transformations");
        ++transforms;
        this.werewolvesConfig.set(String.valueOf(playerName) + ".Transformations", (Object)transforms);
    }
    
    public void setLastTransformation(final String playerName) {
        final DateFormat formatter = new SimpleDateFormat(this.datePattern);
        final Date thisDate = new Date();
        this.werewolvesConfig.set(String.valueOf(playerName) + ".LastTransform", (Object)formatter.format(thisDate));
        this.saveTimed();
    }
    
    public int getNumberOfTransformations(final String playerName) {
        return this.werewolvesConfig.getInt(String.valueOf(playerName) + ".Transformations");
    }
    
    public boolean canControlledTransformation(final String playerName) {
        return this.getNumberOfTransformations(playerName) >= this.plugin.transformsForControlledTransformation;
    }
    
    public boolean hasRecentTransformAutoCure(final String playerName) {
        final DateFormat formatter = new SimpleDateFormat(this.datePattern);
        final Date thisDate = new Date();
        Date transformDate = null;
        final String transformDateString = this.werewolvesConfig.getString(String.valueOf(playerName) + ".LastTransform");
        try {
            transformDate = formatter.parse(transformDateString);
        }
        catch (Exception ex) {
            this.plugin.log(String.valueOf(playerName) + " has invalid LastTransform date. Resetting.");
            transformDate = new Date();
            transformDate.setTime(0L);
        }
        final long diff = thisDate.getTime() - transformDate.getTime();
        final long diffDays = diff / 86400000L;
        return diffDays < this.plugin.autoCureDays;
    }
    
    public boolean hasRecentTransform(final String playerName) {
        final DateFormat formatter = new SimpleDateFormat(this.datePattern);
        final Date thisDate = new Date();
        Date transformDate = null;
        final String transformDateString = this.werewolvesConfig.getString(String.valueOf(playerName) + ".LastTransform");
        try {
            transformDate = formatter.parse(transformDateString);
        }
        catch (Exception ex) {
            this.plugin.log(String.valueOf(playerName) + " has invalid LastTransform date. Resetting.");
            transformDate = new Date();
            transformDate.setTime(0L);
        }
        final long diff = thisDate.getTime() - transformDate.getTime();
        final long diffSeconds = diff / 1000L;
        return diffSeconds < 600L;
    }
    
    public boolean hasWerewolfSkin(final String playerName) {
        return this.playerlistNames.containsValue(playerName);
    }
    
    public void setHumanForm(final String playerName) {
        this.werewolvesConfig.set(String.valueOf(playerName) + ".WolfState", (Object)WolfState.HumanForm.name());
        this.saveTimed();
    }
    
    public void setWolfForm(final String playerName) {
        this.werewolvesConfig.set(String.valueOf(playerName) + ".WolfState", (Object)WolfState.WolfForm.name());
        this.saveTimed();
    }
    
    public void setInfectedWerewolf(final String playerName) {
        final Date thisDate = new Date();
        final String pattern = "HH:mm dd-MM-yyyy";
        final DateFormat formatter = new SimpleDateFormat(pattern);
        this.werewolvesConfig.set(String.valueOf(playerName) + ".WolfState", (Object)WolfState.Infected.name());
        this.werewolvesConfig.set(String.valueOf(playerName) + ".InfectedDate", (Object)formatter.format(thisDate));
        this.saveTimed();
    }
    
    public void setNoWerewolf(final String playerName) {
        this.werewolvesConfig.set(playerName, (Object)null);
        this.saveTimed();
    }
    
    public void howl(final Player player) {
        player.getWorld().playSound(player.getLocation(), Sound.WOLF_HOWL, 10.0f, 1.0f);
    }
    
    public void growl(final Player player) {
        player.getWorld().playSound(player.getLocation(), Sound.WOLF_GROWL, 10.0f, 1.0f);
    }
    
    public void sendWerewolfUrges(final Player player) {
        String message = "";
        if (this.random.nextInt(20 + 10 * Werewolf.getWerewolfManager().getNumberOfTransformations(player.getName())) > 0) {
            return;
        }
        if (!this.isWerewolf(player)) {
            return;
        }
        if (Werewolf.getWerewolfManager().hasWerewolfSkin(player.getName())) {
            message = Werewolf.getLanguageManager().getLanguageString(LanguageManager.LANGUAGESTRING.UrgesInfectedThisNight);
        }
        else if (this.isInfectedWerewolf(player.getName())) {
            if (this.isInfectedThisNight(player.getName())) {
                message = Werewolf.getLanguageManager().getLanguageString(LanguageManager.LANGUAGESTRING.UrgesInfectedThisNight);
            }
            else {
                message = Werewolf.getLanguageManager().getLanguageString(LanguageManager.LANGUAGESTRING.UrgesInfectedHumanForm);
            }
        }
        else if (this.plugin.isNightInWorld(player.getWorld()) && player.getWorld().getHighestBlockYAt(player.getLocation()) > player.getLocation().getBlockY()) {
            message = Werewolf.getLanguageManager().getLanguageString(LanguageManager.LANGUAGESTRING.UrgesInside);
        }
        if (!message.isEmpty()) {
            player.sendMessage(ChatColor.LIGHT_PURPLE + message);
        }
    }
    
    public void setPouncing(final String playerName) {
        this.playersPouncing.put(playerName, System.currentTimeMillis());
    }
    
    public boolean isPouncing(final String playerName) {
        return this.playersPouncing.containsKey(playerName);
    }
    
    public void removePouncing(final String playerName) {
        if (!this.playersPouncing.containsKey(playerName)) {
            return;
        }
        if (this.playersPouncing.get(playerName) < System.currentTimeMillis() - 1000L) {
            this.playersPouncing.remove(playerName);
        }
    }
    
    public void addPackWolf(final String playerName) {
        int numberOfWolves = 0;
        if (this.packWolves.containsKey(playerName)) {
            numberOfWolves = this.packWolves.get(playerName);
        }
        ++numberOfWolves;
        this.packWolves.put(playerName, numberOfWolves);
    }
    
    public int getPackWolves(final String playerName) {
        if (this.packWolves.containsKey(playerName)) {
            return this.packWolves.get(playerName);
        }
        return 0;
    }
    
    public void clearPackWolves(final String playerName) {
        if (this.packWolves.containsKey(playerName)) {
            this.packWolves.remove(playerName);
        }
    }
    
    public void update() {
        if (!Werewolf.pluginEnabled) {
            return;
        }
        if (this.random.nextInt(3) == 0) {
            final World world = (World)this.plugin.getServer().getWorlds().toArray()[this.random.nextInt(this.plugin.getServer().getWorlds().size())];
            if (this.plugin.isFullMoonDuskInWorld(world)) {
                if (!this.lastFullMoonAnnouncementTimes.containsKey(world.getUID())) {
                    this.lastFullMoonAnnouncementTimes.put(world.getUID(), 0L);
                }
                if (System.currentTimeMillis() - this.lastFullMoonAnnouncementTimes.get(world.getUID()) > 600000L) {
                    final int numberOfTurningWerewolves = Werewolf.getWerewolfManager().getOnlineWerewolves().size();
                    if (numberOfTurningWerewolves > 0) {
                        Werewolf.getLanguageManager().setAmount(new StringBuilder().append(numberOfTurningWerewolves).toString());
                        final String fullMoonText = Werewolf.getLanguageManager().getLanguageString(LanguageManager.LANGUAGESTRING.FullMoonIsRising);
                        for (final Player player : world.getPlayers()) {
                            this.plugin.sendInfo(player, fullMoonText);
                        }
                    }
                    this.lastFullMoonAnnouncementTimes.put(world.getUID(), System.currentTimeMillis());
                }
            }
        }
        if (this.plugin.autoCureDays > 0 && this.random.nextInt(100) == 0 && Werewolf.getWerewolfManager().getAllWerewolves().size() > 0) {
            final int r = this.random.nextInt(Werewolf.getWerewolfManager().getAllWerewolves().size());
            final String werewolfPlayerName = (String)Werewolf.getWerewolfManager().getAllWerewolves().toArray()[r];
            if (!this.hasRecentTransformAutoCure(werewolfPlayerName)) {
                this.unmakeWerewolf(werewolfPlayerName);
                this.plugin.log(String.valueOf(werewolfPlayerName) + " has not transformed for " + this.plugin.autoCureDays + " days. Removing his werewolf infection.");
            }
        }
        if (this.plugin.getServer().getOnlinePlayers().length == 0) {
            return;
        }
        final Player player2 = this.plugin.getServer().getOnlinePlayers()[this.random.nextInt(this.plugin.getServer().getOnlinePlayers().length)];
        if (this.isWerewolf(player2)) {
            if (this.plugin.isVampire(player2)) {
                this.plugin.log(String.valueOf(player2.getName()) + " is a Vampire! Removing his Werewolf infection...");
                Werewolf.getWerewolfManager().unmakeWerewolf(player2.getName());
                return;
            }
            if (Werewolf.getWerewolfManager().canTransform(player2)) {
                this.plugin.transform(player2);
                return;
            }
            if (Werewolf.getWerewolfManager().canUntransform(player2)) {
                this.plugin.untransform(player2);
                return;
            }
            if (this.plugin.werewolfUrges) {
                this.sendWerewolfUrges(player2);
            }
        }
        if (this.random.nextInt(10) == 0 && this.isValidBiomeForWildWolf(player2.getWorld().getBiome(player2.getLocation().getBlockX(), player2.getLocation().getBlockZ()))) {
            final int wolves = this.getPackWolves(player2.getName());
            if (this.random.nextInt(10 * wolves + 5) == 0) {
                this.spawnWildWolf(player2);
            }
        }
        if (this.plugin.autoBounty && this.plugin.vaultEnabled) {
            Werewolf.getHuntManager().autoAddBounty();
        }
    }
    
    private boolean isValidBiomeForWildWolf(final Biome biome) {
        switch ($SWITCH_TABLE$org$bukkit$block$Biome()[biome.ordinal()]) {
            case 2:
            case 5:
            case 19:
            case 22:
            case 31:
            case 32:
            case 49: {
                return true;
            }
            default: {
                return false;
            }
        }
    }
    
    private void spawnWildWolf(final Player player) {
        final World world = player.getWorld();
        final Location center = player.getLocation();
        final int minDist = 15;
        final int maxDist = 20;
        int x;
        int z;
        do {
            x = this.random.nextInt(maxDist * 2) - maxDist + center.getBlockX();
            z = this.random.nextInt(maxDist * 2) - maxDist + center.getBlockZ();
        } while (Math.abs(x - center.getBlockX()) < minDist || Math.abs(z - center.getBlockZ()) < minDist);
        final int y = world.getHighestBlockYAt(x, z);
        final Location spawnLocation = new Location(world, (double)x, (double)y, (double)z);
        final Wolf wolf = (Wolf)world.spawnEntity(spawnLocation, EntityType.WOLF);
        wolf.setTarget((LivingEntity)player);
    }
    
    static /* synthetic */ int[] $SWITCH_TABLE$org$bukkit$block$Biome() {
        final int[] $switch_TABLE$org$bukkit$block$Biome = WerewolfManager.$SWITCH_TABLE$org$bukkit$block$Biome;
        if ($switch_TABLE$org$bukkit$block$Biome != null) {
            return $switch_TABLE$org$bukkit$block$Biome;
        }
        final int[] $switch_TABLE$org$bukkit$block$Biome2 = new int[Biome.values().length];
        try {
            $switch_TABLE$org$bukkit$block$Biome2[Biome.BEACH.ordinal()] = 17;
        }
        catch (NoSuchFieldError noSuchFieldError) {}
        try {
            $switch_TABLE$org$bukkit$block$Biome2[Biome.BIRCH_FOREST.ordinal()] = 28;
        }
        catch (NoSuchFieldError noSuchFieldError2) {}
        try {
            $switch_TABLE$org$bukkit$block$Biome2[Biome.BIRCH_FOREST_HILLS.ordinal()] = 29;
        }
        catch (NoSuchFieldError noSuchFieldError3) {}
        try {
            $switch_TABLE$org$bukkit$block$Biome2[Biome.BIRCH_FOREST_HILLS_MOUNTAINS.ordinal()] = 56;
        }
        catch (NoSuchFieldError noSuchFieldError4) {}
        try {
            $switch_TABLE$org$bukkit$block$Biome2[Biome.BIRCH_FOREST_MOUNTAINS.ordinal()] = 55;
        }
        catch (NoSuchFieldError noSuchFieldError5) {}
        try {
            $switch_TABLE$org$bukkit$block$Biome2[Biome.COLD_BEACH.ordinal()] = 27;
        }
        catch (NoSuchFieldError noSuchFieldError6) {}
        try {
            $switch_TABLE$org$bukkit$block$Biome2[Biome.COLD_TAIGA.ordinal()] = 31;
        }
        catch (NoSuchFieldError noSuchFieldError7) {}
        try {
            $switch_TABLE$org$bukkit$block$Biome2[Biome.COLD_TAIGA_HILLS.ordinal()] = 32;
        }
        catch (NoSuchFieldError noSuchFieldError8) {}
        try {
            $switch_TABLE$org$bukkit$block$Biome2[Biome.COLD_TAIGA_MOUNTAINS.ordinal()] = 49;
        }
        catch (NoSuchFieldError noSuchFieldError9) {}
        try {
            $switch_TABLE$org$bukkit$block$Biome2[Biome.DEEP_OCEAN.ordinal()] = 25;
        }
        catch (NoSuchFieldError noSuchFieldError10) {}
        try {
            $switch_TABLE$org$bukkit$block$Biome2[Biome.DESERT.ordinal()] = 4;
        }
        catch (NoSuchFieldError noSuchFieldError11) {}
        try {
            $switch_TABLE$org$bukkit$block$Biome2[Biome.DESERT_HILLS.ordinal()] = 18;
        }
        catch (NoSuchFieldError noSuchFieldError12) {}
        try {
            $switch_TABLE$org$bukkit$block$Biome2[Biome.DESERT_MOUNTAINS.ordinal()] = 42;
        }
        catch (NoSuchFieldError noSuchFieldError13) {}
        try {
            $switch_TABLE$org$bukkit$block$Biome2[Biome.EXTREME_HILLS.ordinal()] = 10;
        }
        catch (NoSuchFieldError noSuchFieldError14) {}
        try {
            $switch_TABLE$org$bukkit$block$Biome2[Biome.EXTREME_HILLS_MOUNTAINS.ordinal()] = 59;
        }
        catch (NoSuchFieldError noSuchFieldError15) {}
        try {
            $switch_TABLE$org$bukkit$block$Biome2[Biome.EXTREME_HILLS_PLUS.ordinal()] = 35;
        }
        catch (NoSuchFieldError noSuchFieldError16) {}
        try {
            $switch_TABLE$org$bukkit$block$Biome2[Biome.EXTREME_HILLS_PLUS_MOUNTAINS.ordinal()] = 60;
        }
        catch (NoSuchFieldError noSuchFieldError17) {}
        try {
            $switch_TABLE$org$bukkit$block$Biome2[Biome.FLOWER_FOREST.ordinal()] = 43;
        }
        catch (NoSuchFieldError noSuchFieldError18) {}
        try {
            $switch_TABLE$org$bukkit$block$Biome2[Biome.FOREST.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError19) {}
        try {
            $switch_TABLE$org$bukkit$block$Biome2[Biome.FOREST_HILLS.ordinal()] = 19;
        }
        catch (NoSuchFieldError noSuchFieldError20) {}
        try {
            $switch_TABLE$org$bukkit$block$Biome2[Biome.FROZEN_OCEAN.ordinal()] = 11;
        }
        catch (NoSuchFieldError noSuchFieldError21) {}
        try {
            $switch_TABLE$org$bukkit$block$Biome2[Biome.FROZEN_RIVER.ordinal()] = 12;
        }
        catch (NoSuchFieldError noSuchFieldError22) {}
        try {
            $switch_TABLE$org$bukkit$block$Biome2[Biome.HELL.ordinal()] = 6;
        }
        catch (NoSuchFieldError noSuchFieldError23) {}
        try {
            $switch_TABLE$org$bukkit$block$Biome2[Biome.ICE_MOUNTAINS.ordinal()] = 14;
        }
        catch (NoSuchFieldError noSuchFieldError24) {}
        try {
            $switch_TABLE$org$bukkit$block$Biome2[Biome.ICE_PLAINS.ordinal()] = 13;
        }
        catch (NoSuchFieldError noSuchFieldError25) {}
        try {
            $switch_TABLE$org$bukkit$block$Biome2[Biome.ICE_PLAINS_SPIKES.ordinal()] = 46;
        }
        catch (NoSuchFieldError noSuchFieldError26) {}
        try {
            $switch_TABLE$org$bukkit$block$Biome2[Biome.JUNGLE.ordinal()] = 22;
        }
        catch (NoSuchFieldError noSuchFieldError27) {}
        try {
            $switch_TABLE$org$bukkit$block$Biome2[Biome.JUNGLE_EDGE.ordinal()] = 24;
        }
        catch (NoSuchFieldError noSuchFieldError28) {}
        try {
            $switch_TABLE$org$bukkit$block$Biome2[Biome.JUNGLE_EDGE_MOUNTAINS.ordinal()] = 48;
        }
        catch (NoSuchFieldError noSuchFieldError29) {}
        try {
            $switch_TABLE$org$bukkit$block$Biome2[Biome.JUNGLE_HILLS.ordinal()] = 23;
        }
        catch (NoSuchFieldError noSuchFieldError30) {}
        try {
            $switch_TABLE$org$bukkit$block$Biome2[Biome.JUNGLE_MOUNTAINS.ordinal()] = 47;
        }
        catch (NoSuchFieldError noSuchFieldError31) {}
        try {
            $switch_TABLE$org$bukkit$block$Biome2[Biome.MEGA_SPRUCE_TAIGA.ordinal()] = 58;
        }
        catch (NoSuchFieldError noSuchFieldError32) {}
        try {
            $switch_TABLE$org$bukkit$block$Biome2[Biome.MEGA_SPRUCE_TAIGA_HILLS.ordinal()] = 61;
        }
        catch (NoSuchFieldError noSuchFieldError33) {}
        try {
            $switch_TABLE$org$bukkit$block$Biome2[Biome.MEGA_TAIGA.ordinal()] = 33;
        }
        catch (NoSuchFieldError noSuchFieldError34) {}
        try {
            $switch_TABLE$org$bukkit$block$Biome2[Biome.MEGA_TAIGA_HILLS.ordinal()] = 34;
        }
        catch (NoSuchFieldError noSuchFieldError35) {}
        try {
            $switch_TABLE$org$bukkit$block$Biome2[Biome.MESA.ordinal()] = 38;
        }
        catch (NoSuchFieldError noSuchFieldError36) {}
        try {
            $switch_TABLE$org$bukkit$block$Biome2[Biome.MESA_BRYCE.ordinal()] = 52;
        }
        catch (NoSuchFieldError noSuchFieldError37) {}
        try {
            $switch_TABLE$org$bukkit$block$Biome2[Biome.MESA_PLATEAU.ordinal()] = 40;
        }
        catch (NoSuchFieldError noSuchFieldError38) {}
        try {
            $switch_TABLE$org$bukkit$block$Biome2[Biome.MESA_PLATEAU_FOREST.ordinal()] = 39;
        }
        catch (NoSuchFieldError noSuchFieldError39) {}
        try {
            $switch_TABLE$org$bukkit$block$Biome2[Biome.MESA_PLATEAU_FOREST_MOUNTAINS.ordinal()] = 53;
        }
        catch (NoSuchFieldError noSuchFieldError40) {}
        try {
            $switch_TABLE$org$bukkit$block$Biome2[Biome.MESA_PLATEAU_MOUNTAINS.ordinal()] = 54;
        }
        catch (NoSuchFieldError noSuchFieldError41) {}
        try {
            $switch_TABLE$org$bukkit$block$Biome2[Biome.MUSHROOM_ISLAND.ordinal()] = 15;
        }
        catch (NoSuchFieldError noSuchFieldError42) {}
        try {
            $switch_TABLE$org$bukkit$block$Biome2[Biome.MUSHROOM_SHORE.ordinal()] = 16;
        }
        catch (NoSuchFieldError noSuchFieldError43) {}
        try {
            $switch_TABLE$org$bukkit$block$Biome2[Biome.OCEAN.ordinal()] = 8;
        }
        catch (NoSuchFieldError noSuchFieldError44) {}
        try {
            $switch_TABLE$org$bukkit$block$Biome2[Biome.PLAINS.ordinal()] = 5;
        }
        catch (NoSuchFieldError noSuchFieldError45) {}
        try {
            $switch_TABLE$org$bukkit$block$Biome2[Biome.RIVER.ordinal()] = 9;
        }
        catch (NoSuchFieldError noSuchFieldError46) {}
        try {
            $switch_TABLE$org$bukkit$block$Biome2[Biome.ROOFED_FOREST.ordinal()] = 30;
        }
        catch (NoSuchFieldError noSuchFieldError47) {}
        try {
            $switch_TABLE$org$bukkit$block$Biome2[Biome.ROOFED_FOREST_MOUNTAINS.ordinal()] = 57;
        }
        catch (NoSuchFieldError noSuchFieldError48) {}
        try {
            $switch_TABLE$org$bukkit$block$Biome2[Biome.SAVANNA.ordinal()] = 36;
        }
        catch (NoSuchFieldError noSuchFieldError49) {}
        try {
            $switch_TABLE$org$bukkit$block$Biome2[Biome.SAVANNA_MOUNTAINS.ordinal()] = 50;
        }
        catch (NoSuchFieldError noSuchFieldError50) {}
        try {
            $switch_TABLE$org$bukkit$block$Biome2[Biome.SAVANNA_PLATEAU.ordinal()] = 37;
        }
        catch (NoSuchFieldError noSuchFieldError51) {}
        try {
            $switch_TABLE$org$bukkit$block$Biome2[Biome.SAVANNA_PLATEAU_MOUNTAINS.ordinal()] = 51;
        }
        catch (NoSuchFieldError noSuchFieldError52) {}
        try {
            $switch_TABLE$org$bukkit$block$Biome2[Biome.SKY.ordinal()] = 7;
        }
        catch (NoSuchFieldError noSuchFieldError53) {}
        try {
            $switch_TABLE$org$bukkit$block$Biome2[Biome.SMALL_MOUNTAINS.ordinal()] = 21;
        }
        catch (NoSuchFieldError noSuchFieldError54) {}
        try {
            $switch_TABLE$org$bukkit$block$Biome2[Biome.STONE_BEACH.ordinal()] = 26;
        }
        catch (NoSuchFieldError noSuchFieldError55) {}
        try {
            $switch_TABLE$org$bukkit$block$Biome2[Biome.SUNFLOWER_PLAINS.ordinal()] = 41;
        }
        catch (NoSuchFieldError noSuchFieldError56) {}
        try {
            $switch_TABLE$org$bukkit$block$Biome2[Biome.SWAMPLAND.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError57) {}
        try {
            $switch_TABLE$org$bukkit$block$Biome2[Biome.SWAMPLAND_MOUNTAINS.ordinal()] = 45;
        }
        catch (NoSuchFieldError noSuchFieldError58) {}
        try {
            $switch_TABLE$org$bukkit$block$Biome2[Biome.TAIGA.ordinal()] = 3;
        }
        catch (NoSuchFieldError noSuchFieldError59) {}
        try {
            $switch_TABLE$org$bukkit$block$Biome2[Biome.TAIGA_HILLS.ordinal()] = 20;
        }
        catch (NoSuchFieldError noSuchFieldError60) {}
        try {
            $switch_TABLE$org$bukkit$block$Biome2[Biome.TAIGA_MOUNTAINS.ordinal()] = 44;
        }
        catch (NoSuchFieldError noSuchFieldError61) {}
        return WerewolfManager.$SWITCH_TABLE$org$bukkit$block$Biome = $switch_TABLE$org$bukkit$block$Biome2;
    }
    
    public class TransformationsComparator implements Comparator<String>
    {
        @Override
        public int compare(final String member1, final String member2) {
            return Werewolf.getWerewolfManager().getNumberOfTransformations(member2) - Werewolf.getWerewolfManager().getNumberOfTransformations(member1);
        }
    }
    
    enum WolfState
    {
        None("None", 0), 
        Infected("Infected", 1), 
        HumanForm("HumanForm", 2), 
        WolfForm("WolfForm", 3);
    }
}

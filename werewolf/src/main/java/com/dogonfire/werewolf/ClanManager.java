package com.dogonfire.werewolf;

import java.text.DateFormat;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.Set;
import java.util.Comparator;
import java.util.Collections;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.configuration.file.YamlConfiguration;
import java.io.File;
import org.bukkit.configuration.file.FileConfiguration;
import java.util.HashMap;

public class ClanManager
{
    private Werewolf plugin;
    private long lastSaveTime;
    private HashMap<ClanType, String> werewolfAccount;
    private FileConfiguration clansConfig;
    private File clansConfigFile;
    private HashMap<ClanType, Double> totalClanPoints;
    private HashMap<String, Double> playerClanPoints;
    private HashMap<ClanType, String> clanNames;
    private String datePattern;
    private static /* synthetic */ int[] $SWITCH_TABLE$org$bukkit$entity$EntityType;
    
    ClanManager(final Werewolf plugin) {
        super();
        this.lastSaveTime = 0L;
        this.werewolfAccount = new HashMap<ClanType, String>();
        this.clansConfig = null;
        this.clansConfigFile = null;
        this.totalClanPoints = new HashMap<ClanType, Double>();
        this.playerClanPoints = new HashMap<String, Double>();
        this.clanNames = new HashMap<ClanType, String>();
        this.datePattern = "HH:mm:ss dd-MM-yyyy";
        this.plugin = plugin;
    }
    
    void load() {
        if (this.clansConfigFile == null) {
            this.clansConfigFile = new File(this.plugin.getDataFolder(), "clans.yml");
        }
        this.clansConfig = (FileConfiguration)YamlConfiguration.loadConfiguration(this.clansConfigFile);
        this.plugin.log("Loaded " + this.clansConfig.getKeys(false).size() + " clans.");
        this.clanNames.put(ClanType.Potion, "Witherfang");
        this.clanNames.put(ClanType.WerewolfBite, "Silvermane");
        this.clanNames.put(ClanType.WildBite, "Bloodmoon");
        this.werewolfAccount.put(ClanType.Potion, "SomeWerewolf");
        this.werewolfAccount.put(ClanType.WerewolfBite, "ThatWerewolf");
        this.werewolfAccount.put(ClanType.WildBite, "AnotherWerewolf");
        ClanType[] values;
        for (int length = (values = ClanType.values()).length, i = 0; i < length; ++i) {
            final ClanType clan = values[i];
            this.totalClanPoints.put(clan, 0.0);
        }
        this.resetClanScores();
    }
    
    public void saveTimed() {
        if (System.currentTimeMillis() - this.lastSaveTime < 180000L) {
            return;
        }
        this.save();
    }
    
    public void save() {
        this.lastSaveTime = System.currentTimeMillis();
        if (this.clansConfig == null || this.clansConfigFile == null) {
            return;
        }
        try {
            this.clansConfig.save(this.clansConfigFile);
        }
        catch (Exception ex) {
            this.plugin.log("Could not save config to " + this.clansConfigFile + ": " + ex.getMessage());
        }
    }
    
    public String getClanName(final String playerName) {
        return this.clanNames.get(Werewolf.getWerewolfManager().getWerewolfClan(playerName));
    }
    
    public String getWerewolfAccountForClan(final ClanType type) {
        return this.werewolfAccount.get(type);
    }
    
    public void handleMobKill(final Player player, final ClanType clanType, final EntityType mobType) {
        double points = 0.0;
        if (this.playerClanPoints.containsKey(player.getName())) {
            points = this.playerClanPoints.get(player.getName());
        }
        switch ($SWITCH_TABLE$org$bukkit$entity$EntityType()[mobType.ordinal()]) {
            case 26: {
                points = 5.0;
                break;
            }
            case 29: {
                points = 5.0;
                break;
            }
            case 27: {
                points = 5.0;
                break;
            }
            case 52: {
                points = 4.0;
                break;
            }
            case 42: {
                points = 3.0;
                break;
            }
            case 43: {
                points = 2.0;
                break;
            }
            case 44: {
                points = 3.0;
                break;
            }
            case 35: {
                points = 2.0;
                break;
            }
            case 45: {
                points = 1.0;
                break;
            }
            case 40: {
                points = 1.0;
                break;
            }
        }
        this.playerClanPoints.put(player.getName(), points);
        double totalPoints = this.totalClanPoints.get(clanType);
        totalPoints += points / Werewolf.getWerewolfManager().getWerewolfClanMembers(clanType).size();
        this.totalClanPoints.put(clanType, totalPoints);
        Werewolf.getLanguageManager().setAmount(new StringBuilder().append(points).toString());
        this.plugin.sendInfo(player, ChatColor.AQUA + Werewolf.getLanguageManager().getLanguageString(LanguageManager.LANGUAGESTRING.KilledMobPoints));
    }
    
    public List<ClanType> getClansRanked() {
        final List<ClanType> clanList = new ArrayList<ClanType>();
        ClanType[] values;
        for (int length = (values = ClanType.values()).length, i = 0; i < length; ++i) {
            final ClanType clan = values[i];
            clanList.add(clan);
        }
        Collections.sort(clanList, new ClanComparator());
        return clanList;
    }
    
    public double getClanPoint(final ClanType clan) {
        return this.totalClanPoints.get(clan);
    }
    
    public String getClanName(final ClanType clan) {
        return this.clanNames.get(clan);
    }
    
    public void updateClans() {
        if (!this.plugin.useClans) {
            return;
        }
        final List<ClanType> clanList = this.getClansRanked();
        if (this.totalClanPoints.get(ClanType.Potion) > 0.0 || this.totalClanPoints.get(ClanType.WildBite) > 0.0 || this.totalClanPoints.get(ClanType.WerewolfBite) > 0.0) {
            this.plugin.getServer().broadcastMessage(ChatColor.GOLD + "------------ Werewolf Clan Summary ------------");
            this.plugin.getServer().broadcastMessage(ChatColor.GOLD + "1) " + ChatColor.WHITE + this.clanNames.get(clanList.get(0)) + ChatColor.AQUA + " - " + String.format("%1$,.2f", this.totalClanPoints.get(clanList.get(0))) + " points");
            this.plugin.getServer().broadcastMessage(ChatColor.GOLD + "2) " + ChatColor.WHITE + this.clanNames.get(clanList.get(1)) + ChatColor.AQUA + " - " + String.format("%1$,.2f", this.totalClanPoints.get(clanList.get(1))) + " points");
            this.plugin.getServer().broadcastMessage(ChatColor.GOLD + "3) " + ChatColor.WHITE + this.clanNames.get(clanList.get(2)) + ChatColor.AQUA + " - " + String.format("%1$,.2f", this.totalClanPoints.get(clanList.get(2))) + " points");
            this.plugin.getServer().broadcastMessage(new StringBuilder().append(ChatColor.GOLD).toString());
            this.plugin.getServer().broadcastMessage(ChatColor.GOLD + this.clanNames.get(clanList.get(0)) + " now has the " + ChatColor.DARK_RED + " Blood Rage " + ChatColor.DARK_RED + " power!");
            this.setBloodrageClan(clanList.get(0));
        }
    }
    
    private void setBloodrageClan(final ClanType clan) {
    }
    
    public void updateClan(final ClanType clanType) {
        final Set<String> werewolves = Werewolf.getWerewolfManager().getWerewolvesInClan(clanType);
        final List<String> werewolfList = new ArrayList<String>();
        for (final String werewolf : werewolves) {
            werewolfList.add(werewolf);
        }
        Collections.sort(werewolfList, new ClanMemberComparator());
        this.resetClanScores();
    }
    
    private void resetClanScores() {
        this.playerClanPoints.clear();
        ClanType[] values;
        for (int length = (values = ClanType.values()).length, i = 0; i < length; ++i) {
            final ClanType clan = values[i];
            final double points = this.totalClanPoints.get(clan);
            this.totalClanPoints.put(clan, points / 2.0);
        }
    }
    
    public void sendMessageToClan(final ClanType clan, final String message) {
    }
    
    public String getAlpha(final ClanType clan) {
        return this.clansConfig.getString(String.valueOf(clan.name()) + ".AlphaName");
    }
    
    public boolean isAlpha(final String playerName) {
        ClanType[] values;
        for (int length = (values = ClanType.values()).length, i = 0; i < length; ++i) {
            final ClanType clan = values[i];
            final String alphaName = this.clansConfig.getString(String.valueOf(clan.name()) + ".AlphaName");
            if (alphaName != null) {
                if (alphaName.equals(playerName)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public boolean isAlpha(final ClanType clan, final String playerName) {
        final String alphaName = this.clansConfig.getString(String.valueOf(clan.name()) + ".AlphaName");
        return alphaName != null && alphaName.equals(playerName);
    }
    
    public void assignAlphaInClan(final ClanType clan, final String playerName) {
        this.clansConfig.set(String.valueOf(clan.name()) + ".AlphaName", (Object)playerName);
        this.saveTimed();
    }
    
    public boolean hasRecentCall(final ClanType clan) {
        final DateFormat formatter = new SimpleDateFormat(this.datePattern);
        final Date thisDate = new Date();
        Date transformDate = null;
        final String transformDateString = this.clansConfig.getString(String.valueOf(clan.name()) + ".LastCallTime");
        try {
            transformDate = formatter.parse(transformDateString);
        }
        catch (Exception ex) {
            this.plugin.log(String.valueOf(clan.name()) + " has invalid LastCallTime date. Resetting.");
            transformDate = new Date();
            transformDate.setTime(0L);
        }
        final long diff = thisDate.getTime() - transformDate.getTime();
        final long diffSeconds = diff / 1000L;
        return diffSeconds < 30L;
    }
    
    public void setLastCall(final ClanType clan) {
        final Date thisDate = new Date();
        final DateFormat formatter = new SimpleDateFormat(this.datePattern);
        this.clansConfig.set(String.valueOf(clan.name()) + ".LastCallTime", (Object)formatter.format(thisDate));
        this.saveTimed();
    }
    
    static /* synthetic */ int[] $SWITCH_TABLE$org$bukkit$entity$EntityType() {
        final int[] $switch_TABLE$org$bukkit$entity$EntityType = ClanManager.$SWITCH_TABLE$org$bukkit$entity$EntityType;
        if ($switch_TABLE$org$bukkit$entity$EntityType != null) {
            return $switch_TABLE$org$bukkit$entity$EntityType;
        }
        final int[] $switch_TABLE$org$bukkit$entity$EntityType2 = new int[EntityType.values().length];
        try {
            $switch_TABLE$org$bukkit$entity$EntityType2[EntityType.ARROW.ordinal()] = 5;
        }
        catch (NoSuchFieldError noSuchFieldError) {}
        try {
            $switch_TABLE$org$bukkit$entity$EntityType2[EntityType.BAT.ordinal()] = 40;
        }
        catch (NoSuchFieldError noSuchFieldError2) {}
        try {
            $switch_TABLE$org$bukkit$entity$EntityType2[EntityType.BLAZE.ordinal()] = 36;
        }
        catch (NoSuchFieldError noSuchFieldError3) {}
        try {
            $switch_TABLE$org$bukkit$entity$EntityType2[EntityType.BOAT.ordinal()] = 18;
        }
        catch (NoSuchFieldError noSuchFieldError4) {}
        try {
            $switch_TABLE$org$bukkit$entity$EntityType2[EntityType.CAVE_SPIDER.ordinal()] = 34;
        }
        catch (NoSuchFieldError noSuchFieldError5) {}
        try {
            $switch_TABLE$org$bukkit$entity$EntityType2[EntityType.CHICKEN.ordinal()] = 45;
        }
        catch (NoSuchFieldError noSuchFieldError6) {}
        try {
            $switch_TABLE$org$bukkit$entity$EntityType2[EntityType.COMPLEX_PART.ordinal()] = 61;
        }
        catch (NoSuchFieldError noSuchFieldError7) {}
        try {
            $switch_TABLE$org$bukkit$entity$EntityType2[EntityType.COW.ordinal()] = 44;
        }
        catch (NoSuchFieldError noSuchFieldError8) {}
        try {
            $switch_TABLE$org$bukkit$entity$EntityType2[EntityType.CREEPER.ordinal()] = 25;
        }
        catch (NoSuchFieldError noSuchFieldError9) {}
        try {
            $switch_TABLE$org$bukkit$entity$EntityType2[EntityType.DROPPED_ITEM.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError10) {}
        try {
            $switch_TABLE$org$bukkit$entity$EntityType2[EntityType.EGG.ordinal()] = 56;
        }
        catch (NoSuchFieldError noSuchFieldError11) {}
        try {
            $switch_TABLE$org$bukkit$entity$EntityType2[EntityType.ENDERMAN.ordinal()] = 33;
        }
        catch (NoSuchFieldError noSuchFieldError12) {}
        try {
            $switch_TABLE$org$bukkit$entity$EntityType2[EntityType.ENDER_CRYSTAL.ordinal()] = 54;
        }
        catch (NoSuchFieldError noSuchFieldError13) {}
        try {
            $switch_TABLE$org$bukkit$entity$EntityType2[EntityType.ENDER_DRAGON.ordinal()] = 38;
        }
        catch (NoSuchFieldError noSuchFieldError14) {}
        try {
            $switch_TABLE$org$bukkit$entity$EntityType2[EntityType.ENDER_PEARL.ordinal()] = 9;
        }
        catch (NoSuchFieldError noSuchFieldError15) {}
        try {
            $switch_TABLE$org$bukkit$entity$EntityType2[EntityType.ENDER_SIGNAL.ordinal()] = 10;
        }
        catch (NoSuchFieldError noSuchFieldError16) {}
        try {
            $switch_TABLE$org$bukkit$entity$EntityType2[EntityType.EXPERIENCE_ORB.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError17) {}
        try {
            $switch_TABLE$org$bukkit$entity$EntityType2[EntityType.FALLING_BLOCK.ordinal()] = 15;
        }
        catch (NoSuchFieldError noSuchFieldError18) {}
        try {
            $switch_TABLE$org$bukkit$entity$EntityType2[EntityType.FIREBALL.ordinal()] = 7;
        }
        catch (NoSuchFieldError noSuchFieldError19) {}
        try {
            $switch_TABLE$org$bukkit$entity$EntityType2[EntityType.FIREWORK.ordinal()] = 16;
        }
        catch (NoSuchFieldError noSuchFieldError20) {}
        try {
            $switch_TABLE$org$bukkit$entity$EntityType2[EntityType.FISHING_HOOK.ordinal()] = 57;
        }
        catch (NoSuchFieldError noSuchFieldError21) {}
        try {
            $switch_TABLE$org$bukkit$entity$EntityType2[EntityType.GHAST.ordinal()] = 31;
        }
        catch (NoSuchFieldError noSuchFieldError22) {}
        try {
            $switch_TABLE$org$bukkit$entity$EntityType2[EntityType.GIANT.ordinal()] = 28;
        }
        catch (NoSuchFieldError noSuchFieldError23) {}
        try {
            $switch_TABLE$org$bukkit$entity$EntityType2[EntityType.HORSE.ordinal()] = 52;
        }
        catch (NoSuchFieldError noSuchFieldError24) {}
        try {
            $switch_TABLE$org$bukkit$entity$EntityType2[EntityType.IRON_GOLEM.ordinal()] = 51;
        }
        catch (NoSuchFieldError noSuchFieldError25) {}
        try {
            $switch_TABLE$org$bukkit$entity$EntityType2[EntityType.ITEM_FRAME.ordinal()] = 12;
        }
        catch (NoSuchFieldError noSuchFieldError26) {}
        try {
            $switch_TABLE$org$bukkit$entity$EntityType2[EntityType.LEASH_HITCH.ordinal()] = 3;
        }
        catch (NoSuchFieldError noSuchFieldError27) {}
        try {
            $switch_TABLE$org$bukkit$entity$EntityType2[EntityType.LIGHTNING.ordinal()] = 58;
        }
        catch (NoSuchFieldError noSuchFieldError28) {}
        try {
            $switch_TABLE$org$bukkit$entity$EntityType2[EntityType.MAGMA_CUBE.ordinal()] = 37;
        }
        catch (NoSuchFieldError noSuchFieldError29) {}
        try {
            $switch_TABLE$org$bukkit$entity$EntityType2[EntityType.MINECART.ordinal()] = 19;
        }
        catch (NoSuchFieldError noSuchFieldError30) {}
        try {
            $switch_TABLE$org$bukkit$entity$EntityType2[EntityType.MINECART_CHEST.ordinal()] = 20;
        }
        catch (NoSuchFieldError noSuchFieldError31) {}
        try {
            $switch_TABLE$org$bukkit$entity$EntityType2[EntityType.MINECART_COMMAND.ordinal()] = 17;
        }
        catch (NoSuchFieldError noSuchFieldError32) {}
        try {
            $switch_TABLE$org$bukkit$entity$EntityType2[EntityType.MINECART_FURNACE.ordinal()] = 21;
        }
        catch (NoSuchFieldError noSuchFieldError33) {}
        try {
            $switch_TABLE$org$bukkit$entity$EntityType2[EntityType.MINECART_HOPPER.ordinal()] = 23;
        }
        catch (NoSuchFieldError noSuchFieldError34) {}
        try {
            $switch_TABLE$org$bukkit$entity$EntityType2[EntityType.MINECART_MOB_SPAWNER.ordinal()] = 24;
        }
        catch (NoSuchFieldError noSuchFieldError35) {}
        try {
            $switch_TABLE$org$bukkit$entity$EntityType2[EntityType.MINECART_TNT.ordinal()] = 22;
        }
        catch (NoSuchFieldError noSuchFieldError36) {}
        try {
            $switch_TABLE$org$bukkit$entity$EntityType2[EntityType.MUSHROOM_COW.ordinal()] = 48;
        }
        catch (NoSuchFieldError noSuchFieldError37) {}
        try {
            $switch_TABLE$org$bukkit$entity$EntityType2[EntityType.OCELOT.ordinal()] = 50;
        }
        catch (NoSuchFieldError noSuchFieldError38) {}
        try {
            $switch_TABLE$org$bukkit$entity$EntityType2[EntityType.PAINTING.ordinal()] = 4;
        }
        catch (NoSuchFieldError noSuchFieldError39) {}
        try {
            $switch_TABLE$org$bukkit$entity$EntityType2[EntityType.PIG.ordinal()] = 42;
        }
        catch (NoSuchFieldError noSuchFieldError40) {}
        try {
            $switch_TABLE$org$bukkit$entity$EntityType2[EntityType.PIG_ZOMBIE.ordinal()] = 32;
        }
        catch (NoSuchFieldError noSuchFieldError41) {}
        try {
            $switch_TABLE$org$bukkit$entity$EntityType2[EntityType.PLAYER.ordinal()] = 60;
        }
        catch (NoSuchFieldError noSuchFieldError42) {}
        try {
            $switch_TABLE$org$bukkit$entity$EntityType2[EntityType.PRIMED_TNT.ordinal()] = 14;
        }
        catch (NoSuchFieldError noSuchFieldError43) {}
        try {
            $switch_TABLE$org$bukkit$entity$EntityType2[EntityType.SHEEP.ordinal()] = 43;
        }
        catch (NoSuchFieldError noSuchFieldError44) {}
        try {
            $switch_TABLE$org$bukkit$entity$EntityType2[EntityType.SILVERFISH.ordinal()] = 35;
        }
        catch (NoSuchFieldError noSuchFieldError45) {}
        try {
            $switch_TABLE$org$bukkit$entity$EntityType2[EntityType.SKELETON.ordinal()] = 26;
        }
        catch (NoSuchFieldError noSuchFieldError46) {}
        try {
            $switch_TABLE$org$bukkit$entity$EntityType2[EntityType.SLIME.ordinal()] = 30;
        }
        catch (NoSuchFieldError noSuchFieldError47) {}
        try {
            $switch_TABLE$org$bukkit$entity$EntityType2[EntityType.SMALL_FIREBALL.ordinal()] = 8;
        }
        catch (NoSuchFieldError noSuchFieldError48) {}
        try {
            $switch_TABLE$org$bukkit$entity$EntityType2[EntityType.SNOWBALL.ordinal()] = 6;
        }
        catch (NoSuchFieldError noSuchFieldError49) {}
        try {
            $switch_TABLE$org$bukkit$entity$EntityType2[EntityType.SNOWMAN.ordinal()] = 49;
        }
        catch (NoSuchFieldError noSuchFieldError50) {}
        try {
            $switch_TABLE$org$bukkit$entity$EntityType2[EntityType.SPIDER.ordinal()] = 27;
        }
        catch (NoSuchFieldError noSuchFieldError51) {}
        try {
            $switch_TABLE$org$bukkit$entity$EntityType2[EntityType.SPLASH_POTION.ordinal()] = 55;
        }
        catch (NoSuchFieldError noSuchFieldError52) {}
        try {
            $switch_TABLE$org$bukkit$entity$EntityType2[EntityType.SQUID.ordinal()] = 46;
        }
        catch (NoSuchFieldError noSuchFieldError53) {}
        try {
            $switch_TABLE$org$bukkit$entity$EntityType2[EntityType.THROWN_EXP_BOTTLE.ordinal()] = 11;
        }
        catch (NoSuchFieldError noSuchFieldError54) {}
        try {
            $switch_TABLE$org$bukkit$entity$EntityType2[EntityType.UNKNOWN.ordinal()] = 62;
        }
        catch (NoSuchFieldError noSuchFieldError55) {}
        try {
            $switch_TABLE$org$bukkit$entity$EntityType2[EntityType.VILLAGER.ordinal()] = 53;
        }
        catch (NoSuchFieldError noSuchFieldError56) {}
        try {
            $switch_TABLE$org$bukkit$entity$EntityType2[EntityType.WEATHER.ordinal()] = 59;
        }
        catch (NoSuchFieldError noSuchFieldError57) {}
        try {
            $switch_TABLE$org$bukkit$entity$EntityType2[EntityType.WITCH.ordinal()] = 41;
        }
        catch (NoSuchFieldError noSuchFieldError58) {}
        try {
            $switch_TABLE$org$bukkit$entity$EntityType2[EntityType.WITHER.ordinal()] = 39;
        }
        catch (NoSuchFieldError noSuchFieldError59) {}
        try {
            $switch_TABLE$org$bukkit$entity$EntityType2[EntityType.WITHER_SKULL.ordinal()] = 13;
        }
        catch (NoSuchFieldError noSuchFieldError60) {}
        try {
            $switch_TABLE$org$bukkit$entity$EntityType2[EntityType.WOLF.ordinal()] = 47;
        }
        catch (NoSuchFieldError noSuchFieldError61) {}
        try {
            $switch_TABLE$org$bukkit$entity$EntityType2[EntityType.ZOMBIE.ordinal()] = 29;
        }
        catch (NoSuchFieldError noSuchFieldError62) {}
        return ClanManager.$SWITCH_TABLE$org$bukkit$entity$EntityType = $switch_TABLE$org$bukkit$entity$EntityType2;
    }
    
    public class ClanComparator implements Comparator<ClanType>
    {
        @Override
        public int compare(final ClanType clan1, final ClanType clan2) {
            return (int)(ClanManager.this.totalClanPoints.get(clan2) - ClanManager.this.totalClanPoints.get(clan1));
        }
    }
    
    public class ClanMemberComparator implements Comparator<String>
    {
        @Override
        public int compare(final String member1, final String member2) {
            return (int)(ClanManager.this.playerClanPoints.get(member2) - ClanManager.this.playerClanPoints.get(member1));
        }
    }
    
    public enum ClanType
    {
        WildBite("WildBite", 0), 
        WerewolfBite("WerewolfBite", 1), 
        Potion("Potion", 2);
    }
}

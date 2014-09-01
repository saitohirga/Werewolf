package com.dogonfire.werewolf;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import java.util.Iterator;
import java.util.logging.Logger;
import org.bukkit.util.Vector;
import org.bukkit.Effect;
import com.massivecraft.vampire.entity.UPlayer;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.PluginManager;
import org.bukkit.event.Listener;
import org.bukkit.World;
import com.dogonfire.werewolf.tasks.UndisguiseTask;
import org.bukkit.plugin.Plugin;
import com.dogonfire.werewolf.tasks.DisguiseTask;
import java.util.Collection;
import java.util.HashSet;
import java.util.Arrays;
import com.dogonfire.werewolf.listeners.ChatListener;
import com.dogonfire.werewolf.listeners.InteractListener;
import com.dogonfire.werewolf.listeners.PlayerListener;
import com.dogonfire.werewolf.listeners.InventoryListener;
import com.dogonfire.werewolf.listeners.DamageListener;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.configuration.file.FileConfiguration;
import java.util.List;
import java.util.Set;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import java.util.concurrent.ConcurrentHashMap;
import java.util.ArrayList;
import org.bukkit.plugin.java.JavaPlugin;

public class Werewolf extends JavaPlugin
{
    public static boolean pluginEnabled;
    public boolean vampireEnabled;
    public boolean vaultEnabled;
    public boolean noCheatPlusEnabled;
    public boolean antiCheatEnabled;
    public boolean healthBarEnabled;
    public static int nightStart;
    public static int nightEnd;
    public int wolfdistance;
    public double cureChance;
    public double wildWolfInfectionRisk;
    public double werewolfInfectionRisk;
    public double potionInfectChance;
    public int compassUpdateRate;
    public ArrayList<String> wolfMessage;
    public boolean movementUpdateThreading;
    public int movementUpdateFrequency;
    public ConcurrentHashMap<Player, Integer> positionUpdaters;
    public static Server server;
    public boolean debug;
    public String language;
    private final Set<String> supportedLanguages;
    public boolean renameWerewolves;
    public boolean autoBounty;
    public int autoBountyMaximum;
    public boolean werewolfUrges;
    public boolean announceFullmoons;
    public boolean wolfChat;
    public boolean useWerewolfGroupName;
    public boolean useTrophies;
    public boolean dropArmorOnTransform;
    public boolean onlyTransformDuringFullMoon;
    public boolean keepWerewolfHandsFree;
    public boolean cureWerewolfWhenSlain;
    public String werewolfGroupName;
    public int autoCureDays;
    public boolean useSigns;
    public double infectionPrice;
    public double curePrice;
    public boolean useClans;
    public boolean useUpdateNotifications;
    public int transformsForNoDropItems;
    public int transformsForControlledTransformation;
    public int transformsForGoldImmunity;
    public int transformationTimeoutSeconds;
    public boolean usePounce;
    public float pouncePlaneSpeed;
    public float pounceHeightSpeed;
    public List<String> allowedWorlds;
    private static Werewolf plugin;
    private static FileConfiguration config;
    public static PacketUtils pu;
    private static LanguageManager languageManager;
    private static PotionManager potionManager;
    private static ClanManager clanManager;
    private static SignManager signManager;
    private static WerewolfManager werewolfManager;
    private static HuntManager huntManager;
    private static TrophyManager trophyManager;
    private static SkinManager skinManager;
    private static PermissionsManager permissionsManager;
    private static Economy economy;
    private Commands commands;
    private DamageListener damageListener;
    private InventoryListener inventoryListener;
    private PlayerListener playerListener;
    private InteractListener interactListener;
    private ChatListener chatListener;
    public String serverName;
    
    static {
        Werewolf.pluginEnabled = false;
        Werewolf.nightStart = 13300;
        Werewolf.nightEnd = 23500;
        Werewolf.server = null;
        Werewolf.config = null;
        Werewolf.pu = null;
        Werewolf.languageManager = null;
        Werewolf.potionManager = null;
        Werewolf.clanManager = null;
        Werewolf.signManager = null;
        Werewolf.werewolfManager = null;
        Werewolf.huntManager = null;
        Werewolf.trophyManager = null;
        Werewolf.skinManager = null;
        Werewolf.permissionsManager = null;
        Werewolf.economy = null;
    }
    
    public Werewolf() {
        super();
        this.vampireEnabled = false;
        this.vaultEnabled = false;
        this.noCheatPlusEnabled = false;
        this.antiCheatEnabled = false;
        this.healthBarEnabled = false;
        this.wolfdistance = 10;
        this.cureChance = 1.0;
        this.wildWolfInfectionRisk = 1.0;
        this.werewolfInfectionRisk = 1.0;
        this.potionInfectChance = 1.0;
        this.compassUpdateRate = 100;
        this.wolfMessage = new ArrayList<String>();
        this.movementUpdateThreading = true;
        this.movementUpdateFrequency = 4;
        this.positionUpdaters = new ConcurrentHashMap<Player, Integer>();
        this.debug = false;
        this.language = "english";
        this.supportedLanguages = new HashSet<String>(Arrays.asList("english", "german", "french", "chinese", "polish", "danish", "portuguese", "spanish"));
        this.renameWerewolves = true;
        this.autoBounty = true;
        this.autoBountyMaximum = 1000;
        this.werewolfUrges = true;
        this.announceFullmoons = true;
        this.wolfChat = true;
        this.useWerewolfGroupName = false;
        this.useTrophies = true;
        this.dropArmorOnTransform = true;
        this.onlyTransformDuringFullMoon = true;
        this.keepWerewolfHandsFree = true;
        this.cureWerewolfWhenSlain = true;
        this.werewolfGroupName = "Werewolf";
        this.autoCureDays = 14;
        this.useSigns = false;
        this.infectionPrice = 1000.0;
        this.curePrice = 500.0;
        this.useClans = false;
        this.useUpdateNotifications = true;
        this.transformsForNoDropItems = 2;
        this.transformsForControlledTransformation = 6;
        this.transformsForGoldImmunity = 8;
        this.transformationTimeoutSeconds = 1800;
        this.usePounce = false;
        this.pouncePlaneSpeed = 1.0f;
        this.pounceHeightSpeed = 1.0f;
        this.allowedWorlds = new ArrayList<String>();
        this.commands = null;
        this.damageListener = null;
        this.inventoryListener = null;
        this.playerListener = null;
        this.interactListener = null;
        this.chatListener = null;
        this.serverName = "Your Server";
    }
    
    public static SignManager getSignManager() {
        return Werewolf.signManager;
    }
    
    public static LanguageManager getLanguageManager() {
        return Werewolf.languageManager;
    }
    
    public static PotionManager getPotionManager() {
        return Werewolf.potionManager;
    }
    
    public static PermissionsManager getPermissionsManager() {
        return Werewolf.permissionsManager;
    }
    
    public static ClanManager getClanManager() {
        return Werewolf.clanManager;
    }
    
    public static WerewolfManager getWerewolfManager() {
        return Werewolf.werewolfManager;
    }
    
    public static HuntManager getHuntManager() {
        return Werewolf.huntManager;
    }
    
    public static TrophyManager getTrophyManager() {
        return Werewolf.trophyManager;
    }
    
    public static SkinManager getSkinManager() {
        return Werewolf.skinManager;
    }
    
    public static Economy getEconomy() {
        return Werewolf.economy;
    }
    
    public void disguiseWerewolf(final Player p) {
        Werewolf.server.getScheduler().scheduleSyncDelayedTask((Plugin)Werewolf.plugin, (Runnable)new DisguiseTask(Werewolf.plugin, p), 8L);
    }
    
    public void undisguiseWerewolf(final String playerName, final boolean makeVisible, final boolean forever) {
        final Player player = this.getServer().getPlayer(playerName);
        if (player != null) {
            Werewolf.server.getScheduler().scheduleSyncDelayedTask((Plugin)Werewolf.plugin, (Runnable)new UndisguiseTask(Werewolf.plugin, player.getWorld(), player.getName(), player.getEntityId(), makeVisible, forever), 8L);
        }
        else {
            Werewolf.server.getScheduler().scheduleSyncDelayedTask((Plugin)Werewolf.plugin, (Runnable)new UndisguiseTask(Werewolf.plugin, null, playerName, 0, makeVisible, forever), 8L);
        }
    }
    
    public void sendInfo(final Player player, final String message) {
        if (player == null) {
            this.log(message);
        }
        else {
            player.sendMessage(message);
        }
    }
    
    public void OnDisable() {
        CompassTracker.stop();
        this.reloadSettings();
        Werewolf.pluginEnabled = false;
    }
    
    public void onEnable() {
        Werewolf.pluginEnabled = true;
        this.commands = new Commands(this);
        Werewolf.permissionsManager = new PermissionsManager(this);
        Werewolf.werewolfManager = new WerewolfManager(this);
        Werewolf.clanManager = new ClanManager(this);
        Werewolf.skinManager = new SkinManager(this);
        Werewolf.potionManager = new PotionManager(this);
        Werewolf.languageManager = new LanguageManager(this);
        this.damageListener = new DamageListener(this);
        this.playerListener = new PlayerListener(this);
        this.interactListener = new InteractListener(this);
        this.chatListener = new ChatListener(this);
        this.inventoryListener = new InventoryListener(this);
        Werewolf.pu = new PacketUtils(this);
        Werewolf.plugin = this;
        Werewolf.server = this.getServer();
        Werewolf.config = this.getConfig();
        final PluginManager pm = this.getServer().getPluginManager();
        if (pm.isPluginEnabled("NoCheatPlus")) {
            Werewolf.plugin.log("NoCheatPlus detected. Overriding cheat checking for Werewolves.");
            this.noCheatPlusEnabled = true;
        }
        if (pm.getPlugin("AntiCheat") != null) {
            Werewolf.plugin.log("AntiCheat detected. Overriding cheat checking for Werewolves.");
            this.antiCheatEnabled = true;
        }
        if (pm.getPlugin("Vault") != null) {
            this.vaultEnabled = true;
            Werewolf.huntManager = new HuntManager(this);
            this.log("Vault detected. Bounties and sign economy are enabled!");
            CompassTracker.setPlugin(this);
            CompassTracker.setUpdateRate(this.compassUpdateRate);
            final RegisteredServiceProvider<Economy> economyProvider = (RegisteredServiceProvider<Economy>)Werewolf.plugin.getServer().getServicesManager().getRegistration((Class)Economy.class);
            if (economyProvider != null) {
                Werewolf.economy = (Economy)economyProvider.getProvider();
            }
            else {
                Werewolf.plugin.log("Vault not found.");
            }
        }
        else {
            this.log("Vault not found. Werewolf bounties and signs are disabled.");
        }
        if (pm.getPlugin("vampire") != null) {
            this.log("Vampire plugin detected. Enabling support for vampirism :-)");
            this.vampireEnabled = true;
        }
        if (pm.getPlugin("HealthBar") != null) {
            this.log("HealthBar plugin detected. Enabling support for healthbars.");
            this.healthBarEnabled = true;
        }
        this.getServer().getPluginManager().registerEvents((Listener)this.playerListener, (Plugin)this);
        this.getServer().getPluginManager().registerEvents((Listener)this.interactListener, (Plugin)this);
        this.getServer().getPluginManager().registerEvents((Listener)this.damageListener, (Plugin)this);
        this.getServer().getPluginManager().registerEvents((Listener)this.inventoryListener, (Plugin)this);
        this.getServer().getPluginManager().registerEvents((Listener)this.chatListener, (Plugin)this);
        this.loadSettings();
        this.saveSettings();
        Werewolf.permissionsManager.load();
        Werewolf.werewolfManager.load();
        Werewolf.clanManager.load();
        Werewolf.languageManager.load();
        if (this.vaultEnabled) {
            Werewolf.huntManager.load();
        }
        this.getServer().getScheduler().scheduleSyncRepeatingTask((Plugin)this, (Runnable)new Runnable() {
            @Override
            public void run() {
                Werewolf.getWerewolfManager().update();
            }
        }, 20L, 100L);
        if (this.useClans) {
            this.getServer().getScheduler().scheduleSyncRepeatingTask((Plugin)this, (Runnable)new Runnable() {
                @Override
                public void run() {
                    Werewolf.getClanManager().updateClans();
                }
            }, 20L, 72000L);
        }
        this.startMetrics();
    }
    
    public boolean isWerewolvesAllowedInWorld(final Player player) {
        return this.allowedWorlds.contains(player.getWorld().getName());
    }
    
    public boolean isVampire(final Player player) {
        if (this.vampireEnabled) {
            final UPlayer uplayer = UPlayer.get((Object)player);
            return uplayer != null && uplayer.isVampire();
        }
        return false;
    }
    
    public boolean isUnderOpenSky(final Player player) {
        return player.getWorld().getHighestBlockYAt(player.getLocation()) <= player.getLocation().getBlockY();
    }
    
    public boolean isFullMoonDuskInWorld(final World world) {
        final long time = world.getFullTime() % 24000L;
        return time > 12000L && time < 14000L && this.MoonCheck(world) == MoonPhase.FullMoon;
    }
    
    public boolean isNightInWorld(final World world) {
        final long time = world.getFullTime() % 24000L;
        return time > this.getTimeStart() && time < this.getTimeEnd();
    }
    
    public boolean isFullMoonInWorld(final World world) {
        if (Werewolf.plugin.onlyTransformDuringFullMoon) {
            return this.isNightInWorld(world) && this.MoonCheck(world) == MoonPhase.FullMoon;
        }
        return this.isNightInWorld(world);
    }
    
    public static MoonPhase getMoonPhaseByInt(final int I) {
        return MoonPhase.values()[I];
    }
    
    public MoonPhase MoonCheck(final World world) {
        final long T = world.getFullTime();
        final long D = T / 24000L;
        final int days = (int)D;
        final int phaseInt = days % 8;
        return getMoonPhaseByInt(phaseInt);
    }
    
    public String getNextFullMoonText(final World world) {
        final long T = world.getFullTime();
        final long D = T / 24000L;
        final int days = (int)D;
        final int phaseInt = days % 8;
        switch (phaseInt) {
            case 0: {
                return getLanguageManager().getLanguageString(LanguageManager.LANGUAGESTRING.Today);
            }
            case 1: {
                return getLanguageManager().getLanguageString(LanguageManager.LANGUAGESTRING.In7Days);
            }
            case 2: {
                return getLanguageManager().getLanguageString(LanguageManager.LANGUAGESTRING.In6Days);
            }
            case 3: {
                return getLanguageManager().getLanguageString(LanguageManager.LANGUAGESTRING.In5Days);
            }
            case 4: {
                return getLanguageManager().getLanguageString(LanguageManager.LANGUAGESTRING.In4Days);
            }
            case 5: {
                return getLanguageManager().getLanguageString(LanguageManager.LANGUAGESTRING.In3Days);
            }
            case 6: {
                return getLanguageManager().getLanguageString(LanguageManager.LANGUAGESTRING.In3Days);
            }
            case 7: {
                return getLanguageManager().getLanguageString(LanguageManager.LANGUAGESTRING.Tomorrow);
            }
            default: {
                return "WTF?";
            }
        }
    }
    
    public boolean hasTransformation() {
        return true;
    }
    
    public int getTimeStart() {
        return Werewolf.nightStart;
    }
    
    public int getTimeEnd() {
        return Werewolf.nightEnd;
    }
    
    public void transform(final Player player) {
        if (!this.isWerewolvesAllowedInWorld(player)) {
            return;
        }
        if (!getWerewolfManager().hasWerewolfSkin(player.getName())) {
            player.getLocation().getWorld().playEffect(player.getLocation(), Effect.SMOKE, 100);
            player.getLocation().getWorld().playEffect(player.getLocation().add(new Vector(0, 1, 0)), Effect.SMOKE, 100);
            player.getLocation().getWorld().playEffect(player.getLocation().add(new Vector(0, 2, 0)), Effect.SMOKE, 100);
            Werewolf.werewolfManager.setWerewolfSkin(player);
        }
        else {
            Werewolf.plugin.log("Could not transform " + player.getName() + ": Not a werewolf!");
        }
    }
    
    public void untransform(final Player player) {
        player.getLocation().getWorld().playEffect(player.getLocation(), Effect.SMOKE, 100);
        player.getLocation().getWorld().playEffect(player.getLocation().add(new Vector(0, 1, 0)), Effect.SMOKE, 100);
        player.getLocation().getWorld().playEffect(player.getLocation().add(new Vector(0, 2, 0)), Effect.SMOKE, 100);
        Werewolf.werewolfManager.unsetWerewolfSkin(player.getName(), true);
    }
    
    public void setPositionUpdater(final Player player, final WerewolfSkin skin) {
        if (this.movementUpdateThreading) {
            this.positionUpdaters.put(player, this.getServer().getScheduler().scheduleSyncRepeatingTask((Plugin)this, (Runnable)new PlayerPositionUpdater(this, player, skin), 1L, (long)this.movementUpdateFrequency));
        }
    }
    
    public void log(final String message) {
        Logger.getLogger("minecraft").info("[" + this.getDescription().getFullName() + "] " + message);
    }
    
    public void logDebug(final String message) {
        if (this.debug) {
            Logger.getLogger("minecraft").info("[" + this.getDescription().getFullName() + "] " + message);
        }
    }
    
    public void reloadSettings() {
        this.reloadConfig();
        this.loadSettings();
    }
    
    public void loadSettings() {
        this.debug = Werewolf.config.getBoolean("Settings.Debug", false);
        DamageListener.ITEM_DAMAGE = Werewolf.config.getInt("Modifiers.Wolf.ItemDamage", 3);
        DamageListener.HAND_DAMAGE = Werewolf.config.getInt("Modifiers.Wolf.HandDamage", 8);
        DamageListener.SILVER_MULTIPLIER = Werewolf.config.getDouble("Modifiers.Wolf.SilverMultiplier", 2.0);
        DamageListener.armorMultiplier = Werewolf.config.getDouble("Modifiers.Wolf.ArmorMultiplier", 0.8);
        this.wolfdistance = Werewolf.config.getInt("Modifiers.WolfDistance", 10);
        this.cureChance = Werewolf.config.getDouble("Infection.CureChance", 1.0);
        this.werewolfInfectionRisk = Werewolf.config.getDouble("Infection.WerewolfBiteRisk", 0.05);
        this.wildWolfInfectionRisk = Werewolf.config.getDouble("Infection.WildWolfBiteRisk", 0.75);
        this.autoCureDays = Werewolf.config.getInt("Infection.AutoCureDays", 14);
        this.allowedWorlds = (List<String>)Werewolf.config.getStringList("AllowedWorlds");
        if (this.allowedWorlds.size() == 0) {
            this.log("Allowed worlds is empty. Adding world " + this.getServer().getWorlds().get(0).getName() + " as werewolf world.");
            this.allowedWorlds.add(this.getServer().getWorlds().get(0).getName());
        }
        else {
            for (final String worldName : this.allowedWorlds) {
                this.log("Werewolves are allowed in worlds '" + worldName + "'");
            }
        }
        DamageListener.WEREWOLF_GROWL = Werewolf.config.getString("Files.Growl", "");
        Werewolf.nightStart = Werewolf.config.getInt("Night.Start", 13000);
        Werewolf.nightEnd = Werewolf.config.getInt("Night.End", 23000);
        this.wolfMessage.add("*Grunt*");
        this.wolfMessage.add("*Grunt* *Grrrr*");
        this.wolfMessage.add("*Grunt* *Grunt*");
        this.wolfMessage.add("*Growl*");
        this.wolfMessage.add("*Grrroowl Grunt*");
        this.wolfMessage.add("*Grrrrr*");
        this.wolfMessage.add("*Rrrrrr*");
        this.wolfMessage.add("*Groooowl*");
        this.wolfMessage.add("*Grrrrr* *Grrr*");
        this.wolfMessage.add("*Hoooowl*");
        this.wolfMessage.add("*Rrraagh*");
        this.wolfMessage.add("*Grrawl*");
        this.wolfMessage.add("*Grrrrawl* *Growls*");
        this.wolfMessage.add("*HOOOOOWLLLL!*");
        this.wolfMessage.add("*Wimper*");
        this.wolfMessage.add("*Awooooo*");
        this.werewolfGroupName = Werewolf.config.getString("WerewolfGroup.Name", "Werewolf");
        this.useWerewolfGroupName = Werewolf.config.getBoolean("WerewolfGroup.Enabled", false);
        this.useTrophies = Werewolf.config.getBoolean("Trophies.Enabled", false);
        this.autoBounty = Werewolf.config.getBoolean("Settings.AutoBounty", false);
        this.autoBountyMaximum = Werewolf.config.getInt("Settings.AutoBountyMaximum", 1000);
        this.renameWerewolves = Werewolf.config.getBoolean("Settings.RenameWerewolves", true);
        this.useUpdateNotifications = Werewolf.config.getBoolean("Settings.DisplayUpdateNotifications", true);
        this.werewolfUrges = Werewolf.config.getBoolean("Settings.WerewolfUrges", true);
        this.wolfChat = Werewolf.config.getBoolean("Settings.WolfChat", true);
        this.dropArmorOnTransform = Werewolf.config.getBoolean("Settings.DropArmorOnTransform", true);
        this.onlyTransformDuringFullMoon = Werewolf.config.getBoolean("Settings.OnlyTransformDuringFullMoon", true);
        this.serverName = Werewolf.config.getString("Settings.ServerName", "Your Server");
        this.cureWerewolfWhenSlain = Werewolf.config.getBoolean("Settings.CureWerewolfWhenSlain", false);
        this.useSigns = Werewolf.config.getBoolean("Signs.Enabled", false);
        if (this.useSigns) {
            Werewolf.signManager = new SignManager(this);
            this.getServer().getPluginManager().registerEvents((Listener)Werewolf.signManager, (Plugin)this);
        }
        this.curePrice = Werewolf.config.getDouble("Signs.CurePrice", 500.0);
        this.infectionPrice = Werewolf.config.getDouble("Signs.InfectionPrice", 1000.0);
        this.language = Werewolf.config.getString("Settings.Language", "english");
        if (!this.supportedLanguages.contains(this.language)) {
            this.log("Language '" + this.language + "' is not supported. Reverting to english.");
            this.language = "english";
        }
        this.transformsForNoDropItems = Werewolf.config.getInt("Maturity.NoDropItems", 3);
        this.transformsForControlledTransformation = Werewolf.config.getInt("Maturity.ControlledTransformation", 7);
        this.transformsForGoldImmunity = Werewolf.config.getInt("Maturity.GoldImmunity", 10);
        this.usePounce = Werewolf.config.getBoolean("Pounce.Enabled", false);
        this.pouncePlaneSpeed = (float)Werewolf.config.getDouble("Pounce.PlaneSpeed", 2.25);
        this.pounceHeightSpeed = (float)Werewolf.config.getDouble("Pounce.HeightSpeed", 1.1);
        if (this.useTrophies && Werewolf.trophyManager == null) {
            (Werewolf.trophyManager = new TrophyManager(this)).load();
        }
        this.useClans = Werewolf.config.getBoolean("Clans.Enabled", false);
    }
    
    public void saveSettings() {
        Werewolf.config.set("Settings.ServerName", (Object)this.serverName);
        Werewolf.config.set("Settings.DisplayUpdateNotifications", (Object)this.useUpdateNotifications);
        Werewolf.config.set("Settings.AutoBounty", (Object)this.autoBounty);
        Werewolf.config.set("Settings.AutoBountyMaximum", (Object)this.autoBountyMaximum);
        Werewolf.config.set("Settings.RenameWerewolves", (Object)this.renameWerewolves);
        Werewolf.config.set("Settings.WerewolfUrges", (Object)this.werewolfUrges);
        Werewolf.config.set("Settings.Language", (Object)this.language);
        Werewolf.config.set("Settings.WolfChat", (Object)this.wolfChat);
        Werewolf.config.set("Settings.DropArmorOnTransform", (Object)this.dropArmorOnTransform);
        Werewolf.config.set("Settings.OnlyTransformDuringFullMoon", (Object)this.onlyTransformDuringFullMoon);
        Werewolf.config.set("Settings.CureWerewolfWhenSlain", (Object)this.cureWerewolfWhenSlain);
        Werewolf.config.set("Maturity.NoDropItems", (Object)this.transformsForNoDropItems);
        Werewolf.config.set("Maturity.ControlledTransformation", (Object)this.transformsForControlledTransformation);
        Werewolf.config.set("Maturity.GoldImmunity", (Object)this.transformsForGoldImmunity);
        Werewolf.config.set("Settings.Debug", (Object)this.debug);
        Werewolf.config.set("Infection.CureChance", (Object)this.cureChance);
        Werewolf.config.set("Infection.WerewolfBiteRisk", (Object)this.werewolfInfectionRisk);
        Werewolf.config.set("Infection.WildWolfBiteRisk", (Object)this.wildWolfInfectionRisk);
        Werewolf.config.set("Infection.AutoCureDays", (Object)this.autoCureDays);
        Werewolf.config.set("Modifiers.Wolf.ItemDamage", (Object)DamageListener.ITEM_DAMAGE);
        Werewolf.config.set("Modifiers.Wolf.HandDamage", (Object)DamageListener.HAND_DAMAGE);
        Werewolf.config.set("Modifiers.Wolf.SilverMultiplier", (Object)DamageListener.SILVER_MULTIPLIER);
        Werewolf.config.set("Modifiers.Wolf.ArmorMultiplier", (Object)DamageListener.armorMultiplier);
        Werewolf.config.set("Modifiers.Wolf.WolfDistance", (Object)this.wolfdistance);
        Werewolf.config.set("WerewolfGroup.Enabled", (Object)this.useWerewolfGroupName);
        Werewolf.config.set("WerewolfGroup.Name", (Object)this.werewolfGroupName);
        Werewolf.config.set("Pounce.Enabled", (Object)this.usePounce);
        Werewolf.config.set("Pounce.PlaneSpeed", (Object)this.pouncePlaneSpeed);
        Werewolf.config.set("Pounce.HeightSpeed", (Object)this.pounceHeightSpeed);
        Werewolf.config.set("AllowedWorlds", (Object)this.allowedWorlds);
        Werewolf.config.set("Night.Start", (Object)Werewolf.nightStart);
        Werewolf.config.set("Night.End", (Object)Werewolf.nightEnd);
        Werewolf.config.set("Clans.Enabled", (Object)this.useClans);
        Werewolf.config.set("Signs.Enabled", (Object)this.useSigns);
        Werewolf.config.set("Signs.CurePrice", (Object)this.curePrice);
        Werewolf.config.set("Signs.InfectionPrice", (Object)this.infectionPrice);
        Werewolf.config.set("Trophies.Enabled", (Object)this.useTrophies);
        this.saveConfig();
    }
    
    public boolean onCommand(final CommandSender sender, final Command cmd, final String label, final String[] args) {
        return this.commands.onCommand(sender, cmd, label, args);
    }
    
    public void startMetrics() {
        try {
            final Metrics metrics = new Metrics((Plugin)this);
            metrics.addCustomData(new Metrics.Plotter("Servers") {
                @Override
                public int getValue() {
                    return 1;
                }
            });
            metrics.addCustomData(new Metrics.Plotter("Using Vault") {
                @Override
                public int getValue() {
                    if (Werewolf.this.vaultEnabled) {
                        return 1;
                    }
                    return 0;
                }
            });
            metrics.addCustomData(new Metrics.Plotter("Using Vampire") {
                @Override
                public int getValue() {
                    if (Werewolf.this.vampireEnabled) {
                        return 1;
                    }
                    return 0;
                }
            });
            metrics.addCustomData(new Metrics.Plotter("Using AntiCheat") {
                @Override
                public int getValue() {
                    if (Werewolf.this.antiCheatEnabled) {
                        return 1;
                    }
                    return 0;
                }
            });
            metrics.addCustomData(new Metrics.Plotter("Using NoCheatPlus") {
                @Override
                public int getValue() {
                    if (Werewolf.this.noCheatPlusEnabled) {
                        return 1;
                    }
                    return 0;
                }
            });
            metrics.addCustomData(new Metrics.Plotter("WolfChat") {
                @Override
                public int getValue() {
                    if (Werewolf.this.wolfChat) {
                        return 1;
                    }
                    return 0;
                }
            });
            metrics.addCustomData(new Metrics.Plotter(this.language) {
                @Override
                public int getValue() {
                    return 1;
                }
            });
            metrics.addCustomData(new Metrics.Plotter("Rename Werewolves") {
                @Override
                public int getValue() {
                    if (Werewolf.this.renameWerewolves) {
                        return 1;
                    }
                    return 0;
                }
            });
            metrics.start();
        }
        catch (Exception e) {
            this.log("Failed to submit metrics :-(");
        }
    }
    
    public enum MoonPhase
    {
        FullMoon("FullMoon", 0), 
        WaningGibbous("WaningGibbous", 1), 
        LastQuarter("LastQuarter", 2), 
        WaningCrescent("WaningCrescent", 3), 
        NewMoon("NewMoon", 4), 
        WaxingCrescent("WaxingCrescent", 5), 
        FirstQuarter("FirstQuarter", 6), 
        WaxingGibbous("WaxingGibbous", 7);
    }
}

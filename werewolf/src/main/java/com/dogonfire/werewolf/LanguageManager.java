package com.dogonfire.werewolf;

import org.bukkit.entity.EntityType;
import org.bukkit.Material;
import org.bukkit.ChatColor;
import java.util.List;
import com.google.common.io.Files;
import java.nio.charset.Charset;
import org.bukkit.configuration.file.YamlConfiguration;
import java.io.File;
import java.io.IOException;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.BufferedInputStream;
import java.net.URL;
import java.io.OutputStream;
import java.io.InputStream;
import java.util.Enumeration;
import java.net.Socket;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.NetworkInterface;
import java.util.Random;
import org.bukkit.configuration.file.FileConfiguration;

public class LanguageManager
{
    private Werewolf plugin;
    private FileConfiguration languageConfig;
    private Random random;
    private String amount;
    private String playerName;
    private String type;
    private String authorName;
    private String languageName;
    
    private void detectCountry() {
        String localAddress = "";
        Enumeration<NetworkInterface> interfaces = null;
        try {
            interfaces = NetworkInterface.getNetworkInterfaces();
        }
        catch (SocketException e) {
            e.printStackTrace();
        }
        while (interfaces.hasMoreElements()) {
            final NetworkInterface current = interfaces.nextElement();
            System.out.println(current);
            try {
                if (!current.isUp() || current.isLoopback()) {
                    continue;
                }
                if (current.isVirtual()) {
                    continue;
                }
            }
            catch (SocketException e2) {
                e2.printStackTrace();
            }
            final Enumeration<InetAddress> addresses = current.getInetAddresses();
            while (addresses.hasMoreElements()) {
                final InetAddress current_addr = addresses.nextElement();
                if (current_addr.isLoopbackAddress()) {
                    continue;
                }
                if (!(current_addr instanceof Inet4Address)) {
                    continue;
                }
                localAddress = current_addr.getHostAddress();
                System.out.println(current_addr.getHostAddress());
            }
        }
        try {
            final Socket s = new Socket("internic.net", 43);
            final InputStream in = s.getInputStream();
            final OutputStream out = s.getOutputStream();
            final String str = String.valueOf(localAddress) + "\\n";
            final byte[] buf = str.getBytes();
            out.write(buf);
            int c;
            while ((c = in.read()) != -1) {
                System.out.print((char)c);
            }
            s.close();
        }
        catch (Exception ex) {}
    }
    
    private void downloadLanguageFile(final String fileName) throws IOException {
        final BufferedInputStream in = new BufferedInputStream(new URL("http://www.doggycraft.dk/plugins/werewolf/lang/" + fileName).openStream());
        final FileOutputStream fos = new FileOutputStream(this.plugin.getDataFolder() + "/lang/" + fileName);
        final BufferedOutputStream bout = new BufferedOutputStream(fos, 1024);
        final byte[] data = new byte[1024];
        int x = 0;
        while ((x = in.read(data, 0, 1024)) >= 0) {
            bout.write(data, 0, x);
        }
        bout.close();
        in.close();
    }
    
    private boolean loadLanguageFile(final String fileName) {
        final File languageConfigFile = new File(this.plugin.getDataFolder() + "/lang/" + fileName);
        if (!languageConfigFile.exists()) {
            return false;
        }
        try {
            (this.languageConfig = (FileConfiguration)new YamlConfiguration()).loadFromString(Files.toString(languageConfigFile, Charset.forName("UTF-8")));
        }
        catch (Exception e) {
            this.plugin.log("Could not load data from " + languageConfigFile + ": " + e.getMessage());
            return false;
        }
        this.languageName = this.languageConfig.getString("Version.Name");
        this.authorName = this.languageConfig.getString("Version.Author");
        this.plugin.logDebug("Loaded " + this.languageConfig.getString("Version.Name") + " by " + this.languageConfig.getString("Version.Author") + " version " + this.languageConfig.getString("Version.Version"));
        return true;
    }
    
    public void load() {
        final File directory = new File(this.plugin.getDataFolder() + "/lang");
        if (!directory.exists()) {
            System.out.println("Creating language file directory '/lang'...");
            final boolean result = directory.mkdir();
            if (!result) {
                this.plugin.logDebug("Could not create language directory!");
                return;
            }
            this.plugin.logDebug("Language directory created");
        }
        final String languageFileName = String.valueOf(this.plugin.language) + ".yml";
        if (!this.loadLanguageFile(languageFileName)) {
            this.plugin.log("Could not load " + languageFileName + " from the /lang folder.");
            this.plugin.log("Downloading " + languageFileName + " from DogOnFire...");
            try {
                this.downloadLanguageFile(languageFileName);
            }
            catch (Exception ex) {
                this.plugin.log("Could not download " + languageFileName + " language file from DogOnFire: " + ex.getMessage());
                return;
            }
            if (!this.loadLanguageFile(languageFileName)) {
                this.plugin.log("Could not load " + languageFileName + "!");
            }
        }
    }
    
    public String getLanguageString(final LANGUAGESTRING type) {
        final List<String> strings = (List<String>)this.languageConfig.getStringList(type.name());
        if (strings.size() == 0) {
            this.plugin.log("No language strings found for " + type.name() + "!");
            return String.valueOf(type.name()) + " MISSING";
        }
        final String text = (String)strings.toArray()[this.random.nextInt(strings.size())];
        return this.parseString(text);
    }
    
    LanguageManager(final Werewolf p) {
        super();
        this.languageConfig = null;
        this.random = new Random();
        this.plugin = p;
    }
    
    public String getAuthor() {
        return this.authorName;
    }
    
    public String getLanguageName() {
        return this.languageName;
    }
    
    public String parseString(final String id) {
        String string = id.replaceAll("&([0-9a-f])", "§$1");
        if (string.contains("$ServerName")) {
            string = string.replace("$ServerName", ChatColor.GOLD + this.plugin.serverName + ChatColor.WHITE + ChatColor.BOLD);
        }
        if (string.contains("$PlayerName")) {
            string = string.replace("$PlayerName", ChatColor.GOLD + this.playerName + ChatColor.WHITE + ChatColor.BOLD);
        }
        if (string.contains("$Amount")) {
            string = string.replace("$Amount", ChatColor.GOLD + this.amount + ChatColor.WHITE + ChatColor.BOLD);
        }
        if (string.contains("$Type")) {
            string = string.replace("$Type", ChatColor.GOLD + this.type + ChatColor.WHITE + ChatColor.BOLD);
        }
        return string;
    }
    
    public String parseStringForBook(final String id) {
        String string = id;
        if (string.contains("$ServerName")) {
            string = string.replace("$ServerName", this.plugin.serverName);
        }
        if (string.contains("$PlayerName")) {
            string = string.replace("$PlayerName", this.playerName);
        }
        if (string.contains("$Amount")) {
            string = string.replace("$Amount", this.amount);
        }
        if (string.contains("$Type")) {
            string = string.replace("$Type", this.type);
        }
        return string;
    }
    
    public String getPlayerName() {
        return this.playerName;
    }
    
    public void setPlayerName(final String name) {
        if (name == null) {
            this.plugin.logDebug("WARNING: Setting null playername");
        }
        this.playerName = name;
    }
    
    public String getAmount() {
        return this.amount;
    }
    
    public void setAmount(final String amount) {
        this.amount = amount;
    }
    
    public String getType() {
        return this.type;
    }
    
    public void setType(final String t) {
        if (t == null) {
            this.plugin.logDebug("WARNING: Setting null type");
        }
        this.type = t;
    }
    
    public String getItemTypeName(final Material material) {
        final String itemTypeName = null;
        if (itemTypeName == null) {
            final String languageFileName = String.valueOf(this.plugin.language) + ".yml";
            this.plugin.logDebug("WARNING: No language string in " + languageFileName + " for the item '" + material.name() + "'");
            return material.name();
        }
        return itemTypeName;
    }
    
    public String getMobTypeName(final EntityType type) {
        final String mobTypeName = null;
        if (mobTypeName == null) {
            final String languageFileName = String.valueOf(this.plugin.language) + ".yml";
            this.plugin.logDebug("WARNING: No language string in " + languageFileName + " for the mob type '" + type.name() + "'");
            return type.name();
        }
        return mobTypeName;
    }
    
    public enum LANGUAGESTRING
    {
        FullMoonIsRising("FullMoonIsRising", 0), 
        YouBoughtAInfectionPotion("YouBoughtAInfectionPotion", 1), 
        YouBoughtACurePotion("YouBoughtACurePotion", 2), 
        YouDoNotHaveEnoughMoney("YouDoNotHaveEnoughMoney", 3), 
        YouPlacedACureSign("YouPlacedACureSign", 4), 
        YouPlacedAPotionSign("YouPlacedAPotionSign", 5), 
        YouAreNotTheAlphaOfTheClan("YouAreNotTheAlphaOfTheClan", 6), 
        NoRecentAlphaCall("NoRecentAlphaCall", 7), 
        ClanAlphaCannotAnswerCall("ClanAlphaCannotAnswerCall", 8), 
        YouCalledClanMember("YouCalledClanMember", 9), 
        YouAreBeingCalled("YouAreBeingCalled", 10), 
        NewClanAlpha("NewClanAlpha", 11), 
        CannotTransformSoSoon("CannotTransformSoSoon", 12), 
        WerewolfCommandControlledTransformation("WerewolfCommandControlledTransformation", 13), 
        WerewolfCommandGoldImmunity("WerewolfCommandGoldImmunity", 14), 
        WerewolfCommandNoDropItems("WerewolfCommandNoDropItems", 15), 
        WerewolfCommandFullMoonImmunity("WerewolfCommandFullMoonImmunity", 16), 
        KilledMobPoints("KilledMobPoints", 17), 
        MustExperienceMoreFullMoons("MustExperienceMoreFullMoons", 18), 
        NoWerewolfHuntersInWorld("NoWerewolfHuntersInWorld", 19), 
        TopWerewolfHunters("TopWerewolfHunters", 20), 
        CreatedWerewolfInfectionPotion("CreatedWerewolfInfectionPotion", 21), 
        CreatedWerewolfCurePotion("CreatedWerewolfCurePotion", 22), 
        NotAWerewolfAnymore("NotAWerewolfAnymore", 23), 
        NowAWerewolf("NowAWerewolf", 24), 
        InvalidCommand("InvalidCommand", 25), 
        HelpCall("HelpCall", 26), 
        HelpWerewolf("HelpWerewolf", 27), 
        HelpCheck("HelpCheck", 28), 
        HelpTop("HelpTop", 29), 
        HelpHunt("HelpHunt", 30), 
        HelpBounty("HelpBounty", 31), 
        HelpAddBounty("HelpAddBounty", 32), 
        HelpInfectSelf("HelpInfectSelf", 33), 
        HelpInfectOther("HelpInfectOther", 34), 
        HelpToggleSelf("HelpToggleSelf", 35), 
        HelpToggleOther("HelpToggleOther", 36), 
        HelpPotion("HelpPotion", 37), 
        HelpCure("HelpCure", 38), 
        HelpTransform("HelpTransform", 39), 
        HelpClan("HelpClan", 40), 
        MadeWerewolf("MadeWerewolf", 41), 
        UnMadeWerewolf("UnMadeWerewolf", 42), 
        Infected("Infected", 43), 
        PlayerIsHuntingWerewolves("PlayerIsHuntingWerewolves", 44), 
        PlayerIsNoLongerHuntingWerewolves("PlayerIsNoLongerHuntingWerewolves", 45), 
        TakeCompassInHands("TakeCompassInHands", 46), 
        YouMustHaveYourHandsFree("YouMustHaveYourHandsFree", 47), 
        NoPermissionForCommand("NoPermissionForCommand", 48), 
        NoWerewolvesOnline("NoWerewolvesOnline", 49), 
        NoWerewolvesInThisWorld("NoWerewolvesInThisWorld", 50), 
        WerewolvesCannotHuntWerewolves("WerewolvesCannotHuntWerewolves", 51), 
        WerewolfCommandNumberOfWerewolves("WerewolfCommandNumberOfWerewolves", 52), 
        WerewolfCommandIsWerewolfAlpha("WerewolfCommandIsWerewolfAlpha", 53), 
        WerewolfCommandIsWerewolf("WerewolfCommandIsWerewolf", 54), 
        WerewolfCommandIsNotWerewolf("WerewolfCommandIsNotWerewolf", 55), 
        WerewolfCommandNextFullMoon("WerewolfCommandNextFullMoon", 56), 
        WerewolfCommandListCommands("WerewolfCommandListCommands", 57), 
        Transform("Transform", 58), 
        Untransform("Untransform", 59), 
        BiteVictim("BiteVictim", 60), 
        BiteAttacker("BiteAttacker", 61), 
        BiteWildWolf("BiteWildWolf", 62), 
        KilledMob("KilledMob", 63), 
        WerewolfTryEat("WerewolfTryEat", 64), 
        DrinkCureSuccess("DrinkCureSuccess", 65), 
        DrinkCureFailure("DrinkCureFailure", 66), 
        DrinkInfectionSuccess("DrinkInfectionSuccess", 67), 
        DrinkInfectionFailure("DrinkInfectionFailure", 68), 
        PotionCreated("PotionCreated", 69), 
        UrgesInfectedThisNight("UrgesInfectedThisNight", 70), 
        UrgesInside("UrgesInside", 71), 
        UrgesInfectedHumanForm("UrgesInfectedHumanForm", 72), 
        UrgesTransformed("UrgesTransformed", 73), 
        InfectionPotionTitle("InfectionPotionTitle", 74), 
        CurePotionDescription1("CurePotionDescription1", 75), 
        CurePotionDescription2("CurePotionDescription2", 76), 
        InfectionPotionDescription1("InfectionPotionDescription1", 77), 
        InfectionPotionDescription2("InfectionPotionDescription2", 78), 
        BountyPlayerAdded("BountyPlayerAdded", 79), 
        BountyServerAdded("BountyServerAdded", 80), 
        BountyTotal("BountyTotal", 81), 
        KilledWerewolfBounty("KilledWerewolfBounty", 82), 
        KilledWerewolfNoBounty("KilledWerewolfNoBounty", 83), 
        TrophyDescription("TrophyDescription", 84), 
        InfoCommandHowl("InfoCommandHowl", 85), 
        InfoCommandGrowl("InfoCommandGrowl", 86), 
        Today("Today", 87), 
        Tomorrow("Tomorrow", 88), 
        In2Days("In2Days", 89), 
        In3Days("In3Days", 90), 
        In4Days("In4Days", 91), 
        In5Days("In5Days", 92), 
        In6Days("In6Days", 93), 
        In7Days("In7Days", 94);
    }
}

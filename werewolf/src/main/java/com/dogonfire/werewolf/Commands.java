package com.dogonfire.werewolf;

import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Material;
import java.util.Random;
import java.util.Iterator;
import java.util.Set;
import java.util.List;
import org.apache.commons.lang.StringUtils;
import java.util.Comparator;
import java.util.Collections;
import java.util.ArrayList;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

public class Commands implements Listener
{
    private Werewolf plugin;
    
    Commands(final Werewolf p) {
        super();
        this.plugin = p;
    }
    
    public boolean CommandInfo(final Player player) {
        if (player == null) {
            this.plugin.log("---------- " + this.plugin.getDescription().getFullName() + " ----------");
            Werewolf.getLanguageManager().setAmount(new StringBuilder().append(Werewolf.getWerewolfManager().getNumberOfWerewolves()).toString());
            this.plugin.log(Werewolf.getLanguageManager().getLanguageString(LanguageManager.LANGUAGESTRING.WerewolfCommandNumberOfWerewolves));
        }
        else {
            player.sendMessage(ChatColor.YELLOW + "------------------ " + this.plugin.getDescription().getFullName() + " ------------------");
            player.sendMessage(ChatColor.AQUA + "By DogOnFire");
            player.sendMessage(ChatColor.AQUA + Werewolf.getLanguageManager().getLanguageName() + " by " + Werewolf.getLanguageManager().getAuthor());
            player.sendMessage(new StringBuilder().append(ChatColor.AQUA).toString());
            Werewolf.getLanguageManager().setAmount(new StringBuilder().append(Werewolf.getWerewolfManager().getNumberOfWerewolves()).toString());
            player.sendMessage(ChatColor.AQUA + Werewolf.getLanguageManager().getLanguageString(LanguageManager.LANGUAGESTRING.WerewolfCommandNumberOfWerewolves));
            player.sendMessage(new StringBuilder().append(ChatColor.AQUA).toString());
            if (Werewolf.getWerewolfManager().isWerewolf(player)) {
                final int numberOfTransformations = Werewolf.getWerewolfManager().getNumberOfTransformations(player.getName());
                Werewolf.getLanguageManager().setType(Werewolf.getClanManager().getClanName(player.getName()));
                Werewolf.getLanguageManager().setAmount("Level " + Werewolf.getWerewolfManager().getNumberOfTransformations(player.getName()));
                player.sendMessage(ChatColor.WHITE + Werewolf.getLanguageManager().getLanguageString(LanguageManager.LANGUAGESTRING.WerewolfCommandIsWerewolf));
                if (this.plugin.useClans && Werewolf.getClanManager().isAlpha(player.getName())) {
                    final String clanName = String.valueOf(Werewolf.getClanManager().getClanName(Werewolf.getWerewolfManager().getWerewolfClan(player.getName()))) + "s";
                    Werewolf.getLanguageManager().setType(clanName);
                    player.sendMessage(ChatColor.WHITE + Werewolf.getLanguageManager().getLanguageString(LanguageManager.LANGUAGESTRING.WerewolfCommandIsWerewolfAlpha));
                }
                player.sendMessage(new StringBuilder().append(ChatColor.AQUA).toString());
                if (numberOfTransformations >= this.plugin.transformsForNoDropItems) {
                    player.sendMessage(ChatColor.WHITE + Werewolf.getLanguageManager().getLanguageString(LanguageManager.LANGUAGESTRING.WerewolfCommandNoDropItems));
                }
                if (numberOfTransformations >= this.plugin.transformsForControlledTransformation) {
                    player.sendMessage(ChatColor.WHITE + Werewolf.getLanguageManager().getLanguageString(LanguageManager.LANGUAGESTRING.WerewolfCommandControlledTransformation));
                }
                if (numberOfTransformations >= this.plugin.transformsForGoldImmunity) {
                    player.sendMessage(ChatColor.WHITE + Werewolf.getLanguageManager().getLanguageString(LanguageManager.LANGUAGESTRING.WerewolfCommandGoldImmunity));
                }
            }
            else {
                player.sendMessage(ChatColor.RED + Werewolf.getLanguageManager().getLanguageString(LanguageManager.LANGUAGESTRING.WerewolfCommandIsNotWerewolf));
            }
            player.sendMessage(new StringBuilder().append(ChatColor.AQUA).toString());
            Werewolf.getLanguageManager().setAmount(this.plugin.getNextFullMoonText(player.getWorld()));
            player.sendMessage(ChatColor.AQUA + Werewolf.getLanguageManager().getLanguageString(LanguageManager.LANGUAGESTRING.WerewolfCommandNextFullMoon));
            player.sendMessage(new StringBuilder().append(ChatColor.AQUA).toString());
            player.sendMessage(ChatColor.AQUA + Werewolf.getLanguageManager().getLanguageString(LanguageManager.LANGUAGESTRING.WerewolfCommandListCommands));
            this.plugin.log(String.valueOf(player.getName()) + ": /werewolf");
        }
        return true;
    }
    
    public void CommandReload(final Player player) {
        this.plugin.reloadSettings();
        Werewolf.getWerewolfManager().load();
        if (player == null) {
            this.plugin.log(String.valueOf(this.plugin.getDescription().getFullName()) + ": Reloaded configuration.");
        }
        else {
            player.sendMessage(ChatColor.YELLOW + this.plugin.getDescription().getFullName() + ": " + ChatColor.WHITE + "Reloaded configuration.");
        }
    }
    
    public boolean CommandHelp(final Player player) {
        if (player == null) {
            this.plugin.log(ChatColor.WHITE + "/werewolf" + ChatColor.AQUA + " - Show basic info");
            this.plugin.log("/howl - Howl as a Werewolf!");
            this.plugin.log("/growl - Growl as a Werewolf!");
            this.plugin.log("/werewolf top - View the top Werewolf hunters in " + this.plugin.serverName);
            this.plugin.log("/werewolf hunt - Toggles Werewolf hunt mode");
            this.plugin.log("/werewolf check <playername> - Check Werewolf status for a player");
            this.plugin.log("/werewolf bounty - Check the current bounty for killing a Werewolf");
            this.plugin.log("/werewolf addbounty - Add to the bounty for killing a Werewolf");
            this.plugin.log("/werewolf toggle - Toggles Werewolf status for yourself");
            this.plugin.log("/werewolf toggle <playername> - Toggles Werewolf status for another player");
            this.plugin.log("/werewolf infect <playername> - Infects a player with the Werewolf infection");
        }
        else {
            player.sendMessage(ChatColor.YELLOW + "---------- " + this.plugin.getDescription().getFullName() + " ----------");
            player.sendMessage(ChatColor.AQUA + Werewolf.getLanguageManager().getLanguageString(LanguageManager.LANGUAGESTRING.HelpWerewolf));
            if (this.plugin.vaultEnabled) {
                if (player.isOp() || Werewolf.getPermissionsManager().hasPermission(player, "werewolf.hunt")) {
                    player.sendMessage(ChatColor.AQUA + Werewolf.getLanguageManager().getLanguageString(LanguageManager.LANGUAGESTRING.HelpHunt));
                }
                if (player.isOp() || Werewolf.getPermissionsManager().hasPermission(player, "werewolf.top")) {
                    player.sendMessage(ChatColor.AQUA + Werewolf.getLanguageManager().getLanguageString(LanguageManager.LANGUAGESTRING.HelpTop));
                }
                if (player.isOp() || Werewolf.getPermissionsManager().hasPermission(player, "werewolf.bounty")) {
                    player.sendMessage(ChatColor.AQUA + Werewolf.getLanguageManager().getLanguageString(LanguageManager.LANGUAGESTRING.HelpBounty));
                }
                if (player.isOp() || Werewolf.getPermissionsManager().hasPermission(player, "werewolf.addbounty")) {
                    player.sendMessage(ChatColor.AQUA + Werewolf.getLanguageManager().getLanguageString(LanguageManager.LANGUAGESTRING.HelpAddBounty));
                }
            }
            if (player.isOp() || Werewolf.getPermissionsManager().hasPermission(player, "werewolf.transform")) {
                player.sendMessage(ChatColor.AQUA + Werewolf.getLanguageManager().getLanguageString(LanguageManager.LANGUAGESTRING.HelpTransform));
            }
            if (player.isOp() || Werewolf.getPermissionsManager().hasPermission(player, "werewolf.check")) {
                player.sendMessage(ChatColor.AQUA + Werewolf.getLanguageManager().getLanguageString(LanguageManager.LANGUAGESTRING.HelpCheck));
            }
            if (player.isOp() || Werewolf.getPermissionsManager().hasPermission(player, "werewolf.infectself")) {
                player.sendMessage(ChatColor.AQUA + Werewolf.getLanguageManager().getLanguageString(LanguageManager.LANGUAGESTRING.HelpInfectSelf));
            }
            if (player.isOp() || Werewolf.getPermissionsManager().hasPermission(player, "werewolf.infect")) {
                player.sendMessage(ChatColor.AQUA + Werewolf.getLanguageManager().getLanguageString(LanguageManager.LANGUAGESTRING.HelpInfectOther));
            }
            if (player.isOp() || Werewolf.getPermissionsManager().hasPermission(player, "werewolf.togglewerewolf")) {
                player.sendMessage(ChatColor.AQUA + Werewolf.getLanguageManager().getLanguageString(LanguageManager.LANGUAGESTRING.HelpToggleOther));
            }
            if (player.isOp() || Werewolf.getPermissionsManager().hasPermission(player, "werewolf.togglewerewolfself")) {
                player.sendMessage(ChatColor.AQUA + Werewolf.getLanguageManager().getLanguageString(LanguageManager.LANGUAGESTRING.HelpToggleSelf));
            }
            if (player.isOp() || Werewolf.getPermissionsManager().hasPermission(player, "werewolf.potion.infection.create")) {
                player.sendMessage(ChatColor.AQUA + Werewolf.getLanguageManager().getLanguageString(LanguageManager.LANGUAGESTRING.HelpPotion));
            }
            if (player.isOp() || Werewolf.getPermissionsManager().hasPermission(player, "werewolf.potion.cure.create")) {
                player.sendMessage(ChatColor.AQUA + Werewolf.getLanguageManager().getLanguageString(LanguageManager.LANGUAGESTRING.HelpCure));
            }
            if (this.plugin.useClans && Werewolf.getClanManager().isAlpha(player.getName())) {
                if (player.isOp() || Werewolf.getPermissionsManager().hasPermission(player, "werewolf.clan")) {
                    player.sendMessage(ChatColor.AQUA + Werewolf.getLanguageManager().getLanguageString(LanguageManager.LANGUAGESTRING.HelpClan));
                }
                if (player.isOp() || Werewolf.getPermissionsManager().hasPermission(player, "werewolf.alpha.call")) {
                    player.sendMessage(ChatColor.AQUA + Werewolf.getLanguageManager().getLanguageString(LanguageManager.LANGUAGESTRING.HelpCall));
                }
            }
            this.plugin.log(String.valueOf(player.getName()) + ": /werewolf help");
        }
        return true;
    }
    
    private void CommandTopHunters(final Player player) {
        if (!this.plugin.vaultEnabled) {
            this.plugin.sendInfo(player, ChatColor.RED + "Vault not detected. Werewolf hunts & bounties are disabled.");
        }
        else if (player == null || Werewolf.getPermissionsManager().hasPermission(player, "werewolf.top") || player.isOp()) {
            List<Hunter> hunters = new ArrayList<Hunter>();
            final Set<String> list = Werewolf.getHuntManager().getHunters();
            for (final String hunterName : list) {
                final int kills = Werewolf.getHuntManager().getHunterKills(hunterName);
                if (kills > 0) {
                    hunters.add(new Hunter(hunterName, kills));
                }
            }
            if (hunters.size() == 0) {
                if (player != null) {
                    this.plugin.sendInfo(player, ChatColor.RED + Werewolf.getLanguageManager().getLanguageString(LanguageManager.LANGUAGESTRING.NoWerewolfHuntersInWorld));
                }
                else {
                    this.plugin.log(Werewolf.getLanguageManager().getLanguageString(LanguageManager.LANGUAGESTRING.NoWerewolfHuntersInWorld));
                }
                return;
            }
            if (player != null) {
                this.plugin.sendInfo(player, ChatColor.RED + Werewolf.getLanguageManager().getLanguageString(LanguageManager.LANGUAGESTRING.TopWerewolfHunters));
            }
            else {
                this.plugin.log(Werewolf.getLanguageManager().getLanguageString(LanguageManager.LANGUAGESTRING.TopWerewolfHunters));
            }
            Collections.sort(hunters, new TopHuntersComparator());
            final int l = hunters.size();
            if (l > 10) {
                hunters = hunters.subList(0, 10);
            }
            int n = 1;
            for (final Hunter hunter : hunters) {
                final String message = new StringBuilder().append(n).append(ChatColor.AQUA).append(" - ").append(StringUtils.rightPad(hunter.name, 15)).append(StringUtils.rightPad(hunter.kills + " Kills", 3)).toString();
                this.plugin.sendInfo(player, ChatColor.YELLOW + message);
                ++n;
            }
            if (player != null) {
                this.plugin.log(String.valueOf(player.getName()) + ": /werewolf top");
            }
        }
        else {
            this.plugin.sendInfo(player, ChatColor.RED + Werewolf.getLanguageManager().getLanguageString(LanguageManager.LANGUAGESTRING.NoPermissionForCommand));
        }
    }
    
    public boolean CommandTransform(final Player player) {
        if (player == null) {
            this.plugin.sendInfo(player, ChatColor.RED + "This command cannot be used from console");
            return false;
        }
        if (!Werewolf.getPermissionsManager().hasPermission(player, "werewolf.transform") && !player.isOp()) {
            this.plugin.sendInfo(player, ChatColor.RED + Werewolf.getLanguageManager().getLanguageString(LanguageManager.LANGUAGESTRING.NoPermissionForCommand));
            return false;
        }
        if (!Werewolf.getWerewolfManager().isWerewolf(player)) {
            this.plugin.sendInfo(player, ChatColor.RED + Werewolf.getLanguageManager().getLanguageString(LanguageManager.LANGUAGESTRING.WerewolfCommandIsNotWerewolf));
            return false;
        }
        if (this.plugin.isVampire(player)) {
            this.plugin.sendInfo(player, ChatColor.RED + Werewolf.getLanguageManager().getLanguageString(LanguageManager.LANGUAGESTRING.NoPermissionForCommand));
            return false;
        }
        if (Werewolf.getWerewolfManager().getNumberOfTransformations(player.getName()) < this.plugin.transformsForControlledTransformation) {
            final int fullMoons = this.plugin.transformsForControlledTransformation - Werewolf.getWerewolfManager().getNumberOfTransformations(player.getName());
            Werewolf.getLanguageManager().setAmount(new StringBuilder().append(fullMoons).toString());
            this.plugin.sendInfo(player, ChatColor.RED + Werewolf.getLanguageManager().getLanguageString(LanguageManager.LANGUAGESTRING.MustExperienceMoreFullMoons));
            return false;
        }
        if (Werewolf.getWerewolfManager().hasWerewolfSkin(player.getName())) {
            this.plugin.untransform(player);
        }
        else {
            if (Werewolf.getWerewolfManager().hasRecentTransform(player.getName())) {
                this.plugin.sendInfo(player, ChatColor.RED + Werewolf.getLanguageManager().getLanguageString(LanguageManager.LANGUAGESTRING.CannotTransformSoSoon));
                return false;
            }
            this.plugin.transform(player);
        }
        return true;
    }
    
    public boolean CommandClan(final Player player) {
        if (!this.plugin.useClans) {
            this.plugin.sendInfo(player, ChatColor.RED + "Clans are not enabled.");
            return false;
        }
        if (player == null) {
            this.plugin.sendInfo(player, ChatColor.RED + "This command cannot be used from console");
            return false;
        }
        if (!Werewolf.getPermissionsManager().hasPermission(player, "werewolf.clan") && !player.isOp()) {
            this.plugin.sendInfo(player, ChatColor.RED + Werewolf.getLanguageManager().getLanguageString(LanguageManager.LANGUAGESTRING.NoPermissionForCommand));
            return false;
        }
        if (!Werewolf.getWerewolfManager().isWerewolf(player)) {
            this.plugin.sendInfo(player, ChatColor.RED + Werewolf.getLanguageManager().getLanguageString(LanguageManager.LANGUAGESTRING.WerewolfCommandIsNotWerewolf));
            return false;
        }
        final String clanName = Werewolf.getClanManager().getClanName(player.getName());
        final ClanManager.ClanType playerClan = Werewolf.getWerewolfManager().getWerewolfClan(player.getName());
        String alphaName = Werewolf.getClanManager().getAlpha(playerClan);
        if (alphaName == null) {
            alphaName = "None!";
        }
        this.plugin.sendInfo(player, ChatColor.GOLD + "------------ The " + clanName + " Clan ------------");
        this.plugin.sendInfo(player, new StringBuilder().append(ChatColor.AQUA).toString());
        this.plugin.sendInfo(player, ChatColor.AQUA + " Clan Alpha : " + ChatColor.WHITE + alphaName);
        this.plugin.sendInfo(player, new StringBuilder().append(ChatColor.AQUA).toString());
        this.plugin.sendInfo(player, ChatColor.GOLD + "------------ The Werewolf Clans ------------");
        final List<ClanManager.ClanType> clanList = Werewolf.getClanManager().getClansRanked();
        int n = 1;
        for (final ClanManager.ClanType clan : clanList) {
            if (clan == playerClan) {
                this.plugin.sendInfo(player, new StringBuilder().append(ChatColor.GOLD).append(n).append(") ").append(ChatColor.GOLD).append(Werewolf.getClanManager().getClanName(clan)).append(ChatColor.AQUA).append("  -  ").append(Werewolf.getWerewolfManager().getWerewolfClanMembers(clan).size()).append(" members  -  ").append(String.format("%1$,.2f", Werewolf.getClanManager().getClanPoint(clan))).append(" clan points").toString());
            }
            else {
                this.plugin.sendInfo(player, new StringBuilder().append(ChatColor.GOLD).append(n).append(") ").append(ChatColor.WHITE).append(Werewolf.getClanManager().getClanName(clan)).append(ChatColor.AQUA).append("  -  ").append(Werewolf.getWerewolfManager().getWerewolfClanMembers(clan).size()).append(" members  -  ").append(String.format("%1$,.2f", Werewolf.getClanManager().getClanPoint(clan))).append(" clan points").toString());
            }
            ++n;
        }
        this.plugin.sendInfo(player, new StringBuilder().append(ChatColor.GOLD).toString());
        this.plugin.sendInfo(player, ChatColor.GOLD + Werewolf.getClanManager().getClanName(clanList.get(0)) + ChatColor.DARK_RED + " has the Blood Rage!");
        return true;
    }
    
    public boolean CommandToggleSelfWerewolf(final Player player, final String[] args) {
        if (player == null) {
            this.plugin.sendInfo(player, ChatColor.RED + "This command cannot be used from console");
            return false;
        }
        if (!Werewolf.getPermissionsManager().hasPermission(player, "werewolf.togglewerewolfself") && !player.isOp()) {
            this.plugin.sendInfo(player, ChatColor.RED + Werewolf.getLanguageManager().getLanguageString(LanguageManager.LANGUAGESTRING.NoPermissionForCommand));
            return false;
        }
        if (this.plugin.isVampire(player)) {
            player.sendMessage(ChatColor.RED + "You cannot be a werewolf when you are a vampire!");
            return false;
        }
        if (!Werewolf.getWerewolfManager().isWerewolf(player)) {
            final Random random = new Random();
            Werewolf.getWerewolfManager().makeWerewolf(player, true, ClanManager.ClanType.values()[random.nextInt(ClanManager.ClanType.values().length)]);
            Werewolf.getWerewolfManager().setInfectedThisNight(player.getName(), false);
            this.plugin.sendInfo(player, ChatColor.AQUA + Werewolf.getLanguageManager().getLanguageString(LanguageManager.LANGUAGESTRING.NowAWerewolf));
        }
        else {
            Werewolf.getWerewolfManager().unmakeWerewolf(player.getName());
            this.plugin.sendInfo(player, ChatColor.AQUA + Werewolf.getLanguageManager().getLanguageString(LanguageManager.LANGUAGESTRING.NotAWerewolfAnymore));
        }
        if (player != null) {
            this.plugin.log(String.valueOf(player.getName()) + ": /werewolf toggleself");
        }
        return true;
    }
    
    public boolean CommandTogglePlayerWerewolf(final Player player, final String[] args) {
        if (player != null && !Werewolf.getPermissionsManager().hasPermission(player, "werewolf.togglewerewolf")) {
            if (!player.isOp()) {
                this.plugin.sendInfo(player, ChatColor.RED + Werewolf.getLanguageManager().getLanguageString(LanguageManager.LANGUAGESTRING.NoPermissionForCommand));
                return true;
            }
        }
        try {
            final Player newWerewolf = this.plugin.getServer().getPlayer(args[1]);
            if (newWerewolf == null) {
                throw new Exception();
            }
            if (!Werewolf.getWerewolfManager().isWerewolf(newWerewolf)) {
                final Random random = new Random();
                Werewolf.getWerewolfManager().makeWerewolf(newWerewolf, true, ClanManager.ClanType.values()[random.nextInt(ClanManager.ClanType.values().length)]);
                Werewolf.getWerewolfManager().setInfectedThisNight(newWerewolf.getName(), false);
                if (player != null) {
                    player.sendMessage(ChatColor.AQUA + newWerewolf.getName() + " is now a werewolf! ");
                    Werewolf.getLanguageManager().setPlayerName(player.getName());
                }
                else {
                    this.plugin.log(String.valueOf(newWerewolf.getName()) + " is now a werewolf! ");
                    Werewolf.getLanguageManager().setPlayerName("CONSOLE");
                }
                Werewolf.getLanguageManager().getLanguageString(LanguageManager.LANGUAGESTRING.MadeWerewolf);
            }
            else {
                Werewolf.getWerewolfManager().unmakeWerewolf(newWerewolf.getName());
                if (player != null) {
                    player.sendMessage(ChatColor.AQUA + newWerewolf.getName() + " is no longer a werewolf ...");
                    Werewolf.getLanguageManager().setPlayerName(player.getName());
                }
                else {
                    this.plugin.log(String.valueOf(newWerewolf.getName()) + " is no longer a werewolf ...");
                    Werewolf.getLanguageManager().setPlayerName("CONSOLE");
                }
                newWerewolf.sendMessage(ChatColor.AQUA + Werewolf.getLanguageManager().getLanguageString(LanguageManager.LANGUAGESTRING.UnMadeWerewolf));
            }
            if (player != null) {
                this.plugin.log(String.valueOf(player.getName()) + ": /werewolf toggle " + newWerewolf.getName());
            }
        }
        catch (Exception e) {
            this.plugin.sendInfo(player, ChatColor.RED + "Invalid player name...");
        }
        return true;
    }
    
    public boolean CommandInfect(final Player player, final String[] args) {
        if (player != null && !Werewolf.getPermissionsManager().hasPermission(player, "werewolf.infect")) {
            if (!player.isOp()) {
                this.plugin.sendInfo(player, ChatColor.RED + Werewolf.getLanguageManager().getLanguageString(LanguageManager.LANGUAGESTRING.NoPermissionForCommand));
                return true;
            }
        }
        try {
            final Player newInfection = this.plugin.getServer().getPlayer(args[1]);
            if (newInfection == null) {
                throw new Exception();
            }
            if (!Werewolf.getWerewolfManager().isWerewolf(newInfection)) {
                final Random random = new Random();
                Werewolf.getWerewolfManager().makeWerewolf(newInfection, false, ClanManager.ClanType.values()[random.nextInt(ClanManager.ClanType.values().length)]);
                this.plugin.sendInfo(player, ChatColor.GREEN + newInfection.getName() + " now has the werewolf infection!");
                if (player == null) {
                    Werewolf.getLanguageManager().setPlayerName("CONSOLE");
                }
                else {
                    Werewolf.getLanguageManager().setPlayerName(player.getName());
                }
                newInfection.sendMessage(ChatColor.AQUA + Werewolf.getLanguageManager().getLanguageString(LanguageManager.LANGUAGESTRING.Infected));
            }
            else {
                this.plugin.sendInfo(player, ChatColor.RED + newInfection.getName() + " already is a werewolf! Please toggle his status first...");
            }
            this.plugin.log(String.valueOf(player.getName()) + ": /werewolf infect " + newInfection.getName());
        }
        catch (Exception e) {
            this.plugin.sendInfo(player, ChatColor.RED + "Invalid player name...");
        }
        return true;
    }
    
    public boolean CommandInfectSelf(final Player player, final String[] args) {
        if (player == null) {
            this.plugin.sendInfo(player, ChatColor.RED + "This command cannot be used from console");
            return false;
        }
        if (Werewolf.getPermissionsManager().hasPermission(player, "werewolf.infectself") || player.isOp()) {
            if (!Werewolf.getWerewolfManager().isWerewolf(player)) {
                final Random random = new Random();
                if (Werewolf.getWerewolfManager().makeWerewolf(player, false, ClanManager.ClanType.values()[random.nextInt(ClanManager.ClanType.values().length)])) {
                    player.sendMessage(ChatColor.AQUA + "You now have the werewolf infection!");
                }
                else {
                    player.sendMessage(ChatColor.RED + "Could not make you a werewolf!");
                }
            }
            else {
                player.sendMessage(ChatColor.RED + "You are already a werewolf! Please toggle your status first...");
            }
            this.plugin.log(String.valueOf(player.getName()) + ": /werewolf infect");
        }
        else {
            this.plugin.sendInfo(player, ChatColor.RED + Werewolf.getLanguageManager().getLanguageString(LanguageManager.LANGUAGESTRING.NoPermissionForCommand));
        }
        return true;
    }
    
    public boolean CommandCheck(final Player player, final String[] args) {
        if (player == null || Werewolf.getPermissionsManager().hasPermission(player, "werewolf.check") || player.isOp()) {
            Player checkPlayer = null;
            try {
                checkPlayer = this.plugin.getServer().getPlayer(args[1]);
            }
            catch (Exception ex) {
                this.plugin.log("'" + player.getName() + "' made a command error");
            }
            if (checkPlayer != null) {
                if (Werewolf.getWerewolfManager().isFullWerewolf(checkPlayer.getName())) {
                    if (player != null) {
                        player.sendMessage(ChatColor.AQUA + checkPlayer.getName() + " is a full werewolf");
                    }
                    else {
                        this.plugin.log(String.valueOf(checkPlayer.getName()) + " is a full werewolf");
                    }
                }
                else if (Werewolf.getWerewolfManager().isInfectedWerewolf(checkPlayer.getName())) {
                    if (player != null) {
                        player.sendMessage(ChatColor.AQUA + checkPlayer.getName() + " is a infected werewolf");
                    }
                    else {
                        this.plugin.log(String.valueOf(checkPlayer.getName()) + " is a infected werewolf");
                    }
                }
                else if (player != null) {
                    player.sendMessage(ChatColor.AQUA + checkPlayer.getName() + " is not a werewolf");
                }
                else {
                    this.plugin.log(String.valueOf(checkPlayer.getName()) + " is not a werewolf");
                }
                if (player != null) {
                    this.plugin.log(String.valueOf(player.getName()) + ": /werewolf check " + checkPlayer.getName());
                }
            }
        }
        else {
            this.plugin.sendInfo(player, ChatColor.RED + Werewolf.getLanguageManager().getLanguageString(LanguageManager.LANGUAGESTRING.NoPermissionForCommand));
        }
        return true;
    }
    
    public boolean CommandBounty(final Player player) {
        if (!this.plugin.vaultEnabled) {
            this.plugin.sendInfo(player, ChatColor.RED + "Vault not detected. Werewolf hunts & bounties are disabled.");
        }
        else if (player == null || Werewolf.getPermissionsManager().hasPermission(player, "werewolf.bounty") || player.isOp()) {
            Werewolf.getLanguageManager().setAmount(Werewolf.getHuntManager().getBounty());
            if (player == null) {
                this.plugin.log(Werewolf.getLanguageManager().getLanguageString(LanguageManager.LANGUAGESTRING.BountyTotal));
            }
            else {
                player.sendMessage(ChatColor.AQUA + Werewolf.getLanguageManager().getLanguageString(LanguageManager.LANGUAGESTRING.BountyTotal));
            }
            if (player != null) {
                this.plugin.log(String.valueOf(player.getName()) + ": /werewolf bounty");
            }
        }
        else {
            this.plugin.sendInfo(player, ChatColor.RED + Werewolf.getLanguageManager().getLanguageString(LanguageManager.LANGUAGESTRING.NoPermissionForCommand));
        }
        return true;
    }
    
    public boolean CommandAddBounty(final Player player, final String[] args) {
        if (!this.plugin.vaultEnabled) {
            this.plugin.sendInfo(player, ChatColor.RED + "Vault not detected. Werewolf hunts & bounties are disabled.");
        }
        else if (player == null || Werewolf.getPermissionsManager().hasPermission(player, "werewolf.addbounty") || player.isOp()) {
            if (Werewolf.getWerewolfManager().isWerewolf(player)) {
                this.plugin.sendInfo(player, ChatColor.RED + "Werewolves cannot add to the Werewolf bounty!");
                return true;
            }
            try {
                final int bounty = Integer.parseInt(args[1]);
                if (bounty == 0) {
                    this.plugin.sendInfo(player, ChatColor.RED + "How about adding a real amount?");
                    return false;
                }
                Werewolf.getHuntManager().addBounty(player.getName(), bounty);
                if (player != null) {
                    this.plugin.log(String.valueOf(player.getName()) + ": /werewolf addbounty " + bounty);
                }
            }
            catch (Exception ex) {
                this.plugin.sendInfo(player, ChatColor.RED + "Come on, that is not a valid bounty :/");
            }
        }
        else {
            this.plugin.sendInfo(player, ChatColor.RED + Werewolf.getLanguageManager().getLanguageString(LanguageManager.LANGUAGESTRING.NoPermissionForCommand));
        }
        return true;
    }
    
    public boolean CommandHuntWerewolf(final Player player) {
        if (player == null) {
            this.plugin.log("You cannot use this command from the console.");
            return false;
        }
        if (!this.plugin.vaultEnabled) {
            this.plugin.sendInfo(player, ChatColor.RED + "Vault not detected. Werewolf hunts & bounties are disabled.");
            return false;
        }
        if (!this.plugin.isWerewolvesAllowedInWorld(player)) {
            Werewolf.getLanguageManager().setType(player.getWorld().getName());
            this.plugin.sendInfo(player, ChatColor.RED + Werewolf.getLanguageManager().getLanguageString(LanguageManager.LANGUAGESTRING.NoWerewolvesInThisWorld));
            return false;
        }
        if (player != null && !Werewolf.getPermissionsManager().hasPermission(player, "werewolf.hunt") && !player.isOp()) {
            this.plugin.sendInfo(player, ChatColor.RED + Werewolf.getLanguageManager().getLanguageString(LanguageManager.LANGUAGESTRING.NoPermissionForCommand));
            return false;
        }
        if (Werewolf.getWerewolfManager().isWerewolf(player)) {
            this.plugin.sendInfo(player, ChatColor.RED + Werewolf.getLanguageManager().getLanguageString(LanguageManager.LANGUAGESTRING.WerewolvesCannotHuntWerewolves));
            return false;
        }
        if (!Werewolf.getHuntManager().isHunting(player.getName())) {
            if (Werewolf.getWerewolfManager().getOnlineWerewolves().size() == 0) {
                this.plugin.sendInfo(player, ChatColor.RED + Werewolf.getLanguageManager().getLanguageString(LanguageManager.LANGUAGESTRING.NoWerewolvesOnline));
                return false;
            }
            if (player.getItemInHand().getType() != Material.AIR) {
                this.plugin.sendInfo(player, ChatColor.RED + Werewolf.getLanguageManager().getLanguageString(LanguageManager.LANGUAGESTRING.YouMustHaveYourHandsFree));
                return false;
            }
            player.setItemInHand(new ItemStack(Material.COMPASS));
            Werewolf.getLanguageManager().setPlayerName(player.getName());
            this.plugin.getServer().broadcastMessage(ChatColor.GOLD + Werewolf.getLanguageManager().getLanguageString(LanguageManager.LANGUAGESTRING.PlayerIsHuntingWerewolves));
            Werewolf.getHuntManager().setHunting(player.getName(), true);
        }
        else {
            if (player.getItemInHand().getType() != Material.COMPASS) {
                this.plugin.sendInfo(player, ChatColor.RED + Werewolf.getLanguageManager().getLanguageString(LanguageManager.LANGUAGESTRING.TakeCompassInHands));
                return false;
            }
            player.setItemInHand(new ItemStack(Material.AIR));
            Werewolf.getLanguageManager().setPlayerName(player.getName());
            this.plugin.getServer().broadcastMessage(ChatColor.GOLD + Werewolf.getLanguageManager().getLanguageString(LanguageManager.LANGUAGESTRING.PlayerIsNoLongerHuntingWerewolves));
            Werewolf.getHuntManager().setHunting(player.getName(), false);
        }
        if (player != null) {
            this.plugin.log(String.valueOf(player.getName()) + ": /werewolf hunt");
        }
        return true;
    }
    
    public boolean CommandGrowl(final Player player) {
        if (player == null) {
            this.plugin.log("You cannot use this command from the console.");
            return false;
        }
        if (!Werewolf.getPermissionsManager().hasPermission(player, "werewolf.growl") && !player.isOp()) {
            this.plugin.sendInfo(player, ChatColor.RED + Werewolf.getLanguageManager().getLanguageString(LanguageManager.LANGUAGESTRING.NoPermissionForCommand));
            return false;
        }
        if (!Werewolf.getWerewolfManager().isWolfForm(player.getName())) {
            this.plugin.sendInfo(player, ChatColor.RED + "You are not in wolf form");
            return false;
        }
        Werewolf.getWerewolfManager().growl(player);
        return true;
    }
    
    public boolean CommandHowl(final Player player) {
        if (player == null) {
            this.plugin.log("You cannot use this command from the console.");
            return false;
        }
        if (!Werewolf.getPermissionsManager().hasPermission(player, "werewolf.howl") && !player.isOp()) {
            this.plugin.sendInfo(player, ChatColor.RED + Werewolf.getLanguageManager().getLanguageString(LanguageManager.LANGUAGESTRING.NoPermissionForCommand));
            return false;
        }
        if (!Werewolf.getWerewolfManager().isWolfForm(player.getName())) {
            this.plugin.sendInfo(player, ChatColor.RED + "You are not in wolf form");
            return false;
        }
        Werewolf.getWerewolfManager().howl(player);
        if (Werewolf.getClanManager().isAlpha(player.getName())) {
            for (final Entity entity : player.getNearbyEntities(17.5, 17.5, 17.5)) {
                if (!(entity instanceof LivingEntity)) {
                    continue;
                }
                final LivingEntity livingEntity = (LivingEntity)entity;
                livingEntity.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 200, 3));
            }
        }
        return true;
    }
    
    public boolean CommandCall(final Player player) {
        if (player == null) {
            this.plugin.log("You cannot use this command from the console.");
            return false;
        }
        if (!Werewolf.getPermissionsManager().hasPermission(player, "werewolf.alpha.call") && !player.isOp()) {
            this.plugin.sendInfo(player, ChatColor.RED + Werewolf.getLanguageManager().getLanguageString(LanguageManager.LANGUAGESTRING.NoPermissionForCommand));
            return false;
        }
        if (!Werewolf.getWerewolfManager().isWolfForm(player.getName())) {
            this.plugin.sendInfo(player, ChatColor.RED + "You are not in wolf form");
            return false;
        }
        final ClanManager.ClanType playerClan = Werewolf.getWerewolfManager().getWerewolfClan(player.getName());
        if (!Werewolf.getClanManager().isAlpha(playerClan, player.getName())) {
            this.plugin.sendInfo(player, ChatColor.RED + Werewolf.getLanguageManager().getLanguageString(LanguageManager.LANGUAGESTRING.YouAreNotTheAlphaOfTheClan));
            return false;
        }
        Werewolf.getClanManager().setLastCall(playerClan);
        Werewolf.getWerewolfManager().howl(player);
        for (final String playerName : Werewolf.getWerewolfManager().getWerewolfClanMembers(playerClan)) {
            final Player clanPlayer = this.plugin.getServer().getPlayer(playerName);
            if (clanPlayer == null) {
                continue;
            }
            Werewolf.getLanguageManager().setPlayerName(player.getName());
            this.plugin.sendInfo(clanPlayer, ChatColor.RED + Werewolf.getLanguageManager().getLanguageString(LanguageManager.LANGUAGESTRING.YouAreBeingCalled));
            Werewolf.getLanguageManager().setPlayerName(clanPlayer.getName());
            this.plugin.sendInfo(player, ChatColor.RED + Werewolf.getLanguageManager().getLanguageString(LanguageManager.LANGUAGESTRING.YouCalledClanMember));
        }
        return true;
    }
    
    public boolean CommandAcceptCall(final Player player) {
        if (player == null) {
            this.plugin.log("You cannot use this command from the console.");
            return false;
        }
        if (!Werewolf.getPermissionsManager().hasPermission(player, "werewolf.acceptcall") && !player.isOp()) {
            this.plugin.sendInfo(player, ChatColor.RED + Werewolf.getLanguageManager().getLanguageString(LanguageManager.LANGUAGESTRING.NoPermissionForCommand));
            return false;
        }
        if (!Werewolf.getWerewolfManager().isWolfForm(player.getName())) {
            this.plugin.sendInfo(player, ChatColor.RED + "You are not in wolf form");
            return false;
        }
        if (Werewolf.getClanManager().isAlpha(player.getName())) {
            this.plugin.sendInfo(player, ChatColor.RED + Werewolf.getLanguageManager().getLanguageString(LanguageManager.LANGUAGESTRING.ClanAlphaCannotAnswerCall));
            return false;
        }
        final ClanManager.ClanType playerClan = Werewolf.getWerewolfManager().getWerewolfClan(player.getName());
        if (!Werewolf.getClanManager().hasRecentCall(playerClan)) {
            this.plugin.sendInfo(player, ChatColor.RED + Werewolf.getLanguageManager().getLanguageString(LanguageManager.LANGUAGESTRING.NoRecentAlphaCall));
            return false;
        }
        final String alphaName = Werewolf.getClanManager().getAlpha(playerClan);
        final Player alphaPlayer = this.plugin.getServer().getPlayer(alphaName);
        player.teleport((Entity)alphaPlayer);
        return true;
    }
    
    public boolean CommandInfectionPotion(final Player player) {
        if (player == null) {
            this.plugin.log("You cannot use this command from the console.");
            return false;
        }
        if (!Werewolf.getPermissionsManager().hasPermission(player, "werewolf.potion.infection.create") && !player.isOp()) {
            this.plugin.sendInfo(player, ChatColor.RED + Werewolf.getLanguageManager().getLanguageString(LanguageManager.LANGUAGESTRING.NoPermissionForCommand));
            return false;
        }
        if (player.getItemInHand() != null && player.getItemInHand().getType() != Material.AIR) {
            this.plugin.sendInfo(player, ChatColor.RED + Werewolf.getLanguageManager().getLanguageString(LanguageManager.LANGUAGESTRING.YouMustHaveYourHandsFree));
            return false;
        }
        final ItemStack potionItem = Werewolf.getPotionManager().createWerewolfInfectionPotion();
        player.setItemInHand(potionItem);
        this.plugin.sendInfo(player, ChatColor.AQUA + Werewolf.getLanguageManager().getLanguageString(LanguageManager.LANGUAGESTRING.CreatedWerewolfInfectionPotion));
        return true;
    }
    
    public boolean CommandCurePotion(final Player player) {
        if (player == null) {
            this.plugin.log("You cannot use this command from the console.");
            return false;
        }
        if (!Werewolf.getPermissionsManager().hasPermission(player, "werewolf.potion.cure.create") && !player.isOp()) {
            this.plugin.sendInfo(player, ChatColor.RED + Werewolf.getLanguageManager().getLanguageString(LanguageManager.LANGUAGESTRING.NoPermissionForCommand));
            return false;
        }
        if (player.getItemInHand() != null && player.getItemInHand().getType() != Material.AIR) {
            this.plugin.sendInfo(player, ChatColor.RED + Werewolf.getLanguageManager().getLanguageString(LanguageManager.LANGUAGESTRING.YouMustHaveYourHandsFree));
            return false;
        }
        final ItemStack potionItem = Werewolf.getPotionManager().createWerewolfCurePotion();
        player.setItemInHand(potionItem);
        this.plugin.sendInfo(player, ChatColor.AQUA + Werewolf.getLanguageManager().getLanguageString(LanguageManager.LANGUAGESTRING.CreatedWerewolfCurePotion));
        return true;
    }
    
    public boolean onCommand(final CommandSender sender, final Command cmd, final String label, final String[] args) {
        Player player = null;
        if (sender instanceof Player) {
            player = (Player)sender;
        }
        if (label.equalsIgnoreCase("growl")) {
            this.CommandGrowl(player);
        }
        if (label.equalsIgnoreCase("howl")) {
            this.CommandHowl(player);
        }
        if (!label.equalsIgnoreCase("werewolf") && !label.equalsIgnoreCase("ww")) {
            return true;
        }
        if (args.length == 0) {
            this.CommandInfo(player);
            return true;
        }
        if (args.length == 2) {
            if (args[0].equalsIgnoreCase("toggle")) {
                this.CommandTogglePlayerWerewolf(player, args);
            }
            else if (args[0].equalsIgnoreCase("infect")) {
                this.CommandInfect(player, args);
            }
            else if (args[0].equalsIgnoreCase("addbounty")) {
                this.CommandAddBounty(player, args);
            }
            else if (args[0].equalsIgnoreCase("check")) {
                this.CommandCheck(player, args);
            }
            else {
                this.plugin.sendInfo(player, ChatColor.RED + Werewolf.getLanguageManager().getLanguageString(LanguageManager.LANGUAGESTRING.InvalidCommand));
            }
            return true;
        }
        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("reload")) {
                this.CommandReload(player);
            }
            else if (args[0].equalsIgnoreCase("help")) {
                this.CommandHelp(player);
            }
            else if (args[0].equalsIgnoreCase("clan")) {
                this.CommandClan(player);
            }
            else if (args[0].equalsIgnoreCase("transform")) {
                this.CommandTransform(player);
            }
            else if (args[0].equalsIgnoreCase("toggle")) {
                this.CommandToggleSelfWerewolf(player, args);
            }
            else if (args[0].equalsIgnoreCase("infect")) {
                this.CommandInfectSelf(player, args);
            }
            else if (args[0].equalsIgnoreCase("bounty")) {
                this.CommandBounty(player);
            }
            else if (args[0].equalsIgnoreCase("call")) {
                this.CommandCall(player);
            }
            else if (args[0].equalsIgnoreCase("top")) {
                this.CommandTopHunters(player);
            }
            else if (args[0].equalsIgnoreCase("hunt")) {
                this.CommandHuntWerewolf(player);
            }
            else if (args[0].equalsIgnoreCase("potion")) {
                this.CommandInfectionPotion(player);
            }
            else if (args[0].equalsIgnoreCase("cure")) {
                this.CommandCurePotion(player);
            }
            else if (args[0].equalsIgnoreCase("on")) {
                if (!player.isOp()) {
                    this.plugin.sendInfo(player, ChatColor.RED + Werewolf.getLanguageManager().getLanguageString(LanguageManager.LANGUAGESTRING.NoPermissionForCommand));
                    return false;
                }
                Werewolf.pluginEnabled = true;
                this.plugin.log(" has been enabled");
                if (player != null) {
                    player.sendMessage(ChatColor.AQUA + "Werewolves are now enabled");
                }
                for (final World world : this.plugin.getServer().getWorlds()) {
                    this.plugin.loadSettings();
                    if (this.plugin.isNightInWorld(world)) {
                        for (final Player werewolf : world.getPlayers()) {
                            Werewolf.getWerewolfManager().setWerewolfSkin(werewolf);
                        }
                    }
                }
            }
            else if (args[0].equalsIgnoreCase("off")) {
                if (!player.isOp()) {
                    this.plugin.sendInfo(player, ChatColor.RED + Werewolf.getLanguageManager().getLanguageString(LanguageManager.LANGUAGESTRING.NoPermissionForCommand));
                    return false;
                }
                Werewolf.pluginEnabled = true;
                this.plugin.log(" has been disabled");
                if (player != null) {
                    player.sendMessage(ChatColor.AQUA + "Werewolves are now disabled");
                }
                for (final World world : this.plugin.getServer().getWorlds()) {
                    this.plugin.saveSettings();
                    for (final Player werewolf : world.getPlayers()) {
                        Werewolf.getWerewolfManager().unsetWerewolfSkin(werewolf.getName(), true);
                    }
                }
            }
            else {
                this.plugin.sendInfo(player, ChatColor.RED + Werewolf.getLanguageManager().getLanguageString(LanguageManager.LANGUAGESTRING.InvalidCommand));
            }
            return true;
        }
        return false;
    }
    
    public class TopHuntersComparator implements Comparator<Hunter>
    {
        @Override
        public int compare(final Hunter object1, final Hunter object2) {
            return object2.kills - object1.kills;
        }
    }
    
    public class Hunter
    {
        public String name;
        public int kills;
        
        Hunter(final String hunterName, final int k) {
            super();
            this.name = hunterName;
            this.kills = k;
        }
    }
}

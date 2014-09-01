package com.dogonfire.werewolf;

import org.anjocaido.groupmanager.data.User;
import org.anjocaido.groupmanager.dataholder.OverloadedWorldHolder;
import java.util.Iterator;
import ru.tehkode.permissions.PermissionUser;
import org.bukkit.command.CommandSender;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import de.bananaco.bpermissions.api.util.Calculable;
import de.bananaco.bpermissions.api.World;
import de.bananaco.bpermissions.api.WorldManager;
import com.platymuus.bukkit.permissions.Group;
import org.anjocaido.groupmanager.permissions.AnjoPermissionsHandler;
import de.bananaco.bpermissions.api.ApiLayer;
import de.bananaco.bpermissions.api.util.CalculableType;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import ru.tehkode.permissions.bukkit.PermissionsEx;
import org.anjocaido.groupmanager.GroupManager;
import ru.tehkode.permissions.PermissionManager;
import com.platymuus.bukkit.permissions.PermissionsPlugin;
import org.bukkit.plugin.PluginManager;

public class PermissionsManager
{
    private String pluginName;
    private PluginManager pluginManager;
    private Werewolf plugin;
    private PermissionsPlugin permissionsBukkit;
    private PermissionManager pex;
    private GroupManager groupManager;
    
    public PermissionsManager(final Werewolf p) {
        super();
        this.pluginName = "null";
        this.pluginManager = null;
        this.permissionsBukkit = null;
        this.pex = null;
        this.groupManager = null;
        this.plugin = p;
    }
    
    public void load() {
        this.pluginManager = this.plugin.getServer().getPluginManager();
        if (this.pluginManager.getPlugin("PermissionsBukkit") != null) {
            this.plugin.log("Using PermissionsBukkit.");
            this.pluginName = "PermissionsBukkit";
            this.permissionsBukkit = (PermissionsPlugin)this.pluginManager.getPlugin("PermissionsBukkit");
        }
        else if (this.pluginManager.getPlugin("PermissionsEx") != null) {
            this.plugin.log("Using PermissionsEx.");
            this.pluginName = "PermissionsEx";
            this.pex = PermissionsEx.getPermissionManager();
        }
        else if (this.pluginManager.getPlugin("GroupManager") != null) {
            this.plugin.log("Using GroupManager");
            this.pluginName = "GroupManager";
            this.groupManager = (GroupManager)this.pluginManager.getPlugin("GroupManager");
        }
        else if (this.pluginManager.getPlugin("bPermissions") != null) {
            this.plugin.log("Using bPermissions.");
            this.pluginName = "bPermissions";
        }
        else {
            this.plugin.log("No permissions plugin detected! Defaulting to superperm");
            this.pluginName = "SuperPerm";
        }
    }
    
    public Plugin getPlugin() {
        return (Plugin)this.plugin;
    }
    
    public String getPermissionPluginName() {
        return this.pluginName;
    }
    
    public boolean hasPermission(final Player player, final String node) {
        if (this.pluginName.equals("PermissionsBukkit")) {
            return player.hasPermission(node);
        }
        if (this.pluginName.equals("PermissionsEx")) {
            return this.pex.has(player, node);
        }
        if (this.pluginName.equals("GroupManager")) {
            final AnjoPermissionsHandler handler = this.groupManager.getWorldsHolder().getWorldPermissionsByPlayerName(player.getName());
            return handler != null && handler.permission(player, node);
        }
        if (this.pluginName.equals("bPermissions")) {
            return ApiLayer.hasPermission(player.getWorld().getName(), CalculableType.USER, player.getName(), node);
        }
        if (this.pluginName.equals("zPermissions")) {
            return player.hasPermission(node);
        }
        return player.hasPermission(node);
    }
    
    public boolean isGroup(final String groupName) {
        if (this.pluginName.equals("PermissionsBukkit")) {
            return this.permissionsBukkit.getGroup(groupName) != null;
        }
        if (this.pluginName.equals("PermissionsEx")) {
            return this.pex.getGroup(groupName) != null;
        }
        if (this.pluginName.equals("GroupManager")) {
            return this.permissionsBukkit.getGroup(groupName) != null;
        }
        this.pluginName.equals("bPermissions");
        return false;
    }
    
    public String getGroup(final String playerName) {
        if (this.pluginName.equals("PermissionsBukkit")) {
            if (this.permissionsBukkit.getGroups(playerName) == null) {
                return "";
            }
            if (this.permissionsBukkit.getGroups(playerName).size() == 0) {
                return "";
            }
            return this.permissionsBukkit.getGroups(playerName).get(0).getName();
        }
        else if (this.pluginName.equals("PermissionsEx")) {
            if (this.pex.getUser(playerName).getGroups() == null || this.pex.getUser(playerName).getGroups().length == 0) {
                return "";
            }
            return this.pex.getUser(playerName).getGroups()[0].getName();
        }
        else if (this.pluginName.equals("GroupManager")) {
            final AnjoPermissionsHandler handler = this.groupManager.getWorldsHolder().getWorldPermissionsByPlayerName(playerName);
            if (handler == null) {
                this.plugin.logDebug("PermissionManager(): No handler for player " + playerName);
                return "";
            }
            return handler.getGroup(playerName);
        }
        else {
            if (!this.pluginName.equals("bPermissions")) {
                return "";
            }
            final World w = WorldManager.getInstance().getWorld(playerName);
            if (w == null) {
                return "";
            }
            if (w.getUser(playerName).getGroupsAsString().size() == 0) {
                return "";
            }
            return (String)w.getUser(playerName).getGroupsAsString().toArray()[0];
        }
    }
    
    public String getPrefix(final String playerName) {
        if (this.pluginName.equals("PermissionsBukkit")) {
            return "";
        }
        if (this.pluginName.equals("PermissionsEx")) {
            return this.pex.getUser(this.pluginName).getOwnSuffix();
        }
        if (this.pluginName.equals("GroupManager")) {
            final AnjoPermissionsHandler handler = this.groupManager.getWorldsHolder().getWorldPermissionsByPlayerName(playerName);
            if (handler == null) {
                return "";
            }
            return handler.getUserPrefix(playerName);
        }
        else {
            if (!this.pluginName.equals("bPermissions")) {
                return "";
            }
            final World w = WorldManager.getInstance().getWorld(playerName);
            if (w == null) {
                return "";
            }
            final Calculable c = w.get(playerName, CalculableType.USER);
            return c.getValue("prefix");
        }
    }
    
    public void setGroup(final String playerName, final String groupName) {
        if (this.pluginName.equals("PermissionsBukkit")) {
            if (this.permissionsBukkit.getServer().getPlayer(playerName) != null && this.permissionsBukkit.getServer().getPlayer(playerName).getGameMode() == GameMode.CREATIVE) {
                this.permissionsBukkit.getServer().dispatchCommand((CommandSender)Bukkit.getConsoleSender(), "gm " + playerName);
            }
            this.permissionsBukkit.getServer().dispatchCommand((CommandSender)Bukkit.getConsoleSender(), "perm player setgroup " + playerName + " " + groupName);
        }
        else if (this.pluginName.equals("PermissionsEx")) {
            final PermissionUser user = PermissionsEx.getPermissionManager().getUser(playerName);
            final String[] groups = { groupName };
            user.setGroups(groups);
        }
        else if (this.pluginName.equals("bPermissions")) {
            for (final org.bukkit.World world : this.plugin.getServer().getWorlds()) {
                ApiLayer.setGroup(world.getName(), CalculableType.USER, playerName, groupName);
            }
        }
        else if (this.pluginName.equals("GroupManager")) {
            final OverloadedWorldHolder owh = this.groupManager.getWorldsHolder().getWorldDataByPlayerName(playerName);
            if (owh == null) {
                return;
            }
            final User user2 = owh.getUser(playerName);
            if (user2 == null) {
                this.plugin.log("No player with the name '" + groupName + "'");
                return;
            }
            final org.anjocaido.groupmanager.data.Group group = owh.getGroup(groupName);
            if (group == null) {
                this.plugin.log("No group with the name '" + groupName + "'");
                return;
            }
            user2.setGroup(group);
            final Player p = Bukkit.getPlayer(playerName);
            if (p != null) {
                GroupManager.BukkitPermissions.updatePermissions(p);
            }
        }
    }
}

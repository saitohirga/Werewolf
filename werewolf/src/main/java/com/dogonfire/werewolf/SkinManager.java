package com.dogonfire.werewolf;

import org.bukkit.Location;
import org.bukkit.util.Vector;
import java.util.Iterator;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_7_R1.entity.CraftPlayer;
import net.minecraft.server.v1_7_R1.Packet;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Team;
import org.bukkit.scoreboard.Scoreboard;
import java.util.HashMap;

public class SkinManager
{
    private Werewolf plugin;
    private HashMap<String, WerewolfSkin> skins;
    private Scoreboard scoreBoard;
    private Team werewolfTeam;
    private Objective nameObjective;
    protected int nextID;
    
    SkinManager(final Werewolf p) {
        super();
        this.skins = new HashMap<String, WerewolfSkin>();
        this.scoreBoard = null;
        this.werewolfTeam = null;
        this.nameObjective = null;
        this.nextID = Integer.MIN_VALUE;
        this.plugin = p;
    }
    
    public WerewolfSkin getSkin(final Player player) {
        return this.skins.get(player.getName());
    }
    
    public int getNextAvailableID() {
        return this.nextID++;
    }
    
    public int setWerewolfSkin(final Player player) {
        if (this.skins.containsKey(player.getName())) {
            return this.skins.get(player.getName()).getEntityID();
        }
        final int entityID = this.getNextAvailableID();
        final ClanManager.ClanType clantype = Werewolf.getWerewolfManager().getWerewolfClan(player.getName());
        final WerewolfSkin skin = new WerewolfSkin(Werewolf.getClanManager().getWerewolfAccountForClan(clantype), entityID);
        this.skins.put(player.getName(), skin);
        final Packet spawnPacket = (Packet)skin.getPlayerSpawnPacket(player.getLocation(), (short)player.getItemInHand().getTypeId());
        final Packet infoPacket = (Packet)skin.getPlayerInfoPacket(player, true);
        this.disguiseToWorld(player.getWorld(), player, new Packet[] { spawnPacket, infoPacket });
        return skin.getEntityID();
    }
    
    public void setWerewolfSkin(final Player player, final Player observer) {
        if (!this.skins.containsKey(player.getName())) {
            return;
        }
        final WerewolfSkin skin = this.skins.get(player.getName());
        observer.hidePlayer(player);
        final Packet spawnPacket = (Packet)skin.getPlayerSpawnPacket(player.getLocation(), (short)player.getItemInHand().getTypeId());
        final Packet infoPacket = (Packet)skin.getPlayerInfoPacket(player, true);
        ((CraftPlayer)observer).getHandle().playerConnection.sendPacket(spawnPacket);
        ((CraftPlayer)observer).getHandle().playerConnection.sendPacket(infoPacket);
    }
    
    public void unsetWerewolfSkin(final Player player) {
        if (!this.skins.containsKey(player.getName())) {
            return;
        }
        final WerewolfSkin skin = this.skins.get(player.getName());
        final Packet packet = (Packet)skin.getEntityDestroyPacket();
        final Packet packet2 = (Packet)skin.getPlayerInfoPacket(player, false);
        this.undisguiseToWorld(player.getWorld(), player, new Packet[] { packet, packet2 });
        this.skins.remove(player.getName());
    }
    
    public void removeSkinFromWorld(final World world, final String playerName) {
        if (!this.skins.containsKey(playerName)) {
            return;
        }
        final WerewolfSkin skin = this.skins.get(playerName);
        final Packet packet = (Packet)skin.getEntityDestroyPacket();
        for (final Player observer : world.getPlayers()) {
            if (!observer.getName().equals(playerName)) {
                ((CraftPlayer)observer).getHandle().playerConnection.sendPacket(packet);
            }
        }
        this.skins.remove(playerName);
    }
    
    public void sendMovement(final Player werewolfPlayer, final Vector velocity, final Location to) {
        final WerewolfSkin skin = this.skins.get(werewolfPlayer.getName());
        if (skin == null || to == null) {
            return;
        }
        final MovementValues movement = skin.getMovement(to);
        if (movement.x < -128 || movement.x > 128 || movement.y < -128 || movement.y > 128 || movement.z < -128 || movement.z > 128) {
            final Packet packet = (Packet)skin.getEntityTeleportPacket(to);
            this.sendPacketsToWorld(werewolfPlayer.getWorld(), new Packet[] { packet });
        }
        else if (movement.x == 0 && movement.y == 0 && movement.z == 0) {
            final Packet packet = (Packet)skin.getEntityLookPacket(to);
            final Packet packet2 = (Packet)skin.getHeadRotatePacket(to);
            this.sendPacketsToWorld(werewolfPlayer.getWorld(), new Packet[] { packet, packet2 });
        }
        else {
            final Packet packet = (Packet)skin.getHeadRotatePacket(to);
            final Packet packet2 = (Packet)skin.getEntityTeleportPacket(to);
            this.sendPacketsToWorld(werewolfPlayer.getWorld(), new Packet[] { packet, packet2 });
        }
    }
    
    public void sendWorldChange(final Player player, final World fromWorld) {
        final WerewolfSkin skin = this.getSkin(player);
        final Packet killPacket = (Packet)skin.getEntityDestroyPacket();
        final Packet killListPacket = (Packet)skin.getPlayerInfoPacket(player, false);
        final Packet revivePlayerPacket = (Packet)skin.getPlayerSpawnPacket(player.getLocation(), (short)player.getItemInHand().getTypeId());
        final Packet reviveListPacket = (Packet)skin.getPlayerInfoPacket(player, true);
        if (killListPacket == null) {
            this.undisguiseToWorld(fromWorld, player, new Packet[] { killPacket });
        }
        else {
            this.undisguiseToWorld(fromWorld, player, new Packet[] { killPacket, killListPacket });
        }
        this.disguiseToWorld(player.getWorld(), player, new Packet[] { revivePlayerPacket, reviveListPacket });
    }
    
    public void sendPacketsToWorld(final World world, final Packet[] packet) {
        for (final Player player : world.getPlayers()) {
            for (final Packet p : packet) {
                ((CraftPlayer)player).getHandle().playerConnection.sendPacket(p);
            }
        }
    }
    
    public void disguiseToWorld(final World world, final Player player, final Packet[] packet) {
        for (final Player observer : world.getPlayers()) {
            if (observer.getEntityId() != player.getEntityId() && !observer.getName().equals(player.getName())) {
                this.plugin.logDebug("Disguising " + player.getName() + " to " + observer.getName());
                observer.hidePlayer(player);
                for (final Packet p : packet) {
                    ((CraftPlayer)observer).getHandle().playerConnection.sendPacket(p);
                }
            }
        }
    }
    
    public void showWorldDisguises(final Player observer) {
        for (final String name : Werewolf.getWerewolfManager().getOnlineWerewolves()) {
            final Player werewolf = this.plugin.getServer().getPlayer(name);
            if (werewolf != null && werewolf.getEntityId() != observer.getEntityId() && werewolf.getWorld() == observer.getWorld()) {
                this.setWerewolfSkin(werewolf, observer);
            }
        }
    }
    
    public void undisguiseToWorld(final World world, final Player player, final Packet[] packet) {
        for (final Player observer : world.getPlayers()) {
            if (observer.getEntityId() != player.getEntityId()) {
                this.plugin.logDebug("undisguiseToWorld(): Sending packets and making visible to " + observer.getName());
                for (final Packet p : packet) {
                    ((CraftPlayer)observer).getHandle().playerConnection.sendPacket(p);
                }
                observer.showPlayer(player);
            }
        }
    }
    
    public void visibleToWorld(final Player player) {
        for (final Player observer : player.getWorld().getPlayers()) {
            if (observer.getEntityId() != player.getEntityId()) {
                this.plugin.logDebug("Making " + player.getName() + " visible to " + observer.getName());
                observer.showPlayer(player);
            }
        }
    }
}

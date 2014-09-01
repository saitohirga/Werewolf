package com.dogonfire.werewolf;

import net.minecraft.server.v1_7_R1.PacketPlayOutScoreboardTeam;
import org.bukkit.scoreboard.Team;
import net.minecraft.server.v1_7_R1.PacketPlayOutAnimation;
import net.minecraft.server.v1_7_R1.PacketPlayOutEntityHeadRotation;
import org.bukkit.craftbukkit.v1_7_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import net.minecraft.server.v1_7_R1.PacketPlayOutPlayerInfo;
import net.minecraft.server.v1_7_R1.PacketPlayOutEntityTeleport;
import net.minecraft.server.v1_7_R1.PacketPlayOutRelEntityMoveLook;
import net.minecraft.server.v1_7_R1.PacketPlayOutEntityLook;
import net.minecraft.server.v1_7_R1.PacketPlayOutEntityEquipment;
import net.minecraft.server.v1_7_R1.ItemStack;
import net.minecraft.server.v1_7_R1.PacketPlayOutEntityDestroy;
import java.lang.reflect.Field;
import net.minecraft.util.com.mojang.authlib.GameProfile;
import net.minecraft.server.v1_7_R1.MathHelper;
import net.minecraft.server.v1_7_R1.PacketPlayOutNamedEntitySpawn;
import org.bukkit.Location;
import net.minecraft.server.v1_7_R1.PacketPlayOutEntityMetadata;
import net.minecraft.server.v1_7_R1.Entity;
import net.minecraft.server.v1_7_R1.DataWatcher;

public class WerewolfSkin
{
    private int entityID;
    private String name;
    private int encposX;
    private int encposY;
    private int encposZ;
    private boolean firstSpawnPacket;
    private DataWatcher metadata;
    private boolean burning;
    private byte hasCustomName;
    
    WerewolfSkin(final String accountName, final int id) {
        super();
        this.firstSpawnPacket = false;
        this.burning = false;
        this.hasCustomName = 1;
        this.entityID = id;
        this.name = accountName;
        (this.metadata = new WerewolfDataWatcher(null)).a(0, (Object)0);
        this.metadata.a(5, (Object)accountName);
        this.metadata.a(12, (Object)0);
        this.setCustomName(true);
    }
    
    public static byte degreeToByte(final float degree) {
        return (byte)((int)degree * 256.0f / 360.0f);
    }
    
    public int getEntityID() {
        return this.entityID;
    }
    
    public void setCrouch(final boolean crouched) {
        if (crouched) {
            this.metadata.watch(0, (Object)2);
        }
        else {
            this.metadata.watch(0, (Object)0);
        }
    }
    
    public void setBurning(final boolean burning) {
        if (burning) {
            this.metadata.watch(0, (Object)1);
        }
        else {
            this.metadata.watch(0, (Object)0);
        }
    }
    
    public void setCustomName(final boolean visible) {
    }
    
    public PacketPlayOutEntityMetadata getMetadataPacket() {
        return new PacketPlayOutEntityMetadata(this.entityID, this.metadata, true);
    }
    
    public PacketPlayOutNamedEntitySpawn getPlayerSpawnPacket(final Location loc, final short item) {
        final PacketPlayOutNamedEntitySpawn packet = new PacketPlayOutNamedEntitySpawn();
        final int x = MathHelper.floor(loc.getX() * 32.0);
        final int y = MathHelper.floor(loc.getY() * 32.0);
        final int z = MathHelper.floor(loc.getZ() * 32.0);
        if (!this.firstSpawnPacket) {
            this.encposX = x;
            this.encposY = y;
            this.encposZ = z;
            this.firstSpawnPacket = true;
        }
        try {
            final Field idField = packet.getClass().getDeclaredField("a");
            final Field profileField = packet.getClass().getDeclaredField("b");
            final Field xField = packet.getClass().getDeclaredField("c");
            final Field yField = packet.getClass().getDeclaredField("d");
            final Field zField = packet.getClass().getDeclaredField("e");
            final Field yawField = packet.getClass().getDeclaredField("f");
            final Field pitchField = packet.getClass().getDeclaredField("g");
            final Field itemField = packet.getClass().getDeclaredField("h");
            final Field metadataField = packet.getClass().getDeclaredField("i");
            idField.setAccessible(true);
            profileField.setAccessible(true);
            xField.setAccessible(true);
            yField.setAccessible(true);
            zField.setAccessible(true);
            yawField.setAccessible(true);
            pitchField.setAccessible(true);
            itemField.setAccessible(true);
            metadataField.setAccessible(true);
            idField.set(packet, this.entityID);
            profileField.set(packet, new GameProfile(this.name, this.name));
            xField.set(packet, x);
            yField.set(packet, y);
            zField.set(packet, z);
            yawField.set(packet, degreeToByte(loc.getYaw()));
            pitchField.set(packet, degreeToByte(loc.getPitch()));
            itemField.set(packet, item);
            metadataField.set(packet, this.metadata);
        }
        catch (Exception e) {
            System.out.println("Werewolf could not access a PacketPlayOutNamedEntitySpawn package!");
            e.printStackTrace();
        }
        return packet;
    }
    
    public PacketPlayOutEntityDestroy getEntityDestroyPacket() {
        return new PacketPlayOutEntityDestroy(new int[] { this.entityID });
    }
    
    public PacketPlayOutEntityEquipment getEquipmentChangePacket(final short slot, final ItemStack item) {
        PacketPlayOutEntityEquipment packet;
        if (item == null) {
            packet = new PacketPlayOutEntityEquipment();
            try {
                Field field = packet.getClass().getDeclaredField("a");
                field.setAccessible(true);
                field.setInt(packet, this.entityID);
                field = packet.getClass().getDeclaredField("b");
                field.setAccessible(true);
                field.setShort(packet, slot);
                final Field itemField = packet.getClass().getDeclaredField("c");
                itemField.setAccessible(true);
                itemField.set(packet, null);
            }
            catch (Exception ex) {
                System.out.println("Werewolf was unable to access a PacketPlayOutEntityEquipment field!");
                ex.printStackTrace();
            }
        }
        else {
            packet = new PacketPlayOutEntityEquipment(this.entityID, (int)slot, item);
        }
        return packet;
    }
    
    public PacketPlayOutEntityLook getEntityLookPacket(final Location loc) {
        return new PacketPlayOutEntityLook(this.entityID, degreeToByte(loc.getYaw()), degreeToByte(loc.getPitch()));
    }
    
    public byte[] getYawPitch(final Location loc) {
        final byte yaw = degreeToByte(loc.getYaw());
        final byte pitch = degreeToByte(loc.getPitch());
        return new byte[] { yaw, pitch };
    }
    
    public PacketPlayOutRelEntityMoveLook getEntityMoveLookPacket(final Location look) {
        final byte[] yp = this.getYawPitch(look);
        final MovementValues movement = this.getMovement(look);
        this.encposX += movement.x;
        this.encposY += movement.y;
        this.encposZ += movement.z;
        return new PacketPlayOutRelEntityMoveLook(this.entityID, (byte)movement.x, (byte)movement.y, (byte)movement.z, yp[0], yp[1]);
    }
    
    public MovementValues getMovement(final Location to) {
        final int x = MathHelper.floor(to.getX() * 32.0);
        final int y = MathHelper.floor(to.getY() * 32.0);
        final int z = MathHelper.floor(to.getZ() * 32.0);
        final int diffx = x - this.encposX;
        final int diffy = y - this.encposY;
        final int diffz = z - this.encposZ;
        return new MovementValues(diffx, diffy, diffz, degreeToByte(to.getYaw()), degreeToByte(to.getPitch()));
    }
    
    public PacketPlayOutEntityTeleport getEntityTeleportPacket(final Location loc) {
        final int x = MathHelper.floor(32.0 * loc.getX());
        final int y = MathHelper.floor(32.0 * loc.getY());
        final int z = MathHelper.floor(32.0 * loc.getZ());
        final PacketPlayOutEntityTeleport packet = new PacketPlayOutEntityTeleport();
        try {
            Field field = packet.getClass().getDeclaredField("a");
            field.setAccessible(true);
            field.setInt(packet, this.entityID);
            field.setAccessible(!field.isAccessible());
            field = packet.getClass().getDeclaredField("b");
            field.setAccessible(true);
            field.setInt(packet, x);
            field.setAccessible(!field.isAccessible());
            field = packet.getClass().getDeclaredField("c");
            field.setAccessible(true);
            field.setInt(packet, y);
            field = packet.getClass().getDeclaredField("d");
            field.setAccessible(true);
            field.setInt(packet, z);
            field = packet.getClass().getDeclaredField("e");
            field.setAccessible(true);
            field.setByte(packet, degreeToByte(loc.getYaw()));
            field = packet.getClass().getDeclaredField("f");
            field.setAccessible(true);
            field.setByte(packet, degreeToByte(loc.getPitch()));
        }
        catch (Exception ex) {
            System.out.println("Werewolf was unable to access a PacketPlayOutEntityTeleport field!");
            ex.printStackTrace();
        }
        if (!this.firstSpawnPacket) {
            this.encposX = x;
            this.encposY = y;
            this.encposZ = z;
        }
        return packet;
    }
    
    public PacketPlayOutPlayerInfo getPlayerInfoPacket() {
        return this.getPlayerInfoPacket(null, false);
    }
    
    public PacketPlayOutPlayerInfo getPlayerInfoPacket(final Player player, final boolean show) {
        PacketPlayOutPlayerInfo packet = null;
        int ping;
        if (show) {
            ping = ((CraftPlayer)player).getHandle().ping;
        }
        else {
            ping = 9999;
        }
        packet = new PacketPlayOutPlayerInfo(this.name, show, ping);
        return packet;
    }
    
    public PacketPlayOutEntityHeadRotation getHeadRotatePacket(final Location loc) {
        final PacketPlayOutEntityHeadRotation packet = new PacketPlayOutEntityHeadRotation();
        try {
            Field field = packet.getClass().getDeclaredField("a");
            field.setAccessible(true);
            field.setInt(packet, this.entityID);
            field = packet.getClass().getDeclaredField("b");
            field.setAccessible(true);
            field.setByte(packet, degreeToByte(loc.getYaw()));
        }
        catch (Exception ex) {
            System.out.println("Werewolf was unable to access a PacketPlayOutEntityHeadRotation field!");
            ex.printStackTrace();
        }
        return packet;
    }
    
    public PacketPlayOutAnimation getAnimationPacket(final int animation) {
        final PacketPlayOutAnimation packet = new PacketPlayOutAnimation();
        try {
            final Field idField = packet.getClass().getDeclaredField("a");
            final Field animationField = packet.getClass().getDeclaredField("b");
            idField.setAccessible(true);
            animationField.setAccessible(true);
            idField.set(packet, this.entityID);
            animationField.set(packet, animation);
        }
        catch (Exception ex) {
            System.out.println("Werewolf was unable to access a PacketPlayOutAnimation field!");
            ex.printStackTrace();
        }
        return packet;
    }
    
    public PacketPlayOutScoreboardTeam getScoreBoardTeamPacket(final Team team) {
        final PacketPlayOutScoreboardTeam packet = new PacketPlayOutScoreboardTeam();
        return packet;
    }
}

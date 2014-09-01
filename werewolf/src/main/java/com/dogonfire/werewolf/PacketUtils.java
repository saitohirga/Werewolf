package com.dogonfire.werewolf;

import net.minecraft.server.v1_7_R1.PacketPlayOutRemoveEntityEffect;
import org.bukkit.potion.PotionEffectType;
import java.lang.reflect.Field;
import net.minecraft.server.v1_7_R1.Packet;
import org.bukkit.craftbukkit.v1_7_R1.entity.CraftPlayer;
import net.minecraft.server.v1_7_R1.PacketPlayOutEntityEffect;
import org.bukkit.potion.PotionEffect;
import org.bukkit.entity.Player;

public class PacketUtils
{
    private Werewolf plugin;
    
    PacketUtils(final Werewolf p) {
        super();
        this.plugin = p;
    }
    
    public void addPotionEffectNoGraphic(final Player player, final PotionEffect pe) {
        final PacketPlayOutEntityEffect pm = new PacketPlayOutEntityEffect();
        try {
            Field field = pm.getClass().getDeclaredField("a");
            field.setAccessible(true);
            field.setInt(pm, player.getEntityId());
            field = pm.getClass().getDeclaredField("b");
            field.setAccessible(true);
            field.setByte(pm, (byte)(pe.getType().getId() & 0xFF));
            field = pm.getClass().getDeclaredField("c");
            field.setAccessible(true);
            field.setByte(pm, (byte)(pe.getAmplifier() & 0xFF));
            short duration = Short.MAX_VALUE;
            if (pe.getDuration() < 32767) {
                duration = (short)pe.getDuration();
            }
            final Field field2 = pm.getClass().getDeclaredField("d");
            field2.setAccessible(true);
            field2.setShort(pm, duration);
        }
        catch (Exception e) {
            System.out.println("Werewolf could not access a PacketPlayOutEntityEffect package!");
            e.printStackTrace();
        }
        ((CraftPlayer)player).getHandle().playerConnection.sendPacket((Packet)pm);
    }
    
    public void removePotionEffectNoGraphic(final Player player, final PotionEffectType pe) {
        final PacketPlayOutRemoveEntityEffect pr = new PacketPlayOutRemoveEntityEffect();
        try {
            Field field = pr.getClass().getDeclaredField("a");
            field.setAccessible(true);
            field.setInt(pr, player.getEntityId());
            field.setAccessible(!field.isAccessible());
            field = pr.getClass().getDeclaredField("b");
            field.setAccessible(true);
            field.setByte(pr, (byte)pe.getId());
            field.setAccessible(!field.isAccessible());
        }
        catch (Exception e) {
            System.out.println("Werewolf could not access a PacketPlayOutEntityEffect package!");
            e.printStackTrace();
        }
        ((CraftPlayer)player).getHandle().playerConnection.sendPacket((Packet)pr);
    }
}

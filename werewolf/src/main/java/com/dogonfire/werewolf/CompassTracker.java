package com.dogonfire.werewolf;

import org.bukkit.entity.Player;
import org.bukkit.Server;
import java.util.Iterator;
import org.bukkit.plugin.Plugin;
import java.util.ArrayList;

public class CompassTracker implements Runnable
{
    private static ArrayList<String> watchList;
    private static int taskId;
    private static Werewolf plugin;
    private static long updateRate;
    
    static {
        CompassTracker.watchList = new ArrayList<String>();
        CompassTracker.taskId = -2;
        CompassTracker.updateRate = 100L;
    }
    
    public static void setPlugin(final Werewolf newPlugin) {
        CompassTracker.plugin = newPlugin;
    }
    
    public static void setUpdateRate(final long newUpdateRate) {
        CompassTracker.updateRate = newUpdateRate;
        stop();
        start();
    }
    
    public static boolean start() {
        if (!isRunning()) {
            CompassTracker.taskId = CompassTracker.plugin.getServer().getScheduler().scheduleSyncRepeatingTask((Plugin)CompassTracker.plugin, (Runnable)new CompassTracker(), 40L, CompassTracker.updateRate);
            if (isRunning()) {
                return true;
            }
        }
        return false;
    }
    
    public static boolean isRunning() {
        return CompassTracker.taskId > 0;
    }
    
    public static void stop() {
        if (isRunning()) {
            CompassTracker.plugin.getServer().getScheduler().cancelTask(CompassTracker.taskId);
        }
        CompassTracker.taskId = -2;
    }
    
    public static boolean hasWatcher(final String watcherName) {
        return CompassTracker.watchList.contains(watcherName);
    }
    
    public static void addWatcher(final String watcherName) {
        CompassTracker.watchList.add(watcherName);
        start();
    }
    
    public static void removePlayer(final String playerName) {
        if (CompassTracker.watchList.contains(playerName)) {
            final Iterator iterator = CompassTracker.watchList.iterator();
            while (iterator.hasNext()) {
                if (iterator.next() == playerName) {
                    iterator.remove();
                }
            }
        }
        removeWatcher(playerName);
    }
    
    public static void removeWatcher(final String playerName) {
        if (CompassTracker.watchList.contains(playerName)) {
            CompassTracker.watchList.remove(playerName);
        }
        if (CompassTracker.watchList.isEmpty()) {
            stop();
        }
    }
    
    @Override
    public void run() {
        final Server server = CompassTracker.plugin.getServer();
        for (final String playerName : CompassTracker.watchList) {
            final Player watcher = server.getPlayer(playerName);
            if (watcher != null) {
                final Player watched = Werewolf.getWerewolfManager().getNearestWerewolf(watcher.getName());
                if (watched == null) {
                    watcher.setCompassTarget(watcher.getLocation());
                    return;
                }
                watcher.setCompassTarget(watched.getLocation());
                watcher.saveData();
            }
        }
    }
}

package com.dogonfire.werewolf;

import java.util.Iterator;
import java.io.IOException;
import java.util.logging.Level;
import org.bukkit.Bukkit;

class Metrics$1 implements Runnable {
    private boolean firstPost = true;
    
    @Override
    public void run() {
        try {
            synchronized (Metrics.access$0(Metrics.this)) {
                if (Metrics.this.isOptOut() && Metrics.access$1(Metrics.this) > 0) {
                    Metrics.access$2(Metrics.this).getServer().getScheduler().cancelTask(Metrics.access$1(Metrics.this));
                    Metrics.access$3(Metrics.this, -1);
                    for (final Graph graph : Metrics.access$4(Metrics.this)) {
                        graph.onOptOut();
                    }
                }
            }
            // monitorexit(Metrics.access$0(this.this$0))
            Metrics.access$5(Metrics.this, !this.firstPost);
            this.firstPost = false;
        }
        catch (IOException e) {
            Bukkit.getLogger().log(Level.INFO, "[Metrics] " + e.getMessage());
        }
    }
}
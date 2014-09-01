package com.dogonfire.werewolf.tasks;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import com.dogonfire.werewolf.Werewolf;

public class RenameTask implements Runnable {
	private Werewolf plugin;
	private Player player;
	private String displayName;
	private String listName;

	public RenameTask(final Werewolf instance, final Player p) {
		super();
		this.player = null;
		this.displayName = null;
		this.listName = null;
		this.plugin = instance;
		this.player = p;
	}

	public RenameTask(final Werewolf instance, final Player p,
			final String listname, final String displayname) {
		super();
		this.player = null;
		this.displayName = null;
		this.listName = null;
		this.plugin = instance;
		this.player = p;
		this.displayName = displayname;
		this.listName = listname;
	}

	public void renameToWerewolf(final Player player) {
		int n = 0;
		boolean renamed = false;
		player.setCustomName(ChatColor.GOLD + "Werewolf");
		player.setCustomNameVisible(true);
		while (!renamed) {
			renamed = true;
			try {
				player.setPlayerListName(ChatColor.GOLD + "Werewolf" + n);
			} catch (Exception ex) {
				++n;
				renamed = false;
				if (!this.plugin.debug) {
					continue;
				}
				this.plugin.log("Could not set player list name for player ID "
						+ player.getEntityId());
			}
		}
	}

	@Override
	public void run() {
		if (this.displayName == null) {
			this.renameToWerewolf(this.player);
		} else {
			try {
				this.player.setPlayerListName(this.displayName);
				this.player.setCustomNameVisible(false);
			} catch (Exception ex) {
				this.plugin.logDebug("Could not rename "
						+ this.player.getName() + " to " + this.displayName);
			}
		}
	}
}

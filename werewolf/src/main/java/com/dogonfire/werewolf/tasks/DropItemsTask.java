package com.dogonfire.werewolf.tasks;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import com.dogonfire.werewolf.Werewolf;

public class DropItemsTask implements Runnable {
	private Werewolf plugin;
	private Player player;

	private void dropHandItem(final Player player) {
		final PlayerInventory inventory = player.getInventory();
		final ItemStack stack = inventory.getItemInHand();
		if (stack == null || stack.getAmount() == 0
				|| stack.getType().equals((Object) Material.AIR)) {
			return;
		}
		if (this.plugin.dropArmorOnTransform) {
			player.getWorld().dropItemNaturally(player.getLocation(), stack);
			inventory.remove(stack);
		} else {
			final int slot = player.getInventory().firstEmpty();
			if (slot > -1) {
				player.getInventory().setItem(slot, stack);
			} else {
				player.getWorld()
						.dropItemNaturally(player.getLocation(), stack);
			}
		}
	}

	public DropItemsTask(final Werewolf instance, final Player p) {
		super();
		this.player = null;
		this.plugin = instance;
		this.player = p;
		this.plugin.log("CALLING DROPITEMSTASK!");
	}

	@Override
	public void run() {
		this.dropHandItem(this.player);
	}
}

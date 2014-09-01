package com.dogonfire.werewolf.tasks;

import org.bukkit.potion.PotionEffect;
import org.bukkit.entity.Player;
import com.dogonfire.werewolf.Werewolf;

public class PotionEffectTask implements Runnable {
	private Werewolf plugin;
	private Player player;
	private PotionEffect potionEffect;

	public PotionEffectTask(final Werewolf instance, final Player p,
			final PotionEffect pe) {
		super();
		this.player = null;
		this.potionEffect = null;
		this.plugin = instance;
		this.player = p;
		this.potionEffect = pe;
	}

	@Override
	public void run() {
		if (this.player == null) {
			this.plugin.logDebug("PotionEffectTask::Run(): Player is null!");
			return;
		}
		Werewolf.pu.addPotionEffectNoGraphic(this.player, this.potionEffect);
	}
}

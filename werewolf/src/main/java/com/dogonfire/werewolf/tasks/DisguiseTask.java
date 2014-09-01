package com.dogonfire.werewolf.tasks;

import com.dogonfire.werewolf.ClanManager;
import com.dogonfire.werewolf.LanguageManager;
import org.bukkit.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import net.h31ix.anticheat.api.AnticheatAPI;
import net.h31ix.anticheat.manage.CheckType;
import fr.neatmonster.nocheatplus.hooks.NCPExemptionManager;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import com.dogonfire.werewolf.Werewolf;

public class DisguiseTask implements Runnable {
	private Werewolf plugin;
	private Player player;
	private static/* synthetic */int[] $SWITCH_TABLE$com$dogonfire$werewolf$ClanManager$ClanType;

	public DisguiseTask(final Werewolf instance, final Player p) {
		super();
		this.player = null;
		this.plugin = instance;
		this.player = p;
	}

	private void dropArmor() {
		final PlayerInventory inventory = this.player.getInventory();
		ItemStack[] armorContents;
		for (int length = (armorContents = inventory.getArmorContents()).length, i = 0; i < length; ++i) {
			final ItemStack stack = armorContents[i];
			if (stack != null && !stack.getType().equals((Object) Material.AIR)
					&& stack.getAmount() != 0) {
				if (Werewolf.getWerewolfManager().getNumberOfTransformations(
						this.player.getName()) < this.plugin.transformsForNoDropItems) {
					this.player.getWorld().dropItemNaturally(
							this.player.getLocation(), stack);
					inventory.remove(stack);
				} else {
					final int slot = this.player.getInventory().firstEmpty();
					if (slot > -1) {
						this.player.getInventory().setItem(slot, stack);
					} else {
						this.player.getWorld().dropItemNaturally(
								this.player.getLocation(), stack);
						inventory.remove(stack);
					}
				}
			}
		}
		this.player.getInventory().setArmorContents(
				new ItemStack[] { new ItemStack(Material.AIR),
						new ItemStack(Material.AIR),
						new ItemStack(Material.AIR),
						new ItemStack(Material.AIR) });
	}

	private void dropHandItem() {
		final PlayerInventory inventory = this.player.getInventory();
		final ItemStack stack = inventory.getItemInHand();
		if (stack == null || stack.getAmount() == 0
				|| stack.getType().equals((Object) Material.AIR)) {
			return;
		}
		if (Werewolf.getWerewolfManager().getNumberOfTransformations(
				this.player.getName()) < this.plugin.transformsForNoDropItems) {
			this.player.getWorld().dropItemNaturally(this.player.getLocation(),
					stack);
			inventory.remove(stack);
		} else {
			final int slot = this.player.getInventory().firstEmpty();
			if (slot > -1) {
				this.player.getInventory().setItem(slot, stack);
				this.player.setItemInHand((ItemStack) null);
			} else {
				this.player.getWorld().dropItemNaturally(
						this.player.getLocation(), stack);
				inventory.remove(stack);
			}
		}
	}

	@Override
	public void run() {
		if (this.player == null) {
			this.plugin.logDebug("DisguiseTask::Run(): Player is null!");
			return;
		}
		if (Werewolf.getWerewolfManager()
				.hasWerewolfSkin(this.player.getName())) {
			return;
		}
		if (this.plugin.noCheatPlusEnabled) {
			NCPExemptionManager.exemptPermanently(this.player);
		}
		if (this.plugin.antiCheatEnabled) {
			AnticheatAPI.exemptPlayer(this.player, CheckType.FLY);
			AnticheatAPI.exemptPlayer(this.player, CheckType.SPEED);
		}
		final ClanManager.ClanType clan = Werewolf.getWerewolfManager()
				.getWerewolfClan(this.player.getName());
		Werewolf.server.getScheduler().scheduleSyncDelayedTask(
				(Plugin) this.plugin,
				(Runnable) new PotionEffectTask(this.plugin, this.player,
						new PotionEffect(PotionEffectType.CONFUSION, 100, 1)),
				1L);
		Werewolf.server.getScheduler().scheduleSyncDelayedTask(
				(Plugin) this.plugin,
				(Runnable) new PotionEffectTask(this.plugin, this.player,
						new PotionEffect(PotionEffectType.HUNGER, 32000, 2)),
				8L);
		Werewolf.server.getScheduler().scheduleSyncDelayedTask(
				(Plugin) this.plugin,
				(Runnable) new PotionEffectTask(this.plugin, this.player,
						new PotionEffect(PotionEffectType.NIGHT_VISION, 32000,
								1)), 16L);
		switch ($SWITCH_TABLE$com$dogonfire$werewolf$ClanManager$ClanType()[clan
				.ordinal()]) {
		case 3: {
			Werewolf.server.getScheduler().scheduleSyncDelayedTask(
					(Plugin) this.plugin,
					(Runnable) new PotionEffectTask(this.plugin, this.player,
							new PotionEffect(PotionEffectType.JUMP, 32000, 3)),
					16L);
			Werewolf.server.getScheduler()
					.scheduleSyncDelayedTask(
							(Plugin) this.plugin,
							(Runnable) new PotionEffectTask(this.plugin,
									this.player, new PotionEffect(
											PotionEffectType.SPEED, 32000, 3)),
							32L);
			this.player.setWalkSpeed(1.0f);
			break;
		}
		case 1: {
			Werewolf.server.getScheduler().scheduleSyncDelayedTask(
					(Plugin) this.plugin,
					(Runnable) new PotionEffectTask(this.plugin, this.player,
							new PotionEffect(PotionEffectType.JUMP, 32000, 2)),
					16L);
			Werewolf.server.getScheduler()
					.scheduleSyncDelayedTask(
							(Plugin) this.plugin,
							(Runnable) new PotionEffectTask(this.plugin,
									this.player, new PotionEffect(
											PotionEffectType.SPEED, 32000, 1)),
							32L);
			Werewolf.server.getScheduler().scheduleSyncDelayedTask(
					(Plugin) this.plugin,
					(Runnable) new PotionEffectTask(this.plugin, this.player,
							new PotionEffect(PotionEffectType.REGENERATION,
									32000, 2)), 64L);
			this.player.setWalkSpeed(0.5f);
			break;
		}
		case 2: {
			Werewolf.server.getScheduler().scheduleSyncDelayedTask(
					(Plugin) this.plugin,
					(Runnable) new PotionEffectTask(this.plugin, this.player,
							new PotionEffect(PotionEffectType.JUMP, 32000, 2)),
					16L);
			Werewolf.server.getScheduler()
					.scheduleSyncDelayedTask(
							(Plugin) this.plugin,
							(Runnable) new PotionEffectTask(this.plugin,
									this.player, new PotionEffect(
											PotionEffectType.SPEED, 32000, 1)),
							32L);
			Werewolf.server.getScheduler().scheduleSyncDelayedTask(
					(Plugin) this.plugin,
					(Runnable) new PotionEffectTask(this.plugin, this.player,
							new PotionEffect(PotionEffectType.INCREASE_DAMAGE,
									32000, 2)), 64L);
			this.player.setWalkSpeed(0.5f);
			break;
		}
		}
		this.dropArmor();
		this.dropHandItem();
		Werewolf.getSkinManager().setWerewolfSkin(this.player);
		Werewolf.getWerewolfManager().pushPlayerData(this.player);
		if (this.plugin.isFullMoonInWorld(this.player.getWorld())
				&& !Werewolf.getWerewolfManager().hasRecentTransform(
						this.player.getName())) {
			Werewolf.getWerewolfManager()
					.incrementNumberOfFullMoonTransformations(
							this.player.getName());
		}
		Werewolf.getWerewolfManager().setLastTransformation(
				this.player.getName());
		if (this.plugin.healthBarEnabled) {
			Bukkit.getScoreboardManager();
		}
		if (this.plugin.useWerewolfGroupName) {
			final String originalGroup = Werewolf.getPermissionsManager()
					.getGroup(this.player.getName());
			Werewolf.getWerewolfManager().setOriginalPermissionGroup(
					this.player.getName(), originalGroup);
			Werewolf.getPermissionsManager().setGroup(this.player.getName(),
					this.plugin.werewolfGroupName);
		}
		try {
			this.player.setPlayerListName(ChatColor.GOLD + "Werewolf");
		} catch (Exception ex1) {
			int n = 1;
			Boolean renamed = false;
			while (!renamed) {
				renamed = true;
				try {
					this.player.setPlayerListName(ChatColor.GOLD + "Werewolf"
							+ n);
				} catch (Exception ex2) {
					++n;
					renamed = false;
				}
			}
		}
		Werewolf.getLanguageManager().setAmount(
				new StringBuilder().append(
						Werewolf.getWerewolfManager()
								.getNumberOfTransformations(
										this.player.getName())).toString());
		this.player.sendMessage(ChatColor.LIGHT_PURPLE
				+ Werewolf.getLanguageManager().getLanguageString(
						LanguageManager.LANGUAGESTRING.Transform));
		if (Werewolf.getPermissionsManager().hasPermission(this.player,
				"werewolf.howl")) {
			final String message = Werewolf.getLanguageManager()
					.getLanguageString(
							LanguageManager.LANGUAGESTRING.InfoCommandHowl);
			this.player.sendMessage(ChatColor.AQUA + message);
		}
		if (Werewolf.getPermissionsManager().hasPermission(this.player,
				"werewolf.growl")) {
			final String message = Werewolf.getLanguageManager()
					.getLanguageString(
							LanguageManager.LANGUAGESTRING.InfoCommandGrowl);
			this.player.sendMessage(ChatColor.AQUA + message);
		}
		this.plugin.log(String.valueOf(this.player.getName())
				+ " turned into a werewolf!");
	}

	static/* synthetic */int[] $SWITCH_TABLE$com$dogonfire$werewolf$ClanManager$ClanType() {
		final int[] $switch_TABLE$com$dogonfire$werewolf$ClanManager$ClanType = DisguiseTask.$SWITCH_TABLE$com$dogonfire$werewolf$ClanManager$ClanType;
		if ($switch_TABLE$com$dogonfire$werewolf$ClanManager$ClanType != null) {
			return $switch_TABLE$com$dogonfire$werewolf$ClanManager$ClanType;
		}
		final int[] $switch_TABLE$com$dogonfire$werewolf$ClanManager$ClanType2 = new int[ClanManager.ClanType
				.values().length];
		try {
			$switch_TABLE$com$dogonfire$werewolf$ClanManager$ClanType2[ClanManager.ClanType.Potion
					.ordinal()] = 3;
		} catch (NoSuchFieldError noSuchFieldError) {
		}
		try {
			$switch_TABLE$com$dogonfire$werewolf$ClanManager$ClanType2[ClanManager.ClanType.WerewolfBite
					.ordinal()] = 2;
		} catch (NoSuchFieldError noSuchFieldError2) {
		}
		try {
			$switch_TABLE$com$dogonfire$werewolf$ClanManager$ClanType2[ClanManager.ClanType.WildBite
					.ordinal()] = 1;
		} catch (NoSuchFieldError noSuchFieldError3) {
		}
		return DisguiseTask.$SWITCH_TABLE$com$dogonfire$werewolf$ClanManager$ClanType = $switch_TABLE$com$dogonfire$werewolf$ClanManager$ClanType2;
	}
}

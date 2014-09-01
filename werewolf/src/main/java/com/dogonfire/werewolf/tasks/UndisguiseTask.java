package com.dogonfire.werewolf.tasks;

import java.util.Iterator;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.entity.Player;
import org.bukkit.Bukkit;
import org.bukkit.potion.PotionEffectType;
import net.h31ix.anticheat.api.AnticheatAPI;
import net.h31ix.anticheat.manage.CheckType;
import fr.neatmonster.nocheatplus.hooks.NCPExemptionManager;
import org.bukkit.World;
import com.dogonfire.werewolf.Werewolf;

public class UndisguiseTask implements Runnable {
	public Werewolf plugin;
	public String playerName;
	private boolean makeVisible;
	private boolean forever;
	private World world;
	private int playerEntityID;

	public UndisguiseTask(final Werewolf instance, final World world,
			final String playerName, final int playerEntityID,
			final boolean visible, final boolean forever) {
		super();
		this.plugin = instance;
		this.playerName = playerName;
		this.makeVisible = visible;
		this.forever = forever;
		this.playerEntityID = playerEntityID;
		this.world = world;
	}

	@Override
	public void run() {
		final Player player = this.plugin.getServer()
				.getPlayer(this.playerName);
		if (player != null) {
			if (this.plugin.noCheatPlusEnabled) {
				NCPExemptionManager.unexempt(player);
			}
			if (this.plugin.antiCheatEnabled) {
				AnticheatAPI.unexemptPlayer(player, CheckType.FLY);
				AnticheatAPI.unexemptPlayer(player, CheckType.SPEED);
			}
			Werewolf.pu.removePotionEffectNoGraphic(player,
					PotionEffectType.CONFUSION);
			Werewolf.pu.removePotionEffectNoGraphic(player,
					PotionEffectType.SPEED);
			Werewolf.pu.removePotionEffectNoGraphic(player,
					PotionEffectType.HUNGER);
			Werewolf.pu.removePotionEffectNoGraphic(player,
					PotionEffectType.NIGHT_VISION);
			Werewolf.pu.removePotionEffectNoGraphic(player,
					PotionEffectType.INCREASE_DAMAGE);
			Werewolf.pu.removePotionEffectNoGraphic(player,
					PotionEffectType.REGENERATION);
			player.setWalkSpeed(0.2f);
			if (!this.plugin.usePounce) {
				Werewolf.pu.removePotionEffectNoGraphic(player,
						PotionEffectType.JUMP);
			} else {
				player.setAllowFlight(false);
			}
			final String playerListName = Werewolf.getWerewolfManager()
					.getPlayerListName(player);
			if (playerListName == null) {
				this.plugin.logDebug("Could not find playerlist name for "
						+ player.getName());
			} else {
				player.setPlayerListName(playerListName);
			}
			if (this.makeVisible) {
				Werewolf.getSkinManager().unsetWerewolfSkin(player);
			}
			if (this.plugin.healthBarEnabled) {
				final ScoreboardManager manager = Bukkit.getScoreboardManager();
				player.setScoreboard(manager.getMainScoreboard());
			}
		}
		if (!this.makeVisible) {
			if (this.world != null) {
				Werewolf.getSkinManager().removeSkinFromWorld(this.world,
						this.playerName);
			} else {
				for (final World world : this.plugin.getServer().getWorlds()) {
					Werewolf.getSkinManager().removeSkinFromWorld(world,
							this.playerName);
				}
			}
		}
		Werewolf.getWerewolfManager().popPlayerData(this.playerName);
		if (this.plugin.useWerewolfGroupName) {
			final String groupName = Werewolf.getWerewolfManager()
					.getOriginalPermissionGroup(player.getName());
			if (groupName != null
					&& !groupName.equals(this.plugin.werewolfGroupName)) {
				this.plugin.logDebug("Putting " + player.getName()
						+ " into group " + groupName);
				Werewolf.getPermissionsManager().setGroup(this.playerName,
						groupName);
			}
		}
		if (this.forever) {
			Werewolf.getWerewolfManager().setNoWerewolf(this.playerName);
		} else {
			Werewolf.getWerewolfManager().setHumanForm(this.playerName);
		}
		Werewolf.getWerewolfManager().clearPackWolves(this.playerName);
	}
}

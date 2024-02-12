package me.MrGraycat.eglow.event;

import me.MrGraycat.eglow.EGlow;
import me.MrGraycat.eglow.Util.enums.EnumUtil;
import me.MrGraycat.eglow.Util.text.ChatUtil;
import me.MrGraycat.eglow.config.EGlowMessageConfig;
import me.MrGraycat.eglow.data.DataManager;
import me.MrGraycat.eglow.data.EGlowPlayer;
import me.MrGraycat.eglow.config.EGlowMainConfig.MainConfig;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

public class EGlowEventListener113AndAbove implements Listener {

	public EGlowEventListener113AndAbove() {
		EGlow.getInstance().getServer().getPluginManager().registerEvents(this, EGlow.getInstance());
	}

	@EventHandler
	public void PlayerPotionEvent(EntityPotionEffectEvent event) {
		Entity entity = event.getEntity();

		if (entity instanceof Player) {
			EGlowPlayer eGlowPlayer = DataManager.getEGlowPlayer((Player) entity);

			new BukkitRunnable() {
				@Override
				public void run() {
					if (eGlowPlayer == null)
						return;

					if (!MainConfig.SETTINGS_DISABLE_GLOW_WHEN_INVISIBLE.getBoolean()) {
						if (eGlowPlayer.getGlowDisableReason().equals(EnumUtil.GlowDisableReason.INVISIBLE)) {
							eGlowPlayer.setGlowDisableReason(EnumUtil.GlowDisableReason.NONE);
						}
						return;
					}

					if (event.getNewEffect() != null && event.getNewEffect().getType().equals(PotionEffectType.INVISIBILITY)) {
						if (eGlowPlayer.isGlowing()) {
							eGlowPlayer.disableGlow(false);
							eGlowPlayer.setGlowDisableReason(EnumUtil.GlowDisableReason.INVISIBLE);

							if (MainConfig.SETTINGS_NOTIFICATIONS_INVISIBILITY.getBoolean())
								ChatUtil.sendMsg(eGlowPlayer.getPlayer(), EGlowMessageConfig.Message.INVISIBILITY_BLOCKED.get(eGlowPlayer.getPlayer()), true);
							return;
						}
					}

					if (event.getNewEffect() == null && event.getOldEffect() != null && event.getOldEffect().getType().equals(PotionEffectType.INVISIBILITY)) {
						if (eGlowPlayer.getGlowDisableReason().equals(EnumUtil.GlowDisableReason.INVISIBLE)) {
							if (eGlowPlayer.setGlowDisableReason(EnumUtil.GlowDisableReason.NONE).equals(EnumUtil.GlowDisableReason.NONE)) {
								eGlowPlayer.activateGlow();

								if (MainConfig.SETTINGS_NOTIFICATIONS_INVISIBILITY.getBoolean())
									ChatUtil.sendMsg(eGlowPlayer.getPlayer(), EGlowMessageConfig.Message.INVISIBILITY_ALLOWED.get(eGlowPlayer.getPlayer()), true);
							}
						}
					}
				}
			}.runTaskLaterAsynchronously(EGlow.getInstance(), 1L);
		}
	}
}
package me.MrGraycat.eglow.command.subcommands.admin;

import me.MrGraycat.eglow.Util.enums.EnumUtil;
import me.MrGraycat.eglow.command.SubCommand;
import me.MrGraycat.eglow.config.EGlowMainConfig.MainConfig;
import me.MrGraycat.eglow.config.EGlowMessageConfig.Message;
import me.MrGraycat.eglow.data.DataManager;
import me.MrGraycat.eglow.data.EGlowEffect;
import me.MrGraycat.eglow.data.EGlowPlayer;
import me.MrGraycat.eglow.Util.text.ChatUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Set;

public class SetCommand extends SubCommand {

	@Override
	public String getName() {
		return "set";
	}

	@Override
	public String getPermission() {
		return "eglow.command.set";
	}

	@Override
	public String[] getSyntax() {
		return new String[]{"/eGlow set <player/npc> <color>",
				"/eGlow set <player/npc> blink <color> <speed>",
				"/eGlow set <player/npc> <effect> <speed>",
				"/eGlow set <player/npc> glowonjoin <true/false>"};
	}

	@Override
	public boolean isPlayerCmd() {
		return false;
	}

	@Override
	public void perform(CommandSender sender, EGlowPlayer eGlowPlayer, String[] args) {
		Player pSender = (sender instanceof Player player) ? player : null;

		Set<EGlowPlayer> eGlowTargets = getTarget(sender, args);
		boolean isSilent = (args[args.length - 1].equalsIgnoreCase("-s"));

		if (eGlowTargets.isEmpty()) {
			sendSyntax(sender);
			return;
		}

		for (EGlowPlayer eGlowTarget : eGlowTargets) {
			EGlowEffect eGlowEffect = null;

			if (eGlowTarget == null)
				continue;

			if (!isSilent) {
				switch (args.length) {
					case (3):
						eGlowEffect = DataManager.getEGlowEffect(args[2].toLowerCase().replace("off", "none").replace("disable", "none"));
						break;
					case (4):
						if (args[2].equalsIgnoreCase("glowonjoin")) {
							eGlowTarget.setGlowOnJoin(Boolean.parseBoolean(args[3].toLowerCase()));
							ChatUtil.sendMsg(sender, Message.OTHER_GLOW_ON_JOIN_CONFIRM.get(eGlowTarget, args[3].toLowerCase()), true);
							continue;

						}
						eGlowEffect = DataManager.getEGlowEffect(args[2] + args[3]);
						break;
					case (5):
						eGlowEffect = DataManager.getEGlowEffect(args[2] + args[3] + args[4]);
						break;
				}
			} else {
				switch (args.length) {
					case (4):
						eGlowEffect = DataManager.getEGlowEffect(args[2].toLowerCase().replace("off", "none").replace("disable", "none"));
						break;
					case (5):
						if (args[2].equalsIgnoreCase("glowonjoin")) {
							eGlowTarget.setGlowOnJoin(Boolean.parseBoolean(args[3].toLowerCase()));
							ChatUtil.sendMsg(sender, Message.OTHER_GLOW_ON_JOIN_CONFIRM.get(eGlowTarget, args[3].toLowerCase()), true);
							continue;

						}
						eGlowEffect = DataManager.getEGlowEffect(args[2] + args[3]);
						break;
					case (6):
						eGlowEffect = DataManager.getEGlowEffect(args[2] + args[3] + args[4]);
						break;
				}
			}


			if (eGlowEffect == null) {
				sendSyntax(sender);
				return;
			}

			if (eGlowEffect.getName().equals("none")) {
				if (eGlowTarget.isGlowing()) {
					eGlowTarget.disableGlow(false);

					if (eGlowTarget.getEntityType().equals(EnumUtil.EntityType.PLAYER) && MainConfig.SETTINGS_NOTIFICATIONS_TARGET_COMMAND.getBoolean())
						ChatUtil.sendMsg(eGlowTarget.getPlayer(), Message.TARGET_NOTIFICATION_PREFIX.get(pSender) + Message.DISABLE_GLOW.get(pSender), true);
				}

				ChatUtil.sendMsg(sender, Message.OTHER_CONFIRM_OFF.get(eGlowTarget), true);
			} else {
				if (!eGlowTarget.isSameGlow(eGlowEffect)) {
					eGlowTarget.activateGlow(eGlowEffect);

					if (eGlowTarget.getEntityType().equals(EnumUtil.EntityType.PLAYER) && MainConfig.SETTINGS_NOTIFICATIONS_TARGET_COMMAND.getBoolean())
						ChatUtil.sendMsg(eGlowTarget.getPlayer(), Message.TARGET_NOTIFICATION_PREFIX.get(pSender) + Message.NEW_GLOW.get(pSender, eGlowEffect.getDisplayName()), true);
				}

				if (!args[args.length - 1].equalsIgnoreCase("-s")) {
					ChatUtil.sendMsg(sender, Message.OTHER_CONFIRM.get(eGlowTarget, eGlowEffect.getDisplayName()), true);
				}
			}
		}
	}
}
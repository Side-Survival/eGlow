package me.MrGraycat.eglow.command.subcommands.admin;

import me.MrGraycat.eglow.Util.enums.EnumUtil;
import me.MrGraycat.eglow.command.SubCommand;
import me.MrGraycat.eglow.config.EGlowMessageConfig;
import me.MrGraycat.eglow.data.EGlowPlayer;
import me.MrGraycat.eglow.config.EGlowMainConfig.MainConfig;
import me.MrGraycat.eglow.Util.text.ChatUtil;
import org.bukkit.command.CommandSender;

import java.util.Set;

public class UnsetCommand extends SubCommand {
	@Override
	public String getName() {
		return "unset";
	}

	@Override
	public String getPermission() {
		return "eglow.command.unset";
	}

	@Override
	public String[] getSyntax() {
		return new String[]{"/eGlow unset <player/npc>"};
	}

	@Override
	public boolean isPlayerCmd() {
		return false;
	}

	@Override
	public void perform(CommandSender sender, EGlowPlayer eGlowPlayer, String[] args) {
		Set<EGlowPlayer> eGlowTargets = getTarget(sender, args);

		if (eGlowTargets.isEmpty()) {
			sendSyntax(sender);
			return;
		}

		for (EGlowPlayer eTarget : eGlowTargets) {
			if (eTarget == null)
				continue;

			if (eTarget.isGlowing()) {
				eTarget.disableGlow(false);

				if (eTarget.getEntityType().equals(EnumUtil.EntityType.PLAYER) && MainConfig.SETTINGS_NOTIFICATIONS_TARGET_COMMAND.getBoolean())
					ChatUtil.sendMsg(eTarget.getPlayer(), EGlowMessageConfig.Message.TARGET_NOTIFICATION_PREFIX.get() + EGlowMessageConfig.Message.DISABLE_GLOW.get(), true);
			}

			if (!args[args.length - 1].equalsIgnoreCase("-s")) {
				ChatUtil.sendMsg(sender, EGlowMessageConfig.Message.OTHER_CONFIRM_OFF.get(eTarget), true);
			}
		}
	}
}
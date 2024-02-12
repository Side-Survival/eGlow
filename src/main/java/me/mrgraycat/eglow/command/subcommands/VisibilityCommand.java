package me.MrGraycat.eglow.command.subcommands;

import me.MrGraycat.eglow.Util.enums.EnumUtil;
import me.MrGraycat.eglow.Util.packets.PacketUtil;
import me.MrGraycat.eglow.Util.text.ChatUtil;
import me.MrGraycat.eglow.command.SubCommand;
import me.MrGraycat.eglow.config.EGlowMessageConfig;
import me.MrGraycat.eglow.data.EGlowPlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class VisibilityCommand extends SubCommand {

	@Override
	public String getName() {
		return "visibility";
	}

	@Override
	public String getPermission() {
		return "eglow.command.visibility";
	}

	@Override
	public String[] getSyntax() {
		return new String[]{"/eGlow visibility <all/other/own/none>"};
	}

	@Override
	public boolean isPlayerCmd() {
		return true;
	}

	@Override
	public void perform(CommandSender sender, EGlowPlayer eGlowPlayer, String[] args) {
		Player pSender = (sender instanceof Player player) ? player : null;

		if (args.length >= 2) {
			if (eGlowPlayer.getGlowVisibility().equals(EnumUtil.GlowVisibility.UNSUPPORTEDCLIENT)) {
				ChatUtil.sendMsg(sender, EGlowMessageConfig.Message.UNSUPPORTED_GLOW.get(pSender), true);
				return;
			}

			switch (args[1].toLowerCase()) {
				case "all":
				case "other":
				case "own":
				case "none":
					break;
				default:
					sendSyntax(sender);
					return;
			}

			EnumUtil.GlowVisibility oldVisibility = eGlowPlayer.getGlowVisibility();
			EnumUtil.GlowVisibility newVisibility = EnumUtil.GlowVisibility.valueOf(args[1].toUpperCase());

			if (!oldVisibility.equals(newVisibility)) {
				eGlowPlayer.setGlowVisibility(newVisibility);
				PacketUtil.forceUpdateGlow(eGlowPlayer);
			}

			ChatUtil.sendMsg(sender, EGlowMessageConfig.Message.VISIBILITY_CHANGE.get(pSender, newVisibility.name()), true);
		} else {
			switch (eGlowPlayer.getGlowVisibility()) {
				case ALL:
					eGlowPlayer.setGlowVisibility(EnumUtil.GlowVisibility.OTHER);
					break;
				case OTHER:
					eGlowPlayer.setGlowVisibility(EnumUtil.GlowVisibility.OWN);
					break;
				case OWN:
					eGlowPlayer.setGlowVisibility(EnumUtil.GlowVisibility.NONE);
					break;
				case NONE:
					eGlowPlayer.setGlowVisibility(EnumUtil.GlowVisibility.ALL);
					break;
			}

			PacketUtil.forceUpdateGlow(eGlowPlayer);
			ChatUtil.sendMsg(sender, EGlowMessageConfig.Message.VISIBILITY_CHANGE.get(pSender, eGlowPlayer.getGlowVisibility().name()), true);
		}
	}
}
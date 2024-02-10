package me.MrGraycat.eglow.command.subcommands;

import me.MrGraycat.eglow.Util.text.ChatUtil;
import me.MrGraycat.eglow.command.SubCommand;
import me.MrGraycat.eglow.config.EGlowMessageConfig;
import me.MrGraycat.eglow.data.EGlowPlayer;
import org.bukkit.command.CommandSender;

public class ToggleGlowOnJoinCommand extends SubCommand {

	@Override
	public String getName() {
		return "toggleglowonjoin";
	}

	@Override
	public String getPermission() {
		return "eglow.command.toggleglowonjoin";
	}

	@Override
	public String[] getSyntax() {
		return new String[]{"/eGlow toggleglowonjoin"};
	}

	@Override
	public boolean isPlayerCmd() {
		return true;
	}

	@Override
	public void perform(CommandSender sender, EGlowPlayer eGlowPlayer, String[] args) {
		eGlowPlayer.setGlowOnJoin(!eGlowPlayer.isGlowOnJoin());
		ChatUtil.sendMsg(sender, EGlowMessageConfig.Message.GLOWONJOIN_TOGGLE.get(String.valueOf(eGlowPlayer.isGlowOnJoin())), true);
	}
}
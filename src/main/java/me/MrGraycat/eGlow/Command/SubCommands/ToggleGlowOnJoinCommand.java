package me.MrGraycat.eGlow.Command.SubCommands;

import me.MrGraycat.eGlow.Command.SubCommand;
import me.MrGraycat.eGlow.Config.EGlowMessageConfig;
import me.MrGraycat.eGlow.Manager.Interface.IEGlowPlayer;
import me.MrGraycat.eGlow.Util.Text.ChatUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ToggleGlowOnJoinCommand extends SubCommand {

	@Override
	public String getName() {
		return "toggleglowonjoin";
	}

	@Override
	public String getDescription() {
		return "Toggle your glow on join status";
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
	public void perform(CommandSender sender, IEGlowPlayer ePlayer, String[] args) {
		Player pSender = (sender instanceof Player player) ? player : null;

		ePlayer.setGlowOnJoin(!ePlayer.getGlowOnJoin());
		ChatUtil.sendMsg(sender, EGlowMessageConfig.Message.GLOWONJOIN_TOGGLE.get(pSender, String.valueOf(ePlayer.getGlowOnJoin())), true);
	}
}

package me.MrGraycat.eglow.command.subcommands;

import me.MrGraycat.eglow.Util.enums.EnumUtil;
import me.MrGraycat.eglow.Util.text.ChatUtil;
import me.MrGraycat.eglow.command.SubCommand;
import me.MrGraycat.eglow.config.EGlowMessageConfig;
import me.MrGraycat.eglow.data.EGlowPlayer;
import me.MrGraycat.eglow.gui.menus.EGlowMainMenu;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GUICommand extends SubCommand {

	@Override
	public String getName() {
		return "gui";
	}

	@Override
	public String getPermission() {
		return "eglow.command.gui";
	}

	@Override
	public String[] getSyntax() {
		return new String[]{"/eGlow"};
	}

	@Override
	public boolean isPlayerCmd() {
		return true;
	}

	@Override
	public void perform(CommandSender sender, EGlowPlayer eGlowPlayer, String[] args) {
		Player pSender = (sender instanceof Player player) ? player : null;

		switch (eGlowPlayer.getGlowDisableReason()) {
			case BLOCKEDWORLD:
				ChatUtil.sendMsg(sender, EGlowMessageConfig.Message.WORLD_BLOCKED.get(pSender), true);
				return;
			case INVISIBLE:
				ChatUtil.sendMsg(sender, EGlowMessageConfig.Message.INVISIBILITY_BLOCKED.get(pSender), true);
				return;
			case ANIMATION:
				ChatUtil.sendMsg(sender, EGlowMessageConfig.Message.ANIMATION_BLOCKED.get(pSender), true);
				return;
		}

		if (eGlowPlayer.getGlowVisibility().equals(EnumUtil.GlowVisibility.UNSUPPORTEDCLIENT))
			ChatUtil.sendPlainMsg(sender, EGlowMessageConfig.Message.UNSUPPORTED_GLOW.get(pSender), true);
		
		new EGlowMainMenu(eGlowPlayer).openInventory();
	}
}
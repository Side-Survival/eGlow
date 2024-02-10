package me.MrGraycat.eglow.command.subcommands;

import me.MrGraycat.eglow.Util.enums.EnumUtil;
import me.MrGraycat.eglow.Util.text.ChatUtil;
import me.MrGraycat.eglow.command.SubCommand;
import me.MrGraycat.eglow.config.EGlowMessageConfig;
import me.MrGraycat.eglow.data.EGlowPlayer;
import me.MrGraycat.eglow.gui.menus.EGlowMainMenu;
import org.bukkit.command.CommandSender;

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
		switch (eGlowPlayer.getGlowDisableReason()) {
			case BLOCKEDWORLD:
				ChatUtil.sendMsg(sender, EGlowMessageConfig.Message.WORLD_BLOCKED.get(), true);
				return;
			case INVISIBLE:
				ChatUtil.sendMsg(sender, EGlowMessageConfig.Message.INVISIBILITY_BLOCKED.get(), true);
				return;
			case ANIMATION:
				ChatUtil.sendMsg(sender, EGlowMessageConfig.Message.ANIMATION_BLOCKED.get(), true);
				return;
		}

		if (eGlowPlayer.getGlowVisibility().equals(EnumUtil.GlowVisibility.UNSUPPORTEDCLIENT))
			ChatUtil.sendPlainMsg(sender, EGlowMessageConfig.Message.UNSUPPORTED_GLOW.get(), true);
		
		new EGlowMainMenu(eGlowPlayer).openInventory();
	}
}
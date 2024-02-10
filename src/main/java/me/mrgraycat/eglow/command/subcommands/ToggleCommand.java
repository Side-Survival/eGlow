package me.MrGraycat.eglow.command.subcommands;

import me.MrGraycat.eglow.Util.enums.EnumUtil;
import me.MrGraycat.eglow.Util.text.ChatUtil;
import me.MrGraycat.eglow.command.SubCommand;
import me.MrGraycat.eglow.config.EGlowMessageConfig;
import me.MrGraycat.eglow.data.DataManager;
import me.MrGraycat.eglow.data.EGlowEffect;
import me.MrGraycat.eglow.data.EGlowPlayer;
import org.bukkit.command.CommandSender;

public class ToggleCommand extends SubCommand {

	@Override
	public String getName() {
		return "toggle";
	}

	@Override
	public String getPermission() {
		return "eglow.command.toggle";
	}

	@Override
	public String[] getSyntax() {
		return new String[]{"/eGlow toggle"};
	}

	@Override
	public boolean isPlayerCmd() {
		return true;
	}

	@Override
	public void perform(CommandSender sender, EGlowPlayer eGlowPlayer, String[] args) {
		if (eGlowPlayer.isGlowing()) {
			eGlowPlayer.disableGlow(false);
			ChatUtil.sendMsg(sender, EGlowMessageConfig.Message.DISABLE_GLOW.get(), true);
		} else {
			if (eGlowPlayer.getGlowEffect() == null || eGlowPlayer.getGlowEffect().getName().equals("none")) {
				ChatUtil.sendMsg(sender, EGlowMessageConfig.Message.NO_LAST_GLOW.get(), true);
				return;
			}

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

			EGlowEffect currentEGlowEffect = eGlowPlayer.getGlowEffect();

			if (eGlowPlayer.hasPermission(currentEGlowEffect.getPermissionNode()) || (DataManager.isCustomEffect(currentEGlowEffect.getName()) && eGlowPlayer.hasPermission("eglow.egloweffect.*")) || eGlowPlayer.isForcedGlow(currentEGlowEffect)) {
				eGlowPlayer.activateGlow();
			} else {
				ChatUtil.sendMsg(sender, EGlowMessageConfig.Message.NO_PERMISSION.get(), true);
				return;
			}
			ChatUtil.sendMsg(sender, EGlowMessageConfig.Message.NEW_GLOW.get(eGlowPlayer.getLastGlowName()), true);
		}
	}
}
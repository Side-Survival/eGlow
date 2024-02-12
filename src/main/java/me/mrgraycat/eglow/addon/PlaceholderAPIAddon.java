package me.MrGraycat.eglow.addon;

import me.MrGraycat.eglow.Util.enums.EnumUtil;
import me.MrGraycat.eglow.data.DataManager;
import me.MrGraycat.eglow.data.EGlowEffect;
import me.MrGraycat.eglow.data.EGlowPlayer;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.MrGraycat.eglow.EGlow;
import me.MrGraycat.eglow.config.EGlowMessageConfig.Message;
import me.MrGraycat.eglow.Util.text.ChatUtil;
import org.bukkit.entity.Player;

public class PlaceholderAPIAddon extends PlaceholderExpansion {
	/**
	 * Register all eGlow placeholders in PlaceholderAPI
	 */
	public PlaceholderAPIAddon() {
		register();
	}

	@Override
	public String getAuthor() {
		return EGlow.getInstance().getDescription().getAuthors().toString();
	}

	@Override
	public String getVersion() {
		return EGlow.getInstance().getDescription().getVersion();
	}

	@Override
	public String getIdentifier() {
		return "eglow";
	}

	@Override
	public String getRequiredPlugin() {
		return "eGlow";
	}

	@Override
	public boolean canRegister() {
		return EGlow.getInstance() != null;
	}

	@Override
	public boolean register() {
		if (!canRegister())
			return false;
		return super.register();
	}

	@Override
	public boolean persist() {
		return true;
	}

	@Override
	public String onPlaceholderRequest(Player player, String identifier) {
		if (player == null)
			return "";

		EGlowPlayer eGlowPlayer = DataManager.getEGlowPlayer(player);

		if (eGlowPlayer == null)
			return "";

		switch (identifier.toLowerCase()) {
			case ("client_version"):
				return eGlowPlayer.getVersion().getFriendlyName();
			case ("glowcolor"):
				return (eGlowPlayer.getGlowStatus() && !eGlowPlayer.isFakeGlowStatus()) ? eGlowPlayer.getActiveColor().toString() : "";
			case ("colorchar"):
				return (eGlowPlayer.getGlowStatus() && !eGlowPlayer.isFakeGlowStatus()) ? String.valueOf(eGlowPlayer.getActiveColor().getChar()) : "r";
			case ("activeglow"):
				return (eGlowPlayer.isGlowing()) ? ChatUtil.getEffectChatName(eGlowPlayer) : Message.COLOR.get(player, "none");
			case ("activeglow_raw"):
				return (eGlowPlayer.isGlowing()) ? ChatUtil.setToBasicName(ChatUtil.getEffectChatName(eGlowPlayer)) : ChatUtil.setToBasicName(Message.COLOR.get(player, "none"));
			case ("lastglow"):
				return (eGlowPlayer.getLastGlowName());
			case ("lastglow_raw"):
				return ChatUtil.setToBasicName(eGlowPlayer.getLastGlowName());
			case ("glow_speed"):
				return getSpeedFromEffect(player, eGlowPlayer.getGlowEffect(), false);
			case ("glow_speed_raw"):
				return getSpeedFromEffect(player, eGlowPlayer.getGlowEffect(), true);
			case ("glowstatus"):
				return (eGlowPlayer.getGlowStatus() && !eGlowPlayer.isFakeGlowStatus()) ? Message.GUI_YES.get(player) : Message.GUI_NO.get(player);
			case ("glowstatus_raw"):
				return (eGlowPlayer.getGlowStatus() && !eGlowPlayer.isFakeGlowStatus()) ? "true" : "false";
			case ("glowstatus_join"):
				return (eGlowPlayer.isGlowOnJoin()) ? Message.GUI_YES.get(player) : Message.GUI_NO.get(player);
			case ("glowstatus_join_raw"):
				return (eGlowPlayer.isGlowOnJoin()) ? "true" : "false";
			case ("glow_visibility"):
				return (eGlowPlayer.getGlowVisibility().equals(EnumUtil.GlowVisibility.UNSUPPORTEDCLIENT)) ? Message.VISIBILITY_UNSUPPORTED.get(player) : Message.valueOf("VISIBILITY_" + eGlowPlayer.getGlowVisibility().toString()).get(player);
			case ("glow_visibility_all"):
				return ((eGlowPlayer.getGlowVisibility().equals(EnumUtil.GlowVisibility.ALL)) ? Message.GLOW_VISIBILITY_INDICATOR.get(player) : "") + Message.VISIBILITY_ALL.get(player);
			case ("glow_visibility_other"):
				return ((eGlowPlayer.getGlowVisibility().equals(EnumUtil.GlowVisibility.OTHER)) ? Message.GLOW_VISIBILITY_INDICATOR.get(player) : "") + Message.VISIBILITY_OTHER.get(player);
			case ("glow_visibility_own"):
				return ((eGlowPlayer.getGlowVisibility().equals(EnumUtil.GlowVisibility.OWN)) ? Message.GLOW_VISIBILITY_INDICATOR.get(player) : "") + Message.VISIBILITY_OWN.get(player);
			case ("glow_visibility_none"):
				return ((eGlowPlayer.getGlowVisibility().equals(EnumUtil.GlowVisibility.NONE)) ? Message.GLOW_VISIBILITY_INDICATOR.get(player) : "") + Message.VISIBILITY_NONE.get(player);
			default:
				boolean raw = identifier.toLowerCase().endsWith("_raw");
				if (identifier.toLowerCase().contains("has_permission_")) {
					EGlowEffect effect = DataManager.getEGlowEffect(identifier.toLowerCase().replace("has_permission_", "").replace("_raw", ""));
					if (effect != null) {
						if (player.hasPermission(effect.getPermissionNode())) {
							return (raw) ? "true" : Message.GUI_YES.get(player);
						} else {
							return (raw) ? "false" : Message.GUI_NO.get(player);
						}
					} else {
						return "Invalid effect";
					}
				}
		}
		return null;
	}

	private String getSpeedFromEffect(Player player, EGlowEffect effect, boolean raw) {
		if (effect == null)
			return (raw) ? "none" : Message.COLOR.get(player, "none");

		String effectName = effect.getName();

		if (effectName.contains("slow"))
			return (raw) ? "slow" : Message.COLOR.get(player, "slow");

		if (effectName.contains("fast"))
			return (raw) ? "fast" : Message.COLOR.get(player, "fast");
		return (raw) ? "none" : Message.COLOR.get(player, "none");
	}
}
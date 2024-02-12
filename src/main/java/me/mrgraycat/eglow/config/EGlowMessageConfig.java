package me.MrGraycat.eglow.config;

import lv.side.lang.api.LangAPI;
import me.MrGraycat.eglow.EGlow;
import me.MrGraycat.eglow.Util.LangMessages;
import me.MrGraycat.eglow.Util.enums.EnumUtil;
import me.MrGraycat.eglow.data.EGlowPlayer;
import me.MrGraycat.eglow.Util.text.ChatUtil;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class EGlowMessageConfig {

	private static YamlConfiguration config;
	private static File configFile;

	public static void initialize() {
		File oldConfigFile = new File(EGlow.getInstance().getDataFolder(), "Messages.yml");
		configFile = new File(EGlow.getInstance().getDataFolder(), "messages.yml");

		try {
			if (oldConfigFile.exists())
				oldConfigFile.renameTo(configFile);

			if (!configFile.exists()) {
				ChatUtil.sendToConsole("&f[&eeGlow&f]: &4messages.yml not found&f! &eCreating&f...", false);
				configFile.getParentFile().mkdirs();
				EGlow.getInstance().saveResource("messages.yml", false);
			} else {
				ChatUtil.sendToConsole("&f[&eeGlow&f]: &aLoading messages config&f.", false);
			}

			config = new YamlConfiguration();
			config.load(configFile);
			repairConfig();
		} catch (Exception e) {
			ChatUtil.reportError(e);
		}
	}

	public static void updateLang() {
		Map<String, String> keyMap = new HashMap<>();

		if (config.isConfigurationSection("main")) {
			ConfigurationSection messages = config.getConfigurationSection("main");

			//noinspection ConstantConditions
			Collection<String> keys = messages.getKeys(true);
			for (String key : keys) {
				if (messages.isString(key)) {
					String value = messages.getString(key);
					keyMap.put("s-eglow.main." + key, value);
				}
			}
		}

		LangAPI.updateDefaults(keyMap);
	}

	public static boolean reloadConfig() {
		YamlConfiguration configBackup = config;
		File configFileBackup = configFile;

		try {
			config = null;
			configFile = null;

			configFile = new File(EGlow.getInstance().getDataFolder(), "messages.yml");
			config = new YamlConfiguration();
			config.load(configFile);
			return true;
		} catch (Exception e) {
			config = configBackup;
			configFile = configFileBackup;

			ChatUtil.reportError(e);
			return false;
		}
	}

	public enum Message {
		PREFIX("main.prefix"),
		NO_PERMISSION("main.no-permission"),
		COLOR("main.color-"),
		NEW_GLOW("main.glow-new"),
		SAME_GLOW("main.glow-same"),
		DISABLE_GLOW("main.glow-disable"),
		GLOW_REMOVED("main.glow-removed"),
		GLOWONJOIN_TOGGLE("main.glow-glowonjoin-toggle"),
		VISIBILITY_CHANGE("main.glow-visibility-change"),
		VISIBILITY_ALL("main.glow-visibility-all"),
		VISIBILITY_OTHER("main.glow-visibility-other"),
		VISIBILITY_OWN("main.glow-visibility-own"),
		VISIBILITY_NONE("main.glow-visibility-none"),
		VISIBILITY_UNSUPPORTED("main.glow-visibility-unsupported-version"),
		UNSUPPORTED_GLOW("main.glow-unsupported"),
		OTHER_CONFIRM("main.other-glow-player-confirm"),
		OTHER_CONFIRM_OFF("main.other-glow-player-confirm-off"),
		OTHER_GLOW_ON_JOIN_CONFIRM("main.other-glow-on-join-confirm"),
		OTHER_PLAYER_IN_DISABLED_WORLD("main.other-glow-player-disabled-world"),
		OTHER_PLAYER_INVISIBLE("main.other-glow-player-invisible"),
		OTHER_PLAYER_ANIMATION("main.other-glow-player-animation"),
		TARGET_NOTIFICATION_PREFIX("main.other-glow-target-notification-prefix"),
		RELOAD_SUCCESS("main.reload-success"),
		RELOAD_FAIL("main.reload-fail"),
		RELOAD_GLOW_ALLOWED("main.reload-glow-allowed"),
		RELOAD_GLOW_BLOCKED("main.reload-glow-blocked"),
		GLOWING_STATE_ON_JOIN("main.glowing-state-on-join"),
		NON_GLOWING_STATE_ON_JOIN("main.non-glowing-state-on-join"),
		NO_LAST_GLOW("main.argument-no-last-glow"),
		INCORRECT_USAGE("main.argument-incorrect-usage"),
		PLAYER_NOT_FOUND("main.argument-player-not-found"),
		COMMAND_LIST("main.command-list"),
		PLAYER_ONLY("main.command-player-only"),
		INVISIBILITY_BLOCKED("main.invisibility-glow-blocked"),
		INVISIBILITY_ALLOWED("main.invisibility-glow-allowed"),
		WORLD_BLOCKED("main.world-glow-blocked"),
		WORLD_ALLOWED("main.world-glow-allowed"),
		ANIMATION_BLOCKED("main.animation-glow-blocked"),
		CITIZENS_NOT_INSTALLED("main.citizens-not-installed"),
		CITIZENS_NPC_NOT_FOUND("main.citizens-npc-not-found"),
		GUI_TITLE("gui.title"),
		GUI_COLOR("gui.color-"),
		GUI_YES("gui.misc-yes"),
		GUI_NO("gui.misc-no"),
		GUI_NOT_AVAILABLE("gui.misc-not-available"),
		GUI_LEFT_CLICK("gui.misc-left-click"),
		GUI_RIGHT_CLICK("gui.misc-right-click"),
		GUI_CLICK_TO_TOGGLE("gui.misc-click-to-toggle"),
		GUI_CLICK_TO_CYCLE("gui.misc-click-to-cycle"),
		GUI_CLICK_TO_OPEN("gui.misc-click-to-open"),
		GUI_PREVIOUS_PAGE("gui.misc-previous-page"),
		GUI_NEXT_PAGE("gui.misc-next-page"),
		GUI_PAGE_LORE("gui.misc-page-lore"),
		GUI_MAIN_MENU("gui.misc-main-menu"),
		GUI_COOLDOWN("gui.misc-interaction-cooldown"),
		GUI_COLOR_PERMISSION("gui.color-colorpermission"),
		GUI_BLINK_PERMISSION("gui.color-blinkpermission"),
		GUI_EFFECT_PERMISSION("gui.color-effectpermission"),
		GUI_SETTINGS_NAME("gui.setting-item-name"),
		GUI_GLOW_ON_JOIN("gui.setting-glow-on-join"),
		GUI_LAST_GLOW("gui.setting-last-glow"),
		GUI_GLOW_ITEM_NAME("gui.glow-item-name"),
		GUI_GLOWING("gui.glow-glowing"),
		GLOW_VISIBILITY_ITEM_NAME("gui.glow-visibility-item-name"),
		GLOW_VISIBILITY_INDICATOR("gui.glow-visibility-indicator"),
		GUI_SPEED_ITEM_NAME("gui.speed-item-name"),
		GUI_SPEED("gui.speed-speed"),
		GUI_CUSTOM_EFFECTS_ITEM_NAME("gui.custom-effect-item-name");

		private final Message msg;
		private final String configPath;

		Message(String configPath) {
			this.msg = this;
			this.configPath = configPath;
		}

		public String getConfigPath() {
			return configPath;
		}

		public String get(Player player) {
			if (getConfigPath().startsWith("gui."))
				return getColorValue(getConfigPath());
			else
				return LangMessages.get(player, getConfigPath());
		}

		public String get(Player player, String value) {
			switch (msg) {
				case COLOR:
					return LangMessages.get(player, msg.getConfigPath() + value);
				case GUI_PAGE_LORE:
					return getColorValue(msg.getConfigPath(), "%page%", value);
				case GUI_COLOR:
					if (config.contains(msg.getConfigPath() + value))
						return getColorValue(msg.getConfigPath() + value);
					return getColorValue(Message.COLOR.getConfigPath() + value);
				case GLOWONJOIN_TOGGLE:
					return LangMessages.getParam(player, msg.getConfigPath(), "%value%", value);
				case VISIBILITY_CHANGE:
					return (value.toUpperCase().equals(EnumUtil.GlowVisibility.UNSUPPORTEDCLIENT.toString())) ?
							LangMessages.getParam(player, msg.getConfigPath(), "%value%", Message.VISIBILITY_UNSUPPORTED.get(player)) :
							LangMessages.getParam(player, msg.getConfigPath(), "%value%", Message.valueOf("VISIBILITY_" + value).get(player));
				case INCORRECT_USAGE:
					return LangMessages.getParam(player, msg.getConfigPath(), "%command%", value);
				case NEW_GLOW:
				case GLOWING_STATE_ON_JOIN:
					return LangMessages.getParam(player, msg.getConfigPath(), "%glowname%", value);
				case RELOAD_GLOW_ALLOWED:
				case RELOAD_GLOW_BLOCKED:
					return LangMessages.getParam(player, msg.getConfigPath(), "%reason%", value);
				default:
					break;
			}
			return "Incorrect handled message for: " + msg;
		}

		public String get(EGlowPlayer eGlowTarget, String value) {
			switch (msg) {
				case OTHER_CONFIRM:
					return getColorValue(msg.getConfigPath(), eGlowTarget, "%glowname%", value);
				case OTHER_GLOW_ON_JOIN_CONFIRM:
					return getColorValue(msg.getConfigPath(), eGlowTarget, "%value%", value);
				default:
					break;
			}
			return "Incorrect handled message for: " + msg;
		}

		public String get(EGlowPlayer eGlowTarget) {
			return LangMessages.getParam(eGlowTarget.getPlayer(), msg.getConfigPath(), "%target%", eGlowTarget.getDisplayName());
		}

		private String getColorValue(String path) {
			String text = config.getString(path);

			if (text == null)
				return "&cFailed to get text for&f: '&e" + path + "'";
			return ChatUtil.translateColors(text);
		}

		private String getColorValue(String path, String textToReplace, String replacement) {
			String text = config.getString(path);

			if (text == null)
				return "&cFailed to get text for&f: '&e" + path + "'";
			if (replacement == null)
				return "&cInvalid effectname&f.";
			return ChatUtil.translateColors(text.replace(textToReplace, replacement));
		}

		private String getColorValue(String path, EGlowPlayer eGlowPlayer, String textToReplace, String replacement) {
			return LangMessages.getParam(
					eGlowPlayer.getPlayer(), path,
					new String[]{textToReplace, "%target%"},
					new String[]{replacement, eGlowPlayer.getDisplayName()}
			);
		}
	}

	private static void repairConfig() {
		InputStream resource = EGlow.getInstance().getResource("messages.yml");
		YamlConfiguration tempConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(Objects.requireNonNull(resource)));

		for (String path : Objects.requireNonNull(tempConfig.getConfigurationSection("")).getKeys(true)) {
			if (!config.contains(path))
				config.set(path, tempConfig.get(path));
		}

		try {
			config.save(configFile);
		} catch (Exception exception) {
			ChatUtil.reportError(exception);
		}
	}
}
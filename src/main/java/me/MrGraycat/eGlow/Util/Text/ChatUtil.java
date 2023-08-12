package me.MrGraycat.eGlow.Util.Text;

import me.MrGraycat.eGlow.Config.EGlowMainConfig.MainConfig;
import me.MrGraycat.eGlow.Config.EGlowMessageConfig.Message;
import me.MrGraycat.eGlow.Manager.DataManager;
import me.MrGraycat.eGlow.Manager.Interface.IEGlowPlayer;
import me.MrGraycat.eGlow.Util.Packets.Chat.ChatColor;
import me.MrGraycat.eGlow.Util.Packets.Chat.rgb.RGBUtils;
import me.MrGraycat.eGlow.Util.Packets.PacketUtil;
import me.MrGraycat.eGlow.Util.Packets.ProtocolVersion;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChatUtil {
	private final static Pattern rgb = Pattern.compile("#[0-9a-fA-F]{6}");

	public static String setToBasicName(String effect) {
		effect = ChatColor.stripColor(effect).toLowerCase();

		if (effect.contains("slow"))
			effect = effect.replace("slow", "");

		if (effect.contains("fast"))
			effect = effect.replace("fast", "");

		if (effect.contains("("))
			effect = effect.replace("(", "").replace(")", "");

		return effect.replace(" ", "");
	}

	public static String translateColors(String text) {
		if (text == null || text.isEmpty())
			return "";

		try {
			if (ProtocolVersion.SERVER_VERSION.getMinorVersion() <= 15) {
				text = RGBUtils.getInstance().convertRGBtoLegacy(text);
				return text.replace("&", "§");
			}
		} catch (NullPointerException e) {
			return text.replace("&", "§");
		}

		text = RGBUtils.getInstance().applyFormats(text);

		Matcher match = rgb.matcher(text);
		while (match.find()) {
			String color = text.substring(match.start(), match.end());
			text = text.replace(color, ChatColor.of(color) + "");
			match = rgb.matcher(text);
		}

		return text.replace("&", "§");
	}

	public static List<String> translateColors(List<String> input) {
		for (int i = 0; i < input.size(); i++) {
			input.set(i, translateColors(input.get(i)));
		}

		return input;
	}

	public static void sendPlainMsg(Object sender, String message, boolean withPrefix) {
		if (!message.isEmpty()) {
			if (sender instanceof Player player) {
				message = translateColors(((withPrefix) ? Message.PREFIX.get(player) : "") + message);
				player.sendMessage(message);
			} else {
				message = translateColors(((withPrefix) ? Message.PREFIX.get((Player) null) : "") + message);
				((CommandSender) sender).sendMessage(message);
			}
		}
	}

	public static void sendMsgFromGUI(Player player, String message) {
		if (MainConfig.ACTIONBARS_ENABLE.getBoolean() && MainConfig.ACTIONBARS_IN_GUI.getBoolean()) {
			sendMsg(player, message, true);
		} else {
			sendPlainMsg(player, message, true);
		}
	}

	public static void sendMsg(Object sender, String message, boolean withPrefix) {
		if (!message.isEmpty()) {
			if (sender instanceof Player player) {
				message = translateColors(((withPrefix) ? Message.PREFIX.get(player) : "") + message);
				if (MainConfig.ACTIONBARS_ENABLE.getBoolean()) {
					sendActionbar(player, message);
				} else {
					player.sendMessage(message);
				}
			} else {
				message = translateColors(((withPrefix) ? Message.PREFIX.get((Player) null) : "") + message);
				((CommandSender) sender).sendMessage(message);
			}
		}
	}

	public static void sendToConsole(String message, boolean withPrefix) {
		Bukkit.getConsoleSender().sendMessage(translateColors(((withPrefix) ? Message.PREFIX.get((Player) null) : "") + message));
	}

	private static void sendActionbar(Player player, String message) {
		IEGlowPlayer ePlayer = DataManager.getEGlowPlayer(player);

		if (ePlayer == null) {
			return;
		}

		if (ePlayer.getVersion().getMinorVersion() < 9) {
			sendPlainMsg(player, message, false);
		} else {
			PacketUtil.sendActionbar(ePlayer, message);
		}
	}

	public static void reportError(Exception e) {
		sendToConsole("&f[&eeGlow&f]: &4Please report this error to MrGraycat&f!:", false);
		e.printStackTrace();
	}

	public static String getEffectChatName(IEGlowPlayer entity) {
		return (entity.getEffect() == null) ? Message.GUI_NOT_AVAILABLE.get(entity.getPlayer()) : entity.getEffect().getDisplayName(entity.getPlayer());
	}

	public static String getEffectName(String effect) {
		return "&e" + effect + " &f(" + Objects.requireNonNull(DataManager.getEGlowEffect(effect), "Unable to retrieve effect from given name").getDisplayName(null) + "&f)";
	}
}
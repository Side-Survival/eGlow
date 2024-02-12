package me.MrGraycat.eglow.gui.menus;

import me.MrGraycat.eglow.Util.enums.EnumUtil;
import me.MrGraycat.eglow.EGlow;
import me.MrGraycat.eglow.config.EGlowMainConfig.MainConfig;
import me.MrGraycat.eglow.config.EGlowMessageConfig.Message;
import me.MrGraycat.eglow.data.DataManager;
import me.MrGraycat.eglow.data.EGlowEffect;
import me.MrGraycat.eglow.data.EGlowPlayer;
import me.MrGraycat.eglow.gui.Menu;
import me.MrGraycat.eglow.Util.packets.PacketUtil;
import me.MrGraycat.eglow.Util.text.ChatUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Objects;

public class EGlowMainMenu extends Menu {
	public EGlowMainMenu(EGlowPlayer eGlowPlayer) {
		super(eGlowPlayer.getPlayer());
	}

	@Override
	public String getMenuName() {
		return ChatUtil.translateColors(((MainConfig.SETTINGS_GUI_ADD_PREFIX.getBoolean()) ? Message.GUI_TITLE.get(getiPlayer()) : Message.PREFIX.get(getiPlayer()) + Message.GUI_TITLE.get(getiPlayer())));
	}

	@Override
	public int getSlots() {
		return 36;
	}

	@Override
	public void handleMenu(InventoryClickEvent event) {
		Player player = (Player) event.getWhoClicked();
		EGlowPlayer eGlowPlayer = DataManager.getEGlowPlayer(player);
		ClickType clickType = event.getClick();
		int clickedSlot = event.getSlot();

		if ((System.currentTimeMillis() - getMenuMetadata().getLastClicked()) < MainConfig.SETTINGS_GUIS_INTERACTION_DELAY.getLong()) {
			ChatUtil.sendMsgFromGUI(player, Message.GUI_COOLDOWN.get(player));
			return;
		}

		getMenuMetadata().setLastClicked(System.currentTimeMillis());

		switch (clickedSlot) {
			case (0):
				enableGlow(player, clickType, "red");
				break;
			case (1):
				enableGlow(player, clickType, "darkred");
				break;
			case (2):
				enableGlow(player, clickType, "gold");
				break;
			case (3):
				enableGlow(player, clickType, "yellow");
				break;
			case (4):
				enableGlow(player, clickType, "green");
				break;
			case (5):
				enableGlow(player, clickType, "darkgreen");
				break;
			case (6):
				enableGlow(player, clickType, "aqua");
				break;
			case (7):
				enableGlow(player, clickType, "darkaqua");
				break;
			case (8):
				enableGlow(player, clickType, "blue");
				break;
			case (10):
				enableGlow(player, clickType, "darkblue");
				break;
			case (11):
				enableGlow(player, clickType, "purple");
				break;
			case (12):
				enableGlow(player, clickType, "pink");
				break;
			case (13):
				enableGlow(player, clickType, "white");
				break;
			case (14):
				enableGlow(player, clickType, "gray");
				break;
			case (15):
				enableGlow(player, clickType, "darkgray");
				break;
			case (16):
				enableGlow(player, clickType, "black");
				break;
			case (22):
				enableGlow(player, clickType, "rainbow");
				break;
			case (28):
				if (eGlowPlayer.skipSaveData())
					eGlowPlayer.setSaveData(true);

				eGlowPlayer.setGlowOnJoin(!eGlowPlayer.isGlowOnJoin());
				break;
			case (30):
				if (eGlowPlayer.getPlayer().hasPermission("eglow.command.toggle")) {
					if (eGlowPlayer.isGlowing()) {
						eGlowPlayer.disableGlow(false);
						ChatUtil.sendMsgFromGUI(player, Message.DISABLE_GLOW.get(player));
					} else {
						if (eGlowPlayer.getGlowEffect() == null || eGlowPlayer.getGlowEffect().getName().equals("none")) {
							ChatUtil.sendMsgFromGUI(player, Message.NO_LAST_GLOW.get(player));
							return;
						}

						switch (eGlowPlayer.getGlowDisableReason()) {
							case BLOCKEDWORLD:
								ChatUtil.sendMsgFromGUI(player, Message.WORLD_BLOCKED.get(player));
								return;
							case INVISIBLE:
								ChatUtil.sendMsgFromGUI(player, Message.INVISIBILITY_BLOCKED.get(player));
								return;
							case ANIMATION:
								ChatUtil.sendMsgFromGUI(player, Message.ANIMATION_BLOCKED.get(player));
								return;
						}

						EGlowEffect currentEGlowEffect = eGlowPlayer.getGlowEffect();

						if (eGlowPlayer.hasPermission(currentEGlowEffect.getPermissionNode()) || (DataManager.isCustomEffect(currentEGlowEffect.getName()) && eGlowPlayer.hasPermission("eglow.egloweffect.*")) || eGlowPlayer.isForcedGlow(currentEGlowEffect)) {
							eGlowPlayer.activateGlow();
						} else {
							ChatUtil.sendMsgFromGUI(player, Message.NO_PERMISSION.get(player));
							return;
						}
						ChatUtil.sendMsgFromGUI(player, Message.NEW_GLOW.get(player, eGlowPlayer.getLastGlowName()));
					}
				} else {
					ChatUtil.sendMsgFromGUI(player, Message.NO_PERMISSION.get(player));
				}
				break;
			case (31):
				if (!eGlowPlayer.hasPermission("eglow.command.visibility")) {
					ChatUtil.sendMsgFromGUI(player, Message.NO_PERMISSION.get(player));
					return;
				}

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
				ChatUtil.sendMsgFromGUI(player, Message.VISIBILITY_CHANGE.get(player, eGlowPlayer.getGlowVisibility().name()));
				break;
			case (32):
				if (hasEffect(eGlowPlayer))
					updateSpeed(eGlowPlayer);
				break;
			case (34):
				if (MainConfig.SETTINGS_GUI_CUSTOM_EFFECTS.getBoolean())
					new EGlowEffectMenu(eGlowPlayer.getPlayer()).openInventory();
				break;
			default:
				return;
		}

		UpdateMainNavigationBar(eGlowPlayer);
	}

	@Override
	public void setMenuItems() {
		new BukkitRunnable() {
			@Override
			public void run() {
				EGlowPlayer eGlowPlayer = DataManager.getEGlowPlayer(getMenuMetadata().getOwner());
				Player player = eGlowPlayer.getPlayer();

				UpdateMainNavigationBar(eGlowPlayer);

				getInventory().setItem(0, createLeatherColor(eGlowPlayer, "red", 255, 85, 85));
				getInventory().setItem(1, createLeatherColor(eGlowPlayer, "dark-red", 170, 0, 0));
				getInventory().setItem(2, createLeatherColor(eGlowPlayer, "gold", 255, 170, 0));
				getInventory().setItem(3, createLeatherColor(eGlowPlayer, "yellow", 255, 255, 85));
				getInventory().setItem(4, createLeatherColor(eGlowPlayer, "green", 85, 255, 85));
				getInventory().setItem(5, createLeatherColor(eGlowPlayer, "dark-green", 0, 170, 0));
				getInventory().setItem(6, createLeatherColor(eGlowPlayer, "aqua", 85, 255, 255));
				getInventory().setItem(7, createLeatherColor(eGlowPlayer, "dark-aqua", 0, 170, 170));
				getInventory().setItem(8, createLeatherColor(eGlowPlayer, "blue", 85, 85, 255));

				getInventory().setItem(10, createLeatherColor(eGlowPlayer, "dark-blue", 0, 0, 170));
				getInventory().setItem(11, createLeatherColor(eGlowPlayer, "purple", 170, 0, 170));
				getInventory().setItem(12, createLeatherColor(eGlowPlayer, "pink", 255, 85, 255));
				getInventory().setItem(13, createLeatherColor(eGlowPlayer, "white", 255, 255, 255));
				getInventory().setItem(14, createLeatherColor(eGlowPlayer, "gray", 170, 170, 170));
				getInventory().setItem(15, createLeatherColor(eGlowPlayer, "dark-gray", 85, 85, 85));
				getInventory().setItem(16, createLeatherColor(eGlowPlayer, "black", 0, 0, 0));

				getInventory().setItem(22, createItem(Material.NETHER_STAR, Message.GUI_COLOR.get(player, "effect-rainbow"), 0, Message.GUI_LEFT_CLICK.get(player) + Message.COLOR.get(player, "effect-rainbow"), Message.GUI_EFFECT_PERMISSION.get(player) + ((eGlowPlayer.getPlayer().hasPermission(Objects.requireNonNull(DataManager.getEGlowEffect("rainbowslow"), "Unable to retrieve effect from given name").getPermissionNode()) ? Message.GUI_YES.get(player) : Message.GUI_NO.get(player)))));

				new BukkitRunnable() {
					@Override
					public void run() {
						getMenuMetadata().getOwner().openInventory(getInventory());
					}
				}.runTask(EGlow.getInstance());
			}
		}.runTaskAsynchronously(EGlow.getInstance());
	}
}
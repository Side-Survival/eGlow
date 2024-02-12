package me.MrGraycat.eglow.gui;

import lombok.Getter;
import me.MrGraycat.eglow.Util.text.ChatUtil;
import me.MrGraycat.eglow.config.EGlowMainConfig;
import me.MrGraycat.eglow.config.EGlowMessageConfig;
import me.MrGraycat.eglow.data.DataManager;
import me.MrGraycat.eglow.data.EGlowEffect;
import me.MrGraycat.eglow.data.EGlowPlayer;
import me.MrGraycat.eglow.gui.manager.MenuItemManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import java.util.Objects;

@Getter
public abstract class Menu extends MenuItemManager implements InventoryHolder {
	protected MenuMetadata menuMetadata;
	protected Inventory inventory;
	private final Player iPlayer;

	public Menu(Player player) {
		this.menuMetadata = getMenuMetadata(player);
		this.iPlayer = player;
	}

	public abstract String getMenuName();

	public abstract int getSlots();

	public abstract void handleMenu(InventoryClickEvent e);

	public abstract void setMenuItems();

	public void openInventory() {
		inventory = Bukkit.createInventory(this, getSlots(), getMenuName());
		this.setMenuItems();
		getMenuMetadata().getOwner().openInventory(getInventory());
	}

	@Override
	public Inventory getInventory() {
		return inventory;
	}

	/**
	 * Enable glow for a player based on the clicktype
	 *
	 * @param player     to enable the glow for
	 * @param clickType  left/right click
	 * @param effectName effect to check for solid/blink/effect
	 */
	public void enableGlow(Player player, ClickType clickType, String effectName) {
		EGlowPlayer eGlowPlayer = DataManager.getEGlowPlayer(player);

		if (clickType.equals(ClickType.LEFT)) {
			if (DataManager.getEGlowEffect(effectName) != null) {
				EGlowEffect color = DataManager.getEGlowEffect(effectName);

				if (color == null)
					return;

				if (!(player.hasPermission(color.getPermissionNode()) || DataManager.isCustomEffect(color.getName()) && Objects.requireNonNull(player.getPlayer(), "Unable to retrieve player").hasPermission("eglow.effect.*"))) {
					ChatUtil.sendMsgFromGUI(player, EGlowMessageConfig.Message.NO_PERMISSION.get(player));
					return;
				}

				if (eGlowPlayer.isSameGlow(color)) {
					ChatUtil.sendMsgFromGUI(player, EGlowMessageConfig.Message.SAME_GLOW.get(player));
					return;
				}

				eGlowPlayer.activateGlow(color);
				ChatUtil.sendMsgFromGUI(player, EGlowMessageConfig.Message.NEW_GLOW.get(player, color.getDisplayName()));
			} else if (DataManager.getEGlowEffect(effectName + "slow") != null) { //for rainbow effect 
				EGlowEffect effect = DataManager.getEGlowEffect(effectName + "slow");

				if (!player.hasPermission(Objects.requireNonNull(effect, "Unable to retrieve effect from given name").getPermissionNode())) {
					ChatUtil.sendMsgFromGUI(player, EGlowMessageConfig.Message.NO_PERMISSION.get(player));
					return;
				}

				if (eGlowPlayer.isSameGlow(effect)) {
					ChatUtil.sendMsgFromGUI(player, EGlowMessageConfig.Message.SAME_GLOW.get(player));
					return;
				}

				eGlowPlayer.activateGlow(effect);
				ChatUtil.sendMsgFromGUI(player, EGlowMessageConfig.Message.NEW_GLOW.get(player, effect.getDisplayName()));
			}

		} else if (clickType.equals(ClickType.RIGHT)) {
			EGlowEffect effect = DataManager.getEGlowEffect("blink" + effectName + "slow");

			if (effect == null)
				return;

			if (!player.hasPermission(effect.getPermissionNode())) {
				ChatUtil.sendMsgFromGUI(player, EGlowMessageConfig.Message.NO_PERMISSION.get(player));
				return;
			}

			if (eGlowPlayer.isSameGlow(effect)) {
				ChatUtil.sendMsgFromGUI(player, EGlowMessageConfig.Message.SAME_GLOW.get(player));
				return;
			}

			eGlowPlayer.activateGlow(effect);
			ChatUtil.sendMsgFromGUI(player, EGlowMessageConfig.Message.NEW_GLOW.get(player, effect.getDisplayName()));
		}
	}

	/**
	 * Update the effects speed
	 *
	 * @param eGlowPlayer to update the speed for
	 */
	public void updateSpeed(EGlowPlayer eGlowPlayer) {
		if (eGlowPlayer.getGlowEffect() != null) {
			String effect = eGlowPlayer.getGlowEffect().getName();
			EGlowEffect eGlowEffect = null;

			if (effect.contains("slow"))
				eGlowEffect = DataManager.getEGlowEffect(effect.replace("slow", "fast"));

			if (effect.contains("fast"))
				eGlowEffect = DataManager.getEGlowEffect(effect.replace("fast", "slow"));

			eGlowPlayer.activateGlow(eGlowEffect);
			ChatUtil.sendMsgFromGUI(getMenuMetadata().getOwner(), EGlowMessageConfig.Message.NEW_GLOW.get(eGlowPlayer.getPlayer(), Objects.requireNonNull(eGlowEffect, "Unable to get displayname from effect").getDisplayName()));
		}
	}

	/**
	 * Code to update the navigationbar for the main menu
	 *
	 * @param eGlowPlayer to update the navigationbar for
	 */
	public void UpdateMainNavigationBar(EGlowPlayer eGlowPlayer) {
		if (EGlowMainConfig.MainConfig.SETTINGS_GUI_ADD_GLASS_PANES.getBoolean()) {
			getInventory().setItem(27, createItem(Material.valueOf(GLASS_PANE), "&f", 5, ""));
			getInventory().setItem(29, createItem(Material.valueOf(GLASS_PANE), "&f", 5, ""));
			getInventory().setItem(32, createItem(Material.valueOf(GLASS_PANE), "&f", 5, ""));
			getInventory().setItem(33, createItem(Material.valueOf(GLASS_PANE), "&f", 5, ""));

			getInventory().setItem(34, createItem(Material.valueOf(GLASS_PANE), "&f", 5, ""));

			getInventory().setItem(35, createItem(Material.valueOf(GLASS_PANE), "&f", 5, ""));
		}

		getInventory().setItem(28, createPlayerSkull(eGlowPlayer));
		getInventory().setItem(30, createGlowingStatus(eGlowPlayer));
		getInventory().setItem(31, createGlowVisibility(eGlowPlayer));

		if (hasEffect(eGlowPlayer))
			getInventory().setItem(32, createItem(Material.valueOf(CLOCK), EGlowMessageConfig.Message.GUI_SPEED_ITEM_NAME.get(eGlowPlayer.getPlayer()), 0, createSpeedLore(eGlowPlayer)));

		if (EGlowMainConfig.MainConfig.SETTINGS_GUI_CUSTOM_EFFECTS.getBoolean())
			getInventory().setItem(34, setItemGlow(createItem(Material.BOOK, EGlowMessageConfig.Message.GUI_CUSTOM_EFFECTS_ITEM_NAME.get(eGlowPlayer.getPlayer()), 0, EGlowMessageConfig.Message.GUI_CLICK_TO_OPEN.get(eGlowPlayer.getPlayer()))));
	}

	public Player getiPlayer() {
		return iPlayer;
	}
}
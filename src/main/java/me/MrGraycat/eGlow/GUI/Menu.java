package me.MrGraycat.eGlow.GUI;

import me.MrGraycat.eGlow.Config.EGlowMainConfig.MainConfig;
import me.MrGraycat.eGlow.Config.EGlowMessageConfig.Message;
import me.MrGraycat.eGlow.GUI.Manager.MenuItemManager;
import me.MrGraycat.eGlow.Manager.DataManager;
import me.MrGraycat.eGlow.Manager.Interface.IEGlowEffect;
import me.MrGraycat.eGlow.Manager.Interface.IEGlowPlayer;
import me.MrGraycat.eGlow.Util.Text.ChatUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import java.util.Objects;

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
		menuMetadata.getOwner().openInventory(inventory);
	}
	
	@Override
	public Inventory getInventory() {
		return inventory;
	}
	
	/**
	 * Enable glow for a player based on the clicktype
	 * @param player to enable the glow for
	 * @param clickType left/right click
	 * @param effectName effect to check for solid/blink/effect
	 */
	public void enableGlow(Player player, ClickType clickType, String effectName) {
		IEGlowPlayer eGlowPlayer = DataManager.getEGlowPlayer(player);
		
		if (clickType.equals(ClickType.LEFT)) {
			if (DataManager.getEGlowEffect(effectName) != null) {
				IEGlowEffect color = DataManager.getEGlowEffect(effectName);
			
				if (color == null)
					return;
				
				if (!(player.hasPermission(color.getPermission()) || DataManager.isCustomEffect(color.getName()) && Objects.requireNonNull(player.getPlayer(), "Unable to retrieve player").hasPermission("eglow.effect.*"))) {
					ChatUtil.sendMsgFromGUI(player, Message.NO_PERMISSION.get(player));
					return;
				}
					
				if (eGlowPlayer.isSameGlow(color)) {
					ChatUtil.sendMsgFromGUI(player, Message.SAME_GLOW.get(player));
					return;
				}
					
				eGlowPlayer.activateGlow(color);
				ChatUtil.sendMsgFromGUI(player, Message.NEW_GLOW.get(player, color.getDisplayName(player)));
			} else if (DataManager.getEGlowEffect(effectName + "slow") != null) { //for rainbow effect 
				IEGlowEffect effect = DataManager.getEGlowEffect(effectName + "slow");
				
				if (!player.hasPermission(Objects.requireNonNull(effect, "Unable to retrieve effect from given name").getPermission())) {
					ChatUtil.sendMsgFromGUI(player, Message.NO_PERMISSION.get(player));
					return;
				}
				
				if (eGlowPlayer.isSameGlow(effect)) {
					ChatUtil.sendMsgFromGUI(player, Message.SAME_GLOW.get(player));
					return;
				}
				
				eGlowPlayer.activateGlow(effect);
				ChatUtil.sendMsgFromGUI(player, Message.NEW_GLOW.get(player, effect.getDisplayName(player)));
			}
				
		} else if (clickType.equals(ClickType.RIGHT)){
			IEGlowEffect effect = DataManager.getEGlowEffect("blink" + effectName + "slow");

			if (effect == null)
				return;
			
			if (!player.hasPermission(effect.getPermission())) {
				ChatUtil.sendMsgFromGUI(player, Message.NO_PERMISSION.get(player));
				return;
			}
			
			if (eGlowPlayer.isSameGlow(effect)) {
				ChatUtil.sendMsgFromGUI(player, Message.SAME_GLOW.get(player));
				return;
			}
			
			eGlowPlayer.activateGlow(effect);
			ChatUtil.sendMsgFromGUI(player, Message.NEW_GLOW.get(player, effect.getDisplayName(player)));
		}
	}
	
	/**
	 * Update the effects speed
	 * @param player to update the speed for
	 */
	public void updateSpeed(IEGlowPlayer player) {
		if (player.getEffect() != null) {
			String effect = player.getEffect().getName();
			IEGlowEffect eGlowEffect = null;
			
			if (effect.contains("slow"))
				eGlowEffect = DataManager.getEGlowEffect(effect.replace("slow", "fast"));
			
			if (effect.contains("fast"))
				eGlowEffect = DataManager.getEGlowEffect(effect.replace("fast", "slow"));
			
			player.activateGlow(eGlowEffect);
			ChatUtil.sendMsgFromGUI(menuMetadata.getOwner(), Message.NEW_GLOW.get(menuMetadata.getOwner(), Objects.requireNonNull(eGlowEffect, "Unable to get displayname from effect").getDisplayName(menuMetadata.getOwner())));
		}
	}
	
	/**
	 * Code to update the navigationbar for the main menu
	 * @param p to update the navigationbar for
	 */
	public void UpdateMainNavigationBar(IEGlowPlayer p) {
		if (MainConfig.SETTINGS_GUI_ADD_GLASS_PANES.getBoolean()) {
			inventory.setItem(27, createItem(Material.valueOf(GLASS_PANE), "&f", 5, ""));
			inventory.setItem(29, createItem(Material.valueOf(GLASS_PANE), "&f", 5, ""));
			inventory.setItem(32, createItem(Material.valueOf(GLASS_PANE), "&f", 5, ""));
			inventory.setItem(33, createItem(Material.valueOf(GLASS_PANE), "&f", 5, ""));
			
			inventory.setItem(34, createItem(Material.valueOf(GLASS_PANE), "&f", 5, ""));
			
			inventory.setItem(35, createItem(Material.valueOf(GLASS_PANE), "&f", 5, ""));
		}
		
		inventory.setItem(28, createPlayerSkull(p));
		inventory.setItem(30, createGlowingStatus(p));
		inventory.setItem(31, createItem(Material.NETHER_STAR, Message.GUI_COLOR.get(p.getPlayer(), "effect-rainbow"), 0, Message.GUI_LEFT_CLICK.get(p.getPlayer()) + Message.COLOR.get(p.getPlayer(), "effect-rainbow"), Message.GUI_EFFECT_PERMISSION.get(p.getPlayer()) + ((p.getPlayer().hasPermission(Objects.requireNonNull(DataManager.getEGlowEffect("rainbowslow"), "Unable to retrieve effect from given name").getPermission()) ? Message.GUI_YES.get(p.getPlayer()) : Message.GUI_NO.get(p.getPlayer())))));

		if (hasEffect(p))
			inventory.setItem(32, createItem(Material.valueOf(CLOCK), Message.GUI_SPEED_ITEM_NAME.get(p.getPlayer()), 0, createSpeedLore(p)));

		if (MainConfig.SETTINGS_GUI_CUSTOM_EFFECTS.getBoolean())
			inventory.setItem(34, setItemGlow(createItem(Material.BOOK, Message.GUI_CUSTOM_EFFECTS_ITEM_NAME.get(p.getPlayer()), 0, Message.GUI_CLICK_TO_OPEN.get(p.getPlayer()))));
	}

	public Player getIPlayer() {
		return iPlayer;
	}
}
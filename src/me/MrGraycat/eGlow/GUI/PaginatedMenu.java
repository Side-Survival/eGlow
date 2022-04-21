package me.MrGraycat.eGlow.GUI;

import org.bukkit.Material;
import org.bukkit.entity.Player;

import me.MrGraycat.eGlow.Config.EGlowMainConfig;
import me.MrGraycat.eGlow.Config.EGlowMessageConfig.Message;
import me.MrGraycat.eGlow.Manager.DataManager;
import me.MrGraycat.eGlow.Manager.Interface.IEGlowPlayer;
import me.MrGraycat.eGlow.Util.Packets.ProtocolVersion;

public abstract class PaginatedMenu extends Menu {
	protected int page = 1;
	protected int maxItemsPerPage = 26;
	
	public PaginatedMenu(Player player) {
		super(player);
	}
	
	public int getMaxItemsPerPage() {
		return maxItemsPerPage;
	}
	
	/**
	 * Update the navigationbar in the custom effects gui
	 * @param p player to update the navigation bar for
	 */
	public void UpdateMainEffectsNavigationBar(IEGlowPlayer p) {
		if (EGlowMainConfig.OptionAddGlassToInv()) {
			inventory.setItem(27, createItem(Material.valueOf(GLASS_PANE), "&f", 5, ""));
			inventory.setItem(30, createItem(Material.valueOf(GLASS_PANE), "&f", 5, ""));
			inventory.setItem(31, createItem(Material.valueOf(GLASS_PANE), "&f", 5, ""));
			inventory.setItem(32, createItem(Material.valueOf(GLASS_PANE), "&f", 5, ""));
			inventory.setItem(35, createItem(Material.valueOf(GLASS_PANE), "&f", 5, ""));
		}
		
		inventory.setItem(28, createPlayerSkull(p));
		inventory.setItem(29, createGlowingStatus(p));
		inventory.setItem(33, createItem((ProtocolVersion.SERVER_VERSION.getMinorVersion() >= 14) ? Material.valueOf("OAK_SIGN") : Material.valueOf("SIGN"), Message.GUI_PREVIOUS_PAGE.get(), 0, Message.GUI_PAGE_LORE.get((page == 1) ? Message.GUI_MAIN_MENU.get() : page - 1 + "")));
		inventory.setItem(34, createItem((ProtocolVersion.SERVER_VERSION.getMinorVersion() >= 14) ? Material.valueOf("OAK_SIGN") : Material.valueOf("SIGN"), Message.GUI_NEXT_PAGE.get(), 0, Message.GUI_PAGE_LORE.get((DataManager.getCustomEffects().size() < (page * getMaxItemsPerPage())) ? Message.GUI_NOT_AVAILABLE.get() : page + 1 + "")));
	}
}

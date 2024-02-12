package me.MrGraycat.eglow.gui.menus;

import lombok.Getter;
import me.MrGraycat.eglow.Util.enums.EnumUtil;
import me.MrGraycat.eglow.Util.packets.ProtocolVersion;
import me.MrGraycat.eglow.Util.text.ChatUtil;
import me.MrGraycat.eglow.config.EGlowCustomEffectsConfig;
import me.MrGraycat.eglow.config.EGlowMainConfig;
import me.MrGraycat.eglow.config.EGlowMessageConfig;
import me.MrGraycat.eglow.data.DataManager;
import me.MrGraycat.eglow.data.EGlowEffect;
import me.MrGraycat.eglow.data.EGlowPlayer;
import me.MrGraycat.eglow.gui.PaginatedMenu;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public class EGlowEffectMenu extends PaginatedMenu {
	private ConcurrentHashMap<Integer, String> effects = new ConcurrentHashMap<>();

	public EGlowEffectMenu(Player player) {
		super(player);
	}

	@Override
	public String getMenuName() {
		return ChatUtil.translateColors(((EGlowMainConfig.MainConfig.SETTINGS_GUI_ADD_PREFIX.getBoolean()) ? EGlowMessageConfig.Message.GUI_TITLE.get(getiPlayer()) : EGlowMessageConfig.Message.PREFIX.get(getiPlayer()) + EGlowMessageConfig.Message.GUI_TITLE.get(getiPlayer())));
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

		if ((System.currentTimeMillis() - getMenuMetadata().getLastClicked()) < EGlowMainConfig.MainConfig.SETTINGS_GUIS_INTERACTION_DELAY.getLong()) {
			ChatUtil.sendMsgFromGUI(player, EGlowMessageConfig.Message.GUI_COOLDOWN.get(player));
			return;
		}

		getMenuMetadata().setLastClicked(System.currentTimeMillis());

		switch (clickedSlot) {
			case (28):
				if (eGlowPlayer.skipSaveData())
					eGlowPlayer.setSaveData(true);

				eGlowPlayer.setGlowOnJoin(!eGlowPlayer.isGlowOnJoin());
				break;
			case (29):
				if (eGlowPlayer.getPlayer().hasPermission("eglow.command.toggle")) {
					if (eGlowPlayer.isGlowing()) {
						eGlowPlayer.disableGlow(false);
						ChatUtil.sendMsgFromGUI(player, EGlowMessageConfig.Message.DISABLE_GLOW.get(player));
					} else {
						if (eGlowPlayer.getGlowEffect() == null || eGlowPlayer.getGlowEffect().getName().equals("none")) {
							ChatUtil.sendMsgFromGUI(player, EGlowMessageConfig.Message.NO_LAST_GLOW.get(player));
							return;
						}

						switch (eGlowPlayer.getGlowDisableReason()) {
							case BLOCKEDWORLD:
								ChatUtil.sendMsgFromGUI(player, EGlowMessageConfig.Message.WORLD_BLOCKED.get(player));
								return;
							case INVISIBLE:
								ChatUtil.sendMsgFromGUI(player, EGlowMessageConfig.Message.INVISIBILITY_BLOCKED.get(player));
								return;
							case ANIMATION:
								ChatUtil.sendMsgFromGUI(player, EGlowMessageConfig.Message.ANIMATION_BLOCKED.get(player));
								return;
						}

						EGlowEffect currentEGlowEffect = eGlowPlayer.getGlowEffect();

						if (eGlowPlayer.hasPermission(currentEGlowEffect.getPermissionNode()) || (DataManager.isCustomEffect(currentEGlowEffect.getName()) && eGlowPlayer.hasPermission("eglow.egloweffect.*")) || eGlowPlayer.isForcedGlow(currentEGlowEffect)) {
							eGlowPlayer.activateGlow();
						} else {
							ChatUtil.sendMsgFromGUI(player, EGlowMessageConfig.Message.NO_PERMISSION.get(player));
							return;
						}
						ChatUtil.sendMsgFromGUI(player, EGlowMessageConfig.Message.NEW_GLOW.get(player, eGlowPlayer.getLastGlowName()));
					}
				} else {
					ChatUtil.sendMsgFromGUI(player, EGlowMessageConfig.Message.NO_PERMISSION.get(player));
				}
				break;
			case (33):
				if (getPage() == 1) {
					new EGlowMainMenu(eGlowPlayer).openInventory();
				} else {
					page--;
					super.openInventory();
				}
				break;
			case (34):
				if (hasNextPage()) {
					page++;
					super.openInventory();
				}
				break;
			default:
				if (getEffects().containsKey(clickedSlot)) {
					String effect = getEffects().get(clickedSlot);
					enableGlow(eGlowPlayer.getPlayer(), clickType, effect);
				}
				break;
		}

		UpdateMainEffectsNavigationBar(eGlowPlayer);
	}

	@Override
	public void setMenuItems() {
		Player player = getMenuMetadata().getOwner();
		EGlowPlayer eGlowPlayer = DataManager.getEGlowPlayer(player);
		effects = new ConcurrentHashMap<>();
		UpdateMainEffectsNavigationBar(eGlowPlayer);
		setHasNextPage(false);

		int slot = 0;
		int currentEffectSlot = 0;
		int nextEffectSlot = (26 * (getPage() - 1)) + ((getPage() > 1) ? 1 : 0);

		for (String effect : EGlowCustomEffectsConfig.Effect.GET_ALL_EFFECTS.get()) {
			EGlowEffect eGlowEffect = DataManager.getEGlowEffect(effect.toLowerCase());
			if (eGlowEffect == null)
				continue;

			if (player.hasPermission(eGlowEffect.getPermissionNode()) || player.hasPermission("eglow.effect.*")) {
				if (currentEffectSlot != nextEffectSlot) {
					currentEffectSlot++;
					continue;
				}

				if (slot > getMaxItemsPerPage()) {
					setHasNextPage(true);
					UpdateMainEffectsNavigationBar(eGlowPlayer);
					return;
				}


				Material material = getMaterial(effect);
				String name = getName(effect);
				int meta = getMeta(effect);
				int model = getModelID(effect);
				ArrayList<String> lores = new ArrayList<>();

				for (String lore : EGlowCustomEffectsConfig.Effect.GET_LORES.getList(effect)) {
					lore = ChatUtil.translateColors(lore.replace("%effect_name%", eGlowEffect.getDisplayName()).replace("%effect_has_permission%", hasPermission(eGlowPlayer, eGlowEffect.getPermissionNode())));
					lores.add(lore);
				}

				getInventory().setItem(slot, createItem(material, name, meta, lores, model));

				if (!getEffects().containsKey(slot))
					getEffects().put(slot, eGlowEffect.getName());

				slot++;
			}
		}
	}

	private Material getMaterial(String effect) {
		String material = EGlowCustomEffectsConfig.Effect.GET_MATERIAL.getString(effect).toUpperCase();
		try {
			if (ProtocolVersion.SERVER_VERSION.getMinorVersion() >= 13) {
				switch (material) {
					case "SAPLING":
						material = "SPRUCE_SAPLING";
						break;
					case "PUMPKIN":
						material = "CARVED_PUMPKIN";
				}
			}

			return Material.valueOf(material);
		} catch (IllegalArgumentException | NullPointerException e) {
			ChatUtil.sendToConsole("Material: " + material + " for effect " + effect + "is not valid.", true);
			return Material.valueOf("DIRT");
		}
	}

	private String getName(String effect) {
		return EGlowCustomEffectsConfig.Effect.GET_NAME.getString(effect);
	}

	private int getMeta(String effect) {
		return EGlowCustomEffectsConfig.Effect.GET_META.getInt(effect);
	}

	private int getModelID(String effect) {
		return (ProtocolVersion.SERVER_VERSION.getMinorVersion() >= 14) ? EGlowCustomEffectsConfig.Effect.GET_MODEL_ID.getInt(effect) : -1;
	}
}
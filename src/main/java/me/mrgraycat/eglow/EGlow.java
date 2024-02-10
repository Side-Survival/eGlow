package me.MrGraycat.eglow;

import lombok.Getter;
import lombok.Setter;
import me.MrGraycat.eglow.API.EGlowAPI;
import me.MrGraycat.eglow.Util.DebugUtil;
import me.MrGraycat.eglow.Util.enums.Dependency;
import me.MrGraycat.eglow.Util.packets.NMSHook;
import me.MrGraycat.eglow.Util.packets.ProtocolVersion;
import me.MrGraycat.eglow.addon.LuckPermsAddon;
import me.MrGraycat.eglow.addon.PlaceholderAPIAddon;
import me.MrGraycat.eglow.addon.VaultAddon;
import me.MrGraycat.eglow.addon.internal.AdvancedGlowVisibilityAddon;
import me.MrGraycat.eglow.command.EGlowCommand;
import me.MrGraycat.eglow.data.DataManager;
import me.MrGraycat.eglow.database.EGlowPlayerdataManager;
import me.MrGraycat.eglow.event.EGlowEventListener;
import me.MrGraycat.eglow.addon.citizens.CitizensAddon;
import me.MrGraycat.eglow.addon.tab.TABAddon;
import me.MrGraycat.eglow.config.EGlowCustomEffectsConfig;
import me.MrGraycat.eglow.config.EGlowMainConfig;
import me.MrGraycat.eglow.config.EGlowMainConfig.MainConfig;
import me.MrGraycat.eglow.config.EGlowMessageConfig;
import me.MrGraycat.eglow.Util.text.ChatUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Objects;

@Getter
@Setter
public class EGlow extends JavaPlugin {
	private static EGlow instance;
	private static EGlowAPI api;
	private boolean upToDate = true;

	//Addons
	private AdvancedGlowVisibilityAddon advancedGlowVisibilityAddon;
	private CitizensAddon citizensAddon;
	private TABAddon tabAddon;
	private LuckPermsAddon lpAddon;
	private VaultAddon vaultAddon;

	@Override
	public void onEnable() {
		instance = this;
		api = new EGlowAPI();

		if (versionIsCompactible()) {
			ProtocolVersion.SERVER_VERSION = ProtocolVersion.fromServerString(Bukkit.getBukkitVersion().split("-")[0]);

			NMSHook.initialize();

			loadConfigs();

			DataManager.initialize();

			registerEventsAndCommands();
			checkForUpdates();
			runAddonHooks();
			runPlayerCheckOnEnable();
		} else {
			ChatUtil.sendToConsole("Disabling eGlow! Your server version is not compatible! (" + DebugUtil.getServerVersion() + ")", false);
			getServer().getPluginManager().disablePlugin(this);
		}
	}

	@Override
	public void onDisable() {
		if (getAdvancedGlowVisibilityAddon() != null) {
			getAdvancedGlowVisibilityAddon().shutdown();
		}

		if (getLpAddon() != null) {
			getLpAddon().unload();
		}

		runPlayerCheckOnDisable();
	}

	private boolean versionIsCompactible() {
		return !DebugUtil.getServerVersion().equals("v_1_9_R1") && DebugUtil.getMinorVersion() >= 9 && DebugUtil.getMinorVersion() <= 20;
	}

	private void loadConfigs() {
		EGlowMainConfig.initialize();
		EGlowMessageConfig.initialize();
		EGlowCustomEffectsConfig.initialize();
		EGlowPlayerdataManager.initialize();
	}

	private void registerEventsAndCommands() {
		Objects.requireNonNull(getCommand("eglow")).setExecutor(new EGlowCommand());
		new EGlowEventListener();
	}

	private void runAddonHooks() {
		new BukkitRunnable() {
			@Override
			public void run() {
				if (MainConfig.ADVANCED_GLOW_VISIBILITY_ENABLE.getBoolean() && getAdvancedGlowVisibilityAddon() == null)
					setAdvancedGlowVisibilityAddon(new AdvancedGlowVisibilityAddon());
				if (Dependency.PLACEHOLDER_API.isLoaded())
					new PlaceholderAPIAddon();
				if (Dependency.VAULT.isLoaded())
					setVaultAddon(new VaultAddon(getInstance()));
				if (Dependency.CITIZENS.isLoaded() && getCitizensAddon() == null)
					setCitizensAddon(new CitizensAddon());
				if (Dependency.LUCK_PERMS.isLoaded()) {
					setLpAddon(new LuckPermsAddon(getInstance()));
				}

				setTabAddon(new TABAddon(getInstance()));
			}
		}.runTask(this);
	}

	private void runPlayerCheckOnEnable() {
		if (!getServer().getOnlinePlayers().isEmpty()) {
			for (Player player : getServer().getOnlinePlayers()) {
				if (DataManager.getEGlowPlayer(player) == null)
					EGlowEventListener.PlayerConnect(player, player.getUniqueId());
			}
		}
	}

	private void runPlayerCheckOnDisable() {
		if (!getServer().getOnlinePlayers().isEmpty()) {
			for (Player player : getServer().getOnlinePlayers()) {
				if (DataManager.getEGlowPlayer(player) == null)
					EGlowEventListener.PlayerDisconnect(player);
			}
		}
	}

	private void checkForUpdates() {
		try {
			URL url = new URL("https://api.spigotmc.org/legacy/update.php?resource=63295");
			String currentVersion = getInstance().getDescription().getVersion();
			String latestVersion = new BufferedReader(new InputStreamReader(url.openConnection().getInputStream())).readLine();

			if (currentVersion.contains("PRE") || currentVersion.contains("SNAPSHOT")) {
				String betaVersion = currentVersion.split("-")[0];
				setUpToDate(!betaVersion.equals(latestVersion));
			} else {
				if (!latestVersion.contains(currentVersion)) {
					setUpToDate(false);
				}
			}
		} catch (Exception exception) {
			//None would care if this fails
		}
	}

	//Getter
	public static EGlow getInstance() {
		return EGlow.instance;
	}

	public static EGlowAPI getAPI() {
		return api;
	}
}
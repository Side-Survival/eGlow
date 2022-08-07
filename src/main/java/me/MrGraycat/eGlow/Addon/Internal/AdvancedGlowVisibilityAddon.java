package me.MrGraycat.eGlow.Addon.Internal;

import me.MrGraycat.eGlow.Config.EGlowMainConfig;
import me.MrGraycat.eGlow.EGlow;
import me.MrGraycat.eGlow.Manager.DataManager;
import me.MrGraycat.eGlow.Manager.Interface.IEGlowPlayer;
import me.MrGraycat.eGlow.Util.BiPair;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.Vector;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class AdvancedGlowVisibilityAddon {

    private static final double FRUSTUM_SIZE = Math.toRadians(120); // How large the frustum volume is, in radians.
    private static final int MAX_DISTANCE = 50;

    private static final Set<Material> ignoredBlocks = EnumSet.noneOf(Material.class);

    static {
        // This config is internal only, and is assumed to be correct.
        InputStream resource = EGlow.getInstance().getResource("internal/AdvancedGlowIgnoreList.yml");
        YamlConfiguration config = YamlConfiguration.loadConfiguration(new InputStreamReader(resource));
        List<Material> materials = config.getStringList("ignored-blocks").stream()
                .map(Material::matchMaterial)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        ignoredBlocks.addAll(materials);
    }

    private boolean FORCE_STOP = false;
    private final Map<UUID, Location> cache = Collections.synchronizedMap(new HashMap<>());

    public AdvancedGlowVisibilityAddon() {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (getForceStop()) {
                    cancel();
                    EGlow.getInstance().setAdvancedGlowVisibility(null);
                    return;
                }

                Collection<IEGlowPlayer> ePlayers = DataManager.getEGlowPlayers();

                List<BiPair<UUID, UUID>> checkedPlayers = new ArrayList<>(ePlayers.size());

                for (IEGlowPlayer ePlayer : ePlayers) {
                    Player player = ePlayer.getPlayer();
                    Location playerLoc = player.getEyeLocation();

                    if (checkLocationCache(ePlayer, playerLoc))
                        continue;

                    List<IEGlowPlayer> nearbyEPlayers = new ArrayList<>();
                    for (Player p : playerLoc.getWorld().getPlayers()) {
                        if (p != player && distance(p.getEyeLocation(), playerLoc) < MAX_DISTANCE) {
                            nearbyEPlayers.add(DataManager.getEGlowPlayer(p));
                        }
                    }

                    Vector playerDir = playerLoc.getDirection();

                    for (IEGlowPlayer nearby : nearbyEPlayers) {
                        BiPair<UUID, UUID> pair = new BiPair<>(ePlayer.getUUID(), nearby.getUUID());
                        if (checkedPlayers.contains(pair)) {
                            continue; // We've already checked visibility between these two players.
                        }

                        Location nearbyLoc = nearby.getPlayer().getEyeLocation();
                        Vector nearbyDir = nearbyLoc.getDirection();

                        double angle = nearbyDir.angle(playerDir);
                        if (angle > FRUSTUM_SIZE) {
                            toggleGlow(ePlayer, nearby, false);
                            continue;
                        } else {
                            Raytrace trace = new Raytrace(playerLoc, nearbyLoc);
                            boolean hasLineOfSight = trace.hasLineOfSight();
                            toggleGlow(ePlayer, nearby, hasLineOfSight);
                        }
                        checkedPlayers.add(pair);
                    }
                }
            }
        }.runTaskTimerAsynchronously(EGlow.getInstance(), 0, Math.max(EGlowMainConfig.MainConfig.ADVANCED_GLOW_VISIBILITY_DELAY.getInt(), 10));
    }

    /**
     * Checks if a players location has changed from the cached value, updating the cache if necessary.
     *
     * @param ePlayer   The player whose cache to check against.
     * @param playerLoc The location to check against.
     * @return True if the players location hasn't changed, false otherwise.
     */
    private boolean checkLocationCache(IEGlowPlayer ePlayer, Location playerLoc) {
        Location cached = cache.get(ePlayer.getUUID());

        if (cached == null) {
            cache.put(ePlayer.getUUID(), playerLoc);
        } else {
            if (cached.equals(playerLoc)) {
                return true;
            } else {
                cache.replace(ePlayer.getUUID(), playerLoc);
            }
        }
        return false;
    }

    /**
     * Toggles glow visibility for two players.
     *
     * @param p1     Player 1.
     * @param p2     Player 2.
     * @param toggle Whether to enable, or disable glow.
     */
    private void toggleGlow(IEGlowPlayer p1, IEGlowPlayer p2, boolean toggle) {
        if (toggle) {
            p1.addGlowTarget(p2.getPlayer());
            p2.addGlowTarget(p1.getPlayer());
        } else {
            p1.removeGlowTarget(p2.getPlayer());
            p2.removeGlowTarget(p1.getPlayer());
        }
    }

    public void uncachePlayer(IEGlowPlayer ePlayer) {
        cache.remove(ePlayer.getUUID());
    }

    public void shutdown() {
        setForceStop();
    }

    private int distance(Location start, Location end) {
        return (int) Math.floor(Math.sqrt(Math.pow((start.getX() - end.getX()), 2) + Math.pow((start.getY() - end.getY()), 2) + Math.pow((start.getZ() - end.getZ()), 2)));
    }

    private void setForceStop() {
        this.FORCE_STOP = true;
    }

    private boolean getForceStop() {
        return this.FORCE_STOP;
    }

    public static class Raytrace {

        private final Location origin;
        private final Location target;
        private final Vector direction;

        protected Raytrace(Location origin, Location target) {
            this.origin = origin;
            this.target = target;
            this.direction = target.clone().toVector().subtract(origin.clone().toVector()).normalize();
        }

        protected boolean hasLineOfSight() {
            if (origin.equals(target))
                return true;

            BlockIterator blocks = new BlockIterator(origin.getWorld(), origin.toVector(), direction, 0.0, distance());

            while (blocks.hasNext()) {
                Block block = blocks.next();

                if (!block.isLiquid() && !block.isPassable() && !AdvancedGlowVisibilityAddon.ignoredBlocks.contains(block.getType())) {
                    return false;
                }
            }
            return true;
        }

        private int distance() {
            return (int) Math.floor(Math.sqrt(Math.pow((origin.getX() - target.getX()), 2) + Math.pow((origin.getY() - target.getY()), 2) + Math.pow((origin.getZ() - target.getZ()), 2)));
        }

    }

}
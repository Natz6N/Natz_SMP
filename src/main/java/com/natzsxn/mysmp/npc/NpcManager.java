package com.natzsxn.mysmp.npc;

import com.natzsxn.mysmp.config.ConfigManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.UUID;

public class NpcManager implements Listener {
    private final JavaPlugin plugin;
    private final ConfigManager config;
    private final NamespacedKey KEY_TYPE;
    private UUID warpZombieId;
    private UUID warpLine2Id;
    private UUID welcomeLine1Id;
    private UUID welcomeLine2Id;

    public NpcManager(JavaPlugin plugin, ConfigManager config) {
        this.plugin = plugin;
        this.config = config;
        this.KEY_TYPE = new NamespacedKey(plugin, "mysmp_npc_type");
    }

    public void spawnAll() {
        cleanupTagged();
        spawnWarpZombie();
        spawnWelcomeSteve();
        startWelcomeUpdater();
    }

    public void reloadAll() {
        cleanupTagged();
        spawnAll();
    }

    private World resolveWorld() {
        String lobby = config.getLobbyWorldName();
        World w = Bukkit.getWorld(lobby);
        if (w == null) {
            Bukkit.getLogger().warning("MySMP: Lobby world '" + lobby + "' not found; NPC not spawned.");
        }
        return w;
    }

    private void spawnWarpZombie() {
        World w = resolveWorld();
        if (w == null) return;
        Location loc = new Location(w, -31, 84, -26);
        Zombie z = w.spawn(loc, Zombie.class, e -> {
            e.setAI(false);
            e.setSilent(true);
            e.setInvulnerable(true);
            e.setRemoveWhenFarAway(false);
            e.setPersistent(true);
            e.setCustomName("§aWarp Survival");
            e.setCustomNameVisible(true);
            e.getPersistentDataContainer().set(KEY_TYPE, PersistentDataType.STRING, "warp_zombie");
            ItemStack helm = new ItemStack(Material.NETHERITE_HELMET);
            e.getEquipment().setHelmet(helm);
            e.getEquipment().setHelmetDropChance(0f);
        });
        warpZombieId = z.getUniqueId();

        ArmorStand line2 = w.spawn(loc.clone().add(0, 0.6, 0), ArmorStand.class, a -> {
            a.setInvisible(true);
            a.setMarker(true);
            a.setGravity(false);
            a.setInvulnerable(true);
            a.setCustomNameVisible(true);
            a.setCustomName("§7Klik mobs untuk masuk ke dunia survival");
            a.getPersistentDataContainer().set(KEY_TYPE, PersistentDataType.STRING, "warp_zombie_line2");
        });
        warpLine2Id = line2.getUniqueId();
    }

    private void spawnWelcomeSteve() {
        World w = resolveWorld();
        if (w == null) return;
        Location base = new Location(w, 0, 84, -22);
        ArmorStand line1 = w.spawn(base, ArmorStand.class, a -> {
            a.setInvisible(false);
            a.setMarker(false);
            a.setArms(true);
            a.setBasePlate(false);
            a.setGravity(false);
            a.setInvulnerable(true);
            a.setRemoveWhenFarAway(false);
            a.setPersistent(true);
            a.setCustomNameVisible(true);
            a.setCustomName("§bWelcome {player}");
            a.getPersistentDataContainer().set(KEY_TYPE, PersistentDataType.STRING, "welcome_line1");
            ItemStack head = new ItemStack(Material.PLAYER_HEAD);
            SkullMeta sm = (SkullMeta) head.getItemMeta();
            if (sm != null) {
                sm.setOwningPlayer(Bukkit.getOfflinePlayer("Steve"));
                head.setItemMeta(sm);
            }
            a.getEquipment().setHelmet(head);
        });
        welcomeLine1Id = line1.getUniqueId();

        ArmorStand line2 = w.spawn(base.clone().add(0, 0.6, 0), ArmorStand.class, a -> {
            a.setInvisible(true);
            a.setMarker(true);
            a.setGravity(false);
            a.setInvulnerable(true);
            a.setCustomNameVisible(true);
            a.setCustomName("§7Terimakasih yang sudah bergabung hehe :)");
            a.getPersistentDataContainer().set(KEY_TYPE, PersistentDataType.STRING, "welcome_line2");
        });
        welcomeLine2Id = line2.getUniqueId();
    }

    private void startWelcomeUpdater() {
        Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            Entity e = getEntityById(welcomeLine1Id);
            if (!(e instanceof ArmorStand)) return;
            Player nearest = null;
            double best = Double.MAX_VALUE;
            for (Player p : e.getWorld().getPlayers()) {
                double d = p.getLocation().distance(e.getLocation());
                if (d <= 5.0 && d < best) { best = d; nearest = p; }
            }
            String name = "§bWelcome {player}";
            if (nearest != null) name = "§bWelcome " + nearest.getName();
            ((ArmorStand) e).setCustomName(name);
        }, 40L, 40L);
    }

    private Entity getEntityById(UUID id) {
        if (id == null) return null;
        for (World w : Bukkit.getWorlds()) {
            Entity e = w.getEntities().stream().filter(en -> en.getUniqueId().equals(id)).findFirst().orElse(null);
            if (e != null) return e;
        }
        return null;
    }

    private void cleanupTagged() {
        for (World w : Bukkit.getWorlds()) {
            for (Entity e : w.getEntities()) {
                if (e.getPersistentDataContainer().has(KEY_TYPE, PersistentDataType.STRING)) {
                    e.remove();
                }
            }
        }
    }

    @EventHandler
    public void onInteractEntity(PlayerInteractEntityEvent e) {
        Entity clicked = e.getRightClicked();
        String type = clicked.getPersistentDataContainer().get(KEY_TYPE, PersistentDataType.STRING);
        if (type == null) return;
        if (type.equals("warp_zombie")) {
            Bukkit.dispatchCommand(e.getPlayer(), "warp survival");
        }
    }
}

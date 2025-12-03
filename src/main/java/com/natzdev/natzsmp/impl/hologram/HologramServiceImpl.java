package com.natzdev.natzsmp.impl.hologram;

import com.natzdev.natzsmp.api.hologram.Hologram;
import com.natzdev.natzsmp.api.hologram.HologramService;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class HologramServiceImpl implements HologramService {
    private final JavaPlugin plugin;
    private final Map<UUID, Hologram> holograms = new ConcurrentHashMap<>();

    public HologramServiceImpl(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    // ==================== CREATE ====================

    @Override
    public Hologram create(String name, Location location) {
        return create(name, location, new ArrayList<>(), true);
    }

    @Override
    public Hologram create(String name, Location location, List<Component> lines) {
        return create(name, location, lines, true);
    }

    @Override
    public Hologram create(String name, Location location, boolean saveToFile) {
        return create(name, location, new ArrayList<>(), saveToFile);
    }

    @Override
    public Hologram create(String name, Location location, List<Component> lines, boolean saveToFile) {
        // saveToFile flag is ignored for now; BasicHologram persistence is in-memory only
        Hologram holo = new BasicHologram(plugin, name, location, lines, true);
        holograms.put(holo.getId(), holo);
        return holo;
    }

    // ==================== QUERY ====================

    @Override
    public Optional<Hologram> get(UUID id) {
        return Optional.ofNullable(holograms.get(id));
    }

    @Override
    public Optional<Hologram> getByName(String name) {
        if (name == null) return Optional.empty();
        return holograms.values().stream()
                .filter(h -> name.equalsIgnoreCase(h.getName()))
                .findFirst();
    }

    @Override
    public Collection<Hologram> getAll() {
        return Collections.unmodifiableCollection(holograms.values());
    }

    // ==================== REMOVE ====================

    @Override
    public void remove(UUID id) {
        Hologram holo = holograms.remove(id);
        if (holo != null) holo.delete();
    }

    @Override
    public void removeByName(String name) {
        getByName(name).ifPresent(holo -> {
            holograms.remove(holo.getId());
            holo.delete();
        });
    }

    @Override
    public void removeAll() {
        holograms.values().forEach(Hologram::delete);
        holograms.clear();
    }

    // ==================== EXISTENCE & COUNT ====================

    @Override
    public boolean exists(String name) {
        return getByName(name).isPresent();
    }

    @Override
    public boolean exists(UUID id) {
        return holograms.containsKey(id);
    }

    @Override
    public int getCount() {
        return holograms.size();
    }

    // ==================== PERSISTENCE HOOKS ====================

    @Override
    public void saveAll() {
        holograms.values().forEach(Hologram::save);
    }

    @Override
    public void loadAll() {
        // No backing storage yet; nothing to load
    }

    // ==================== VISIBILITY HELPERS ====================

    @Override
    public void showAll(org.bukkit.entity.Player player) {
        if (player == null) return;
        holograms.values().forEach(h -> h.show(player));
    }

    @Override
    public void hideAll(org.bukkit.entity.Player player) {
        if (player == null) return;
        holograms.values().forEach(h -> h.hide(player));
    }
}

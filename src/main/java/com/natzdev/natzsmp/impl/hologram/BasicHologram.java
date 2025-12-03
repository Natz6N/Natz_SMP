package com.natzdev.natzsmp.impl.hologram;

import com.natzdev.natzsmp.api.hologram.Hologram;
import com.natzdev.natzsmp.api.hologram.HologramFlag;
import com.natzdev.natzsmp.api.hologram.HologramLine;
import com.natzdev.natzsmp.api.hologram.HologramLineType;
import com.natzdev.natzsmp.api.hologram.HologramPage;
import com.natzdev.natzsmp.nms.api.NmsAdapter;
import com.natzdev.natzsmp.nms.api.NmsHologramPartData;
import com.natzdev.natzsmp.nms.api.renderer.NmsHologramRendererFactory;
import com.natzdev.natzsmp.nms.api.renderer.NmsTextHologramRenderer;
import com.natzdev.natzsmp.util.ServiceRegistry;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public class BasicHologram implements Hologram {
    private final UUID id = UUID.randomUUID();
    private final JavaPlugin plugin;

    private final String name;
    private Location location;
    private final boolean global;

    // Backward-compatible lines representation (first page text lines)
    private List<Component> lines;

    // Viewers tracking
    private final Set<UUID> viewers = new HashSet<>();

    // Flags / state
    private boolean enabled = true;
    private boolean downOrigin = false;
    private boolean alwaysFacePlayer = false;
    private String permission;
    private final Set<HologramFlag> flags = EnumSet.noneOf(HologramFlag.class);

    // Ranges & intervals (no ticking yet, only stored values)
    private int displayRange = 32;
    private int updateRange = 32;
    private int updateInterval = 20;

    private static final double LINE_SPACING = 0.27D;
    private final List<NmsTextHologramRenderer> renderers = new ArrayList<>();
    private final NmsHologramRendererFactory rendererFactory;

    // Simple single-page model backed by "lines"
    private final List<HologramPage> pages = new ArrayList<>();

    public BasicHologram(JavaPlugin plugin, Location location, List<Component> lines, boolean global) {
        this(plugin, null, location, lines, global);
    }

    public BasicHologram(JavaPlugin plugin, String name, Location location, List<Component> lines, boolean global) {
        this.plugin = plugin;
        this.name = (name != null && !name.isEmpty()) ? name : "holo-" + id.toString().substring(0, 8);
        this.location = location.clone();
        this.lines = new ArrayList<>(lines);
        this.global = global;
        NmsAdapter api = ServiceRegistry.get(NmsAdapter.class);
        this.rendererFactory = api != null ? api.getHologramComponentFactory() : null;
        ensureRenderers();

        // Initialize single page view over lines
        this.pages.add(new BasicHologramPage(0));
    }

    // ==================== CORE IDENTIFICATION ====================

    @Override
    public UUID getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    // ==================== LOCATION ====================

    @Override
    public Location getLocation() {
        return location.clone();
    }

    @Override
    public void setLocation(Location location) {
        this.location = location.clone();
    }

    // ==================== PAGES ====================

    @Override
    public List<HologramPage> getPages() {
        return Collections.unmodifiableList(pages);
    }

    @Override
    public Optional<HologramPage> getPage(int index) {
        if (index < 0 || index >= pages.size()) {
            return Optional.empty();
        }
        return Optional.of(pages.get(index));
    }

    @Override
    public int getPageCount() {
        return pages.size();
    }

    @Override
    public HologramPage addPage() {
        int index = pages.size();
        HologramPage page = new BasicHologramPage(index);
        pages.add(page);
        return page;
    }

    @Override
    public HologramPage insertPage(int index) {
        if (index < 0 || index > pages.size()) {
            return addPage();
        }
        HologramPage page = new BasicHologramPage(index);
        pages.add(index, page);
        // Re-index following pages
        for (int i = index + 1; i < pages.size(); i++) {
            ((BasicHologramPage) pages.get(i)).index = i;
        }
        return page;
    }

    @Override
    public Optional<HologramPage> removePage(int index) {
        if (index < 0 || index >= pages.size()) {
            return Optional.empty();
        }
        HologramPage removed = pages.remove(index);
        for (int i = index; i < pages.size(); i++) {
            ((BasicHologramPage) pages.get(i)).index = i;
        }
        return Optional.of(removed);
    }

    @Override
    public HologramPage getFirstPage() {
        if (pages.isEmpty()) {
            return addPage();
        }
        return pages.get(0);
    }

    // ==================== VISIBILITY ====================

    @Override
    public void show(Player player) {
        if (!enabled) {
            return;
        }
        if (permission != null && !permission.isEmpty() && !player.hasPermission(permission)) {
            return;
        }
        ensureRenderers();
        for (int i = 0; i < renderers.size(); i++) {
            NmsTextHologramRenderer r = renderers.get(i);
            int index = i;
            NmsHologramPartData<Component> data = new NmsHologramPartData<>(
                    () -> linePosition(index),
                    () -> lines.get(index)
            );
            r.display(player, data);
        }
        viewers.add(player.getUniqueId());
    }

    @Override
    public void hide(Player player) {
        for (NmsTextHologramRenderer r : renderers) {
            r.hide(player);
        }
        viewers.remove(player.getUniqueId());
    }

    @Override
    public void showAll() {
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            show(player);
        }
    }

    @Override
    public void hideAll() {
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            hide(player);
        }
    }

    @Override
    public void update(Player player) {
        // Simple implementation: re-show to player
        if (viewers.contains(player.getUniqueId())) {
            hide(player);
            show(player);
        }
    }

    @Override
    public void updateAll() {
        for (UUID uuid : new HashSet<>(viewers)) {
            Player p = plugin.getServer().getPlayer(uuid);
            if (p != null && p.isOnline()) {
                update(p);
            }
        }
    }

    // ==================== MOVEMENT ====================

    @Override
    public void teleport(Location location) {
        this.location = location.clone();
        for (UUID uuid : viewers) {
            Player p = plugin.getServer().getPlayer(uuid);
            if (p == null) continue;
            for (int i = 0; i < renderers.size(); i++) {
                NmsTextHologramRenderer r = renderers.get(i);
                int index = i;
                r.move(p, new NmsHologramPartData<>(() -> linePosition(index), () -> lines.get(index)));
            }
        }
    }

    @Override
    public void realignLines() {
        // For this basic implementation, line positions are computed on the fly
        updateAll();
    }

    @Override
    public void delete() {
        viewers.clear();
        for (Player p : plugin.getServer().getOnlinePlayers()) {
            hide(p);
        }
    }

    // ==================== STATE & FLAGS ====================

    @Override
    public boolean isValid() {
        return location != null;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        if (!enabled) {
            hideAll();
        } else {
            showAll();
        }
    }

    @Override
    public int getDisplayRange() {
        return displayRange;
    }

    @Override
    public void setDisplayRange(int range) {
        this.displayRange = range;
    }

    @Override
    public int getUpdateRange() {
        return updateRange;
    }

    @Override
    public void setUpdateRange(int range) {
        this.updateRange = range;
    }

    @Override
    public int getUpdateInterval() {
        return updateInterval;
    }

    @Override
    public void setUpdateInterval(int interval) {
        this.updateInterval = interval;
    }

    @Override
    public boolean isDownOrigin() {
        return downOrigin;
    }

    @Override
    public void setDownOrigin(boolean downOrigin) {
        this.downOrigin = downOrigin;
    }

    @Override
    public boolean isAlwaysFacePlayer() {
        return alwaysFacePlayer;
    }

    @Override
    public void setAlwaysFacePlayer(boolean alwaysFacePlayer) {
        this.alwaysFacePlayer = alwaysFacePlayer;
    }

    @Override
    public void setPermission(String permission) {
        this.permission = permission;
    }

    @Override
    public String getPermission() {
        return permission;
    }

    @Override
    public void addFlag(HologramFlag flag) {
        flags.add(flag);
    }

    @Override
    public void removeFlag(HologramFlag flag) {
        flags.remove(flag);
    }

    @Override
    public boolean hasFlag(HologramFlag flag) {
        return flags.contains(flag);
    }

    @Override
    public Set<HologramFlag> getFlags() {
        return Collections.unmodifiableSet(flags);
    }

    // ==================== PERSISTENCE (IN-MEMORY ONLY) ====================

    @Override
    public void save() {
        // No file persistence yet in this basic implementation
    }

    @Override
    public void load() {
        // No file persistence yet in this basic implementation
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", id.toString());
        map.put("name", name);
        map.put("world", location.getWorld() != null ? location.getWorld().getName() : "");
        map.put("x", location.getX());
        map.put("y", location.getY());
        map.put("z", location.getZ());
        map.put("enabled", enabled);
        map.put("displayRange", displayRange);
        map.put("updateRange", updateRange);
        map.put("updateInterval", updateInterval);
        map.put("downOrigin", downOrigin);
        map.put("alwaysFacePlayer", alwaysFacePlayer);
        map.put("permission", permission);
        return map;
    }

    // ==================== BACKWARD COMPAT LINES ====================

    @Override
    public void setLines(List<Component> lines) {
        this.lines = new ArrayList<>(lines);
        ensureRenderers();
        // Refresh viewers
        for (UUID uuid : viewers) {
            Player p = plugin.getServer().getPlayer(uuid);
            if (p != null && p.isOnline()) {
                show(p);
            }
        }
    }

    @Override
    public List<Component> getLines() {
        return Collections.unmodifiableList(lines);
    }

    public boolean isGlobal() { return global; }

    // ==================== INTERNAL RENDERING HELPERS ====================

    private void ensureRenderers() {
        if (rendererFactory == null) return;
        // Adjust size
        if (renderers.size() > lines.size()) {
            // remove extra renderers, hide from viewers first
            for (int i = lines.size(); i < renderers.size(); i++) {
                NmsTextHologramRenderer r = renderers.get(i);
                for (UUID uuid : viewers) {
                    Player p = plugin.getServer().getPlayer(uuid);
                    if (p != null) r.hide(p);
                }
            }
            renderers.subList(lines.size(), renderers.size()).clear();
        }
        while (renderers.size() < lines.size()) {
            renderers.add(rendererFactory.createTextRenderer());
        }
    }

    private Location linePosition(int index) {
        int linesCount = lines.size();
        double yOffset = LINE_SPACING * (linesCount - 1 - index);
        return location.clone().add(0, yOffset, 0);
    }

    // ==================== SIMPLE INNER PAGE/LINE IMPLEMENTATIONS ====================

    private class BasicHologramPage implements HologramPage {
        private int index;

        BasicHologramPage(int index) {
            this.index = index;
        }

        @Override
        public int getIndex() {
            return index;
        }

        @Override
        public Hologram getParent() {
            return BasicHologram.this;
        }

        @Override
        public List<HologramLine> getLines() {
            List<HologramLine> result = new ArrayList<>();
            for (int i = 0; i < lines.size(); i++) {
                result.add(new BasicHologramLine(this, i));
            }
            return result;
        }

        @Override
        public Optional<HologramLine> getLine(int index) {
            if (index < 0 || index >= lines.size()) {
                return Optional.empty();
            }
            return Optional.of(new BasicHologramLine(this, index));
        }

        @Override
        public int getLineCount() {
            return lines.size();
        }

        @Override
        public HologramLine addLine(String content) {
            lines.add(Component.text(content));
            ensureRenderers();
            return new BasicHologramLine(this, lines.size() - 1);
        }

        @Override
        public HologramLine addLine(String content, HologramLineType type) {
            // Only TEXT is supported in this basic implementation
            return addLine(content);
        }

        @Override
        public HologramLine insertLine(int index, String content) {
            if (index < 0 || index > lines.size()) {
                index = lines.size();
            }
            lines.add(index, Component.text(content));
            ensureRenderers();
            return new BasicHologramLine(this, index);
        }

        @Override
        public HologramLine insertLine(int index, String content, HologramLineType type) {
            return insertLine(index, content);
        }

        @Override
        public Optional<HologramLine> removeLine(int index) {
            if (index < 0 || index >= lines.size()) {
                return Optional.empty();
            }
            BasicHologramLine line = new BasicHologramLine(this, index);
            lines.remove(index);
            ensureRenderers();
            return Optional.of(line);
        }

        @Override
        public void clearLines() {
            lines.clear();
            ensureRenderers();
        }

        @Override
        public double getTotalHeight() {
            return lines.size() * LINE_SPACING;
        }

        @Override
        public Location getCenter() {
            return location.clone();
        }

        @Override
        public Location getNextLineLocation() {
            int index = lines.size();
            return linePosition(index);
        }

        @Override
        public void addClickAction(com.natzdev.natzsmp.api.hologram.ClickType clickType,
                                   com.natzdev.natzsmp.api.hologram.HologramAction action) {
            // Not supported in this basic implementation
        }

        @Override
        public void removeClickAction(com.natzdev.natzsmp.api.hologram.ClickType clickType,
                                      com.natzdev.natzsmp.api.hologram.HologramAction action) {
            // Not supported in this basic implementation
        }

        @Override
        public List<com.natzdev.natzsmp.api.hologram.HologramAction> getClickActions(com.natzdev.natzsmp.api.hologram.ClickType clickType) {
            return Collections.emptyList();
        }

        @Override
        public void executeClickActions(com.natzdev.natzsmp.api.hologram.ClickType clickType, Player player) {
            // Not supported in this basic implementation
        }

        @Override
        public void addFlag(HologramFlag flag) {
            flags.add(flag);
        }

        @Override
        public void removeFlag(HologramFlag flag) {
            flags.remove(flag);
        }

        @Override
        public boolean hasFlag(HologramFlag flag) {
            return flags.contains(flag);
        }

        @Override
        public Set<HologramFlag> getFlags() {
            return Collections.unmodifiableSet(flags);
        }

        @Override
        public void show(Player player) {
            BasicHologram.this.show(player);
        }

        @Override
        public void hide(Player player) {
            BasicHologram.this.hide(player);
        }

        @Override
        public void update(Player player) {
            BasicHologram.this.update(player);
        }

        @Override
        public void updateAll() {
            BasicHologram.this.updateAll();
        }

        @Override
        public void delete() {
            BasicHologram.this.delete();
        }

        @Override
        public boolean isValid() {
            return BasicHologram.this.isValid();
        }

        @Override
        public Map<String, Object> serialize() {
            return BasicHologram.this.serialize();
        }
    }

    private static class BasicHologramLine implements HologramLine {
        private final BasicHologramPage parent;
        private final int index;

        BasicHologramLine(BasicHologramPage parent, int index) {
            this.parent = parent;
            this.index = index;
        }

        @Override
        public UUID getId() {
            // Derive a deterministic UUID from hologram id and line index
            return UUID.nameUUIDFromBytes((parent.getParent().getId() + ":" + index).getBytes());
        }

        @Override
        public HologramPage getParent() {
            return parent;
        }

        @Override
        public HologramLineType getType() {
            return HologramLineType.TEXT;
        }

        @Override
        public Location getLocation() {
            return ((BasicHologram) parent.getParent()).linePosition(index);
        }

        @Override
        public void setLocation(Location location) {
            // Not supported per-line in this basic implementation
        }

        @Override
        public String getContent() {
            List<Component> components = ((BasicHologram) parent.getParent()).lines;
            if (index < 0 || index >= components.size()) {
                return "";
            }
            return components.get(index).toString();
        }

        @Override
        public void setContent(String content) {
            List<Component> components = ((BasicHologram) parent.getParent()).lines;
            if (index < 0 || index >= components.size()) {
                return;
            }
            components.set(index, Component.text(content));
        }

        @Override
        public Component getText() {
            List<Component> components = ((BasicHologram) parent.getParent()).lines;
            if (index < 0 || index >= components.size()) {
                return Component.empty();
            }
            return components.get(index);
        }

        @Override
        public void setText(Component text) {
            List<Component> components = ((BasicHologram) parent.getParent()).lines;
            if (index < 0 || index >= components.size()) {
                return;
            }
            components.set(index, text);
        }

        @Override
        public org.bukkit.inventory.ItemStack getItem() {
            return null;
        }

        @Override
        public void setItem(org.bukkit.inventory.ItemStack item) {
            // Not supported in this basic implementation
        }

        @Override
        public double getHeight() {
            return LINE_SPACING;
        }

        @Override
        public void setHeight(double height) {
            // Ignored in this basic implementation
        }

        @Override
        public double getOffsetX() {
            return 0;
        }

        @Override
        public void setOffsetX(double offsetX) {
            // Ignored
        }

        @Override
        public double getOffsetY() {
            return 0;
        }

        @Override
        public void setOffsetY(double offsetY) {
            // Ignored
        }

        @Override
        public double getOffsetZ() {
            return 0;
        }

        @Override
        public void setOffsetZ(double offsetZ) {
            // Ignored
        }

        @Override
        public void setOffset(double x, double y, double z) {
            // Ignored
        }

        @Override
        public float getFacing() {
            return 0;
        }

        @Override
        public void setFacing(float facing) {
            // Ignored
        }

        @Override
        public void addFlag(HologramFlag flag) {
            // Use hologram-level flags
            ((BasicHologram) parent.getParent()).addFlag(flag);
        }

        @Override
        public void removeFlag(HologramFlag flag) {
            ((BasicHologram) parent.getParent()).removeFlag(flag);
        }

        @Override
        public boolean hasFlag(HologramFlag flag) {
            return ((BasicHologram) parent.getParent()).hasFlag(flag);
        }

        @Override
        public Set<HologramFlag> getFlags() {
            return ((BasicHologram) parent.getParent()).getFlags();
        }

        @Override
        public void setPermission(String permission) {
            ((BasicHologram) parent.getParent()).setPermission(permission);
        }

        @Override
        public String getPermission() {
            return ((BasicHologram) parent.getParent()).getPermission();
        }

        @Override
        public void show(Player player) {
            ((BasicHologram) parent.getParent()).show(player);
        }

        @Override
        public void hide(Player player) {
            ((BasicHologram) parent.getParent()).hide(player);
        }

        @Override
        public void update(Player player) {
            ((BasicHologram) parent.getParent()).update(player);
        }

        @Override
        public void updateAll() {
            ((BasicHologram) parent.getParent()).updateAll();
        }

        @Override
        public void delete() {
            ((BasicHologram) parent.getParent()).delete();
        }

        @Override
        public boolean isValid() {
            return ((BasicHologram) parent.getParent()).isValid();
        }

        @Override
        public Map<String, Object> serialize() {
            return ((BasicHologram) parent.getParent()).serialize();
        }
    }
}

package com.natzdev.natzsmp.commands;

import com.natzdev.natzsmp.api.hologram.Hologram;
import com.natzdev.natzsmp.api.hologram.HologramAPI;
import com.natzdev.natzsmp.api.hologram.HologramLine;
import com.natzdev.natzsmp.api.hologram.HologramService;
import com.natzdev.natzsmp.util.ServiceRegistry;
import net.kyori.adventure.text.Component;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Handler terpisah untuk subcommand "/natz holo".
 *
 * Tujuan:
 * - Memisahkan logika hologram dari NatzCommand utama agar lebih mudah di-maintain.
 * - Menyediakan API command yang fleksibel untuk mengelola hologram.
 */
public final class HologramCommand {

    // Utility class, tidak perlu di-instansiasi
    private HologramCommand() {}

    /**
     * Entry point untuk subcommand "/natz holo ...".
     * args[0] diasumsikan sudah "holo" ketika dipanggil dari NatzCommand.
     */
    public static boolean handle(CommandSender sender, String label, String[] args) {
        if (args.length == 1) {
            sendUsage(sender, label);
            return true;
        }

        // Pastikan HologramAPI terhubung ke HologramService impl
        if (!ensureService(sender)) {
            // Pesan error sudah dikirim jika gagal
            return true;
        }

        String action = args[1].toLowerCase(Locale.ROOT);
        switch (action) {
            case "help":
                sendUsage(sender, label);
                return true;
            case "list":
                handleList(sender);
                return true;
            case "create":
                handleCreate(sender, args, label);
                return true;
            case "delete":
                handleDelete(sender, args, label);
                return true;
            case "info":
                handleInfo(sender, args, label);
                return true;
            case "addline":
                handleAddLine(sender, args, label);
                return true;
            case "removeline":
                handleRemoveLine(sender, args, label);
                return true;
            case "movehere":
                handleMoveHere(sender, args, label);
                return true;
            default:
                sender.sendMessage(ChatColor.RED + "Unknown holo subcommand. Use /" + label + " holo help");
                return true;
        }
    }

    /**
     * Tab complete untuk subcommand "holo".
     */
    public static List<String> tabComplete(CommandSender sender, String[] args) {
        List<String> result = new ArrayList<>();

        // args[0] == "holo" ketika dipanggil dari NatzCommand
        if (args.length == 2) {
            String prefix = args[1].toLowerCase(Locale.ROOT);
            for (String sub : new String[]{"help", "list", "create", "delete", "info", "addline", "removeline", "movehere"}) {
                if (sub.startsWith(prefix)) {
                    result.add(sub);
                }
            }
            return result;
        }

        String sub = args[1].toLowerCase(Locale.ROOT);

        // Argumen ke-3 biasanya adalah nama hologram untuk beberapa subcommand
        if (args.length == 3 && needsHologramName(sub)) {
            if (!ensureService(null)) {
                return result;
            }
            String prefix = args[2].toLowerCase(Locale.ROOT);
            for (String name : getAllHologramNames()) {
                if (name.toLowerCase(Locale.ROOT).startsWith(prefix)) {
                    result.add(name);
                }
            }
            return result;
        }

        // Untuk argumen selanjutnya (index / text), untuk saat ini tidak ada saran khusus
        return result;
    }

    /**
     * Menentukan apakah subcommand membutuhkan nama hologram sebagai argumen kedua.
     */
    private static boolean needsHologramName(String sub) {
        switch (sub) {
            case "delete":
            case "info":
            case "addline":
            case "removeline":
            case "movehere":
                return true;
            default:
                return false;
        }
    }

    /**
     * Mengirimkan ringkasan usage untuk semua subcommand holo.
     */
    private static void sendUsage(CommandSender sender, String label) {
        sender.sendMessage(ChatColor.GOLD + "==== Hologram Commands ====");
        sender.sendMessage(ChatColor.YELLOW + "/" + label + " holo list" + ChatColor.GRAY + " - List semua hologram");
        sender.sendMessage(ChatColor.YELLOW + "/" + label + " holo create <name> [text...]" + ChatColor.GRAY + " - Buat hologram di lokasi pemain");
        sender.sendMessage(ChatColor.YELLOW + "/" + label + " holo delete <name>" + ChatColor.GRAY + " - Hapus hologram");
        sender.sendMessage(ChatColor.YELLOW + "/" + label + " holo info <name>" + ChatColor.GRAY + " - Info detail hologram");
        sender.sendMessage(ChatColor.YELLOW + "/" + label + " holo addline <name> <text...>" + ChatColor.GRAY + " - Tambah baris di akhir");
        sender.sendMessage(ChatColor.YELLOW + "/" + label + " holo removeline <name> <index>" + ChatColor.GRAY + " - Hapus baris (index 1-based)");
        sender.sendMessage(ChatColor.YELLOW + "/" + label + " holo movehere <name>" + ChatColor.GRAY + " - Pindah hologram ke lokasi pemain");
    }

    /**
     * Memastikan HologramAPI punya service yang valid.
     * Jika belum diset, akan mencoba mengambil dari ServiceRegistry lalu mendaftarkannya.
     */
    private static boolean ensureService(CommandSender sender) {
        try {
            HologramAPI.getService();
            return true;
        } catch (IllegalStateException ex) {
            HologramService service = ServiceRegistry.get(HologramService.class);
            if (service != null) {
                HologramAPI.setService(service);
                return true;
            }
            if (sender != null) {
                sender.sendMessage(ChatColor.RED + "Hologram service is not available. Periksa inisialisasi plugin.");
            }
            return false;
        }
    }

    /**
     * Helper untuk mendapatkan semua hologram yang terdaftar.
     */
    private static Collection<Hologram> getAllHolograms() {
        return HologramAPI.getAllHolograms();
    }

    /**
     * Helper untuk mendapatkan semua nama hologram (digunakan untuk tab-complete).
     */
    private static List<String> getAllHologramNames() {
        return getAllHolograms().stream()
                .map(Hologram::getName)
                .collect(Collectors.toList());
    }

    /**
     * Handler untuk "/natz holo list".
     */
    private static void handleList(CommandSender sender) {
        Collection<Hologram> holograms = getAllHolograms();
        if (holograms.isEmpty()) {
            sender.sendMessage(ChatColor.YELLOW + "Belum ada hologram yang dibuat.");
            return;
        }
        sender.sendMessage(ChatColor.GOLD + "Holograms (" + holograms.size() + "):");
        for (Hologram holo : holograms) {
            sender.sendMessage(ChatColor.AQUA + "- " + holo.getName()
                    + ChatColor.GRAY + " @ " + formatLocation(holo.getLocation()));
        }
    }

    /**
     * Handler untuk "/natz holo create <name> [text...]".
     * Membuat hologram di lokasi pemain dengan satu baris teks awal.
     */
    private static void handleCreate(CommandSender sender, String[] args, String label) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Hanya player yang dapat membuat hologram (butuh lokasi).");
            return;
        }
        if (args.length < 3) {
            sender.sendMessage(ChatColor.RED + "Usage: /" + label + " holo create <name> [text...]");
            return;
        }

        String name = args[2];
        if (HologramAPI.hologramExists(name)) {
            sender.sendMessage(ChatColor.RED + "Hologram dengan nama itu sudah ada.");
            return;
        }

        // Gabungkan sisa argumen sebagai teks baris pertama (boleh kosong -> gunakan default)
        String lineText;
        if (args.length > 3) {
            StringBuilder sb = new StringBuilder();
            for (int i = 3; i < args.length; i++) {
                if (i > 3) sb.append(' ');
                sb.append(args[i]);
            }
            lineText = sb.toString();
        } else {
            lineText = "New hologram " + name;
        }

        Player player = (Player) sender;
        Location loc = player.getLocation();

        // Buat hologram dan langsung tampilkan ke semua pemain
        Hologram hologram = HologramAPI.createHologram(name, loc, Component.text(lineText));
        hologram.showAll();

        sender.sendMessage(ChatColor.GREEN + "Hologram '" + name + "' dibuat di lokasimu.");
    }

    /**
     * Handler untuk "/natz holo delete <name>".
     */
    private static void handleDelete(CommandSender sender, String[] args, String label) {
        if (args.length < 3) {
            sender.sendMessage(ChatColor.RED + "Usage: /" + label + " holo delete <name>");
            return;
        }
        String name = args[2];
        if (!HologramAPI.hologramExists(name)) {
            sender.sendMessage(ChatColor.RED + "Hologram dengan nama itu tidak ditemukan.");
            return;
        }

        HologramAPI.deleteHologram(name);
        sender.sendMessage(ChatColor.GREEN + "Hologram '" + name + "' telah dihapus.");
    }

    /**
     * Handler untuk "/natz holo info <name>".
     * Menampilkan informasi dasar hologram (lokasi, jumlah page/line, status enabled).
     */
    private static void handleInfo(CommandSender sender, String[] args, String label) {
        if (args.length < 3) {
            sender.sendMessage(ChatColor.RED + "Usage: /" + label + " holo info <name>");
            return;
        }
        String name = args[2];
        Optional<Hologram> holoOpt = HologramAPI.getHologram(name);
        if (!holoOpt.isPresent()) {
            sender.sendMessage(ChatColor.RED + "Hologram dengan nama itu tidak ditemukan.");
            return;
        }

        Hologram holo = holoOpt.get();
        sender.sendMessage(ChatColor.GOLD + "==== Hologram Info: " + holo.getName() + " ====");
        sender.sendMessage(ChatColor.GRAY + "Location: " + ChatColor.AQUA + formatLocation(holo.getLocation()));
        sender.sendMessage(ChatColor.GRAY + "Enabled: " + (holo.isEnabled() ? ChatColor.GREEN + "yes" : ChatColor.RED + "no"));
        sender.sendMessage(ChatColor.GRAY + "Pages: " + ChatColor.AQUA + holo.getPageCount());
        int lines = 0;
        if (holo.getPageCount() > 0) {
            lines = holo.getFirstPage().getLineCount();
        }
        sender.sendMessage(ChatColor.GRAY + "Lines (page 1): " + ChatColor.AQUA + lines);
        sender.sendMessage(ChatColor.GRAY + "Display range: " + ChatColor.AQUA + holo.getDisplayRange());
        sender.sendMessage(ChatColor.GRAY + "Update range: " + ChatColor.AQUA + holo.getUpdateRange());
        sender.sendMessage(ChatColor.GRAY + "Update interval: " + ChatColor.AQUA + holo.getUpdateInterval() + " ticks");
    }

    /**
     * Handler untuk "/natz holo addline <name> <text...>".
     * Menambahkan baris baru ke page pertama hologram.
     */
    private static void handleAddLine(CommandSender sender, String[] args, String label) {
        if (args.length < 4) {
            sender.sendMessage(ChatColor.RED + "Usage: /" + label + " holo addline <name> <text...>");
            return;
        }
        String name = args[2];
        Optional<Hologram> holoOpt = HologramAPI.getHologram(name);
        if (!holoOpt.isPresent()) {
            sender.sendMessage(ChatColor.RED + "Hologram dengan nama itu tidak ditemukan.");
            return;
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 3; i < args.length; i++) {
            if (i > 3) sb.append(' ');
            sb.append(args[i]);
        }
        String text = sb.toString();

        Hologram holo = holoOpt.get();
        HologramLine line = HologramAPI.addLine(holo, text);

        int index = holo.getFirstPage().getLineCount();
        sender.sendMessage(ChatColor.GREEN + "Menambahkan line ke-" + index + " pada hologram '" + name + "'.");
    }

    /**
     * Handler untuk "/natz holo removeline <name> <index>".
     * Index yang digunakan adalah 1-based agar lebih natural bagi user.
     */
    private static void handleRemoveLine(CommandSender sender, String[] args, String label) {
        if (args.length < 4) {
            sender.sendMessage(ChatColor.RED + "Usage: /" + label + " holo removeline <name> <index>");
            return;
        }
        String name = args[2];
        Optional<Hologram> holoOpt = HologramAPI.getHologram(name);
        if (!holoOpt.isPresent()) {
            sender.sendMessage(ChatColor.RED + "Hologram dengan nama itu tidak ditemukan.");
            return;
        }

        int index1Based;
        try {
            index1Based = Integer.parseInt(args[3]);
        } catch (NumberFormatException e) {
            sender.sendMessage(ChatColor.RED + "Index harus berupa angka.");
            return;
        }
        if (index1Based <= 0) {
            sender.sendMessage(ChatColor.RED + "Index harus >= 1.");
            return;
        }

        int index0 = index1Based - 1;
        Hologram holo = holoOpt.get();
        Optional<HologramLine> removed = HologramAPI.removeLine(holo, 0, index0);
        if (!removed.isPresent()) {
            sender.sendMessage(ChatColor.RED + "Line dengan index tersebut tidak ditemukan.");
            return;
        }

        sender.sendMessage(ChatColor.GREEN + "Line ke-" + index1Based + " pada hologram '" + name + "' telah dihapus.");
    }

    /**
     * Handler untuk "/natz holo movehere <name>".
     * Memindahkan hologram ke lokasi player yang menjalankan command.
     */
    private static void handleMoveHere(CommandSender sender, String[] args, String label) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Hanya player yang dapat memindahkan hologram (butuh lokasi).");
            return;
        }
        if (args.length < 3) {
            sender.sendMessage(ChatColor.RED + "Usage: /" + label + " holo movehere <name>");
            return;
        }

        String name = args[2];
        Optional<Hologram> holoOpt = HologramAPI.getHologram(name);
        if (!holoOpt.isPresent()) {
            sender.sendMessage(ChatColor.RED + "Hologram dengan nama itu tidak ditemukan.");
            return;
        }

        Player player = (Player) sender;
        Location loc = player.getLocation();
        HologramAPI.moveHologram(name, loc);
        sender.sendMessage(ChatColor.GREEN + "Hologram '" + name + "' dipindahkan ke lokasimu.");
    }

    /**
     * Utility kecil untuk mem-format lokasi menjadi string ringkas.
     */
    private static String formatLocation(Location loc) {
        if (loc == null || loc.getWorld() == null) {
            return "unknown";
        }
        return loc.getWorld().getName() + " "
                + loc.getBlockX() + ","
                + loc.getBlockY() + ","
                + loc.getBlockZ();
    }
}

package uk.antiperson.stackmob.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class Utilities {

    public static final boolean IS_FOLIA;
    static {
        boolean f = false;
        try {
            Class.forName("io.papermc.paper.threadedregions.RegionizedServer");
            f = true;
        } catch (ClassNotFoundException ignored) {}
        IS_FOLIA = f;
    }
    public static final Component PREFIX = Component.text("StackMob ").color(TextColor.color(0, 206, 209)).append(Component.text(">> ").color(NamedTextColor.GRAY)).compact();
    public static final String PREFIX_STRING = LegacyComponentSerializer.legacySection().serialize(PREFIX);
    public static final String SLIME_METADATA = "deathcount";
    public static final String NO_LEASH_METADATA = "stop-leash";
    public static final String DISCORD = "https://discord.gg/fz9xzuB";
    public static final String GITHUB = "https://github.com/Nathat23/StackMob-5";
    public static final String GITHUB_DEFAULT_CONFIG = GITHUB + "/tree/master/src/main/resources";
    private static final boolean usingPaper;
    public static final List<Material> DROWNED_MATERIALS = Arrays.asList(Material.NAUTILUS_SHELL, Material.TRIDENT);
    public static final List<EquipmentSlot> HAND_SLOTS = Arrays.asList(EquipmentSlot.HAND, EquipmentSlot.OFF_HAND);
    private static MinecraftVersion minecraftVersion;
    private static final LegacyComponentSerializer legacyComponentSerializer = LegacyComponentSerializer.builder().character('&').hexColors().hexCharacter('#').build();

    static {
        boolean usingPaper1;
        try {
            Bukkit.spigot().getClass().getMethod("getPaperConfig");
            usingPaper1 = true;
        } catch (NoSuchMethodException e) {
            usingPaper1 = false;
        }
        usingPaper = usingPaper1;
    }

    public static Component createComponent(String toTranslate) {
        return legacyComponentSerializer.deserialize(toTranslate);
    }

    public static List<Integer> split(int dividend, int divisor) {
        int fullAmount = dividend / divisor;
        int remainder = dividend % divisor;
        List<Integer> numbers = new ArrayList<>(fullAmount + 1);
        for (int i = 0; i < fullAmount; i++) {
            numbers.add(divisor);
        }
        if (remainder > 0) {
            numbers.add(remainder);
        }
        return numbers;
    }

    public static CompletableFuture<DownloadResult> downloadFile(File filePath, String url) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                URL webPath = new URL(url);
                try (InputStream in = webPath.openStream()) {
                    Files.createDirectories(filePath.getParentFile().toPath());
                    Files.copy(in, filePath.toPath(), StandardCopyOption.REPLACE_EXISTING);
                }
                return DownloadResult.SUCCESSFUL;
            } catch (IOException e) {
                e.printStackTrace();
                return DownloadResult.ERROR;
            }
        });
    }

    public static boolean isPaper() {
        return usingPaper;
    }

    public static MinecraftVersion getMinecraftVersion() {
        if (minecraftVersion == null) {
            minecraftVersion = MinecraftVersion.V1_16;
            String packageName = Bukkit.getServer().getBukkitVersion();
            String ending = packageName.substring(0, packageName.indexOf('-'));
            for (MinecraftVersion version: MinecraftVersion.values()) {
                if (ending.contains(version.getInternalName())){
                    minecraftVersion = version;
                }
            }
        }
        return minecraftVersion;
    }

    public static boolean isVersionAtLeast(MinecraftVersion version) {
        return getMinecraftVersion().ordinal() >= version.ordinal();
    }

    public static boolean isDye(ItemStack material) {
        return material.getType().toString().endsWith("_DYE");
    }

    public static void removeHandItem(Player player, int itemAmount) {
        if (itemAmount == player.getInventory().getItemInMainHand().getAmount()) {
           player.getInventory().setItemInMainHand(null);
           return;
        }
        ItemStack is = player.getInventory().getItemInMainHand();
        is.setAmount(is.getAmount() - itemAmount);
        player.getInventory().setItemInMainHand(is);
    }

    public enum DownloadResult {
        SUCCESSFUL,
        ERROR
    }

    public static String filter(String string) {
        return string.replaceAll("[^A-Za-z\\d]", " ");
    }

    public static double toDouble(final String str) {
        return toDouble(str, 0.0d);
    }

    public static double toDouble(final String str, final double defaultValue) {
        if (str == null) {
            return defaultValue;
        }
        try {
            return Double.parseDouble(str);
        } catch (final NumberFormatException nfe) {
            return defaultValue;
        }
    }

    public static int toInt(final String str) {
        return toInt(str, 0);
    }

    public static int toInt(final String str, final int defaultValue) {
        if (str == null) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(str);
        } catch (final NumberFormatException nfe) {
            return defaultValue;
        }
    }

    public static String[] removeFirst(String[] array) {
        String[] newArray = new String[array.length - 1];
        System.arraycopy(array, 1, newArray, 0, array.length - 1);
        return newArray;
    }

    public static String capitalizeString(String string) {
        String work = string.toLowerCase();
        char[] chars = work.toCharArray();
        boolean shouldCapitalize = true;
        for (int i = 0; i < chars.length; i++) {
            if (shouldCapitalize) {
                chars[i] = Character.toTitleCase(chars[i]);
                shouldCapitalize = false;
                continue;
            }
            if (Character.isSpaceChar(chars[i])) {
                shouldCapitalize = true;
            }
        }
        return new String(chars);
    }

    public static Material getScuteMaterial() {
        return isVersionAtLeast(MinecraftVersion.V1_20_6) ? Material.getMaterial("TURTLE_SCUTE") : Material.getMaterial("SCUTE");
    }

    public enum MinecraftVersion {
        V1_16("1.16"),
        V1_17("1.17"),
        V1_18("1.18"),
        V1_18_2("1.18.2"),
        V1_19_4("1.19.4"),
        V1_20_4("1.20.4"),
        V1_20_6("1.20.6"),
        V1_21("1.21");

        final String internalName;

        MinecraftVersion(String internalName) {
            this.internalName = internalName;
        }

        public String getInternalName() {
            return internalName;
        }
    }
}

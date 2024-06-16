package uk.antiperson.stackmob.utils;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import uk.antiperson.stackmob.StackMob;

import java.util.Arrays;
import java.util.List;

public class ItemTools {

    public static final String ITEM_NAME = ChatColor.GOLD + "The Stick Of Stacking";
    public static final List<String> ITEM_LORE = Arrays.asList(ChatColor.GREEN + "A useful tool for modifying stacked mobs.",
            ChatColor.GOLD + "Right click to perform action" ,
            ChatColor.GOLD + "Shift-right click to change mode.");

    private final StackMob sm;
    public ItemTools(StackMob sm) {
        this.sm = sm;
    }

    public ItemStack createStackingTool() {
        ItemStack is = new ItemStack(Material.BONE, 1);
        is.addUnsafeEnchantment(Enchantment.CHANNELING, 100);
        ItemMeta itemMeta = is.getItemMeta();
        itemMeta.setDisplayName(ITEM_NAME);
        itemMeta.setLore(ITEM_LORE);
        itemMeta.getPersistentDataContainer().set(sm.getToolKey(), PersistentDataType.INTEGER, 1);
        is.setItemMeta(itemMeta);
        return is;
    }

    public void giveStackingTool(Player player) {
        player.getInventory().addItem(createStackingTool());
    }

    public boolean isStackingTool(ItemStack is) {
        if (is.getItemMeta() == null) {
            return false;
        }
        return is.getItemMeta().getPersistentDataContainer().has(sm.getToolKey(), PersistentDataType.INTEGER);
    }

}

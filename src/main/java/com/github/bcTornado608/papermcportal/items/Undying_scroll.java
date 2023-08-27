package com.github.bcTornado608.papermcportal.items;


import javax.annotation.Nonnull;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import com.github.bcTornado608.papermcportal.constants.CommonConstants;
import com.github.bcTornado608.papermcportal.utils.TextHelpers;
import com.google.common.collect.ImmutableList;

import net.kyori.adventure.text.format.NamedTextColor;

public class Undying_scroll {

    public static @Nonnull ItemStack getItemStack(int count){
        ItemStack stack = new ItemStack(Material.PAPER, count);

        ItemMeta meta = stack.getItemMeta();
        meta.displayName(TextHelpers.italicText("\"Scroll of Undying\"", NamedTextColor.DARK_PURPLE));
        meta.lore(ImmutableList.of(TextHelpers.italicText("The universe whence, and whither?", NamedTextColor.RED)));
        
        meta.getPersistentDataContainer().set(CommonConstants.ITEM_ID_KEY, PersistentDataType.STRING, CommonConstants.UNDYING_SCROLL_RECIPE);
        
        stack.setItemMeta(meta);
        return stack;
    }

    public static boolean isItem(ItemStack stack){
        if(stack.getType() != Material.PAPER){
            return false;
        }
        return CommonConstants.UNDYING_SCROLL_RECIPE.equals(
                stack.getItemMeta().getPersistentDataContainer().get(CommonConstants.ITEM_ID_KEY, PersistentDataType.STRING));
    }
}
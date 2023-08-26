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

public class CopperHoe {

    public static @Nonnull ItemStack getItemStack(int count){
        ItemStack stack = new ItemStack(Material.GOLDEN_HOE, count);

        ItemMeta meta = stack.getItemMeta();
        meta.displayName(TextHelpers.normalText("Copper Hoe"));
        meta.lore(ImmutableList.of(TextHelpers.italicText("Harvest time!", NamedTextColor.GREEN)));
        
        meta.getPersistentDataContainer().set(CommonConstants.ITEM_ID_KEY, PersistentDataType.STRING, CommonConstants.COPPER_TOOLS);
        
        stack.setItemMeta(meta);
        return stack;
    }

    public static boolean isItem(ItemStack stack){
        if(stack.getType() != Material.GOLDEN_HOE){
            return false;
        }
        return CommonConstants.COPPER_TOOLS.equals(
                stack.getItemMeta().getPersistentDataContainer().get(CommonConstants.ITEM_ID_KEY, PersistentDataType.STRING));
    }
}

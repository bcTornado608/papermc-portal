package com.github.bcTornado608.papermcportal.constants;



import org.bukkit.NamespacedKey;
import org.bukkit.plugin.Plugin;

public class CommonConstants {    
    public static final String ITEM_ID = "item_id";
    public static NamespacedKey ITEM_ID_KEY = null;


    public static final String COPPER_TOOLS = "copper_tools";
    public static final String ICE_BOMB = "ice_bomb";


    public static final String COPPER_PICKAXE_RECIPE = "copper_pickaxe_recipe";
    public static NamespacedKey COPPER_PICKAXE_RECIPE_KEY = null;

    public static final String COPPER_AXE_RECIPE = "copper_axe_recipe";
    public static NamespacedKey COPPER_AXE_RECIPE_KEY = null;

    public static final String COPPER_SHOVEL_RECIPE = "copper_shovel_recipe";
    public static NamespacedKey COPPER_SHOVEL_RECIPE_KEY = null;

    public static final String COPPER_HOE_RECIPE = "copper_hoe_recipe";
    public static NamespacedKey COPPER_HOE_RECIPE_KEY = null;

    public static final String COPPER_SWORD_RECIPE = "copper_sword_recipe";
    public static NamespacedKey COPPER_SWORD_RECIPE_KEY = null;

    public static final String COPPER_NAME_TAG_RECIPE = "copper_name_tag_recipe";
    public static NamespacedKey COPPER_NAME_TAG_RECIPE_KEY = null;

    public static final String ICE_BOMB_RECIPE = "ice_bomb_recipe";
    public static NamespacedKey ICE_BOMB_RECIPE_KEY = null;

    public static void initializeConstants(Plugin plugin){
        ITEM_ID_KEY = new NamespacedKey(plugin, ITEM_ID);
        COPPER_PICKAXE_RECIPE_KEY = new NamespacedKey(plugin, COPPER_PICKAXE_RECIPE);
        COPPER_AXE_RECIPE_KEY = new NamespacedKey(plugin, COPPER_AXE_RECIPE);
        COPPER_SHOVEL_RECIPE_KEY = new NamespacedKey(plugin, COPPER_SHOVEL_RECIPE);
        COPPER_HOE_RECIPE_KEY = new NamespacedKey(plugin, COPPER_HOE_RECIPE);
        COPPER_SWORD_RECIPE_KEY = new NamespacedKey(plugin, COPPER_SWORD_RECIPE);
        COPPER_NAME_TAG_RECIPE_KEY = new NamespacedKey(plugin, COPPER_NAME_TAG_RECIPE);
        ICE_BOMB_RECIPE_KEY = new NamespacedKey(plugin, ICE_BOMB_RECIPE);
    }
}

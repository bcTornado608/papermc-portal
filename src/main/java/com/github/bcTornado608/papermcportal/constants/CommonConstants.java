package com.github.bcTornado608.papermcportal.constants;

import org.bukkit.NamespacedKey;
import org.bukkit.plugin.Plugin;

public class CommonConstants {

    public static final String ITEM_ID = "item_id";
    public static NamespacedKey ITEM_ID_KEY;

    public static final String LOC_STORE = "loc_store";
    public static NamespacedKey LOC_STORE_KEY;

    public static final String WORLD_STORE = "world_store";
    public static NamespacedKey WORLD_STORE_KEY;

    public static final String NORMAL_STICK_RECIPE = "normal_stick";
    public static NamespacedKey NORMAL_STICK_RECIPE_KEY = null;

    public static final String TELEPORTATION_SCROLL_RECIPE = "teleportation_scroll";
    public static NamespacedKey TELEPORTATION_SCROLL_RECIPE_KEY = null;

    public static final String UNDYING_SCROLL_RECIPE = "undying_scroll";
    public static NamespacedKey UNDYING_SCROLL_RECIPE_KEY = null;


    public static void initializeConstants(Plugin plugin){
        ITEM_ID_KEY = new NamespacedKey(plugin, ITEM_ID);
        LOC_STORE_KEY = new NamespacedKey(plugin, LOC_STORE);
        WORLD_STORE_KEY = new NamespacedKey(plugin, WORLD_STORE);
        NORMAL_STICK_RECIPE_KEY = new NamespacedKey(plugin, NORMAL_STICK_RECIPE);
        TELEPORTATION_SCROLL_RECIPE_KEY = new NamespacedKey(plugin, TELEPORTATION_SCROLL_RECIPE);
        UNDYING_SCROLL_RECIPE_KEY = new NamespacedKey(plugin, UNDYING_SCROLL_RECIPE);
    }
}

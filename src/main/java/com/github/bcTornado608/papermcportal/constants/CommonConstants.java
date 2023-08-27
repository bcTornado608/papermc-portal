package com.github.bcTornado608.papermcportal.constants;



import java.util.jar.Attributes.Name;

import org.bukkit.NamespacedKey;
import org.bukkit.plugin.Plugin;

public class CommonConstants {

    public static final String ITEM_ID = "item_id";
    public static NamespacedKey ITEM_ID_KEY;

    public static final String NORMAL_STICK_RECIPE = "normal_stick";
    public static NamespacedKey NORMAL_STICK_RECIPE_KEY = null;


    public static void initializeConstants(Plugin plugin){
        ITEM_ID_KEY = new NamespacedKey(plugin, ITEM_ID);
        NORMAL_STICK_RECIPE_KEY = new NamespacedKey(plugin, NORMAL_STICK_RECIPE);
    }
}

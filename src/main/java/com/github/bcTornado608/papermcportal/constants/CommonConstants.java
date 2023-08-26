package com.github.bcTornado608.papermcportal.constants;



import org.bukkit.NamespacedKey;
import org.bukkit.plugin.Plugin;

public class CommonConstants {

    public static final String ITEM_ID = "item_id";
    public static NamespacedKey ITEM_ID_KEY;


    public static void initializeConstants(Plugin plugin){
        ITEM_ID_KEY = new NamespacedKey(plugin, ITEM_ID);
    }
}

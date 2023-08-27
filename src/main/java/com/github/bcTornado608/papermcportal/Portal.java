package com.github.bcTornado608.papermcportal;



import org.bukkit.Material;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.github.bcTornado608.papermcportal.constants.CommonConstants;
import com.github.bcTornado608.papermcportal.items.Normal_stick;
import com.github.bcTornado608.papermcportal.items.Teleportation_scroll;
import com.github.bcTornado608.papermcportal.items.Undying_scroll;
import com.github.bcTornado608.papermcportal.listeners.PortalListener;


public class Portal extends JavaPlugin implements Listener{
    static private Portal instance;


    @Override
    public void onEnable() {
        super.onEnable();
        instance = this;

        CommonConstants.initializeConstants(this);

        registerCommands();
        registerListeners();
        registerRecipies();

        startBgTasks();
    }

    public static Portal getInstance(){
        return instance;
    }

    private void startBgTasks(){
    }
    
    private void registerCommands(){
    }

    private void registerListeners(){
        PluginManager manager = getServer().getPluginManager();
        manager.registerEvents(new PortalListener(), this);
    }

    private void registerRecipies(){
        // wand
        ShapedRecipe recipe = new ShapedRecipe(CommonConstants.NORMAL_STICK_RECIPE_KEY, Normal_stick.getItemStack(1));
        recipe.shape(
                "E",
                "S");
        recipe.setIngredient('E', Material.ENDER_EYE);
        recipe.setIngredient('S', Material.STICK);
        getServer().addRecipe(recipe);

        // teleportation scroll
        ShapedRecipe recipe2 = new ShapedRecipe(CommonConstants.TELEPORTATION_SCROLL_RECIPE_KEY, Teleportation_scroll.getItemStack(1));
        recipe2.shape(
                "E",
                "P");
        recipe2.setIngredient('E', Material.ENDER_EYE);
        recipe2.setIngredient('P', Material.PAPER);
        getServer().addRecipe(recipe2);

        // Undying scroll
        ShapedRecipe recipe3 = new ShapedRecipe(CommonConstants.UNDYING_SCROLL_RECIPE_KEY, Undying_scroll.getItemStack(1));
        recipe3.shape(
                " E ",
                " T ",
                " P ");
        recipe3.setIngredient('E', Material.ENDER_EYE);
        recipe3.setIngredient('P', Material.PAPER);
        recipe3.setIngredient('T', Material.TOTEM_OF_UNDYING);
        getServer().addRecipe(recipe3);
    }
}

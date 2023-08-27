package com.github.bcTornado608.papermcportal;



import org.bukkit.Material;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.github.bcTornado608.papermcportal.constants.CommonConstants;
import com.github.bcTornado608.papermcportal.items.Normal_stick;
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

    }
}

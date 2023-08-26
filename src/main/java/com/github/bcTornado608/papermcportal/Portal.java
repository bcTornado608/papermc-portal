package com.github.bcTornado608.papermcportal;



import org.bukkit.Material;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.github.bcTornado608.papermcportal.constants.CommonConstants;
import com.github.bcTornado608.papermcportal.items.CopperAxe;
import com.github.bcTornado608.papermcportal.items.CopperHoe;
import com.github.bcTornado608.papermcportal.items.CopperPickaxe;
import com.github.bcTornado608.papermcportal.items.CopperShovel;
import com.github.bcTornado608.papermcportal.items.CopperSword;
import com.github.bcTornado608.papermcportal.items.IceBomb;
import com.github.bcTornado608.papermcportal.listeners.PortalListener;
import com.github.bcTornado608.papermcportal.tasks.SlimePotTask;



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
        new SlimePotTask().runTaskTimer(this, 0, 10);
    }
    
    private void registerCommands(){
    }

    private void registerListeners(){
        PluginManager manager = getServer().getPluginManager();
        manager.registerEvents(new PortalListener(), this);
    }

    private void registerRecipies(){
        // copper pickaxe
        ShapedRecipe recipe = new ShapedRecipe(CommonConstants.COPPER_PICKAXE_RECIPE_KEY, CopperPickaxe.getItemStack(1));
        recipe.shape(
                "CCC",
                " I ",
                " I ");
        recipe.setIngredient('C', Material.COPPER_INGOT);
        recipe.setIngredient('I', Material.STICK);
        getServer().addRecipe(recipe);

        // copper axe
        recipe = new ShapedRecipe(CommonConstants.COPPER_AXE_RECIPE_KEY, CopperAxe.getItemStack(1));
        recipe.shape(
            "CC",
            "IC",
            "I ");
        recipe.setIngredient('C', Material.COPPER_INGOT);
        recipe.setIngredient('I', Material.STICK);
        getServer().addRecipe(recipe);

        // copper shovel
        recipe = new ShapedRecipe(CommonConstants.COPPER_SHOVEL_RECIPE_KEY, CopperShovel.getItemStack(1));
        recipe.shape(
            "C",
            "I",
            "I");
        recipe.setIngredient('C', Material.COPPER_INGOT);
        recipe.setIngredient('I', Material.STICK);
        getServer().addRecipe(recipe);


        // copper hoe
        recipe = new ShapedRecipe(CommonConstants.COPPER_HOE_RECIPE_KEY, CopperHoe.getItemStack(1));
        recipe.shape(
            "CC",
            "I ",
            "I ");
        recipe.setIngredient('C', Material.COPPER_INGOT);
        recipe.setIngredient('I', Material.STICK);
        getServer().addRecipe(recipe);

        // copper sword
        recipe = new ShapedRecipe(CommonConstants.COPPER_SWORD_RECIPE_KEY, CopperSword.getItemStack(1)); 
        recipe.shape(
            "C",
            "C",
            "I"
        );
        recipe.setIngredient('C', Material.COPPER_INGOT);
        recipe.setIngredient('I', Material.STICK);
        getServer().addRecipe(recipe);

        // copper name tag
        ShapelessRecipe shapelessRecipe = new ShapelessRecipe(CommonConstants.COPPER_NAME_TAG_RECIPE_KEY, new ItemStack(Material.NAME_TAG));
        shapelessRecipe.addIngredient(1, Material.STRING);
        shapelessRecipe.addIngredient(1, Material.COPPER_INGOT);
        getServer().addRecipe(shapelessRecipe);

        // ice bomb
        recipe = new ShapedRecipe(CommonConstants.ICE_BOMB_RECIPE_KEY, IceBomb.getItemStack(1)); 
        recipe.shape(
            " S ",
            "SGS",
            " S "
        );
        recipe.setIngredient('S', Material.SNOWBALL);
        recipe.setIngredient('G', Material.GUNPOWDER);
        getServer().addRecipe(recipe);

    }
}

package com.github.bcTornado608.papermcportal;



import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.github.bcTornado608.papermcportal.constants.CommonConstants;
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
    }
}

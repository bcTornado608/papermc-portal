package com.github.bcTornado608.papermcportal.utils;

import java.io.Serializable;
import java.util.UUID;

import org.bukkit.World;

public class WorldWrapper implements Serializable{
    private UUID uuid = null;
    public WorldWrapper(UUID uuid){
        this.uuid = uuid;
    }
    public UUID getWorld(){
        return uuid;
    }
}

package com.github.bcTornado608.papermcportal.utils;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class Teleport {
    public static void t(Player player){
        World world = player.getWorld();
        Location location = new Location(world, 39, 63, 36, 0, 0);
        player.teleport(location);
    };

    public static void te(Player player, Location loc){
        player.teleport(loc);
    };
}

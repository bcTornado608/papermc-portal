package com.github.bcTornado608.papermcportal.tasks;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Slime;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import com.github.bcTornado608.papermcportal.Portal;
import com.github.bcTornado608.papermcportal.utils.PotHelpers;

public class SlimePotTask extends BukkitRunnable{
    private Set<UUID> itemsInWater = new HashSet<>();
    private Random rng = new Random();

    private static final int MAX_SLIME_SIZE = 16;
    private static final int MAX_POT_SIZE = 256;


    @Override
    public void run() {
        Set<Item> allItems = new HashSet<>();
        for (World world : Portal.getInstance().getServer().getWorlds()) {
            Collection<Item> items = world.getEntitiesByClass(Item.class);
            allItems.addAll(items);
        }
        itemsInWater.retainAll(allItems.stream().map(Item::getUniqueId).collect(Collectors.toSet()));
        allItems.forEach(this::updateItemInWater);
    }

    private void updateItemInWater(Item item){
        if(item.getLocation().getBlock().getType() == Material.WATER){
            // item in water
            if(itemsInWater.contains(item.getUniqueId())){
                // already in water, ignore
            } else {
                // just entered water, call trigger
                processItemEnterWaterEvent(item);
                itemsInWater.add(item.getUniqueId());
            }
        } else {
            // item not in water
            itemsInWater.remove(item.getUniqueId());
        }
    }
    
    private void processItemEnterWaterEvent(Item item){
        ItemStack itemStack = item.getItemStack();
        if (itemStack.getType() == Material.SLIME_BALL){
            // slime ball entered water, detect copper pot and spawn slime
            if (PotHelpers.detectPot(Material.COPPER_BLOCK, Material.WATER, item.getLocation().getBlock(), MAX_POT_SIZE).isEmpty()){
                // not a pot, ignore
            } else {
                // is a pot, delete the slime ball, generate a slime
                Location loc = item.getLocation();
                ItemStack stack = item.getItemStack();
                while (stack.getAmount() > 0) {
                    Slime newSlime = (Slime)loc.getWorld().spawnEntity(loc, EntityType.SLIME, SpawnReason.CUSTOM);
                    newSlime.setSize(0);
                    stack.setAmount(stack.getAmount() - 1);
                }
                item.remove();
            }
        } else if (itemStack.getType() == Material.SUGAR){
            // sugar entered water, detect copper pot and grow slime
            Set<Block> potVolume = PotHelpers.detectPot(Material.COPPER_BLOCK, Material.WATER, item.getLocation().getBlock(), MAX_POT_SIZE);
            if(potVolume.isEmpty()){
                // not a pot, ignore
            } else {
                Collection<Slime> allSlimes = item.getWorld().getEntitiesByClass(Slime.class);
                List<Slime> slimesInPot = allSlimes.stream()
                        .filter(slime -> potVolume.contains(slime.getLocation().getBlock()) || potVolume.contains(slime.getLocation().getBlock().getRelative(BlockFace.DOWN)))
                        .filter(slime -> slime.getSize() < MAX_SLIME_SIZE)
                        .collect(Collectors.toList());
                while(slimesInPot.size() > 0){
                    int idx = rng.nextInt(0, slimesInPot.size());
                    Slime luckySlime = slimesInPot.get(idx);
                    luckySlime.setSize(luckySlime.getSize() + 1);
                    ItemStack stack = item.getItemStack();
                    stack.setAmount(stack.getAmount() - 1);
                    if (stack.getAmount() == 0){
                        item.remove();
                        break;
                    }
                    if(luckySlime.getSize() >= 16){
                        slimesInPot.remove(idx);
                    }
                }
            }
        }
    }
}

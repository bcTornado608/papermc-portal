package com.github.bcTornado608.papermcportal.listeners;


import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.block.TileState;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.sign.Side;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;
import org.checkerframework.checker.units.qual.A;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.joml.Vector3i;

import com.github.bcTornado608.papermcportal.Portal;
import com.github.bcTornado608.papermcportal.constants.CommonConstants;
import com.github.bcTornado608.papermcportal.items.CopperAxe;
import com.github.bcTornado608.papermcportal.utils.FloatingBlocksHelpers;
import com.github.bcTornado608.papermcportal.utils.StringHash;
import com.github.bcTornado608.papermcportal.utils.Teleport;
import com.github.bcTornado608.papermcportal.utils.TextHelpers;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import net.kyori.adventure.text.format.NamedTextColor;

import org.apache.commons.lang3.ArrayUtils;
import org.bukkit.Location;


/**
 * NewPlayerListener
 */
public class PortalListener implements Listener {

    // @EventHandler(ignoreCancelled = true)
    // public void onBlockBreak(BlockBreakEvent event){
    //     event.setCancelled(true);
    //     event.getBlock().setType(Material.DIAMOND_BLOCK);
    //     Teleport.t(event.getPlayer());
    // }

    @EventHandler(ignoreCancelled = false)
    public void onItemUse(PlayerInteractEvent event){
        Portal.getInstance().getLogger().info(event.getEventName());
        if(event.getAction() == Action.LEFT_CLICK_AIR){
            ItemStack item = event.getPlayer().getInventory().getItemInMainHand();
            int[] stickloc = item.getItemMeta().getPersistentDataContainer().get(CommonConstants.ITEM_ID_KEY, PersistentDataType.INTEGER_ARRAY);
            event.getPlayer().sendPlainMessage("TELEPORTABLE:: X: " + stickloc[0] + ", Y: " + stickloc[1] + ", Z: " + stickloc[2]);
        } else if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
            BlockState state = event.getClickedBlock().getState();
            if(state instanceof TileState){
                int[] loc = ((TileState)state).getPersistentDataContainer().get(CommonConstants.ITEM_ID_KEY, PersistentDataType.INTEGER_ARRAY);
                event.getPlayer().sendPlainMessage("TELEPORTABLE:: X: " + loc[0] + ", Y: " + loc[1] + ", Z: " + loc[2]);
            }
        }
    }
    
    @EventHandler(ignoreCancelled = false)
    public void onItemUseAtEntity(EntityDamageByEntityEvent event){
        Portal.getInstance().getLogger().info(event.getEventName());
        if((event.getDamager() instanceof Player) && ((Player)event.getDamager()).getInventory().getItemInMainHand().getType() == Material.STICK){
            Teleport.t(((Player)event.getDamager()));
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onUseSign(PlayerInteractEvent event){
        // Creates teleport wand
        ItemStack item = event.getPlayer().getInventory().getItemInMainHand();
        int[] stickloc = item.getItemMeta().getPersistentDataContainer().get(CommonConstants.ITEM_ID_KEY, PersistentDataType.INTEGER_ARRAY);
        if(event.getAction() == Action.RIGHT_CLICK_BLOCK && item.getType() == Material.STICK) {
            // if stick is not connected to any portal yet
            if(stickloc == null){
                BlockState st = event.getClickedBlock().getState();
                // if clicked on a sign near portal
                if(st instanceof Sign && isNearPortal(st.getLocation())) {
                    event.getPlayer().sendPlainMessage("The stick in your hand glows abnormally...");
                    String[] lines = ArrayUtils.addAll(((Sign)st).getSide(Side.FRONT).getLines(), ((Sign)st).getSide(Side.BACK).getLines());
                    event.getPlayer().sendPlainMessage(lines[0]);
                    ItemMeta meta = item.getItemMeta();
                    meta.displayName(TextHelpers.normalText("Unusual Stick"));
                    meta.lore(ImmutableList.of(TextHelpers.italicText("The stick omits faint light...", NamedTextColor.GREEN)));
                    Location loc = st.getLocation();
                    int[] LOCATION = {(int)loc.getX(), (int)loc.getY(), (int)loc.getZ(), (int)loc.getYaw(), (int)loc.getPitch(), StringHash.hash(lines[0])};
                    meta.getPersistentDataContainer().set(CommonConstants.ITEM_ID_KEY, PersistentDataType.INTEGER_ARRAY, LOCATION);
                    item.setItemMeta(meta);
                    item.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 1);
                } else {
                    event.getPlayer().sendPlainMessage("Nothing happens...");
                }
            } else {
                // stick is already connected to a portal
                Block signn = event.getClickedBlock();
                BlockState st = signn.getState();
                if(st instanceof Sign && isNearPortal(st.getLocation())) {
                    String[] lines = ArrayUtils.addAll(((Sign)st).getSide(Side.FRONT).getLines(), ((Sign)st).getSide(Side.BACK).getLines());
                    if(stickloc[5] == StringHash.hash(lines[0])){
                        /* setup the portal here */
                        // sets current sign state
                        TileState state = (TileState) signn.getState();
                        state.getPersistentDataContainer().set(CommonConstants.ITEM_ID_KEY, PersistentDataType.INTEGER_ARRAY, stickloc);
                        state.update();
                        // sets remote sign state
                        Location location = new Location(event.getPlayer().getWorld(), stickloc[0], stickloc[1], stickloc[2], stickloc[3], stickloc[4]);
                        Block rmtsign = location.getBlock();
                        if(!(rmtsign.getState() instanceof Sign)){
                            event.getPlayer().sendPlainMessage("The light dies down within your hand...");
                            event.getPlayer().getInventory().setItemInMainHand(new ItemStack(Material.STICK));
                        } else {
                            Location curLoc = signn.getLocation();
                            int[] CURLOCATION = {(int)curLoc.getX(), (int)curLoc.getY(), (int)curLoc.getZ(), (int)curLoc.getYaw(), (int)curLoc.getPitch(), stickloc[5]};
                            TileState rmtstate = (TileState) rmtsign.getState();
                            rmtstate.getPersistentDataContainer().set(CommonConstants.ITEM_ID_KEY, PersistentDataType.INTEGER_ARRAY, CURLOCATION);
                            rmtstate.update();
                        }

                        event.getPlayer().sendPlainMessage("An stream of abnormal energy flows out of the stick in your hand...");
                        event.getPlayer().getInventory().setItemInMainHand(new ItemStack(Material.STICK));
                    } else {
                        event.getPlayer().sendPlainMessage("Nothing happens...");
                    }
                    
                } else {
                    event.getPlayer().sendPlainMessage("Nothing happens...");
                }
            }
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onStepIntoPortal(PlayerMoveEvent event){

    }

    // If the location is near a portal
    /* A    A    A    A     A     A
     * A    S    S    S     S     A
     * A    S    L    L     S     A
     * A    S    L    L     S     A
     * A    S    S    S     S     A
     * A    A    A    A     A     A
     */
    // A: acceptable positions; S: stairs; L: lava
    public boolean isNearPortal(Location location){
        return true;
    }

    // Returns the sign associated with the portal
    // Returns null if not a portal
    public Block steppedInPortal(Location location){
        return null;
    }

    // @EventHandler(ignoreCancelled = false)
    // public void onPlaceSign(BlockPlaceEvent event){
    //     Block blk = event.getBlock();
    //     if(blk instanceof Sign) {
    //         String[] lines = ArrayUtils.addAll(((Sign)blk).getSide(Side.FRONT).getLines(), ((Sign)blk).getSide(Side.BACK).getLines());
    //     }
    // }
}
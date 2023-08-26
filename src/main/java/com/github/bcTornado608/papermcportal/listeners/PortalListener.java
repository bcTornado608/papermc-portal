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
import org.bukkit.block.data.type.Stairs;
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
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.checkerframework.checker.units.qual.A;
import org.checkerframework.checker.units.qual.t;
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
            if(item.getItemMeta() == null) return;
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

                if(isNearPortal(st.getLocation()) != null){
                    event.getPlayer().sendPlainMessage("THE SIGN IS NEAR PORTAL");
                } else {
                    event.getPlayer().sendPlainMessage("THE SIGN IS NOT NEAR PORTAL");
                }

                // if clicked on a sign near portal
                if(st instanceof Sign && isNearPortal(st.getLocation()) != null && st instanceof TileState && (((Sign)st).getPersistentDataContainer().get(CommonConstants.ITEM_ID_KEY, PersistentDataType.INTEGER_ARRAY) == null)) {
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
                if(st instanceof Sign && isNearPortal(st.getLocation()) != null) {
                    String[] lines = ArrayUtils.addAll(((Sign)st).getSide(Side.FRONT).getLines(), ((Sign)st).getSide(Side.BACK).getLines());
                    TileState state = (TileState) signn.getState();
                    Location location = new Location(event.getPlayer().getWorld(), stickloc[0], stickloc[1], stickloc[2], stickloc[3], stickloc[4]);
                    // if sign matches and is not already occupied
                    if(stickloc[5] == StringHash.hash(lines[0]) && (state.getPersistentDataContainer().get(CommonConstants.ITEM_ID_KEY, PersistentDataType.INTEGER_ARRAY) == null) && !(location.getBlock().equals(st.getBlock()))){
                        /* setup the portal here */
                        // sets current sign state
                        state.getPersistentDataContainer().set(CommonConstants.ITEM_ID_KEY, PersistentDataType.INTEGER_ARRAY, stickloc);
                        state.update();
                        // sets remote sign state
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

    @EventHandler(ignoreCancelled = false)
    public void onStepIntoPortal(PlayerMoveEvent event){
        Location blkloc = event.getTo();
        Location blklocprev = event.getFrom();

        if(blkloc.getBlock().getType() == Material.LAVA && blklocprev.getBlock().getType() != Material.LAVA){
            Block sign = isInPortal(blkloc) == null ? isInPortal(blklocprev) : isInPortal(blkloc);
            if(sign == null) return;    
            event.getPlayer().setInvulnerable(true);
            // event.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 100, 2));
            event.getPlayer().setWalkSpeed((float)1);
            BlockState st = sign.getState();
            Block rmtsign = null;
            if(st instanceof TileState){
                int[] locArr = ((TileState)st).getPersistentDataContainer().get(CommonConstants.ITEM_ID_KEY, PersistentDataType.INTEGER_ARRAY);
                Location location = new Location(event.getPlayer().getWorld(), locArr[0], locArr[1], locArr[2], locArr[3], locArr[4]);
                rmtsign = location.getBlock();
            }
            if(rmtsign != null){
                Location destination = isNearPortal(rmtsign.getLocation());
                Teleport.te(event.getPlayer(), destination);
                event.getPlayer().sendPlainMessage("You stepped into a portal connected to magic web...");
            }
        } else if (blklocprev.getBlock().getType() == Material.LAVA && blkloc.getBlock().getType() != Material.LAVA){
            Block sign = isInPortal(blkloc) == null ? isInPortal(blklocprev) : isInPortal(blkloc);
            if(sign == null) return;

            event.getPlayer().setInvulnerable(false);
            event.getPlayer().setFireTicks(0);
            event.getPlayer().setWalkSpeed((float)0.2);
        }
    }

    // If the location is near a portal
    /*              X
     *              |
     *              |
     * A    A    A    A     A     A
     * A    S    S    S     S     A
     * A    S    L    L     S     A —————— Z
     * A    S    L   (L)    S     A
     * A    S    S    S     S     A
     * A    A    A    A     A     A 
     */
    // A: acceptable positions; S: stairs; L: lava
    // On success returns the location of the centre of the portal
    public Location isNearPortal(Location loc){
        Vector tfront = new Vector(1, 0, 0);
        Vector tback = new Vector(-1, 0, 0);
        Vector tleft = new Vector(0, 0, -1);
        Vector tright = new Vector(0, 0, 1);
        Location lmax = loc.add(tleft.multiply(2));
        Location rmax = loc.add(tright.multiply(2));
        Location fmax = loc.add(tfront.multiply(2));
        Location bmax = loc.add(tback.multiply(2));
        if(lmax.getBlock().getType() == Material.LAVA){
            Portal.getInstance().getLogger().info("NEAR LAVA");
            if(isInPortal(lmax) != null) return lmax;
        }
        else if(rmax.getBlock().getType() == Material.LAVA){
            Portal.getInstance().getLogger().info("NEAR LAVA");
            if(isInPortal(rmax) != null) return lmax;
        }
        else if(fmax.getBlock().getType() == Material.LAVA){
            Portal.getInstance().getLogger().info("NEAR LAVA");
            if(isInPortal(fmax) != null) return lmax;
        }
        else if(bmax.getBlock().getType() == Material.LAVA){
            Portal.getInstance().getLogger().info("NEAR LAVA");
            if(isInPortal(bmax) != null) return lmax;
        } else{
            Portal.getInstance().getLogger().info("NOT NEAR LAVA");
        }
        return null;
    }

    // Returns the sign associated with the portal **** must activate sign otherwise return null
    // Returns null if not a portal
    public Block isInPortal(Location loc){
        int sizelimit = 3;
        Vector tfront = new Vector(1, 0, 0);
        Vector tback = new Vector(-1, 0, 0);
        Vector tleft = new Vector(0, 0, -1);
        Vector tright = new Vector(0, 0, 1);
        
        Location lmax = loc;
        int i = 0;
        while(!(lmax.getBlock().getState().getBlockData() instanceof Stairs) && i < 3){
            lmax = lmax.add(tleft);
            i++;
        }
        i = 0;
        Location rmax = loc;
        while(!(rmax.getBlock().getState().getBlockData() instanceof Stairs) && i < 3){
            rmax = rmax.add(tright);
            i++;
        }
        i = 0;
        Location fmax = loc;
        while(!(fmax.getBlock().getState().getBlockData() instanceof Stairs) && i < 3){
            fmax = fmax.add(tfront);
            i++;
        }
        i = 0;
        Location bmax = loc;
        while(!(bmax.getBlock().getState().getBlockData() instanceof Stairs) && i < 3){
            bmax = bmax.add(tback);
            i++;
        }
        if((rmax.getZ() - lmax.getZ() > sizelimit+1)||(fmax.getX() - bmax.getX() > sizelimit+1)) return null;
        
        boolean isPortal = true;
        // check if boundary is made of stairs
        for(int j = (int)lmax.getZ(); j < (int)rmax.getZ()+1; j++){
            Location upperBound = new Location(loc.getWorld(), fmax.getX(), loc.getY(), (double)j);
            Location lowerBound = new Location(loc.getWorld(), bmax.getX(), loc.getY(), (double)j);
            if(!(upperBound.getBlock().getState().getBlockData() instanceof Stairs && lowerBound.getBlock().getState().getBlockData() instanceof Stairs)){
                isPortal = false;
            }
        }

        for(int k = (int)bmax.getZ(); k < (int)fmax.getZ()+1; k++){
            Location leftBound = new Location(loc.getWorld(), (double)k, loc.getY(), lmax.getZ());
            Location rightBound = new Location(loc.getWorld(), (double)k, loc.getY(), rmax.getZ());
            if(!(leftBound.getBlock().getState().getBlockData() instanceof Stairs && rightBound.getBlock().getState().getBlockData() instanceof Stairs)){
                isPortal = false;
            }
        }
        // check if filled with lava
        for(int j = (int)lmax.getZ()+1; j < (int)rmax.getZ(); j++){
            for(int l = (int)bmax.getX()+1; l < (int)fmax.getX(); l++){
                Location locacheck = new Location(loc.getWorld(), (double)l, loc.getY(), (double)j);
                if(locacheck.getBlock().getType() != Material.LAVA){
                    isPortal = false;
                }
            }
        }
        // find the associated sign
        if(isPortal){
            for(int j = (int)lmax.getZ()-1; j < (int)rmax.getZ()+2; j++){
                Location upperBound = new Location(loc.getWorld(), fmax.getX()+1, loc.getY(), (double)j);
                Location lowerBound = new Location(loc.getWorld(), bmax.getX()-1, loc.getY(), (double)j);
                Location signloc = (upperBound.getBlock().getState() instanceof Sign) ? upperBound : ((lowerBound.getBlock().getState() instanceof Sign) ? lowerBound : null);
                if(signloc != null){
                    return signloc.getBlock();
                }
            }

            for(int k = (int)bmax.getZ()-1; k < (int)fmax.getZ()+2; k++){
                Location leftBound = new Location(loc.getWorld(), (double)k, loc.getY(), lmax.getZ()-1);
                Location rightBound = new Location(loc.getWorld(), (double)k, loc.getY(), rmax.getZ()+1);
                Location signloc = (leftBound.getBlock().getState() instanceof Sign) ? leftBound : ((rightBound.getBlock().getState() instanceof Sign) ? rightBound : null);
                if(signloc != null){
                    return signloc.getBlock();
                }
            }
        }
                            
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
package com.github.bcTornado608.papermcportal.listeners;


import org.apache.commons.lang3.ArrayUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.block.TileState;
import org.bukkit.block.data.type.Stairs;
import org.bukkit.block.sign.Side;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import com.github.bcTornado608.papermcportal.Portal;
import com.github.bcTornado608.papermcportal.constants.CommonConstants;
import com.github.bcTornado608.papermcportal.utils.StringHash;
import com.github.bcTornado608.papermcportal.utils.Teleport;
import com.github.bcTornado608.papermcportal.utils.TextHelpers;
import com.google.common.collect.ImmutableList;

import net.kyori.adventure.text.format.NamedTextColor;


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

    // @EventHandler(ignoreCancelled = false)
    // public void onItemUse(PlayerInteractEvent event){
    //     Portal.getInstance().getLogger().info(event.getEventName());
    //     if(event.getAction() == Action.LEFT_CLICK_AIR){
    //         ItemStack item = event.getPlayer().getInventory().getItemInMainHand();
    //         int[] stickloc = item.getItemMeta().getPersistentDataContainer().get(CommonConstants.ITEM_ID_KEY, PersistentDataType.INTEGER_ARRAY);
    //         if(stickloc == null) return;
    //         event.getPlayer().sendPlainMessage("TELEPORTABLE:: X: " + stickloc[0] + ", Y: " + stickloc[1] + ", Z: " + stickloc[2]);
    //     } else if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
    //         BlockState state = event.getClickedBlock().getState();
    //         if(state instanceof TileState){
    //             int[] loc = ((TileState)state).getPersistentDataContainer().get(CommonConstants.ITEM_ID_KEY, PersistentDataType.INTEGER_ARRAY);
    //             if(loc == null) return;
    //             event.getPlayer().sendPlainMessage("TELEPORTABLE:: X: " + loc[0] + ", Y: " + loc[1] + ", Z: " + loc[2]);
    //         }
    //     }
    // }
    
    // @EventHandler(ignoreCancelled = false)
    // public void onItemUseAtEntity(EntityDamageByEntityEvent event){
    //     Portal.getInstance().getLogger().info(event.getEventName());
    //     if((event.getDamager() instanceof Player) && ((Player)event.getDamager()).getInventory().getItemInMainHand().getType() == Material.STICK){
    //         Teleport.t(((Player)event.getDamager()));
    //     }
    // }

    @EventHandler(ignoreCancelled = true)
    public void onUseSign(PlayerInteractEvent event){
        // Creates teleport wand
        ItemStack item = event.getPlayer().getInventory().getItemInMainHand();
        int[] stickloc = item.getItemMeta().getPersistentDataContainer().get(CommonConstants.ITEM_ID_KEY, PersistentDataType.INTEGER_ARRAY);
        if(event.getAction() == Action.RIGHT_CLICK_BLOCK && item.getType() == Material.STICK) {
            // if stick is not connected to any portal yet
            if(stickloc == null){
                BlockState st = event.getClickedBlock().getState();

                // if(isNearPortal(st.getLocation()) != null){
                //     event.getPlayer().sendPlainMessage("THE SIGN IS NEAR PORTAL");
                // } else {
                //     event.getPlayer().sendPlainMessage("THE SIGN IS NOT NEAR PORTAL");
                // }

                // if clicked on a sign near portal
                if(st instanceof Sign && isNearPortal(st.getLocation()) != null && st instanceof TileState && (((Sign)st).getPersistentDataContainer().get(CommonConstants.ITEM_ID_KEY, PersistentDataType.INTEGER_ARRAY) == null)) {
                    event.getPlayer().sendPlainMessage("The stick in your hand glows abnormally...");
                    String[] lines = ArrayUtils.addAll(((Sign)st).getSide(Side.FRONT).getLines(), ((Sign)st).getSide(Side.BACK).getLines());
                    event.getPlayer().sendPlainMessage(lines[0]);
                    ItemMeta meta = item.getItemMeta();
                    meta.displayName(TextHelpers.normalText("Unusual Stick"));
                    meta.lore(ImmutableList.of(TextHelpers.italicText("The stick omits faint light...", NamedTextColor.GREEN)));
                    Location loc = st.getLocation();
                    int[] LOCATION = {(int)loc.getBlockX(), (int)loc.getBlockY(), (int)loc.getBlockZ(), (int)loc.getYaw(), (int)loc.getPitch(), StringHash.hash(lines[0])};
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
                            int[] CURLOCATION = {(int)curLoc.getBlockX(), (int)curLoc.getBlockY(), (int)curLoc.getBlockZ(), (int)curLoc.getYaw(), (int)curLoc.getPitch(), stickloc[5]};
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
        Location blkloc = event.getTo().clone();
        Location blklocprev = event.getFrom().clone();
        // Location cur = event.getPlayer().getLocation();
        // event.getPlayer().sendPlainMessage("You are stepping on: "+cur.add(0, -1, 0).getBlock().getType().toString());

        if(blkloc.getBlock().getType() == Material.LAVA && blklocprev.getBlock().getType() != Material.LAVA){
            Block sign = (isInPortal(blkloc) == null) ? isInPortal(blklocprev) : isInPortal(blkloc);
            if(sign == null) return;    
            event.getPlayer().setInvulnerable(true);
            // event.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 100, 2));
            // event.getPlayer().setWalkSpeed((float)1);
            BlockState st = sign.getState();
            Block rmtsign = null;
            if(st instanceof TileState){
                int[] locArr = ((TileState)st).getPersistentDataContainer().get(CommonConstants.ITEM_ID_KEY, PersistentDataType.INTEGER_ARRAY);
                if(locArr == null){
                    return;
                }
                Location location = new Location(event.getPlayer().getWorld(), locArr[0], locArr[1], locArr[2], locArr[3], locArr[4]);
                rmtsign = location.getBlock();
            }
            if(rmtsign != null){
                Location destination = isNearPortal(rmtsign.getLocation());
                if(destination == null){
                    return;
                }
                Teleport.te(event.getPlayer(), destination);
                event.getPlayer().sendPlainMessage("You stepped into a portal connected to magic web...");
            }
        } else if (blklocprev.getBlock().getType() == Material.LAVA && blkloc.getBlock().getType() != Material.LAVA){
            Block sign = (isInPortal(blkloc) == null) ? isInPortal(blklocprev) : isInPortal(blkloc);
            if(sign == null) return;

            event.getPlayer().setInvulnerable(false);
            event.getPlayer().setFireTicks(0);
            event.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 360, 1));
            // event.getPlayer().setWalkSpeed((float)0.2);
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
        Location lmax = loc.clone().add(tleft.multiply(2));
        Location rmax = loc.clone().add(tright.multiply(2));
        Location fmax = loc.clone().add(tfront.multiply(2));
        Location bmax = loc.clone().add(tback.multiply(2));
        // Portal.getInstance().getLogger().info("ORIGINAL LOCATION: "+loc.getBlockX() + "," + loc.getBlockY() + "," + loc.getBlockZ());
        // Portal.getInstance().getLogger().info(lmax.getBlockX() + "," + lmax.getBlockY()+"," + lmax.getBlockZ());
        // Portal.getInstance().getLogger().info(rmax.getBlockX() + "," + rmax.getBlockY()+"," + rmax.getBlockZ());
        // Portal.getInstance().getLogger().info(fmax.getBlockX() + "," + fmax.getBlockY()+"," + fmax.getBlockZ());
        // Portal.getInstance().getLogger().info(bmax.getBlockX() + "," + bmax.getBlockY()+"," + bmax.getBlockZ());

        if(lmax.getBlock().getType() == Material.LAVA){
            // Portal.getInstance().getLogger().info("NEAR LAVA");
            if(isInPortal(lmax) != null) return lmax;
        }
        else if(rmax.getBlock().getType() == Material.LAVA){
            // Portal.getInstance().getLogger().info("NEAR LAVA");
            if(isInPortal(rmax) != null) return rmax;
        }
        else if(fmax.getBlock().getType() == Material.LAVA){
            // Portal.getInstance().getLogger().info("NEAR LAVA");
            if(isInPortal(fmax) != null) return fmax;
        }
        else if(bmax.getBlock().getType() == Material.LAVA){
            // Portal.getInstance().getLogger().info("NEAR LAVA");
            if(isInPortal(bmax) != null) return bmax;
        } else{
            // Portal.getInstance().getLogger().info("NOT NEAR LAVA");
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
        
        Location lmax = loc.clone();
        int i = 0;
        while(!(lmax.getBlock().getState().getBlockData() instanceof Stairs) && i < 3){
            lmax.add(tleft);
            i++;
        }
        i = 0;
        Location rmax = loc.clone();
        while(!(rmax.getBlock().getState().getBlockData() instanceof Stairs) && i < 3){
            rmax.add(tright);
            i++;
        }
        i = 0;
        Location fmax = loc.clone();
        while(!(fmax.getBlock().getState().getBlockData() instanceof Stairs) && i < 3){
            fmax.add(tfront);
            i++;
        }
        i = 0;
        Location bmax = loc.clone();
        while(!(bmax.getBlock().getState().getBlockData() instanceof Stairs) && i < 3){
            bmax.add(tback);
            i++;
        }
        // Portal.getInstance().getLogger().info(lmax.getBlockX() + "," + lmax.getBlockY()+"," + lmax.getBlockZ());
        // Portal.getInstance().getLogger().info(rmax.getBlockX() + "," + rmax.getBlockY()+"," + rmax.getBlockZ());
        // Portal.getInstance().getLogger().info(fmax.getBlockX() + "," + fmax.getBlockY()+"," + fmax.getBlockZ());
        // Portal.getInstance().getLogger().info(bmax.getBlockX() + "," + bmax.getBlockY()+"," + bmax.getBlockZ());


        if((rmax.getBlockZ() - lmax.getBlockZ() > sizelimit+1)||(fmax.getBlockX() - bmax.getBlockX() > sizelimit+1)) return null;
        
        boolean isPortal = true;
        // check if boundary is made of stairs
        for(int j = (int)lmax.getBlockZ(); j < (int)rmax.getBlockZ()+1; j++){
            Location upperBound = new Location(loc.getWorld(), fmax.getBlockX(), loc.getBlockY(), (double)j);
            Location lowerBound = new Location(loc.getWorld(), bmax.getBlockX(), loc.getBlockY(), (double)j);
            if(!(upperBound.getBlock().getState().getBlockData() instanceof Stairs && lowerBound.getBlock().getState().getBlockData() instanceof Stairs)){
                isPortal = false;
                Portal.getInstance().getLogger().info("a");
            }
            // upperBound.getBlock().setType(Material.DIAMOND_BLOCK);
            // lowerBound.getBlock().setType(Material.DIAMOND_BLOCK);
        }

        for(int k = (int)bmax.getBlockX(); k < (int)fmax.getBlockX()+1; k++){
            Location leftBound = new Location(loc.getWorld(), (double)k, loc.getBlockY(), lmax.getBlockZ());
            Location rightBound = new Location(loc.getWorld(), (double)k, loc.getBlockY(), rmax.getBlockZ());
            if(!(leftBound.getBlock().getState().getBlockData() instanceof Stairs && rightBound.getBlock().getState().getBlockData() instanceof Stairs)){
                isPortal = false;
                Portal.getInstance().getLogger().info("b");
            }
            // leftBound.getBlock().setType(Material.DIAMOND_BLOCK);
            // rightBound.getBlock().setType(Material.DIAMOND_BLOCK);
        }
        // check if filled with lava
        for(int j = (int)lmax.getBlockZ()+1; j < (int)rmax.getBlockZ(); j++){
            for(int l = (int)bmax.getBlockX()+1; l < (int)fmax.getBlockX(); l++){
                Location locacheck = new Location(loc.getWorld(), (double)l, loc.getBlockY(), (double)j);
                if(locacheck.getBlock().getType() != Material.LAVA){
                    isPortal = false;
                    Portal.getInstance().getLogger().info("c");
                }
                // locacheck.getBlock().setType(Material.DIAMOND_BLOCK);

            }
        }
        // find the associated sign
        if(isPortal){
            Portal.getInstance().getLogger().info("IT IS A PORTAL!");
            for(int j = (int)lmax.getBlockZ()-1; j < (int)rmax.getBlockZ()+2; j++){
                Location upperBound = new Location(loc.getWorld(), fmax.getBlockX()+1, loc.getBlockY(), (double)j);
                Location lowerBound = new Location(loc.getWorld(), bmax.getBlockX()-1, loc.getBlockY(), (double)j);
                Location signloc = (upperBound.getBlock().getState() instanceof Sign) ? upperBound : ((lowerBound.getBlock().getState() instanceof Sign) ? lowerBound : null);
                if(signloc != null){
                    return signloc.getBlock();
                }
            }

            for(int k = (int)bmax.getBlockX()-1; k < (int)fmax.getBlockX()+2; k++){
                Location leftBound = new Location(loc.getWorld(), (double)k, loc.getBlockY(), lmax.getBlockZ()-1);
                Location rightBound = new Location(loc.getWorld(), (double)k, loc.getBlockY(), rmax.getBlockZ()+1);
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
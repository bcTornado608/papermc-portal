package com.github.bcTornado608.papermcportal.listeners;


import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;

import org.apache.commons.lang3.ArrayUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.block.TileState;
import org.bukkit.block.data.type.Stairs;
import org.bukkit.block.sign.Side;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import org.codehaus.plexus.util.cli.CommandLineTimeOutException;

import com.github.bcTornado608.papermcportal.Portal;
import com.github.bcTornado608.papermcportal.constants.CommonConstants;
import com.github.bcTornado608.papermcportal.items.Normal_stick;
import com.github.bcTornado608.papermcportal.items.Teleportation_scroll;
import com.github.bcTornado608.papermcportal.items.Undying_scroll;
import com.github.bcTornado608.papermcportal.utils.StringHash;
import com.github.bcTornado608.papermcportal.utils.Teleport;
import com.github.bcTornado608.papermcportal.utils.TextHelpers;
import com.github.bcTornado608.papermcportal.utils.WorldDataType;
import com.github.bcTornado608.papermcportal.utils.WorldWrapper;
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

    @EventHandler(ignoreCancelled = false)
    public void onItemUse(PlayerInteractEvent event){
        Portal.getInstance().getLogger().info(event.getEventName());
        if(event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK){
            ItemStack item = event.getPlayer().getInventory().getItemInMainHand();
            if(!Teleportation_scroll.isItem(item)) return;
            int[] scrollLoc = item.getItemMeta().getPersistentDataContainer().get(CommonConstants.LOC_STORE_KEY, PersistentDataType.INTEGER_ARRAY);
            if(scrollLoc == null) return;
            if(event.getPlayer().getTargetBlock(null, 30).getState() instanceof Sign) return;
            Block rmtsign = null;
            Location location = new Location(Bukkit.getServer().getWorld(item.getItemMeta().getPersistentDataContainer().get(CommonConstants.WORLD_STORE_KEY, new WorldDataType()).getWorld()), scrollLoc[0], scrollLoc[1], scrollLoc[2], scrollLoc[3], scrollLoc[4]);
            rmtsign = location.getBlock();
            
            if(rmtsign != null){
                Location destination = isNearPortal(rmtsign.getLocation());
                if(destination == null){
                    return;
                }
                // teleports player to destination specified in the scroll
                // event.getPlayer().setInvulnerable(true);
                // add negative effect for using scroll
                Set<PotionEffect> effects = new HashSet<PotionEffect>();
                effects.add(new PotionEffect(PotionEffectType.POISON, 20*3, 2));            
                effects.add(new PotionEffect(PotionEffectType.DARKNESS, 20*20, 2));            
                effects.add(new PotionEffect(PotionEffectType.GLOWING, 20*60*2, 2));    
                effects.add(new PotionEffect(PotionEffectType.LEVITATION, 20*4, 2));            
                effects.add(new PotionEffect(PotionEffectType.WEAKNESS, 20*60*2, 2));            
                effects.add(new PotionEffect(PotionEffectType.WITHER, 20*6, 2)); 
                effects.add(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 20*30, 1));           
                event.getPlayer().addPotionEffects(effects);
                // event.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 100, 2));
                // event.getPlayer().setWalkSpeed((float)1);
                Teleport.te(event.getPlayer(), destination);
                event.getPlayer().sendPlainMessage("Magic of the item in your hand dies down...");
                ItemStack mhItem = event.getPlayer().getInventory().getItemInMainHand();
                mhItem.setAmount(mhItem.getAmount()-1);
                // event.getPlayer().getInventory().addItem(new ItemStack(Material.PAPER, 1));
                addToPlayerInv(event.getPlayer(), new ItemStack(Material.PAPER, 1));
            }
        }
    }
    
    // @EventHandler(ignoreCancelled = false)
    // public void onItemUseAtEntity(EntityDamageByEntityEvent event){
    //     Portal.getInstance().getLogger().info(event.getEventName());
    //     if((event.getDamager() instanceof Player) && (Teleportation_scroll.isItem(((Player)event.getDamager()).getInventory().getItemInMainHand()))){
    //         Player p = (Player)event.getDamager();
    //         // teleports player to destination specified in the scroll
    //         ItemStack item = p.getInventory().getItemInMainHand();
    //         if(!Teleportation_scroll.isItem(item)) return;
    //         int[] scrollLoc = item.getItemMeta().getPersistentDataContainer().get(CommonConstants.LOC_STORE_KEY, PersistentDataType.INTEGER_ARRAY);
    //         if(scrollLoc == null) return;
    //         // teleports player to destination specified in the scroll
    //         p.setInvulnerable(true);
    //         // p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 100, 2));
    //         // p.setWalkSpeed((float)1);
    //         Block rmtsign = null;
    //         Location location = new Location(item.getItemMeta().getPersistentDataContainer().get(CommonConstants.WORLD_STORE_KEY, new WorldDataType()).getWorld(), scrollLoc[0], scrollLoc[1], scrollLoc[2], scrollLoc[3], scrollLoc[4]);
    //         rmtsign = location.getBlock();

    //         if(rmtsign != null){
    //             Location destination = isNearPortal(rmtsign.getLocation());
    //             if(destination == null){
    //                 return;
    //             }
    //             Teleport.te(p, destination);
    //             p.sendPlainMessage("Magic of the item in your hand dies down...");
    //             ItemStack mhItem = p.getInventory().getItemInMainHand();
    //             mhItem.setAmount(mhItem.getAmount()-1);
    //             // p.getInventory().addItem(new ItemStack(Material.PAPER, 1));
    //             addToPlayerInv(p, new ItemStack(Material.PAPER, 1));
    //         }
    //     }
    // }

    @EventHandler(ignoreCancelled = true)
    public void onUseSign(PlayerInteractEvent event){
        // Creates teleport wand
        ItemStack item = event.getPlayer().getInventory().getItemInMainHand();
        ItemMeta met = item.getItemMeta();
        int[] stickloc = null;
        if(met!=null) stickloc = met.getPersistentDataContainer().get(CommonConstants.LOC_STORE_KEY, PersistentDataType.INTEGER_ARRAY);
        if(event.getAction() == Action.RIGHT_CLICK_BLOCK && Normal_stick.isItem(item)) {
            // if stick is not connected to any portal yet
            if(stickloc == null){
                BlockState st = event.getClickedBlock().getState();

                // if clicked on a sign near portal
                if(st instanceof Sign && isNearPortal(st.getLocation()) != null && st instanceof TileState && (((Sign)st).getPersistentDataContainer().get(CommonConstants.LOC_STORE_KEY, PersistentDataType.INTEGER_ARRAY) == null)) {
                    event.getPlayer().sendPlainMessage("The stick in your hand glows abnormally...");
                    String[] lines = ArrayUtils.addAll(((Sign)st).getSide(Side.FRONT).getLines(), ((Sign)st).getSide(Side.BACK).getLines());
                    event.getPlayer().sendPlainMessage(lines[0]);
                    item.setAmount(item.getAmount()-1);
                    ItemStack newitem = Normal_stick.getItemStack(1);
                    ItemMeta meta = newitem.getItemMeta();
                    meta.displayName(TextHelpers.italicText("\"Unusual Stick\"", NamedTextColor.GOLD));
                    meta.lore(ImmutableList.of(TextHelpers.italicText("The stick omits faint light...", NamedTextColor.RED)));
                    Location loc = st.getLocation();
                    int[] LOCATION = {(int)loc.getBlockX(), (int)loc.getBlockY(), (int)loc.getBlockZ(), (int)loc.getYaw(), (int)loc.getPitch(), StringHash.hash(lines[0])};
                    meta.getPersistentDataContainer().set(CommonConstants.LOC_STORE_KEY, PersistentDataType.INTEGER_ARRAY, LOCATION);
                    meta.getPersistentDataContainer().set(CommonConstants.WORLD_STORE_KEY, new WorldDataType(), new WorldWrapper(event.getPlayer().getWorld().getUID()));
                    newitem.setItemMeta(meta);
                    newitem.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 1);
                    // event.getPlayer().getInventory().addItem(newitem);
                    addToPlayerInv(event.getPlayer(), newitem);
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
                    Location location = new Location(Bukkit.getServer().getWorld(item.getItemMeta().getPersistentDataContainer().get(CommonConstants.WORLD_STORE_KEY, new WorldDataType()).getWorld()), stickloc[0], stickloc[1], stickloc[2], stickloc[3], stickloc[4]);
                    // if sign matches and is not already occupied
                    if(stickloc[5] == StringHash.hash(lines[0]) && (state.getPersistentDataContainer().get(CommonConstants.LOC_STORE_KEY, PersistentDataType.INTEGER_ARRAY) == null) && !(location.getBlock().equals(st.getBlock()))){
                        /* setup the portal here */
                        // sets current sign state
                        state.getPersistentDataContainer().set(CommonConstants.LOC_STORE_KEY, PersistentDataType.INTEGER_ARRAY, stickloc);
                        state.getPersistentDataContainer().set(CommonConstants.WORLD_STORE_KEY, new WorldDataType(), met.getPersistentDataContainer().get(CommonConstants.WORLD_STORE_KEY, new WorldDataType()));
                        state.update();
                        // sets remote sign state
                        Block rmtsign = location.getBlock();
                        if(!(rmtsign.getState() instanceof Sign)){
                            event.getPlayer().sendPlainMessage("The light dies down within your hand...");
                            event.getPlayer().getInventory().setItemInMainHand(Normal_stick.getItemStack(event.getPlayer().getInventory().getItemInMainHand().getAmount()));
                        } else {
                            Location curLoc = signn.getLocation();
                            int[] CURLOCATION = {(int)curLoc.getBlockX(), (int)curLoc.getBlockY(), (int)curLoc.getBlockZ(), (int)curLoc.getYaw(), (int)curLoc.getPitch(), stickloc[5]};
                            TileState rmtstate = (TileState) rmtsign.getState();
                            rmtstate.getPersistentDataContainer().set(CommonConstants.LOC_STORE_KEY, PersistentDataType.INTEGER_ARRAY, CURLOCATION);
                            rmtstate.getPersistentDataContainer().set(CommonConstants.WORLD_STORE_KEY, new WorldDataType(), new WorldWrapper(state.getWorld().getUID()));
                            rmtstate.update();
                        }

                        event.getPlayer().sendPlainMessage("An stream of abnormal energy flows out of the stick in your hand...");
                        item.setAmount(item.getAmount()-1);
                        ItemStack newitem = Normal_stick.getItemStack(1);
                        // event.getPlayer().getInventory().addItem(newitem);
                        addToPlayerInv(event.getPlayer(), newitem);
                    } else {
                        event.getPlayer().sendPlainMessage("Nothing happens...");
                    }
                    
                } else {
                    event.getPlayer().sendPlainMessage("Nothing happens...");
                }
            }
            event.setCancelled(true);
        } else if (event.getAction() == Action.RIGHT_CLICK_BLOCK && (Teleportation_scroll.isItem(item) || Undying_scroll.isItem(item))) {
            // Player enchants a teleportation scroll
            BlockState st = event.getClickedBlock().getState();
            // if clicked on a sign near portal
            if(st instanceof Sign && isNearPortal(st.getLocation()) != null && st instanceof TileState) {
                event.getPlayer().sendPlainMessage("Magic flows in your hand...");
                String[] lines = ArrayUtils.addAll(((Sign)st).getSide(Side.FRONT).getLines(), ((Sign)st).getSide(Side.BACK).getLines());
                event.getPlayer().sendPlainMessage(lines[0]);
                //
                ItemStack newitem = Teleportation_scroll.isItem(item)? Teleportation_scroll.getItemStack(1) : ((Undying_scroll.isItem(item)) ? Undying_scroll.getItemStack(1) : null);
                item.setAmount(item.getAmount()-1);
                ItemMeta meta = newitem.getItemMeta();
                
                meta.lore(ImmutableList.of(TextHelpers.italicText(lines[0], NamedTextColor.RED)));
                Location loc = st.getLocation();
                int[] LOCATION = {(int)loc.getBlockX(), (int)loc.getBlockY(), (int)loc.getBlockZ(), (int)loc.getYaw(), (int)loc.getPitch(), StringHash.hash(lines[0])};
                meta.getPersistentDataContainer().set(CommonConstants.LOC_STORE_KEY, PersistentDataType.INTEGER_ARRAY, LOCATION);
                meta.getPersistentDataContainer().set(CommonConstants.WORLD_STORE_KEY, new WorldDataType(), new WorldWrapper(event.getPlayer().getWorld().getUID()));
                newitem.setItemMeta(meta);
                newitem.addUnsafeEnchantment(Enchantment.LUCK, 1);
                // event.getPlayer().getInventory().addItem(newitem);
                addToPlayerInv(event.getPlayer(), newitem);
            } else {
                event.getPlayer().sendPlainMessage("Nothing happens...");
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
            BlockState st = sign.getState();
            Block rmtsign = null;
            if(st instanceof TileState){
                int[] locArr = ((TileState)st).getPersistentDataContainer().get(CommonConstants.LOC_STORE_KEY, PersistentDataType.INTEGER_ARRAY);
                if(locArr == null){
                    return;
                }
                Location location = new Location(Bukkit.getServer().getWorld(((TileState)st).getPersistentDataContainer().get(CommonConstants.WORLD_STORE_KEY, new WorldDataType()).getWorld()), locArr[0], locArr[1], locArr[2], locArr[3], locArr[4]);
                rmtsign = location.getBlock();
            }
            if(rmtsign != null){
                Location destination = isNearPortal(rmtsign.getLocation());
                if(destination == null){
                    return;
                }
                // event.getPlayer().setInvulnerable(true);
                // event.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 100, 2));
                // event.getPlayer().setWalkSpeed((float)1);
                Teleport.te(event.getPlayer(), destination);
                event.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 20*30, 1));
                event.getPlayer().sendPlainMessage("You stepped into a portal connected to magic web...");
            }
        } else if (blklocprev.getBlock().getType() == Material.LAVA && blkloc.getBlock().getType() != Material.LAVA){
            Block sign = (isInPortal(blkloc) == null) ? isInPortal(blklocprev) : isInPortal(blkloc);
            if(sign == null) return;

            // event.getPlayer().setInvulnerable(false);
            event.getPlayer().setFireTicks(0);
            // event.getPlayer().setWalkSpeed((float)0.2);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerDeath(EntityDamageEvent event){
        if(event.getEntity() instanceof Player){
            Player p = (Player) event.getEntity();
            if(p.getHealth() <= event.getDamage()) {
                ItemStack[] inv = p.getInventory().getStorageContents();
                ItemStack it = p.getInventory().getItemInOffHand();
                ItemStack[] itarm = p.getInventory().getArmorContents();
                for(ItemStack is : inv){
                    if(is != null && is.getAmount() > 0 && Undying_scroll.isItem(is)){
                        // use undying scroll
                        int[] scrollLoc = is.getItemMeta().getPersistentDataContainer().get(CommonConstants.LOC_STORE_KEY, PersistentDataType.INTEGER_ARRAY);
                        if(scrollLoc == null) continue;
                        // teleports player to destination specified in the scroll
                        Block rmtsign = null;
                        Location location = new Location(Bukkit.getServer().getWorld(is.getItemMeta().getPersistentDataContainer().get(CommonConstants.WORLD_STORE_KEY, new WorldDataType()).getWorld()), scrollLoc[0], scrollLoc[1], scrollLoc[2], scrollLoc[3], scrollLoc[4]);
                        rmtsign = location.getBlock();
                        
                        if(rmtsign != null){
                            Location destination = isNearPortal(rmtsign.getLocation());
                            if(destination == null){
                                return;
                            }
                            Teleport.te(p, destination);
                            // p.setInvulnerable(true);
                            // p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 100, 2));
                            // p.setWalkSpeed((float)1);
                            p.setHealth(0.5);
                            p.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 20*60*2, 2));
                            p.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 20*30, 1));
                            p.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 20*60*2, 5));
                            p.sendPlainMessage("Your scroll of undying breaks, you can feel the magic aura around you died down.");
                        }
                        is.setAmount(is.getAmount()-1);
                        event.setCancelled(true);
                        break;
                    }
                }
                for(ItemStack is : itarm){
                    if(is != null && is.getAmount() > 0 && Undying_scroll.isItem(is)){
                        // use undying scroll
                        int[] scrollLoc = is.getItemMeta().getPersistentDataContainer().get(CommonConstants.LOC_STORE_KEY, PersistentDataType.INTEGER_ARRAY);
                        if(scrollLoc == null) continue;
                        // teleports player to destination specified in the scroll
                        Block rmtsign = null;
                        Location location = new Location(Bukkit.getServer().getWorld(is.getItemMeta().getPersistentDataContainer().get(CommonConstants.WORLD_STORE_KEY, new WorldDataType()).getWorld()), scrollLoc[0], scrollLoc[1], scrollLoc[2], scrollLoc[3], scrollLoc[4]);
                        rmtsign = location.getBlock();
                        
                        if(rmtsign != null){
                            Location destination = isNearPortal(rmtsign.getLocation());
                            if(destination == null){
                                return;
                            }
                            Teleport.te(p, destination);
                            // p.setInvulnerable(true);
                            // p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 100, 2));
                            // p.setWalkSpeed((float)1);
                            p.setHealth(0.5);
                            p.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 20*60*2, 2));
                            p.sendPlainMessage("Your scroll of undying breaks, you can feel the magic aura around you died down.");
                        }
                        is.setAmount(is.getAmount()-1);
                        event.setCancelled(true);
                        break;
                    }
                }
                if(it != null && it.getAmount() > 0 && Undying_scroll.isItem(it)){
                    // use undying scroll
                    int[] scrollLoc = it.getItemMeta().getPersistentDataContainer().get(CommonConstants.LOC_STORE_KEY, PersistentDataType.INTEGER_ARRAY);
                    if(scrollLoc == null) return;
                    // teleports player to destination specified in the scroll
                    Block rmtsign = null;
                    Location location = new Location(Bukkit.getServer().getWorld(it.getItemMeta().getPersistentDataContainer().get(CommonConstants.WORLD_STORE_KEY, new WorldDataType()).getWorld()), scrollLoc[0], scrollLoc[1], scrollLoc[2], scrollLoc[3], scrollLoc[4]);
                    rmtsign = location.getBlock();
                    
                    if(rmtsign != null){
                        Location destination = isNearPortal(rmtsign.getLocation());
                        if(destination == null){
                            return;
                        }
                        Teleport.te(p, destination);
                        // p.setInvulnerable(true);
                        // p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 100, 2));
                        // p.setWalkSpeed((float)1);
                        p.setHealth(0.5);
                        p.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 20*60*2, 2));
                        p.sendPlainMessage("Your scroll of undying breaks, you can feel the magic aura around you died down.");
                    }
                    it.setAmount(it.getAmount()-1);
                    event.setCancelled(true);
                }
            }
        }
    }


    public void addToPlayerInv(Player p, ItemStack itemStack){
        HashMap<Integer, ItemStack> nope = p.getInventory().addItem(itemStack);
        for(Entry<Integer, ItemStack> entry : nope.entrySet())
        {   
            p.getWorld().dropItemNaturally(p.getLocation(), entry.getValue());
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
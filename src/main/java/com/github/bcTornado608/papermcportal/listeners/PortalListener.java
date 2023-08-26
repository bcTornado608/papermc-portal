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
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.joml.Vector3i;

import com.github.bcTornado608.papermcportal.Portal;
import com.github.bcTornado608.papermcportal.constants.CommonConstants;
import com.github.bcTornado608.papermcportal.items.CopperAxe;
import com.github.bcTornado608.papermcportal.utils.FloatingBlocksHelpers;
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

    @EventHandler(ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event){
        event.setCancelled(true);
        event.getBlock().setType(Material.DIAMOND_BLOCK);
        Teleport.t(event.getPlayer());
    }

    @EventHandler(ignoreCancelled = false)
    public void onItemUse(PlayerInteractEvent event){
        Portal.getInstance().getLogger().info(event.getEventName());
        if(event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK){
            if(event.getPlayer().getInventory().getItemInMainHand().getType() == Material.STICK){
                Teleport.t(event.getPlayer());
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onUseSign(PlayerInteractEvent event){
        // Creates teleport wand
        ItemStack item = event.getPlayer().getInventory().getItemInMainHand();
        if(event.getAction() == Action.RIGHT_CLICK_BLOCK && item.getType() == Material.STICK) {
            // if stick is not connected to any portal yet
            if(item.getItemMeta().getPersistentDataContainer().get(CommonConstants.ITEM_ID_KEY, PersistentDataType.INTEGER_ARRAY) == null){
                event.getPlayer().sendPlainMessage("The stick in your hand glows abnormally...");
                BlockState st = event.getClickedBlock().getState();
                if(st instanceof Sign) {
                    String[] lines = ArrayUtils.addAll(((Sign)st).getSide(Side.FRONT).getLines(), ((Sign)st).getSide(Side.BACK).getLines());
                    event.getPlayer().sendPlainMessage(lines[0]);
                    ItemMeta meta = item.getItemMeta();
                    meta.displayName(TextHelpers.normalText("Unusual Stick"));
                    meta.lore(ImmutableList.of(TextHelpers.italicText("The stick omits faint light...", NamedTextColor.GREEN)));
                    Location loc = st.getLocation();
                    int[] LOCATION = {(int)loc.getX(), (int)loc.getY(), (int)loc.getZ(), (int)loc.getYaw(), (int)loc.getPitch()};
                    meta.getPersistentDataContainer().set(CommonConstants.ITEM_ID_KEY, PersistentDataType.INTEGER_ARRAY, LOCATION);
                    item.setItemMeta(meta);
                    item.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 1);
                }
            } else {
                // stick is already connected to a portal
                event.getPlayer().sendPlainMessage("Nothing happens...");
            }
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = false)
    public void onItemUseAtEntity(EntityDamageByEntityEvent event){
        Portal.getInstance().getLogger().info(event.getEventName());
        if((event.getDamager() instanceof Player) && ((Player)event.getDamager()).getInventory().getItemInMainHand().getType() == Material.STICK){
            Teleport.t(((Player)event.getDamager()));
        }
    }

    // @EventHandler(ignoreCancelled = false)
    // public void onPlaceSign(BlockPlaceEvent event){
    //     Block blk = event.getBlock();
    //     if(blk instanceof Sign) {
    //         String[] lines = ArrayUtils.addAll(((Sign)blk).getSide(Side.FRONT).getLines(), ((Sign)blk).getSide(Side.BACK).getLines());
    //     }
    // }
}
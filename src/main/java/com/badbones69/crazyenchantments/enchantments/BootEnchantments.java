package com.badbones69.crazyenchantments.enchantments;

import com.badbones69.crazyenchantments.CrazyEnchantments;
import com.badbones69.crazyenchantments.Starter;
import com.badbones69.crazyenchantments.api.CrazyManager;
import com.badbones69.crazyenchantments.api.PluginSupport.SupportedPlugins;
import com.badbones69.crazyenchantments.api.enums.CEnchantments;
import com.badbones69.crazyenchantments.api.events.ArmorEquipEvent;
import com.badbones69.crazyenchantments.api.managers.WingsManager;
import com.badbones69.crazyenchantments.api.support.anticheats.SpartanSupport;
import com.badbones69.crazyenchantments.utilities.WingsUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;

public class BootEnchantments implements Listener {

    private final CrazyEnchantments plugin = CrazyEnchantments.getPlugin();

    private final Starter starter = plugin.getStarter();

    private final CrazyManager crazyManager = starter.getCrazyManager();

    // Plugin Support.
    private final SpartanSupport spartanSupport = starter.getSpartanSupport();

    // Plugin Managers.
    private final WingsManager wingsManager = starter.getWingsManager();

    // Utils
    private final WingsUtils wingsUtils = starter.getWingsUtils();

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onEquip(ArmorEquipEvent event) {
        if (!wingsManager.isWingsEnabled()) return;

        Player player = event.getPlayer();

        // Check the new armor piece.
        wingsUtils.checkArmor(event.getNewArmorPiece(), true, null, player);

        // Check the old armor piece.
        wingsUtils.checkArmor(null, false, event.getOldArmorPiece(), player);
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onFly(PlayerToggleFlightEvent event) {
        if (!wingsManager.isWingsEnabled()) return;

        Player player = event.getPlayer();

        if (player.getEquipment().getBoots() == null) return;
        if (!crazyManager.hasEnchantment(player.getEquipment().getBoots(), CEnchantments.WINGS)) return;

        if (wingsUtils.checkRegion(player)) return;
        if (wingsUtils.isEnemiesNearby(player)) return;

        if (SupportedPlugins.SPARTAN.isPluginLoaded()) spartanSupport.cancelNormalMovements(player);

        if (event.isFlying()) {
            if (player.getAllowFlight()) {
                event.setCancelled(true);
                player.setFlying(true);
                wingsManager.addFlyingPlayer(player);
            }
        } else {
            wingsManager.removeFlyingPlayer(player);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onMove(PlayerMoveEvent event) {
        if (event.getFrom() == event.getTo()) return;

        Player player = event.getPlayer();
        boolean isFlying = player.isFlying();

        if (wingsManager.isWingsEnabled() && crazyManager.hasEnchantment(player.getEquipment().getBoots(), CEnchantments.WINGS)) {
            if (wingsUtils.checkRegion(player)) {
                if (!wingsUtils.isEnemiesNearby(player)) {
                    player.setAllowFlight(true);
                } else {
                    if (isFlying && wingsUtils.checkGameMode(player)) {
                        player.setFlying(false);
                        player.setAllowFlight(false);
                        wingsManager.removeFlyingPlayer(player);
                    }
                }
            } else {
                if (isFlying && wingsUtils.checkGameMode(player)) {
                    player.setFlying(false);
                    player.setAllowFlight(false);
                    wingsManager.removeFlyingPlayer(player);
                }
            }

            if (isFlying) wingsManager.addFlyingPlayer(player);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onJoin(PlayerJoinEvent event) {
        if (!wingsManager.isWingsEnabled()) return;
        Player player = event.getPlayer();

        if (!crazyManager.hasEnchantment(player.getEquipment().getBoots(), CEnchantments.WINGS)) return;

        if (wingsUtils.checkRegion(player)) return;
        if (wingsUtils.isEnemiesNearby(player)) return;

        if (SupportedPlugins.SPARTAN.isPluginLoaded()) spartanSupport.cancelNormalMovements(player);

        player.setAllowFlight(true);
        wingsManager.addFlyingPlayer(player);
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onLeave(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        if (!wingsManager.isWingsEnabled()) return;
        if (!wingsManager.isFlyingPlayer(player)) return;

        player.setFlying(false);
        player.setAllowFlight(false);
        wingsManager.removeFlyingPlayer(player);
    }
}
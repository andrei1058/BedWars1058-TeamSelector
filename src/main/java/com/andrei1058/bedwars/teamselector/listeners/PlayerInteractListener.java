package com.andrei1058.bedwars.teamselector.listeners;

import com.andrei1058.bedwars.teamselector.Main;
import com.andrei1058.bedwars.teamselector.teamselector.TeamSelectorGUI;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class PlayerInteractListener implements Listener {

    @EventHandler
    public void onPlayerInteract(@NotNull PlayerInteractEvent e) {
        if (!(e.getAction() == Action.RIGHT_CLICK_BLOCK || e.getAction() == Action.RIGHT_CLICK_AIR)) return;
        ItemStack i = Main.bw.getVersionSupport().getItemInHand(e.getPlayer());
        if (i == null) return;
        if (i.getType() == Material.AIR) return;
        if (!Main.bw.getVersionSupport().isCustomBedWarsItem(i)) return;
        if (Main.bw.getVersionSupport().getCustomData(i).equals(TeamSelectorGUI.TEAM_SELECTOR_IDENTIFIER)) {
            e.setCancelled(true);
            TeamSelectorGUI.openGUI(e.getPlayer(), false);
        }
    }
}

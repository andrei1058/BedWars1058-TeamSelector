package com.andrei1058.bedwars.teamselector.listeners;

import com.andrei1058.bedwars.Main;
import com.andrei1058.bedwars.teamselector.configuration.Config;
import com.andrei1058.bedwars.teamselector.teamselector.TeamSelectorGUI;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class InventoryClickListener implements Listener {

    @EventHandler
    //Prevent inventory move
    public void onInventoryClick(InventoryClickEvent e){
        ItemStack i = e.getCurrentItem();
        if (i == null) return;
        if (i.getType() == Material.AIR) return;
        if (Config.config.getBoolean(Config.ALLOW_MOVE_TROUGH_INVENTORY)) return;
        if (!Main.nms.isCustomBedWarsItem(i)) return;
        if (Main.nms.getCustomData(i).equals(TeamSelectorGUI.TEAM_SELECTOR_IDENTIFIER)){
            e.setCancelled(true);
        }
    }
}

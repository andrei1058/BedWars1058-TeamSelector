package com.andrei1058.bedwars.teamselector.listeners;

import com.andrei1058.bedwars.Main;
import com.andrei1058.bedwars.api.PlayerJoinArenaEvent;
import com.andrei1058.bedwars.arena.Arena;
import com.andrei1058.bedwars.configuration.Language;
import com.andrei1058.bedwars.teamselector.configuration.Config;
import com.andrei1058.bedwars.teamselector.configuration.Messages;
import com.andrei1058.bedwars.teamselector.teamselector.TeamSelectorGUI;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ArenaJoinListener implements Listener {

    @EventHandler
    public void onBwArenaJoin(PlayerJoinArenaEvent e){
        if (e.isSpectator()) return;
        Arena arena = Arena.getArenaByPlayer(e.getPlayer());
        if (arena == null) return;

        ItemStack i;
        try {
            i = new ItemStack(Material.valueOf(Config.config.getString(Config.SELECTOR_ITEM_STACK_MATERIAL)));
        } catch (Exception ex){
            Main.plugin.getLogger().severe("Team-Selector Material is invalid!");
            ex.printStackTrace();
            return;
        }

        ItemMeta im = i.getItemMeta();
        im.setLore(Language.getList(e.getPlayer(), Messages.SELECTOR_LORE));
        im.setDisplayName(Language.getMsg(e.getPlayer(), Messages.SELECTOR_NAME));
        i.setItemMeta(im);
        i = com.andrei1058.bedwars.Main.nms.addCustomData(i, TeamSelectorGUI.TEAM_SELECTOR_IDENTIFIER);

        int slot = Config.config.getInt(Config.SELECTOR_SLOT);

        ItemStack finalI = i;
        Bukkit.getScheduler().runTaskLater(Main.plugin, () -> {
            if (slot < 0){
                e.getPlayer().getInventory().addItem(finalI);
            } else {
                e.getPlayer().getInventory().setItem(slot, finalI);
            }
        }, 10L);
    }
}

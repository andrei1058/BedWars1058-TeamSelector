package com.andrei1058.bedwars.teamselector.teamselector;

import com.andrei1058.bedwars.api.GameState;
import com.andrei1058.bedwars.arena.Arena;
import com.andrei1058.bedwars.arena.BedWarsTeam;
import com.andrei1058.bedwars.configuration.Language;
import com.andrei1058.bedwars.teamselector.api.events.TeamSelectorOpenEvent;
import com.andrei1058.bedwars.teamselector.configuration.Config;
import com.andrei1058.bedwars.teamselector.configuration.Messages;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.UUID;

public class TeamSelectorGUI {

    //Players with Team Selector GUI opened
    public static ArrayList<UUID> openGUIs = new ArrayList<>();

    //Gui opener identifier
    public static final String TEAM_SELECTOR_IDENTIFIER = "BWTEAMSELECTOR";

    public static void openGUI(Player player){
        //Check if arena isn't started yet
        Arena arena = Arena.getArenaByPlayer(player);
        if (arena == null) return;
        if (arena.getStatus() == GameState.playing) return;
        if (arena.getStatus() == GameState.restarting) return;

        //Call related event
        TeamSelectorOpenEvent e = new TeamSelectorOpenEvent(player);
        Bukkit.getPluginManager().callEvent(e);
        if (e.isCancelled()) return;

        //Create gui
        openGUIs.add(player.getUniqueId());
        int size = arena.getTeams().size();
        int layer1_stop_slot = 16;
        int layer2_stop_slot = 25;
        if (size > 7 && size <= 14) {
            size = 27;
        } else {
            size = 36;
        }
        int items_per_layer = arena.getTeams().size()/2;
        int start_slot = 16-items_per_layer-((6-items_per_layer)/2);
        Inventory inv = Bukkit.createInventory(null, size, Language.getMsg(player, Messages.GUI_NAME));
        for (BedWarsTeam bwt : arena.getTeams()) {
            ItemStack i = new ItemStack(Material.valueOf(Config.config.getString(Config.SELECTOR_ITEM_STACK_MATERIAL)));
            ItemMeta im = i.getItemMeta();
            im.setDisplayName(bwt.getName());
            i.setItemMeta(im);
            inv.setItem(start_slot, i);
            start_slot++;
            if (start_slot == layer1_stop_slot &&  arena.getTeams().size()-7 > 0){
                start_slot = 19-items_per_layer-((6-items_per_layer)/2);
            } else if (start_slot == layer2_stop_slot) {
                break;
            }
        }

        player.openInventory(inv);
    }
}

package com.andrei1058.bedwars.teamselector.teamselector;

import com.andrei1058.bedwars.api.GameState;
import com.andrei1058.bedwars.arena.Arena;
import com.andrei1058.bedwars.configuration.Language;
import com.andrei1058.bedwars.teamselector.api.events.TeamSelectorOpenEvent;
import com.andrei1058.bedwars.teamselector.configuration.Messages;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;
import java.util.UUID;

public class TeamSelectorGUI {

    private static int size = 27;

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
        Inventory inv = Bukkit.createInventory(null, size, Language.getMsg(player, Messages.GUI_NAME));

        player.openInventory(inv);
    }
}

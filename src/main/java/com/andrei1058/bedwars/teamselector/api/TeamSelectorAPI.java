package com.andrei1058.bedwars.teamselector.api;

import com.andrei1058.bedwars.arena.BedWarsTeam;
import org.bukkit.entity.Player;

public interface TeamSelectorAPI {

    /**
     * Get player's selected team
     *
     * @since API 1
     */
    BedWarsTeam getSelectedTeam(Player player);


    /** Get api version*/
    int getApiVersion();
}

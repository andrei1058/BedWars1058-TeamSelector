package com.andrei1058.bedwars.teamselector.api;

import com.andrei1058.bedwars.arena.Arena;
import com.andrei1058.bedwars.arena.BedWarsTeam;
import org.bukkit.entity.Player;

public class TeamSelector implements TeamSelectorAPI {

    @Override
    public BedWarsTeam getSelectedTeam(Player player) {
        Arena a = Arena.getArenaByPlayer(player);
        return a == null ? null : a.getTeam(player);
    }

    @Override
    public int getApiVersion() {
        return 1;
    }
}

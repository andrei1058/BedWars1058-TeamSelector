package com.andrei1058.bedwars.teamselector.api;

import com.andrei1058.bedwars.api.arena.IArena;
import com.andrei1058.bedwars.api.arena.team.ITeam;
import com.andrei1058.bedwars.teamselector.Main;
import org.bukkit.entity.Player;

public class TeamSelector implements TeamSelectorAPI {

    @Override
    public ITeam getSelectedTeam(Player player) {
        IArena a = Main.bw.getArenaUtil().getArenaByPlayer(player);
        return a == null ? null : a.getTeam(player);
    }

    @Override
    public int getApiVersion() {
        return 2;
    }
}

package com.andrei1058.bedwars.teamselector.listeners;

import com.andrei1058.bedwars.Main;
import com.andrei1058.bedwars.api.arena.GameState;
import com.andrei1058.bedwars.api.events.gameplay.GameStateChangeEvent;
import com.andrei1058.bedwars.api.events.gameplay.TeamAssignEvent;
import com.andrei1058.bedwars.api.events.player.PlayerJoinArenaEvent;
import com.andrei1058.bedwars.api.events.player.PlayerLeaveArenaEvent;
import com.andrei1058.bedwars.arena.Arena;
import com.andrei1058.bedwars.arena.BedWarsTeam;
import com.andrei1058.bedwars.teamselector.api.events.TeamSelectorAbortEvent;
import com.andrei1058.bedwars.teamselector.teamselector.TeamSelectorGUI;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class ArenaListener implements Listener {

    @EventHandler
    public void onBwArenaJoin(PlayerJoinArenaEvent e) {
        if (e.isSpectator()) return;
        Arena arena = Arena.getArenaByPlayer(e.getPlayer());
        if (arena == null) return;
        if (arena.getStatus() == GameState.waiting || arena.getStatus() == GameState.starting) {
            Bukkit.getScheduler().runTaskLater(Main.plugin, () -> TeamSelectorGUI.giveItem(e.getPlayer(), null), 10L);
        }
    }

    @EventHandler
    //Remove player from team
    public void onBwArenaLeave(PlayerLeaveArenaEvent e) {
        Arena a = e.getArena();
        if (a.getStatus() == GameState.playing) return;
        if (a.getStatus() == GameState.restarting) return;
        BedWarsTeam t = a.getTeam(e.getPlayer());
        if (t == null) return;
        TeamSelectorGUI.removePlayerFromTeam(e.getPlayer(), t);
        Bukkit.getPluginManager().callEvent(new TeamSelectorAbortEvent(e.getPlayer()));
    }

    @EventHandler
    public void onAssign(TeamAssignEvent e) {
        if (e.isCancelled()) return;

        //cancel if player have team
        if (e.getArena().getTeam(e.getPlayer()) != null) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onStatusChange(GameStateChangeEvent e) {
        if (e.getState() == GameState.starting) {
            int size = e.getArena().getPlayers().size();
            int teams = 0;
            int members = 0;
            for (BedWarsTeam t : e.getArena().getTeams()) {
                if (t.getMembers().isEmpty()) continue;
                teams++;
                members += t.getMembers().size();
            }
            if (size - members <= 0 && teams == 1) {
                e.getArena().setStatus(GameState.waiting);
            }
        }
    }
}

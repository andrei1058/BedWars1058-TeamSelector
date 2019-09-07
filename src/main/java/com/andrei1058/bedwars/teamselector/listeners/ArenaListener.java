package com.andrei1058.bedwars.teamselector.listeners;

import com.andrei1058.bedwars.api.arena.GameState;
import com.andrei1058.bedwars.api.arena.IArena;
import com.andrei1058.bedwars.api.arena.team.ITeam;
import com.andrei1058.bedwars.api.events.gameplay.GameStateChangeEvent;
import com.andrei1058.bedwars.api.events.gameplay.TeamAssignEvent;
import com.andrei1058.bedwars.api.events.player.PlayerJoinArenaEvent;
import com.andrei1058.bedwars.api.events.player.PlayerLeaveArenaEvent;
import com.andrei1058.bedwars.teamselector.Main;
import com.andrei1058.bedwars.teamselector.api.events.TeamSelectorAbortEvent;
import com.andrei1058.bedwars.teamselector.teamselector.TeamSelectorGUI;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class ArenaListener implements Listener {

    @EventHandler
    public void onBwArenaJoin(PlayerJoinArenaEvent e) {
        if (e.isCancelled()) return;
        if (e.isSpectator()) return;
        if (e.getArena() == null) return;
        if (e.getArena().getStatus() == GameState.waiting || e.getArena().getStatus() == GameState.starting) {
            Bukkit.getScheduler().runTaskLater(Main.plugin, () -> TeamSelectorGUI.giveItem(e.getPlayer(), null), 30L);
        }
    }

    @EventHandler
    //Remove player from team
    public void onBwArenaLeave(PlayerLeaveArenaEvent e) {
        IArena a = e.getArena();
        if (a.getStatus() == GameState.playing) return;
        if (a.getStatus() == GameState.restarting) return;
        ITeam t = a.getTeam(e.getPlayer());
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
        if (e.getNewState() == GameState.starting) {
            int size = e.getArena().getPlayers().size();
            int teams = 0;
            int members = 0;
            for (ITeam t : e.getArena().getTeams()) {
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

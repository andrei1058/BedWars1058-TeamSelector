package com.andrei1058.bedwars.teamselector.teamselector;

import com.andrei1058.bedwars.api.arena.IArena;
import com.andrei1058.bedwars.api.arena.team.ITeam;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TeamManager {

    private static TeamManager INSTANCE;

    private final HashMap<IArena, ArenaPreferences> cachedPreferences = new HashMap<>();

    private TeamManager() {

    }

    public static TeamManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new TeamManager();
        }
        return INSTANCE;
    }

    /**
     * Clear team preferences for an arena.
     */
    public void clearArenaCache(IArena arena) {
        cachedPreferences.remove(arena);
    }

    /**
     * Handle player quit.
     */
    public void onQuit(IArena arena, Player player) {
        ArenaPreferences preferences = getArena(arena);
        if (preferences != null) {
            preferences.removePlayer(player);
        }
    }

    public ITeam getPlayerTeam(Player player, IArena arena) {
        ArenaPreferences pref = getArena(arena);
        if (pref != null) {
            return pref.getTeam(player);
        }
        return null;
    }

    public int getPlayersCount(ITeam team, IArena arena) {
        ArenaPreferences preferences = getArena(arena);
        if (preferences != null) {
            return preferences.getPlayersCount(team);
        }
        return 0;
    }

    public List<Player> getMembers(ITeam team, IArena arena) {
        ArenaPreferences preferences = getArena(arena);
        if (preferences != null) {
            return preferences.getMembers(team);
        }
        return new ArrayList<>();
    }

    public void setPlayerTeam(Player player, IArena arena, ITeam team) {
        ArenaPreferences preferences = getArena(arena);
        if (preferences != null) {
            preferences.setPlayerTeam(player, team);
        }
    }

    public ArenaPreferences getArena(IArena arena) {
        return cachedPreferences.getOrDefault(arena, cachedPreferences.putIfAbsent(arena, new ArenaPreferences(arena)));
    }
}

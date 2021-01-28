package com.andrei1058.bedwars.teamselector.teamselector;

import com.andrei1058.bedwars.api.arena.IArena;
import com.andrei1058.bedwars.api.arena.team.ITeam;
import com.andrei1058.bedwars.api.arena.team.ITeamAssigner;
import com.andrei1058.bedwars.api.events.gameplay.TeamAssignEvent;
import com.andrei1058.bedwars.teamselector.Main;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

public class TeamSelectorAssigner implements ITeamAssigner {

    private final LinkedList<PlayerGroup> playerGroups = new LinkedList<>();
    private final List<UUID> skippedFromPartyCheck = new ArrayList<>();
    private final List<UUID> playersAddedToATeam = new ArrayList<>();
    private final LinkedList<ITeam> teams = new LinkedList<>();

    @Override
    public void assignTeams(IArena arena) {
        teams.addAll(arena.getTeams());

        // create groups from parties
        for (Player player : arena.getPlayers()) {
            // check if is party owner or someone with a big rank to identify a party
            if (!skippedFromPartyCheck.contains(player.getUniqueId()) && Main.bw.getPartyUtil().isOwner(player)) {
                // all party members will be added to a skip list in case there are more members with
                // a rank that allow them to join games.
                // To be more explicit #isOwner may return true for more than a player.
                Queue<Player> partyMembers = new LinkedList<>();
                for (Player inParty : Main.bw.getPartyUtil().getMembers(player)) {
                    // if partied player is not engaged in another game or in lobby
                    if (arena.isPlayer(inParty)) {
                        partyMembers.add(inParty);
                        skippedFromPartyCheck.add(inParty.getUniqueId());
                    }
                }
                // some parties do not include the party owner in the members list
                // so we check if he was included
                if (!partyMembers.contains(player)) {
                    partyMembers.add(player);
                    skippedFromPartyCheck.add(player.getUniqueId());
                }

                // if is alone skip step
                if (partyMembers.size() < 2) continue;

                // cache parties in new groups of players and split parties bigger than max in team
                PlayerGroup playerGroup = null;
                do {
                    // on first occurrence or if the group is filled
                    if (playerGroup == null || playerGroup.getMembers().size() == arena.getMaxInTeam()) {
                        playerGroup = new PlayerGroup(arena, null);
                        playerGroups.add(playerGroup);
                    }
                    // current player
                    Player toBeAdded = partyMembers.poll();
                    playerGroup.addPlayer(toBeAdded);
                } while (!partyMembers.isEmpty());
            }
        }

        // create groups from registered preferences
        ArenaPreferences registeredPreference = TeamManager.getInstance().getArena(arena);
        for (ITeam preference : registeredPreference.getSelections().values().stream().distinct().collect(Collectors.toList())) {
            PlayerGroup playerGroup = new PlayerGroup(arena, preference);
            for (Player teamSelector : registeredPreference.getMembers(preference)) {
                // players amount in that case cannot be bigger than max in team so we don't have to split anything
                playerGroup.addPlayer(teamSelector);
            }
            playerGroups.add(playerGroup);
        }

        // order player groups
        Collections.sort(playerGroups);

        // order final teams by less selected to synchronize with PlayerGroup comparator
        if (!registeredPreference.getSelections().isEmpty()) {
            teams.sort(Comparator.comparingInt(o -> registeredPreference.getMembers(o).size()));
        }

        // assign groups to teams
        for (PlayerGroup playerGroup : playerGroups) {
            if (playerGroup.getMembers().isEmpty()) continue;
            if (playerGroup.getPreference() == null) {
                ITeam targetTeam = null;
                for (ITeam team : teams) {
                    if (arena.getMaxInTeam() - team.getMembers().size() >= playerGroup.getMembers().size()) {
                        targetTeam = team;
                        break;
                    }
                }
                if (targetTeam != null) {
                    for (Player player : playerGroup.getMembers()) {
                        targetTeam.addPlayers(player);
                        TeamAssignEvent teamAssignEvent = new TeamAssignEvent(player, targetTeam, arena);
                        Bukkit.getPluginManager().callEvent(teamAssignEvent);
                        playersAddedToATeam.add(player.getUniqueId());
                        debug(player, "Added to team: " + targetTeam.getName() + " on " + arena.getWorldName());
                    }
                    // if team is filled
                    // make team unavailable
                    if (targetTeam.getMembers().size() == arena.getMaxInTeam()) {
                        teams.remove(targetTeam);
                    }
                }
            } else {
                // priority on preferences that can fill an entire team.
                // same code is used on low priority and we check if there is still space on a possible compromised preference
                // assign players to target team
                for (Player player : playerGroup.getMembers()) {
                    if (playerGroup.getPreference().getMembers().size() < arena.getMaxInTeam()) {
                        playerGroup.getPreference().addPlayers(player);
                        TeamAssignEvent teamAssignEvent = new TeamAssignEvent(player, playerGroup.getPreference(), arena);
                        Bukkit.getPluginManager().callEvent(teamAssignEvent);
                        playersAddedToATeam.add(player.getUniqueId());
                        debug(player, "Added to team: " + playerGroup.getPreference().getName() + " on " + arena.getWorldName());
                    }
                }
                // make team unavailable
                if (playerGroup.getPreference().getMembers().size() == arena.getMaxInTeam()) {
                    teams.remove(playerGroup.getPreference());
                }
            }
        }

        // assign remaining players to a team
        for (Player player : arena.getPlayers()) {
            if (!playersAddedToATeam.contains(player.getUniqueId())) {
                boolean added = false;
                for (ITeam team : teams) {
                    if (team.getMembers().size() < arena.getMaxInTeam()) {
                        team.addPlayers(player);
                        debug(player, "Added to team: " + team.getName() + " on " + arena.getWorldName());
                        added = true;
                        break;
                    }
                }
                if (!added) {
                    // if he hasn't been added to a team kick
                    debug(player, "That was unexpected: you haven't been assigned to a team!");
                    player.kickPlayer("That was unexpected: you haven't been assigned to a team!");
                }
            }
        }

        playerGroups.clear();
        skippedFromPartyCheck.clear();
        playersAddedToATeam.clear();
        teams.clear();
    }

    public static void debug(Player player, String message) {
        Main.plugin.getLogger().warning("NTS-DEBUG: " + message + (player == null ? "" : " - " + player.getName() + "."));
    }
}

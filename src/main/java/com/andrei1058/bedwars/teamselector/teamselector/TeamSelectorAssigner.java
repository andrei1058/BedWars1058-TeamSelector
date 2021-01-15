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

    private static final List<UUID> skipped = new ArrayList<>();

    @Override
    public void assignTeams(IArena arena) {

        ArenaPreferences preferences = TeamManager.getInstance().getArena(arena);

        // list of selected teams used later to filter unselected teams
        LinkedList<ITeam> selectedTeams = preferences.getSelections().values().stream().distinct().collect(Collectors.toCollection(LinkedList::new));

        // prioritize players who filled a team by team-selector
        for (ITeam team : new ArrayList<>(selectedTeams)) {
            List<Player> selectors = preferences.getMembers(team);
            if (selectors.size() == arena.getMaxInTeam()) {
                for (Player selector : selectors) {
                    TeamAssignEvent e = new TeamAssignEvent(selector, team, arena);
                    Bukkit.getPluginManager().callEvent(e);
                    if (!e.isCancelled()) {
                        team.addPlayers(selector);
                        skipped.add(selector.getUniqueId());
                    }
                }
                // check if team is still full because team assign event can be cancelled
                if (team.getMembers().size() == arena.getMaxInTeam()) {
                    selectedTeams.remove(team);
                }
            }
        }

        // order teams by less selected
        LinkedList<ITeam> lessSelectedTeams = new LinkedList<>();
        // cache unselected teams first
        for (ITeam team : arena.getTeams()) {
            if (team.getMembers().size() < arena.getMaxInTeam() && !selectedTeams.contains(team)) {
                lessSelectedTeams.add(team);
            }
        }
        // less selected first
        selectedTeams.sort(Comparator.comparingInt(o -> preferences.getMembers(o).size()));
        // keep teams in a single place ordered by less used first
        lessSelectedTeams.addAll(selectedTeams);


        // add here filtered members because #isOwner in some cases will return
        // true if the given user can chose arenas so it may create duplications if there
        // are more users with chose arena permission
        List<UUID> preventDuplication = new ArrayList<>();

        // list what members need to be added
        List<List<Player>> parties = new ArrayList<>();
        for (Player inGame : arena.getPlayers()) {
            if (preventDuplication.contains(inGame.getUniqueId())) {
                continue;
            }
            if (Main.bw.getPartyUtil().isOwner(inGame)) {
                List<Player> partyMembers = Main.bw.getPartyUtil().getMembers(inGame);
                if (!partyMembers.isEmpty()) {
                    List<Player> filteredMembers = new ArrayList<>();
                    for (Player member : partyMembers) {
                        // check if party member is in this arena
                        if (arena.isPlayer(member)) {
                            filteredMembers.add(member);
                            preventDuplication.add(member.getUniqueId());
                        }
                    }
                    if (!filteredMembers.isEmpty()) {
                        // add the possible party owner to the list
                        // because some party adapters may not include the owner in the members list
                        if (!filteredMembers.contains(inGame)) {
                            filteredMembers.add(inGame);
                            preventDuplication.add(inGame.getUniqueId());
                        }
                        parties.add(filteredMembers);
                    }
                }
            }
        }

        // team-up parties - make full teams first
        for (List<Player> party : parties) {
            if (party.size() >= arena.getMaxInTeam() && lessSelectedTeams.get(0).getMembers().isEmpty()) {
                ITeam team = lessSelectedTeams.get(0);
                for (int i = 0; i < party.size() && team.getMembers().size() < arena.getMaxInTeam(); i++) {
                    Player member = party.remove(0);
                    TeamAssignEvent e = new TeamAssignEvent(member, team, arena);
                    Bukkit.getPluginManager().callEvent(e);
                    if (!e.isCancelled()) {
                        team.addPlayers(member);
                        skipped.add(member.getUniqueId());
                    }
                }
                // check if team is still full because team assign event can be cancelled
                if (team.getMembers().size() >= arena.getMaxInTeam()) {
                    lessSelectedTeams.remove(team);
                }
            }
        }

        // sort parties by bigger first

        // team up remaining players from parties
        parties.sort(Comparator.comparingInt(List::size));
        while (parties.size() > 0 && parties.get(0).size() > 1) {
            parties.sort(Comparator.comparingInt(List::size));
            List<Player> party = parties.get(0);
            // if remained one player treat like a regular player
            if (party.size() > 1) {
                // check if the player amount who booked that team is greater and BREAK
                ITeam team = lessSelectedTeams.get(0);
                // if players who selected that team are more than the remaining party size
                if (preferences.getMembers(team).size() < party.size()) {
                    for (int i = 0; i < party.size() && team.getMembers().size() < arena.getMaxInTeam(); i++) {
                        Player member = party.remove(0);
                        TeamAssignEvent e = new TeamAssignEvent(member, team, arena);
                        Bukkit.getPluginManager().callEvent(e);
                        if (!e.isCancelled()) {
                            team.addPlayers(member);
                            skipped.add(member.getUniqueId());
                        }
                    }
                    // check if team is still full because team assign event can be cancelled
                    if (team.getMembers().size() >= arena.getMaxInTeam()) {
                        lessSelectedTeams.remove(team);
                    }
                } else {
                    // no more attempts required because parties are ordered and teams list as well
                    break;
                }
            }
        }

        // give team preferences if possible
        for (Map.Entry<Player, ITeam> entry : preferences.getSelections().entrySet()) {
            if (!skipped.contains(entry.getKey().getUniqueId())) {
                if (entry.getValue().getMembers().size() < arena.getMaxInTeam()) {
                    TeamAssignEvent e = new TeamAssignEvent(entry.getKey(), entry.getValue(), arena);
                    Bukkit.getPluginManager().callEvent(e);
                    if (!e.isCancelled()) {
                        entry.getValue().addPlayers(entry.getKey());
                        skipped.add(entry.getKey().getUniqueId());
                    }
                }
            }
        }

        // assign remaining players a team
        // I guess this part should implement balance-teams
        for (Player player : arena.getPlayers()) {
            if (!skipped.contains(player.getUniqueId())) {
                for (ITeam team : arena.getTeams()) {
                    if (team.getMembers().size() < arena.getMaxInTeam()) {
                        TeamAssignEvent e = new TeamAssignEvent(player, team, arena);
                        Bukkit.getPluginManager().callEvent(e);
                        if (!e.isCancelled()) {
                            team.addPlayers(player);
                        }
                        break;
                    }
                }
            }
        }
    }
}

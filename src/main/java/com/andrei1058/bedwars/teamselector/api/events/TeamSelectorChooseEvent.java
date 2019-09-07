package com.andrei1058.bedwars.teamselector.api.events;

import com.andrei1058.bedwars.api.arena.IArena;
import com.andrei1058.bedwars.api.arena.team.ITeam;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class TeamSelectorChooseEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();


    private Player player;
    private IArena arena;
    private ITeam chosen, oldChoice;
    private boolean cancelled = false;

    /**
     * Called when a Player joins a team via team selector
     */
    public TeamSelectorChooseEvent(Player player, IArena arena, ITeam chosen, ITeam oldChoice) {
        this.player = player;
        this.arena = arena;
        this.chosen = chosen;
        this.oldChoice = oldChoice;
    }

    /**
     * Get player
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * Get arena
     */
    public IArena getArena() {
        return arena;
    }

    /**
     * Get chosen team
     */
    public ITeam getChosen() {
        return chosen;
    }

    /**
     * Get old choice.
     *
     * @return null if it's the first player's choice
     */
    public ITeam getOldChoice() {
        return oldChoice;
    }

    /**
     * Check if event is canceled
     */
    public boolean isCancelled() {
        return cancelled;
    }

    /**
     * If you cancel it and the player's having a team, it will remain in it
     */
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}

package com.andrei1058.bedwars.teamselector.api.events;

import com.andrei1058.bedwars.arena.Arena;
import com.andrei1058.bedwars.arena.BedWarsTeam;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class TeamSelectorChooseEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();


    private Player player;
    private Arena arena;
    private BedWarsTeam chosen, oldChoice;
    private boolean cancelled = false;

    /**
     * Called when a Player joins a team via team selector
     *
     * @since API 1
     */
    public TeamSelectorChooseEvent(Player player, Arena arena, BedWarsTeam chosen, BedWarsTeam oldChoice) {
        this.player = player;
        this.arena = arena;
        this.chosen = chosen;
        this.oldChoice = oldChoice;
    }

    /**
     * Get player
     *
     * @since API 1
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * Get arena
     *
     * @since API 1
     */
    public Arena getArena() {
        return arena;
    }

    /**
     * Get chosen team
     *
     * @since API 1
     */
    public BedWarsTeam getChosen() {
        return chosen;
    }

    /**
     * Get old choice.
     *
     * @return null if it's the first player's choice
     * @since API 1
     */
    public BedWarsTeam getOldChoice() {
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
     *
     * @since API 1
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

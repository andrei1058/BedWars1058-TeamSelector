package com.andrei1058.bedwars.teamselector.api.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class TeamSelectorAbortEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();

    private final Player player;

    /**
     * This is called when a player leaves the arena and his data is removed from team selector
     *
     * @since API 1
     */
    public TeamSelectorAbortEvent(Player player) {
        this.player = player;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    /**
     * Get player
     *
     * @since API 1
     */
    public Player getPlayer() {
        return player;
    }

    public HandlerList getHandlers() {
        return HANDLERS;
    }
}

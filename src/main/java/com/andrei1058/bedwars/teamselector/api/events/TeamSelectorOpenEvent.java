package com.andrei1058.bedwars.teamselector.api.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class TeamSelectorOpenEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();

    private final Player player;
    private boolean cancelled = false;

    /**
     * Called when a player opens the Team Selector GUI
     */
    public TeamSelectorOpenEvent(Player p) {
        this.player = p;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    /**
     * Get the player
     *
     * @since API 1
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * Check if cancelled
     *
     * @since API 1
     */
    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public HandlerList getHandlers() {
        return HANDLERS;
    }
}

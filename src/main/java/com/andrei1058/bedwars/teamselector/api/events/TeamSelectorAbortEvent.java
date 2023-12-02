package com.andrei1058.bedwars.teamselector.api.events;

import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
@Getter
public class TeamSelectorAbortEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();

    /**
     * -- GETTER --
     *  Get player
     *
     */
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

    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }
}

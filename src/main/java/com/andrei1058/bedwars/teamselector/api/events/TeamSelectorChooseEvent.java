package com.andrei1058.bedwars.teamselector.api.events;

import com.andrei1058.bedwars.api.arena.IArena;
import com.andrei1058.bedwars.api.arena.team.ITeam;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
@Getter
public class TeamSelectorChooseEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();


    /**
     * -- GETTER --
     *  Get player
     */
    private final Player player;
    /**
     * -- GETTER --
     *  Get arena
     */
    private final IArena arena;
    /**
     * -- GETTER --
     *  Get chosen team
     */
    private final ITeam chosen;
    /**
     * -- GETTER --
     *  Get old choice. NUll if it is the first player's choice.
     *
     */
    private final ITeam oldChoice;
    /**
     * -- GETTER --
     *  Check if event is canceled
     */
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

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    /**
     * If you cancel it and the player's having a team, it will remain in it
     */
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }
}

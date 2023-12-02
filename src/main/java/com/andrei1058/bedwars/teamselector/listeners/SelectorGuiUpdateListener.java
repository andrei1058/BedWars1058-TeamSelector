package com.andrei1058.bedwars.teamselector.listeners;

import com.andrei1058.bedwars.teamselector.api.events.TeamSelectorAbortEvent;
import com.andrei1058.bedwars.teamselector.api.events.TeamSelectorChooseEvent;
import com.andrei1058.bedwars.teamselector.teamselector.TeamSelectorGUI;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

public class SelectorGuiUpdateListener implements Listener {

    @EventHandler
    public void onTeamJoin(@NotNull TeamSelectorChooseEvent e) {
        if (e.isCancelled()) return;
        TeamSelectorGUI.updateGUIs();
    }

    @EventHandler
    public void onAbort(TeamSelectorAbortEvent e) {
        TeamSelectorGUI.updateGUIs();
    }
}

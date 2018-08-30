package com.andrei1058.bedwars.teamselector.listeners;

import com.andrei1058.bedwars.teamselector.api.events.TeamSelectorAbortEvent;
import com.andrei1058.bedwars.teamselector.api.events.TeamSelectorChooseEvent;
import com.andrei1058.bedwars.teamselector.teamselector.TeamSelectorGUI;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class SelectorGuiUpdateListener implements Listener {

    @EventHandler
    public void onTeamJoin(TeamSelectorChooseEvent e){
        if (e.isCancelled()) return;
        TeamSelectorGUI.updateGUIs();
    }

    @EventHandler
    public void onAbort(TeamSelectorAbortEvent e){
        TeamSelectorGUI.updateGUIs();
    }
}

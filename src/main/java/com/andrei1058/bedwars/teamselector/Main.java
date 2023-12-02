package com.andrei1058.bedwars.teamselector;

import com.andrei1058.bedwars.api.BedWars;
import com.andrei1058.bedwars.teamselector.api.TeamSelector;
import com.andrei1058.bedwars.teamselector.api.TeamSelectorAPI;
import com.andrei1058.bedwars.teamselector.configuration.Config;
import com.andrei1058.bedwars.teamselector.configuration.Messages;
import com.andrei1058.bedwars.teamselector.listeners.ArenaListener;
import com.andrei1058.bedwars.teamselector.listeners.InventoryListener;
import com.andrei1058.bedwars.teamselector.listeners.PlayerInteractListener;
import com.andrei1058.bedwars.teamselector.listeners.SelectorGuiUpdateListener;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.SimplePie;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public class Main extends JavaPlugin {

    public static BedWars bw;
    public static Main plugin;

    /**
     * Register listeners
     */
    private static void registerListeners(Listener @NotNull ... listeners) {
        PluginManager pm = Bukkit.getPluginManager();
        for (Listener l : listeners) {
            pm.registerEvents(l, plugin);
        }
    }

    @Override
    public void onEnable() {
        plugin = this;

        //Disable if plugin not found
        if (Bukkit.getPluginManager().getPlugin("BedWars1058") == null) {
            getLogger().severe("BedWars1058 was not found. Disabling...");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        var registration = Bukkit.getServicesManager().getRegistration(BedWars.class);
        if (null == registration) {
            getLogger().severe("Cannot hook into BedWars1058.");
            Bukkit.getPluginManager().disablePlugin(plugin);
            return;
        }


        bw = registration.getProvider();
        getLogger().info("Hooked into BedWars1058!");

        // register team selector API
        Bukkit.getServicesManager().register(TeamSelectorAPI.class, new TeamSelector(), this, ServicePriority.Normal);


        //Create configuration
        Config.addDefaultConfig();

        //Save default messages
        Messages.setupMessages();

        //Register listeners
        registerListeners(new ArenaListener(), new InventoryListener(), new PlayerInteractListener(), new SelectorGuiUpdateListener());

        // bStats
        Metrics metrics = new Metrics(this, 9091);
        metrics.addCustomChart(new SimplePie("selector_slot", () -> String.valueOf(Config.config.getInt(Config.SELECTOR_SLOT))));
        metrics.addCustomChart(new SimplePie("allot_team_change", () -> String.valueOf(Config.config.getBoolean(Config.ALLOW_TEAM_CHANGE))));
        metrics.addCustomChart(new SimplePie("balance_teams", () -> String.valueOf(Config.config.getBoolean(Config.BALANCE_TEAMS))));
        metrics.addCustomChart(new SimplePie("balance_teams", () -> String.valueOf(Config.config.getBoolean(Config.BALANCE_TEAMS))));
    }
}

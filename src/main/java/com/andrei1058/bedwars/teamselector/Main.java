package com.andrei1058.bedwars.teamselector;

import com.andrei1058.bedwars.api.BedWars;
import com.andrei1058.bedwars.api.GameAPI;
import com.andrei1058.bedwars.teamselector.configuration.Config;
import com.andrei1058.bedwars.teamselector.configuration.Messages;
import com.andrei1058.bedwars.teamselector.listeners.ArenaListener;
import com.andrei1058.bedwars.teamselector.listeners.InventoryListener;
import com.andrei1058.bedwars.teamselector.listeners.PlayerInteractListener;
import com.andrei1058.bedwars.teamselector.listeners.SelectorGuiUpdateListener;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

    public static BedWars bw;
    public static Main plugin;

    @Override
    public void onEnable() {

        //Disable if pl not found
        if (Bukkit.getPluginManager().getPlugin("BedWars1058") == null) {
            getLogger().severe("BedWars1058 was not found. Disabling...");
            setEnabled(false);
            return;
        }

        //Get api
        try {
            bw = (BedWars) Bukkit.getServicesManager().getRegistration(GameAPI.class).getProvider();
        } catch (Exception ex) {
            getLogger().severe("Can't hook into BedWars1058.");
            ex.printStackTrace();
            setEnabled(false);
            return;
        }

        //Check api level
        if (bw.getApiVersion() < 11) {
            getLogger().severe("Your BedWars1058 version is outdated. I need API version 10 or higher!");
            setEnabled(false);
            return;
        }

        getLogger().info("Hook into BedWars1058!");

        plugin = this;

        //Create configuration
        Config.addDefaultConfig();

        //Save default messages
        Messages.setupMessages();

        //Register listeners
        registerListeners(new ArenaListener(), new InventoryListener(), new PlayerInteractListener(), new SelectorGuiUpdateListener());
    }

    /**
     * Register listeners
     *
     * @since API 1
     */
    public static void registerListeners(Listener... listeners) {
        PluginManager pm = Bukkit.getPluginManager();
        for (Listener l : listeners) {
            pm.registerEvents(l, plugin);
        }
    }

    public static String getForCurrentVersion(String v18, String v12, String v13) {
        switch (com.andrei1058.bedwars.Main.getServerVersion()) {
            case "v1_12_R1":
                return v12;
            case "v1_13_R1":
            case "v1_13_R2":
                return v13;
        }
        return v18;
    }


}

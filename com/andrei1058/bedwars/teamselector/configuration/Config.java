package com.andrei1058.bedwars.teamselector.configuration;

import com.andrei1058.bedwars.configuration.ConfigManager;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class Config {

    public static ConfigManager config;

    /**
     * Setup default config
     */
    public static void addDefaultConfig() {
        File dir = new File("plugins/BedWars1058/Addons/TeamSelector");
        if (dir.exists()) {
            dir.mkdir();
        }
        config = new ConfigManager("config", "plugins/BedWars1058/Addons/TeamSelector", false);
        YamlConfiguration yml = config.getYml();
        yml.options().header("Team Selector Add-on for BedWars1058 Mini-game.\n\nDocumentation:\n" +
                SELECTOR_ITEM_STACK_MATERIAL + ": WOOL - The material you want the team-selector item be.\n" +
                SELECTOR_SLOT + ": 0 - The slot where to put the item. Set it to -1 to assign the first empty slot.\n" +
                GIVE_SELECTOR_SELECTED_TEAM_COLOR + ": true - True if you the selector to have the selected team's color.\n" +
                ALLOW_TEAM_CHANGE + ": true - True if you want to allow players to change selected team.\n" +
                ALLOW_MOVE_TROUGH_INVENTORY + ": false - True if you want to allow players to move it in inventory.\n" +
                BALANCE_TEAMS+ ": true - True if you want to have balanced teams size.");

        yml.addDefault(SELECTOR_ITEM_STACK_MATERIAL, "WOOL");
        yml.addDefault(SELECTOR_SLOT, 4);
        yml.addDefault(GIVE_SELECTOR_SELECTED_TEAM_COLOR, true);
        yml.addDefault(ALLOW_TEAM_CHANGE, true);
        yml.addDefault(ALLOW_MOVE_TROUGH_INVENTORY, false);
        yml.addDefault(BALANCE_TEAMS, true);
        yml.options().copyDefaults(true);
        config.save();
    }

    public static final String SELECTOR_ITEM_STACK_MATERIAL = "team-selector-item-stack";
    public static final String SELECTOR_SLOT = "team-selector-slot";
    public static final String GIVE_SELECTOR_SELECTED_TEAM_COLOR = "give-team-color";
    public static final String ALLOW_TEAM_CHANGE = "allow-team-change";
    public static final String ALLOW_MOVE_TROUGH_INVENTORY = "allow-move-in-inventory";
    public static final String BALANCE_TEAMS = "balance-teams";
}

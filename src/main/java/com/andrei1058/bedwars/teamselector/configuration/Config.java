package com.andrei1058.bedwars.teamselector.configuration;

import com.andrei1058.bedwars.api.configuration.ConfigManager;
import com.andrei1058.bedwars.teamselector.Main;
import org.bukkit.Sound;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.logging.Level;

public class Config {

    private Config(){
        //Private constructor to prevent class instantiation
    }

    public static final String SELECTOR_ITEM_STACK_MATERIAL = "team-selector-item-stack";
    public static final String SELECTOR_SLOT = "team-selector-slot";
    public static final String GIVE_SELECTOR_SELECTED_TEAM_COLOR = "give-team-color";
    public static final String ALLOW_TEAM_CHANGE = "allow-team-change";
    public static final String ALLOW_MOVE_TROUGH_INVENTORY = "allow-move-in-inventory";
    public static final String BALANCE_TEAMS = "balance-teams";
    public static final String GUI_OPEN_SOUND = "gui-open-sound";
    public static final String SUCCESS_SOUND = "success-sound";
    public static final String ERROR_SOUND = "error-sound";
    public static ConfigManager config;

    /**
     * Setup default config
     */
    public static void addDefaultConfig() {

        //noinspection ResultOfMethodCallIgnored
        new File("plugins/BedWars1058/Addons/TeamSelector").mkdirs();

        config = new ConfigManager(Main.plugin, "config", "plugins/BedWars1058/Addons/TeamSelector");
        YamlConfiguration yml = config.getYml();
        yml.options().header("Team Selector Add-on for BedWars1058 Mini-game.\n\nDocumentation:\n" +
                SELECTOR_ITEM_STACK_MATERIAL + ": WOOL - The material you want the team-selector item be.\n" +
                SELECTOR_SLOT + ": 0 - The slot where to put the item. Set it to -1 to assign the first empty slot.\n" +
                GIVE_SELECTOR_SELECTED_TEAM_COLOR + ": true - True if you the selector to have the selected team's color.\n" +
                ALLOW_TEAM_CHANGE + ": true - True if you want to allow players to change selected team.\n" +
                ALLOW_MOVE_TROUGH_INVENTORY + ": false - True if you want to allow players to move it in inventory.\n" +
                BALANCE_TEAMS + ": true - True if you want to have balanced teams size.\n" +
                GUI_OPEN_SOUND + ": BLOCK_SHULKER_BOX_OPEN - The sound to be played when you open the team selector.\n" +
                SUCCESS_SOUND + ": BLOCK_SHULKER_BOX_CLOSE - The sound to be played when you select a team successfully.\n" +
                ERROR_SOUND + ": BLOCK_ANVIL_DESTROY - The sound to be played when you can't select a team.\n" +
                "Sounds for latest version: https://hub.spigotmc.org/javadocs/spigot/org/bukkit/Sound.html\n" +
                "Sounds for 1.8 or older: https://docs.codelanx.com/Bukkit/1.8/org/bukkit/Sound.html");

        yml.addDefault(SELECTOR_ITEM_STACK_MATERIAL, Main.bw.getForCurrentVersion("WOOL", "WOOL", "WHITE_WOOL"));
        yml.addDefault(SELECTOR_SLOT, 4);
        yml.addDefault(GIVE_SELECTOR_SELECTED_TEAM_COLOR, true);
        yml.addDefault(ALLOW_TEAM_CHANGE, true);
        yml.addDefault(ALLOW_MOVE_TROUGH_INVENTORY, false);
        yml.addDefault(BALANCE_TEAMS, true);
        yml.addDefault(GUI_OPEN_SOUND, Main.bw.getForCurrentVersion("CHEST_OPEN", "CHEST_OPEN", "BLOCK_SHULKER_BOX_OPEN"));
        yml.addDefault(SUCCESS_SOUND, Main.bw.getForCurrentVersion("CHEST_CLOSE", "CHEST_CLOSE", "BLOCK_SHULKER_BOX_CLOSE"));
        yml.addDefault(ERROR_SOUND, Main.bw.getForCurrentVersion("ANVIL_BREAK", "ANVIL_BREAK", "BLOCK_ANVIL_DESTROY"));
        yml.options().copyDefaults(true);
        config.save();
    }

    /**
     * Play sound
     */
    public static void playSound(Player player, String path) {
        if (player == null) return;
        String sound = Config.config.getString(path);
        try {
            player.playSound(player.getLocation(), Sound.valueOf(sound), 1f, 1f);
        } catch (Exception ex) {
            sendSoundLog(sound);
        }
    }

    private static void sendSoundLog(String sound) {
        Main.plugin.getLogger().log(Level.SEVERE, sound + " it's not a valid sound for your server version!");
    }
}

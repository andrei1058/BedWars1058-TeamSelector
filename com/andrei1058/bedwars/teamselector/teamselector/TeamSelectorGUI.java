package com.andrei1058.bedwars.teamselector.teamselector;

import com.andrei1058.bedwars.Main;
import com.andrei1058.bedwars.api.GameState;
import com.andrei1058.bedwars.api.TeamColor;
import com.andrei1058.bedwars.arena.Arena;
import com.andrei1058.bedwars.arena.BedWarsTeam;
import com.andrei1058.bedwars.configuration.Language;
import com.andrei1058.bedwars.teamselector.api.events.TeamSelectorChooseEvent;
import com.andrei1058.bedwars.teamselector.api.events.TeamSelectorOpenEvent;
import com.andrei1058.bedwars.teamselector.configuration.Config;
import com.andrei1058.bedwars.teamselector.configuration.Messages;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TeamSelectorGUI {

    //Players with Team Selector GUI opened
    public static ArrayList<UUID> openGUIs = new ArrayList<>();

    //Gui opener identifier
    public static final String TEAM_SELECTOR_IDENTIFIER = "BWTEAMSELECTOR";
    public static final String TEAM_JOIN_IDENTIFIER = "BWJOIN_";

    public static void openGUI(Player player, boolean update) {
        //Check if arena isn't started yet
        Arena arena = Arena.getArenaByPlayer(player);
        if (arena == null) return;
        if (arena.getStatus() == GameState.playing) return;
        if (arena.getStatus() == GameState.restarting) return;

        //Call related event
        if (!update) {
            TeamSelectorOpenEvent e = new TeamSelectorOpenEvent(player);
            Bukkit.getPluginManager().callEvent(e);
            if (e.isCancelled()) return;

            openGUIs.add(player.getUniqueId());
        }

        //Create gui
        int size = arena.getTeams().size();
        int layer1_stop_slot = 16;
        int layer2_stop_slot = 25;
        if (size > 7 && size <= 14) {
            size = 27;
        } else {
            size = 36;
        }
        int items_per_layer = arena.getTeams().size() / 2;
        int start_slot = 16 - items_per_layer - ((6 - items_per_layer) / 2);

        Inventory inv;
        if (update) {
            inv = player.getOpenInventory().getTopInventory();
        } else {
            inv = Bukkit.createInventory(null, size, Language.getMsg(player, Messages.GUI_NAME));
        }
        List<Integer> ocupedSlots = new ArrayList<>();
        for (BedWarsTeam bwt : arena.getTeams()) {

            ItemStack i = new ItemStack(Material.valueOf(Config.config.getString(Config.SELECTOR_ITEM_STACK_MATERIAL)));
            i = Main.nms.colourItem(i, bwt);
            i = Main.nms.addCustomData(i, TEAM_JOIN_IDENTIFIER + bwt.getName());

            ItemMeta im = i.getItemMeta();
            im.setDisplayName(Language.getMsg(player, Messages.CHOICE_NAME).replace("{color}", TeamColor.getChatColor(bwt.getColor()).toString()).replace("{team}", bwt.getName()));
            List<String> lore = new ArrayList<>();
            for (String s : Language.getList(player, Messages.CHOICE_LORE)) {
                lore.add(s.replace("{color}", TeamColor.getChatColor(bwt.getColor()).toString()).replace("{team}", bwt.getName()).replace("{selected}", String.valueOf(bwt.getMembers().size()))
                        .replace("{total}", String.valueOf(arena.getMaxInTeam())));
            }
            im.setLore(lore);
            i.setItemMeta(im);

            inv.setItem(start_slot, i);
            ocupedSlots.add(start_slot);
            start_slot++;
            if (start_slot == layer1_stop_slot && arena.getTeams().size() - 7 > 0) {
                start_slot = 19 - items_per_layer - ((6 - items_per_layer) / 2);
            } else if (start_slot == layer2_stop_slot) {
                break;
            }
        }

        BedWarsTeam selected = arena.getTeam(player);
        for (int x = 0; x < inv.getSize(); x++) {
            if (ocupedSlots.contains(x)) continue;
            if (selected == null) {
                inv.setItem(x, new ItemStack(Material.AIR));
            } else {
                inv.setItem(x, Main.nms.colourItem(new ItemStack(Material.STAINED_GLASS_PANE), selected));
            }
        }

        player.openInventory(inv);
    }

    /**
     * Add a player to a team
     *
     * @return false if cannot add
     * @since API 1
     */
    public static boolean joinTeam(Player player, String teamName) {
        Arena arena = Arena.getArenaByPlayer(player);
        if (arena == null) return false;
        if (arena.getStatus() == GameState.playing) return false;
        if (arena.getStatus() == GameState.restarting) return false;
        BedWarsTeam bwt = arena.getTeam(teamName);
        if (bwt == null) return false;

        if (Main.getParty().hasParty(player)) {
            player.sendMessage(Language.getMsg(player, Messages.PARTY_DENIED));
            return false;
        }

        //Check if team is full
        if (bwt.getSize() == arena.getMaxInTeam()) {
            player.sendMessage(Language.getMsg(player, Messages.TEAM_FULL).replace("{color}", TeamColor.getChatColor(bwt.getColor()).toString()).replace("{team}", bwt.getName()));
            return false;
        }


        //Balance Teams
        for (BedWarsTeam t : arena.getTeams()) {
            if (t == bwt) continue;
            if (t.getMembers().size() < bwt.getMembers().size()) {
                if (Config.config.getBoolean(Config.BALANCE_TEAMS)) {
                    player.sendMessage(Language.getMsg(player, Messages.TEAM_NOT_BALANCED));
                    return true;
                } else if (arena.getStatus() == GameState.starting) {
                    player.sendMessage(Language.getMsg(player, Messages.CANT_JOIN_WHILE_STARTING));
                }
            }
        }

        BedWarsTeam team = arena.getTeam(player);
        //Call event
        TeamSelectorChooseEvent e = new TeamSelectorChooseEvent(player, arena, bwt, team);
        Bukkit.getPluginManager().callEvent(e);

        if (e.isCancelled()) {
            return false;
        }

        //Check if can switch
        if (arena.getTeam(player) != null) {
            if (!Config.config.getBoolean(Config.ALLOW_TEAM_CHANGE)) {
                player.sendMessage(Language.getMsg(player, Messages.SWITCH_DISABLED));
                return false;
            }
            //If allowed remove from team
            removePlayerFromTeam(player, team);
        }

        //Refresh ream selector item
        giveItem(player, bwt);

        //Add to team
        bwt.addPlayers(player);
        player.sendMessage(Language.getMsg(player, Messages.TEAM_JOIN).replace("{color}", TeamColor.getChatColor(bwt.getColor()).toString()).replace("{team}", bwt.getName()).replace("{selected}", String.valueOf(bwt.getMembers().size()))
                .replace("{total}", String.valueOf(arena.getMaxInTeam())));
        return true;
    }

    /**
     * Remove a player from a team added via TeamSelector
     *
     * @since API 1
     */
    public static void removePlayerFromTeam(Player player, BedWarsTeam team) {
        team.getMembers().remove(player);
        if (team.getBedHolo(player) != null) {
            team.getBedHolo(player).destroy();
        }
    }

    /**
     * Update inventories
     *
     * @since API 1
     */
    public static void updateGUIs() {
        for (UUID player : new ArrayList<>(TeamSelectorGUI.openGUIs)) {
            Player p = Bukkit.getPlayer(player);
            if (p == null) continue;
            if (p.getOpenInventory() == null) {
                openGUIs.remove(player);
                continue;
            }
            TeamSelectorGUI.openGUI(p, true);
        }
    }

    /**
     * Give the team selector item-stack
     *
     * @since API 1
     */
    public static void giveItem(Player p, BedWarsTeam team) {
        ItemStack i;
        try {
            i = new ItemStack(Material.valueOf(Config.config.getString(Config.SELECTOR_ITEM_STACK_MATERIAL)));
        } catch (Exception ex) {
            Main.plugin.getLogger().severe("Team-Selector Material is invalid!");
            ex.printStackTrace();
            return;
        }

        if (team != null && Config.config.getBoolean(Config.GIVE_SELECTOR_SELECTED_TEAM_COLOR)){
            i = Main.nms.colourItem(i, team);
        }

        ItemMeta im = i.getItemMeta();
        im.setLore(Language.getList(p, Messages.SELECTOR_LORE));
        im.setDisplayName(Language.getMsg(p, Messages.SELECTOR_NAME));
        i.setItemMeta(im);
        i = com.andrei1058.bedwars.Main.nms.addCustomData(i, TeamSelectorGUI.TEAM_SELECTOR_IDENTIFIER);
        p.getInventory().setItem(Config.config.getInt(Config.SELECTOR_SLOT), i);
    }
}

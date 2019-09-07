package com.andrei1058.bedwars.teamselector.teamselector;

import com.andrei1058.bedwars.api.arena.GameState;
import com.andrei1058.bedwars.api.arena.IArena;
import com.andrei1058.bedwars.api.arena.team.ITeam;
import com.andrei1058.bedwars.api.arena.team.TeamColor;
import com.andrei1058.bedwars.api.language.Language;
import com.andrei1058.bedwars.teamselector.Main;
import com.andrei1058.bedwars.teamselector.api.events.TeamSelectorChooseEvent;
import com.andrei1058.bedwars.teamselector.api.events.TeamSelectorOpenEvent;
import com.andrei1058.bedwars.teamselector.configuration.Config;
import com.andrei1058.bedwars.teamselector.configuration.Messages;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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
        IArena arena = Main.bw.getArenaUtil().getArenaByPlayer(player);
        if (arena == null) return;
        if (arena.getStatus() == GameState.playing) return;
        if (arena.getStatus() == GameState.restarting) return;

        //Call related event
        if (!update) {
            TeamSelectorOpenEvent e = new TeamSelectorOpenEvent(player);
            Bukkit.getPluginManager().callEvent(e);
            if (e.isCancelled()) return;
        }

        //Allowed
        Config.playSound(player, Config.GUI_OPEN_SOUND);
        openGUIs.add(player.getUniqueId());

        //Create gui
        int size;

        int[] layer;

        switch (arena.getTeams().size()) {
            default:
                size = 9;
                layer = new int[]{};
                break;
            case 2:
                layer = new int[]{11, 15};
                size = 27;
                break;
            case 3:
                layer = new int[]{11, 13, 15};
                size = 27;
                break;
            case 4:
                layer = new int[]{10, 12, 14, 16};
                size = 27;
                break;
            case 5:
                layer = new int[]{11, 12, 13, 14, 15};
                size = 27;
                break;
            case 6:
                layer = new int[]{10, 11, 12, 14, 15, 16};
                size = 27;
                break;
            case 7:
                layer = new int[]{10, 11, 12, 13, 14, 15, 16};
                size = 27;
                break;
            case 8:
                layer = new int[]{10, 12, 14, 16, 28, 30, 32, 34};
                size = 45;
                break;
            case 9:
                layer = new int[]{10, 11, 12, 14, 15, 16, 30, 31, 32};
                size = 45;
                break;
            case 10:
                layer = new int[]{11, 12, 13, 14, 15, 20, 21, 22, 23, 24};
                size = 36;
                break;
            case 11:
                layer = new int[]{11, 12, 13, 14, 15, 19, 20, 21, 23, 24, 25};
                size = 36;
                break;
            case 12:
                layer = new int[]{10, 11, 12, 14, 15, 16, 19, 20, 21, 23, 24, 25};
                size = 45;
                break;
        }

        Inventory inv;
        if (update) {
            inv = player.getOpenInventory().getTopInventory();
        } else {
            inv = Bukkit.createInventory(null, size, Language.getMsg(player, Messages.GUI_NAME));
        }
        List<Integer> usedSlots = new ArrayList<>();

        int xx = 0;
        for (ITeam bwt : arena.getTeams()) {
            if (layer.length == xx) break;
            ItemStack i = new ItemStack(Material.valueOf(Config.config.getString(Config.SELECTOR_ITEM_STACK_MATERIAL)));
            i = Main.bw.getVersionSupport().colourItem(i, bwt);
            i = Main.bw.getVersionSupport().addCustomData(i, TEAM_JOIN_IDENTIFIER + bwt.getName());

            ItemMeta im = i.getItemMeta();
            im.setDisplayName(Language.getMsg(player, Messages.CHOICE_NAME).replace("{color}", TeamColor.getChatColor(bwt.getColor()).toString()).replace("{team}", bwt.getName())
                    .replace("{selected}", String.valueOf(bwt.getMembers().size())).replace("{total}", String.valueOf(arena.getMaxInTeam())));
            List<String> lore = new ArrayList<>();
            for (String s : Language.getList(player, Messages.CHOICE_LORE)) {
                s = s.replace("{color}", TeamColor.getChatColor(bwt.getColor()).toString()).replace("{team}", bwt.getName()).replace("{selected}", String.valueOf(bwt.getMembers().size()))
                        .replace("{total}", String.valueOf(arena.getMaxInTeam()));
                if (s.contains("{members}")) {
                    s = s.replace("{members}", "");
                    String color = ChatColor.getLastColors(s);
                    for (Player p : bwt.getMembers()) {
                        lore.add(color + p.getName());
                    }
                } else {
                    lore.add(s);
                }
            }

            im.setLore(lore);
            i.setItemMeta(im);
            inv.setItem(layer[xx], i);
            usedSlots.add(layer[xx]);
            xx++;
        }

        ITeam selected = arena.getTeam(player);
        for (int x = 0; x < inv.getSize(); x++) {
            if (usedSlots.contains(x)) continue;
            if (selected == null) {
                inv.setItem(x, new ItemStack(Material.AIR));
            } else {
                String material = Main.bw.getForCurrentVersion("STAINED_GLASS_PANE", "STAINED_GLASS_PANE", "BLACK_STAINED_GLASS_PANE");
                inv.setItem(x, Main.bw.getVersionSupport().colourItem(new ItemStack(Material.valueOf(material)), selected));
            }
        }

        player.openInventory(inv);
    }

    /**
     * Add a player to a team
     *
     * @return false if cannot add
     */
    public static boolean joinTeam(Player player, String teamName) {
        IArena arena = Main.bw.getArenaUtil().getArenaByPlayer(player);
        if (arena == null) return false;
        if (arena.getStatus() == GameState.playing) return false;
        if (arena.getStatus() == GameState.restarting) return false;
        ITeam bwt = arena.getTeam(teamName);
        if (bwt == null) return false;

        if (Main.bw.getPartyUtil().hasParty(player)) {
            player.sendMessage(Language.getMsg(player, Messages.PARTY_DENIED));
            return false;
        }

        //Check if team is full
        if (bwt.getSize() == arena.getMaxInTeam()) {
            player.sendMessage(Language.getMsg(player, Messages.TEAM_FULL).replace("{color}", TeamColor.getChatColor(bwt.getColor()).toString()).replace("{team}", bwt.getName()));
            return false;
        }


        //Balance Teams
        for (ITeam t : arena.getTeams()) {
            if (t == bwt) continue;
            if (t.getMembers().size() < bwt.getMembers().size()) {
                if (Config.config.getBoolean(Config.BALANCE_TEAMS)) {
                    player.sendMessage(Language.getMsg(player, Messages.TEAM_NOT_BALANCED));
                    return false;
                } else if (arena.getStatus() == GameState.starting) {
                    player.sendMessage(Language.getMsg(player, Messages.CANT_JOIN_WHILE_STARTING));
                }
            }
        }

        ITeam team = arena.getTeam(player);
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
     */
    public static void removePlayerFromTeam(Player player, ITeam team) {
        team.getMembers().remove(player);
        team.destroyBedHolo(player);
    }

    /**
     * Update inventories
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
     */
    public static void giveItem(Player p, ITeam team) {
        ItemStack i;
        try {
            i = new ItemStack(Material.valueOf(Config.config.getString(Config.SELECTOR_ITEM_STACK_MATERIAL)));
        } catch (Exception ex) {
            Main.plugin.getLogger().severe("Team-Selector Material is invalid!");
            ex.printStackTrace();
            return;
        }

        if (team != null && Config.config.getBoolean(Config.GIVE_SELECTOR_SELECTED_TEAM_COLOR)) {
            i = Main.bw.getVersionSupport().colourItem(i, team);
        }

        ItemMeta im = i.getItemMeta();
        im.setLore(Language.getList(p, Messages.SELECTOR_LORE));
        im.setDisplayName(Language.getMsg(p, Messages.SELECTOR_NAME));
        i.setItemMeta(im);
        i = Main.bw.getVersionSupport().addCustomData(i, TeamSelectorGUI.TEAM_SELECTOR_IDENTIFIER);
        p.getInventory().setItem(Config.config.getInt(Config.SELECTOR_SLOT), i);
    }
}

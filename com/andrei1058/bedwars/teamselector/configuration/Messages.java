package com.andrei1058.bedwars.teamselector.configuration;

import com.andrei1058.bedwars.configuration.Language;

import java.util.Arrays;

public class Messages {

    /**
     * Setup Default Messages
     */
    public static void setupMessages() {
        for (Language l : Language.getLanguages()) {
            if (!l.exists(GUI_NAME)) {
                l.set(GUI_NAME, "&8Team Selector");
            }
            if (!l.exists(SELECTOR_NAME)) {
                l.set(SELECTOR_NAME, "&9Team Selector");
            }
            if (!l.exists(SELECTOR_LORE)) {
                l.set(SELECTOR_LORE, Arrays.asList("&7Right-click to to open!"));
            }
            if (!l.exists(CHOICE_LORE)){
                l.set(CHOICE_LORE, Arrays.asList("", "&f- {selected} players.", "&cClick to join!"));
            }
            if (!l.exists(CHOICE_NAME)){
                l.set(CHOICE_NAME, "{color}{team} &fTeam");
            }
            if (!l.exists(SWITCH_DISABLED)){
                l.set(SWITCH_DISABLED, "{prefix}&cYou cannot change your team!");
            }
            if (!l.exists(TEAM_JOIN)){
                l.set(TEAM_JOIN, "{prefix}&eYou joined {color}{team} &eteam!");
            }
            if (!l.exists(TEAM_FULL)){
                l.set(TEAM_FULL, "{prefix}{color}{team} &c is full!");
            }
            if (!l.exists(TEAM_NOT_BALANCED)){
                l.set(TEAM_NOT_BALANCED, "{prefix}&cTeams are not balanced! Try joining another team!");
            }
            if (!l.exists(PARTY_DENIED)){
                l.set(PARTY_DENIED, "{prefix}&cYou can't choose your team because you're in a party!");
            }
            if (!l.exists(CANT_JOIN_WHILE_STARTING)){
                l.set(CANT_JOIN_WHILE_STARTING, "{prefix}&cYou cannot join this team ar this moment! Try with another!");
            }
        }
    }

    public static final String PATH = "add-ons.team-selector.";
    public static final String GUI_NAME = PATH + "inventory-name";
    public static final String SELECTOR_NAME = PATH + "selector-name";
    public static final String SELECTOR_LORE = PATH + "selector-lore";
    public static final String CHOICE_NAME = PATH + "choice.name";
    public static final String CHOICE_LORE = PATH + "choice.lore";
    public static final String SWITCH_DISABLED = PATH + "switch-disabled";
    public static final String TEAM_JOIN = PATH + "team-join";
    public static final String TEAM_FULL = PATH + "team-full";
    public static final String TEAM_NOT_BALANCED = PATH + "teams-not-balanced";
    public static final String PARTY_DENIED = PATH + "party-deny";
    public static final String CANT_JOIN_WHILE_STARTING = PATH + "cant-join-while-starting";
}

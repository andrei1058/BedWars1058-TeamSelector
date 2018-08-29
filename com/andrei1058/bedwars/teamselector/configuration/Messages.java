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
            if (!l.exists(SELECTOR_NAME)){
                l.set(SELECTOR_NAME, "&9Team Selector");
            }
            if (!l.exists(SELECTOR_LORE)){
                l.set(SELECTOR_LORE, Arrays.asList("&7Right-click to to open!"));
            }
        }
    }

    public static final String PATH = "add-ons.team-selector.";
    public static final String GUI_NAME = PATH + "inventory-name";
    public static final String SELECTOR_NAME = PATH + "selector-name";
    public static final String SELECTOR_LORE = PATH + "selector-lore";
}

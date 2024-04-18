package com.controladad.boutia_pms.fragment_navigation_handler;


public class BackTo implements Command {
    private String screenKey;

    public BackTo(String screenKey) {
        this.screenKey = screenKey;
    }

    public String getScreenKey() {
        return screenKey;
    }
}

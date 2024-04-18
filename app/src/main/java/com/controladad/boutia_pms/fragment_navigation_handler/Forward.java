package com.controladad.boutia_pms.fragment_navigation_handler;

import com.controladad.boutia_pms.view_models.GeneralVM;

import lombok.Getter;

public class Forward implements Command {

    @Getter
    private String screenKey;
    @Getter
    private GeneralVM transitionData;


    public Forward(String screenKey, GeneralVM transitionData) {
        this.screenKey = screenKey;
        this.transitionData = transitionData;
    }

}

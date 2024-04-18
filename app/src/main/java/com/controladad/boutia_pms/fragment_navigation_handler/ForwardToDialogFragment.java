package com.controladad.boutia_pms.fragment_navigation_handler;


import com.controladad.boutia_pms.view_models.GeneralVM;

import lombok.Getter;

class ForwardToDialogFragment implements Command {
    @Getter
    private String screenKey;
    @Getter
    private GeneralVM transitionData;

    public ForwardToDialogFragment(String screenKey, GeneralVM transitionData) {
        this.screenKey = screenKey;
        this.transitionData = transitionData;
    }
}

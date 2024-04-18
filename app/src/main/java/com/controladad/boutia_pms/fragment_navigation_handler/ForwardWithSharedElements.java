package com.controladad.boutia_pms.fragment_navigation_handler;


import android.view.View;

import com.controladad.boutia_pms.view_models.GeneralVM;

import lombok.Getter;

class ForwardWithSharedElements implements Command {

    @Getter
    private String screenKey;
    @Getter
    private GeneralVM transitionData;
    @Getter
    private View sharedElement;



    @Getter
    private String transitionName;

    public ForwardWithSharedElements(String screenKey, GeneralVM transitionData, String transitionName, View sharedElement) {
        this.screenKey = screenKey;
        this.transitionData = transitionData;
        this.sharedElement = sharedElement;
        this.transitionName = transitionName;
    }

}

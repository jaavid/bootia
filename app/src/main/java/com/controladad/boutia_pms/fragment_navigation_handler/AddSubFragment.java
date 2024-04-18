package com.controladad.boutia_pms.fragment_navigation_handler;

import androidx.fragment.app.Fragment;

import com.controladad.boutia_pms.view_models.GeneralVM;

import lombok.Getter;

public class AddSubFragment implements Command {
    private String screenKey;
    private GeneralVM transitionData;
    @Getter
    private int layoutId;
    @Getter
    private Fragment parentFragment;

    public AddSubFragment(String screenKey, GeneralVM transitionData,int layoutId,Fragment parentFragment) {
        this.screenKey = screenKey;
        this.transitionData = transitionData;
        this.layoutId = layoutId;
        this.parentFragment = parentFragment;
    }

    public String getScreenKey() {
        return screenKey;
    }

    public GeneralVM getTransitionData() {
        return transitionData;
    }
}

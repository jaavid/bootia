package com.controladad.boutia_pms.view_models.items_view_models;

import com.controladad.boutia_pms.utility.Constants;

import lombok.Getter;
import lombok.Setter;


public class SimpleTextViewDataModel implements GeneralDataModel {

    @Getter
    private String titleToShow;

    @Getter
    private TextGetter getter;
    private TextSetter setter;

    @Setter
    @Getter
    private String itemName;
    @Getter
    private boolean isItemFilled = true;

    public SimpleTextViewDataModel(String titleToShow, TextGetter getter, TextSetter setter) {
        this.titleToShow = titleToShow;
        this.getter = getter;
        this.setter = setter;
    }

    @Override
    public int getKey() {
        return Constants.SIMPLE_TEXT_VIEW_ITEM_KEY;
    }
}

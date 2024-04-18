package com.controladad.boutia_pms.view_models.items_view_models;

import android.view.View;

import com.controladad.boutia_pms.utility.Constants;

import lombok.Getter;
import lombok.Setter;


public class SingleButtonDataModel implements GeneralDataModel {
    @Getter
    private String buttonText;
    @Getter
    private View.OnClickListener buttonClicked;
    private TextGetter getter;
    private TextSetter setter;

    @Setter
    @Getter
    private String itemName;
    @Getter
    private boolean isItemFilled = true;

    public SingleButtonDataModel(String buttonText, View.OnClickListener buttonClicked, TextGetter getter, TextSetter setter) {
        this.buttonText = buttonText;
        this.buttonClicked = buttonClicked;
        this.getter = getter;
        this.setter = setter;
    }

    @Override
    public int getKey() {
        return Constants.SINGLE_BUTTON_CARD_ITEM_KEY;
    }
}

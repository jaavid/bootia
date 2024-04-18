package com.controladad.boutia_pms.view_models.items_view_models;


import android.view.View;

import com.controladad.boutia_pms.utility.Constants;

import lombok.Getter;
import lombok.Setter;

public class DoubleButtonDataModel implements GeneralDataModel {
    @Getter
    private String strSubmitButtonTextDouble;
    @Getter
    private String strAddButtonTextDouble;

    @Setter
    @Getter
    private String itemName;
    @Getter
    private boolean isItemFilled = true;

    @Getter
    private View.OnClickListener buttonSubmitClicked;
    @Getter
    private View.OnClickListener buttonAddClicked;
    private TextGetter getter;
    private TextSetter setter;

    public DoubleButtonDataModel(String strSubmitButtonTextDouble, String strAddButtonTextDouble, View.OnClickListener buttonSubmitClicked, View.OnClickListener buttonAddClicked, TextGetter getter, TextSetter setter) {
        this.strSubmitButtonTextDouble = strSubmitButtonTextDouble;
        this.strAddButtonTextDouble = strAddButtonTextDouble;
        this.buttonSubmitClicked = buttonSubmitClicked;
        this.buttonAddClicked = buttonAddClicked;
        this.getter = getter;
        this.setter = setter;
    }

    @Override
    public int getKey() {
        return Constants.DOUBLE_BUTTON_CARD_ITEM_KEY;
    }
}

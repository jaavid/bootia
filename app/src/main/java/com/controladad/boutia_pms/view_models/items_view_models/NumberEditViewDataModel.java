package com.controladad.boutia_pms.view_models.items_view_models;

import com.controladad.boutia_pms.utility.Constants;

import lombok.Getter;
import lombok.Setter;

public class NumberEditViewDataModel implements GeneralDataModel {
    @Getter
    private String inputNumber;

    public void setInputNumber(String inputNumber) {
        this.inputNumber = inputNumber;
        setter.setText(inputNumber);
        if(!inputNumber.isEmpty()) isItemFilled = true;
        else isItemFilled = false;
    }

    @Getter
    private String inputNumberTitle;
    private TextGetter getter;
    private TextSetter setter;
    @Getter
    private boolean isDecimal;

    public NumberEditViewDataModel(String inputNumberTitle, TextGetter getter, TextSetter setter) {
        this.inputNumberTitle = inputNumberTitle;
        this.getter = getter;
        this.setter = setter;
        inputNumber = getter.getText();
    }

    public NumberEditViewDataModel(String inputNumberTitle, TextGetter getter, TextSetter setter, boolean isDecimal) {
        this.inputNumberTitle = inputNumberTitle;
        this.getter = getter;
        this.setter = setter;
        inputNumber = getter.getText();
        this.isDecimal = isDecimal;
    }

    @Setter
    private String itemName;

    @Override
    public String getItemName() {
        if(itemName == null)
            itemName = inputNumberTitle;
        return itemName;
    }

    @Getter
    private boolean isItemFilled;

    @Override
    public int getKey() {
        if (!isDecimal)
        return Constants.NUMBER_EDIT_VIEW_CARD_ITEM_KEY;
        else return Constants.NUMBER_DECIMAL_EDIT_VIEW_CARD_ITEM_KEY;
    }
}

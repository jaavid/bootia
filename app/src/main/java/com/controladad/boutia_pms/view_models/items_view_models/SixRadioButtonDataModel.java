package com.controladad.boutia_pms.view_models.items_view_models;


import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.controladad.boutia_pms.utility.Constants;

import lombok.Getter;
import lombok.Setter;

public class SixRadioButtonDataModel implements GeneralDataModel {
    @Getter
    private String firstRadioButton;
    @Getter
    private String secondRadioButton;
    @Getter
    private String thirdRadioButton;
    @Getter
    private String fourthRadioButton;
    @Getter
    private String fifthRadioButton;
    @Getter
    private String sixthRadioButton;
    @Getter
    @Setter
    private RadioGroup rightRadioGroup;
    @Setter
    private RadioGroup leftRadioGroup;
    @Getter
    private String selectedItem;
    private TextGetter getter;
    private TextSetter setter;

    @Setter
    @Getter
    private String itemName;
    @Getter
    private boolean isItemFilled = true;

    public SixRadioButtonDataModel(String firstRadioButton, String secondRadioButton,
                                   String thirdRadioButton, String fourthRadioButton,
                                   String fifthRadioButton, String sixthRadioButton,
                                   TextGetter getter,TextSetter setter) {
        this.firstRadioButton = firstRadioButton;
        this.secondRadioButton = secondRadioButton;
        this.thirdRadioButton = thirdRadioButton;
        this.fourthRadioButton = fourthRadioButton;
        this.fifthRadioButton = fifthRadioButton;
        this.sixthRadioButton = sixthRadioButton;
        this.getter = getter;
        this.setter = setter;
        selectedItem = getter.getText();
    }

    public View.OnClickListener onRightItemsClicked(){
        return v -> {
          leftRadioGroup.clearCheck();
          selectedItem = ((RadioButton)v).getText().toString();
          setter.setText(selectedItem);
        };
    }

    public View.OnClickListener onLeftItemsClicked(){
        return v -> {
            rightRadioGroup.clearCheck();
            selectedItem = ((RadioButton)v).getText().toString();
            setter.setText(selectedItem);
        };
    }

    @Override
    public int getKey() {
        return Constants.SIX_RADIO_BUTTON_CARD_ITEM_KEY;
    }
}

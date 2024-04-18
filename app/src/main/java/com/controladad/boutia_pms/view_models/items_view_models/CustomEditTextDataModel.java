package com.controladad.boutia_pms.view_models.items_view_models;


import com.controladad.boutia_pms.utility.Constants;

import lombok.Getter;
import lombok.Setter;

public class CustomEditTextDataModel implements GeneralDataModel {

    @Getter
    private String txtUserDataEntry;

    @Getter
    private String hintForEditText;

    @Setter
    private String itemName;

    public String getItemName() {
        if(itemName == null)
            itemName = hintForEditText;
        return itemName;
    }


    @Override
    public boolean isItemFilled() {
        return ((txtUserDataEntry != null && !txtUserDataEntry.isEmpty()));// ||
                //Objects.equals(hintForEditText, BoutiaApplication.INSTANCE.getString(R.string.tozihat)) ||
                //Objects.equals(hintForEditText, BoutiaApplication.INSTANCE.getString(R.string.others)));
    }

    public void setTxtUserDataEntry(String txtUserDataEntry) {
        this.txtUserDataEntry = txtUserDataEntry;
        setter.setText(txtUserDataEntry);
    }


    private TextGetter getter;
    private TextSetter setter;

    public CustomEditTextDataModel(String hintForEditText, TextGetter getter, TextSetter setter) {
        this.hintForEditText = hintForEditText;
        this.getter = getter;
        this.setter = setter;
        txtUserDataEntry = getter.getText();
    }

    @Override
    public int getKey() {
        return Constants.CUSTOM_EDIT_TEXT_CARD_ITEM_KEY;
    }
}

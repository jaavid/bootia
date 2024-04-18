package com.controladad.boutia_pms.view_models.items_view_models;


import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import android.view.View;

import com.controladad.boutia_pms.BR;
import com.controladad.boutia_pms.utility.Constants;

import lombok.Getter;
import lombok.Setter;

public class CustomTextViewDataModel extends BaseObservable implements GeneralDataModel {
    @Getter
    private String strCustomTextViewTitle;

    @Setter
    private String itemName;

    @Getter
    private int buttonVisibility = View.GONE;

    @Getter
    private View.OnClickListener onButtonClicked;
    @Getter
    private String buttonText;

    @Override
    public String getItemName() {
        if(itemName == null)
            itemName = strCustomTextViewTitle;
        return itemName;
    }

    @Getter
    private boolean isItemFilled = true;

    @Bindable
    @Getter
    private String strDataToShow;
    @Getter
    private int imageResource;

    private TextGetter getter;
    private TextSetter setter;


    public CustomTextViewDataModel(String strCustomTextViewTitle, TextGetter getter, TextSetter setter, int imageResource) {
        this.strCustomTextViewTitle = strCustomTextViewTitle;
        this.getter = getter;
        this.setter = setter;
        strDataToShow = getter.getText();
        this.imageResource = imageResource;
    }

    public CustomTextViewDataModel(String strCustomTextViewTitle, TextGetter getter, TextSetter setter, int imageResource, View.OnClickListener onClickListener, String buttonText) {
        this.strCustomTextViewTitle = strCustomTextViewTitle;
        this.getter = getter;
        this.setter = setter;
        strDataToShow = getter.getText();
        this.imageResource = imageResource;
        if(onClickListener != null)
            buttonVisibility = View.VISIBLE;
        onButtonClicked = onClickListener;
        this.buttonText = buttonText;
    }

    @Override
    public int getKey() {
        return Constants.CUSTOM_TEXT_VIEW_CARD_ITEM_KEY;
    }

    public void setStrDataToShow(String strDataToShow) {
        this.strDataToShow = strDataToShow;
        notifyPropertyChanged(BR.strDataToShow);
        setter.setText(strDataToShow);
    }

}

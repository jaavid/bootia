package com.controladad.boutia_pms.view_models.items_view_models;


import android.widget.CompoundButton;
import android.widget.RadioButton;

import com.controladad.boutia_pms.utility.Constants;

import java.util.Objects;

import lombok.Getter;
import lombok.Setter;

public class SegmentedControlDataModel implements GeneralDataModel {
    @Getter
    private String firstSegmentText;
    @Getter
    private String secondSegmentText;
    @Getter
    private String thirdSegmentText;
    @Getter
    private String fourthSegmentText;
    @Getter
    private String fifthSegmentText;
    @Getter
    private String title;
    @Getter
    private String selectedItemText;
    @Setter
    @Getter
    private CompoundButton.OnCheckedChangeListener onCheckedChangeListener;
    @Getter
    private boolean isFirstSegmentChecked;
    @Getter
    private boolean isSecondSegmentChecked;
    @Getter
    private boolean isThirdSegmentChecked;
    @Getter
    private boolean isFourthSegmentChecked;
    @Getter
    private boolean isFifthSegmentChecked;
    private TextGetter getter;
    private TextSetter setter;

    @Setter
    private String itemName;

    @Override
    public String getItemName() {
        if(itemName == null)
            itemName = title;
        return itemName;
    }

    public boolean isItemFilled() {
        return isFirstSegmentChecked || isSecondSegmentChecked || isThirdSegmentChecked || isFourthSegmentChecked || isFifthSegmentChecked;
    }

    public SegmentedControlDataModel(String firstSegmentText, String secondSegmentText, String title, TextGetter getter, TextSetter setter) {
        this.firstSegmentText = firstSegmentText;
        this.secondSegmentText = secondSegmentText;
        this.title = title;
        this.getter = getter;
        this.setter = setter;
        checkItem();
    }

    public SegmentedControlDataModel(String firstSegmentText, String secondSegmentText, String thirdSegmentText, String title, TextGetter getter, TextSetter setter) {
        this.firstSegmentText = firstSegmentText;
        this.secondSegmentText = secondSegmentText;
        this.thirdSegmentText = thirdSegmentText;
        this.title = title;
        this.getter = getter;
        this.setter = setter;
        checkItem();
    }

    public SegmentedControlDataModel(String firstSegmentText, String secondSegmentText, String thirdSegmentText, String fourthSegmentText, String title, TextGetter getter, TextSetter setter) {
        this.firstSegmentText = firstSegmentText;
        this.secondSegmentText = secondSegmentText;
        this.thirdSegmentText = thirdSegmentText;
        this.fourthSegmentText = fourthSegmentText;
        this.title = title;
        this.getter = getter;
        this.setter = setter;
        checkItem();
    }

    public SegmentedControlDataModel(String firstSegmentText, String secondSegmentText, String thirdSegmentText, String fourthSegmentText, String fifthSegmentText, String title, TextGetter getter, TextSetter setter) {
        this.firstSegmentText = firstSegmentText;
        this.secondSegmentText = secondSegmentText;
        this.thirdSegmentText = thirdSegmentText;
        this.fourthSegmentText = fourthSegmentText;
        this.fifthSegmentText = fifthSegmentText;
        this.title = title;
        this.getter = getter;
        this.setter = setter;
        checkItem();
    }

    @Override
    public int getKey() {
        if(thirdSegmentText == null)
            return Constants.TWO_SEGMENT_CARD_ITEM_KEY;
        else if(fourthSegmentText == null)
            return Constants.THREE_SEGMENT_CONTROL_CARD_ITEM_KEY;
        else if(fifthSegmentText == null)
            return Constants.FOUR_SEGMENT_CONTROL_CARD_ITEM_KEY;
        else
            return Constants.FIVE_SEGMENT_CONTROL_CARD_ITEM_KEY;
    }

    public RadioButton.OnClickListener onCheckedChanged(){
        return v -> {
            RadioButton radioButton = (RadioButton) v;
            if(radioButton.isChecked()){
                selectedItemText = radioButton.getText().toString();
                setter.setText(selectedItemText);
                if(onCheckedChangeListener!=null)
                    onCheckedChangeListener.onCheckedChanged(radioButton,radioButton.isChecked());
                isFirstSegmentChecked = false;
                isSecondSegmentChecked = false;
                isThirdSegmentChecked = false;
                isFourthSegmentChecked = false;
                isFifthSegmentChecked = false;
                if(Objects.equals(selectedItemText, firstSegmentText))
                    isFirstSegmentChecked = true;
                else if(Objects.equals(selectedItemText, secondSegmentText))
                    isSecondSegmentChecked = true;
                else if(Objects.equals(selectedItemText, thirdSegmentText))
                    isThirdSegmentChecked = true;
                else if(Objects.equals(selectedItemText, fourthSegmentText))
                    isFourthSegmentChecked = true;
                else if(Objects.equals(selectedItemText, fifthSegmentText))
                    isFifthSegmentChecked = true;
            }
        };
    }

    private void checkItem(){
        isFirstSegmentChecked = false;
        isSecondSegmentChecked = false;
        isThirdSegmentChecked = false;
        isFourthSegmentChecked = false;
        isFifthSegmentChecked = false;
        selectedItemText = getter.getText();
        if(selectedItemText!=null){
            if(Objects.equals(selectedItemText, firstSegmentText))
                isFirstSegmentChecked = true;
            else if(Objects.equals(selectedItemText, secondSegmentText))
                isSecondSegmentChecked = true;
            else if(Objects.equals(selectedItemText, thirdSegmentText))
                isThirdSegmentChecked = true;
            else if(Objects.equals(selectedItemText, fourthSegmentText))
                isFourthSegmentChecked = true;
            else if(Objects.equals(selectedItemText, fifthSegmentText))
                isFifthSegmentChecked = true;
        }
    }
}

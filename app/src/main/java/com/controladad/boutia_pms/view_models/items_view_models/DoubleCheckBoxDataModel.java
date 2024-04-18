package com.controladad.boutia_pms.view_models.items_view_models;


import android.view.View;
import android.widget.LinearLayout;

import com.controladad.boutia_pms.utility.Constants;

import cn.refactor.library.SmoothCheckBox;
import lombok.Getter;
import lombok.Setter;

public class DoubleCheckBoxDataModel implements GeneralDataModel {

    @Setter
    @Getter
    private boolean isUserSelectedRight;
    @Getter
    private String rightText;
    @Setter
    @Getter
    private SmoothCheckBox.OnCheckedChangeListener onCheckedChangeListenerRight;
    @Setter
    @Getter
    private boolean isUserSelectedLeft;
    @Getter
    private String leftText;
    @Setter
    @Getter
    private SmoothCheckBox.OnCheckedChangeListener onCheckedChangeListenerLeft;
    private TextGetter leftGetter;
    private TextSetter leftSetter;
    private TextGetter rightGetter;
    private TextSetter rightSetter;
    @Setter
    private SmoothCheckBox leftCheckBox;
    @Setter
    private SmoothCheckBox rightCheckBox;
    @Setter
    private LinearLayout leftLinearLayout;

    @Getter
    private int leftCheckBoxVisibility = View.VISIBLE;

    public void setLeftCheckBoxVisibility(int leftCheckBoxVisibility) {
        this.leftCheckBoxVisibility = leftCheckBoxVisibility;
        if(leftLinearLayout!=null)
            leftLinearLayout.setVisibility(leftCheckBoxVisibility);
    }

    public void setItemName(String itemName) {

    }

    public String getItemName() {
        return null;
    }

    public DoubleCheckBoxDataModel(String rightText, SmoothCheckBox.OnCheckedChangeListener onCheckedChangeListenerRight,
                                   String leftText, SmoothCheckBox.OnCheckedChangeListener onCheckedChangeListenerLeft
            ,TextGetter rightGetter, TextSetter rightSetter, TextGetter leftGetter , TextSetter leftSetter) {

        this.rightText = rightText;
        this.onCheckedChangeListenerRight = onCheckedChangeListenerRight;
        this.leftText = leftText;
        this.onCheckedChangeListenerLeft = onCheckedChangeListenerLeft;
        this.leftGetter = leftGetter;
        this.leftSetter = leftSetter;
        this.rightGetter = rightGetter;
        this.rightSetter = rightSetter;
        isUserSelectedRight = Boolean.valueOf(rightGetter.getText());
        isUserSelectedLeft = Boolean.valueOf(leftGetter.getText());
    }

    public View.OnClickListener onLeftButtonClicked(){
        return v -> {
            isUserSelectedLeft = !isUserSelectedLeft();
            leftSetter.setText(String.valueOf(isUserSelectedLeft));
            leftCheckBox.setChecked(isUserSelectedLeft,true);
            if (onCheckedChangeListenerLeft != null)
                onCheckedChangeListenerLeft.onCheckedChanged(leftCheckBox, isUserSelectedLeft);
        };
    }


    public View.OnClickListener onRightButtonClicked(){
        return v -> {
            isUserSelectedRight = !isUserSelectedRight;
            rightCheckBox.setChecked(isUserSelectedRight,true);
            rightSetter.setText(String.valueOf(isUserSelectedRight));
            if(onCheckedChangeListenerRight !=null)
                onCheckedChangeListenerRight.onCheckedChanged(rightCheckBox,isUserSelectedRight);
        };
    }

    @Override
        public int getKey() {
            return Constants.DOUBLE_CHECK_BOX_CARD_ITEM_KEY;
        }

    @Override
    public boolean isItemFilled() {
        return isUserSelectedLeft || isUserSelectedRight;
    }

}

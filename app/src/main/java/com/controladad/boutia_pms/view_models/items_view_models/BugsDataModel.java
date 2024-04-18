package com.controladad.boutia_pms.view_models.items_view_models;


import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import android.view.View;

import com.controladad.boutia_pms.BR;
import com.controladad.boutia_pms.utility.Constants;

import lombok.Getter;
import lombok.Setter;

public class BugsDataModel extends BaseObservable implements GeneralDataModel {

    @Bindable
    @Getter
    private int deleteIconVisibility = View.GONE;

    @Bindable
    @Getter
    private View.OnClickListener onRemoveItemClicked;
    @Getter
    private String text = "ایرادات";

    @Setter
    @Getter
    private String itemName;
    @Getter
    private boolean isItemFilled = true;

    public BugsDataModel() {
    }

    public BugsDataModel(String text) {
        this.text = text;
    }

    public void setOnRemoveItemClicked(View.OnClickListener onRemoveItemClicked) {
        this.onRemoveItemClicked = onRemoveItemClicked;
        notifyPropertyChanged(BR.onRemoveItemClicked);
        if(onRemoveItemClicked!=null){
            deleteIconVisibility = View.VISIBLE;
            notifyPropertyChanged(BR.deleteIconVisibility);
        }
    }

    public void setDeleteIconVisibility(int deleteIconVisibility) {
        this.deleteIconVisibility = deleteIconVisibility;
        notifyPropertyChanged(BR.deleteIconVisibility);
    }

    public int getKey() {
        return Constants.BUGS_ITEM_KEY;
    }
}

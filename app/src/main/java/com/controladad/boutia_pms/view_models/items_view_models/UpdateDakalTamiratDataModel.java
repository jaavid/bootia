package com.controladad.boutia_pms.view_models.items_view_models;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.library.baseAdapters.BR;

import android.view.View;

//import com.android.databinding.library.baseAdapters.BR;
import com.controladad.boutia_pms.utility.Constants;
import com.controladad.boutia_pms.view_models.GenericMethodTwo;

import lombok.Getter;

public class UpdateDakalTamiratDataModel extends BaseObservable implements GeneralDataModel {
    private String itemName;
    @Getter
    private String lastUpdateDate;

    public void setLastUpdateDate(String lastUpdateDate) {
        this.lastUpdateDate = lastUpdateDate;
        notifyChange();
    }

    private String key;
    private GenericMethodTwo<String, UpdateDakalTamiratDataModel> update;
    @Getter
    private boolean isLoading;
    @Getter
    private int state = Constants.READY_STATE;
    public void setState(int state){
        this.state = state;
        notifyChange();
    }

    public void setLoading(boolean loading) {
        isLoading = loading;
        notifyChange();
    }
    @Bindable
    @Getter
    private int progressPercent = 0;

    public void setProgressPercent(int progressPercent) {
        this.progressPercent = progressPercent;
        notifyPropertyChanged(BR.progressPercent);
    }

    public UpdateDakalTamiratDataModel(String itemName, String key,
                                       boolean isLoading, String lastUpdateDate, GenericMethodTwo<String, UpdateDakalTamiratDataModel> update) {
        this.itemName = itemName;
        this.key = key;
        this.update = update;
        this.isLoading = isLoading;
        this.lastUpdateDate = lastUpdateDate;
    }

    public View.OnClickListener onClickListener = v -> {
        update.fun(key, this);
    };

    @Override
    public int getKey() {
        return 0;
    }

    @Override
    public String getItemName() {
        return itemName;
    }

    @Override
    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    @Override
    public boolean isItemFilled() {
        return true;
    }
}

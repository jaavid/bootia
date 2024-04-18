package com.controladad.boutia_pms.view_models.items_view_models;

import androidx.databinding.BaseObservable;
import androidx.core.content.ContextCompat;

import com.controladad.boutia_pms.R;
import com.controladad.boutia_pms.models.database.IradatDakalEntity;
import com.controladad.boutia_pms.utility.BoutiaApplication;

import lombok.Getter;
import saman.zamani.persiandate.PersianDate;
import saman.zamani.persiandate.PersianDateFormat;

public class TowerChooseIVM extends BaseObservable implements GeneralDataModel {
    @Getter
    private IradatDakalEntity iradatDakalEntity;
    @Getter
    private boolean isSelected = false;

    public int backGround() {
        if (isSelected) {
            return ContextCompat.getColor(BoutiaApplication.INSTANCE, R.color.colorPrimary);
        } else {
            return ContextCompat.getColor(BoutiaApplication.INSTANCE, R.color.white);
        }
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
        notifyChange();
    }

    public TowerChooseIVM(IradatDakalEntity iradatDakalEntity) {
        this.iradatDakalEntity = iradatDakalEntity;
    }

    public String getDate(){
        String time = iradatDakalEntity.getTimeStamp();
        if (time !=  null){
            Long timeStamp = Long.valueOf(time)*1000;
            PersianDate persianDate = new PersianDate(timeStamp);
            PersianDateFormat persianDateFormat = new PersianDateFormat("Y/m/d");
            return persianDateFormat.format(persianDate);
        }
        else
            return "نامشخص است.";
    }

    @Override
    public int getKey() {
        return iradatDakalEntity.getNId();
    }

    @Override
    public String getItemName() {
        return "دکل شماره -> " + iradatDakalEntity.getBarcode().substring(iradatDakalEntity.getBarcode().length() - 3);
    }

    @Override
    public void setItemName(String itemName) {

    }

    @Override
    public boolean isItemFilled() {
        return false;
    }
}

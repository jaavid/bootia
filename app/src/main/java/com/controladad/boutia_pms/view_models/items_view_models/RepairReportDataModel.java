package com.controladad.boutia_pms.view_models.items_view_models;

import android.view.View;

import com.controladad.boutia_pms.utility.Constants;

import lombok.Getter;
import lombok.Setter;

public class RepairReportDataModel extends ReportItemDataModel {
    @Getter
    private int nId;
    private boolean isRootin;

    public boolean isRootin() {
        return isRootin;
    }

    public RepairReportDataModel(String towerCode, String missionName, String reviewType, String reviewDate, String buttonText, int nId, boolean isRootin) {
        super(towerCode, missionName, reviewType, reviewDate, buttonText);
        if(towerCode == null)
            numberVisibility = View.GONE;
        this.nId = nId;
        this.isRootin = isRootin;
    }

    @Getter
    private int numberVisibility = View.VISIBLE;


    public RepairReportDataModel(int state) {
        super(state);
    }

    @Override
    public int getKey() {
        return Constants.REPAIR_REPORT_KEY;
    }

    @Setter
    @Getter
    private String itemName;


    @Override
    public boolean isItemFilled() {
        return false;
    }
}

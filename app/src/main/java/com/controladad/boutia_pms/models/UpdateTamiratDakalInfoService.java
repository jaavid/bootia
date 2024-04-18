package com.controladad.boutia_pms.models;

import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import androidx.annotation.NonNull;
import androidx.core.app.JobIntentService;

import com.controladad.boutia_pms.view_models.Function;
import com.controladad.boutia_pms.view_models.GenericMethod;
import com.controladad.boutia_pms.view_models.UpdateDakalVM;

import lombok.Setter;

public class UpdateTamiratDakalInfoService extends JobIntentService {

    private final UpdateTamiratBinder mBinder = new UpdateTamiratBinder();
    private UpdateTamiratDakalInfoModel mUpdateTamiratDakalInfoModel;

    public UpdateTamiratBinder getmBinder() {
        return mBinder;
    }


    @Setter
    private String mid;

    public class UpdateTamiratBinder extends Binder {
        public UpdateTamiratDakalInfoService getService() {
            return UpdateTamiratDakalInfoService.this;
        }
    }

    @Override
    protected void onHandleWork(@NonNull Intent intent) {

    }

    public UpdateTamiratDakalInfoModel getmUpdateTamiratDakalInfoModel() {
        return mUpdateTamiratDakalInfoModel;
    }

    public UpdateTamiratDakalInfoModel getUpdateTamiratDakalInfoModel(UpdateDakalVM repairMissionChoseVM
            , Function onSuccess, Function onFailed, GenericMethod<Integer> updateProgress, Function onNoDakal) {
        if (mUpdateTamiratDakalInfoModel == null) {
            mUpdateTamiratDakalInfoModel = new UpdateTamiratDakalInfoModel(repairMissionChoseVM,
                    onSuccess, onFailed, updateProgress, onNoDakal);
        }
        return mUpdateTamiratDakalInfoModel;
    }

    public String getMid() {
        return mid;
    }

    public void setMid(String mid) {
        this.mid = mid;
    }

    @Override
    public IBinder onBind(@NonNull Intent intent) {
        return mBinder;
    }
}

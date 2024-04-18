package com.controladad.boutia_pms.models;

import com.controladad.boutia_pms.models.database.IradatDakalEntity;
import com.controladad.boutia_pms.utility.BoutiaApplication;
import com.controladad.boutia_pms.view_models.RepairTowerChoseVM;

import java.util.ArrayList;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import lombok.Getter;
import timber.log.Timber;

public class RepairDetailModel {
    @Getter
    private ArrayList<IradatDakalEntity> iradatDakalEntities;
    @Getter
    private ArrayList<IradatDakalEntity> iradatDakalEntityFilteredArrayList;
    private RepairTowerChoseVM repairTowerChoseVM;
    private Disposable disposable;

    public RepairDetailModel(RepairTowerChoseVM repairTowerChoseVM) {
        this.repairTowerChoseVM = repairTowerChoseVM;
    }


    public void getIradatDakalFromDataBase(int mid) {
        //mid is now static
        disposable = BoutiaApplication.INSTANCE.getDb()
                .getIradatDakalDao()
                .getIradatDakal(mid)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        missionEntities -> {
                            iradatDakalEntities = new ArrayList<>();
                            iradatDakalEntities.addAll(missionEntities);
                            iradatDakalEntityFilteredArrayList = new ArrayList<>();
                            iradatDakalEntityFilteredArrayList.addAll(iradatDakalEntities);
                            repairTowerChoseVM.afterDataReceivedComplete();
                        }
                        , throwable -> {
                            Timber.d(throwable.getMessage());
                        }
                );
    }

}

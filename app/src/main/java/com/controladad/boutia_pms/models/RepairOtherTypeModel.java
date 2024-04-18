package com.controladad.boutia_pms.models;

import com.controladad.boutia_pms.location.LocationManager;
import com.controladad.boutia_pms.models.database.OtherRepairTypeEntity;
import com.controladad.boutia_pms.utility.BoutiaApplication;
import com.controladad.boutia_pms.view_models.Function;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import saman.zamani.persiandate.PersianDate;
import saman.zamani.persiandate.PersianDateFormat;

public class RepairOtherTypeModel {
    private Disposable disposable;
    public void saveMissionToDataBase(String mid, String elateTakhir, String operationTime, String repairType,
                                      String description,Long startTime, Long finishTime, Function onSuccess, Function onError){

        final OtherRepairTypeEntity[] otherRepairTypeEntity = new OtherRepairTypeEntity[1];

        LocationManager locationManager = LocationManager.getInstance(BoutiaApplication.INSTANCE, Integer.valueOf(mid), repairType);

        String[] lat = new String[1];
        String[] lng = new String[1];

        PersianDate pdate = new PersianDate();
        PersianDateFormat persianDateFormat = new PersianDateFormat("Y/m/d H:i:s");
        String date = persianDateFormat.format(pdate);


        disposable = locationManager.getLocation()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(location -> {
                    lat[0] = String.valueOf(location.getLatitude());
                    lng[0] = String.valueOf(location.getLongitude());
                    otherRepairTypeEntity[0] = new OtherRepairTypeEntity(mid, lat[0], lng[0],date, elateTakhir, operationTime , repairType, description);
                   /* tamirDakalEntity.setSubmitDate(date);
                    tamirDakalEntity.setLat(lat[0]);
                    tamirDakalEntity.setLng(lng[0]);*/
                },throwable -> {
                    onError.fun();
                },()->{
                    disposable = Observable.fromCallable(()->{
                                BoutiaApplication.INSTANCE.getDb()
                                        .getOtherRepairTypeDao()
                                        .insertIradatDakal(otherRepairTypeEntity[0]);
                                return false;
                            }
                    ).subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(s->{},
                                    throwable -> onError.fun(),
                                    onSuccess::fun
                            );
                });
    }
}

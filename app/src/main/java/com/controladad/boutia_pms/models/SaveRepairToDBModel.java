package com.controladad.boutia_pms.models;

import com.controladad.boutia_pms.location.LocationManager;
import com.controladad.boutia_pms.models.database.TamirDakalEntity;
import com.controladad.boutia_pms.utility.BoutiaApplication;
import com.controladad.boutia_pms.utility.Constants;
import com.controladad.boutia_pms.view_models.Function;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import saman.zamani.persiandate.PersianDate;
import saman.zamani.persiandate.PersianDateFormat;

public class SaveRepairToDBModel {
    private Disposable disposable;

    public void saveRepairToDB(TamirDakalEntity tamirDakalEntity, Long startTime, Long finishTime, Function onComplete, Function onError) {

        LocationManager locationManager = LocationManager.getInstance(BoutiaApplication.INSTANCE, tamirDakalEntity.getMId(), Constants.REPAIR);

        String[] lat = new String[1];
        String[] lng = new String[1];

        PersianDate pdate = new PersianDate();
        PersianDateFormat persianDateFormat = new PersianDateFormat("Y/m/d H:i:s");
        String date = persianDateFormat.format(pdate);



        tamirDakalEntity.setStartTime(startTime.toString());
        tamirDakalEntity.setEndTime(finishTime.toString());

        disposable = locationManager.getLocation()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(location -> {
                    lat[0] = String.valueOf(location.getLatitude());
                    lng[0] = String.valueOf(location.getLongitude());
                    tamirDakalEntity.setSubmitDate(date);
                    tamirDakalEntity.setLat(lat[0]);
                    tamirDakalEntity.setLng(lng[0]);
                }, throwable -> {
                    onError.fun();
                }, () -> {
                    disposable = Observable.fromCallable(() -> {
                                BoutiaApplication.INSTANCE.getDb()
                                        .getTamirDakalDao()
                                        .insertIradatDakal(tamirDakalEntity);
                                return false;
                            }
                    ).subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(s -> {
                                    },
                                    throwable -> onError.fun(),
                                    onComplete::fun
                            );
                });
    }
}

package com.controladad.boutia_pms.models;

import com.controladad.boutia_pms.models.database.Mission_Dao;
import com.controladad.boutia_pms.models.database.Mission_Entity;
import com.controladad.boutia_pms.utility.BoutiaApplication;
import com.controladad.boutia_pms.view_models.Function;
import com.controladad.boutia_pms.view_models.GenericMethod;

import java.util.ArrayList;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import lombok.Getter;
import saman.zamani.persiandate.PersianDate;
import timber.log.Timber;

public class MissionChoseModel {
    @Getter
    private ArrayList<Mission_Entity> missionEntitiesList;
    @Getter
    private ArrayList<Mission_Entity> takhMissionEntitiesList;
    @Getter
    private ArrayList<Mission_Entity> tajMissionEntitiesList;
    @Getter
    private ArrayList<Mission_Entity> onTimeMissionEntitiesList;
    @Getter
    private ArrayList<Mission_Entity> missionEntitiesFilteredList;

    public void getBazdidMissionsFromDataBase(Function afterDataReceived) {

        PersianDate currentDate = new PersianDate();
        int m = currentDate.getShMonth();
        String month;
        if (m < 10)
            month = "0" + m;
        else
            month = String.valueOf(m);
        int d = currentDate.getShDay();
        String day;
        if (d < 10)
            day = "0" + d;
        else
            day = String.valueOf(d);
        String currentDateString = currentDate.getShYear() + "/" +
                month + "/" + day;
        Mission_Dao mission_dao = BoutiaApplication.INSTANCE.getDb()
                .getMissionDao();
        Disposable disposable = mission_dao
                .getMission()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        missionEntities -> {
                            missionEntitiesList = new ArrayList<>();
                            missionEntitiesList.addAll(missionEntities);
                            missionEntitiesFilteredList = new ArrayList<>();
                            missionEntitiesFilteredList.addAll(missionEntities);
                            Disposable disposable2 = mission_dao.getTakhirMission(currentDateString)
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(
                                            takhMissionEntities -> {
                                                takhMissionEntitiesList = new ArrayList<>();
                                                takhMissionEntitiesList.addAll(takhMissionEntities);
                                                Disposable disposable3 = mission_dao.getTajilMission(currentDateString)
                                                .subscribeOn(Schedulers.io())
                                                        .observeOn(AndroidSchedulers.mainThread())
                                                        .subscribe(
                                                                tajMissionEntities -> {
                                                                    tajMissionEntitiesList = new ArrayList<>();
                                                                    tajMissionEntitiesList.addAll(tajMissionEntities);
                                                                    Disposable disposable4 = mission_dao.getOnTimeMission(currentDateString)
                                                                            .subscribeOn(Schedulers.io())
                                                                            .observeOn(AndroidSchedulers.mainThread())
                                                                            .subscribe(
                                                                                    onTomeMissionEntities -> {
                                                                                        onTimeMissionEntitiesList = new ArrayList<>();
                                                                                        onTimeMissionEntitiesList.addAll(onTomeMissionEntities);
                                                                                        afterDataReceived.fun();
                                                                                    }
                                                                                    , throwable -> {
                                                                                        Timber.d(throwable.getMessage());
                                                                                    }
                                                                            );

                                                                }
                                                                , throwable -> {
                                                                    Timber.d(throwable.getMessage());
                                                                }
                                                        );
                                            }
                                            , throwable -> {
                                                Timber.d(throwable.getMessage());
                                            }
                                    );
                        }
                        , throwable -> {
                            Timber.d(throwable.getMessage());
                        }
                );
    }

    public void getVoltage(int mid, GenericMethod<String> onComplete) {
        final String[] mids = new String[1];
        Disposable disposable = Flowable.fromCallable(() -> {
            mids[0] = BoutiaApplication.INSTANCE
                    .getDb()
                    .getOprLineDao()
                    .getOprLinesMid(mid)
                    .get(0)
                    .getMVoltage();
            return false;
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe((s) -> {

                }, throwable -> {
                    if (throwable instanceof IndexOutOfBoundsException) {
                        onComplete.fun("0");
                    }
                    Timber.d(throwable.getMessage());
                }, () -> {
                    onComplete.fun(mids[0]);
                });
    }

}

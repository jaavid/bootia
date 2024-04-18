package com.controladad.boutia_pms.models;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import androidx.annotation.NonNull;

import com.controladad.boutia_pms.models.database.Mission_Entity;
import com.controladad.boutia_pms.models.database.Mission_Tamirat_Entity;
import com.controladad.boutia_pms.models.database.OprLinesEntity;
import com.controladad.boutia_pms.models.retrofit_models.RetrofitModels;
import com.controladad.boutia_pms.models.shared_preferences_models.SharedPreferencesModels;
import com.controladad.boutia_pms.network.dagger.RetrofitProviderComponent;
import com.controladad.boutia_pms.network.network_check.NoNetworkException;
import com.controladad.boutia_pms.network.retrofit.RetrofitApiService;
import com.controladad.boutia_pms.utility.BoutiaApplication;
import com.controladad.boutia_pms.view_models.Function;
import com.controladad.boutia_pms.view_models.GenericMethod;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import lombok.Getter;
import retrofit2.HttpException;
import retrofit2.Response;
import saman.zamani.persiandate.PersianDate;
import saman.zamani.persiandate.PersianDateFormat;
import timber.log.Timber;

import static com.controladad.boutia_pms.utility.Constants.CREDENTIALS_PREFERENCES;

public class HomeModel {

    //private HomeVM homeVM;
    @Getter
    private Disposable bazdidMissionDisposable;
    @Getter
    private Disposable repairMissionDisposable;
    @Getter
    private Disposable getTimeDisposable;
    private SharedPreferencesModels.SharedPreferencesCredentials credentials;
    @Getter
    private List<RetrofitModels.Missions> bazdidMissionsList;
    @Getter
    private List<RetrofitModels.RepairList> repairLists;
    @Getter
    private List<RetrofitModels.Dakals> dakalsList;
    private RetrofitProviderComponent component;

    public HomeModel(RetrofitProviderComponent component) {
        this.component = component;
    }

    public void getMissionsList(GenericMethod<Integer> progressUpdateRepair,
                                GenericMethod<Integer> progressUpdateReview,
                                com.controladad.boutia_pms.view_models.Function afterDataReceived,
                                com.controladad.boutia_pms.view_models.Function onFailed) {

        getSharedPreferences();
        RetrofitApiService apiService = component.getService();

        final String token = credentials.getToken();
        final String cookie = credentials.getCookie();
        final String imei = credentials.getImei();
        bazdidMissionsList = new ArrayList<>();

        dakalsList = new ArrayList<>();

        final int[] finalProgressPercentage = {0};

        int[] gooril = {-1};
        bazdidMissionDisposable = apiService
                .checkToken(token, cookie, "a")
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(preUser1 -> Observable.just(preUser1.getUser().getUserRole())
                        .flatMapIterable(Map::values)
                        .subscribe(v -> {
                            if (v.equals("authenticated user")) {
                                getBazdidMissions(progressUpdateRepair, progressUpdateReview, afterDataReceived, onFailed, apiService, token, cookie, finalProgressPercentage);

                                gooril[0] = 0;
                            } else if (gooril[0] == -1) {
                                apiService
                                        .getUser(imei)
                                        .subscribeOn(Schedulers.newThread())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe(preUser -> {
                                            Timber.d(preUser.getUser().getUserName());

                                            //Setting Credentials Preferences
                                            final String token1 = preUser.getToken();
                                            final String cookie1 = preUser.getSessionName() + "=" + preUser.getSessionId();
                                            setSharedPreferencesCredentials(token1, cookie1, imei);

                                            getBazdidMissions(progressUpdateRepair, progressUpdateReview, afterDataReceived, onFailed, apiService, token1, cookie1, finalProgressPercentage);

                                        }, throwable -> {
                                            if (throwable instanceof HttpException) {
                                                Response response = ((HttpException) throwable).response();

                                                switch (response.code()) {

                                                    case 401:

                                                        break;

                                                    case 400:


                                                        break;

                                                    case 403:
                                                        break;
                                                }

                                            } else if (throwable instanceof NoNetworkException) {
                                                Timber.d(throwable.getMessage());
                                            } else {
                                                Timber.d(throwable.getMessage());
                                            }

                                        });
                            }
                        }));

    }

    @NonNull
    private Disposable getBazdidMissions(GenericMethod<Integer> progressUpdateRepair, GenericMethod<Integer> progressUpdateReview, Function afterDataReceived, Function onFailed, RetrofitApiService apiService, String token, String cookie, int[] finalProgressPercentage) {
        return apiService.getBazdidMissions(token, cookie, "1", "0")
                .flatMap(x -> {
                    int count = (int) Math.ceil(Double.parseDouble(x.getOpCount()) / 20);
                    return Observable
                            .range(0, count)
                            .map(c -> {
                                //progressPercentage[0] = c;
                                return c * 20;
                            }).concatMap(d -> apiService.getBazdidMissions(token, cookie, "20", d.toString()));
                }).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(mission -> {
                    bazdidMissionsList.addAll(mission.getOperationList());
                    finalProgressPercentage[0] = (bazdidMissionsList.size() * 100 / Integer.valueOf(mission.getOpCount()));
                    progressUpdateRepair.fun(finalProgressPercentage[0]);
                }, throwable -> {
                    getRepairMissionsFromDataBase(progressUpdateReview, afterDataReceived, onFailed);
                    if (throwable instanceof HttpException) {

                        Response response = ((HttpException) throwable).response();

                        switch (response.code()) {

                            case 401:

                                break;

                            case 400:

                                break;

                            case 403:

                                break;
                        }

                    } else if (throwable instanceof NoNetworkException) {
                        Timber.d(throwable.getMessage());
                        onFailed.fun();
                    } else {
                        Timber.d(throwable.getMessage());
                        onFailed.fun();
                    }
                }, () -> {
                    addBazdidMissionsToDataBase();
                    getRepairMissionsFromDataBase(progressUpdateReview, afterDataReceived, onFailed);
                });
    }

    private void getRepairMissionsFromDataBase(GenericMethod<Integer> progressUpdateReview,
                                               com.controladad.boutia_pms.view_models.Function afterDataReceived,
                                               com.controladad.boutia_pms.view_models.Function onFailed) {
        getSharedPreferences();
        RetrofitApiService apiService = component.getService();
        final String token = credentials.getToken();
        final String cookie = credentials.getCookie();
        final String imei = credentials.getImei();
        repairLists = new ArrayList<>();
        final int[] finalProgressPercentage = {0};


        int[] gooril = {-1};
        repairMissionDisposable =
                apiService
                        .checkToken(token, cookie, "a")
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(preUser1 -> Observable.just(preUser1.getUser().getUserRole())
                                .flatMapIterable(Map::values)
                                .subscribe(v -> {
                                    if (v.equals("authenticated user")) {
                                        getRepairListX(progressUpdateReview, afterDataReceived, onFailed, apiService, token, cookie, finalProgressPercentage);

                                        gooril[0] = 0;
                                    } else if (gooril[0] == -1) {
                                        apiService
                                                .getUser(imei)
                                                .subscribeOn(Schedulers.newThread())
                                                .observeOn(AndroidSchedulers.mainThread())
                                                .subscribe(preUser -> {
                                                    Timber.d(preUser.getUser().getUserName());

                                                    //Setting Credentials Preferences
                                                    final String token1 = preUser.getToken();
                                                    final String cookie1 = preUser.getSessionName() + "=" + preUser.getSessionId();
                                                    setSharedPreferencesCredentials(token1, cookie1, imei);

                                                    getRepairListX(progressUpdateReview, afterDataReceived, onFailed, apiService, token1, cookie1, finalProgressPercentage);

                                                }, throwable -> {
                                                    if (throwable instanceof HttpException) {
                                                        Response response = ((HttpException) throwable).response();

                                                        switch (response.code()) {

                                                            case 401:

                                                                break;

                                                            case 400:


                                                                break;

                                                            case 403:
                                                                break;
                                                        }

                                                    } else if (throwable instanceof NoNetworkException) {
                                                        Timber.d(throwable.getMessage());
                                                    } else {
                                                        Timber.d(throwable.getMessage());
                                                    }

                                                });
                                    }
                                }));

    }

    private Disposable getRepairListX(GenericMethod<Integer> progressUpdateReview, Function afterDataReceived, Function onFailed, RetrofitApiService apiService, String token, String cookie, int[] finalProgressPercentage) {
        return apiService.getRepairListsX(token, token, "1", "0")
                .flatMap(x -> {
                    int count = (int) Math.ceil(Double.parseDouble(x.getRepairOperationCount()) / 20);
                    return Observable
                            .range(0, count)
                            .map(c -> c * 20)
                            .concatMap(d -> apiService.getRepairListsX(token, cookie, "20", d.toString()));
                }).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(mission -> {
                    repairLists.addAll(mission.getRepairList());
                    finalProgressPercentage[0] = (repairLists.size() * 100 / Integer.valueOf(mission.getRepairOperationCount()));
                    progressUpdateReview.fun(finalProgressPercentage[0]);
                }, throwable -> {

                    if (throwable instanceof HttpException) {

                        Response response = ((HttpException) throwable).response();

                        switch (response.code()) {

                            case 401:

                                break;

                            case 400:

                                break;

                            case 403:

                                break;
                        }

                    } else if (throwable instanceof NoNetworkException) {
                        Timber.d(throwable.getMessage());
                        onFailed.fun();
                    } else {
                        Timber.d(throwable.getMessage());
                        onFailed.fun();
                    }
                }, () -> {
                    addTamirMissionsToDataBase();
                    afterDataReceived.fun();
                });
    }


    public void getCurrentDateAndUpdateData(GenericMethod<Integer> progressUpdateRepair,
                                            GenericMethod<Integer> progressUpdateReview,
                                            com.controladad.boutia_pms.view_models.Function afterDataReceived,
                                            com.controladad.boutia_pms.view_models.Function onFailed,
                                            Function onTimeNotValid) {

        Disposable disposable = component.getService().getTime()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(mission -> {
                    Long currentTime = Long.valueOf(mission.get(1));
                    currentTime *= 1000;
                    Long currentDeviceTime = Calendar.getInstance().getTimeInMillis();
                    int tenMinute = 5 * 60 * 60 * 1000;
                    if ((currentDeviceTime + tenMinute) > currentTime && (currentDeviceTime - tenMinute) < currentTime)
                        getMissionsList(progressUpdateRepair,
                                progressUpdateReview,
                                afterDataReceived,
                                onFailed);
                    else
                        onTimeNotValid.fun();


                }, throwable -> {

                    if (throwable instanceof HttpException) {

                        Response response = ((HttpException) throwable).response();

                        switch (response.code()) {

                            case 401:

                                break;

                            case 400:

                                break;

                            case 403:

                                break;
                        }

                    } else if (throwable instanceof NoNetworkException) {
                        Timber.d(throwable.getMessage());
                        onFailed.fun();
                    } else {
                        Timber.d(throwable.getMessage());
                        onFailed.fun();
                    }
                }, () -> {

                });
    }

    private void addBazdidMissionsToDataBase() {
        Disposable disposable = Flowable.fromCallable(() -> {
            ArrayList<Mission_Entity> modelArrayList = new ArrayList<>();
            ArrayList<OprLinesEntity> oprLinesEntities = new ArrayList<>();

            //Saving some records
            for (RetrofitModels.Missions m : bazdidMissionsList) {

                String dispCode = "";

                for (RetrofitModels.MissionLine line : m.getMissionLine()) {
                    oprLinesEntities.add(new OprLinesEntity(Integer.valueOf(m.getNid()), line.getLineName(), line.getLineVoltage(), line.getLineCode(), line.getLineWidth()));
                    if (!dispCode.equals(""))
                        dispCode = dispCode + "  |  " + line.getLineCode();
                    else
                        dispCode = line.getLineCode();
                }

                Mission_Entity mission_entity = new Mission_Entity(Integer.valueOf(m.getNid()),
                        m.getMissionsTitle(), m.getMissionType().getType(), convertToPersianDate(m.getMissionStartDate()),
                        convertToPersianDate(m.getMissionEndDate()), m.getMissionOperationGroup().getGroupName(), dispCode);

                modelArrayList.add(mission_entity);
            }
            //inserting records
            BoutiaApplication.INSTANCE.getDb().getMissionDao().deleteAllMissions();
            BoutiaApplication.INSTANCE.getDb().getMissionDao().insertMission(modelArrayList);
            BoutiaApplication.INSTANCE.getDb().getOprLineDao().deleteAllOprLines();
            BoutiaApplication.INSTANCE.getDb().getOprLineDao().insertOprLines(oprLinesEntities);
            return false;
        }).subscribeOn(Schedulers.io())
                .subscribe((s) -> {
                }, throwable -> {
                    Timber.d("Error Saving Bazdid Mission to database");
                });
    }


    private void addTamirMissionsToDataBase() {
        Disposable disposable = Flowable.fromCallable(() -> {
            ArrayList<Mission_Tamirat_Entity> modelArrayList = new ArrayList<>();
            //Saving some records
            for (RetrofitModels.RepairList m : repairLists) {

                String dispCode = "";
                String tamirName = "";
                for (RetrofitModels.MultipleL2 line : m.getRepairMissionDetails().getLineDescription().getMultipleL2()) {

                    if (!Objects.equals(dispCode,""))
                        dispCode = dispCode + "  |  " + line.getCode();
                    else
                        dispCode = line.getCode();



                }
                if (!Objects.equals(m.getRepairMissionDetails().getLineDescription().getTitle(),""))
                    tamirName = m.getRepairMissionDetails().getLineDescription().getTitle();
                else
                    tamirName = "بدون نام";

                Mission_Tamirat_Entity mission_tamirat_entity = new Mission_Tamirat_Entity
                        (Integer.valueOf(m.getRepairMissionDetails().getMid()),
                                tamirName,
                                dispCode,
                                m.getRepairMissionDetails().getType().getName(),
                                convertToPersianDate(m.getRepairMissionDetails().getLineDate().getStartDate()),
                                convertToPersianDate(m.getRepairMissionDetails().getLineDate().getEndDate()),
                                m.getRepairMissionDetails().getGroup().getGroupName());
                //m.getRepairMissionDetails().getLineDescription().getTitle() changed with tamirName
                //m.getRepairMissionDetails().getLineDescription().getCode() changed with dispCode

                modelArrayList.add(mission_tamirat_entity);
            }
            //inserting records
            BoutiaApplication.INSTANCE.getDb().getMissionTamiratDao().deleteAllTamiratMissions();
            BoutiaApplication.INSTANCE.getDb().getMissionTamiratDao().insertMissionTamirat(modelArrayList);
            return false;
        }).subscribeOn(Schedulers.io())
                .subscribe((s) -> {
                }, throwable -> {
                    Timber.d("Error Saving Tamir Mission to database");
                });
    }

    private String convertToPersianDate(String inputDate) {

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = null;
        try {
            date = format.parse(inputDate);
            PersianDate pdate = new PersianDate(date);
            PersianDateFormat persianDateFormat = new PersianDateFormat("Y/m/d");
            return persianDateFormat.format(pdate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return inputDate;

    }

    private void getSharedPreferences() {

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(BoutiaApplication.INSTANCE);
        Gson gson = new GsonBuilder().create();

        if (credentials == null) {
            String value = preferences.getString(CREDENTIALS_PREFERENCES, "");

            if (value.equals("")) {
                credentials = null;
            } else {
                credentials = gson.fromJson(value, SharedPreferencesModels.SharedPreferencesCredentials.class);
            }
        }

    }

    private void setSharedPreferencesCredentials(String token, String cookie, String imei) {

        SharedPreferencesModels.SharedPreferencesCredentials sharedPreferencesCredentials = new SharedPreferencesModels.SharedPreferencesCredentials(token, cookie, imei);
        Gson gson = new GsonBuilder().create();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(BoutiaApplication.INSTANCE);
        preferences.edit().putString(CREDENTIALS_PREFERENCES, gson.toJson(sharedPreferencesCredentials)).apply();
    }
}

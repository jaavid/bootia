package com.controladad.boutia_pms.models;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import androidx.core.app.NotificationManagerCompat;
import android.view.View;

import com.controladad.boutia_pms.R;
import com.controladad.boutia_pms.dagger.AppComponents;
import com.controladad.boutia_pms.dagger.ContextModule;
import com.controladad.boutia_pms.dagger.DaggerAppComponents;
import com.controladad.boutia_pms.dagger.NotificationModule;
import com.controladad.boutia_pms.models.data_models.ProjectModel;
import com.controladad.boutia_pms.models.database.BazdidYaTamirEntity;
import com.controladad.boutia_pms.models.database.BoutiaDataBase;
import com.controladad.boutia_pms.models.database.ReportTamir;
import com.controladad.boutia_pms.models.database.ReportTamirOtherType;
import com.controladad.boutia_pms.models.database.TamirDakalEntity;
import com.controladad.boutia_pms.models.database.TrackEntity;
import com.controladad.boutia_pms.models.retrofit_models.RetrofitModels;
import com.controladad.boutia_pms.models.shared_preferences_models.SharedPreferencesModels;
import com.controladad.boutia_pms.network.UploadService;
import com.controladad.boutia_pms.network.network_check.NoNetworkException;
import com.controladad.boutia_pms.network.retrofit.RetrofitApiService;
import com.controladad.boutia_pms.utility.BoutiaApplication;
import com.controladad.boutia_pms.utility.Constants;
import com.controladad.boutia_pms.utility.MimeUtils;
import com.controladad.boutia_pms.view_models.Function;
import com.controladad.boutia_pms.view_models.GenericMethod;
import com.controladad.boutia_pms.view_models.items_view_models.GeneralDataModel;
import com.controladad.boutia_pms.view_models.items_view_models.RepairReportDataModel;
import com.controladad.boutia_pms.view_models.items_view_models.ReportItemDataModel;
import com.controladad.boutia_pms.view_models.items_view_models.TrackReportItemDataModel;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import lombok.Getter;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.HttpException;
import retrofit2.Response;
import saman.zamani.persiandate.PersianDate;
import timber.log.Timber;

import static com.controladad.boutia_pms.utility.Constants.CREDENTIALS_PREFERENCES;

public class ReportModel {

    private BoutiaDataBase dataBase = BoutiaApplication.INSTANCE.getDb();
    private ReportItemDataModel reportItemDataModel;
    private RepairReportDataModel repairReportDataModel;
    private SharedPreferencesModels.SharedPreferencesCredentials credentials;
    private Disposable disposableDatabase;
    private Disposable disposableNetwork;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private List<String> sendingTracksIds = new ArrayList<>();
    private List<String> sendingMissionIds = new ArrayList<>();
    private List<Integer> sendingReportTamirIds = new ArrayList<>();
    private RetrofitApiService retrofitApiService;
    private Context context;
    private UploadService uploadService;
    @Getter
    private List<String> trackListTracker = new ArrayList<>();
    @Getter
    private List<String> missionListTracker = new ArrayList<>();
    @Getter
    private List<String> repairMissionListTracker = new ArrayList<>();
    private int prepareDataCounter = 0;
    private int trackCounter = 0;
    private int repairReportCounter = 0;
    private int errorTracker = 0;


    public ReportModel(UploadService uploadService, Context context, RetrofitApiService retrofitApiService) {

        this.retrofitApiService = retrofitApiService;
        this.context = context;
        this.uploadService = uploadService;
    }

    public ReportModel() {
        context = BoutiaApplication.INSTANCE;
    }

    public void getDataFromDatabase(GenericMethod<GeneralDataModel> method, Function whenListIsEmpty) {
        class HelperClass {
            private int emptyListCounter = 0;
        }
        final HelperClass helperClass = new HelperClass();
        disposableDatabase = dataBase
                .getTrackDao().getTrackCountWithNameForBazdid()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(trackCountsList -> {

                            if (trackCountsList.size() == 0)
                                helperClass.emptyListCounter++;
                            if (helperClass.emptyListCounter == 4)
                                whenListIsEmpty.fun();

                            for (int i = 0; i < trackCountsList.size(); i++) {

                                TrackReportItemDataModel trackReportItemDataModel = new TrackReportItemDataModel(trackCountsList.get(i).getMissionName(),
                                        trackCountsList.get(i).getMIdCount(), "آماده ارسال",trackCountsList.get(i).getDate());
                                trackReportItemDataModel.setState(Constants.READY_STATE);
                                trackReportItemDataModel.setMid(trackCountsList.get(i).getMId());
                                if (sendingTracksIds.contains(trackCountsList.get(i).getMId())) {
                                    trackReportItemDataModel.setSending(true);
                                    trackReportItemDataModel.setReportTrackButtonVisibility(View.GONE);
                                }
                                method.fun(trackReportItemDataModel);
                            }
                        }
                        , throwable -> {
                            Timber.d(throwable.getMessage());
                        },
                        () ->
                                Timber.d("getTrackKountWithName")
                );


        disposableDatabase = dataBase
                .getTrackDao().getTrackCountWithNameForTamir()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(trackCountsList -> {

                            if (trackCountsList.size() == 0)
                                helperClass.emptyListCounter++;
                            if (helperClass.emptyListCounter == 4)
                                whenListIsEmpty.fun();

                            for (int i = 0; i < trackCountsList.size(); i++) {
                                Timber.d(trackCountsList.get(i).getMIdCount());

                                // if(Integer.valueOf(trackCountsList.get(i).getMIdCount()) - Integer.valueOf(trackCountList.get(i).getMIdCount()) > 0){
                                TrackReportItemDataModel trackReportItemDataModel = new TrackReportItemDataModel(trackCountsList.get(i).getMissionName(),
                                        trackCountsList.get(i).getMIdCount(), "آماده ارسال", trackCountsList.get(i).getDate());
                                trackReportItemDataModel.setState(Constants.READY_STATE);
                                trackReportItemDataModel.setMid(trackCountsList.get(i).getMId());
                                if (sendingTracksIds.contains(trackCountsList.get(i).getMId())) {
                                    trackReportItemDataModel.setSending(true);
                                    trackReportItemDataModel.setReportTrackButtonVisibility(View.GONE);
                                }
                                method.fun(trackReportItemDataModel);
                            }
                        }
                        , throwable -> {
                            Timber.d(throwable.getMessage());
                        },
                        () ->
                                Timber.d("getTrackKountWithName")
                );

        disposableDatabase = dataBase
                .getTrackDao().getTrackCountWithNameForTamirOtherType()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(trackCountsList -> {

                            if (trackCountsList.size() == 0)
                                helperClass.emptyListCounter++;
                            if (helperClass.emptyListCounter == 4)
                                whenListIsEmpty.fun();

                            for (int i = 0; i < trackCountsList.size(); i++) {

                                TrackReportItemDataModel trackReportItemDataModel = new
                                        TrackReportItemDataModel(trackCountsList.get(i).getMissionName() + ", " +
                                        repairTypeConvertToPersian(trackCountsList.get(i).getType()), trackCountsList.get(i).getMIdCount(), "آماده ارسال",
                                        trackCountsList.get(i).getDate());

                                trackReportItemDataModel.setState(Constants.READY_STATE);
                                trackReportItemDataModel.setMid(trackCountsList.get(i).getMId());
                                if (sendingTracksIds.contains(trackCountsList.get(i).getMId())) {
                                    trackReportItemDataModel.setSending(true);
                                    trackReportItemDataModel.setReportTrackButtonVisibility(View.GONE);
                                }
                                method.fun(trackReportItemDataModel);
                            }
                        }
                        , throwable -> {
                            Timber.d(throwable.getMessage());
                        },
                        () ->
                                Timber.d("getTrackKountWithName")
                );


        ArrayList<BazdidYaTamirEntity> bazdidYaTamirEntityArrayList = new ArrayList<>();

        disposableDatabase = dataBase
                .getBazdidYaTamir()
                .getAllBazdidYaTamir()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(bazdidYaTamirEntities -> {
                            if (bazdidYaTamirEntities.isEmpty())
                                helperClass.emptyListCounter++;
                            if (helperClass.emptyListCounter == 4)
                                whenListIsEmpty.fun();
                            bazdidYaTamirEntityArrayList.addAll(bazdidYaTamirEntities);
                            for (BazdidYaTamirEntity bazdidYaTamirEntity : bazdidYaTamirEntityArrayList) {
                                Gson gson = new GsonBuilder().create();
                                ProjectModel.DakalFieldes dakalFieldes = gson.fromJson(bazdidYaTamirEntity.getMFieldDakal(), ProjectModel.DakalFieldes.class);
                                if (!sendingMissionIds.contains(bazdidYaTamirEntity.getMDate())) {
                                    reportItemDataModel = new ReportItemDataModel(dakalFieldes.getTowerNumber().getNumber(),
                                            dakalFieldes.getMissionName(), dakalFieldes.getCheckupType().getType(), bazdidYaTamirEntity.getMDate(),
                                            "آماده ارسال");
                                } else {
                                    reportItemDataModel = new ReportItemDataModel(dakalFieldes.getTowerNumber().getNumber(),
                                            dakalFieldes.getMissionName(), dakalFieldes.getCheckupType().getType(), bazdidYaTamirEntity.getMDate(),
                                            "آماده ارسال");
                                    reportItemDataModel.setSending(true);
                                    reportItemDataModel.setReportButtonVisibility(View.GONE);
                                }
                                reportItemDataModel.setState(Constants.READY_STATE);
                                reportItemDataModel.setMid(bazdidYaTamirEntity.getMId());
                                //   reportVM.changeTextVisibility(true);
                                method.fun(reportItemDataModel);
                            }
                        }
                        , throwable -> {
                            Timber.d(throwable.getMessage());
                        });


        ArrayList<ReportTamir> reportTamirArrayList = new ArrayList<>();


        disposableDatabase = dataBase
                .getTamirDakalDao()
                .getReportTamirWithName()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(tamirDakalEntities -> {
                            if (tamirDakalEntities.isEmpty())
                                helperClass.emptyListCounter++;
                            if (helperClass.emptyListCounter == 4)
                                whenListIsEmpty.fun();
                            reportTamirArrayList.addAll(tamirDakalEntities);
                            for (ReportTamir reportTamir : reportTamirArrayList) {
                                if (!sendingReportTamirIds.contains(Integer.valueOf(reportTamir.getNId()))) {
                                    repairReportDataModel = new RepairReportDataModel(reportTamir.getDakalNumber(),
                                            reportTamir.getTitle(), "روتین", reportTamir.getDate(),
                                            "آماده ارسال", reportTamir.getNId(), true);
                                } else {
                                    repairReportDataModel = new RepairReportDataModel(reportTamir.getDakalNumber(),
                                            reportTamir.getTitle(), "روتین", reportTamir.getDate(),
                                            "آماده ارسال", reportTamir.getNId(), true);
                                    repairReportDataModel.setSending(true);
                                    repairReportDataModel.setReportButtonVisibility(View.GONE);
                                }
                                repairReportDataModel.setState(Constants.READY_STATE);

                                //   reportVM.changeTextVisibility(true);
                                method.fun(repairReportDataModel);

                                //for changing button text and icons
                            }
                        }
                        , throwable -> {
                            Timber.d(throwable.getMessage());
                        });


        ArrayList<ReportTamirOtherType> reportTamirOtherArrayList = new ArrayList<>();


        disposableDatabase = dataBase
                .getOtherRepairTypeDao()
                .getReportTamirOtherWithName()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(tamirDakalEntities -> {
                            if (tamirDakalEntities.isEmpty())
                                helperClass.emptyListCounter++;
                            if (helperClass.emptyListCounter == 4)
                                whenListIsEmpty.fun();
                            reportTamirOtherArrayList.addAll(tamirDakalEntities);
                            for (ReportTamirOtherType reportTamir : reportTamirOtherArrayList) {
                                if (!sendingReportTamirIds.contains(Integer.valueOf(reportTamir.getNId()))) {
                                    repairReportDataModel = new RepairReportDataModel(null,
                                            String.format("%1$s - %2$s", reportTamir.getTitle(), repairTypeConvertToPersian(reportTamir.getRepairType())),
                                            reportTamir.getType(), reportTamir.getDate(),
                                            "آماده ارسال", reportTamir.getNId(), false);
                                } else {
                                    repairReportDataModel = new RepairReportDataModel(null,
                                            String.format("%1$s - %2$s", reportTamir.getTitle(), repairTypeConvertToPersian(reportTamir.getRepairType())),
                                            reportTamir.getType(), reportTamir.getDate(),
                                            "آماده ارسال", reportTamir.getNId(), false);
                                    repairReportDataModel.setSending(true);
                                    repairReportDataModel.setReportButtonVisibility(View.GONE);
                                }
                                repairReportDataModel.setState(Constants.READY_STATE);

                                //   reportVM.changeTextVisibility(true);
                                method.fun(repairReportDataModel);

                                //for changing button text and icons
                            }
                        }
                        , throwable -> {
                            Timber.d(throwable.getMessage());
                        });


        compositeDisposable.add(disposableDatabase);

    }

    public void sendTrack(String mid, CallBackMethod<String> callBackMethod) {

        getSharedPreferences();
        final String token = credentials.getToken();
        final String cookie = credentials.getCookie();
        Timber.d("sendTrack():");
        trackCounter++;
        trackListTracker.add(mid);

        disposableDatabase = Flowable.fromCallable(() -> {
            List<TrackEntity> trackEntities = dataBase.getTrackDao().getTrack(Integer.valueOf(mid));
            for (TrackEntity trackEntity : trackEntities) {
                if (!sendingTracksIds.contains(mid))
                    sendingTracksIds.add(mid);
                dataBase.getTrackDao().updateTrackUploadStatusToUploading(trackEntity.getNId());
                Timber.d(trackEntity.getMDate());
                String mDate = trackEntity.getMDate();
                String[] dateAndTime = mDate.split("[ \t]");
                String[] datePrepration = dateAndTime[0].split("/");

                String dateForSending = datePrepration[1] + "/" + datePrepration[0] + "/" + datePrepration[2];

                Map<String, String> map = new HashMap<>();

                map.put("title", "track test");
                map.put("type", "track");
                map.put("field_related_operation[und]", String.valueOf(trackEntity.getMId()));
                map.put("field_location[und][0][locpick][user_latitude]", trackEntity.getMLat());
                map.put("field_location[und][0][locpick][user_longitude]", trackEntity.getMLng());
                /**
                 *
                 * This is the preveious type of sending time for tracks
                 */
//                map.put("field_track_time[und][0][value][date]", dateForSending + " - " + dateAndTime[2]);

                if (trackEntity.getMYear() != null) {
                    map.put("field_track_time[und][0][value][year]", trackEntity.getMYear());
                    map.put("field_track_time[und][0][value][month]", trackEntity.getMMonth());
                    map.put("field_track_time[und][0][value][day]", trackEntity.getMDay());
                    map.put("field_track_time[und][0][value][hour]", trackEntity.getMHour());

                    if (trackEntity.getMMinute().length() < 2) {
                        map.put("field_track_time[und][0][value][minute]", "0" + trackEntity.getMMinute());
                    } else {

                        map.put("field_track_time[und][0][value][minute]", trackEntity.getMMinute());
                    }

                    if (trackEntity.getMSecond().length() < 2) {
                        map.put("field_track_time[und][0][value][second]", "0" + trackEntity.getMSecond());
                    } else {

                        map.put("field_track_time[und][0][value][second]", trackEntity.getMSecond());
                    }
                }


                String type = trackEntity.getMType();
                if (Objects.equals(type, Constants.REVIEW))
                    type = Constants.BAZDID;
                map.put("field_track_type[und]", type);

                Disposable disposable2 = retrofitApiService
                        .sendCordinations(token, cookie, map)
                        .subscribe(responseBody -> Timber.d(responseBody.string()), throwable -> {
                                    Flowable.fromCallable(() -> dataBase.getTrackDao())
                                            .subscribe(trackDao -> {
                                                        trackDao.updateTrackUploadStatusToNotUpload(trackEntity.getNId());
                                                    }
                                                    , throwable1 -> {
                                                    });
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
//                                        disposableDatabase = Flowable.fromCallable(() -> false)
//                                                .subscribeOn(AndroidSchedulers.mainThread())
//                                                .subscribe(s -> reportVM.onNoNetworkException());
                                    } else {
                                        Timber.d(throwable.getMessage());
//                                        disposableDatabase = Flowable.fromCallable(() -> false)
//                                                .subscribeOn(AndroidSchedulers.mainThread())
//                                                .subscribe(s -> reportVM.onNoNetworkException());
                                    }
                                }, () -> Flowable.fromCallable(() -> dataBase.getTrackDao())
                                        .subscribe(trackDao -> trackDao.deleteTrack(trackEntity.getNId())
                                                , throwable -> {
                                            Timber.d(throwable.getMessage());
                                                })
                        );
            }
            return true;
        }).subscribeOn(Schedulers.io())
                .subscribe((s) -> {
                        }, throwable -> {
                            errorTracker++;
                            trackCounter--;
                            trackListTracker.remove(mid);
                            checkToStopService();
                            callBackMethod.callBackIfFailed(mid, false);
                            sendingTracksIds.remove(mid);
                        },
                        () -> disposableDatabase = Observable.fromCallable(
                                () -> dataBase.getTrackDao()
                        )
                                .map(b -> b.getTrack(Integer.valueOf(mid))
                                        .isEmpty())
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(successful -> {
                                    if (successful) {
                                        callBackMethod.callBackIfSuccessFull(mid, false);
                                        trackCounter--;
                                        trackListTracker.remove(mid);
                                        checkToStopService();
                                    } else {
                                        sendingTracksIds.remove(mid);
                                        errorTracker++;
                                        callBackMethod.callBackIfFailed(mid, false);
                                        trackCounter--;
                                        trackListTracker.remove(mid);
                                        checkToStopService();
                                    }
                                }, throwable -> {
                                    callBackMethod.callBackIfFailed(mid, false);
                                    trackCounter--;
                                    errorTracker++;
                                    trackListTracker.remove(mid);
                                    checkToStopService();
                                }));

        compositeDisposable.add(disposableDatabase);

    }

    public void prepareData(int mid, String mDate, CallBackMethod<String> callBackMethod) {

        getSharedPreferences();
        final String token = credentials.getToken();
        final String cookie = credentials.getCookie();

        disposableDatabase = dataBase.getBazdidYaTamir()
                .getBazdidYaTamirNotUploadByMid(mid, mDate)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(bazdidYaTamirEntity -> {
                    Gson gson = new GsonBuilder().create();
                    Map<String, String> data = new HashMap<>();

                    String[] dateAndTime = mDate.split("[ \t]");


                    ProjectModel.DakalFieldes dakalFieldes = gson.fromJson(bazdidYaTamirEntity.getMFieldDakal(), ProjectModel.DakalFieldes.class);
                    ProjectModel.Foundation foundation = gson.fromJson(bazdidYaTamirEntity.getMFondasion(), ProjectModel.Foundation.class);
                    ProjectModel.EarthWire earthWire = gson.fromJson(bazdidYaTamirEntity.getMSimZamin(), ProjectModel.EarthWire.class);
                    ProjectModel.Panel panel = gson.fromJson(bazdidYaTamirEntity.getMTablo(), ProjectModel.Panel.class);
                    ProjectModel.BoltsAndNuts boltsAndNuts = gson.fromJson(bazdidYaTamirEntity.getMPich(), ProjectModel.BoltsAndNuts.class);
                    ProjectModel.StairBolts stairBolts = gson.fromJson(bazdidYaTamirEntity.getMPichPelle(), ProjectModel.StairBolts.class);
                    ProjectModel.Thorn thorn = gson.fromJson(bazdidYaTamirEntity.getMKhaar(), ProjectModel.Thorn.class);
                    ProjectModel.Plate plate = gson.fromJson(bazdidYaTamirEntity.getMPlate(), ProjectModel.Plate.class);
                    ProjectModel.Corner corner = gson.fromJson(bazdidYaTamirEntity.getMNabshi(), ProjectModel.Corner.class);
                    ProjectModel.FloodCondition flood = gson.fromJson(bazdidYaTamirEntity.getMSeil(), ProjectModel.FloodCondition.class);
                    Type hadihaType = new TypeToken<ArrayList<ArrayList<ProjectModel.HadiHayePhaseVaMolhaghat>>>() {
                    }.getType();
                    List<List<ProjectModel.HadiHayePhaseVaMolhaghat>> hadi = gson.fromJson(bazdidYaTamirEntity.getMHadi(), hadihaType);
                    Type yaraghType = new TypeToken<ArrayList<ProjectModel.Fittings>>() {
                    }.getType();
                    List<ProjectModel.Fittings> fittings = gson.fromJson(bazdidYaTamirEntity.getMYaragh(), yaraghType);
                    Type zanjireMaghareType = new TypeToken<ArrayList<ProjectModel.IsolationChains>>() {
                    }.getType();
                    List<ProjectModel.IsolationChains> isolationChains = gson.fromJson(bazdidYaTamirEntity.getMZanjireMaghare(), zanjireMaghareType);
                    ProjectModel.SimeMohafezVaMolhaghat simeMohafezVaMolhaghat = gson.fromJson(bazdidYaTamirEntity.getMSimMohafez(), ProjectModel.SimeMohafezVaMolhaghat.class);
                    ProjectModel.LaneParande laneParande = gson.fromJson(bazdidYaTamirEntity.getMLaneParande(), ProjectModel.LaneParande.class);
                    ProjectModel.AshiaEzafe ashiaEzafe = gson.fromJson(bazdidYaTamirEntity.getMAshyaEzafe(), ProjectModel.AshiaEzafe.class);
                    ProjectModel.KhamooshKardaneKhat khamooshKardaneKhat = gson.fromJson(bazdidYaTamirEntity.getMKhamoshiKhat(), ProjectModel.KhamooshKardaneKhat.class);
                    ProjectModel.DakalBohrani dakalBohrani = gson.fromJson(bazdidYaTamirEntity.getMDakalBohrani(), ProjectModel.DakalBohrani.class);
                    ProjectModel.TaghatoBaJadeAsli taghatoBaJadeAsli = gson.fromJson(bazdidYaTamirEntity.getMJadeAsli(), ProjectModel.TaghatoBaJadeAsli.class);
                    ProjectModel.Mavne mavne = gson.fromJson(bazdidYaTamirEntity.getMMavane(), ProjectModel.Mavne.class);
                    ProjectModel.Ensheab ensheab = gson.fromJson(bazdidYaTamirEntity.getMEnsheab(), ProjectModel.Ensheab.class);
                    ProjectModel.TaghatoBa20k taghatoBa20k = gson.fromJson(bazdidYaTamirEntity.getMTaghatoBaBist(), ProjectModel.TaghatoBa20k.class);
                    ProjectModel.VaziateHarim vaziateHarim = gson.fromJson(bazdidYaTamirEntity.getMHarim(), ProjectModel.VaziateHarim.class);
                    ProjectModel.VaziateMasir vaziateMasir = gson.fromJson(bazdidYaTamirEntity.getMMasir(), ProjectModel.VaziateMasir.class);
                    ProjectModel.LoolePressMiani loolePressMiani = gson.fromJson(bazdidYaTamirEntity.getMLoole(), ProjectModel.LoolePressMiani.class);
                    ProjectModel.Goy goyEkhtar = gson.fromJson(bazdidYaTamirEntity.getMGoyEkhtar(), ProjectModel.Goy.class);
                    ProjectModel.Jooshkari jooshkari = gson.fromJson(bazdidYaTamirEntity.getMJoshkari(),ProjectModel.Jooshkari.class);
                    String lat = gson.fromJson(bazdidYaTamirEntity.getMLat(), String.class);
                    String lng = gson.fromJson(bazdidYaTamirEntity.getMLng(), String.class);
                    Type imageListType = new TypeToken<ArrayList<String>>() {
                    }.getType();
                    List<String> imagePathList = gson.fromJson(bazdidYaTamirEntity.getMImagesPath(), imageListType);
                    String scanType = gson.fromJson(bazdidYaTamirEntity.getMScanType(), String.class);


                    requestMustHaves(mid, data, dateAndTime, dakalFieldes, lat, lng, scanType);

                    //First Page
                    if (dakalFieldes != null)
                        dakalsField(data, dakalFieldes);

                    if (foundation != null)
                        foundationField(data, foundation);

                    if (earthWire != null)
                        simZaminField(data, earthWire);

                    if (panel != null)
                        tabloField(data, panel);

                    if (boltsAndNuts != null)
                        pichoMohreField(data, boltsAndNuts);

                    if (stairBolts != null)
                        stairBoltsField(data, stairBolts);

                    if (thorn != null)
                        thornField(data, thorn);

                    if (plate != null)
                        plateField(data, plate);

                    if (corner != null)
                        nabshiField(data, corner);

                    if (flood != null)
                        seilField(data, flood);

                    //Second Page

                    if (hadi != null)
                        hadiAndPhaseField(data, hadi);

                    if (fittings != null)
                        yaraghFields(data, fittings);

                    if (isolationChains != null)
                        zanjireVaMaghareField(data, isolationChains);

                    //Third Page

                    if (simeMohafezVaMolhaghat != null)
                        simeMohafezField(data, simeMohafezVaMolhaghat);

                    if (laneParande != null)
                        laneParandeField(data, laneParande);

                    if (ashiaEzafe != null)
                        ashiaEzafeField(data, ashiaEzafe);

                    if (khamooshKardaneKhat != null)
                        khamooshiKhatField(data, khamooshKardaneKhat);

                    if (dakalBohrani != null)
                        dakalBohraniField(data, dakalBohrani);
                    //Fourth Page

                    if (taghatoBaJadeAsli != null)
                        taghatoBaJadeAsliField(data, taghatoBaJadeAsli);

                    if (mavne != null)
                        mavaneField(data, mavne);

                    if (ensheab != null)
                        ensheabField(data, ensheab);

                    if (taghatoBa20k != null)
                        taghatoBaBistKField(data, taghatoBa20k);

                    if (vaziateHarim != null)
                        vaziateHarimField(data, vaziateHarim);

                    if (vaziateMasir != null)
                        vazyateMasirField(data, vaziateMasir);

                    if (loolePressMiani != null)
                        loolePressMianiField(data, loolePressMiani);

                    if (goyEkhtar != null)
                        goyEkhtarField(data,goyEkhtar);

                    if (jooshkari != null)
                        jooshkariField(data,jooshkari);

                    Map<String, String> dataForSending = new HashMap<>();

                    for (Map.Entry<String, String> map : data.entrySet()) {

                        if (map.getValue() != null) {
                            dataForSending.put(map.getKey(), map.getValue());
                        }

                    }

                    Timber.d("prepareDate():");
                    sendingData(mid, mDate, token, cookie, imagePathList, retrofitApiService, dataForSending, callBackMethod);

                }, throwable -> {
                    this.prepareDataCounter--;
                    checkToStopService();
                    Timber.d("prepareDate(): throwable()");
                }, () -> Timber.d("prepareDate(): onComplete()"));

        compositeDisposable.add(disposableDatabase);

    }


    private void sendingRepairData(int nid, String token, String cookie, RetrofitApiService
            apiService, Map<String, String> dataForSending, CallBackMethod<String> callBackMethod, String mDate) {

        // Adding the dakal's nid to a list to keep track
        Timber.d("sendingRepairData(): before api service");
        sendingReportTamirIds.add(nid);
        repairReportCounter++;
        this.repairMissionListTracker.add(mDate);

        disposableNetwork = apiService
                .sendData(token, cookie, dataForSending)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(responseBody -> Timber.d("sendingRepairData(): responseBode()"), throwable -> {
                    sendingReportTamirIds.remove(Integer.valueOf(nid));
                    Timber.d("SendingRepairData(): throwable()");
                    callBackMethod.callBackIfFailed(String.valueOf(mDate), true);
                    this.repairMissionListTracker.remove(mDate);
                    repairReportCounter--;
                    errorTracker++;
                    checkToStopService();
                    if (throwable instanceof HttpException) {

                        Response response = ((HttpException) throwable).response();

                        switch (response.code()) {

                            case 401:
                                Timber.d("SendingRepairData(): throwable(): response code - 401");
                                break;

                            case 400:
                                Timber.d("SendingRepairData(): throwable(): response code - 400");
                                break;

                            case 403:
                                Timber.d("SendingRepairData(): throwable(): response code - 403");
                                break;

                            default:
                                Timber.d("SendingRepairData(): throwable(): response code - " + response.code());
                                break;
                        }
                    }
                }, () -> {
                    sendingReportTamirIds.remove(Integer.valueOf(nid));
                    Timber.d("SendingRepairData(): onComplete()");
                    disposableDatabase = Flowable.fromCallable(
                            () -> {
                                Timber.d("SendingRepairData(): onComplete(): flowable(): body()");
                                dataBase.getTamirDakalDao()
                                        .deleteTamirByNid(nid);
                                return false;
                            }
                    ).subscribeOn(Schedulers.io())
                            .subscribe(t -> Timber.d("sendingRepairData(): onComplete(): flowable(): onNext():"),
                                    throwable -> Timber.d("sendingRepairData(): onComplete: flowable(): throwable()"));

                    callBackMethod.callBackIfSuccessFull(String.valueOf(mDate), true);
                    this.repairMissionListTracker.remove(mDate);
                    repairReportCounter--;
                    checkToStopService();
                });

        compositeDisposable.add(disposableNetwork);
    }


    public void sendingRepairReportOtherType(int nid, String mDate, CallBackMethod callBackMethod) {
        getSharedPreferences();
        Timber.d("sendingRepairReport(): ");
        final String token = credentials.getToken();
        final String cookie = credentials.getCookie();
        disposableDatabase = dataBase.getOtherRepairTypeDao()
                .getTamirDakalByNid(nid)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(tamirDakalEntity -> {
                            Timber.d("sendingRepairReport(): getTamirDakalDao()");
                            Map<String, String> data = new HashMap<>();

                            data.put("type", "repair2");
                            data.put("body[und][0][value]", tamirDakalEntity.getRepairDescription());
                            data.put("field_related_operation[und]", String.valueOf(tamirDakalEntity.getMId()));
                            data.put("field_location[und][0][locpick][user_latitude]", tamirDakalEntity.getLat());
                            data.put("field_location[und][0][locpick][user_longitude]", tamirDakalEntity.getLng());
                            data.put("field_r_type[und]", tamirDakalEntity.getRepairType());

                            sendingRepairOtherTypeData(nid, token, cookie, retrofitApiService, data, callBackMethod, mDate);

                        }, throwable -> {
                            repairReportCounter--;
                            checkToStopService();
                            Timber.d("repairReport(): getTamirDakalDao(): throwable()");
                        }
                );
        compositeDisposable.add(disposableDatabase);
    }

    private void sendingRepairOtherTypeData(int nid, String token, String cookie, RetrofitApiService
            apiService, Map<String, String> dataForSending, CallBackMethod<String> callBackMethod, String mDate) {

        sendingReportTamirIds.add(nid);
        repairReportCounter++;
        this.repairMissionListTracker.add(mDate);

        disposableNetwork = apiService
                .sendData(token, cookie, dataForSending)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(responseBody -> Timber.d("sendingRepairData(): responseBode()"), throwable -> {
                    sendingReportTamirIds.remove(Integer.valueOf(nid));
                    Timber.d("SendingRepairData(): throwable()");
                    callBackMethod.callBackIfFailed(String.valueOf(mDate), true);
                    this.repairMissionListTracker.remove(mDate);
                    repairReportCounter--;
                    errorTracker++;
                    checkToStopService();
                    if (throwable instanceof HttpException) {

                        Response response = ((HttpException) throwable).response();

                        switch (response.code()) {

                            case 401:
                                Timber.d("SendingRepairData(): throwable(): response code - 401");
                                break;

                            case 400:
                                Timber.d("SendingRepairData(): throwable(): response code - 400");
                                break;

                            case 403:
                                Timber.d("SendingRepairData(): throwable(): response code - 403");
                                break;

                            default:
                                Timber.d("SendingRepairData(): throwable(): response code - " + response.code());
                                break;
                        }
                    }
                }, () -> {
                    sendingReportTamirIds.remove(Integer.valueOf(nid));
                    Timber.d("SendingRepairData(): onComplete()");
                    disposableDatabase = Flowable.fromCallable(
                            () -> {
                                Timber.d("SendingRepairData(): onComplete(): flowable(): body()");
                                dataBase.getOtherRepairTypeDao()
                                        .deleteTamirByNid(nid);
                                return false;
                            }
                    ).subscribeOn(Schedulers.io())
                            .subscribe(t -> Timber.d("sendingRepairData(): onComplete(): flowable(): onNext():"),
                                    throwable -> Timber.d("sendingRepairData(): onComplete: flowable(): throwable()"));

                    callBackMethod.callBackIfSuccessFull(String.valueOf(mDate), true);
                    this.repairMissionListTracker.remove(mDate);
                    repairReportCounter--;
                    checkToStopService();
                });

        compositeDisposable.add(disposableNetwork);
    }


    public void sendingRepairReport(int nid, String mDate, CallBackMethod callBackMethod) {
        getSharedPreferences();
        Timber.d("sendingRepairReport(): ");
        final String token = credentials.getToken();
        final String cookie = credentials.getCookie();
        final String imei = credentials.getImei();
        disposableDatabase = dataBase.getTamirDakalDao()
                .getTamirDakalByNid(nid)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(tamirDakalEntity -> {
                            Timber.d("sendingRepairReport(): getTamirDakalDao()");
                            Map<String, String> data = new HashMap<>();
                            String[] dateAndTime = mDate.split("[ \t]");


                            String scanType = tamirDakalEntity.getMScanType();

                            //Any request must have
                            anyRequestMustHaveTamir(tamirDakalEntity, data, dateAndTime[1], dateAndTime[0], tamirDakalEntity.getLat(), tamirDakalEntity.getLng(), scanType);

                            //The mission it self

                            //Foundation
                            if (tamirDakalEntity.isFoundationRepaired()) {
                                data.put("field_r_foundation[und]", "1");
                            } else {
                                data.put("field_r_foundation[und]", "0");
                            }

                            if (tamirDakalEntity.getFoundationRepairedDec() != null) {
                                data.put("field_r_foundation_desc[und][0][value]", tamirDakalEntity.getFoundationRepairedDec());
                            }

                            //Tablo
                            if (tamirDakalEntity.isTabloRepaired()) {
                                data.put("field_r_tablo[und]", "1");
                            } else {
                                data.put("field_r_tablo[und]", "0");
                            }

                            if (tamirDakalEntity.getTabloRepairedDec() != null) {
                                data.put("field_r_tablo_desc[und][0][value]", tamirDakalEntity.getTabloRepairedDec());
                            }

                            //Sime Zamin
                            if (tamirDakalEntity.isSimzaminRepaired()) {
                                data.put("field_r_simzamin[und]", "1");
                            } else {
                                data.put("field_r_simzamin[und]", "0");
                            }

                            if (tamirDakalEntity.getSimzaminRepairedDec() != null) {
                                data.put("field_r_simzamin_desc[und][0][value]", tamirDakalEntity.getSimzaminRepairedDec());
                            }

                            //Pich
                            if (tamirDakalEntity.isPichRepaired()) {
                                data.put("field_r_pich[und]", "1");
                            } else {
                                data.put("field_r_pich[und]", "0");
                            }

                            if (tamirDakalEntity.getPichRepairedDec() != null) {
                                data.put("field_r_pich_desc[und][0][value]", tamirDakalEntity.getPichRepairedDec());
                            }

                            //pich Pelle
                            if (tamirDakalEntity.isPichPelleRepaired()) {
                                data.put("field_r_pich_pelle[und]", "1");
                            } else {
                                data.put("field_r_pich_pelle[und]", "0");
                            }

                            if (tamirDakalEntity.getPichPelleRepairedDec() != null) {
                                data.put("field_r_pich_pelle_desc[und][0][value]", tamirDakalEntity.getPichPelleRepairedDec());
                            }

                            //khar
                            if (tamirDakalEntity.isKharRepaired()) {
                                data.put("field_r_khar[und]", "1");
                            } else {
                                data.put("field_r_khar[und]", "0");
                            }

                            if (tamirDakalEntity.getKharRepairedDec() != null) {


                            }

                            //plate
                            if (tamirDakalEntity.isPlateRepaired()) {
                                data.put("field_r_plate[und]", "1");
                            } else {
                                data.put("field_r_plate[und]", "0");
                            }

                            if (tamirDakalEntity.getPlateRepairedDec() != null) {
                                data.put("field_r_plate_desc[und][0][value]", tamirDakalEntity.getPlateRepairedDec());
                            }

                            //nabshi
                            if (tamirDakalEntity.isNabshiRepaired()) {
                                data.put("field_r_nabshi[und]", "1");
                            } else {
                                data.put("field_r_nabshi[und]", "0");
                            }

                            if (tamirDakalEntity.getNabshiRepairedDec() != null) {
                                data.put("field_r_nabshi_desc[und][0][value]", tamirDakalEntity.getNabshiRepairedDec());
                            }

                            //seil
                            if (tamirDakalEntity.isSeilRepaired()) {
                                data.put("field_r_seil[und]", "1");
                            } else {
                                data.put("field_r_seil[und]", "0");
                            }

                            if (tamirDakalEntity.getSeilRepairedDec() != null) {
                                data.put("field_r_seil_desc[und][0][value]", tamirDakalEntity.getSeilRepairedDec());
                            }

                            //hadi
                            if (tamirDakalEntity.isHadiRepaired()) {
                                data.put("field_r_hadi[und]", "1");
                            } else {
                                data.put("field_r_hadi[und]", "0");
                            }

                            if (tamirDakalEntity.getHadiRepairedDec() != null) {
                                data.put("field_r_hadi_desc[und][0][value]", tamirDakalEntity.getHadiRepairedDec());
                            }

                            //yaragh
                            if (tamirDakalEntity.isYaraghRepaired()) {
                                data.put("field_r_yaragh[und]", "1");
                            } else {
                                data.put("field_r_yaragh[und]", "0");
                            }

                            if (tamirDakalEntity.getYaraghRepairedDec() != null) {
                                data.put("field_r_yaragh_desc[und][0][value]", tamirDakalEntity.getYaraghRepairedDec());
                            }

                            //Zanjire va molhaghat
                            if (tamirDakalEntity.isZmmRepaired()) {
                                data.put("field_r_zmm[und]", "1");
                            } else {
                                data.put("field_r_zmm[und]", "0");
                            }

                            if (tamirDakalEntity.getZmmRepairedDec() != null) {
                                data.put("field_r_zmm_desc[und][0][value]", tamirDakalEntity.getZmmRepairedDec());
                            }

                            //mohafez
                            if (tamirDakalEntity.isMohafezRepaired()) {
                                data.put("field_r_mohafez[und]", "1");
                            } else {
                                data.put("field_r_mohafez[und]", "0");
                            }

                            if (tamirDakalEntity.getMohafezRepairedDec() != null) {
                                data.put("field_r_mohafez_desc[und][0][value]", tamirDakalEntity.getMohafezRepairedDec());
                            }

                            //lane
                            if (tamirDakalEntity.isLaneRepaired()) {
                                data.put("field_r_lane[und]", "1");
                            } else {
                                data.put("field_r_lane[und]", "0");
                            }

                            if (tamirDakalEntity.getLaneRepairedDec() != null) {
                                data.put("field_r_lane_desc[und][0][value]", tamirDakalEntity.getLaneRepairedDec());
                            }

                            //ezafe
                            if (tamirDakalEntity.isEzafeRepaired()) {
                                data.put("field_r_ezafe[und]", "1");
                            } else {
                                data.put("field_r_ezafe[und]", "0");
                            }

                            if (tamirDakalEntity.getEzafeRepairedDec() != null) {
                                data.put("field_r_ezafe_desc[und][0][value]", tamirDakalEntity.getEzafeRepairedDec());
                            }

                            if (tamirDakalEntity.getStartTime() != null && !Objects.equals(tamirDakalEntity.getStartTime().trim(),"")) {

                                long startTime = Long.valueOf(tamirDakalEntity.getStartTime());

                                PersianDate pdateStart = new PersianDate(startTime);

                                data.put("field_r_timing[und][0][value][year]" , String.valueOf(pdateStart.getShYear()));
                                data.put("field_r_timing[und][0][value][month]" ,  String.valueOf(pdateStart.getShMonth()));
                                data.put("field_r_timing[und][0][value][day]" ,  String.valueOf(pdateStart.getShDay()));
                                data.put("field_r_timing[und][0][value][hour]" ,  String.valueOf(pdateStart.getHour()));

                                if (pdateStart.getMinute()>9)
                                    data.put("field_r_timing[und][0][value][minute]" ,  String.valueOf(pdateStart.getMinute()));
                                else
                                    data.put("field_r_timing[und][0][value][minute]" ,  "0" + String.valueOf(pdateStart.getMinute()));


                                if (pdateStart.getSecond()>9)
                                    data.put("field_r_timing[und][0][value][second]" ,  String.valueOf(pdateStart.getSecond()));
                                else
                                    data.put("field_r_timing[und][0][value][second]" ,  "0" + String.valueOf(pdateStart.getSecond()));

                            }

                            if (tamirDakalEntity.getEndTime() != null && !Objects.equals(tamirDakalEntity.getEndTime().trim(),"")) {

                                long finishTime = Long.valueOf(tamirDakalEntity.getEndTime());
                                PersianDate pdateEnd = new PersianDate(finishTime);

                                //data.put("field_r_timing[und][0][value2][date]" , tamirDakalEntity.getEndTime());

                                data.put("field_r_timing[und][0][value2][year]" , String.valueOf(pdateEnd.getShYear()));
                                data.put("field_r_timing[und][0][value2][month]" ,  String.valueOf(pdateEnd.getShMonth()));
                                data.put("field_r_timing[und][0][value2][day]" ,  String.valueOf(pdateEnd.getShDay()));
                                data.put("field_r_timing[und][0][value2][hour]" ,  String.valueOf(pdateEnd.getHour()));

                                if (pdateEnd.getMinute()>9)
                                    data.put("field_r_timing[und][0][value2][minute]" ,  String.valueOf(pdateEnd.getMinute()));
                                else
                                    data.put("field_r_timing[und][0][value2][minute]" ,  "0" + String.valueOf(pdateEnd.getMinute()));

                                if (pdateEnd.getSecond()>9)
                                    data.put("field_r_timing[und][0][value2][second]" ,  String.valueOf(pdateEnd.getSecond()));
                                else
                                    data.put("field_r_timing[und][0][value2][second]" ,  "0" + String.valueOf(pdateEnd.getSecond()));

                                //data.put("field_r_timing[und][0][value2][second]" ,  String.valueOf(pdateEnd.getSecond()));
                            }

                            int[] gooril = {-1};

                    sendingRepairData(nid, token, cookie, retrofitApiService, data, callBackMethod, mDate);


//                    retrofitApiService
//                                    .checkToken(token, cookie, "a")
//                                    .subscribeOn(Schedulers.newThread())
//                                    .observeOn(AndroidSchedulers.mainThread())
//                                    .subscribe(preUser1 -> Observable.just(preUser1.getUser().getUserRole())
//                                            .flatMapIterable(Map::values)
//                                            .subscribe(v -> {
//                                                if (v.equals("authenticated user")) {
//                                                    gooril[0] = 0;
//                                                } else if (gooril[0] == -1) {
//                                                    retrofitApiService
//                                                            .getUser(imei)
//                                                            .subscribeOn(Schedulers.newThread())
//                                                            .observeOn(AndroidSchedulers.mainThread())
//                                                            .subscribe(preUser -> {
//                                                                Timber.d(preUser.getUser().getUserName());
//
//                                                                //Setting Credentials Preferences
//                                                                final String token1 = preUser.getToken();
//                                                                final String cookie1 = preUser.getSessionName() + "=" + preUser.getSessionId();
//                                                                setSharedPreferencesCredentials(token1, cookie1, imei);
//
//                                                                sendingRepairData(nid, token1, cookie1, retrofitApiService, data, callBackMethod, mDate);
//
//                                                            }, throwable -> {
//                                                                if (throwable instanceof HttpException) {
//                                                                    Response response = ((HttpException) throwable).response();
//
//                                                                    switch (response.code()) {
//
//                                                                        case 401:
//
//                                                                            break;
//
//                                                                        case 400:
//
//
//                                                                            break;
//
//                                                                        case 403:
//                                                                            break;
//                                                                    }
//
//                                                                } else if (throwable instanceof NoNetworkException) {
//                                                                    Timber.d(throwable.getMessage());
//                                                                } else {
//                                                                    Timber.d(throwable.getMessage());
//                                                                }
//
//                                                            });
//                                                }
//                                            }, throwable -> {
//                                                if (throwable instanceof HttpException) {
//                                                    Response response = ((HttpException) throwable).response();
//
//                                                    switch (response.code()) {
//
//                                                        case 401:
//
//                                                            break;
//
//                                                        case 400:
//
//
//                                                            break;
//
//                                                        case 403:
//                                                            break;
//                                                    }
//
//                                                } else if (throwable instanceof NoNetworkException) {
//                                                    Timber.d(throwable.getMessage());
//                                                } else {
//                                                    Timber.d(throwable.getMessage());
//                                                }
//
//                                            }));


                        }, throwable -> {
                            repairReportCounter--;
                            checkToStopService();
                            Timber.d("repairReport(): getTamirDakalDao(): throwable()");
                        }
                );
        compositeDisposable.add(disposableDatabase);
    }

    private void anyRequestMustHaveTamir(TamirDakalEntity
                                                 tamirDakalEntity, Map<String, String> data, String time, String date, String lat, String
                                                 lng, String scanType) {
        data.put("field_r_barcode[und][0][value]", tamirDakalEntity.getBarcode());
        data.put("field_r_related_op[und]", String.valueOf(tamirDakalEntity.getMId()));
        data.put("field_r_dispatching[und][0][value]", tamirDakalEntity.getDispatching_number());
        data.put("field_r_dakal_number[und][0][value]", tamirDakalEntity.getDakalNumber());
        data.put("field_r_location[und][0][locpick][user_latitude]", lat);
        data.put("field_r_location[und][0][locpick][user_longitude]", lng);

        if (scanType != null) {
            data.put("field_barcode_scan[und]", scanType);
        }
        //End Of location chist
//        data.put("field_r_time[und][0][value][time]", time);
        //TODO finally there are three types (routine/mahdood/track) but now its just routine
        data.put("field_repair_type[und]", "routine");
        data.put("field_operation_time[und][0][field_timing][und]", tamirDakalEntity.getOperationTime());

        if (!tamirDakalEntity.getOperationTime().equals("ontime"))
            data.put("field_operation_time[und][0][field_timing_reason][und]", tamirDakalEntity.getElateTakhir());

        String[] dateForSending = date.split("/");
        data.put("field_r_time[und][0][value][date]", dateForSending[1] + "/" + dateForSending[2] + "/" + dateForSending[0] + " - " + time);
        data.put("type", "repair");
        data.put("title", "Repair Mission");
    }

    private void sendingData(int mid,
                             String mDate,
                             String token,
                             String cookie,
                             List<String> imagePathList,
                             RetrofitApiService apiService,
                             Map<String, String> dataForSending,
                             CallBackMethod<String> callBackMethod) {


        this.prepareDataCounter++;
        this.missionListTracker.add(mDate);
        sendingMissionIds.add(mDate);

        if (imagePathList != null && imagePathList.size() > 0) {

            Timber.d("SendingDataWithImage(): Ready image for sending");
            List<MultipartBody.Part> part = prepareFileParts(imagePathList);

            disposableNetwork = apiService
                    .sendPicture(token, cookie, part)
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(imageResponseModels -> {

                        Timber.d("SendingImage(): onNext(): Images Has Been Uploaded and getting the fid's ready for sending");
                        int i = 0;
                        for (RetrofitModels.ImageResponseModel image : imageResponseModels) {
                            dataForSending.put("field_images[und][" + i + "][fid]", image.getFid());
                            i = +1;
                        }

                        apiService
                                .sendData(token, cookie, dataForSending)
                                .subscribeOn(Schedulers.newThread())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(responseBody -> Timber.d("SendingDataWithImage(): onNext(): Successfully Data sent with images"),
                                        throwable -> {
                                            sendingMissionIds.remove(mDate);
                                            callBackMethod.callBackIfFailed(mDate, true);
                                            this.missionListTracker.remove(mDate);
                                            errorTracker++;
                                            this.prepareDataCounter--;
                                            checkToStopService();
                                            Timber.d("sendingDataWithImage(): onError(): Error Was Thrown");
                                            if (throwable instanceof HttpException) {

                                                Response response = ((HttpException) throwable).response();

                                                switch (response.code()) {

                                                    case 401:
                                                        Timber.d("sendingDataWithImage(): onError(): Error Was Thrown - 401");
                                                        break;

                                                    case 400:
                                                        Timber.d("sendingDataWithImage(): onError(): Error Was Thrown - 400");
                                                        break;

                                                    case 403:
                                                        Timber.d("sendingDataWithImage(): onError(): Error Was Thrown - 403");
                                                        break;

                                                    default:
                                                        Timber.d("sendingDataWithImage(): onError(): Error Was Thrown - " + response.code());
                                                        break;
                                                }
                                            }
                                        }, () -> {

                                            Timber.d("sendingDataWithImage(): onComplete");
                                            disposableDatabase = Flowable.fromCallable(
                                                    () -> {
                                                        for (String s : imagePathList) {
                                                            File file = new File(s);
                                                            file.delete();
                                                        }
                                                        dataBase.getBazdidYaTamir()
                                                                .deleteBazdidYaTamir(mid, mDate);
                                                        return false;
                                                    }
                                            ).subscribeOn(Schedulers.io())
                                                    .subscribe(is -> Timber.d("sendingDataWithImage(): onComplete(): fromCallable(): onNext()"),
                                                            throwable -> Timber.d("sendingDataWithImage(): onComplete(): fromCallable(): throwable()"));
                                            callBackMethod.callBackIfSuccessFull(mDate, true);
                                            this.missionListTracker.remove(mDate);
                                            this.prepareDataCounter--;
                                            checkToStopService();
                                        });
                    }, throwable -> {
                        sendingMissionIds.remove(mDate);
                        callBackMethod.callBackIfFailed(mDate, true);
                        Timber.d("sendingImage(): onError(): Error sending the images...");
                        this.missionListTracker.remove(mDate);
                        this.prepareDataCounter--;
                        checkToStopService();

                        if (throwable instanceof HttpException) {

                            Response response = ((HttpException) throwable).response();

                            switch (response.code()) {

                                case 401:
                                    Timber.d("sendingImage(): onError(): Error sending the images... - 401");
                                    break;

                                case 400:
                                    Timber.d("sendingImage(): onError(): Error sending the images... - 400");
                                    break;

                                case 403:
                                    Timber.d("sendingImage(): onError(): Error sending the images... - 403");
                                    break;
                                default:
                                    Timber.d("sendingImage(): onError(): Error Sending The images... - " + response.code());
                                    break;
                            }
                        }
                    });
            compositeDisposable.add(disposableNetwork);
        } else {
            Timber.d("sendingData(): before the api call");
            disposableNetwork = apiService
                    .sendData(token, cookie, dataForSending)
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(responseBody -> Timber.d("sendingData(): onNext()"),
                            throwable -> {
                                Timber.d("sendingData(): throwable()");

                                sendingMissionIds.remove(mDate);
                                callBackMethod.callBackIfFailed(mDate, true);
                                this.prepareDataCounter--;
                                errorTracker++;
                                this.missionListTracker.remove(mDate);
                                checkToStopService();
                                if (throwable instanceof HttpException) {

                                    Response response = ((HttpException) throwable).response();

                                    switch (response.code()) {

                                        case 401:
                                            Timber.d("sendingData(): throwable(): Error sending data: - 401");
                                            break;

                                        case 400:
                                            Timber.d("sendingData(): throwable(): Error sending data: - 400");
                                            break;

                                        case 403:
                                            Timber.d("sendingData(): throwable(): Error sending data: - 403");
                                            break;
                                        default:
                                            Timber.d("sendingData(): throwable(): Error sending data: - " + response.code());
                                            break;
                                    }
                                }
                            }, () -> {
                                Timber.d("sendingData(): onComplete()");


                                disposableDatabase = Flowable.fromCallable(
                                        () -> {
                                            Timber.d("sendingData(): onComplete(): fromCallable()");
                                            if (imagePathList != null)
                                                for (String s : imagePathList) {
                                                    File file = new File(s);
                                                    file.delete();
                                                }
                                            dataBase.getBazdidYaTamir()
                                                    .deleteBazdidYaTamir(mid, mDate);
                                            return false;
                                        }
                                ).subscribeOn(Schedulers.io())
                                        .subscribe(is -> Timber.d("sendingData(): onComplete(): fromCallable(): onNext()"),
                                                throwable -> Timber.d("sendingData(): onComplete(): fromCallable(): onError()"));
                                callBackMethod.callBackIfSuccessFull(mDate, true);
                                this.prepareDataCounter--;
                                this.missionListTracker.remove(mDate);
                                checkToStopService();
                            });
            compositeDisposable.add(disposableNetwork);
        }
    }

    private void checkToStopService() {
        if (prepareDataCounter == 0 && repairReportCounter == 0 && trackCounter == 0) {
            //Check for any errors
            if (errorTracker > 0) {
                AppComponents app = DaggerAppComponents
                        .builder()
                        .contextModule(new ContextModule(context))
                        .notificationModule(new NotificationModule("اشکال در ارسال دیتا"))
                        .build();

                NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(uploadService.getContext());
                notificationManagerCompat.notify(1782, app.getNotificationBuilder().build());
            }
            // Stop service
            compositeDisposable.dispose();
            uploadService.stopSelf();
        }
    }

    private void loolePressMianiField(Map<String, String> data, ProjectModel.LoolePressMiani
            loolePressMiani) {
        if (Objects.equals(loolePressMiani.getCondition(), Constants.IS_GOOD)) {

            data.put("field_loole[und][0][field_good][und]", "1");

        } else if (Objects.equals(loolePressMiani.getCondition(), Constants.IS_BAD)) {

            data.put("field_loole[und][0][field_good][und]", "0");
            if (loolePressMiani.getBadCondition() != null) {
                data.put("field_loole[und][0][field_loole_shield_text][und][0][value]", loolePressMiani.getBadCondition().getShomareShield());
                data.put("field_loole[und][0][field_loole_faz_text][und][0][value]", loolePressMiani.getBadCondition().getShomareFaze());
                data.put("field_loole[und][0][field_loole_desc][und][0][value]", loolePressMiani.getBadCondition().getDescription());
                data.put("field_loole[und][0][field_loole_tedad][und][0][value]", loolePressMiani.getBadCondition().getTedad());
            }
        }
    }


    private void goyEkhtarField(Map<String, String> data,ProjectModel.Goy goyEkhtar){
        if (Objects.equals(goyEkhtar.getCondition(), Constants.IS_GOOD))
        {
            data.put("field_goy[und][0][field_good][und]", "1");
        }
        else if (Objects.equals(goyEkhtar.getCondition(),Constants.IS_BAD))
        {
            data.put("field_goy[und][0][field_good][und]", "0");
            if (goyEkhtar.getBadCondition() != null){
                data.put("field_goy[und][0][field_goy_shield][und][0][value]", goyEkhtar.getBadCondition().getShieldNumber());
                data.put("field_goy[und][0][field_goy_faz][und][0][value]", goyEkhtar.getBadCondition().getFazNumber());
                data.put("field_goy[und][0][field_goy_desc][und][0][value]", goyEkhtar.getBadCondition().getDescription());
            }
        }

    }

    private void jooshkariField(Map<String, String> data,ProjectModel.Jooshkari jooshkari){
        if (Objects.equals(jooshkari.getCondition(), Constants.IS_GOOD))
        {
            data.put("field_joosh[und][0][field_good][und]", "1");
        }
        else if (Objects.equals(jooshkari.getCondition(),Constants.IS_BAD))
        {

            data.put("field_joosh[und][0][field_good][und]", "0");
            if (jooshkari.getBadCondition() != null){
                data.put("field_joosh[und][0][field_joosh_metr][und][0][value]", jooshkari.getBadCondition().getTooleJooshkariBeMetr());
                data.put("field_joosh[und][0][field_joosh_desc][und][0][value]", jooshkari.getBadCondition().getDesc());
            }
        }

    }

    private void vazyateMasirField(Map<String, String> data, ProjectModel.VaziateMasir
            vaziateMasir) {
        if (Objects.equals(vaziateMasir.getCondition(), Constants.IS_GOOD)) {

            data.put("field_masir[und][0][field_good][und]", "1");

        } else if (Objects.equals(vaziateMasir.getCondition(), Constants.IS_BAD)) {

            data.put("field_masir[und][0][field_good][und]", "0");
            if (vaziateMasir.getBadCondition() != null) {
                data.put("field_masir[und][0][field_masir_bad][und]", fiveSegmentedConvertor(vaziateMasir.getBadCondition().getAmount()));
                data.put("field_masir[und][0][field_masir_desc][und][0][value]", vaziateMasir.getBadCondition().getDescription());
            }
        }
    }

    private void vaziateHarimField(Map<String, String> data, ProjectModel.VaziateHarim
            vaziateHarim) {
        if (Objects.equals(vaziateHarim.getCondition(), Constants.IS_GOOD)) {

            data.put("field_harim[und][0][field_good][und]", "1");

        } else if (Objects.equals(vaziateHarim.getCondition(), Constants.IS_BAD)) {

            data.put("field_harim[und][0][field_good][und]", "0");

            if (vaziateHarim.getBadCondition() != null) {
                if (Objects.equals(vaziateHarim.getBadCondition().getSakhtoSaz(), Constants.TRUE)) {
                    data.put("field_harim[und][0][field_harim_bad][und][0]", "sakht");
                }

                if (Objects.equals(vaziateHarim.getBadCondition().getKeshavarzi(), Constants.TRUE)) {

                    data.put("field_harim[und][0][field_harim_bad][und][1]", "keshavarzi");
                }

                if (Objects.equals(vaziateHarim.getBadCondition().getOthers(), Constants.TRUE)) {

                    data.put("field_harim[und][0][field_harim_bad][und][2]", "sayer");
                }

                if (Objects.equals(vaziateHarim.getBadCondition().getDarakht(), Constants.TRUE)) {

                    data.put("field_harim[und][0][field_harim_bad][und][3]", "derakht");
                }

                if (Objects.equals(vaziateHarim.getBadCondition().getJade(), Constants.TRUE)) {

                    data.put("field_harim[und][0][field_harim_bad][und][4]", "jade");
                }

                if (Objects.equals(vaziateHarim.getBadCondition().getLooleHayeSookhtResani(), Constants.TRUE)) {

                    data.put("field_harim[und][0][field_harim_bad][und][5]", "sookht");
                }

                if (Objects.equals(vaziateHarim.getBadCondition().getKanal(), Constants.TRUE)) {

                    data.put("field_harim[und][0][field_harim_bad][und][6]", "kanal");
                }

                if (Objects.equals(vaziateHarim.getBadCondition().getRailway(), Constants.TRUE)) {

                    data.put("field_harim[und][0][field_harim_bad][und][7]", "railway");
                }

                data.put("field_harim[und][0][field_harim_desc][und][0][value]", vaziateHarim.getBadCondition().getDescription());
            }
        }
    }

    private void taghatoBaBistKField(Map<String, String> data, ProjectModel.TaghatoBa20k
            taghatoBa20k) {
        if (Objects.equals(taghatoBa20k.getCondition(), Constants.IS_GOOD)) {

            data.put("field_20k[und][0][field_good][und]", "1");

        } else if (Objects.equals(taghatoBa20k.getCondition(), Constants.IS_BAD)) {

            data.put("field_20k[und][0][field_good][und]", "0");
            if (taghatoBa20k.getBadCondition() != null) {
                data.put("field_20k[und][0][field_tedad_20k][und][0][value]", taghatoBa20k.getBadCondition().getTedad());
                data.put("field_20k[und][0][field_20k_desc][und][0][value]", taghatoBa20k.getBadCondition().getDescription());
            }
        }
    }

    private void ensheabField(Map<String, String> data, ProjectModel.Ensheab ensheab) {
        if (Objects.equals(ensheab.getCondition(), Constants.IS_GOOD)) {

            data.put("field_ensheab[und][0][field_good][und]", "1");

        } else if (Objects.equals(ensheab.getCondition(), Constants.IS_BAD)) {

            data.put("field_ensheab[und][0][field_good][und]", "0");
            if (ensheab.getBadCondition() != null)
                data.put("field_ensheab[und][0][field_ensheab_desc][und][0][value]", ensheab.getBadCondition().getDescription());
        }
    }

    private void mavaneField(Map<String, String> data, ProjectModel.Mavne mavne) {
        if (Objects.equals(mavne.getCondition(), Constants.IS_GOOD)) {

            data.put("field_mavane[und][0][field_good][und]", "1");
        } else if (Objects.equals(mavne.getCondition(), Constants.IS_BAD)) {

            data.put("field_mavane[und][0][field_good][und]", "0");
            if (mavne.getBadCondition() != null)
                data.put("field_mavane[und][0][field_mavane_desc][und][0][value]", mavne.getBadCondition().getDescription());
        }
    }

    private void taghatoBaJadeAsliField
            (Map<String, String> data, ProjectModel.TaghatoBaJadeAsli taghatoBaJadeAsli) {
        if (Objects.equals(taghatoBaJadeAsli.getCondition(), Constants.IS_GOOD)) {
            data.put("field_jade_asli[und][0][field_good][und]", "1");
        } else if (Objects.equals(taghatoBaJadeAsli.getCondition(), Constants.IS_BAD)) {

            data.put("field_jade_asli[und][0][field_good][und]", "0");
            if (taghatoBaJadeAsli.getBadCondition() != null) {
                data.put("field_jade_asli[und][0][field_jade_tedad][und][0][value]", taghatoBaJadeAsli.getBadCondition().getTedad());
                data.put("field_jade_asli[und][0][field_jade_desc][und][0][value]", taghatoBaJadeAsli.getBadCondition().getDescription());
            }
        }
    }

    private void khamooshiKhatField
            (Map<String, String> data, ProjectModel.KhamooshKardaneKhat khamooshKardaneKhat) {
        if (Objects.equals(khamooshKardaneKhat.getVaziate(), "1")) {

            data.put("field_khamooshi[und][0][field_good][und]", "1");
            data.put("field_khamooshi[und][0][field_khamooshi_desc][und][0][value]", khamooshKardaneKhat.getDescription());
        } else {

            data.put("field_khamooshi[und][0][field_good][und]", "0");
            data.put("field_khamooshi[und][0][field_khamooshi_desc][und][0][value]", khamooshKardaneKhat.getDescription());
        }
    }

    private void dakalBohraniField
            (Map<String, String> data, ProjectModel.DakalBohrani dakalBohrani) {
        if (Objects.equals(dakalBohrani.getVaziate(), "1")) {

            data.put("field_bohran[und][0][field_good][und]", "0");
            data.put("field_bohran[und][0][field_bohran_desc][und][0][value]", dakalBohrani.getDescription());
        } else {

            data.put("field_bohran[und][0][field_good][und]", "1");
            data.put("field_bohran[und][0][field_bohran_desc][und][0][value]", dakalBohrani.getDescription());
        }
    }

    private void ashiaEzafeField(Map<String, String> data, ProjectModel.AshiaEzafe
            ashiaEzafe) {
        if (Objects.equals(ashiaEzafe.getCondition(), Constants.IS_GOOD)) {
            data.put("field_ezafe[und][0][field_good][und]", "1");
        } else if (Objects.equals(ashiaEzafe.getCondition(), Constants.IS_BAD)) {

            data.put("field_ezafe[und][0][field_good][und]", "0");
            if (ashiaEzafe.getBadCondition() != null) {
                data.put("field_ezafe[und][0][field_ezafe_faz][und][0][value]", ashiaEzafe.getBadCondition().getTedadeashiaEzafePhase());
                data.put("field_ezafe[und][0][field_ezafe_gard][und][0][value]", ashiaEzafe.getBadCondition().getTedadeashiaEzafeGard());
                data.put("field_ezafe[und][0][field_ezafe_dakal][und][0][value]", ashiaEzafe.getBadCondition().getTedadeashiaEzafeDakal());
                data.put("field_ezafe[und][0][field_ezafe_desc][und][0][value]", ashiaEzafe.getBadCondition().getDescription());
            }
        }
    }

    private void laneParandeField(Map<String, String> data, ProjectModel.LaneParande
            laneParande) {
        if (Objects.equals(laneParande.getCondition(), Constants.IS_GOOD)) {

            data.put("field_lane_parande[und][0][field_good][und]", "1");

        } else if (Objects.equals(laneParande.getCondition(), Constants.IS_BAD)) {

            data.put("field_lane_parande[und][0][field_good][und]", "0");
            if (laneParande.getBadCondition() != null) {
                data.put("field_lane_parande[und][0][field_lane_faz][und][0][value]", laneParande.getBadCondition().getTedadeLaneParandePhase());
                data.put("field_lane_parande[und][0][field_lane_dakal][und][0][value]", laneParande.getBadCondition().getTedadeLaneParandeDakal());
                data.put("field_lane_parande[und][0][field_lane_gard][und][0][value]", laneParande.getBadCondition().getTedadeLaneParandeGard());
                data.put("field_lane_parande[und][0][field_lane_desc][und][0][value]", laneParande.getBadCondition().getDescription());
            }
        }
    }

    private void simeMohafezField
            (Map<String, String> data, ProjectModel.SimeMohafezVaMolhaghat simeMohafezVaMolhaghat) {
        if (Objects.equals(simeMohafezVaMolhaghat.getType(), context.getString(R.string.do_shield))) {

            data.put("field_mohafez[und][0][field_mohafez_type][und]", "do");
            data.put("field_mohafez[und][1][field_mohafez_type][und]", "do");
            if (simeMohafezVaMolhaghat.getDoShieldFields() != null) {
                if (Objects.equals(simeMohafezVaMolhaghat.getDoShieldFields().getJenseSimA(), context.getString(R.string.shield_wire))) {

                    data.put("field_mohafez[und][0][field_shield_type][und]", "wire");

                } else if (Objects.equals(simeMohafezVaMolhaghat.getDoShieldFields().getJenseSimA(), context.getString(R.string.opgw))) {

                    data.put("field_mohafez[und][0][field_shield_type][und]", "opgw");
                    if (simeMohafezVaMolhaghat.getDoShieldFields().getOpgwFieldsA() != null &&
                            simeMohafezVaMolhaghat.getDoShieldFields().getOpgwFieldsA().getJointBox() != null)
                        data.put("field_mohafez[und][0][field_jointbox][und]", mapJointBoxLabel(simeMohafezVaMolhaghat.getDoShieldFields().getOpgwFieldsA().getJointBox()));

                }

                if (Objects.equals(simeMohafezVaMolhaghat.getDoShieldFields().getJenseSimB(), context.getString(R.string.shield_wire))) {

                    data.put("field_mohafez[und][1][field_shield_type][und]", "wire");
                } else if (Objects.equals(simeMohafezVaMolhaghat.getDoShieldFields().getJenseSimB(), context.getString(R.string.opgw))) {

                    data.put("field_mohafez[und][1][field_shield_type][und]", "opgw");

                    if (simeMohafezVaMolhaghat.getDoShieldFields().getOpgwFieldsB() != null &&
                            simeMohafezVaMolhaghat.getDoShieldFields().getOpgwFieldsB().getJointBox() != null)
                        data.put("field_mohafez[und][1][field_jointbox][und]", mapJointBoxLabel(simeMohafezVaMolhaghat.getDoShieldFields().getOpgwFieldsB().getJointBox()));
                }

                if (Objects.equals(simeMohafezVaMolhaghat.getDoShieldFields().getConditionA(), Constants.IS_GOOD)) {

                    data.put("field_mohafez[und][0][field_shield_good][und]", "1");

                } else {

                    data.put("field_mohafez[und][0][field_shield_good][und]", "0");

                    if (simeMohafezVaMolhaghat.getDoShieldFields().getSimeMohafezBadConditionA() != null) {
                        if (Objects.equals(simeMohafezVaMolhaghat.getDoShieldFields().getSimeMohafezBadConditionA().getAcharKeshi(), Constants.TRUE)) {

                            data.put("field_mohafez[und][0][field_shield_iradat][und][0]", "achar");
                        }

                        if (Objects.equals(simeMohafezVaMolhaghat.getDoShieldFields().getSimeMohafezBadConditionA().getLoolePress(), Constants.TRUE)) {

                            data.put("field_mohafez[und][0][field_shield_iradat][und][1]", "peres");
                        }

                        if (Objects.equals(simeMohafezVaMolhaghat.getDoShieldFields().getSimeMohafezBadConditionA().getKolomp(), Constants.TRUE)) {


                            data.put("field_mohafez[und][0][field_shield_iradat][und][2]", "klmp");
                        }

                        if (Objects.equals(simeMohafezVaMolhaghat.getDoShieldFields().getSimeMohafezBadConditionA().getJamper(), Constants.TRUE)) {
                            data.put("field_mohafez[und][0][field_shield_iradat][und][3]", "jamper");
                        }
                        if (Objects.equals(simeMohafezVaMolhaghat.getDoShieldFields().getSimeMohafezBadConditionA().getDamper(), Constants.TRUE)) {

                            data.put("field_mohafez[und][0][field_shield_iradat][und][4]", "damper");
                        }
                        if (Objects.equals(simeMohafezVaMolhaghat.getDoShieldFields().getSimeMohafezBadConditionA().getKafi(), Constants.TRUE)) {

                            data.put("field_mohafez[und][0][field_shield_iradat][und][5]", "kafi");
                        }
                        if (Objects.equals(simeMohafezVaMolhaghat.getDoShieldFields().getSimeMohafezBadConditionA().getArmorad(), Constants.TRUE)) {

                            data.put("field_mohafez[und][0][field_shield_iradat][und][6]", "aramorad");
                        }
                        if (Objects.equals(simeMohafezVaMolhaghat.getDoShieldFields().getSimeMohafezBadConditionA().getEshpil(), Constants.TRUE)) {

                            data.put("field_mohafez[und][0][field_shield_iradat][und][7]", "eshpil");
                        }
                        if (Objects.equals(simeMohafezVaMolhaghat.getDoShieldFields().getSimeMohafezBadConditionA().getGoyeEkhtar(), Constants.IS_CHECKED)) {

                            data.put("field_mohafez[und][0][field_shield_iradat][und][8]", "goy");
                        }

                        data.put("field_mohafez[und][0][field_mohafez_desc][und][0][value]", simeMohafezVaMolhaghat.getDoShieldFields().getSimeMohafezBadConditionA().getDescription());
                    }
                }


                if (Objects.equals(simeMohafezVaMolhaghat.getDoShieldFields().getConditionB(), Constants.IS_GOOD)) {

                    data.put("field_mohafez[und][1][field_shield_good][und]", "1");

                } else if (Objects.equals(simeMohafezVaMolhaghat.getDoShieldFields().getConditionB(), Constants.IS_BAD)) {

                    data.put("field_mohafez[und][1][field_shield_good][und]", "0");

                    if (simeMohafezVaMolhaghat.getDoShieldFields().getSimeMohafezBadConditionB() != null) {
                        if (Objects.equals(simeMohafezVaMolhaghat.getDoShieldFields().getSimeMohafezBadConditionB().getAcharKeshi(), Constants.TRUE)) {

                            data.put("field_mohafez[und][1][field_shield_iradat][und][0]", "achar");
                        }

                        if (Objects.equals(simeMohafezVaMolhaghat.getDoShieldFields().getSimeMohafezBadConditionB().getLoolePress(), Constants.TRUE)) {

                            data.put("field_mohafez[und][1][field_shield_iradat][und][1]", "peres");
                        }

                        if (Objects.equals(simeMohafezVaMolhaghat.getDoShieldFields().getSimeMohafezBadConditionB().getKolomp(), Constants.TRUE)) {

                            data.put("field_mohafez[und][1][field_shield_iradat][und][2]", "klmp");
                        }

                        if (Objects.equals(simeMohafezVaMolhaghat.getDoShieldFields().getSimeMohafezBadConditionB().getJamper(), Constants.TRUE)) {
                            data.put("field_mohafez[und][1][field_shield_iradat][und][3]", "jamper");
                        }
                        if (Objects.equals(simeMohafezVaMolhaghat.getDoShieldFields().getSimeMohafezBadConditionB().getDamper(), Constants.TRUE)) {

                            data.put("field_mohafez[und][1][field_shield_iradat][und][4]", "damper");
                        }
                        if (Objects.equals(simeMohafezVaMolhaghat.getDoShieldFields().getSimeMohafezBadConditionB().getKafi(), Constants.TRUE)) {

                            data.put("field_mohafez[und][1][field_shield_iradat][und][5]", "kafi");
                        }
                        if (Objects.equals(simeMohafezVaMolhaghat.getDoShieldFields().getSimeMohafezBadConditionB().getArmorad(), Constants.TRUE)) {

                            data.put("field_mohafez[und][1][field_shield_iradat][und][6]", "aramorad");
                        }
                        if (Objects.equals(simeMohafezVaMolhaghat.getDoShieldFields().getSimeMohafezBadConditionB().getEshpil(), Constants.TRUE)) {

                            data.put("field_mohafez[und][1][field_shield_iradat][und][7]", "eshpil");
                        }
                        if (Objects.equals(simeMohafezVaMolhaghat.getDoShieldFields().getSimeMohafezBadConditionB().getGoyeEkhtar(), Constants.IS_CHECKED)) {

                            data.put("field_mohafez[und][1][field_shield_iradat][und][8]", "goy");
                        }


                        data.put("field_mohafez[und][1][field_mohafez_desc][und][0][value]", simeMohafezVaMolhaghat.getDoShieldFields().getSimeMohafezBadConditionB().getDescription());
                    }
                }
            }

        } else if (Objects.equals(simeMohafezVaMolhaghat.getType(), context.getString(R.string.tak_shield))) {

            data.put("field_mohafez[und][0][field_mohafez_type][und]", "tak");

            if (simeMohafezVaMolhaghat.getTakShieldFields() != null) {
                if (Objects.equals(simeMohafezVaMolhaghat.getTakShieldFields().getJenseSim(), context.getString(R.string.shield_wire))) {

                    data.put("field_mohafez[und][0][field_shield_type][und]", "wire");

                } else if (Objects.equals(simeMohafezVaMolhaghat.getTakShieldFields().getJenseSim(), context.getString(R.string.opgw))) {

                    data.put("field_mohafez[und][0][field_shield_type][und]", "opgw");
                    if (simeMohafezVaMolhaghat.getTakShieldFields().getOpgwFields() != null && simeMohafezVaMolhaghat.getTakShieldFields().getOpgwFields().getJointBox() != null)
                        data.put("field_mohafez[und][0][field_jointbox][und]", mapJointBoxLabel(simeMohafezVaMolhaghat.getTakShieldFields().getOpgwFields().getJointBox()));

                }

                if (Objects.equals(simeMohafezVaMolhaghat.getTakShieldFields().getCondition(), Constants.IS_GOOD)) {

                    data.put("field_mohafez[und][0][field_shield_good][und]", "1");

                } else if (Objects.equals(simeMohafezVaMolhaghat.getTakShieldFields().getCondition(), Constants.IS_BAD)) {

                    data.put("field_mohafez[und][0][field_shield_good][und]", "0");

                    if (simeMohafezVaMolhaghat.getTakShieldFields().getSimeMohafezBadCondition() != null) {
                        if (Objects.equals(simeMohafezVaMolhaghat.getTakShieldFields().getSimeMohafezBadCondition().getAcharKeshi(), Constants.TRUE)) {

                            data.put("field_mohafez[und][0][field_shield_iradat][und][0]", "achar");
                        }

                        if (Objects.equals(simeMohafezVaMolhaghat.getTakShieldFields().getSimeMohafezBadCondition().getLoolePress(), Constants.TRUE)) {

                            data.put("field_mohafez[und][0][field_shield_iradat][und][1]", "peres");
                        }

                        if (Objects.equals(simeMohafezVaMolhaghat.getTakShieldFields().getSimeMohafezBadCondition().getKolomp(), Constants.TRUE)) {

                            data.put("field_mohafez[und][0][field_shield_iradat][und][2]", "klmp");
                        }

                        if (Objects.equals(simeMohafezVaMolhaghat.getTakShieldFields().getSimeMohafezBadCondition().getJamper(), Constants.TRUE)) {

                            data.put("field_mohafez[und][0][field_shield_iradat][und][3]", "jamper");
                        }
                        if (Objects.equals(simeMohafezVaMolhaghat.getTakShieldFields().getSimeMohafezBadCondition().getDamper(), Constants.TRUE)) {

                            data.put("field_mohafez[und][0][field_shield_iradat][und][4]", "damper");
                        }
                        if (Objects.equals(simeMohafezVaMolhaghat.getTakShieldFields().getSimeMohafezBadCondition().getKafi(), Constants.TRUE)) {

                            data.put("field_mohafez[und][0][field_shield_iradat][und][5]", "kafi");
                        }
                        if (Objects.equals(simeMohafezVaMolhaghat.getTakShieldFields().getSimeMohafezBadCondition().getArmorad(), Constants.TRUE)) {

                            data.put("field_mohafez[und][0][field_shield_iradat][und][6]", "aramorad");
                        }
                        if (Objects.equals(simeMohafezVaMolhaghat.getTakShieldFields().getSimeMohafezBadCondition().getEshpil(), Constants.TRUE)) {

                            data.put("field_mohafez[und][0][field_shield_iradat][und][7]", "eshpil");
                        }
                        if (Objects.equals(simeMohafezVaMolhaghat.getTakShieldFields().getSimeMohafezBadCondition().getGoyeEkhtar(), Constants.IS_CHECKED)) {

                            data.put("field_mohafez[und][0][field_shield_iradat][und][8]", "goy");
                        }

                        data.put("field_mohafez[und][0][field_mohafez_desc][und][0][value]", simeMohafezVaMolhaghat.getTakShieldFields().getSimeMohafezBadCondition().getDescription());
                    }
                }
            }

        } else if (Objects.equals(simeMohafezVaMolhaghat.getType(), context.getString(R.string.faghed_shield))) {

            data.put("field_mohafez[und][0][field_mohafez_type][und]", "faghed");
        }
    }

    private void zanjireVaMaghareField
            (Map<String, String> data, List<ProjectModel.IsolationChains> isolationChains) {
        //Isolation A
        for (int i = 0; isolationChains.size() > i; i++) {

            if (Objects.equals(isolationChains.get(i).getACondition(), Constants.IS_GOOD)) {

                data.put("field_z_m_m[und][" + i + "][field_zmm_a_good][und]", "1");
                data.put("field_z_m_m[und][" + i + "][field_dakal_dispatching][und][0][value]", isolationChains.get(i).getDispatching());

                if (Objects.equals(isolationChains.get(i).getAType(), context.getString(R.string.seramiki))) {

                    data.put("field_z_m_m[und][" + i + "][field_zmm_a_type][und]", "seramik");

                } else if (Objects.equals(isolationChains.get(i).getAType(), context.getString(R.string.shisheyi))) {

                    data.put("field_z_m_m[und][" + i + "][field_zmm_a_type][und]", "shishe");

                } else if (Objects.equals(isolationChains.get(i).getAType(), context.getString(R.string.silicon))) {

                    data.put("field_z_m_m[und][" + i + "][field_zmm_a_type][und]", "silicon");

                } else if (Objects.equals(isolationChains.get(i).getAType(), context.getString(R.string.tarkibi))) {

                    data.put("field_z_m_m[und][" + i + "][field_zmm_a_type][und]", "tarkibi");
                }

            } else if (Objects.equals(isolationChains.get(i).getACondition(), Constants.IS_BAD)) {

                data.put("field_z_m_m[und][" + i + "][field_zmm_a_good][und]", "0");
                data.put("field_z_m_m[und][" + i + "][field_dakal_dispatching][und][0][value]", isolationChains.get(i).getDispatching());

                if (Objects.equals(isolationChains.get(i).getAType(), context.getString(R.string.seramiki))) {

                    data.put("field_z_m_m[und][" + i + "][field_zmm_a_type][und]", "seramik");

                } else if (Objects.equals(isolationChains.get(i).getAType(), context.getString(R.string.shisheyi))) {

                    data.put("field_z_m_m[und][" + i + "][field_zmm_a_type][und]", "shishe");

                } else if (Objects.equals(isolationChains.get(i).getAType(), context.getString(R.string.silicon))) {

                    data.put("field_z_m_m[und][" + i + "][field_zmm_a_type][und]", "silicon");
                    if (isolationChains.get(i).getIfAConditionBad() != null &&
                            Objects.equals(isolationChains.get(i).getIfAConditionBad().isTeared(), Constants.TRUE)) {

                        data.put("field_z_m_m[und][" + i + "][field_zmm_a_iradat][und][0]", "paregi");
                    }

                } else if (Objects.equals(isolationChains.get(i).getAType(), context.getString(R.string.tarkibi))) {

                    data.put("field_z_m_m[und][" + i + "][field_zmm_a_type][und]", "tarkibi");
                    if (isolationChains.get(i).getIfAConditionBad() != null &&
                            Objects.equals(isolationChains.get(i).getIfAConditionBad().isTeared(), Constants.TRUE)) {

                        data.put("field_z_m_m[und][" + i + "][field_zmm_a_iradat][und][0]", "paregi");
                    }
                }

                if (isolationChains.get(i).getIfAConditionBad() != null) {
                    if (Objects.equals(isolationChains.get(i).getIfAConditionBad().isTilted(), Constants.TRUE)) {

                        data.put("field_z_m_m[und][" + i + "][field_zmm_a_iradat][und][1]", "kaj");
                    }

                    if (Objects.equals(isolationChains.get(i).getIfAConditionBad().getOffset(), Constants.TRUE)) {

                        data.put("field_z_m_m[und][" + i + "][field_zmm_a_iradat][und][2]", "afset");
                    }

                    if (Objects.equals(isolationChains.get(i).getIfAConditionBad().getDontHaveBoltsAndNuts(), Constants.TRUE)) {

                        data.put("field_z_m_m[und][" + i + "][field_zmm_a_iradat][und][3]", "pich");

                    }

                    if (Objects.equals(isolationChains.get(i).getIfAConditionBad().getNick(), Constants.TRUE)) {

                        data.put("field_z_m_m[und][" + i + "][field_zmm_a_iradat][und][4]", "lab");
                    }

                    if (Objects.equals(isolationChains.get(i).getIfAConditionBad().getDontHaveEshpil(), Constants.TRUE)) {

                        data.put("field_z_m_m[und][" + i + "][field_zmm_a_iradat][und][5]", "ashpel");
                    }

                    if (Objects.equals(isolationChains.get(i).getIfAConditionBad().getPolution(), Constants.TRUE)) {

                        data.put("field_z_m_m[und][" + i + "][field_zmm_a_iradat][und][6]", "aloodegi");
                        data.put("field_z_m_m[und][" + i + "][field_zmm_a_aloodegi][und][0][value]", isolationChains.get(i).getIfAConditionBad().getPolutionPercent());
                    }

                    if (Objects.equals(isolationChains.get(i).getIfAConditionBad().getInsolatorPinDropping(), Constants.TRUE)) {

                        data.put("field_z_m_m[und][" + i + "][field_zmm_a_iradat][und][7]", "pin");
                    }

                    if (Objects.equals(isolationChains.get(i).getIfAConditionBad().getNoeAsibeMaghare(), context.getString(R.string.dose_not_have))) {
                        data.put("field_z_m_m[und][" + i + "][field_zmm_a_asib][und]", "nadarad");

                    } else if (Objects.equals(isolationChains.get(i).getIfAConditionBad().getNoeAsibeMaghare(), context.getString(R.string.shekastegi))) {

                        data.put("field_z_m_m[und][" + i + "][field_zmm_a_asib][und]", "shekastegi");
                        data.put("field_z_m_m[und][" + i + "][field_zmm_a_shekastegi][und][0][value]", isolationChains.get(i).getIfAConditionBad().getTedadeShekastegiYaSookhtegi());

                    } else if (Objects.equals(isolationChains.get(i).getIfAConditionBad().getNoeAsibeMaghare(), context.getString(R.string.sokhtegi))) {

                        data.put("field_z_m_m[und][" + i + "][field_zmm_a_asib][und]", "sookhtegi");
                        data.put("field_z_m_m[und][" + i + "][field_zmm_a_sookhtegi][und][0][value]", isolationChains.get(i).getIfAConditionBad().getTedadeShekastegiYaSookhtegi());
                    }

                    if (Objects.equals(isolationChains.get(i).getIfAConditionBad().getCapAndPins(), context.getString(R.string.dose_not_have))) {

                        data.put("field_z_m_m[und][" + i + "][field_zmm_a_pin][und]", "nadarad");

                    } else if (Objects.equals(isolationChains.get(i).getIfAConditionBad().getCapAndPins(), context.getString(R.string.khordegi))) {

                        data.put("field_z_m_m[und][" + i + "][field_zmm_a_pin][und]", "khordegi");

                    } else if (Objects.equals(isolationChains.get(i).getIfAConditionBad().getCapAndPins(), context.getString(R.string.zang_zadegi))) {

                        data.put("field_z_m_m[und][" + i + "][field_zmm_a_pin][und]", "zang");
                    }

                    data.put("field_z_m_m[und][" + i + "][field_zmm_a_desc][und][0][value]", isolationChains.get(i).getIfAConditionBad().getDescription());
                }

            }

        }


        //Isolation B
        for (int i = 0; isolationChains.size() > i; i++) {

            if (Objects.equals(isolationChains.get(i).getBCondition(), Constants.IS_GOOD)) {

                data.put("field_z_m_m[und][" + i + "][field_zmm_b_good][und]", "1");
                data.put("field_z_m_m[und][" + i + "][field_dakal_dispatching][und][0][value]", isolationChains.get(i).getDispatching());

                if (Objects.equals(isolationChains.get(i).getBType(), context.getString(R.string.seramiki))) {

                    data.put("field_z_m_m[und][" + i + "][field_zmm_b_type][und]", "seramik");

                } else if (Objects.equals(isolationChains.get(i).getBType(), context.getString(R.string.shisheyi))) {

                    data.put("field_z_m_m[und][" + i + "][field_zmm_b_type][und]", "shishe");

                } else if (Objects.equals(isolationChains.get(i).getBType(), context.getString(R.string.silicon))) {

                    data.put("field_z_m_m[und][" + i + "][field_zmm_b_type][und]", "silicon");

                } else if (Objects.equals(isolationChains.get(i).getBType(), context.getString(R.string.tarkibi))) {

                    data.put("field_z_m_m[und][" + i + "][field_zmm_b_type][und]", "tarkibi");
                }

            } else if (Objects.equals(isolationChains.get(i).getBCondition(), Constants.IS_BAD)) {

                data.put("field_z_m_m[und][" + i + "][field_zmm_b_good][und]", "0");
                data.put("field_z_m_m[und][" + i + "][field_dakal_dispatching][und][0][value]", isolationChains.get(i).getDispatching());

                if (Objects.equals(isolationChains.get(i).getBType(), context.getString(R.string.seramiki))) {

                    data.put("field_z_m_m[und][" + i + "][field_zmm_b_type][und]", "seramik");

                } else if (Objects.equals(isolationChains.get(i).getBType(), context.getString(R.string.shisheyi))) {

                    data.put("field_z_m_m[und][" + i + "][field_zmm_b_type][und]", "shishe");

                } else if (Objects.equals(isolationChains.get(i).getBType(), context.getString(R.string.silicon))) {

                    data.put("field_z_m_m[und][" + i + "][field_zmm_b_type][und]", "silicon");

                } else if (Objects.equals(isolationChains.get(i).getBType(), context.getString(R.string.tarkibi))) {

                    data.put("field_z_m_m[und][" + i + "][field_zmm_b_type][und]", "tarkibi");
                }

                if (isolationChains.get(i).getIfBConditionBad() != null) {
                    if (Objects.equals(isolationChains.get(i).getIfBConditionBad().isTeared(), Constants.TRUE)) {

                        data.put("field_z_m_m[und][" + i + "][field_zmm_b_iradat][und][0]", "paregi");
                    }

                    if (Objects.equals(isolationChains.get(i).getIfBConditionBad().isTilted(), Constants.TRUE)) {

                        data.put("field_z_m_m[und][" + i + "][field_zmm_b_iradat][und][1]", "kaj");
                    }

                    if (Objects.equals(isolationChains.get(i).getIfBConditionBad().getOffset(), Constants.TRUE)) {

                        data.put("field_z_m_m[und][" + i + "][field_zmm_b_iradat][und][2]", "afset");
                    }

                    if (Objects.equals(isolationChains.get(i).getIfBConditionBad().getDontHaveBoltsAndNuts(), Constants.TRUE)) {

                        data.put("field_z_m_m[und][" + i + "][field_zmm_b_iradat][und][3]", "pich");

                    }

                    if (Objects.equals(isolationChains.get(i).getIfBConditionBad().getNick(), Constants.TRUE)) {

                        data.put("field_z_m_m[und][" + i + "][field_zmm_b_iradat][und][4]", "lab");
                    }

                    if (Objects.equals(isolationChains.get(i).getIfBConditionBad().getDontHaveEshpil(), Constants.TRUE)) {

                        data.put("field_z_m_m[und][" + i + "][field_zmm_b_iradat][und][5]", "ashpel");
                    }

                    if (Objects.equals(isolationChains.get(i).getIfBConditionBad().getPolution(), Constants.TRUE)) {

                        data.put("field_z_m_m[und][" + i + "][field_zmm_b_iradat][und][6]", "aloodegi");
                        data.put("field_z_m_m[und][" + i + "][field_zmm_b_aloodegi][und][0][value]", isolationChains.get(i).getIfBConditionBad().getPolutionPercent());
                    }

                    if (Objects.equals(isolationChains.get(i).getIfBConditionBad().getInsolatorPinDropping(), Constants.TRUE)) {

                        data.put("field_z_m_m[und][" + i + "][field_zmm_b_iradat][und][7]", "pin");
                    }

                    if (Objects.equals(isolationChains.get(i).getIfBConditionBad().getNoeAsibeMaghare(), context.getString(R.string.dose_not_have))) {
                        data.put("field_z_m_m[und][" + i + "][field_zmm_b_asib][und]", "nadarad");

                    } else if (Objects.equals(isolationChains.get(i).getIfBConditionBad().getNoeAsibeMaghare(), context.getString(R.string.shekastegi))) {

                        data.put("field_z_m_m[und][" + i + "][field_zmm_b_asib][und]", "shekastegi");
                        data.put("field_z_m_m[und][" + i + "][field_zmm_b_shekastegi][und][0][value]", isolationChains.get(i).getIfBConditionBad().getTedadeShekastegiYaSookhtegi());

                    } else if (Objects.equals(isolationChains.get(i).getIfBConditionBad().getNoeAsibeMaghare(), context.getString(R.string.sokhtegi))) {

                        data.put("field_z_m_m[und][" + i + "][field_zmm_b_asib][und]", "sookhtegi");
                        data.put("field_z_m_m[und][" + i + "][field_zmm_b_sookhtegi][und][0][value]", isolationChains.get(i).getIfBConditionBad().getTedadeShekastegiYaSookhtegi());
                    }

                    if (Objects.equals(isolationChains.get(i).getIfBConditionBad().getCapAndPins(), context.getString(R.string.dose_not_have))) {

                        data.put("field_z_m_m[und][" + i + "][field_zmm_b_pin][und]", "nadarad");

                    } else if (Objects.equals(isolationChains.get(i).getIfBConditionBad().getCapAndPins(), context.getString(R.string.khordegi))) {

                        data.put("field_z_m_m[und][" + i + "][field_zmm_b_pin][und]", "khordegi");

                    } else if (Objects.equals(isolationChains.get(i).getIfBConditionBad().getCapAndPins(), context.getString(R.string.zang_zadegi))) {

                        data.put("field_z_m_m[und][" + i + "][field_zmm_b_pin][und]", "zang");
                    }

                    data.put("field_z_m_m[und][" + i + "][field_zmm_b_desc][und][0][value]", isolationChains.get(i).getIfBConditionBad().getDescription());
                }
            }


        }

        //Isolation C
        for (int i = 0; isolationChains.size() > i; i++) {

            if (Objects.equals(isolationChains.get(i).getCCondition(), Constants.IS_GOOD)) {

                data.put("field_z_m_m[und][" + i + "][field_zmm_c_good][und]", "1");
                data.put("field_z_m_m[und][" + i + "][field_dakal_dispatching][und][0][value]", isolationChains.get(i).getDispatching());

                if (Objects.equals(isolationChains.get(i).getCType(), context.getString(R.string.seramiki))) {

                    data.put("field_z_m_m[und][" + i + "][field_zmm_c_type][und]", "seramik");

                } else if (Objects.equals(isolationChains.get(i).getCType(), context.getString(R.string.shisheyi))) {

                    data.put("field_z_m_m[und][" + i + "][field_zmm_c_type][und]", "shishe");

                } else if (Objects.equals(isolationChains.get(i).getCType(), context.getString(R.string.silicon))) {

                    data.put("field_z_m_m[und][" + i + "][field_zmm_c_type][und]", "silicon");

                } else if (Objects.equals(isolationChains.get(i).getCType(), context.getString(R.string.tarkibi))) {

                    data.put("field_z_m_m[und][" + i + "][field_zmm_c_type][und]", "tarkibi");
                }

            } else if (Objects.equals(isolationChains.get(i).getCCondition(), Constants.IS_BAD)) {

                data.put("field_z_m_m[und][" + i + "][field_zmm_c_good][und]", "0");
                data.put("field_z_m_m[und][" + i + "][field_dakal_dispatching][und][0][value]", isolationChains.get(i).getDispatching());

                if (Objects.equals(isolationChains.get(i).getCType(), context.getString(R.string.seramiki))) {

                    data.put("field_z_m_m[und][" + i + "][field_zmm_c_type][und]", "seramik");

                } else if (Objects.equals(isolationChains.get(i).getCType(), context.getString(R.string.shisheyi))) {

                    data.put("field_z_m_m[und][" + i + "][field_zmm_c_type][und]", "shishe");

                } else if (Objects.equals(isolationChains.get(i).getCType(), context.getString(R.string.silicon))) {

                    data.put("field_z_m_m[und][" + i + "][field_zmm_c_type][und]", "silicon");

                } else if (Objects.equals(isolationChains.get(i).getCType(), context.getString(R.string.tarkibi))) {

                    data.put("field_z_m_m[und][" + i + "][field_zmm_c_type][und]", "tarkibi");
                }

                if (isolationChains.get(i).getIfCConditionBad() != null) {
                    if (Objects.equals(isolationChains.get(i).getIfCConditionBad().isTeared(), Constants.TRUE)) {

                        data.put("field_z_m_m[und][" + i + "][field_zmm_c_iradat][und][0]", "paregi");
                    }

                    if (Objects.equals(isolationChains.get(i).getIfCConditionBad().isTilted(), Constants.TRUE)) {

                        data.put("field_z_m_m[und][" + i + "][field_zmm_c_iradat][und][1]", "kaj");
                    }

                    if (Objects.equals(isolationChains.get(i).getIfCConditionBad().getOffset(), Constants.TRUE)) {

                        data.put("field_z_m_m[und][" + i + "][field_zmm_c_iradat][und][2]", "afset");
                    }

                    if (Objects.equals(isolationChains.get(i).getIfCConditionBad().getDontHaveBoltsAndNuts(), Constants.TRUE)) {

                        data.put("field_z_m_m[und][" + i + "][field_zmm_c_iradat][und][3]", "pich");

                    }

                    if (Objects.equals(isolationChains.get(i).getIfCConditionBad().getNick(), Constants.TRUE)) {

                        data.put("field_z_m_m[und][" + i + "][field_zmm_c_iradat][und][4]", "lab");
                    }

                    if (Objects.equals(isolationChains.get(i).getIfCConditionBad().getDontHaveEshpil(), Constants.TRUE)) {

                        data.put("field_z_m_m[und][" + i + "][field_zmm_c_iradat][und][5]", "ashpel");
                    }

                    if (Objects.equals(isolationChains.get(i).getIfCConditionBad().getPolution(), Constants.TRUE)) {

                        data.put("field_z_m_m[und][" + i + "][field_zmm_c_iradat][und][6]", "aloodegi");
                        data.put("field_z_m_m[und][" + i + "][field_zmm_c_aloodegi][und][0][value]", isolationChains.get(i).getIfCConditionBad().getPolutionPercent());
                    }

                    if (Objects.equals(isolationChains.get(i).getIfCConditionBad().getInsolatorPinDropping(), Constants.TRUE)) {

                        data.put("field_z_m_m[und][" + i + "][field_zmm_c_iradat][und][7]", "pin");
                    }

                    if (Objects.equals(isolationChains.get(i).getIfCConditionBad().getNoeAsibeMaghare(), context.getString(R.string.dose_not_have))) {
                        data.put("field_z_m_m[und][" + i + "][field_zmm_c_asib][und]", "nadarad");

                    } else if (Objects.equals(isolationChains.get(i).getIfCConditionBad().getNoeAsibeMaghare(), context.getString(R.string.shekastegi))) {

                        data.put("field_z_m_m[und][" + i + "][field_zmm_c_asib][und]", "shekastegi");
                        data.put("field_z_m_m[und][" + i + "][field_zmm_c_shekastegi][und][0][value]", isolationChains.get(i).getIfCConditionBad().getTedadeShekastegiYaSookhtegi());

                    } else if (Objects.equals(isolationChains.get(i).getIfCConditionBad().getNoeAsibeMaghare(), context.getString(R.string.sokhtegi))) {

                        data.put("field_z_m_m[und][" + i + "][field_zmm_c_asib][und]", "sookhtegi");
                        data.put("field_z_m_m[und][" + i + "][field_zmm_c_sookhtegi][und][0][value]", isolationChains.get(i).getIfCConditionBad().getTedadeShekastegiYaSookhtegi());
                    }

                    if (Objects.equals(isolationChains.get(i).getIfCConditionBad().getCapAndPins(), context.getString(R.string.dose_not_have))) {

                        data.put("field_z_m_m[und][" + i + "][field_zmm_c_pin][und]", "nadarad");

                    } else if (Objects.equals(isolationChains.get(i).getIfCConditionBad().getCapAndPins(), context.getString(R.string.khordegi))) {

                        data.put("field_z_m_m[und][" + i + "][field_zmm_c_pin][und]", "khordegi");

                    } else if (Objects.equals(isolationChains.get(i).getIfCConditionBad().getCapAndPins(), context.getString(R.string.zang_zadegi))) {

                        data.put("field_z_m_m[und][" + i + "][field_zmm_c_pin][und]", "zang");
                    }

                    data.put("field_z_m_m[und][" + i + "][field_zmm_c_desc][und][0][value]", isolationChains.get(i).getIfCConditionBad().getDescription());
                }
            }

        }
    }

    private void yaraghFields
            (Map<String, String> data, List<ProjectModel.Fittings> fittings) {
        //Fittings A
        for (int i = 0; fittings.size() > i; i++) {

            if (Objects.equals(fittings.get(i).getACondition(), Constants.IS_GOOD)) {

                data.put("field_yaragh[und][" + i + "][field_a_yaragh_good][und]", "1");
                data.put("field_yaragh[und][" + i + "][field_dakal_dispatching][und][0][value]", fittings.get(i).getDispatching());

            } else if (Objects.equals(fittings.get(i).getACondition(), Constants.IS_BAD)) {

                data.put("field_yaragh[und][" + i + "][field_dakal_dispatching][und][0][value]", fittings.get(i).getDispatching());
                data.put("field_yaragh[und][" + i + "][field_a_yaragh_good][und]", "0");

                if (fittings.get(i).getIfAConditionBad() != null) {
                    if (Objects.equals(fittings.get(i).getIfAConditionBad().getLightningArresterDescription(), Constants.TRUE)) {

                        data.put("field_yaragh[und][" + i + "][field_a_yaragh_iradat][und][0]", "bargh");
                    }

                    if (Objects.equals(fittings.get(i).getIfAConditionBad().getCronarigDescription(), Constants.TRUE)) {

                        data.put("field_yaragh[und][" + i + "][field_a_yaragh_iradat][und][1]", "kero");
                    }

                    if (Objects.equals(fittings.get(i).getIfAConditionBad().getConnectionDescription(), Constants.TRUE)) {

                        data.put("field_yaragh[und][" + i + "][field_a_yaragh_iradat][und][2]", "ettesal");
                    }

                    if (Objects.equals(fittings.get(i).getIfAConditionBad().getInsolesDescription(), Constants.TRUE)) {

                        data.put("field_yaragh[und][" + i + "][field_a_yaragh_iradat][und][3]", "kafi");
                    }

                    data.put("field_yaragh[und][" + i + "][field_a_yaragh_desc][und][0][value]", fittings.get(i).getIfAConditionBad().getDescription());
                }

            }

        }

        //Fittings B
        for (int i = 0; fittings.size() > i; i++) {

            if (Objects.equals(fittings.get(i).getBCondition(), Constants.IS_GOOD)) {

                data.put("field_yaragh[und][" + i + "][field_dakal_dispatching][und][0][value]", fittings.get(i).getDispatching());
                data.put("field_yaragh[und][" + i + "][field_b_yaragh_good][und]", "1");

            } else if (Objects.equals(fittings.get(i).getBCondition(), Constants.IS_BAD)) {

                data.put("field_yaragh[und][" + i + "][field_dakal_dispatching][und][0][value]", fittings.get(i).getDispatching());
                data.put("field_yaragh[und][" + i + "][field_b_yaragh_good][und]", "0");

                if (fittings.get(i).getIfBConditionBad() != null) {
                    if (Objects.equals(fittings.get(i).getIfBConditionBad().getLightningArresterDescription(), Constants.TRUE)) {

                        data.put("field_yaragh[und][" + i + "][field_b_yaragh_iradat][und][0]", "bargh");
                    }

                    if (Objects.equals(fittings.get(i).getIfBConditionBad().getCronarigDescription(), Constants.TRUE)) {

                        data.put("field_yaragh[und][" + i + "][field_b_yaragh_iradat][und][1]", "kero");
                    }

                    if (Objects.equals(fittings.get(i).getIfBConditionBad().getConnectionDescription(), Constants.TRUE)) {

                        data.put("field_yaragh[und][" + i + "][field_b_yaragh_iradat][und][2]", "ettesal");
                    }

                    if (Objects.equals(fittings.get(i).getIfBConditionBad().getInsolesDescription(), Constants.TRUE)) {

                        data.put("field_yaragh[und][" + i + "][field_b_yaragh_iradat][und][3]", "kafi");
                    }

                    data.put("field_yaragh[und][" + i + "][field_b_yaragh_desc][und][0][value]", fittings.get(i).getIfBConditionBad().getDescription());
                }

            }

        }

        //Fittings C
        for (int i = 0; fittings.size() > i; i++) {

            if (Objects.equals(fittings.get(i).getCCondition(), Constants.IS_GOOD)) {

                data.put("field_yaragh[und][" + i + "][field_c_yaragh_good][und]", "1");
                data.put("field_yaragh[und][" + i + "][field_dakal_dispatching][und][0][value]", fittings.get(i).getDispatching());

            } else if (Objects.equals(fittings.get(i).getCCondition(), Constants.IS_BAD)) {

                data.put("field_yaragh[und][" + i + "][field_c_yaragh_good][und]", "0");
                data.put("field_yaragh[und][" + i + "][field_dakal_dispatching][und][0][value]", fittings.get(i).getDispatching());

                if (fittings.get(i).getIfCConditionBad() != null) {
                    if (Objects.equals(fittings.get(i).getIfCConditionBad().getLightningArresterDescription(), Constants.TRUE)) {

                        data.put("field_yaragh[und][" + i + "][field_c_yaragh_iradat][und][0]", "bargh");
                    }

                    if (Objects.equals(fittings.get(i).getIfCConditionBad().getCronarigDescription(), Constants.TRUE)) {

                        data.put("field_yaragh[und][" + i + "][field_c_yaragh_iradat][und][1]", "kero");
                    }

                    if (Objects.equals(fittings.get(i).getIfCConditionBad().getConnectionDescription(), Constants.TRUE)) {

                        data.put("field_yaragh[und][" + i + "][field_c_yaragh_iradat][und][2]", "ettesal");
                    }

                    if (Objects.equals(fittings.get(i).getIfCConditionBad().getInsolesDescription(), Constants.TRUE)) {

                        data.put("field_yaragh[und][" + i + "][field_c_yaragh_iradat][und][3]", "kafi");
                    }

                    data.put("field_yaragh[und][" + i + "][field_c_yaragh_desc][und][0][value]", fittings.get(i).getIfCConditionBad().getDescription());
                }

            }

        }
    }

    private void hadiAndPhaseField
            (Map<String, String> data, List<List<ProjectModel.HadiHayePhaseVaMolhaghat>> hadi) {
        if (hadi != null && !hadi.isEmpty()) {
            List<ProjectModel.HadiHayePhaseVaMolhaghat> hadiA = hadi.get(0);
            List<ProjectModel.HadiHayePhaseVaMolhaghat> hadiB = hadi.get(1);
            List<ProjectModel.HadiHayePhaseVaMolhaghat> hadiC = hadi.get(2);

            if (hadiA != null && !hadiA.isEmpty()) {
                for (int i = 0; hadiA.size() > i; i++) {

                    if (Objects.equals(hadiA.get(i).getCondition(), Constants.IS_GOOD)) {
                        data.put("field_hadi_faz[und][" + i + "][field_a_good][und]", "1");
                        data.put("field_hadi_faz[und][" + i + "][field_dakal_dispatching][und][0][value]", hadiA.get(i).getDispatching());
                    } else if (Objects.equals(hadiA.get(i).getCondition(), Constants.IS_BAD)) {

                        data.put("field_hadi_faz[und][" + i + "][field_a_good][und]", "0");
                        data.put("field_hadi_faz[und][" + i + "][field_dakal_dispatching][und][0][value]", hadiA.get(i).getDispatching());

                        if (hadiA.get(i).getBadCondition() != null) {
                            //Hadi

                            if (Objects.equals(hadiA.get(i).getBadCondition().getHadiBadCondition().getBandaj(), Constants.TRUE)) {
                                data.put("field_hadi_faz[und][" + i + "][field_a_hadi][und][0]", "bandage");
                            }

                            if (Objects.equals(hadiA.get(i).getBadCondition().getHadiBadCondition().getJamper(), Constants.TRUE)) {
                                data.put("field_hadi_faz[und][" + i + "][field_a_hadi][und][1]", "jamper");
                            }
                            if (Objects.equals(hadiA.get(i).getBadCondition().getHadiBadCondition().getLoolePressMiani(), Constants.TRUE)) {
                                data.put("field_hadi_faz[und][" + i + "][field_a_hadi][und][2]", "miani");
                            }
                            if (Objects.equals(hadiA.get(i).getBadCondition().getHadiBadCondition().getSpiser(), Constants.TRUE)) {
                                data.put("field_hadi_faz[und][" + i + "][field_a_hadi][und][3]", "spacer");
                            }
                            if (Objects.equals(hadiA.get(i).getBadCondition().getHadiBadCondition().getLoolePressEntehaee(), Constants.TRUE)) {
                                data.put("field_hadi_faz[und][" + i + "][field_a_hadi][und][4]", "entehaei");
                            }
                            if (Objects.equals(hadiA.get(i).getBadCondition().getHadiBadCondition().getReeper(), Constants.TRUE)) {
                                data.put("field_hadi_faz[und][" + i + "][field_a_hadi][und][5]", "ripper");
                            }
                            if (Objects.equals(hadiA.get(i).getBadCondition().getHadiBadCondition().getGoyeEkhtar(), Constants.IS_CHECKED)) {
                                data.put("field_hadi_faz[und][" + i + "][field_a_hadi][und][6]", "goy");
                            }


                            //Phase

                            if (Objects.equals(hadiA.get(i).getBadCondition().getPhaseBadCondition().getZangZadegi(), Constants.TRUE)) {
                                data.put("field_hadi_faz[und][" + i + "][field_a_faz][und][0]", "zang");
                            }

                            if (Objects.equals(hadiA.get(i).getBadCondition().getPhaseBadCondition().getBadKardegiSim(), Constants.TRUE)) {
                                data.put("field_hadi_faz[und][" + i + "][field_a_faz][und][1]", "bad");
                            }
                            if (Objects.equals(hadiA.get(i).getBadCondition().getPhaseBadCondition().getDamper(), Constants.TRUE)) {
                                data.put("field_hadi_faz[und][" + i + "][field_a_faz][und][2]", "damper");
                            }
                            if (Objects.equals(hadiA.get(i).getBadCondition().getPhaseBadCondition().getArmorad(), Constants.TRUE)) {
                                data.put("field_hadi_faz[und][" + i + "][field_a_faz][und][3]", "armorad");
                            }
                            if (Objects.equals(hadiA.get(i).getBadCondition().getPhaseBadCondition().getColomp(), Constants.TRUE)) {
                                data.put("field_hadi_faz[und][" + i + "][field_a_faz][und][4]", "klmp");
                            }
                            if (Objects.equals(hadiA.get(i).getBadCondition().getPhaseBadCondition().getShotorGalooyee(), Constants.TRUE)) {
                                data.put("field_hadi_faz[und][" + i + "][field_a_faz][und][5]", "shotor");
                            }

                            if (Objects.equals(hadiA.get(i).getBadCondition().getPhaseBadCondition().getVazneh(), Constants.IS_CHECKED)) {
                                data.put("field_hadi_faz[und][" + i + "][field_a_faz][und][]", "vazne");
                            }

                            data.put("field_hadi_faz[und][" + i + "][field_a_faz_desc][und][0][value]", hadiA.get(i).getBadCondition().getDescription());
                        }
                    }
                }
            }

            if (hadiB != null && !hadiB.isEmpty()) {
                for (int i = 0; hadiB.size() > i; i++) {

                    if (Objects.equals(hadiB.get(i).getCondition(), Constants.IS_GOOD)) {
                        data.put("field_hadi_faz[und][" + i + "][field_b_good][und]", "1");
                        data.put("field_hadi_faz[und][" + i + "][field_dakal_dispatching][und][0][value]", hadiB.get(i).getDispatching());
                    } else if (Objects.equals(hadiB.get(i).getCondition(), Constants.IS_BAD)) {

                        data.put("field_hadi_faz[und][" + i + "][field_b_good][und]", "0");
                        data.put("field_hadi_faz[und][" + i + "][field_dakal_dispatching][und][0][value]", hadiB.get(i).getDispatching());

                        if (hadiB.get(i).getBadCondition() != null) {
                            //Hadi
                            if (Objects.equals(hadiB.get(i).getBadCondition().getHadiBadCondition().getBandaj(), Constants.TRUE)) {
                                data.put("field_hadi_faz[und][" + i + "][field_b_hadi][und][0]", "bandage");
                            }

                            if (Objects.equals(hadiB.get(i).getBadCondition().getHadiBadCondition().getJamper(), Constants.TRUE)) {
                                data.put("field_hadi_faz[und][" + i + "][field_b_hadi][und][1]", "jamper");
                            }
                            if (Objects.equals(hadiB.get(i).getBadCondition().getHadiBadCondition().getLoolePressMiani(), Constants.TRUE)) {
                                data.put("field_hadi_faz[und][" + i + "][field_b_hadi][und][2]", "miani");
                            }
                            if (Objects.equals(hadiB.get(i).getBadCondition().getHadiBadCondition().getSpiser(), Constants.TRUE)) {
                                data.put("field_hadi_faz[und][" + i + "][field_b_hadi][und][3]", "spacer");
                            }
                            if (Objects.equals(hadiB.get(i).getBadCondition().getHadiBadCondition().getLoolePressEntehaee(), Constants.TRUE)) {
                                data.put("field_hadi_faz[und][" + i + "][field_b_hadi][und][4]", "entehaei");
                            }
                            if (Objects.equals(hadiB.get(i).getBadCondition().getHadiBadCondition().getReeper(), Constants.TRUE)) {
                                data.put("field_hadi_faz[und][" + i + "][field_b_hadi][und][5]", "ripper");
                            }
                            if (Objects.equals(hadiB.get(i).getBadCondition().getHadiBadCondition().getGoyeEkhtar(), Constants.IS_CHECKED)) {
                                data.put("field_hadi_faz[und][" + i + "][field_b_hadi][und][6]", "goy");
                            }

                            //Phase
                            if (Objects.equals(hadiB.get(i).getBadCondition().getPhaseBadCondition().getZangZadegi(), Constants.TRUE)) {
                                data.put("field_hadi_faz[und][" + i + "][field_b_faz][und][0]", "zang");
                            }

                            if (Objects.equals(hadiB.get(i).getBadCondition().getPhaseBadCondition().getBadKardegiSim(), Constants.TRUE)) {
                                data.put("field_hadi_faz[und][" + i + "][field_b_faz][und][1]", "bad");
                            }
                            if (Objects.equals(hadiB.get(i).getBadCondition().getPhaseBadCondition().getDamper(), Constants.TRUE)) {
                                data.put("field_hadi_faz[und][" + i + "][field_b_faz][und][2]", "damper");
                            }
                            if (Objects.equals(hadiB.get(i).getBadCondition().getPhaseBadCondition().getArmorad(), Constants.TRUE)) {
                                data.put("field_hadi_faz[und][" + i + "][field_b_faz][und][3]", "armorad");
                            }
                            if (Objects.equals(hadiB.get(i).getBadCondition().getPhaseBadCondition().getColomp(), Constants.TRUE)) {
                                data.put("field_hadi_faz[und][" + i + "][field_b_faz][und][4]", "klmp");
                            }
                            if (Objects.equals(hadiB.get(i).getBadCondition().getPhaseBadCondition().getShotorGalooyee(), Constants.TRUE)) {
                                data.put("field_hadi_faz[und][" + i + "][field_b_faz][und][5]", "shotor");
                            }

                            if (Objects.equals(hadiB.get(i).getBadCondition().getPhaseBadCondition().getVazneh(), Constants.IS_CHECKED)) {
                                data.put("field_hadi_faz[und][" + i + "][field_b_faz][und][6]", "vazne");
                            }

                            data.put("field_hadi_faz[und][" + i + "][field_b_faz_desc][und][0][value]", hadiB.get(i).getBadCondition().getDescription());
                        }
                    }
                }
            }

            if (hadiC != null && !hadiC.isEmpty()) {
                for (int i = 0; hadiC.size() > i; i++) {

                    if (Objects.equals(hadiC.get(i).getCondition(), Constants.IS_GOOD)) {
                        data.put("field_hadi_faz[und][" + i + "][field_c_good][und]", "1");
                        data.put("field_hadi_faz[und][" + i + "][field_dakal_dispatching][und][0][value]", hadiC.get(i).getDispatching());
                    } else if (Objects.equals(hadiC.get(i).getCondition(), Constants.IS_BAD)) {

                        data.put("field_hadi_faz[und][" + i + "][field_c_good][und]", "0");
                        data.put("field_hadi_faz[und][" + i + "][field_dakal_dispatching][und][0][value]", hadiC.get(i).getDispatching());

                        if (hadiC.get(i).getBadCondition() != null) {
                            //Hadi
                            if (Objects.equals(hadiC.get(i).getBadCondition().getHadiBadCondition().getBandaj(), Constants.TRUE)) {
                                data.put("field_hadi_faz[und][" + i + "][field_c_hadi][und][0]", "bandage");
                            }

                            if (Objects.equals(hadiC.get(i).getBadCondition().getHadiBadCondition().getJamper(), Constants.TRUE)) {
                                data.put("field_hadi_faz[und][" + i + "][field_c_hadi][und][1]", "jamper");
                            }
                            if (Objects.equals(hadiC.get(i).getBadCondition().getHadiBadCondition().getLoolePressMiani(), Constants.TRUE)) {
                                data.put("field_hadi_faz[und][" + i + "][field_c_hadi][und][2]", "miani");
                            }
                            if (Objects.equals(hadiC.get(i).getBadCondition().getHadiBadCondition().getSpiser(), Constants.TRUE)) {
                                data.put("field_hadi_faz[und][" + i + "][field_c_hadi][und][3]", "spacer");
                            }
                            if (Objects.equals(hadiC.get(i).getBadCondition().getHadiBadCondition().getLoolePressEntehaee(), Constants.TRUE)) {
                                data.put("field_hadi_faz[und][" + i + "][field_c_hadi][und][4]", "entehaei");
                            }
                            if (Objects.equals(hadiC.get(i).getBadCondition().getHadiBadCondition().getReeper(), Constants.TRUE)) {
                                data.put("field_hadi_faz[und][" + i + "][field_c_hadi][und][5]", "ripper");
                            }
                            if (Objects.equals(hadiC.get(i).getBadCondition().getHadiBadCondition().getGoyeEkhtar(), Constants.IS_CHECKED)) {
                                data.put("field_hadi_faz[und][" + i + "][field_c_hadi][und][6]", "goy");
                            }

                            //Phase

                            if (Objects.equals(hadiC.get(i).getBadCondition().getPhaseBadCondition().getZangZadegi(), Constants.TRUE)) {
                                data.put("field_hadi_faz[und][" + i + "][field_c_faz][und][0]", "zang");
                            }

                            if (Objects.equals(hadiC.get(i).getBadCondition().getPhaseBadCondition().getBadKardegiSim(), Constants.TRUE)) {
                                data.put("field_hadi_faz[und][" + i + "][field_c_faz][und][1]", "bad");
                            }
                            if (Objects.equals(hadiC.get(i).getBadCondition().getPhaseBadCondition().getDamper(), Constants.TRUE)) {
                                data.put("field_hadi_faz[und][" + i + "][field_c_faz][und][2]", "damper");
                            }
                            if (Objects.equals(hadiC.get(i).getBadCondition().getPhaseBadCondition().getArmorad(), Constants.TRUE)) {
                                data.put("field_hadi_faz[und][" + i + "][field_c_faz][und][3]", "armorad");
                            }
                            if (Objects.equals(hadiC.get(i).getBadCondition().getPhaseBadCondition().getColomp(), Constants.TRUE)) {
                                data.put("field_hadi_faz[und][" + i + "][field_c_faz][und][4]", "klmp");
                            }
                            if (Objects.equals(hadiC.get(i).getBadCondition().getPhaseBadCondition().getShotorGalooyee(), Constants.TRUE)) {
                                data.put("field_hadi_faz[und][" + i + "][field_c_faz][und][5]", "shotor");
                            }

                            if (Objects.equals(hadiC.get(i).getBadCondition().getPhaseBadCondition().getVazneh(), Constants.IS_CHECKED)) {
                                data.put("field_hadi_faz[und][" + i + "][field_c_faz][und][6]", "vazne");
                            }

                            data.put("field_hadi_faz[und][" + i + "][field_c_faz_desc][und][0][value]", hadiC.get(i).getBadCondition().getDescription());
                        }
                    }
                }
            }
        }
    }

    private void seilField(Map<String, String> data, ProjectModel.FloodCondition flood) {
        if (!Objects.equals(flood.getCondition(), Constants.IS_CHECKED)) {
            data.put("field_seil[und][0][field_good][und]", "1");
        } else {

            data.put("field_seil[und][0][field_good][und]", "0");
            if (flood.getBadCondition() != null) {
                data.put("field_seil[und][0][field_seil_khesarat][und]", fiveSegmentedConvertorFlood(flood.getBadCondition().getDamageAmount()));
                data.put("field_seil[und][0][field_seil_kharat][und]", fiveSegmentedConvertorFlood(flood.getBadCondition().getDangerAmount()));
            }
        }
    }

    private void nabshiField(Map<String, String> data, ProjectModel.Corner corner) {
        if (Objects.equals(corner.getCondition(), Constants.IS_GOOD)) {

            data.put("field_nabshi[und][0][field_good][und]", "1");

        } else if (Objects.equals(corner.getCondition(), Constants.IS_BAD)) {

            data.put("field_nabshi[und][0][field_good][und]", "0");
            if (corner.getCornerConditionIfBad() != null) {
                data.put("field_nabshi[und][0][field_nabshi_position][und][0][value]", corner.getCornerConditionIfBad().getMogheiat());
                data.put("field_nabshi[und][0][field_nabshi_zang][und][0][value]", corner.getCornerConditionIfBad().getRustySizeInMeter());
                data.put("field_nabshi[und][0][field_nabshi_enhena][und][0][value]", corner.getCornerConditionIfBad().getCornerCurve());
                data.put("field_nabshi[und][0][field_nabshi_sarbaz][und][0][value]", corner.getCornerConditionIfBad().getOneWayOpen());
                data.put("field_nabshi[und][0][field_nabshi_desc][und][0][value]", corner.getCornerConditionIfBad().getDescription());

                if (corner.getCornerConditionIfBad().getIfCornerStolen() != null) {
                    for (int i = 0; corner.getCornerConditionIfBad().getIfCornerStolen().size() > i; i++) {

                        if (i >= 1) {
                            data.put("field_nabshi[und][" + i + "][field_good][und]", "0");
                        }

                        data.put("field_nabshi[und][" + i + "][field_nabshi_serghat_size][und][0][value]", corner.getCornerConditionIfBad().getIfCornerStolen().get(i).getSize());
                        data.put("field_nabshi[und][" + i + "][field_nabshi_serghat_tedad][und][0][value]", corner.getCornerConditionIfBad().getIfCornerStolen().get(i).getAmount());
                        data.put("field_nabshi[und][" + i + "][field_nabshi_serghat_tool][und][0][value]", corner.getCornerConditionIfBad().getIfCornerStolen().get(i).getWidth());
                        data.put("field_nabshi[und][" + i + "][field_shomare_nabshi][und][0][value]", corner.getCornerConditionIfBad().getShomareNabshi());
                    }
                }
            }
        }
    }

    private void plateField(Map<String, String> data, ProjectModel.Plate plate) {
        if (Objects.equals(plate.getCondition(), Constants.IS_GOOD)) {
            data.put("field_plate[und][0][field_good][und]", "1");
        } else if (Objects.equals(plate.getCondition(), Constants.IS_BAD) && plate.getPlateIfBadConditions() != null) {

            if (plate.getPlateIfBadConditions().getPlateIfBadCondition() != null)
                for (int i = 0; plate.getPlateIfBadConditions().getPlateIfBadCondition().size() > i; i++) {

                    data.put("field_plate[und][" + i + "][field_good][und]", "0");
                    data.put("field_plate[und][" + i + "][field_plate_position][und][0][value]", plate.getPlateIfBadConditions().getPlateIfBadCondition().get(i).getPlatePlace());
                    data.put("field_plate[und][" + i + "][field_plate_kasri][und][0][value]", plate.getPlateIfBadConditions().getPlateIfBadCondition().get(i).getNumberOfMissingPlates());
                    data.put("field_plate[und][" + i + "][field_plate_zang][und][0][value]", plate.getPlateIfBadConditions().getPlateIfBadCondition().get(i).isPlateRusty());
                    data.put("field_plate[und][" + i + "][field_plate_kasri_pich][und][0][value]", plate.getPlateIfBadConditions().getPlateIfBadCondition().get(i).getPlateThornAndNutsShortage());
                    data.put("field_plate[und][" + i + "][field_plate_number_text][und][0][value]", plate.getPlateIfBadConditions().getPlateIfBadCondition().get(i).getPlateNumber());
                    data.put("field_plate[und][" + i + "][field_plate_shekastegi][und][0][value]", plate.getPlateIfBadConditions().getPlateIfBadCondition().get(i).isPlateBroken());


                }
            data.put("field_plate[und][0][field_plate_general_desc][und][0][value]", plate.getPlateIfBadConditions().getDescription());

        }
    }

    private void thornField(Map<String, String> data, ProjectModel.Thorn thorn) {
        if (Objects.equals(thorn.getCondition(), Constants.IS_GOOD)) {
            data.put("field_khar[und][0][field_good][und]", "1");
        } else if (Objects.equals(thorn.getCondition(), Constants.IS_BAD)) {
            data.put("field_khar[und][0][field_good][und]", "0");
            if (thorn.getThornBadConditions() != null) {
                data.put("field_khar[und][0][field_khar_kasri][und][0][value]", thorn.getThornBadConditions().getAmount());
                data.put("field_khar[und][0][field_khar_zang][und][0][value]", thorn.getThornBadConditions().getRustiness());
                data.put("field_khar[und][0][field_khar_desc][und][0][value]", thorn.getThornBadConditions().getDescription());
            }
        }
    }

    private void stairBoltsField(Map<String, String> data, ProjectModel.StairBolts
            stairBolts) {
        if (Objects.equals(stairBolts.getCondition(), Constants.IS_GOOD)) {
            data.put("field_pich_pelle[und][0][field_good][und]", "1");

        } else if (Objects.equals(stairBolts.getCondition(), Constants.IS_BAD)) {

            data.put("field_pich_pelle[und][0][field_good][und]", "0");

            if (stairBolts.getStairBoltsBadConditions() != null) {
                data.put("field_pich_pelle[und][0][field_pich_pelle_kasri][und][0][value]", stairBolts.getStairBoltsBadConditions().getAmount());
                data.put("field_pich_pelle[und][0][field_pich_pelle_zang][und][0][value]", stairBolts.getStairBoltsBadConditions().getRustiness());
                data.put("field_pich_pelle[und][0][field_pich_pelle_tamir][und][0][value]", stairBolts.getStairBoltsBadConditions().getNiazeBeTamir());
                data.put("field_pich_pelle[und][0][field_pich_pelle_desc][und][0][value]", stairBolts.getStairBoltsBadConditions().getDescription());
            }

        }
    }

    private void pichoMohreField(Map<String, String> data, ProjectModel.BoltsAndNuts
            boltsAndNuts) {
        if (Objects.equals(boltsAndNuts.getCondition(), Constants.IS_GOOD)) {
            data.put("field_pich[und][0][field_good][und]", "1");
        } else if (Objects.equals(boltsAndNuts.getCondition(), Constants.IS_BAD) && boltsAndNuts.getBoltsAndNutsConditionsIfBad() != null) {

            if (boltsAndNuts.getBoltsAndNutsConditionsIfBad().getPichMohreItemList() != null)
                for (int i = 0; boltsAndNuts.getBoltsAndNutsConditionsIfBad().getPichMohreItemList().size() > i; i++) {
                    data.put("field_pich[und][" + i + "][field_good][und]", "0");

                    if (Objects.equals(boltsAndNuts.getBoltsAndNutsConditionsIfBad().getPichMohreItemList().get(i).getDescription(), context.getString(R.string.achar_keshi))) {

                        data.put("field_pich[und][" + i + "][field_pich_bad][und]", "achar");

                    } else if (Objects.equals(boltsAndNuts.getBoltsAndNutsConditionsIfBad().getPichMohreItemList().get(i).getDescription(), context.getString(R.string.kasri))) {

                        data.put("field_pich[und][" + i + "][field_pich_bad][und]", "kasri");

                    } else if (Objects.equals(boltsAndNuts.getBoltsAndNutsConditionsIfBad().getPichMohreItemList().get(i).getDescription(), context.getString(R.string.zang_zadegi))) {

                        data.put("field_pich[und][" + i + "][field_pich_bad][und]", "zang");

                    } else if (Objects.equals(boltsAndNuts.getBoltsAndNutsConditionsIfBad().getPichMohreItemList().get(i).getDescription(), context.getString(R.string.others))) {

                        data.put("field_pich[und][" + i + "][field_pich_bad][und]", "sayer");
                    }


                    if (Objects.equals(boltsAndNuts.getBoltsAndNutsConditionsIfBad().getPichMohreItemList().get(i).getSize(), context.getString(R.string.twelve)))
                        data.put("field_pich[und][" + i + "][field_size_tedad_12][und][0][value]", boltsAndNuts.getBoltsAndNutsConditionsIfBad().getPichMohreItemList().get(i).getAmount());
                    if (Objects.equals(boltsAndNuts.getBoltsAndNutsConditionsIfBad().getPichMohreItemList().get(i).getSize(), context.getString(R.string.fourteen)))
                        data.put("field_pich[und][" + i + "][field_size_tedad_14][und][0][value]", boltsAndNuts.getBoltsAndNutsConditionsIfBad().getPichMohreItemList().get(i).getAmount());
                    if (Objects.equals(boltsAndNuts.getBoltsAndNutsConditionsIfBad().getPichMohreItemList().get(i).getSize(), context.getString(R.string.sixteen)))
                        data.put("field_pich[und][" + i + "][field_size_tedad_16][und][0][value]", boltsAndNuts.getBoltsAndNutsConditionsIfBad().getPichMohreItemList().get(i).getAmount());
                    if (Objects.equals(boltsAndNuts.getBoltsAndNutsConditionsIfBad().getPichMohreItemList().get(i).getSize(), context.getString(R.string.others)))
                        data.put("field_pich[und][" + i + "][field_size_tedad_sayer][und][0][value]", boltsAndNuts.getBoltsAndNutsConditionsIfBad().getPichMohreItemList().get(i).getAmount());


                }

            data.put("field_pich[und][0][field_pich_desc][und][0][value]", boltsAndNuts.getBoltsAndNutsConditionsIfBad().getDescription());

        }
    }

    @SuppressWarnings("ConstantConditions")
    private void tabloField(Map<String, String> data, ProjectModel.Panel panel) {
        if (Objects.equals(panel.getCondition(), Constants.IS_GOOD)) {
            data.put("field_tablo[und][0][field_good][und]", "1");
        } else if (Objects.equals(panel.getCondition(), Constants.IS_BAD)) {
            data.put("field_tablo[und][0][field_good][und]", "0");

            if (panel.getPanelBadConditions() != null) {
                if (Objects.equals(panel.getPanelBadConditions().getPanelNumberCondition(), Constants.IS_CHECKED)) {
                    data.put("field_tablo[und][0][field_tablo_type][und][0]", "shomare");
                    if (panel.getPanelBadConditions().getPanelNumber() != null)
                        data.put("field_tablo[und][0][field_tablo_shomare_bad][und]", panelFiveSegmentedConvertor2(panel.getPanelBadConditions().getPanelNumber().getCondition()));
                }

                if (Objects.equals(panel.getPanelBadConditions().getPanelHavayeeCondition(), Constants.IS_CHECKED)) {
                    data.put("field_tablo[und][0][field_tablo_type][und][1]", "havaei");
                    if (panel.getPanelBadConditions().getPanelHavayee() != null)
                        data.put("field_tablo[und][0][field_tablo_havaei_bad][und]", panelFiveSegmentedConvertor2(panel.getPanelBadConditions().getPanelHavayee().getCondition()));
                }

                if (Objects.equals(panel.getPanelBadConditions().getPhasePanelCondition(), Constants.IS_CHECKED)) {
                    data.put("field_tablo[und][0][field_tablo_type][und][2]", "fazi");
                    if (panel.getPanelBadConditions().getPhasePanel() != null)
                        data.put("field_tablo[und][0][field_tablo_fazi_bad][und]", panelFiveSegmentedConvertor2(panel.getPanelBadConditions().getPhasePanel().getCondition()));
                }

                if (Objects.equals(panel.getPanelBadConditions().getPanelDomainCondition(), Constants.IS_CHECKED)) {
                    data.put("field_tablo[und][0][field_tablo_type][und][3]", "harim");
                    if (panel.getPanelBadConditions().getPanelDomain() != null)
                        data.put("field_tablo[und][0][field_tablo_harim_bad][und]", panelFiveSegmentedConvertor2(panel.getPanelBadConditions().getPanelDomain().getCondition()));
                }

                data.put("field_tablo[und][0][field_tablo_desc][und][0][value]", panel.getPanelBadConditions().getDescription());
            }
        }
    }

    private String panelFiveSegmentedConvertor2(String condition) {

        if (Objects.equals(condition, context.getString(R.string.without_board))) {
            return "faghed";
        } else if (Objects.equals(condition, context.getString(R.string.board_fading_out))) {
            return "kamrang";
        } else if (Objects.equals(condition, context.getString(R.string.board_cleaning))) {
            return "pak";
        } else if (Objects.equals(condition, context.getString(R.string.zang_zadegi))) {
            return "zang";
        } else if (Objects.equals(condition, context.getString(R.string.to_be_bolt_open))) {
            return "pichbaz";
        } else {
            return null;
        }

    }

    private void simZaminField(Map<String, String> data, ProjectModel.EarthWire earthWire) {
        if (Objects.equals(earthWire.getHealthCondition(), Constants.IS_GOOD)) {
            data.put("field_sim_zamin[und][0][field_good][und]", "1");
        } else if (Objects.equals(earthWire.getHealthCondition(), Constants.IS_BAD)) {
            data.put("field_sim_zamin[und][0][field_good][und]", "0");

            if (earthWire.getEarthWireBadConditions() != null) {
                if (Objects.equals(earthWire.getEarthWireBadConditions().getSerghtat(), Constants.IS_CHECKED)) {
                    data.put("field_sim_zamin[und][0][field_sim_zamin_bad][und][0]", "serghat");
                }

                if (Objects.equals(earthWire.getEarthWireBadConditions().getKolomp(), Constants.IS_CHECKED)) {
                    data.put("field_sim_zamin[und][0][field_sim_zamin_bad][und][1]", "klmp");
                }

                if (Objects.equals(earthWire.getEarthWireBadConditions().getParegi(), Constants.IS_CHECKED)) {
                    data.put("field_sim_zamin[und][0][field_sim_zamin_bad][und][2]", "paregi");
                }

                if (Objects.equals(earthWire.getEarthWireBadConditions().getPoosidegi(), Constants.IS_CHECKED)) {
                    data.put("field_sim_zamin[und][0][field_sim_zamin_bad][und][3]", "poosidegi");
                }

                if (Objects.equals(earthWire.getEarthWireBadConditions().getBiroonBoodaneSimeZamin(), Constants.IS_CHECKED)) {
                    data.put("field_sim_zamin[und][0][field_sim_zamin_bad][und][4]", "biroon");
                }

                data.put("field_sim_zamin[und][0][field_sim_zamin_desc][und][0][value]", earthWire.getEarthWireBadConditions().getOthers());
            }
        }
    }

    private void foundationField(Map<String, String> data, ProjectModel.Foundation
            foundation) {

        if (Objects.equals(foundation.getCondition(), Constants.IS_GOOD)) {
            data.put("field_foundation[und][0][field_good][und]", "1");
        } else if (Objects.equals(foundation.getCondition(), Constants.IS_BAD)) {

            data.put("field_foundation[und][0][field_good][und]", "0");
            if (foundation.getFoundationIfBad() != null) {
                data.put("field_foundation[und][0][field_khak_b][und][0][value]", foundation.getFoundationIfBad().getKhakBardari());
                data.put("field_foundation[und][0][field_khak_r][und][0][value]", foundation.getFoundationIfBad().getKhakRizi());
                data.put("field_foundation[und][0][field_mahar][und][0][value]", foundation.getFoundationIfBad().getTedadeMahar());
                data.put("field_foundation[und][0][field_foundation_sayer][und][0][value]", foundation.getFoundationIfBad().getOthers());
                data.put("field_foundation[und][0][field_foundation_desc][und][0][value]", foundation.getFoundationIfBad().getDescription());
                data.put("field_foundation[und][0][field_takhrib_beton][und]", fiveSegmentedConvertor(foundation.getFoundationIfBad().getConcreteDamage()));
                data.put("field_foundation[und][0][field_tarak_o][und]", fiveSegmentedConvertor(foundation.getFoundationIfBad().getHorizontalCrack()));
                data.put("field_foundation[und][0][field_tarak_a][und]", fiveSegmentedConvertor(foundation.getFoundationIfBad().getVerticalCrack()));
                data.put("field_foundation[und][0][field_field_khordegi][und]", fiveSegmentedConvertor(foundation.getFoundationIfBad().getConcreteEat()));
                data.put("field_foundation[und][0][field_toor_seil][und]", fiveSegmentedConvertor(foundation.getFoundationIfBad().getFloodNet()));

                if (Objects.equals(foundation.getFoundationIfBad().getGrassNeedsToBeCleaned(), Constants.IS_CHECKED)) {
                    data.put("field_foundation[und][0][field_paksazi_boote][und]", "1");
                    if (foundation.getFoundationIfBad().getIfGrassNeedsToBeCleaned() != null)
                        data.put("field_foundation[und][0][field_mizan_boote][und]", fiveSegmentedConvertor(foundation.getFoundationIfBad().getIfGrassNeedsToBeCleaned().getGrassCount()));

                } else {
                    data.put("field_foundation[und][0][field_paksazi_boote][und]", "0");
                }
            }
        }

    }

    private String fiveSegmentedConvertor(String conditionChecker) {

        if (Objects.equals(conditionChecker, context.getString(R.string.dose_not_have))) {
            return "nadarad";
        } else if (Objects.equals(conditionChecker, context.getString(R.string.low))) {
            return "kam";
        } else if (Objects.equals(conditionChecker, context.getString(R.string.medium))) {
            return "motevaset";
        } else if (Objects.equals(conditionChecker, context.getString(R.string.high))) {
            return "ziad";
        } else if (Objects.equals(conditionChecker, context.getString(R.string.very_high))) {
            return "khiliziad";
        } else {
            return null;
        }
    }

    private String fiveSegmentedConvertorFlood(String conditionChecker) {

        if (Objects.equals(conditionChecker, context.getString(R.string.dose_not_have))) {
            return "nadarad";
        } else if (Objects.equals(conditionChecker, context.getString(R.string.low))) {
            return "kam";
        } else if (Objects.equals(conditionChecker, context.getString(R.string.medium))) {
            return "motevaset";
        } else if (Objects.equals(conditionChecker, context.getString(R.string.high))) {
            return "ziad";
        } else if (Objects.equals(conditionChecker, context.getString(R.string.very_high))) {
            return "kheiliziad";
        } else {
            return null;
        }
    }

    private void dakalsField(Map<String, String> data, ProjectModel.DakalFieldes
            dakalFieldes) {
        data.put("field_dakal[und][0][field_dakal_name][und][0][value]", dakalFieldes.getTowerName());

        String dakalType = null;
        String noeDakaleSimani = null;
        if (dakalFieldes.getTowerType() != null) {
            if (Objects.equals(dakalFieldes.getTowerType().getType(), context.getString(R.string.telescopic))) {
                dakalType = "teleskopi";
            } else if (Objects.equals(dakalFieldes.getTowerType().getType(), context.getString(R.string.lattice))) {
                dakalType = "moshabak";
            } else if (Objects.equals(dakalFieldes.getTowerType().getType(), context.getString(R.string.cement_beam))) {
                dakalType = "simani";
                if (dakalFieldes.getTowerType().getCementElectricTowerType() != null)
                    noeDakaleSimani = dakalFieldes.getTowerType().getCementElectricTowerType().getCementElectricTowerType();
            }

            data.put("field_dakal[und][0][field_dakal_type_name][und]", dakalType);
        }

        String noeDakal = null;
        if (dakalFieldes.getNoeDakal() != null) {
            if (Objects.equals(dakalFieldes.getNoeDakal().getType(), context.getString(R.string.angel))) {
                noeDakal = "zavie";
            } else if (Objects.equals(dakalFieldes.getNoeDakal().getType(), context.getString(R.string.pendant))) {
                noeDakal = "aviz";
            }
        }

        data.put("field_dakal[und][0][field_dakal_noe][und]", noeDakal);
        data.put("field_dakal[und][0][field_tit_type][und]", noeDakaleSimani);

        if (dakalFieldes.getTowerNumber() != null) {
            String dakalsNumber = dakalFieldes.getTowerNumber().getNumber();
            data.put("field_dakal[und][0][field_shomare_dakal][und][0][value]", dakalsNumber);
        }

        if (dakalFieldes.getCircuitCount() != null)
            data.put("field_dakal[und][0][field_dakal_madar][und][0][value]", dakalFieldes.getCircuitCount().getCount());

        data.put("field_operation_time[und][0][field_timing][und]", dakalFieldes.getVaziateTakhir());

        if (Objects.equals(dakalFieldes.getVaziateTakhir(), "hurry") || Objects.equals(dakalFieldes.getVaziateTakhir(), "delay")) {
            data.put("field_operation_time[und][0][field_timing_reason][und]", dakalFieldes.getElateTakhir());
        }

        data.put("field_next_tower[und][0][value]", dakalFieldes.getNextTowerCode());

    }

    private void requestMustHaves(int mid, Map<String, String> data, String[]
            dateAndTime, ProjectModel.DakalFieldes dakalFieldes, String lat, String lng, String scanType) {
        if (dakalFieldes != null)
            data.put("title", dakalFieldes.getTowerName());
        data.put("type", "inspection");
        data.put("field_related_operation[und]", String.valueOf(mid));
        data.put("field_time[und][0][value][date]", dateAndTime[0]);
        data.put("field_time[und][0][value][time]", dateAndTime[1]);
        data.put("field_dakal_location[und][0][locpick][user_latitude]", lat);
        data.put("field_dakal_location[und][0][locpick][user_longitude]", lng);
        data.put("field_barcode_scan[und]", scanType);

        if (dakalFieldes != null && dakalFieldes.getBarCodeList() != null)
            for (int i = 0; dakalFieldes.getBarCodeList().size() > i; i++) {

                data.put("field_dakal_dispatching[und][" + i + "][value]", dakalFieldes.getBarCodeList().get(i).getNumber());

            }
    }


    private String mapJointBoxLabel(String label) {
        switch (label) {
            case "خوب":
                return "khoob";
            case "بد":
                return "bad";
            default:
                return "nadarad";
        }
    }


    private String repairTypeConvertToPersian(String label) {


        switch (label) {
            case "pahpad":
                return context.getString(R.string.pahpad);
            case "khodro":
                return context.getString(R.string.khodro);
            case "gorooh":
                return context.getString(R.string.goroh);
            case "karshenas":
                return context.getString(R.string.karshenas);
            case "harim":
                return context.getString(R.string.harim);
            case "tahvil":
                return context.getString(R.string.tahvil);
            case "khas":
                return context.getString(R.string.khas);
            case "termo":
                return context.getString(R.string.termo);
            case "zamin":
                return context.getString(R.string.zamin);
            default:
                return "";
        }
    }

    private List<MultipartBody.Part> prepareFileParts(List<String> paths) {
        List<MultipartBody.Part> partList = new ArrayList<>();
        partList.clear();
        for (String path : paths) {

            File file = new File(path);
            String mimeType = MimeUtils.getType(path);
            RequestBody requestFile =
                    RequestBody.create(
                            MediaType.parse(mimeType),
                            file
                    );
            String fileEncode = null;
            try {
                fileEncode = URLEncoder.encode(file.getName(), "utf-8");
                partList.add(MultipartBody.Part.createFormData("files[]", fileEncode, requestFile));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

        }


        return partList;
    }

    private void getSharedPreferences() {

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
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
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        preferences.edit().putString(CREDENTIALS_PREFERENCES, gson.toJson(sharedPreferencesCredentials)).apply();
    }
}

package com.controladad.boutia_pms.models;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.controladad.boutia_pms.models.database.IradatDakalEntity;
import com.controladad.boutia_pms.models.database.Mission_Tamirat_Entity;
import com.controladad.boutia_pms.models.retrofit_models.RetrofitModels;
import com.controladad.boutia_pms.models.shared_preferences_models.SharedPreferencesModels;
import com.controladad.boutia_pms.network.network_check.NoNetworkException;
import com.controladad.boutia_pms.network.retrofit.RetrofitApiService;
import com.controladad.boutia_pms.utility.BoutiaApplication;
import com.controladad.boutia_pms.utility.Constants;
import com.controladad.boutia_pms.view_models.Function;
import com.controladad.boutia_pms.view_models.GenericMethod;
import com.controladad.boutia_pms.view_models.UpdateDakalVM;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import lombok.Getter;
import lombok.Setter;
import retrofit2.HttpException;
import retrofit2.Response;
import saman.zamani.persiandate.PersianDate;
import saman.zamani.persiandate.PersianDateFormat;
import timber.log.Timber;

public class UpdateTamiratDakalInfoModel {

    private UpdateDakalVM repairMissionChoseVM;
    private List<RetrofitModels.Dakals> dakals = new ArrayList<>();
    private Disposable updateDakalListByidDisposable;
    @Setter
    private Function onSuccess;
    @Setter
    private Function onFailed;
    @Setter
    private GenericMethod<Integer> updateProgress;
    @Setter
    private Function onNoDakal;
    private String key;
    private List<RetrofitModels.Dakals> dakalLists;

    private SharedPreferencesModels.SharedPreferencesCredentials credentials;
    @Getter
    private Disposable disposable;

    public void updateDakalList(String key) {

        RetrofitApiService apiService = repairMissionChoseVM.getComponent().getService();

        this.key = key;
        getSharedPreferences();
        final String token = credentials.getToken();
        final String cookie = credentials.getCookie();
        final int[] progressPercentage = {0};
        final int[] finalProgressPercentage = {0};


        updateDakalListByidDisposable = apiService
                .getDakalsListById(token, cookie, "1", "0", key)
                .flatMap(x -> {
                    int count = (int) Math.ceil(Double.parseDouble(x.get(0).getDetailsOfDakal().getDakalsCount()) / 20);
                    return Observable
                            .range(0, count)
                            .map(v -> {
                                //progressPercentage[0] = v;
                                return v * 20;
                            })
                            .concatMap(d -> apiService
                                    .getDakalsListById(token, cookie, "20", d.toString(), key));
                })
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(dakalListLists -> {
                            progressPercentage[0] += dakalListLists.get(0).getDakals().size();
                            finalProgressPercentage[0] = ((progressPercentage[0]) * 100 / Integer.valueOf(dakalListLists.get(0).getDetailsOfDakal().getDakalsCount()));
                            dakals.addAll(dakalListLists.get(0).getDakals());
                            updateProgress.fun(finalProgressPercentage[0]);
                        },
                        throwable -> {
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
                            } else if (throwable instanceof NullPointerException) {
                                onNoDakal.fun();
                            } else {
                                Timber.d(throwable.getMessage());
                                onFailed.fun();
                            }
                        }, () -> {
                            Timber.d("Onsuccess in UpdateTamiratDakalInfoModel has been called");
                            dakalLists = new ArrayList<>();
                            dakalLists.addAll(dakals);
                            saveDakalListToDataBase();
                        });
    }


    public UpdateTamiratDakalInfoModel(UpdateDakalVM repairMissionChoseVM,
                                       Function onSuccess, Function onFailed, GenericMethod<Integer> updateProgress,
                                       Function onNoDakal) {
        this.repairMissionChoseVM = repairMissionChoseVM;
        this.onSuccess = onSuccess;
        this.onFailed = onFailed;
        this.updateProgress = updateProgress;
        this.onNoDakal = onNoDakal;
    }

    public void getMissionTamiratFromDataBase(OnSuccessListener<List<Mission_Tamirat_Entity>> onSuccessListener) {
        disposable = BoutiaApplication.INSTANCE.getDb()
                .getMissionTamiratDao()
                .getMissionTamirat()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(onSuccessListener::onSuccess
                        , throwable -> {
                            Timber.d(throwable.getMessage());
                        }
                );
    }

    public void getTakhMissionTamiratFromDataBase(OnSuccessListener<List<Mission_Tamirat_Entity>> onSuccessListener) {
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
        disposable = BoutiaApplication.INSTANCE.getDb()
                .getMissionTamiratDao()
                .getTakhirMission(currentDateString)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(onSuccessListener::onSuccess
                        , throwable -> {
                            Timber.d(throwable.getMessage());
                        }
                );
    }

    public void getTajilMissionTamiratFromDataBase(OnSuccessListener<List<Mission_Tamirat_Entity>> onSuccessListener) {
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
        disposable = BoutiaApplication.INSTANCE.getDb()
                .getMissionTamiratDao()
                .getTajilMission(currentDateString)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(onSuccessListener::onSuccess
                        , throwable -> {
                            Timber.d(throwable.getMessage());
                        }
                );
    }

    public void getOnTimeMissionTamiratFromDataBase(OnSuccessListener<List<Mission_Tamirat_Entity>> onSuccessListener) {
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
        disposable = BoutiaApplication.INSTANCE.getDb()
                .getMissionTamiratDao()
                .getOnTimeMission(currentDateString)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(onSuccessListener::onSuccess
                        , throwable -> {
                            Timber.d(throwable.getMessage());
                        }
                );
    }


    private void saveDakalListToDataBase() {

        Disposable disposable = Flowable.fromCallable(() -> {
            ArrayList<IradatDakalEntity> iradatDakalEntities = new ArrayList<>();

            for (RetrofitModels.Dakals line : dakalLists) {
                String foundation = line.getFoundations() != null ? stringFoundationBuilder(line.getFoundations()) : "";
                String tablo = line.getTablo() != null ? stringTabloBuilder(line.getTablo()) : "";
                String simzamin = line.getSimeZamin() != null ? stringSimZaminBuilder(line.getSimeZamin()) : "";


                String pich = "";
                if (line.getPich() != null) {

                    for (RetrofitModels.Pich p : line.getPich()) {
                        pich += stringPichBuilder(p);
                    }
                }
                String pichPelle = line.getPichePelle() != null ? stringPichPelleBuilder(line.getPichePelle()) : "";
                String khar = line.getKhar() != null ? stringKharBuilder(line.getKhar()) : "";

                String plate = "";
                if (line.getPlate() != null) {
                    for (RetrofitModels.Plate p : line.getPlate()) {
                        plate += stringPlateBuilder(p);
                    }
                }

                String nabshi = "";
                if (line.getNabshi() != null) {
                    for (RetrofitModels.Nabshi n : line.getNabshi()) {
                        nabshi += stringNabshiBuilder(n);
                    }
                }
                String seil = line.getSeil() != null ? stringSeilsBuilder(line.getSeil()) : "";

                String hadi = "";
                if (line.getHadiFaz() != null) {
                    for (RetrofitModels.HadiFaz hf : line.getHadiFaz()) {
                        hadi += stringHadiFazBuilder(hf);
                    }

                }
                String yaragh = "";
                if (line.getYaragh() != null) {
                    for (RetrofitModels.Yaragh y : line.getYaragh()) {
                        yaragh += stringYaraghBuilder(y);
                    }
                }
                String zmm = "";
                if (line.getZanjireMaghareVaMolhaghat() != null) {
                    for (RetrofitModels.ZMM zmms : line.getZanjireMaghareVaMolhaghat()) {
                        zmm += stringZMMBuilder(zmms);
                    }
                }

                ////TODO bayad anjam shavad mohafez
                String mohafez = "";
                if (line.getMohafez() != null) {
                    for (RetrofitModels.Mohafez mohafez1 : line.getMohafez()) {
                        mohafez += stringMohafezBuilder(mohafez1);
                    }
                }

                String lane = line.getLaneParande() != null ? stringLaneBuilder(line.getLaneParande()) : "";
                String ezafe = line.getEzafe() != null ? stringEzafeBuilder(line.getEzafe()) : "";

                iradatDakalEntities.add(new IradatDakalEntity(Integer.valueOf(line.getMid()), false,
                        line.getBarcode(), foundation, tablo, simzamin,
                        pich, pichPelle, khar, plate,
                        nabshi, seil, hadi, yaragh, zmm,
                        mohafez, lane, ezafe, line.getTimeStamp()));
            }

            BoutiaApplication.INSTANCE.getDb().getIradatDakalDao().deleteIradatDakalWithMID(Integer.parseInt(key));
            BoutiaApplication.INSTANCE.getDb().getIradatDakalDao().insertIradatDakal(iradatDakalEntities);
            return false;
        }).subscribeOn(Schedulers.io())
                .subscribe((s) -> {
                    PersianDate pdate = new PersianDate();
                    PersianDateFormat persianDateFormat = new PersianDateFormat("Y/m/d H:i:s");
                    String date = persianDateFormat.format(pdate);
                    BoutiaApplication.INSTANCE.getDb()
                            .getMissionTamiratDao()
                            .updateTamirDate(Integer.valueOf(key), date);
                    onSuccess.fun();
                }, throwable -> {
                    onFailed.fun();
                    Timber.d("Error Saving Tamir Mission to database");
                });

    }

    private String stringFoundationBuilder(RetrofitModels.Foundations foundation) {
        String foundations = "";
        if (foundation.getKhak() != null)
            foundations += foundation.getKhak().getLabel() + " : " + foundation.getKhak().getData() + " متر مکعب\n";
        if (foundation.getMahar() != null)
            foundations += foundation.getMahar().getLabel() + " : " + foundation.getMahar().getData() + " عدد\n";
        if (foundation.getSayer() != null)
            foundations += foundation.getSayer().getLabel() + " : " + foundation.getSayer().getData() + "\n";
        if (foundation.getTakhribBeton() != null)
            foundations += foundation.getTakhribBeton().getLabel() + " : " + mapValueToPerisan(foundation.getTakhribBeton().getData()) + "\n";
        if (foundation.getTarakAmoodi() != null)
            foundations += foundation.getTarakAmoodi().getLabel() + " : " + mapValueToPerisan(foundation.getTarakAmoodi().getData()) + "\n";
        if (foundation.getTarakOfoghi() != null)
            foundations += foundation.getTarakOfoghi().getLabel() + " : " + mapValueToPerisan(foundation.getTarakOfoghi().getData()) + "\n";
        if (foundation.getKhordegi() != null)
            foundations += foundation.getKhordegi().getLabel() + " : " + mapValueToPerisan(foundation.getKhordegi().getData()) + "\n";
        if (foundation.getToorSeil() != null)
            foundations += foundation.getToorSeil().getLabel() + " : " + mapValueToPerisan(foundation.getToorSeil().getData()) + "\n";
        if (foundation.getPaksaziBoote() != null) {
            foundations += foundation.getPaksaziBoote().getLabel() + " : " + mapValueToPerisan(foundation.getPaksaziBoote().getData()) + "\n";
            if (foundation.getMizaneBoote() != null)
                foundations += foundation.getMizaneBoote().getLabel() + " : " + mapValueToPerisan(foundation.getMizaneBoote().getData()) + "\n";
        }
        if (foundation.getDesc() != null)
            foundations += foundation.getDesc().getLabel() + " : " + foundation.getDesc().getData() + "\n";

        return foundations;
    }

    private String stringSimZaminBuilder(RetrofitModels.SimZamin simeZamins) {
        String simZamin = "";
        if (simeZamins.getSimZaminBad() != null) {
            simZamin += simeZamins.getSimZaminBad().getLabel() + " : ";
            if (simeZamins.getSimZaminBad().getData() != null)
                for (RetrofitModels.ValuesOfDataArray value : simeZamins.getSimZaminBad().getData()) {
                    simZamin += mapValueToPerisan(value.getValue()) + "، ";
                }
            simZamin += "\n";
        }
        if (simeZamins.getSimZaminDesc() != null)
            simZamin += simeZamins.getSimZaminDesc().getLabel() + " : " + simeZamins.getSimZaminDesc().getData() + "\n";

        return simZamin;
    }


    private String stringTabloBuilder(RetrofitModels.Tablo tablos) {
        String tablo = "";
        if (tablos.getTabloFaziBad() != null)
            tablo += tablos.getTabloFaziBad().getLabel() + " : " + mapValueToPerisan(tablos.getTabloFaziBad().getData()) + "\n";
        if (tablos.getTabloHarimBad() != null)
            tablo += tablos.getTabloHarimBad().getLabel() + " : " + mapValueToPerisan(tablos.getTabloHarimBad().getData()) + "\n";
        if (tablos.getTabloHavayeeBad() != null)
            tablo += tablos.getTabloHavayeeBad().getLabel() + " : " + mapValueToPerisan(tablos.getTabloHavayeeBad().getData()) + "\n";
        if (tablos.getTabloShomareBad() != null)
            tablo += tablos.getTabloShomareBad().getLabel() + " : " + mapValueToPerisan(tablos.getTabloShomareBad().getData()) + "\n";
        if (tablos.getTabloDesc() != null)
            tablo += tablos.getTabloDesc().getLabel() + " : " + tablos.getTabloDesc().getData() + "\n";

        return tablo;
    }

    private String stringPichBuilder(RetrofitModels.Pich pichs) {
        String pich = "";
        if (pichs.getPichBad() != null)
            pich += pichs.getPichBad().getLabel() + " : " + mapValueToPerisan(pichs.getPichBad().getData()) + "\n";
        if (pichs.getPichTedad12() != null)
            pich += pichs.getPichTedad12().getLabel() + " : " + pichs.getPichTedad12().getData() + " عدد\n";
        if (pichs.getPichTedad14() != null)
            pich += pichs.getPichTedad14().getLabel() + " : " + pichs.getPichTedad14().getData() + " عدد\n ";
        if (pichs.getPichTedad16() != null)
            pich += pichs.getPichTedad16().getLabel() + " : " + pichs.getPichTedad16().getData() + " عدد\n";
        if (pichs.getPichTedadSayer() != null)
            pich += pichs.getPichTedadSayer().getLabel() + " : " + pichs.getPichTedadSayer().getData() + " عدد\n";
        if (pichs.getPichDescription() != null)
            pich += pichs.getPichDescription().getLabel() + " : " + pichs.getPichDescription().getData() + "\n";
        return pich;
    }

    private String stringPichPelleBuilder(RetrofitModels.PichPelle pichPelles) {
        String pichPelle = "";
        if (pichPelles.getPichePelleKasri() != null)
            pichPelle += pichPelles.getPichePelleKasri().getLabel() + " : " + pichPelles.getPichePelleKasri().getData() + " عدد\n";
        if (pichPelles.getPichePelleZang() != null)
            pichPelle += pichPelles.getPichePelleZang().getLabel() + " : " + pichPelles.getPichePelleZang().getData() + " عدد\n";
        if (pichPelles.getPichePelleTamir() != null)
            pichPelle += pichPelles.getPichePelleTamir().getLabel() + " : " + pichPelles.getPichePelleTamir().getData() + " عدد\n";
        if (pichPelles.getPichePelleDisc() != null)
            pichPelle += pichPelles.getPichePelleDisc().getLabel() + " : " + pichPelles.getPichePelleDisc().getData() + "\n";

        return pichPelle;
    }

    private String stringKharBuilder(RetrofitModels.Khar khars) {
        String khar = "";
        if (khars.getKharKasri() != null)
            khar += khars.getKharKasri().getLabel() + " : " + khars.getKharKasri().getData() + " عدد\n";
        if (khars.getKharZang() != null)
            khar += khars.getKharZang().getLabel() + " : " + khars.getKharZang().getData() + " عدد\n";
        if (khars.getKharDesc() != null)
            khar += khars.getKharDesc().getLabel() + " : " + khars.getKharDesc().getData() + "\n";
        return khar;
    }

    private String stringPlateBuilder(RetrofitModels.Plate plates) {
        String plate = "";
        if (plates.getPlatePosition() != null)
            plate += plates.getPlatePosition().getLabel() + " : " + plates.getPlatePosition().getData() + "\n";
        if (plates.getPlateNumber() != null)
            plate += plates.getPlateNumber().getLabel() + " : " + plates.getPlateNumber().getData() + "\n";
        if (plates.getPlateKasri() != null)
            plate += plates.getPlateKasri().getLabel() + " : " + plates.getPlateKasri().getData() + " عدد\n";
        if (plates.getPlateZang() != null)
            plate += plates.getPlateZang().getLabel() + " : " + plates.getPlateZang().getData() + " عدد\n";
        if (plates.getPlateKasriPich() != null)
            plate += plates.getPlateKasriPich().getLabel() + " : " + plates.getPlateKasriPich().getData() + " عدد\n";
        if (plates.getPlateShekastegi() != null)
            plate += plates.getPlateShekastegi().getLabel() + " : " + plates.getPlateShekastegi().getData() + " عدد\n";
        if (plates.getPlateDesc() != null)
            plate += plates.getPlateDesc().getLabel() + " : " + plates.getPlateDesc().getData() + "\n";
        return plate;
    }

    private String stringNabshiBuilder(RetrofitModels.Nabshi nabshis) {
        String nabshi = "";
        if (nabshis.getNabshiPostion() != null)
            nabshi += nabshis.getNabshiPostion().getLabel() + " : " + nabshis.getNabshiPostion().getData() + "\n";

        if (nabshis.getNabshiShomareh() != null)
            nabshi += nabshis.getNabshiShomareh().getLabel() + " : " + nabshis.getNabshiShomareh().getData() + "\n";

        if (nabshis.getNabshiZang() != null)
            nabshi += nabshis.getNabshiZang().getLabel() + " : " + nabshis.getNabshiZang().getData() + " عدد\n";

        if (nabshis.getNabshiEnhena() != null)
            nabshi += nabshis.getNabshiEnhena().getLabel() + " : " + nabshis.getNabshiEnhena().getData() + " عدد\n";

        if (nabshis.getNabshiSarbaz() != null)
            nabshi += nabshis.getNabshiSarbaz().getLabel() + " : " + nabshis.getNabshiSarbaz().getData() + " عدد\n";

        if (nabshis.getNabshiSerghatSize() != null)
            nabshi += nabshis.getNabshiSerghatSize().getLabel() + " : " + nabshis.getNabshiSerghatSize().getData() + " عدد\n";

        if (nabshis.getNabshiSerghatTedad() != null)
            nabshi += nabshis.getNabshiSerghatTedad().getLabel() + " : " + nabshis.getNabshiSerghatTedad().getData() + " عدد\n";

        if (nabshis.getNabshiSerghatTool() != null)
            nabshi += nabshis.getNabshiSerghatTool().getLabel() + " : " + nabshis.getNabshiSerghatTool().getData() + " متر\n";

        if (nabshis.getNabshiDesc() != null)
            nabshi += nabshis.getNabshiDesc().getLabel() + " : " + nabshis.getNabshiDesc().getData() + "\n";
        return nabshi;
    }

    private String stringSeilsBuilder(RetrofitModels.Seil seils) {

        String seil = "";

        if (seils.getSeilKhatar() != null)
            seil += seils.getSeilKhatar().getLabel() + " : " + mapValueToPerisan(seils.getSeilKhatar().getData()) + "\n";
        if (seils.getSeilKhesarat() != null)
            seil += seils.getSeilKhesarat().getLabel() + " : " + mapValueToPerisan(seils.getSeilKhesarat().getData()) + "\n";

        return seil;
    }

    private String stringEzafeBuilder(RetrofitModels.Ezafe ezafes) {

        String ezafe = "";

        if (ezafes.getEzafeGard() != null)
            ezafe += ezafes.getEzafeGard().getLabel() + " : " + ezafes.getEzafeGard().getData() + " عدد\n";
        if (ezafes.getEzafeFaz() != null)
            ezafe += ezafes.getEzafeFaz().getLabel() + " : " + ezafes.getEzafeFaz().getData() + " عدد\n";
        if (ezafes.getEzafeDakal() != null)
            ezafe += ezafes.getEzafeDakal().getLabel() + " : " + ezafes.getEzafeDakal().getData() + " عدد\n";
        if (ezafes.getEzafeDesc() != null)
            ezafe += ezafes.getEzafeDesc().getLabel() + " : " + ezafes.getEzafeDesc().getData() + "\n";

        return ezafe;
    }

    private String stringLaneBuilder(RetrofitModels.LaneParande laneParandes) {

        String laneParande = "";

        if (laneParandes.getLaneGard() != null)
            laneParande += laneParandes.getLaneGard().getLabel() + " : " + laneParandes.getLaneGard().getData() + " عدد\n";
        if (laneParandes.getLaneFaz() != null)
            laneParande += laneParandes.getLaneFaz().getLabel() + " : " + laneParandes.getLaneFaz().getData() + " عدد\n";
        if (laneParandes.getLaneDakal() != null)
            laneParande += laneParandes.getLaneDakal().getLabel() + " : " + laneParandes.getLaneDakal().getData() + " عدد\n";
        if (laneParandes.getLaneDesc() != null)
            laneParande += laneParandes.getLaneDesc().getLabel() + " : " + laneParandes.getLaneDesc().getData() + "\n";

        return laneParande;
    }


    private String stringHadiFazBuilder(RetrofitModels.HadiFaz hadiFazes) {
        String hadiFaz = "";

        if (hadiFazes.getFazA() != null)
            hadiFaz += hadiFazes.getFazA().getLabel() + " : " + mapValueToPerisan(hadiFazes.getFazA().getData()) + "\n";
        if (hadiFazes.getHadiA() != null)
            hadiFaz += hadiFazes.getHadiA().getLabel() + " : " + mapValueToPerisan(hadiFazes.getHadiA().getData()) + "\n";
        if (hadiFazes.getFazADesc() != null)
            hadiFaz += hadiFazes.getFazADesc().getLabel() + " : " + mapValueToPerisan(hadiFazes.getFazADesc().getData()) + "\n";

        if (hadiFazes.getFazB() != null)
            hadiFaz += hadiFazes.getFazB().getLabel() + " : " + mapValueToPerisan(hadiFazes.getFazB().getData()) + "\n";
        if (hadiFazes.getHadiB() != null)
            hadiFaz += hadiFazes.getHadiB().getLabel() + " : " + mapValueToPerisan(hadiFazes.getHadiB().getData()) + "\n";
        if (hadiFazes.getFazBDesc() != null)
            hadiFaz += hadiFazes.getFazBDesc().getLabel() + " : " + mapValueToPerisan(hadiFazes.getFazBDesc().getData()) + "\n";

        if (hadiFazes.getFazC() != null)
            hadiFaz += hadiFazes.getFazC().getLabel() + " : " + mapValueToPerisan(hadiFazes.getFazC().getData()) + "\n";
        if (hadiFazes.getHadiB() != null)
            hadiFaz += hadiFazes.getHadiB().getLabel() + " : " + mapValueToPerisan(hadiFazes.getHadiB().getData()) + "\n";
        if (hadiFazes.getFazCDesc() != null)
            hadiFaz += hadiFazes.getFazCDesc().getLabel() + " : " + mapValueToPerisan(hadiFazes.getFazCDesc().getData()) + "\n";

        return hadiFaz;
    }

    private String stringYaraghBuilder(RetrofitModels.Yaragh yaraghs) {
        String yaragh = "";

        if (yaraghs.getYaraghIradatA() != null)
            yaragh += yaraghs.getYaraghIradatA().getLabel() + " : " + mapValueToPerisan(yaraghs.getYaraghIradatA().getData()) + "\n";
        if (yaraghs.getYaraghDescA() != null)
            yaragh += yaraghs.getYaraghDescA().getLabel() + " : " + mapValueToPerisan(yaraghs.getYaraghDescA().getData()) + "\n";

        if (yaraghs.getYaraghIradatB() != null)
            yaragh += yaraghs.getYaraghIradatB().getLabel() + " : " + mapValueToPerisan(yaraghs.getYaraghIradatB().getData()) + "\n";
        if (yaraghs.getYaraghDescB() != null)
            yaragh += yaraghs.getYaraghDescB().getLabel() + " : " + mapValueToPerisan(yaraghs.getYaraghDescB().getData()) + "\n";

        if (yaraghs.getYaraghIradatC() != null)
            yaragh += yaraghs.getYaraghIradatC().getLabel() + " : " + mapValueToPerisan(yaraghs.getYaraghIradatC().getData()) + "\n";
        if (yaraghs.getYaraghDescC() != null)
            yaragh += yaraghs.getYaraghDescC().getLabel() + " : " + mapValueToPerisan(yaraghs.getYaraghDescC().getData()) + "\n";

        return yaragh;
    }

    private String stringMohafezBuilder(RetrofitModels.Mohafez mohafezes) {
        String mohafez = "";

        if (mohafezes.getMohafezType() != null)
            if (!Objects.equals(mohafezes.getMohafezType().getData(), "faghed"))
                mohafez += mohafezes.getMohafezType().getLabel() + " : " + mapValueToPerisan(mohafezes.getMohafezType().getData()) + "\n";

        if (mohafezes.getMohafezShieldType() != null)
            mohafez += mohafezes.getMohafezShieldType().getLabel() + " : " + mapValueToPerisan(mohafezes.getMohafezShieldType().getData()) + "\n";

        if (mohafezes.getMohafezJointBox() != null)
            mohafez += mohafezes.getMohafezJointBox().getLabel() + " : " + mapValueToPerisan(mohafezes.getMohafezJointBox().getData()) + "\n";

        if (mohafezes.getMohafezVaziateShield() != null)
            mohafez += mohafezes.getMohafezVaziateShield().getLabel() + " : ";

        if (Objects.equals(mohafezes.getMohafezVaziateShield().getData(), "0"))
            mohafez += "بد\n";
        else
            mohafez += "خوب\n";


        if (mohafezes.getMohafezIradat() != null) {
            if (mohafezes.getMohafezIradat().getData() != null) {
                mohafez += mohafezes.getMohafezIradat().getLabel() + " : ";
                for (RetrofitModels.ValuesOfDataArray value : mohafezes.getMohafezIradat().getData()) {
                    mohafez += mapValueToPerisan(value.getValue()) + "، ";
                }

            }
        }
        mohafez += "\n";

        if (mohafezes.getMohafezDesc() != null)
            mohafez += mohafezes.getMohafezDesc().getLabel() + " : " + mapValueToPerisan(mohafezes.getMohafezDesc().getData()) + "\n";


        return mohafez;
    }

    private String stringZMMBuilder(RetrofitModels.ZMM zanjireMaghareVaMolhaghats) {
        String zanjireMaghareVaMolhaghat = "";
        if (zanjireMaghareVaMolhaghats.getZmmTypeA() != null)
            zanjireMaghareVaMolhaghat += zanjireMaghareVaMolhaghats.getZmmTypeA().getLabel() + " : " + mapValueToPerisan(zanjireMaghareVaMolhaghats.getZmmTypeA().getData()) + "\n";
        if (zanjireMaghareVaMolhaghats.getZmmAAloodegi() != null)
            zanjireMaghareVaMolhaghat += zanjireMaghareVaMolhaghats.getZmmAAloodegi().getLabel() + " : " + mapValueToPerisan(zanjireMaghareVaMolhaghats.getZmmAAloodegi().getData()) + "\n";
        if (zanjireMaghareVaMolhaghats.getZmmAAsib() != null)
            zanjireMaghareVaMolhaghat += zanjireMaghareVaMolhaghats.getZmmAAsib().getLabel() + " : " + mapValueToPerisan(zanjireMaghareVaMolhaghats.getZmmAAsib().getData()) + "\n";
        if (zanjireMaghareVaMolhaghats.getZmmAPin() != null)
            zanjireMaghareVaMolhaghat += zanjireMaghareVaMolhaghats.getZmmAPin().getLabel() + " : " + mapValueToPerisan(zanjireMaghareVaMolhaghats.getZmmAPin().getData()) + "\n";
        if (zanjireMaghareVaMolhaghats.getZmmADesc() != null)
            zanjireMaghareVaMolhaghat += zanjireMaghareVaMolhaghats.getZmmADesc().getLabel() + " : " + mapValueToPerisan(zanjireMaghareVaMolhaghats.getZmmADesc().getData()) + "\n";


        if (zanjireMaghareVaMolhaghats.getZmmTypeB() != null)
            zanjireMaghareVaMolhaghat += zanjireMaghareVaMolhaghats.getZmmTypeB().getLabel() + " : " + mapValueToPerisan(zanjireMaghareVaMolhaghats.getZmmTypeB().getData()) + "\n";
        if (zanjireMaghareVaMolhaghats.getZmmBAloodegi() != null)
            zanjireMaghareVaMolhaghat += zanjireMaghareVaMolhaghats.getZmmBAloodegi().getLabel() + " : " + mapValueToPerisan(zanjireMaghareVaMolhaghats.getZmmBAloodegi().getData()) + "\n";
        if (zanjireMaghareVaMolhaghats.getZmmBAsib() != null)
            zanjireMaghareVaMolhaghat += zanjireMaghareVaMolhaghats.getZmmBAsib().getLabel() + " : " + mapValueToPerisan(zanjireMaghareVaMolhaghats.getZmmBAsib().getData()) + "\n";
        if (zanjireMaghareVaMolhaghats.getZmmBPin() != null)
            zanjireMaghareVaMolhaghat += zanjireMaghareVaMolhaghats.getZmmBPin().getLabel() + " : " + mapValueToPerisan(zanjireMaghareVaMolhaghats.getZmmBPin().getData()) + "\n";
        if (zanjireMaghareVaMolhaghats.getZmmBDesc() != null)
            zanjireMaghareVaMolhaghat += zanjireMaghareVaMolhaghats.getZmmBDesc().getLabel() + " : " + mapValueToPerisan(zanjireMaghareVaMolhaghats.getZmmBDesc().getData()) + "\n";


        if (zanjireMaghareVaMolhaghats.getZmmTypeC() != null)
            zanjireMaghareVaMolhaghat += zanjireMaghareVaMolhaghats.getZmmTypeC().getLabel() + " : " + mapValueToPerisan(zanjireMaghareVaMolhaghats.getZmmTypeC().getData()) + "\n";
        if (zanjireMaghareVaMolhaghats.getZmmCAloodegi() != null)
            zanjireMaghareVaMolhaghat += zanjireMaghareVaMolhaghats.getZmmCAloodegi().getLabel() + " : " + mapValueToPerisan(zanjireMaghareVaMolhaghats.getZmmCAloodegi().getData()) + "\n";
        if (zanjireMaghareVaMolhaghats.getZmmCAsib() != null)
            zanjireMaghareVaMolhaghat += zanjireMaghareVaMolhaghats.getZmmCAsib().getLabel() + " : " + mapValueToPerisan(zanjireMaghareVaMolhaghats.getZmmCAsib().getData()) + "\n";
        if (zanjireMaghareVaMolhaghats.getZmmCPin() != null)
            zanjireMaghareVaMolhaghat += zanjireMaghareVaMolhaghats.getZmmCPin().getLabel() + " : " + mapValueToPerisan(zanjireMaghareVaMolhaghats.getZmmCPin().getData()) + "\n";
        if (zanjireMaghareVaMolhaghats.getZmmCDesc() != null)
            zanjireMaghareVaMolhaghat += zanjireMaghareVaMolhaghats.getZmmCDesc().getLabel() + " : " + mapValueToPerisan(zanjireMaghareVaMolhaghats.getZmmCDesc().getData()) + "\n";

        return zanjireMaghareVaMolhaghat;
    }

    private String mapValueToPerisan(String key) {
        switch (key) {
            case "0":
                return "خیر";
            case "1":
                return "بله";
            case "nadarad":
                return "ندارد";
            case "kam":
                return "کم";
            case "motevaset":
                return "متوسط";
            case "ziad":
                return "کم";
            case "khiliziad":
                return "خیلی زیاد";
            case "Darad":
                return "دارد";
            case "darad":
                return "دارد";
            case "serghat":
                return "سرقت";
            case "poosidegi":
                return "پوسیدگی";
            case "biroon":
                return "بیرون بودن سیم زمین";
            case "klmp":
                return "کلمپ";
            case "paregi":
                return "پارگی سیم";
            case "sayer":
                return "سایر";

            case "shomare":
                return "تابلو شماره";
            case "faghed":
                return "فاقد تابلو";
            case "kamrang":
                return "کم رنگ شدن تابلو";
            case "pak":
                return "پاک شدن تابلو";
            case "zang":
                return "زنگ زدگی";
            case "pichbaz":
                return "باز بودن پیچ تابلو";
            case "havaei":
                return "تابلو هوایی";
            case "fazi":
                return "تابلو فازی";
            case "harim":
                return "تابلو حریم";
            case "achar":
                return "آچار کشی";
            case "kasri":
                return "کسری";

            case "kheiliziad":
                return "خیلی زیاد";
            case "jamper":
                return "جمپر";
            case "spacer":
                return "اسپیسر";
            case "ripper":
                return "ریپیر";
            case "miani":
                return "لوله پرس میانی";
            case "entehaei":
                return "لوله پرس انتهایی";
            case "bandage":
                return "بانداژ";
            case "damper":
                return "دمپر";
            case "armorad":
                return "آرموراد";
            case "vazne":
                return "وزنه";
            case "shotor":
                return "کلمپ انتهایی پیچی";
            case "bargh":
                return "جرقه گیر";
            case "kafi":
                return "کفی";
            case "kero":
                return "کرونارینگ";
            case "ettesal":
                return "اتصالات";
            case "seramik":
                return "سرامیکی";
            case "shishe":
                return "شیشه ای";
            case "silicon":
                return "سیلیکون";
            case "tarkibi":
                return "ترکیبی";
            case "kaj":
                return "کج بودن زنجیر";
            case "afset":
                return "آفست";
            case "pich":
                return "نداشتن پیچ و مهره";
            case "lab":
                return "لب پریدگی";
            case "ashpel":
                return "نداشتن اشپیل";
            case "aloodegi":
                return "آلودگی";
            case "pin":
                return "در حال بیرون آمدن پین مقره";
            case "shekastegi":
                return "شکستگی";
            case "sookhtegi":
                return "سوختگی";
            case "khordegi":
                return "خوردگی";
            case "tak":
                return "تک شیلد";
            case "do":
                return "دو شیلد";
            case "khoob":
                return "خوب";

            case "opgw":
                return "OPGW";
            case "wire":
                return "Shield Wire";
            case "bad":
                return "بد";
            case "peres":
                return "لوله پرس";
            case "aramorad":
                return "آرموراد";
            case "eshpil":
                return "اشپیل";
            case "sakht":
                return "ساخت و ساز";
            case "keshavarzi":
                return "کشاورزی";
            case "derakht":
                return "درخت";
            case "jade":
                return "جاده";
            case "sookht":
                return "لوله های سوخت رسانی";
            default:
                return "";
        }
    }

    private void getSharedPreferences() {

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(repairMissionChoseVM.getActivity());
        Gson gson = new GsonBuilder().create();

        if (credentials == null) {
            String value = preferences.getString(Constants.CREDENTIALS_PREFERENCES, "");

            if (value.equals("")) {
                credentials = null;
            } else {
                credentials = gson.fromJson(value, SharedPreferencesModels.SharedPreferencesCredentials.class);
            }
        }

    }
}
package com.controladad.boutia_pms.models;

import com.controladad.boutia_pms.location.LocationManager;
import com.controladad.boutia_pms.models.data_models.ProjectModel;
import com.controladad.boutia_pms.models.database.BazdidYaTamirDao;
import com.controladad.boutia_pms.models.database.BazdidYaTamirEntity;
import com.controladad.boutia_pms.utility.BoutiaApplication;
import com.controladad.boutia_pms.utility.Constants;
import com.controladad.boutia_pms.view_models.Function;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import saman.zamani.persiandate.PersianDate;
import saman.zamani.persiandate.PersianDateFormat;
import timber.log.Timber;

public class DataBaseReviewModel {
    private Function function;
    private BazdidYaTamirDao bazdidYaTamirDao;
    private Disposable disposable;

    private BazdidYaTamirDao getBazdidYaTamirDao() {
        if (bazdidYaTamirDao == null)
            bazdidYaTamirDao = BoutiaApplication.INSTANCE.getDb().getBazdidYaTamir();
        return bazdidYaTamirDao;
    }

    public DataBaseReviewModel(Function function) {
        this.function = function;
    }

    public void addDataToDataBase(ProjectModel.ReviewModel reviewModel) {
        BazdidYaTamirEntity bazdidYaTamirEntity = getBazdidYaTamirEntity(reviewModel);
        Disposable disposable = Flowable.fromCallable(() -> {
            getBazdidYaTamirDao().insertBazdidYaTamir(bazdidYaTamirEntity);
            return false;
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe((s) -> {
                        },
                        throwable -> {
                        },
                        () -> function.fun());

    }

    private BazdidYaTamirEntity getBazdidYaTamirEntity(ProjectModel.ReviewModel reviewModel) {
        int mid = reviewModel.getMid();
       /* Date c = Calendar.getInstance().getTime();
        //System.out.println("Current time => " + c);

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:MM:ss",new Locale("en"));
        String formattedDate = df.format(c);
        Date currentTime = Calendar.getInstance().getTime();*/

        PersianDate pdate = new PersianDate();
        PersianDateFormat persianDateFormat = new PersianDateFormat("Y/m/d H:i:s");
        String date = persianDateFormat.format(pdate);

        LocationManager locationManager = LocationManager.getInstance(BoutiaApplication.INSTANCE, reviewModel.getMid(), Constants.BAZDID);

        String[] lat = new String[1];
        String[] lng = new String[1];

        lat[0] = "0";
        lng[0] = "1";

        disposable = locationManager.getLocation()
                .subscribe(location -> {
                    lat[0] = String.valueOf(location.getLatitude());
                    lng[0] = String.valueOf(location.getLongitude());
                }, throwable -> {
                    Timber.d(throwable.getMessage());
                });

        String type = Constants.BAZDID;
        String fieldDakal = json(getDakalFields(reviewModel));
        String foundation = json(reviewModel.getFoundation());
        String simeZamin = json(reviewModel.getEarthWire());
        String tablo = json(reviewModel.getPanel());
        String pich = json(reviewModel.getBoltsAndNuts());
        String pichePele = json(reviewModel.getStairBolts());
        String khaar = json(reviewModel.getThorn());
        String plate = json(reviewModel.getPlate());
        String nabshi = json(reviewModel.getCorner());
        String seil = json(reviewModel.getFloodCondition());
        //  String hadi = new Gson().toJson(reviewModel.getHadiHayePhaseVaMolhaghatAList());
        List<List<ProjectModel.HadiHayePhaseVaMolhaghat>> hadiha = new ArrayList<>();
//        reviewModel.getHadiHayePhaseVaMolhaghatAList(), reviewModel.getHadiHayePhaseVaMolhaghatBList(), reviewModel.getHadiHayePhaseVaMolhaghatCList()};
        hadiha.add(0, reviewModel.getHadiHayePhaseVaMolhaghatAList());
        hadiha.add(1, reviewModel.getHadiHayePhaseVaMolhaghatBList());
        hadiha.add(2, reviewModel.getHadiHayePhaseVaMolhaghatCList());
        String hadi = json(hadiha);
        String yaragh = json(reviewModel.getFittingsList());
        String zanjireMaghare = json(reviewModel.getIsolationChainsList());
        String simeMohafez = json(reviewModel.getSimeMohafezVaMolhaghat());
        String laneParande = json(reviewModel.getLaneParande());
        String ashyaEzafe = json(reviewModel.getAshiaEzafe());
        String khamooshiKhat = json(reviewModel.getKhamooshKardaneKhat());
        String dakalBohrani = json(reviewModel.getDakalBohrani());
        String jadeAsli = json(reviewModel.getTaghatoBaJadeAsli());
        String mavane = json(reviewModel.getMavane());
        String ensheab = json(reviewModel.getEnsheab());
        String taghatoBa20k = json(reviewModel.getTaghatoBa20k());
        String loole = json(reviewModel.getLoolePressMiani());
        String harim = json(reviewModel.getVaziateHarim());
        String masir = json(reviewModel.getVaziateMasir());
        String imagePathList = json(reviewModel.getImagePathList());
        String mGoyEkhtar = json(reviewModel.getGoy());
        String jooshkari = json(reviewModel.getJooshkari());

        BazdidYaTamirEntity bazdidYaTamirEntity = new BazdidYaTamirEntity(mid, lat[0], lng[0], date, false, type, fieldDakal,
                foundation, simeZamin, tablo, pich, pichePele, khaar, plate, nabshi, seil, hadi, yaragh, zanjireMaghare,
                simeMohafez, laneParande, ashyaEzafe, khamooshiKhat, jadeAsli, mavane, ensheab, taghatoBa20k, loole,
                harim, masir, imagePathList, "",reviewModel.getScanType(),mGoyEkhtar, dakalBohrani);

        bazdidYaTamirEntity.setMJoshkari(jooshkari);

        return bazdidYaTamirEntity;
    }

    private String json(Object object) {
        Gson gson = new GsonBuilder().create();
        return gson.toJson(object);
    }

    private ProjectModel.DakalFieldes getDakalFields(ProjectModel.ReviewModel reviewModel) {
        return new ProjectModel.DakalFieldes(reviewModel.getElectricTowerType(),
                reviewModel.getNoeDakal(),
                reviewModel.getCheckupType(),
                reviewModel.getMissionName().getName(),
                reviewModel.getTowerName(),
                reviewModel.getElectricTowerNumber(),
                reviewModel.getCircuitCount(),
                reviewModel.getBarCodeList(),
                reviewModel.getVaziateTakhir(),
                reviewModel.getElateTakhir(),
                reviewModel.getNextTowerCode());
    }


}

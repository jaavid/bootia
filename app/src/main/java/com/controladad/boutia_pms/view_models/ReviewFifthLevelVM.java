package com.controladad.boutia_pms.view_models;


import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import androidx.databinding.DataBindingUtil;
import android.os.Parcel;
import android.preference.PreferenceManager;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.controladad.boutia_pms.R;
import com.controladad.boutia_pms.adapters.ReviewFirstLevelAdapter;
import com.controladad.boutia_pms.adapters.StatefulRecyclerView;
import com.controladad.boutia_pms.databinding.FragmentReviewFifthLevelBinding;
import com.controladad.boutia_pms.location.LocationService;
import com.controladad.boutia_pms.models.DataBaseReviewModel;
import com.controladad.boutia_pms.models.data_models.ProjectModel;
import com.controladad.boutia_pms.models.shared_preferences_models.SharedPreferencesModels;
import com.controladad.boutia_pms.utility.Constants;
import com.controladad.boutia_pms.view_models.items_view_models.CustomEditTextDataModel;
import com.controladad.boutia_pms.view_models.items_view_models.CustomTextViewDataModel;
import com.controladad.boutia_pms.view_models.items_view_models.DoubleButtonDataModel;
import com.controladad.boutia_pms.view_models.items_view_models.DoubleCheckBoxDataModel;
import com.controladad.boutia_pms.view_models.items_view_models.GeneralDataModel;
import com.controladad.boutia_pms.view_models.items_view_models.GoodBadDataModel;
import com.controladad.boutia_pms.view_models.items_view_models.ImagesRecyclerViewDataModel;
import com.controladad.boutia_pms.view_models.items_view_models.NumberEditViewDataModel;
import com.controladad.boutia_pms.view_models.items_view_models.SegmentedControlDataModel;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import lombok.Getter;

import static com.controladad.boutia_pms.utility.Constants.CAMERA;
import static com.controladad.boutia_pms.utility.Constants.MANUAL;

@SuppressWarnings("ALL")
public class ReviewFifthLevelVM extends GeneralVM {

    private StatefulRecyclerView recyclerView;
    @Getter
    private ReviewFirstLevelAdapter adapter = new ReviewFirstLevelAdapter();
    private List<GeneralDataModel> goodBadDataModelList = new ArrayList<>();
    private ProjectModel.ReviewModel reviewModel;
    private ProjectModel.ReviewModel reviewModelHelper;

    private boolean isNextMissionClicked = false;
    private List<GeneralDataModel> taghatoBaJadeAsliFieldList = new ArrayList<>();
    private GoodBadDataModel taghatoBaJadeAsli;

    private List<GeneralDataModel> mavaneFieldList = new ArrayList<>();
    private GoodBadDataModel mavane;

    private List<GeneralDataModel> ensheabFieldList = new ArrayList<>();
    private GoodBadDataModel ensheab;


    private List<GeneralDataModel> taghatoBa20KFieldList = new ArrayList<>();
    private GoodBadDataModel taghatoBa20K;


    private List<GeneralDataModel> lolePresMiyaniFieldList = new ArrayList<>();
    private GoodBadDataModel lolePresMiyani;

    private List<GeneralDataModel> gooyeEkhtarFieldList = new ArrayList<>();
    private GoodBadDataModel gooyeEkhtar;


    private List<GeneralDataModel> vaziyatHarimFieldList = new ArrayList<>();
    private GoodBadDataModel vaziyatHarim;


    private List<GeneralDataModel> vaziyatMasirFieldList = new ArrayList<>();
    private GoodBadDataModel vaziyatMasir;

    private SharedPreferencesModels.SharedPreferencesUserModel preferencesUserModel;
    private CustomTextViewDataModel nextTowerScan;

    @Override
    public View binding(LayoutInflater inflater, @Nullable ViewGroup container) {
        FragmentReviewFifthLevelBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_review_fifth_level, container, false);
        binding.setReviewFifthLevelVM(this);
        recyclerView = binding.reviewFifthLevelRecyclerView;
        return binding.getRoot();
    }

    @Override
    public void onCreateFragment() {
        super.onCreateFragment();
        fieldsFilling();
        goodBadDataModelList.add(taghatoBaJadeAsli);
        goodBadDataModelList.add(mavane);
        goodBadDataModelList.add(ensheab);
        goodBadDataModelList.add(taghatoBa20K);
        goodBadDataModelList.add(lolePresMiyani);
        goodBadDataModelList.add(gooyeEkhtar);
        goodBadDataModelList.add(vaziyatHarim);
        goodBadDataModelList.add(vaziyatMasir);
        goodBadDataModelList.add(nextTowerScan);
        goodBadDataModelList.add(new ImagesRecyclerViewDataModel());
        goodBadDataModelList.add(new DoubleButtonDataModel(context.getString(R.string.next_tower_review), context.getString(R.string.mission_finish), onNextButtonClicked(), onFinishButtonClicked(), () -> "", (s) -> {
        }));
        adapter.updateData(goodBadDataModelList);
    }

    private View.OnClickListener onScanCodeClicked() {
        return v -> {
            QRScannerVM qrScannerVM = new QRScannerVM();
            qrScannerVM.setOnOkButtonClicked(v1 -> {
                String barCode = qrScannerVM.getBarCodeTrimmed().toUpperCase();
                String barCodeToSave = barCode.substring(0, 3) + " "
                        + barCode.substring(3, 6) + " " + barCode.substring(6, 8) + " " + barCode.substring(8);
                nextTowerScan.setStrDataToShow(barCodeToSave);
                reviewModel.setNextTowerCode(barCodeToSave);
                getRouter().exit();
            });
            getRouter().navigateTo(Constants.QR_SCANNER_KEY, qrScannerVM);
        };
    }

    private View.OnClickListener onFinishButtonClicked() {
        return v -> {
            for (GeneralDataModel dataModel : goodBadDataModelList) {
                if (!dataModel.isItemFilled()) {
                    showSnackBar("لطفا فیلد " + dataModel.getItemName() + " را پر کنید.");
                    if (recyclerView.getLayoutManager() != null)
                        recyclerView.getLayoutManager().scrollToPosition(adapter.getItemsModelList().indexOf(dataModel));
                    return;
                }
            }
            isNextMissionClicked = false;
            DialogVM criticalTowerDialog = new DialogVM(context.getString(R.string.dakal_bohrani), context.getString(R.string.dakal_bohrani_desc),
                    context.getString(R.string.mission_finish), context.getString(R.string.next_tower_review), context.getString(R.string.yes), context.getString(R.string.no));
            criticalTowerDialog.setOnButtonClicked(onCriticalTowerButtonClicked(criticalTowerDialog));
            getRouter().navigateToDialogFragment(Constants.CRITICAL_TOWER, criticalTowerDialog);

        };
    }

    private void fieldsFilling() {
        taghatoBaJadeAsliFieldFilling();
        mavaneFieldsFilling();
        ensheabFieldFilling();
        taghatoBa20KFieldFilling();
        lolePresMiyaniFieldFilling();
        gooyeEkhtarFieldFilling();
        vaziateHarimFieldsFilling();
        vaziyatMasirFieldFilling();

        String scanType = MANUAL;
        for (ProjectModel.BarCode type : reviewModel.getBarCodeList()) {
            if (type.getType().equals(CAMERA)) {
                scanType = CAMERA;
                break;
            }
        }
        reviewModel.setScanType(scanType);

        nextTowerScan = new CustomTextViewDataModel(context.getString(R.string.barcode_dakal_badi), reviewModel::getNextTowerCode, reviewModel::setNextTowerCode, R.drawable.ic_dispaching_code, onScanCodeClicked(), context.getString(R.string.scan));
    }

    private void vaziyatMasirFieldFilling() {
        vaziyatMasir = new GoodBadDataModel(context.getString(R.string.vaziyat_masir), vaziyatMasirFieldList, adapter,
                () -> reviewModelHelper.getVaziateMasir().getCondition(),
                (s) -> {
                    reviewModelHelper.getVaziateMasir().setCondition(s);
                    reviewModel.getVaziateMasir().setCondition(s);
                    switch (s) {
                        case Constants.IS_BAD:
                            reviewModel.getVaziateMasir().setBadCondition(reviewModelHelper.getVaziateMasir().getBadCondition());
                            break;
                        case Constants.IS_GOOD:
                            reviewModel.getVaziateMasir().setBadCondition(null);
                    }
                },
                () -> reviewModelHelper.getVaziateMasir().getBadCondition().getDescription(),
                (s) -> reviewModelHelper.getVaziateMasir().getBadCondition().setDescription(s));
        SegmentedControlDataModel entekhabVaziyatMasir = new SegmentedControlDataModel(context.getString(R.string.low), context.getString(R.string.medium), context.getString(R.string.high), context.getString(R.string.very_high), "",
                () -> reviewModelHelper.getVaziateMasir().getBadCondition().getAmount(),
                (s) -> reviewModelHelper.getVaziateMasir().getBadCondition().setAmount(s));
        vaziyatMasirFieldList.clear();
        vaziyatMasirFieldList.add(entekhabVaziyatMasir);
    }

    private void vaziateHarimFieldsFilling() {
        vaziyatHarim = new GoodBadDataModel(context.getString(R.string.vaziyat_harim), vaziyatHarimFieldList, adapter,
                () -> reviewModelHelper.getVaziateHarim().getCondition(),
                (s) -> {
                    reviewModelHelper.getVaziateHarim().setCondition(s);
                    reviewModel.getVaziateHarim().setCondition(s);
                    if (Objects.equals(s, Constants.IS_BAD))
                        reviewModel.getVaziateHarim().setBadCondition(reviewModelHelper.getVaziateHarim().getBadCondition());
                    else if (Objects.equals(s, Constants.IS_GOOD))
                        reviewModel.getVaziateHarim().setBadCondition(null);
                },
                () -> reviewModelHelper.getVaziateHarim().getBadCondition().getDescription(),
                (s) -> reviewModelHelper.getVaziateHarim().getBadCondition().setDescription(s));

        DoubleCheckBoxDataModel sakhtoSazDerakht = new DoubleCheckBoxDataModel(context.getString(R.string.sakht_o_saz), null,
                context.getString(R.string.derakht), null,
                () -> reviewModelHelper.getVaziateHarim().getBadCondition().getSakhtoSaz(),
                (s) -> {
                    reviewModelHelper.getVaziateHarim().getBadCondition().setSakhtoSaz(s);
                    reviewModel.getVaziateHarim().getBadCondition().setSakhtoSaz(s);
                }, () -> reviewModelHelper.getVaziateHarim().getBadCondition().getDarakht(),
                (s) -> {
                    reviewModelHelper.getVaziateHarim().getBadCondition().setDarakht(s);
                    reviewModel.getVaziateHarim().getBadCondition().setDarakht(s);
                });
        DoubleCheckBoxDataModel jadeKehavarzi = new DoubleCheckBoxDataModel(context.getString(R.string.keshavarzi), null,
                context.getString(R.string.jade), null,
                () -> reviewModelHelper.getVaziateHarim().getBadCondition().getKeshavarzi(),
                (s) -> {
                    reviewModelHelper.getVaziateHarim().getBadCondition().setKeshavarzi(s);
                    reviewModel.getVaziateHarim().getBadCondition().setKeshavarzi(s);
                }, () -> reviewModelHelper.getVaziateHarim().getBadCondition().getJade(),
                (s) -> {
                    reviewModelHelper.getVaziateHarim().getBadCondition().setJade(s);
                    reviewModel.getVaziateHarim().getBadCondition().setJade(s);
                });
        DoubleCheckBoxDataModel rahahanKanal = new DoubleCheckBoxDataModel(context.getString(R.string.rahahan), null,
                context.getString(R.string.kanal), null,
                () -> reviewModelHelper.getVaziateHarim().getBadCondition().getRailway(),
                (s) -> {
                    reviewModelHelper.getVaziateHarim().getBadCondition().setRailway(s);
                    reviewModel.getVaziateHarim().getBadCondition().setRailway(s);
                }, () -> reviewModelHelper.getVaziateHarim().getBadCondition().getKanal(),
                (s) -> {
                    reviewModelHelper.getVaziateHarim().getBadCondition().setKanal(s);
                    reviewModel.getVaziateHarim().getBadCondition().setKanal(s);
                });
        DoubleCheckBoxDataModel lolehaSayer = new DoubleCheckBoxDataModel(context.getString(R.string.others), null,
                context.getString(R.string.loole_haye_sookht_resani), null,
                () -> reviewModelHelper.getVaziateHarim().getBadCondition().getOthers(),
                (s) -> {
                    reviewModelHelper.getVaziateHarim().getBadCondition().setOthers(s);
                    reviewModel.getVaziateHarim().getBadCondition().setOthers(s);
                }, () -> reviewModelHelper.getVaziateHarim().getBadCondition().getLooleHayeSookhtResani(),
                (s) -> {
                    reviewModelHelper.getVaziateHarim().getBadCondition().setLooleHayeSookhtResani(s);
                    reviewModel.getVaziateHarim().getBadCondition().setLooleHayeSookhtResani(s);
                });
        vaziyatHarimFieldList.clear();
        vaziyatHarimFieldList.add(sakhtoSazDerakht);
        vaziyatHarimFieldList.add(jadeKehavarzi);
        vaziyatHarimFieldList.add(rahahanKanal);
        vaziyatHarimFieldList.add(lolehaSayer);
    }

    private void lolePresMiyaniFieldFilling() {
        lolePresMiyani = new GoodBadDataModel(context.getString(R.string.lole_pres_miyani), lolePresMiyaniFieldList, adapter,
                () -> reviewModelHelper.getLoolePressMiani().getCondition(),
                (s) -> {
                    reviewModelHelper.getLoolePressMiani().setCondition(s);
                    reviewModel.getLoolePressMiani().setCondition(s);
                    switch (s) {
                        case Constants.IS_BAD:
                            reviewModel.getLoolePressMiani().setBadCondition(reviewModelHelper.getLoolePressMiani().getBadCondition());
                            break;
                        case Constants.IS_GOOD:
                            reviewModel.getLoolePressMiani().setBadCondition(null);
                    }
                },
                () -> reviewModelHelper.getLoolePressMiani().getBadCondition().getDescription(),
                (s) -> reviewModelHelper.getLoolePressMiani().getBadCondition().setDescription(s));
        CustomEditTextDataModel shomareShild = new CustomEditTextDataModel(context.getString(R.string.shomare_shild),
                () -> reviewModelHelper.getLoolePressMiani().getBadCondition().getShomareShield(),
                (s) -> reviewModelHelper.getLoolePressMiani().getBadCondition().setShomareShield(s));
        CustomEditTextDataModel shomareFaz = new CustomEditTextDataModel(context.getString(R.string.shomare_faz),
                () -> reviewModelHelper.getLoolePressMiani().getBadCondition().getShomareFaze(),
                (s) -> reviewModelHelper.getLoolePressMiani().getBadCondition().setShomareFaze(s));
        CustomEditTextDataModel tedad = new CustomEditTextDataModel(context.getString(R.string.tedad),
                () -> reviewModelHelper.getLoolePressMiani().getBadCondition().getTedad(),
                (s) -> reviewModelHelper.getLoolePressMiani().getBadCondition().setTedad(s));
        lolePresMiyaniFieldList.clear();
        lolePresMiyaniFieldList.add(shomareShild);
        lolePresMiyaniFieldList.add(shomareFaz);
        lolePresMiyaniFieldList.add(tedad);
        lolePresMiyani.setGoodButtonText(context.getString(R.string.nadarad));
        lolePresMiyani.setBadButtonText(context.getString(R.string.darad));
    }

    private void gooyeEkhtarFieldFilling() {
        gooyeEkhtar = new GoodBadDataModel(context.getString(R.string.gooye_ekhtar), gooyeEkhtarFieldList, adapter,
                () -> reviewModelHelper.getGoy().getCondition(),
                (s) -> {
                    reviewModelHelper.getGoy().setCondition(s);
                    reviewModel.getGoy().setCondition(s);
                    switch (s) {
                        case Constants.IS_BAD:
                            reviewModel.getGoy().setBadCondition(reviewModelHelper.getGoy().getBadCondition());
                            break;
                        case Constants.IS_GOOD:
                            reviewModel.getGoy().setBadCondition(null);
                    }
                },
                () -> reviewModelHelper.getGoy().getBadCondition().getDescription(),
                (s) -> reviewModelHelper.getGoy().getBadCondition().setDescription(s));
        CustomEditTextDataModel shomareShild = new CustomEditTextDataModel(context.getString(R.string.shomare_shild),
                () -> reviewModelHelper.getGoy().getBadCondition().getShieldNumber(),
                (s) -> reviewModelHelper.getGoy().getBadCondition().setShieldNumber(s));
        CustomEditTextDataModel shomareFaz = new CustomEditTextDataModel(context.getString(R.string.shomare_faz),
                () -> reviewModelHelper.getGoy().getBadCondition().getFazNumber(),
                (s) -> reviewModelHelper.getGoy().getBadCondition().setFazNumber(s));
        gooyeEkhtarFieldList.clear();
        gooyeEkhtarFieldList.add(shomareShild);
        gooyeEkhtarFieldList.add(shomareFaz);
        gooyeEkhtar.setGoodButtonText(context.getString(R.string.nadarad));
        gooyeEkhtar.setBadButtonText(context.getString(R.string.darad));
    }

    private void taghatoBa20KFieldFilling() {
        taghatoBa20K = new GoodBadDataModel(context.getString(R.string.taghato_ba_20k), taghatoBa20KFieldList, adapter,
                () -> reviewModelHelper.getTaghatoBa20k().getCondition(),
                (s) -> {
                    reviewModelHelper.getTaghatoBa20k().setCondition(s);
                    reviewModel.getTaghatoBa20k().setCondition(s);
                    switch (s) {
                        case Constants.IS_BAD:
                            reviewModel.getTaghatoBa20k().setBadCondition(reviewModelHelper.getTaghatoBa20k().getBadCondition());
                            break;
                        case Constants.IS_GOOD:
                            reviewModel.getTaghatoBa20k().setBadCondition(null);
                    }
                },
                () -> reviewModelHelper.getTaghatoBa20k().getBadCondition().getDescription(),
                (s) -> reviewModelHelper.getTaghatoBa20k().getBadCondition().setDescription(s));
        NumberEditViewDataModel tedadTaghatoBa20K = new NumberEditViewDataModel(context.getString(R.string.tedad),
                () -> reviewModelHelper.getTaghatoBa20k().getBadCondition().getTedad(),
                (s) -> reviewModelHelper.getTaghatoBa20k().getBadCondition().setTedad(s));

        taghatoBa20K.setGoodButtonText(context.getString(R.string.nadarad));
        taghatoBa20K.setBadButtonText(context.getString(R.string.darad));
        taghatoBa20KFieldList.clear();
        taghatoBa20KFieldList.add(tedadTaghatoBa20K);
    }

    private void ensheabFieldFilling() {
        ensheab = new GoodBadDataModel(context.getString(R.string.ensheab), ensheabFieldList, adapter,
                () -> reviewModelHelper.getEnsheab().getCondition(),
                (s) -> {
                    reviewModelHelper.getEnsheab().setCondition(s);
                    reviewModel.getEnsheab().setCondition(s);
                    switch (s) {
                        case Constants.IS_BAD:
                            reviewModel.getEnsheab().setBadCondition(reviewModelHelper.getEnsheab().getBadCondition());
                            break;
                        case Constants.IS_GOOD:
                            reviewModel.getEnsheab().setBadCondition(null);
                    }
                },
                () -> reviewModelHelper.getEnsheab().getBadCondition().getDescription(),
                (s) -> reviewModelHelper.getEnsheab().getBadCondition().setDescription(s));
        ensheab.setGoodButtonText(context.getString(R.string.nadarad));
        ensheab.setBadButtonText(context.getString(R.string.darad));
    }

    private void mavaneFieldsFilling() {
        mavane = new GoodBadDataModel(context.getString(R.string.mavane), mavaneFieldList, adapter,
                () -> reviewModelHelper.getMavane().getCondition(),
                (s) -> {
                    reviewModelHelper.getMavane().setCondition(s);
                    reviewModel.getMavane().setCondition(s);
                    switch (s) {
                        case Constants.IS_BAD:
                            reviewModel.getMavane().setBadCondition(reviewModelHelper.getMavane().getBadCondition());
                            break;
                        case Constants.IS_GOOD:
                            reviewModel.getMavane().setBadCondition(null);
                    }
                },
                () -> reviewModelHelper.getMavane().getBadCondition().getDescription(),
                (s) -> reviewModelHelper.getMavane().getBadCondition().setDescription(s));
        mavane.setGoodButtonText(context.getString(R.string.nadarad));
        mavane.setBadButtonText(context.getString(R.string.darad));
    }

    private void taghatoBaJadeAsliFieldFilling() {
        taghatoBaJadeAsli = new GoodBadDataModel(context.getString(R.string.taghato_ba_jade_asli), taghatoBaJadeAsliFieldList, adapter,
                () -> reviewModelHelper.getTaghatoBaJadeAsli().getCondition(),
                (s) -> {
                    reviewModelHelper.getTaghatoBaJadeAsli().setCondition(s);
                    reviewModel.getTaghatoBaJadeAsli().setCondition(s);
                    switch (s) {
                        case Constants.IS_BAD:
                            reviewModel.getTaghatoBaJadeAsli().setBadCondition(reviewModelHelper.getTaghatoBaJadeAsli().getBadCondition());
                            break;
                        case Constants.IS_GOOD:
                            reviewModel.getTaghatoBaJadeAsli().setBadCondition(null);
                    }
                },
                () -> reviewModelHelper.getTaghatoBaJadeAsli().getBadCondition().getDescription(),
                (s) -> reviewModelHelper.getTaghatoBaJadeAsli().getBadCondition().setDescription(s));
        NumberEditViewDataModel tedadTaghatoBaJadeAsli = new NumberEditViewDataModel(context.getString(R.string.tedad),
                () -> reviewModelHelper.getTaghatoBaJadeAsli().getBadCondition().getTedad(),
                (s) -> reviewModelHelper.getTaghatoBaJadeAsli().getBadCondition().setTedad(s));
        taghatoBaJadeAsliFieldList.clear();
        taghatoBaJadeAsliFieldList.add(tedadTaghatoBaJadeAsli);
        taghatoBaJadeAsli.setBadButtonText(context.getString(R.string.darad));
        taghatoBaJadeAsli.setGoodButtonText(context.getString(R.string.nadarad));
    }

    private View.OnClickListener onCriticalTowerButtonClicked(DialogVM ciritcalTowerDialog) {
        return v -> {
            if (ciritcalTowerDialog.getCheckedItemTitle() == null) {
                showSnackBar(R.string.please_select_one_of_two);
            } else {
                if (Objects.equals(context.getString(R.string.yes), ciritcalTowerDialog.getCheckedItemTitle()))
                    reviewModel.getDakalBohrani().setVaziate("1");
                else reviewModel.getDakalBohrani().setVaziate("0");
                reviewModel.getDakalBohrani().setDescription(ciritcalTowerDialog.getDescription());
                saveDataToDatabase();
                //getRouter().navigateTo(Constants.REVIEW_FOURTH_LEVEL_SCREEN_KEY, new ReviewFourthLevelVM());
            }
        };
    }

    private View.OnClickListener onNextButtonClicked() {
        return v -> {
            for (GeneralDataModel dataModel : goodBadDataModelList) {
                if (!dataModel.isItemFilled()) {
                    showSnackBar("لطفا فیلد " + dataModel.getItemName() + " را پر کنید.");
                    if (recyclerView.getLayoutManager() != null)
                        recyclerView.getLayoutManager().scrollToPosition(adapter.getItemsModelList().indexOf(dataModel));
                    return;
                }
            }
            isNextMissionClicked = true;
            DialogVM criticalTowerDialog = new DialogVM(context.getString(R.string.dakal_bohrani), context.getString(R.string.dakal_bohrani_desc),
                    context.getString(R.string.mission_finish), context.getString(R.string.next_tower_review), context.getString(R.string.yes), context.getString(R.string.no));
            criticalTowerDialog.setOnButtonClicked(onCriticalTowerButtonClicked(criticalTowerDialog));
            getRouter().navigateToDialogFragment(Constants.CRITICAL_TOWER, criticalTowerDialog);

        };
    }

    private void saveDataToDatabase(){
        if (isNextMissionClicked)
        {
            DataBaseReviewModel dataBaseReviewModel = new DataBaseReviewModel(
                    () -> {
                        ProjectModel.ReviewModel reviewModel = mainActivityVM.getReviewModel();
                        String elateTakhir = reviewModel.getElateTakhir();
                        String vaziateTakhir = reviewModel.getVaziateTakhir();
                        int mid = reviewModel.getMid();
                        ProjectModel.MissionName missionName = new ProjectModel.MissionName(reviewModel.getMissionName().getName());
                        ProjectModel.Voltage voltage = new ProjectModel.Voltage(reviewModel.getVoltage().getVoltage());
                        mainActivityVM.setReviewModel(null);
                        mainActivityVM.setReviewModelHelper(null);
                        mainActivityVM.getReviewModel().setMid(mid);
                        mainActivityVM.getReviewModel().setElateTakhir(elateTakhir);
                        mainActivityVM.getReviewModel().setVaziateTakhir(vaziateTakhir);
                        mainActivityVM.getReviewModel().setMissionName(missionName);
                        mainActivityVM.getReviewModel().setVoltage(voltage);
                        mainActivityVM.getReviewModelHelper().setMid(mid);
                        mainActivityVM.getReviewModelHelper().setElateTakhir(elateTakhir);
                        mainActivityVM.getReviewModelHelper().setVaziateTakhir(vaziateTakhir);
                        mainActivityVM.getReviewModelHelper().setMissionName(missionName);
                        mainActivityVM.getReviewModelHelper().setVoltage(voltage);
                        getRouter().backTo(Constants.MISSION_CHOSE_SCREEN_KEY);
                        getRouter().navigateTo(Constants.REVIEW_SECOND_LEVEL, new ReviewFirstLevelVM(getRoleFromSharePreferences()));
                    }
            );
            dataBaseReviewModel.addDataToDataBase(reviewModel);
        }
        else
        {
            DataBaseReviewModel dataBaseReviewModel = new DataBaseReviewModel(
                    () -> {
                        mainActivityVM.setReviewModel(null);
                        mainActivityVM.setReviewModelHelper(null);
                        getActivity().stopService(new Intent(getActivity(), LocationService.class));
                        getRouter().backTo(Constants.HOME_SCREEN_KEY);
                    }
            );
            dataBaseReviewModel.addDataToDataBase(reviewModel);
        }


    }

    @Override
    public void onCreateView() {
        super.onCreateView();
    }

    public ReviewFifthLevelVM() {
        reviewModel = mainActivityVM.getReviewModel();
        reviewModelHelper = mainActivityVM.getReviewModelHelper();
    }

    public ReviewFifthLevelVM(Parcel source) {
        super(source);
        reviewModel = mainActivityVM.getReviewModel();
        reviewModelHelper = mainActivityVM.getReviewModelHelper();
    }

    @Override
    public int getRightIconSource() {
        return R.drawable.ic_red_mission_stop;
    }

    @Override
    public View.OnClickListener getOnRightToolBarIconClickListener() {
        return v -> {
            getRouter().navigateToDialogFragment("", new MissionStopDialogVM(() -> {
                Intent intent = new Intent(getActivity(), LocationService.class);
                getActivity().stopService(intent);

                NotificationManager notificationManager = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);

                if (notificationManager != null) {
                    notificationManager.cancelAll();
                }

                getRouter().backTo(Constants.HOME_SCREEN_KEY);
                mainActivityVM.setReviewModel(null);
                mainActivityVM.setReviewModelHelper(null);
            }));
        };
    }

    @Override
    String getPageHint() {
        return context.getString(R.string.review_fifth_level_hint);
    }

    //for parcel
    public static Creator<ReviewFifthLevelVM> CREATOR = new Creator<ReviewFifthLevelVM>() {

        @Override
        public ReviewFifthLevelVM createFromParcel(Parcel source) {
            return new ReviewFifthLevelVM(source);
        }

        @Override
        public ReviewFifthLevelVM[] newArray(int size) {
            return new ReviewFifthLevelVM[size];
        }
    };


    // reading role from shared preferences
    private String getRoleFromSharePreferences() {
        //SharedPreferences preferences = getActivity().getSharedPreferences(Constants.USER_MODEL_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        Gson gson = new GsonBuilder().create();

        if (preferencesUserModel == null) {
            String value = preferences.getString(Constants.USER_MODEL_PREFERENCES, "");

            if (value.equals("")) {
                preferencesUserModel = null;
            } else {
                preferencesUserModel = gson.fromJson(value, SharedPreferencesModels.SharedPreferencesUserModel.class);
            }
        }
        return preferencesUserModel != null ? preferencesUserModel.getRole() : null;
    }

}

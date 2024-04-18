package com.controladad.boutia_pms.view_models;

import android.Manifest;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import androidx.databinding.DataBindingUtil;
import android.os.Parcel;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RadioButton;

import com.controladad.boutia_pms.R;
import com.controladad.boutia_pms.adapters.ReviewFirstLevelAdapter;
import com.controladad.boutia_pms.adapters.StatefulRecyclerView;
import com.controladad.boutia_pms.databinding.FragmentReviewThirdLevelBinding;
import com.controladad.boutia_pms.location.LocationService;
import com.controladad.boutia_pms.models.data_models.ProjectModel;
import com.controladad.boutia_pms.utility.Constants;
import com.controladad.boutia_pms.view_models.items_view_models.BugsDataModel;
import com.controladad.boutia_pms.view_models.items_view_models.CheckBoxDataModel;
import com.controladad.boutia_pms.view_models.items_view_models.DoubleCheckBoxDataModel;
import com.controladad.boutia_pms.view_models.items_view_models.GeneralDataModel;
import com.controladad.boutia_pms.view_models.items_view_models.GenericFunction;
import com.controladad.boutia_pms.view_models.items_view_models.GoodBadDataModel;
import com.controladad.boutia_pms.view_models.items_view_models.ImagesRecyclerViewDataModel;
import com.controladad.boutia_pms.view_models.items_view_models.NumberEditViewDataModel;
import com.controladad.boutia_pms.view_models.items_view_models.SegmentedControlDataModel;
import com.controladad.boutia_pms.view_models.items_view_models.SimpleTextViewDataModel;
import com.controladad.boutia_pms.view_models.items_view_models.SingleButtonDataModel;
import com.controladad.boutia_pms.view_models.items_view_models.TextGetter;
import com.controladad.boutia_pms.view_models.items_view_models.TextSetter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import cn.refactor.library.SmoothCheckBox;
import lombok.Getter;


@SuppressWarnings("ConstantConditions")
public class ReviewThirdLevelVM extends GeneralVM {

    @Getter
    private ReviewFirstLevelAdapter adapter = new ReviewFirstLevelAdapter();
    private List<GeneralDataModel> goodBadDataModelList = new ArrayList<>();
    private StatefulRecyclerView recyclerView;
    private ProjectModel.ReviewModel reviewModel;
    private ProjectModel.ReviewModel reviewModelHelper;

    private String barcode;


    @Override
    public View binding(LayoutInflater inflater, @Nullable ViewGroup container) {
        FragmentReviewThirdLevelBinding binding = DataBindingUtil.inflate(inflater,
                R.layout.fragment_review_third_level, container, false);
        binding.setReviewThirdLevelVM(this);
        recyclerView = binding.reviewThirdLevelRecyclerView;
        return binding.getRoot();
    }


    @Override
    public void onCreateFragment() {
        super.onCreateFragment();
        goodBadDataModelList.clear();
        //هادی های فاز و ملحقات
        goodBadDataModelList.add(new SimpleTextViewDataModel(context.getString(R.string.hadihaye_felez_va_molhaghat), null, null));
        addHadihayeFelesFields();
        //یراق
        goodBadDataModelList.add(new SimpleTextViewDataModel(context.getString(R.string.yaragh), null, null));
        addYaraghFields();
        //زنجیره مقره و ملحقات
        SimpleTextViewDataModel zanjireMaghareVaMolhaghatText = new SimpleTextViewDataModel(context.getString(R.string.zanjire_maghare_va_molhaghat), () -> "", (s) -> {
        });
        goodBadDataModelList.add(zanjireMaghareVaMolhaghatText);
        addZanjireMaghareVaMolhaghat();


        goodBadDataModelList.add(new ImagesRecyclerViewDataModel());
        goodBadDataModelList.add(new SingleButtonDataModel(context.getString(R.string.ok_and_continue), onOkButtonClicked(), () -> "", (s) -> {
        }));

        adapter.updateData(goodBadDataModelList);
    }

    private View.OnClickListener onOkButtonClicked() {
        return v -> {
            for (GeneralDataModel dataModel : goodBadDataModelList) {
                if (!dataModel.isItemFilled()) {
                    showSnackBar("لطفا فیلد " + dataModel.getItemName() + " را پر کنید.");
                    if (recyclerView.getLayoutManager() != null)
                        recyclerView.getLayoutManager().scrollToPosition(adapter.getItemsModelList().indexOf(dataModel));
                    return;
                }
            }

            int index = 1;
            for (ProjectModel.IsolationChains isolationChains : reviewModel.getIsolationChainsList()) {
                if (Objects.equals(isolationChains.getACondition(), Constants.IS_BAD)) {
                    if (Objects.equals(isolationChains.getIfAConditionBad().getPolution(), Constants.TRUE)) {
                        if (isolationChains.getIfAConditionBad().getPolutionPercent() == null ||
                                Objects.equals(isolationChains.getIfAConditionBad().getPolutionPercent().trim(), "")) {
                            showSnackBar("لطفا فیلد درصد آلودگی A" + index + " را پر کنید.");
                            return;
                        }
                    }
                }

                if (Objects.equals(isolationChains.getBCondition(), Constants.IS_BAD)) {
                    if (Objects.equals(isolationChains.getIfBConditionBad().getPolution(), Constants.TRUE)) {
                        if (isolationChains.getIfBConditionBad().getPolutionPercent() == null ||
                                Objects.equals(isolationChains.getIfBConditionBad().getPolutionPercent().trim(), "")) {
                            showSnackBar("لطفا فیلد درصد آلودگی B" + index + " را پر کنید.");
                            return;
                        }
                    }
                }

                if (Objects.equals(isolationChains.getCCondition(), Constants.IS_BAD)) {
                    if (Objects.equals(isolationChains.getIfCConditionBad().getPolution(), Constants.TRUE)) {
                        if (isolationChains.getIfCConditionBad().getPolutionPercent() == null ||
                                Objects.equals(isolationChains.getIfCConditionBad().getPolutionPercent().trim(), "")) {
                            showSnackBar("لطفا فیلد درصد آلودگی C" + index + " را پر کنید.");
                            return;
                        }
                    }
                }
                index++;
            }


            ///khmoshiye khat va scan barcode

            if (barcode == null && (mainActivityVM.getBazdidMission() == null ||
                    !Objects.equals(mainActivityVM.getBazdidMission().getMissionType(), "تحویل و تحول"))) {
                QRScannerVM qrScannerVM = new QRScannerVM();
                qrScannerVM.setOnOkButtonClicked(v1 -> {
                    barcode = qrScannerVM.getBarCodeTrimmed().toUpperCase();
                    boolean isCodeTrue = false;
                    for (ProjectModel.BarCode barCode : reviewModel.getBarCodeList()) {
                        if (Objects.equals(barCode.getNumber().substring(1).replaceAll(" ", ""), barcode.substring(1)))
                            isCodeTrue = true;
                    }
                    if (isCodeTrue) {
                        getRouter().exit();
                        DialogVM offLineDialog = new DialogVM(context.getString(R.string.off_line), context.getString(R.string.khat_niaz_be_khamooshi_darad),
                                context.getString(R.string.ok), context.getString(R.string.next_tower_review), context.getString(R.string.yes), context.getString(R.string.no));
                        offLineDialog.setOnButtonClicked(onOffLineDialogNextButtonClicked(offLineDialog));
                        //  offLineDialog.setOnButtonClicked(onOffLineDialogFinishButtonClicked(offLineDialog));
                        getRouter().navigateToDialogFragment(Constants.OFF_LINE, offLineDialog);
                    } else qrScannerVM.showSnackBar(context.getString(R.string.is_code_invalid));
                });
                getRouter().navigateToDialogFragment(Constants.CODE_SCAN_POP_UP, new DialogVM(context.getString(R.string.code_scanning), context.getString(R.string.please_scan_bar_code), context.getString(R.string.scan), v1 -> {
                    if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

                        ActivityCompat.requestPermissions(getActivity(),
                                new String[]{Manifest.permission.CAMERA},
                                Constants.MY_PERMISSIONS_REQUEST_CAMERA);

                    } else {
                        getRouter().navigateTo(Constants.QR_SCANNER_KEY, qrScannerVM);
                    }
                }));
            } else {
                DialogVM offLineDialog = new DialogVM(context.getString(R.string.off_line), context.getString(R.string.khat_niaz_be_khamooshi_darad),
                        context.getString(R.string.mission_finish), context.getString(R.string.next_tower_review), context.getString(R.string.yes), context.getString(R.string.no));
                offLineDialog.setOnButtonClicked(onOffLineDialogNextButtonClicked(offLineDialog));
                getRouter().navigateToDialogFragment(Constants.OFF_LINE, offLineDialog);
            }

            //payan khamoshiye khat va scan barcode

        };
    }


    private View.OnClickListener onOffLineDialogNextButtonClicked(DialogVM offLineDialog) {
        return v -> {
            if (offLineDialog.getCheckedItemTitle() == null) {
                showSnackBar(R.string.please_select_one_of_two);
            } else {
                if (Objects.equals(context.getString(R.string.yes), offLineDialog.getCheckedItemTitle()))
                    reviewModel.getKhamooshKardaneKhat().setVaziate("0");
                else reviewModel.getKhamooshKardaneKhat().setVaziate("1");
                reviewModel.getKhamooshKardaneKhat().setDescription(offLineDialog.getDescription());
                getRouter().navigateTo(Constants.REVIEW_FOURTH_LEVEL_SCREEN_KEY, new ReviewFourthLevelVM());
                /*DataBaseReviewModel dataBaseReviewModel = new DataBaseReviewModel(
                        () -> {
                            *//*mainActivityVM.setReviewModel(null);
                            mainActivityVM.setReviewModelHelper(null);
                            getRouter().backTo(Constants.MISSION_CHOSE_SCREEN_KEY);*//*
                            getRouter().navigateTo(Constants.REVIEW_FIRST_LEVEL, new ReviewFifthLevelVM());
                        }
                );
                dataBaseReviewModel.addDataToDataBase(reviewModel);*/
            }
        };
    }

    private void addHadihayeFelesFields() {
        int tedadeMadar = Integer.valueOf(reviewModelHelper.getCircuitCount().getCount());
        while (reviewModelHelper.getHadiHayePhaseVaMolhaghatAList().size() < tedadeMadar) {
            reviewModelHelper.getHadiHayePhaseVaMolhaghatAList().add(new ProjectModel.HadiHayePhaseVaMolhaghat(null, null,
                    new ProjectModel.HadiHayePhaseVaMolhaghatBadCondition(new ProjectModel.HadiBadCondition(null, null, null,
                            null, null, null, null), new ProjectModel.PhaseBadCondition(null, null,
                            null, null, null, null, null), null)));
            reviewModelHelper.getHadiHayePhaseVaMolhaghatBList().add(new ProjectModel.HadiHayePhaseVaMolhaghat(null, null,
                    new ProjectModel.HadiHayePhaseVaMolhaghatBadCondition(new ProjectModel.HadiBadCondition(null, null, null,
                            null, null, null, null), new ProjectModel.PhaseBadCondition(null, null,
                            null, null, null, null, null), null)));
            reviewModelHelper.getHadiHayePhaseVaMolhaghatCList().add(new ProjectModel.HadiHayePhaseVaMolhaghat(null, null,
                    new ProjectModel.HadiHayePhaseVaMolhaghatBadCondition(new ProjectModel.HadiBadCondition(null, null, null,
                            null, null, null, null), new ProjectModel.PhaseBadCondition(null, null,
                            null, null, null, null, null), null)));
            reviewModel.getHadiHayePhaseVaMolhaghatAList().add(new ProjectModel.HadiHayePhaseVaMolhaghat(null, null, null));
            reviewModel.getHadiHayePhaseVaMolhaghatBList().add(new ProjectModel.HadiHayePhaseVaMolhaghat(null, null, null));
            reviewModel.getHadiHayePhaseVaMolhaghatCList().add(new ProjectModel.HadiHayePhaseVaMolhaghat(null, null, null));
        }
        while (reviewModelHelper.getHadiHayePhaseVaMolhaghatAList().size() > tedadeMadar) {
            int i = reviewModelHelper.getHadiHayePhaseVaMolhaghatAList().size();
            reviewModelHelper.getHadiHayePhaseVaMolhaghatAList().remove(i - 1);
            reviewModelHelper.getHadiHayePhaseVaMolhaghatBList().remove(i - 1);
            reviewModelHelper.getHadiHayePhaseVaMolhaghatCList().remove(i - 1);
            reviewModel.getHadiHayePhaseVaMolhaghatAList().remove(i - 1);
            reviewModel.getHadiHayePhaseVaMolhaghatBList().remove(i - 1);
            reviewModel.getHadiHayePhaseVaMolhaghatCList().remove(i - 1);
        }
        String title;
        for (int i = 0; i < tedadeMadar; i++) {
            reviewModel.getHadiHayePhaseVaMolhaghatAList().get(i).setDispatching(qrCodeToDespatchingConverter(reviewModel.getBarCodeList().get(i).getNumber()));
            reviewModel.getHadiHayePhaseVaMolhaghatBList().get(i).setDispatching(qrCodeToDespatchingConverter(reviewModel.getBarCodeList().get(i).getNumber()));
            reviewModel.getHadiHayePhaseVaMolhaghatCList().get(i).setDispatching(qrCodeToDespatchingConverter(reviewModel.getBarCodeList().get(i).getNumber()));
            reviewModelHelper.getHadiHayePhaseVaMolhaghatAList().get(i).setDispatching(qrCodeToDespatchingConverter(reviewModel.getBarCodeList().get(i).getNumber()));
            reviewModelHelper.getHadiHayePhaseVaMolhaghatBList().get(i).setDispatching(qrCodeToDespatchingConverter(reviewModel.getBarCodeList().get(i).getNumber()));
            reviewModelHelper.getHadiHayePhaseVaMolhaghatCList().get(i).setDispatching(qrCodeToDespatchingConverter(reviewModel.getBarCodeList().get(i).getNumber()));
            ProjectModel.HadiHayePhaseVaMolhaghat hadiHayePhaseVaMolhaghatx;
            ProjectModel.HadiHayePhaseVaMolhaghat hadiHayePhaseVaMolhaghatx1;
            for (int j = 0; j < 3; j++) {
                if (j == 0) {
                    title = "A" + String.valueOf(i + 1) + ": " + qrCodeToDespatchingConverter(reviewModel.getBarCodeList().get(i).getNumber());
                    hadiHayePhaseVaMolhaghatx = reviewModelHelper.getHadiHayePhaseVaMolhaghatAList().get(i);
                    hadiHayePhaseVaMolhaghatx1 = reviewModel.getHadiHayePhaseVaMolhaghatAList().get(i);
                } else if (j == 1) {
                    title = "B" + String.valueOf(i + 1) + ": " + qrCodeToDespatchingConverter(reviewModel.getBarCodeList().get(i).getNumber());
                    hadiHayePhaseVaMolhaghatx = reviewModelHelper.getHadiHayePhaseVaMolhaghatBList().get(i);
                    hadiHayePhaseVaMolhaghatx1 = reviewModel.getHadiHayePhaseVaMolhaghatBList().get(i);
                } else {
                    title = "C" + String.valueOf(i + 1) + ": " + qrCodeToDespatchingConverter(reviewModel.getBarCodeList().get(i).getNumber());
                    hadiHayePhaseVaMolhaghatx = reviewModelHelper.getHadiHayePhaseVaMolhaghatCList().get(i);
                    hadiHayePhaseVaMolhaghatx1 = reviewModel.getHadiHayePhaseVaMolhaghatCList().get(i);
                }

                final ProjectModel.HadiHayePhaseVaMolhaghat hadiHayePhaseVaMolhaghat = hadiHayePhaseVaMolhaghatx;
                final ProjectModel.HadiHayePhaseVaMolhaghat hadiHayePhaseVaMolhaghat1 = hadiHayePhaseVaMolhaghatx1;
                List<GeneralDataModel> hadihayeFelezVaMolhaghatFieldsList = new ArrayList<>();
                ProjectModel.HadiHayePhaseVaMolhaghatBadCondition badCondition = hadiHayePhaseVaMolhaghat.getBadCondition();
                ProjectModel.HadiBadCondition hadiBadCondition = badCondition.getHadiBadCondition();
                ProjectModel.PhaseBadCondition phaseBadCondition = badCondition.getPhaseBadCondition();

                GoodBadDataModel hadihayeFelezVaMolhaghat = new GoodBadDataModel(title, hadihayeFelezVaMolhaghatFieldsList, adapter,
                        hadiHayePhaseVaMolhaghat::getCondition,
                        (s) -> {
                            hadiHayePhaseVaMolhaghat.setCondition(s);
                            hadiHayePhaseVaMolhaghat1.setCondition(s);
                            switch (s) {
                                case Constants.IS_GOOD:
                                    hadiHayePhaseVaMolhaghat1.setBadCondition(null);
                                    break;
                                case Constants.IS_BAD:
                                    hadiHayePhaseVaMolhaghat1.setBadCondition(badCondition);
                                    break;
                            }
                        },
                        context.getString(R.string.iradate_hadi), badCondition::getDescription,
                        badCondition::setDescription);
                goodBadDataModelList.add(hadihayeFelezVaMolhaghat);
                DoubleCheckBoxDataModel jumperLolePresMiyani = new DoubleCheckBoxDataModel(context.getString(R.string.jumper), null, context.getString(R.string.lole_pres_miyani), null,
                        hadiBadCondition::getJamper, hadiBadCondition::setJamper, hadiBadCondition::getLoolePressMiani, hadiBadCondition::setLoolePressMiani);
                DoubleCheckBoxDataModel spacerLolePresEntehayi = new DoubleCheckBoxDataModel(context.getString(R.string.spacer), null, context.getString(R.string.lole_pres_entehayi), null,
                        hadiBadCondition::getSpiser, hadiBadCondition::setSpiser, hadiBadCondition::getLoolePressEntehaee, hadiBadCondition::setLoolePressEntehaee);
                DoubleCheckBoxDataModel repierBandaj = new DoubleCheckBoxDataModel(context.getString(R.string.repier), null, context.getString(R.string.bandaj), null,
                        hadiBadCondition::getReeper, hadiBadCondition::setReeper, hadiBadCondition::getBandaj, hadiBadCondition::setBandaj);
                CheckBoxDataModel gooyeEkhtar = new CheckBoxDataModel(context.getString(R.string.gooye_ekhtar), null, hadiBadCondition::getGoyeEkhtar, hadiBadCondition::setGoyeEkhtar);
                BugsDataModel bugsDataModel = new BugsDataModel(context.getString(R.string.iradate_etesalate_faz));
                DoubleCheckBoxDataModel zadegiBaadKardegi = new DoubleCheckBoxDataModel(context.getString(R.string.zadegi), null, context.getString(R.string.baad_kardegi), null,
                        phaseBadCondition::getZangZadegi, phaseBadCondition::setZangZadegi, phaseBadCondition::getBadKardegiSim, phaseBadCondition::setBadKardegiSim);
                DoubleCheckBoxDataModel damperArmorad = new DoubleCheckBoxDataModel(context.getString(R.string.damper), null, context.getString(R.string.armorad), null,
                        phaseBadCondition::getDamper, phaseBadCondition::setDamper, phaseBadCondition::getArmorad, phaseBadCondition::setArmorad);
                DoubleCheckBoxDataModel colompShotorGloi = new DoubleCheckBoxDataModel(context.getString(R.string.colomp), null, context.getString(R.string.kolomp_entehaye_pich), null,
                        phaseBadCondition::getColomp, phaseBadCondition::setColomp, phaseBadCondition::getShotorGalooyee, phaseBadCondition::setShotorGalooyee);
                CheckBoxDataModel vazne = new CheckBoxDataModel(context.getString(R.string.vazne), null, phaseBadCondition::getVazneh, phaseBadCondition::setVazneh);
                GeneralDataModel[] hadihayeFelezVaMolhaghatFields = {jumperLolePresMiyani, spacerLolePresEntehayi, repierBandaj, gooyeEkhtar, bugsDataModel, zadegiBaadKardegi, damperArmorad, colompShotorGloi, vazne};
                hadihayeFelezVaMolhaghatFieldsList.addAll(Arrays.asList(hadihayeFelezVaMolhaghatFields));
            }
        }
    }


    private void addYaraghFields() {
        int tedadeMadar = Integer.valueOf(reviewModelHelper.getCircuitCount().getCount());
        while (reviewModelHelper.getFittingsList().size() < tedadeMadar) {
            reviewModelHelper.getFittingsList().add(new ProjectModel.Fittings(null, null, null, null,
                    new ProjectModel.FittingsBadCondition(null, null, null, null, null),
                    new ProjectModel.FittingsBadCondition(null, null, null, null, null),
                    new ProjectModel.FittingsBadCondition(null, null, null, null, null)));
            reviewModel.getFittingsList().add(new ProjectModel.Fittings(null, null, null, null, null, null, null));
        }
        while (reviewModelHelper.getFittingsList().size() > tedadeMadar) {
            reviewModelHelper.getFittingsList().remove(tedadeMadar);
            reviewModel.getFittingsList().remove(tedadeMadar);
        }

        String title = "A1";
        for (int i = 0; i < tedadeMadar; i++) {
            reviewModel.getFittingsList().get(i).setDispatching(qrCodeToDespatchingConverter(reviewModel.getBarCodeList().get(i).getNumber()));
            reviewModelHelper.getFittingsList().get(i).setDispatching(qrCodeToDespatchingConverter(reviewModel.getBarCodeList().get(i).getNumber()));
            ProjectModel.Fittings fittingsHelper = reviewModelHelper.getFittingsList().get(i);
            ProjectModel.Fittings fittings = reviewModel.getFittingsList().get(i);
            ProjectModel.FittingsBadCondition badConditionx = fittingsHelper.getIfAConditionBad();
            TextSetter setterHelperx = (s) -> {
            };
            TextSetter setterx = (s) -> {
            };
            TextGetter getterHelperx = () -> "";
            Function badConditionSetterx = () -> {
            };
            Function badConditionNullSetterx = () -> {
            };
            for (int j = 0; j < 3; j++) {
                switch (j) {
                    case 0: {
                        title = "A" + String.valueOf(i + 1) + ": " + qrCodeToDespatchingConverter(reviewModel.getBarCodeList().get(i).getNumber());
                        badConditionx = fittingsHelper.getIfAConditionBad();
                        getterHelperx = fittingsHelper::getACondition;
                        setterx = fittings::setACondition;
                        setterHelperx = fittingsHelper::setACondition;
                        badConditionNullSetterx = () -> fittings.setIfAConditionBad(null);
                        badConditionSetterx = () -> fittings.setIfAConditionBad(fittingsHelper.getIfAConditionBad());
                        break;
                    }
                    case 1: {
                        title = "B" + String.valueOf(i + 1) + ": " + qrCodeToDespatchingConverter(reviewModel.getBarCodeList().get(i).getNumber());
                        badConditionx = fittingsHelper.getIfBConditionBad();
                        getterHelperx = fittingsHelper::getBCondition;
                        setterx = fittings::setBCondition;
                        setterHelperx = fittingsHelper::setBCondition;
                        badConditionNullSetterx = () -> fittings.setIfBConditionBad(null);
                        badConditionSetterx = () -> fittings.setIfBConditionBad(fittingsHelper.getIfBConditionBad());
                        break;
                    }
                    case 2: {
                        title = "C" + String.valueOf(i + 1) + ": " + qrCodeToDespatchingConverter(reviewModel.getBarCodeList().get(i).getNumber());
                        badConditionx = reviewModelHelper.getFittingsList().get(i).getIfCConditionBad();
                        getterHelperx = fittingsHelper::getCCondition;
                        setterx = fittings::setCCondition;
                        setterHelperx = fittingsHelper::setCCondition;
                        badConditionNullSetterx = () -> fittings.setIfCConditionBad(null);
                        badConditionSetterx = () -> fittings.setIfCConditionBad(fittingsHelper.getIfCConditionBad());
                        break;
                    }
                }
                List<GeneralDataModel> yaraghFieldsList = new ArrayList<>();
                final ProjectModel.FittingsBadCondition badCondition = badConditionx;
                final TextGetter getterHelper = getterHelperx;
                final TextSetter setterHelper = setterHelperx;
                final TextSetter setter = setterx;
                final Function badConditionSetter = badConditionSetterx;
                final Function badConditionNullSetter = badConditionNullSetterx;

                GoodBadDataModel yaragh = new GoodBadDataModel(title, yaraghFieldsList, adapter,
                        getterHelper, (s) -> {
                    setter.setText(s);
                    setterHelper.setText(s);
                    switch (s) {
                        case Constants.IS_BAD:
                            badConditionSetter.fun();
                            break;
                        case Constants.IS_GOOD:
                            badConditionNullSetter.fun();
                            break;
                    }
                }, context.getString(R.string.iradate_yaragh), badCondition::getDescription, badCondition::setDescription);
                goodBadDataModelList.add(yaragh);
                DoubleCheckBoxDataModel barghGirKafi = new DoubleCheckBoxDataModel(context.getString(R.string.bagh_gir), null, context.getString(R.string.kafi), null,
                        badCondition::getLightningArresterDescription, badCondition::setLightningArresterDescription, badCondition::getInsolesDescription, badCondition::setInsolesDescription);
                DoubleCheckBoxDataModel koronaringEtesalat = new DoubleCheckBoxDataModel(context.getString(R.string.koronaring), null, context.getString(R.string.etesalat), null,
                        badCondition::getCronarigDescription, badCondition::setCronarigDescription, badCondition::getConnectionDescription, badCondition::setConnectionDescription);
                GeneralDataModel[] yaraghFields = {barghGirKafi, koronaringEtesalat};
                yaraghFieldsList.addAll(Arrays.asList(yaraghFields));
            }
        }
    }

    private RadioButton.OnCheckedChangeListener onNoeAsibCheckChanged(NumberEditViewDataModel tedadShekastegiEditText, NumberEditViewDataModel tedadSokhtegiEditText, SegmentedControlDataModel noeAsibMaghare, GoodBadDataModel zanjireMaghareVaMolhaghat) {
        return (buttonView, isChecked) -> {
            String buttonText = (String) buttonView.getText();
            if (isChecked) {
                if (Objects.equals(buttonText, context.getString(R.string.shekastegi))) {
                    removeTedadeSookhtegiItem(tedadSokhtegiEditText, zanjireMaghareVaMolhaghat);
                    addTedadeShekastegiItem(tedadShekastegiEditText, noeAsibMaghare, zanjireMaghareVaMolhaghat);
                } else if (Objects.equals(buttonText, context.getString(R.string.sokhtegi)) && !adapter.getItemsModelList().contains(tedadSokhtegiEditText)) {
                    removeTedadeShekastegiItem(tedadShekastegiEditText, zanjireMaghareVaMolhaghat);
                    addTedadeSookhtegiItem(tedadSokhtegiEditText, noeAsibMaghare, zanjireMaghareVaMolhaghat);
                } else {
                    removeTedadeSookhtegiItem(tedadSokhtegiEditText, zanjireMaghareVaMolhaghat);
                    removeTedadeShekastegiItem(tedadShekastegiEditText, zanjireMaghareVaMolhaghat);
                }
            }
        };
    }

    private void addTedadeShekastegiItem(NumberEditViewDataModel tedadShekastegiEditText, SegmentedControlDataModel noeAsibMaghare, GoodBadDataModel zanjireMaghareVaMolhaghat) {
        int position = adapter.getItemsModelList().indexOf(noeAsibMaghare) + 1;
        int positionInChildList = zanjireMaghareVaMolhaghat.getChildDataModel().indexOf(noeAsibMaghare) + 1;
        if (!adapter.getItemsModelList().contains(tedadShekastegiEditText)) {
            adapter.getItemsModelList().add(position, tedadShekastegiEditText);
            zanjireMaghareVaMolhaghat.getChildDataModel().add(positionInChildList, tedadShekastegiEditText);
            try {
                adapter.notifyItemInserted(position);
            } catch (IllegalStateException ex) {
                adapter.setHasItemsChanged(true);
            }
        }
    }

    private void removeTedadeShekastegiItem(NumberEditViewDataModel tedadShekastegiEditText, GoodBadDataModel zanjireMaghareVaMolhaghat) {
        if (adapter.getItemsModelList().contains(tedadShekastegiEditText)) {
            int position = adapter.getItemsModelList().indexOf(tedadShekastegiEditText);
            adapter.getItemsModelList().remove(tedadShekastegiEditText);
            zanjireMaghareVaMolhaghat.getChildDataModel().remove(tedadShekastegiEditText);
            try {
                adapter.notifyItemRemoved(position);
            } catch (IllegalStateException ex) {
                adapter.setHasItemsChanged(true);
            }
        }
    }

    private void addTedadeSookhtegiItem(NumberEditViewDataModel tedadSokhtegiEditText, SegmentedControlDataModel noeAsibMaghare, GoodBadDataModel zanjireMaghareVaMolhaghat) {
        if (!adapter.getItemsModelList().contains(tedadSokhtegiEditText)) {
            int position = adapter.getItemsModelList().indexOf(noeAsibMaghare) + 1;
            int positionInChildList = zanjireMaghareVaMolhaghat.getChildDataModel().indexOf(noeAsibMaghare) + 1;
            adapter.getItemsModelList().add(position, tedadSokhtegiEditText);
            zanjireMaghareVaMolhaghat.getChildDataModel().add(positionInChildList, tedadSokhtegiEditText);
            try {
                adapter.notifyItemInserted(position);
            } catch (IllegalStateException ex) {
                adapter.setHasItemsChanged(true);
            }
        }
    }

    private void removeTedadeSookhtegiItem(NumberEditViewDataModel tedadSokhtegiEditText, GoodBadDataModel zanjireMaghareVaMolhaghat) {
        if (adapter.getItemsModelList().contains(tedadSokhtegiEditText)) {
            int position = adapter.getItemsModelList().indexOf(tedadSokhtegiEditText);
            adapter.getItemsModelList().remove(tedadSokhtegiEditText);
            zanjireMaghareVaMolhaghat.getChildDataModel().remove(tedadSokhtegiEditText);
            try {
                adapter.notifyItemRemoved(position);
            } catch (IllegalStateException ex) {
                adapter.setHasItemsChanged(true);
            }
        }
    }

    private void addZanjireMaghareVaMolhaghat() {
        int tedadeMadar = Integer.valueOf(reviewModelHelper.getCircuitCount().getCount());
        while (reviewModelHelper.getIsolationChainsList().size() < tedadeMadar) {
            reviewModelHelper.getIsolationChainsList().add(new ProjectModel.IsolationChains(null, null, null, null, null, null, null,
                    new ProjectModel.IsolationChainsBadCondition(null, null, null, null, null, null, null, null, null, null, null, null, null, null, null),
                    new ProjectModel.IsolationChainsBadCondition(null, null, null, null, null, null, null, null, null, null, null, null, null, null, null),
                    new ProjectModel.IsolationChainsBadCondition(null, null, null, null, null, null, null, null, null, null, null, null, null, null, null)
            ));
            reviewModel.getIsolationChainsList().add(new ProjectModel.IsolationChains(null, null, null, null, null, null, null, null, null, null));
        }

        while ((reviewModelHelper.getIsolationChainsList().size() > tedadeMadar)) {
            reviewModelHelper.getIsolationChainsList().remove(tedadeMadar);
            reviewModel.getIsolationChainsList().remove(tedadeMadar);
        }
        String title = "A1";
        for (int i = 0; i < tedadeMadar; i++) {
            reviewModel.getIsolationChainsList().get(i).setDispatching(qrCodeToDespatchingConverter(reviewModel.getBarCodeList().get(i).getNumber()));
            reviewModelHelper.getIsolationChainsList().get(i).setDispatching(qrCodeToDespatchingConverter(reviewModel.getBarCodeList().get(i).getNumber()));
            ProjectModel.IsolationChains model = reviewModel.getIsolationChainsList().get(i);
            ProjectModel.IsolationChains modelHelper = reviewModelHelper.getIsolationChainsList().get(i);
            ProjectModel.IsolationChainsBadCondition badConditionx = modelHelper.getIfAConditionBad();
            TextGetter typeGetterHelper = () -> "";
            TextSetter typeSetterHelperx = (s) -> {
            };
            TextSetter typeSetterx = (s) -> {
            };
            TextGetter conditionGetterHelper = () -> "";
            TextSetter conditionSetterHelperx = (s) -> {
            };
            TextSetter conditionSetterx = (s) -> {
            };
            GenericMethod<ProjectModel.IsolationChainsBadCondition> badConditionSetterx = (b) -> {
            };
            GenericFunction<ProjectModel.IsolationChainsBadCondition> badConditionGetterx = () -> null;
            Function badConditionNullSetterx = () -> {
            };
            for (int j = 0; j < 3; j++) {
                switch (j) {
                    case 0: {
                        title = "A" + String.valueOf(i + 1) + ": " + qrCodeToDespatchingConverter(reviewModel.getBarCodeList().get(i).getNumber());
                        badConditionx = modelHelper.getIfAConditionBad();
                        typeGetterHelper = modelHelper::getAType;
                        typeSetterHelperx = modelHelper::setAType;
                        typeSetterx = model::setAType;
                        conditionGetterHelper = modelHelper::getACondition;
                        conditionSetterHelperx = modelHelper::setACondition;
                        conditionSetterx = model::setACondition;
                        badConditionSetterx = model::setIfAConditionBad;
                        badConditionNullSetterx = () -> model.setIfAConditionBad(null);
                        badConditionGetterx = model::getIfAConditionBad;
                        break;
                    }
                    case 1: {
                        title = "B" + String.valueOf(i + 1) + ": " + qrCodeToDespatchingConverter(reviewModel.getBarCodeList().get(i).getNumber());
                        badConditionx = modelHelper.getIfBConditionBad();
                        typeGetterHelper = modelHelper::getBType;
                        typeSetterHelperx = modelHelper::setBType;
                        typeSetterx = model::setBType;
                        conditionGetterHelper = modelHelper::getBCondition;
                        conditionSetterHelperx = modelHelper::setBCondition;
                        conditionSetterx = model::setBCondition;
                        badConditionSetterx = model::setIfBConditionBad;
                        badConditionNullSetterx = () -> model.setIfBConditionBad(null);
                        badConditionGetterx = model::getIfBConditionBad;
                        break;
                    }
                    case 2: {
                        title = "C" + String.valueOf(i + 1) + ": " + qrCodeToDespatchingConverter(reviewModel.getBarCodeList().get(i).getNumber());
                        badConditionx = modelHelper.getIfCConditionBad();
                        typeGetterHelper = modelHelper::getCType;
                        typeSetterHelperx = modelHelper::setCType;
                        typeSetterx = model::setCType;
                        conditionGetterHelper = modelHelper::getCCondition;
                        conditionSetterHelperx = modelHelper::setCCondition;
                        conditionSetterx = model::setCCondition;
                        badConditionSetterx = model::setIfCConditionBad;
                        badConditionNullSetterx = () -> model.setIfCConditionBad(null);
                        badConditionGetterx = model::getIfCConditionBad;
                        break;
                    }
                }

                final ProjectModel.IsolationChainsBadCondition badCondition = badConditionx;
                List<GeneralDataModel> zanjireMaghareVaMolhaghatFieldsList = new ArrayList<>();
                final TextSetter typeSetterHelper = typeSetterHelperx;
                final TextSetter typeSetter = typeSetterx;
                final TextSetter conditionSetterHelper = conditionSetterHelperx;
                final TextSetter conditionSetter = conditionSetterx;
                final GenericMethod<ProjectModel.IsolationChainsBadCondition> badConditionSetter = badConditionSetterx;
                final Function badConditionNullSetter = badConditionNullSetterx;
                final GenericFunction<ProjectModel.IsolationChainsBadCondition> badConditionGetter = badConditionGetterx;

                SegmentedControlDataModel zanjireMaghareVaMolhaghatChose = new SegmentedControlDataModel(context.getString(R.string.seramiki), context.getString(R.string.shisheyi), context.getString(R.string.silicon), context.getString(R.string.tarkibi), title,
                        typeGetterHelper, (s) -> {
                    typeSetterHelper.setText(s);
                    typeSetter.setText(s);
                });
                GoodBadDataModel zanjireMaghareVaMolhaghat = new GoodBadDataModel(null, zanjireMaghareVaMolhaghatFieldsList, adapter,
                        conditionGetterHelper, (s) -> {
                    conditionSetter.setText(s);
                    conditionSetterHelper.setText(s);
                    switch (s) {
                        case Constants.IS_BAD:
                            String populationPercent = Objects.equals(badCondition.getPolution(), Constants.IS_CHECKED) ? badCondition.getPolutionPercent() : null;
                            String tedadeSookhtegiYaShekastegi = Objects.equals(badCondition.getNoeAsibeMaghare(), context.getString(R.string.dose_not_have)) ? null : badCondition.getTedadeShekastegiYaSookhtegi();
                            badConditionSetter.fun(new ProjectModel.IsolationChainsBadCondition(badCondition.getDamageType(),
                                    badCondition.getNumberOfDamage(), badCondition.isTeared(), badCondition.isTilted(), badCondition.getOffset(),
                                    badCondition.getNick(), badCondition.getCapAndPins(), badCondition.getInsolatorPinDropping(), badCondition.getDontHaveEshpil(),
                                    badCondition.getDontHaveBoltsAndNuts(), badCondition.getPolution(), populationPercent, badCondition.getNoeAsibeMaghare(),
                                    tedadeSookhtegiYaShekastegi, badCondition.getDescription()));
                            break;
                        case Constants.IS_GOOD:
                            badConditionNullSetter.fun();
                            break;
                    }
                },
                        context.getString(R.string.iradate_maghare_va_molhaghat),
                        badCondition::getDescription, s -> {
                    if (badConditionGetter.fun() != null) {
                        badConditionGetter.fun().setDescription(s);
                        badCondition.setDescription(s);
                    }
                });
                goodBadDataModelList.add(zanjireMaghareVaMolhaghatChose);
                goodBadDataModelList.add(zanjireMaghareVaMolhaghat);
                DoubleCheckBoxDataModel ofsetLabParidegi = new DoubleCheckBoxDataModel(context.getString(R.string.ofset), null, context.getString(R.string.lab_paridegi), null,
                        badCondition::getOffset, (s) -> {
                    if (badConditionGetter.fun() != null) {
                        badConditionGetter.fun().setOffset(s);
                        badCondition.setOffset(s);
                    }
                },
                        badCondition::getNick, (s) -> {
                    if (badConditionGetter.fun() != null) {
                        badConditionGetter.fun().setNick(s);
                        badCondition.setNick(s);
                    }
                });
                DoubleCheckBoxDataModel kajBodanZanjirNadashtanEshpil = new DoubleCheckBoxDataModel(context.getString(R.string.kaj_bodan_zanjir), null, context.getString(R.string.nadashtan_eshpil), null,
                        badCondition::isTilted, (s) -> {
                    if (badConditionGetter.fun() != null) {
                        badConditionGetter.fun().setTilted(s);
                        badCondition.setTilted(s);
                    }
                },
                        badCondition::getDontHaveEshpil, (s) -> {
                    if (badConditionGetter.fun() != null) {
                        badConditionGetter.fun().setDontHaveEshpil(s);
                        badCondition.setDontHaveEshpil(s);
                    }
                });
                DoubleCheckBoxDataModel alodegiParegi = new DoubleCheckBoxDataModel(context.getString(R.string.alodegi), null, context.getString(R.string.paregi), null,
                        badCondition::getPolution, (s) -> {
                    if (badConditionGetter.fun() != null) {
                        badConditionGetter.fun().setPolution(s);
                        badCondition.setPolution(s);
                        if (Objects.equals(s, "false")) {
                            badConditionGetter.fun().setPolutionPercent(badCondition.getPolutionPercent());
                        } else if (Objects.equals(s, "true")) {
                            badConditionGetter.fun().setPolutionPercent(null);
                        }
                    }
                }, badCondition::isTeared, (s) -> {
                    if (badConditionGetter.fun() != null) {
                        badConditionGetter.fun().setTeared(s);
                        badCondition.setTeared(s);
                    }
                });
                alodegiParegi.setOnCheckedChangeListenerRight(onAlodegiCheckChanged(zanjireMaghareVaMolhaghatFieldsList, alodegiParegi, i, j));
                zanjireMaghareVaMolhaghatChose.setOnCheckedChangeListener(onZanjireVaMolhaghatCheckChange(alodegiParegi));
                if (Objects.equals(typeGetterHelper.getText(), context.getString(R.string.shisheyi)) ||
                        Objects.equals(typeGetterHelper.getText(), context.getString(R.string.seramiki)))
                    alodegiParegi.setLeftCheckBoxVisibility(View.GONE);
                DoubleCheckBoxDataModel nadashtanPichMohreDarHalBironAmadanAzPin = new DoubleCheckBoxDataModel(context.getString(R.string.nadashtan_pich_mohre), null, context.getString(R.string.dar_hale_biron_amadan_az_pin_maghare), null,
                        badCondition::getDontHaveBoltsAndNuts, (s) -> {
                    if (badConditionGetter.fun() != null) {
                        badConditionGetter.fun().setDontHaveBoltsAndNuts(s);
                        badCondition.setDontHaveBoltsAndNuts(s);
                    }
                },
                        badCondition::getInsolatorPinDropping, (s) -> {
                    if (badConditionGetter.fun() != null) {
                        badConditionGetter.fun().setInsolatorPinDropping(s);
                        badCondition.setInsolatorPinDropping(s);
                    }
                });

                NumberEditViewDataModel tedadSokhtegi = new NumberEditViewDataModel(context.getString(R.string.tedad_sokhtegi),
                        badCondition::getTedadeShekastegiYaSookhtegi,
                        (s) -> {
                            badCondition.setTedadeShekastegiYaSookhtegi(s);
                            if (badConditionGetter.fun() != null)
                                badConditionGetter.fun().setTedadeShekastegiYaSookhtegi(s);
                        });
                NumberEditViewDataModel tedadShkastegi = new NumberEditViewDataModel(context.getString(R.string.tedad_shekastegi),
                        badCondition::getTedadeShekastegiYaSookhtegi,
                        (s) -> {
                            badCondition.setTedadeShekastegiYaSookhtegi(s);
                            if (badConditionGetter.fun() != null)
                                badConditionGetter.fun().setTedadeShekastegiYaSookhtegi(s);
                        });

                SegmentedControlDataModel noeAsibMaghare = new SegmentedControlDataModel(context.getString(R.string.nadarad), context.getString(R.string.shekastegi), context.getString(R.string.sokhtegi), context.getString(R.string.noe_asib_maghare),
                        badCondition::getNoeAsibeMaghare,
                        (s) -> {
                            if (badConditionGetter.fun() != null) {
                                badConditionGetter.fun().setNoeAsibeMaghare(s);
                                badCondition.setNoeAsibeMaghare(s);
                                if (Objects.equals(s, context.getString(R.string.sokhtegi))) {
                                    badCondition.setTedadeShekastegiYaSookhtegi(tedadSokhtegi.getInputNumber());
                                    if (badConditionGetter.fun() != null)
                                        badConditionGetter.fun().setTedadeShekastegiYaSookhtegi(tedadSokhtegi.getInputNumber());
                                } else if (Objects.equals(s, context.getString(R.string.shekastegi))) {
                                    badCondition.setTedadeShekastegiYaSookhtegi(tedadShkastegi.getInputNumber());
                                    if (badConditionGetter.fun() != null)
                                        badConditionGetter.fun().setTedadeShekastegiYaSookhtegi(tedadShkastegi.getInputNumber());
                                } else {
                                    if (badConditionGetter.fun() != null)
                                        badConditionGetter.fun().setTedadeShekastegiYaSookhtegi(null);
                                }
                            }
                        });

                noeAsibMaghare.setOnCheckedChangeListener(onNoeAsibCheckChanged(tedadShkastegi, tedadSokhtegi, noeAsibMaghare, zanjireMaghareVaMolhaghat));

                SegmentedControlDataModel kapPinMaghare = new SegmentedControlDataModel(context.getString(R.string.nadarad), context.getString(R.string.khordegi), context.getString(R.string.zang_zadegi), context.getString(R.string.kap_pin_maghare),
                        badCondition::getCapAndPins, (s) -> {
                    badCondition.setCapAndPins(s);
                    if (badConditionGetter.fun() != null) {
                        badConditionGetter.fun().setCapAndPins(s);
                    }
                });

                GeneralDataModel[] zanjireMaghareVaMolhaghatFields = {ofsetLabParidegi, kajBodanZanjirNadashtanEshpil, nadashtanPichMohreDarHalBironAmadanAzPin, alodegiParegi, noeAsibMaghare, kapPinMaghare};
                zanjireMaghareVaMolhaghatFieldsList.addAll(Arrays.asList(zanjireMaghareVaMolhaghatFields));
            }
        }
    }

    private CompoundButton.OnCheckedChangeListener onZanjireVaMolhaghatCheckChange(DoubleCheckBoxDataModel alodegiParegi) {
        return (buttonView, isChecked) -> {
            if (isChecked) {
                if ((Objects.equals(String.valueOf(buttonView.getText()), context.getString(R.string.shisheyi))
                        || Objects.equals(String.valueOf(buttonView.getText()), context.getString(R.string.seramiki)))) {
                    alodegiParegi.setLeftCheckBoxVisibility(View.GONE);
                } else {
                    alodegiParegi.setLeftCheckBoxVisibility(View.VISIBLE);
                }
            }
        };
    }

    private SmoothCheckBox.OnCheckedChangeListener onAlodegiCheckChanged(List<GeneralDataModel> zanjireMaghareVaMolhaghatFieldsList,
                                                                         DoubleCheckBoxDataModel ofsetAlodegi, int i, int j) {
        NumberEditViewDataModel numberEditViewDataModel;
        if (j == 0) {
            numberEditViewDataModel = new NumberEditViewDataModel(context.getString(R.string.darsade_aloodegi),
                    () -> reviewModelHelper.getIsolationChainsList().get(i).getIfAConditionBad().getPolutionPercent(),
                    (s) -> {
                        reviewModelHelper.getIsolationChainsList().get(i).getIfAConditionBad().setPolutionPercent(s);
                        reviewModel.getIsolationChainsList().get(i).getIfAConditionBad().setPolutionPercent(s);
                    });
        } else if (j == 1) {
            numberEditViewDataModel = new NumberEditViewDataModel(context.getString(R.string.darsade_aloodegi),
                    () -> reviewModelHelper.getIsolationChainsList().get(i).getIfBConditionBad().getPolutionPercent(),
                    (s) -> {
                        reviewModelHelper.getIsolationChainsList().get(i).getIfBConditionBad().setPolutionPercent(s);
                        reviewModel.getIsolationChainsList().get(i).getIfBConditionBad().setPolutionPercent(s);
                    });
        } else {
            numberEditViewDataModel = new NumberEditViewDataModel(context.getString(R.string.darsade_aloodegi),
                    () -> reviewModelHelper.getIsolationChainsList().get(i).getIfCConditionBad().getPolutionPercent(),
                    (s) -> {
                        reviewModelHelper.getIsolationChainsList().get(i).getIfCConditionBad().setPolutionPercent(s);
                        reviewModel.getIsolationChainsList().get(i).getIfCConditionBad().setPolutionPercent(s);
                    });
        }
        return (checkBox, isChecked) -> {
            if (isChecked) {
                zanjireMaghareVaMolhaghatFieldsList.add(zanjireMaghareVaMolhaghatFieldsList.indexOf(ofsetAlodegi) + 1, numberEditViewDataModel);
                int position = adapter.getItemsModelList().indexOf(ofsetAlodegi) + 1;
                adapter.getItemsModelList().add(position, numberEditViewDataModel);
                try {
                    adapter.notifyItemInserted(position);
                } catch (IllegalStateException e) {
                    adapter.setHasItemsChanged(true);
                }
            } else {
                int position = adapter.getItemsModelList().indexOf(numberEditViewDataModel);
                zanjireMaghareVaMolhaghatFieldsList.remove(numberEditViewDataModel);
                adapter.getItemsModelList().remove(numberEditViewDataModel);
                try {
                    adapter.notifyItemRemoved(position);
                } catch (IllegalStateException e) {
                    adapter.setHasItemsChanged(true);
                }
            }
        };
    }

    private String qrCodeToDespatchingConverter(String qrCode) {
        if (qrCode != null && qrCode.length() > 7)
            return getBarCodeTrimmed(qrCode).substring(1, 6).toUpperCase();
        else return qrCode;
    }

    public static String getBarCodeTrimmed(String qrCode) {
        if (qrCode != null)
            return qrCode.replaceAll(" ", "");
        return null;
    }


    @Override
    public int getLeftIconSource() {
        return R.drawable.ic_back;
    }

    ReviewThirdLevelVM() {
        super();
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
        return context.getString(R.string.review_third_level_hint);
    }

    //for parcel


    private ReviewThirdLevelVM(Parcel in) {
        super(in);
        reviewModel = mainActivityVM.getReviewModel();
        reviewModelHelper = mainActivityVM.getReviewModelHelper();
    }

    public static Creator<ReviewThirdLevelVM> CREATOR = new Creator<ReviewThirdLevelVM>() {
        @Override
        public ReviewThirdLevelVM createFromParcel(Parcel source) {
            return new ReviewThirdLevelVM(source);
        }

        @Override
        public ReviewThirdLevelVM[] newArray(int size) {
            return new ReviewThirdLevelVM[size];
        }
    };
}

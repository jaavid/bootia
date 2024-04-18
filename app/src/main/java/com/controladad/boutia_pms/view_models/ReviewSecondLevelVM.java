package com.controladad.boutia_pms.view_models;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import androidx.databinding.DataBindingUtil;
import android.os.Parcel;
import androidx.annotation.Nullable;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.controladad.boutia_pms.R;
import com.controladad.boutia_pms.adapters.ReviewFirstLevelAdapter;
import com.controladad.boutia_pms.adapters.StatefulRecyclerView;
import com.controladad.boutia_pms.databinding.FragmentReviewSecondLevelBinding;
import com.controladad.boutia_pms.location.LocationService;
import com.controladad.boutia_pms.models.data_models.ProjectModel;
import com.controladad.boutia_pms.utility.Constants;
import com.controladad.boutia_pms.view_models.items_view_models.BugsDataModel;
import com.controladad.boutia_pms.view_models.items_view_models.CheckBoxDataModel;
import com.controladad.boutia_pms.view_models.items_view_models.CustomEditTextDataModel;
import com.controladad.boutia_pms.view_models.items_view_models.DoubleButtonDataModel;
import com.controladad.boutia_pms.view_models.items_view_models.GeneralDataModel;
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
public class ReviewSecondLevelVM extends GeneralVM {

    private ProjectModel.ReviewModel reviewModel;
    private ProjectModel.ReviewModel reviewModelHelper;
    @Getter
    private ReviewFirstLevelAdapter adapter = new ReviewFirstLevelAdapter();
    private List<GeneralDataModel> goodBadDataModelList = new ArrayList<>();

    private CheckBoxDataModel needToCleaningGrassesFoundationField;
    private SegmentedControlDataModel amountOfNeedToCleaningGrassesFoundationField;
    private List<GeneralDataModel> foundationElementList = new ArrayList<>();
    private GoodBadDataModel foundation;

    private GoodBadDataModel earthWire;
    private List<GeneralDataModel> earthWireElementList = new ArrayList<>();

    //فیلدهای تابلو
    private List<GeneralDataModel> tabloFiledsList = new ArrayList<>();
    private GoodBadDataModel tablo;

    //قیلدهای پیچ و مهره
    private List<GeneralDataModel> pichMohreFieldsList = new ArrayList<>();
    private GoodBadDataModel pichMohre;
    private DoubleButtonDataModel pichMohreDoubleButton;


    private List<GeneralDataModel> pichePeleFieldsList = new ArrayList<>();
    private GoodBadDataModel pichePele;

    private List<GeneralDataModel> khareZedeSooudFieldsList = new ArrayList<>();
    private GoodBadDataModel khareZedeSooud;

    private List<GeneralDataModel> plateFieldsList = new ArrayList<>();
    private GoodBadDataModel plate;
    private DoubleButtonDataModel plateDoubleButton;

    private List<GeneralDataModel> nabshiFieldsList = new ArrayList<>();
    private GoodBadDataModel nabshi;
    private DoubleButtonDataModel nabshiDoubleButton;

    private List<GeneralDataModel> jooshKariFieldsList = new ArrayList<>();
    private GoodBadDataModel jooshKari;

    private CheckBoxDataModel checkBoxDataModel = new CheckBoxDataModel(context.getString(R.string.flood), onCheckedChangeListener(), () -> "", (s) -> {
    });
    private SegmentedControlDataModel amountOfDamage = new SegmentedControlDataModel(context.getString(R.string.dose_not_have), context.getString(R.string.low), context.getString(R.string.medium), context.getString(R.string.high), context.getString(R.string.very_high), context.getString(R.string.amount_of_damages), () -> "", (s) -> {
    });
    private SegmentedControlDataModel amountOfDanger = new SegmentedControlDataModel(context.getString(R.string.dose_not_have), context.getString(R.string.low), context.getString(R.string.medium), context.getString(R.string.high), context.getString(R.string.very_high), context.getString(R.string.amount_of_risk), () -> "", (s) -> {
    });

    ReviewSecondLevelVM() {
        super();
        reviewModel = mainActivityVM.getReviewModel();
        reviewModelHelper = mainActivityVM.getReviewModelHelper();
    }

    private StatefulRecyclerView recyclerView;

    @Override
    public View binding(LayoutInflater inflater, @Nullable ViewGroup container) {
        FragmentReviewSecondLevelBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_review_second_level, container, false);
        binding.setReviewSecondLevelVM(this);
        recyclerView = binding.reviewSecondLevelRecyclerView;
        return binding.getRoot();
    }

    @Override
    public void onCreateFragment() {
        super.onCreateFragment();
        fieldsFilling();
        goodBadDataModelList.add(foundation);
        goodBadDataModelList.add(earthWire);
        goodBadDataModelList.add(tablo);
        goodBadDataModelList.add(pichMohre);
        goodBadDataModelList.add(pichePele);
        goodBadDataModelList.add(khareZedeSooud);
        goodBadDataModelList.add(plate);
        goodBadDataModelList.add(nabshi);
        goodBadDataModelList.add(jooshKari);
        goodBadDataModelList.add(checkBoxDataModel);
        goodBadDataModelList.add(new ImagesRecyclerViewDataModel());
        goodBadDataModelList.add(new SingleButtonDataModel(context.getString(R.string.ok_and_continue), onOkButtonClicked(), () -> "", (s) -> {
        }));
        adapter.updateData(goodBadDataModelList);
    }

    @Override
    public void onCreateView() {
        super.onCreateView();

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

            if (Objects.equals(reviewModel.getFoundation().getCondition(), Constants.IS_BAD) &&
                    reviewModel.getFoundation().getFoundationIfBad() != null) {
                String khakbardari = reviewModel.getFoundation().getFoundationIfBad().getKhakBardari();
                if (khakbardari != null && !khakbardari.isEmpty() &&
                        (!khakbardari.contains(".") || khakbardari.substring(khakbardari.indexOf(".")).length() < 3)
                ) {
                    showSnackBar("خاکبرداری باید تا دورقم اعشار باشد.");
                    return;
                }
            }
            if (Objects.equals(reviewModel.getFoundation().getCondition(), Constants.IS_BAD) &&
                    reviewModel.getFoundation().getFoundationIfBad() != null) {
                String khakRizi = reviewModel.getFoundation().getFoundationIfBad().getKhakRizi();
                if (khakRizi != null && !khakRizi.isEmpty() &&
                        (!khakRizi.contains(".") || khakRizi.substring(khakRizi.indexOf(".")).length() < 3)
                ) {
                    showSnackBar("خاکریزی باید تا دورقم اعشار باشد.");
                    return;
                }
            }

            if (reviewModel.getCorner().getCornerConditionIfBad() != null){
                if (reviewModel.getCorner().getCornerConditionIfBad().getIfCornerStolen() != null)
                {
                    for (ProjectModel.CornerStolen serghatItem : reviewModel.getCorner().getCornerConditionIfBad().getIfCornerStolen()) {
                        if (serghatItem.getSize() == null) {
                            showSnackBar("سایز نبشی اجباری است و باید وارد شود.");
                            return;
                        } else if (serghatItem.getSize() != null) {
                            if (Integer.parseInt(serghatItem.getSize()) < 1 ||
                                    Integer.parseInt(serghatItem.getSize()) > 11) {
                                showSnackBar("سایز نبشی باید بین ۱ تا ۱۱ باشد.");
                                return;
                            }
                        }
                    }
                }


            }

            if (reviewModel.getBoltsAndNuts().getBoltsAndNutsConditionsIfBad() != null) {
                for (ProjectModel.PichMohreItem pichMohreItem : reviewModel.getBoltsAndNuts().getBoltsAndNutsConditionsIfBad().getPichMohreItemList()) {
                    if (pichMohreItem.getAmount() == null || pichMohreItem.getAmount().isEmpty()) {
                        showSnackBar("لطفا تعداد پیچ و مهره را وارد کنید.");
                        return;
                    }
                    if (pichMohreItem.getAmount() == null || pichMohreItem.getDescription() == null) {
                        showSnackBar("لطفا نوع عیب پیچ و مهره را انتخاب کنید.");
                        return;
                    }
                    if (pichMohreItem.getAmount() == null || pichMohreItem.getSize() == null) {
                        showSnackBar("لطفا سایز پیچ را وارد کنید.");
                        return;
                    }
                }
            }

            if (Objects.equals(reviewModel.getPanel().getCondition(), Constants.IS_BAD) &&
                    Objects.equals(reviewModel.getPanel().getPanelBadConditions().getPanelNumberCondition(), Constants.IS_CHECKED) &&
                    reviewModel.getPanel().getPanelBadConditions().getPanelNumber().getCondition() == null
            ) {
                showSnackBar("لطفا نوع مشکل تابلو شماره را انتخاب کنید");
                return;
            }

            if (Objects.equals(reviewModel.getPanel().getCondition(), Constants.IS_BAD) &&
                    Objects.equals(reviewModel.getPanel().getPanelBadConditions().getPanelHavayeeCondition(), Constants.IS_CHECKED) &&
                    reviewModel.getPanel().getPanelBadConditions().getPanelHavayee().getCondition() == null
            ) {
                showSnackBar("لطفا نوع مشکل تابلو هوایی را انتخاب کنید");
                return;
            }

            if (Objects.equals(reviewModel.getPanel().getCondition(), Constants.IS_BAD) &&
                    Objects.equals(reviewModel.getPanel().getPanelBadConditions().getPhasePanelCondition(), Constants.IS_CHECKED) &&
                    reviewModel.getPanel().getPanelBadConditions().getPhasePanel().getCondition() == null
            ) {
                showSnackBar("لطفا نوع مشکل تابلو آرایش فازی را انتخاب کنید");
                return;
            }

            if (Objects.equals(reviewModel.getPanel().getCondition(), Constants.IS_BAD) &&
                    Objects.equals(reviewModel.getPanel().getPanelBadConditions().getPanelDomainCondition(), Constants.IS_CHECKED) &&
                    reviewModel.getPanel().getPanelBadConditions().getPanelDomain().getCondition() == null
            ) {
                showSnackBar("لطفا نوع مشکل تابلو حریم را انتخاب کنید");
                return;
            }

            getRouter().navigateTo(Constants.REVIEW_THIRD_LEVEL, new ReviewThirdLevelVM());
        };
    }

    private SmoothCheckBox.OnCheckedChangeListener onNeedToCleaningGrassesCheckedChange() {
        return (buttonView, isChecked) -> {
            needToCleaningGrassesFoundationField.setUserSelected(isChecked);
            List<GeneralDataModel> dataModelList = adapter.getItemsModelList();
            int position = dataModelList.indexOf(needToCleaningGrassesFoundationField) + 1;
            if (isChecked && !dataModelList.contains(amountOfNeedToCleaningGrassesFoundationField)) {
                dataModelList.add(position, amountOfNeedToCleaningGrassesFoundationField);
                foundationElementList.add(foundationElementList.indexOf(needToCleaningGrassesFoundationField) + 1, amountOfNeedToCleaningGrassesFoundationField);
                try {
                    adapter.notifyItemInserted(position);
                } catch (IllegalStateException e) {
                    adapter.setHasItemsChanged(true);
                }
            } else if (!isChecked && dataModelList.contains(amountOfNeedToCleaningGrassesFoundationField)) {
                dataModelList.remove(amountOfNeedToCleaningGrassesFoundationField);
                foundationElementList.remove(amountOfNeedToCleaningGrassesFoundationField);
                try {
                    adapter.notifyItemRemoved(position);
                } catch (IllegalStateException e) {
                    adapter.setHasItemsChanged(true);
                }
            }
        };
    }

    private void addPichMohreItems(int i) {
        List<ProjectModel.PichMohreItem> helperModelList = reviewModelHelper.getBoltsAndNuts().getBoltsAndNutsConditionsIfBad().getPichMohreItemList();
        if (helperModelList.size() < i + 1)
            helperModelList.add(new ProjectModel.PichMohreItem(null, null, null));
        int position = adapter.getItemsModelList().indexOf(pichMohre) + pichMohreFieldsList.size() + 1;
        BugsDataModel bugsDataModel = new BugsDataModel();
        ProjectModel.PichMohreItem mohreItem = helperModelList.get(i);
        bugsDataModel.setOnRemoveItemClicked(v -> {
            pichMohre.onRemoveItemClicked(bugsDataModel).onClick(v);
            helperModelList.remove(mohreItem);
        });
        SegmentedControlDataModel bugType = new SegmentedControlDataModel(context.getString(R.string.achar_keshi), context.getString(R.string.kasri), context.getString(R.string.zang_zadegi), context.getString(R.string.others), context.getString(R.string.bug_type),
                mohreItem::getDescription,
                mohreItem::setDescription);
        SegmentedControlDataModel sizePich = new SegmentedControlDataModel(context.getString(R.string.twelve), context.getString(R.string.fourteen), context.getString(R.string.sixteen), context.getString(R.string.others), context.getString(R.string.size_pich),
                mohreItem::getSize,
                mohreItem::setSize);
        NumberEditViewDataModel editTextDataModel = new NumberEditViewDataModel(context.getString(R.string.enter_the_number),
                mohreItem::getAmount,
                mohreItem::setAmount);
        if (i > 0)
            pichMohreFieldsList.add(pichMohreFieldsList.indexOf(pichMohreDoubleButton), bugsDataModel);
        pichMohreFieldsList.add(pichMohreFieldsList.indexOf(pichMohreDoubleButton), bugType);
        pichMohreFieldsList.add(pichMohreFieldsList.indexOf(pichMohreDoubleButton), sizePich);
        pichMohreFieldsList.add(pichMohreFieldsList.indexOf(pichMohreDoubleButton), editTextDataModel);
        if (adapter.getItemsModelList().contains(pichMohre)) {
            if (i > 0) adapter.getItemsModelList().add(position, bugsDataModel);
            adapter.getItemsModelList().add(position + 1, bugType);
            adapter.getItemsModelList().add(position + 2, sizePich);
            adapter.getItemsModelList().add(position + 3, editTextDataModel);
            try {
                if (i > 0) adapter.notifyItemRangeInserted(position, 4);
                else adapter.notifyItemRangeInserted(position, 3);
            } catch (IllegalStateException e) {
                adapter.setHasItemsChanged(true);
            }
        }
    }

    private void addPlateItems(int i) {
        List<ProjectModel.PlateIfBadCondition> badConditions = reviewModelHelper.getPlate().getPlateIfBadConditions().getPlateIfBadCondition();
        if (badConditions.size() < i + 1)
            badConditions.add(new ProjectModel.PlateIfBadCondition(null, null, null, null, null, null));
        ProjectModel.PlateIfBadCondition badCondition = badConditions.get(i);
        int position = adapter.getItemsModelList().indexOf(plate) + plateFieldsList.size() + 1;
        BugsDataModel bugsDataModel = new BugsDataModel();
        bugsDataModel.setOnRemoveItemClicked(v -> {
            plate.onRemoveItemClicked(bugsDataModel).onClick(v);
            badConditions.remove(badCondition);
        });
        CustomEditTextDataModel mogheiyat = new CustomEditTextDataModel(context.getString(R.string.mogheiyat),
                badCondition::getPlatePlace, badCondition::setPlatePlace);
        NumberEditViewDataModel kasri = new NumberEditViewDataModel(context.getString(R.string.kasri), badCondition::getNumberOfMissingPlates, badCondition::setNumberOfMissingPlates);
        NumberEditViewDataModel zangZadegi = new NumberEditViewDataModel(context.getString(R.string.zang_zadegi), badCondition::isPlateRusty, badCondition::setPlateRusty);
        NumberEditViewDataModel kasriPichMohre = new NumberEditViewDataModel(context.getString(R.string.kasri_pich_mohre_plate), badCondition::getPlateThornAndNutsShortage, badCondition::setPlateThornAndNutsShortage);
        CustomEditTextDataModel shomare = new CustomEditTextDataModel(context.getString(R.string.plate_number), badCondition::getPlateNumber, badCondition::setPlateNumber);
        NumberEditViewDataModel shekastegi = new NumberEditViewDataModel(context.getString(R.string.shekastegi), badCondition::isPlateBroken, badCondition::setPlateBroken);
        if (i > 0) plateFieldsList.add(plateFieldsList.indexOf(plateDoubleButton), bugsDataModel);

        GeneralDataModel[] dataModels = {mogheiyat, shomare, kasri, zangZadegi, kasriPichMohre, shekastegi};
        plateFieldsList.addAll(plateFieldsList.indexOf(plateDoubleButton), Arrays.asList(dataModels));
        if (adapter.getItemsModelList().contains(plate)) {
            adapter.getItemsModelList().addAll(position, Arrays.asList(dataModels));
            if (i > 0)
                adapter.getItemsModelList().add(position, bugsDataModel);
            try {
                if (i > 0) adapter.notifyItemRangeInserted(position, 7);
                else adapter.notifyItemRangeInserted(position, 6);
            } catch (IllegalStateException e) {
                adapter.setHasItemsChanged(true);
            }
        }
    }

    private void addNabshiItems(int i) {
        int position = adapter.getItemsModelList().indexOf(nabshi) + nabshiFieldsList.size() + 1;
        List<ProjectModel.CornerStolen> serghtatItemsList = reviewModelHelper.getCorner().getCornerConditionIfBad().getIfCornerStolen();
        if (serghtatItemsList.size() < i + 1)
            serghtatItemsList.add(new ProjectModel.CornerStolen(null, null, null));
        ProjectModel.CornerStolen serghatItem = serghtatItemsList.get(i);
        BugsDataModel bugsDataModel = new BugsDataModel();
        bugsDataModel.setOnRemoveItemClicked(v -> {
            nabshi.onRemoveItemClicked(bugsDataModel).onClick(v);
            serghtatItemsList.remove(serghatItem);
        });
        NumberEditViewDataModel size = new NumberEditViewDataModel(context.getString(R.string.size_nabshi), serghatItem::getSize, serghatItem::setSize);
        NumberEditViewDataModel tedad = new NumberEditViewDataModel(context.getString(R.string.tedad), serghatItem::getAmount, serghatItem::setAmount);
        NumberEditViewDataModel width = new NumberEditViewDataModel(context.getString(R.string.width), serghatItem::getWidth, serghatItem::setWidth);
        if (i > 0)
            nabshiFieldsList.add(nabshiFieldsList.indexOf(nabshiDoubleButton), bugsDataModel);
        nabshiFieldsList.add(nabshiFieldsList.indexOf(nabshiDoubleButton), size);
        nabshiFieldsList.add(nabshiFieldsList.indexOf(nabshiDoubleButton), tedad);
        nabshiFieldsList.add(nabshiFieldsList.indexOf(nabshiDoubleButton), width);
        if (adapter.getItemsModelList().contains(nabshi)) {
            if (i > 0) adapter.getItemsModelList().add(position, bugsDataModel);
            adapter.getItemsModelList().add(position + 1, size);
            adapter.getItemsModelList().add(position + 2, tedad);
            adapter.getItemsModelList().add(position + 3, width);
            try {
                if (i > 0) adapter.notifyItemRangeInserted(position, 4);
                else adapter.notifyItemRangeInserted(position, 3);
            } catch (IllegalStateException e) {
                adapter.setHasItemsChanged(true);
            }
        }
    }

    @Override
    public int getLeftIconSource() {
        return R.drawable.ic_back;
    }

    private View.OnClickListener onAddingPichMohreItemClicked() {
        return v -> addPichMohreItems(reviewModelHelper.getBoltsAndNuts().getBoltsAndNutsConditionsIfBad().getPichMohreItemList().size());
    }

    private SmoothCheckBox.OnCheckedChangeListener onCheckedChangeListener() {
        return (buttonView, isChecked) -> {
            checkBoxDataModel.setUserSelected(isChecked);
            int position = adapter.getItemsModelList().indexOf(checkBoxDataModel);
            if (isChecked && !adapter.getItemsModelList().contains(amountOfDamage)) {
                adapter.getItemsModelList().add(position + 1, amountOfDamage);
                adapter.getItemsModelList().add(position + 2, amountOfDanger);
                try {
                    adapter.notifyItemRangeInserted(position + 1, 2);
                } catch (IllegalStateException e) {
                    adapter.setHasItemsChanged(true);
                }
            } else if (!isChecked && adapter.getItemsModelList().contains(amountOfDamage)) {
                adapter.getItemsModelList().remove(amountOfDamage);
                adapter.getItemsModelList().remove(amountOfDanger);
                try {
                    adapter.notifyItemRangeRemoved(position + 1, 2);
                } catch (IllegalStateException e) {
                    adapter.setHasItemsChanged(true);
                }

            }
        };
    }

    private void fieldsFilling() {
        foundationFieldsFilling();
        earthWireFieldsFilling();
        tabloFieldsFilling();
        pichMohreFieldsFilling();
        pichePeleFieldsFilling();
        khareZedeSooudFieldsFilling();
        plateFieldsFilling();
        nabshiFieldsFilling();
        jooshKariFieldFilling();
        floodFieldsFilling();
    }

    private void jooshKariFieldFilling() {
        jooshKari = new GoodBadDataModel(context.getString(R.string.joosh_kari), jooshKariFieldsList, adapter,
                () -> reviewModelHelper.getJooshkari().getCondition(),
                (s) -> {
                    reviewModelHelper.getJooshkari().setCondition(s);
                    reviewModel.getJooshkari().setCondition(s);
                    switch (s) {
                        case Constants.IS_BAD:
                            reviewModel.getJooshkari().setBadCondition(reviewModelHelper.getJooshkari().getBadCondition());
                            break;
                        case Constants.IS_GOOD:
                            reviewModel.getJooshkari().setBadCondition(null);
                    }
                },
                () -> reviewModelHelper.getJooshkari().getBadCondition().getDesc(),
                (s) -> reviewModelHelper.getJooshkari().getBadCondition().setDesc(s));
        jooshKariFieldsList.add(new NumberEditViewDataModel("طول جوشکاری (به متر)",
                () -> reviewModelHelper.getJooshkari().getBadCondition().getTooleJooshkariBeMetr(), s -> {
            reviewModelHelper.getJooshkari().getBadCondition().setTooleJooshkariBeMetr(s);
        },true));
        jooshKari.setGoodButtonText(context.getString(R.string.nadarad));
        jooshKari.setBadButtonText(context.getString(R.string.darad));
    }

    private void floodFieldsFilling() {
        checkBoxDataModel = new CheckBoxDataModel(context.getString(R.string.flood), onCheckedChangeListener(),
                () -> reviewModelHelper.getFloodCondition().getCondition(),
                (s) -> {
                    reviewModelHelper.getFloodCondition().setCondition(s);
                    reviewModel.getFloodCondition().setCondition(s);
                    if (Objects.equals(reviewModelHelper.getFloodCondition().getCondition(), Constants.IS_CHECKED)) {
                        reviewModel.getFloodCondition().setBadCondition(reviewModelHelper.getFloodCondition().getBadCondition());
                    } else reviewModel.getFloodCondition().setBadCondition(null);
                });
        amountOfDamage = new SegmentedControlDataModel(context.getString(R.string.dose_not_have), context.getString(R.string.low), context.getString(R.string.medium), context.getString(R.string.high), context.getString(R.string.very_high), context.getString(R.string.amount_of_damages),
                () -> reviewModelHelper.getFloodCondition().getBadCondition().getDamageAmount(),
                (s) -> reviewModelHelper.getFloodCondition().getBadCondition().setDamageAmount(s));
        amountOfDanger = new SegmentedControlDataModel(context.getString(R.string.dose_not_have), context.getString(R.string.low), context.getString(R.string.medium), context.getString(R.string.high), context.getString(R.string.very_high), context.getString(R.string.amount_of_risk),
                () -> reviewModelHelper.getFloodCondition().getBadCondition().getDangerAmount(),
                (s) -> reviewModelHelper.getFloodCondition().getBadCondition().setDangerAmount(s));
    }

    private void nabshiFieldsFilling() {
        ProjectModel.CornerConditionIfBad badCondition = reviewModelHelper.getCorner().getCornerConditionIfBad();
        List<ProjectModel.CornerStolen> seraghtList = badCondition.getIfCornerStolen();
        if (seraghtList.isEmpty())
            seraghtList.add(new ProjectModel.CornerStolen(null, null, null));
        nabshi = new GoodBadDataModel(context.getString(R.string.nabshi), nabshiFieldsList, adapter,
                () -> reviewModelHelper.getCorner().getCondition(),
                (s) -> {
                    reviewModelHelper.getCorner().setCondition(s);
                    reviewModel.getCorner().setCondition(s);
                    switch (s) {
                        case Constants.IS_BAD:
                            reviewModel.getCorner().setCornerConditionIfBad(badCondition);
                            break;
                        case Constants.IS_GOOD:
                            reviewModel.getCorner().setCornerConditionIfBad(null);
                            break;
                    }
                },
                true, 4, badCondition::getDescription, badCondition::setDescription);
        CustomEditTextDataModel shomareNabshi = new CustomEditTextDataModel(context.getString(R.string.shomare_nabshi), badCondition::getShomareNabshi, badCondition::setShomareNabshi);
        CustomEditTextDataModel mogheiyatNabshi = new CustomEditTextDataModel(context.getString(R.string.mogheiyat), badCondition::getMogheiat, badCondition::setMogheiat);
        NumberEditViewDataModel enhenayeNabshi = new NumberEditViewDataModel(context.getString(R.string.enhenaye_nabshi), badCondition::getCornerCurve, badCondition::setCornerCurve);
        NumberEditViewDataModel zangZadegiNabshi = new NumberEditViewDataModel(context.getString(R.string.zang_zadegi), badCondition::getRustySizeInMeter, badCondition::setRustySizeInMeter);
        NumberEditViewDataModel yekSarBazNabshi = new NumberEditViewDataModel(context.getString(R.string.yek_sar_baz), badCondition::getOneWayOpen, badCondition::setOneWayOpen);
        SimpleTextViewDataModel serghatNabshi = new SimpleTextViewDataModel(context.getString(R.string.serghat_nabshi), () -> "", (s) -> {
        });
        nabshiDoubleButton = new DoubleButtonDataModel(context.getString(R.string.ok), context.getString(R.string.adding), nabshi.onClickListener(),
                v -> addNabshiItems(seraghtList.size()), () -> "", (s) -> {
        });
        GeneralDataModel[] nabshiFields = {mogheiyatNabshi, shomareNabshi, zangZadegiNabshi, enhenayeNabshi, yekSarBazNabshi, serghatNabshi, nabshiDoubleButton};
        nabshiFieldsList.clear();
        nabshiFieldsList.addAll(Arrays.asList(nabshiFields));
        int j = seraghtList.size();
        for (int i = 0; i < j; i++) {
            addNabshiItems(i);
        }
    }

    private void plateFieldsFilling() {
        ProjectModel.PlateIfBadConditions badConditions = reviewModelHelper.getPlate().getPlateIfBadConditions();
        if (badConditions.getPlateIfBadCondition().isEmpty())
            badConditions.getPlateIfBadCondition().add(new ProjectModel.PlateIfBadCondition(null, null, null, null, null, null));
        plate = new GoodBadDataModel(context.getString(R.string.plate), plateFieldsList, adapter,
                () -> reviewModelHelper.getPlate().getCondition(),
                (s) -> {
                    reviewModelHelper.getPlate().setCondition(s);
                    reviewModel.getPlate().setCondition(s);
                    if (Objects.equals(s, Constants.IS_GOOD))
                        reviewModel.getPlate().setPlateIfBadConditions(null);
                    else if (Objects.equals(s, Constants.IS_BAD))
                        reviewModel.getPlate().setPlateIfBadConditions(badConditions);
                },
                true, 7, badConditions::getDescription, badConditions::setDescription);
        plateDoubleButton = new DoubleButtonDataModel(context.getString(R.string.ok), context.getString(R.string.adding), plate.onClickListener(),
                v -> addPlateItems(reviewModelHelper.getPlate().getPlateIfBadConditions().getPlateIfBadCondition().size()), () -> "", (s) -> {
        });
        plateFieldsList.clear();
        plateFieldsList.add(plateDoubleButton);
        int j = badConditions.getPlateIfBadCondition().size();
        for (int i = 0; i < j; i++) {
            addPlateItems(i);
        }
    }

    private void khareZedeSooudFieldsFilling() {
        ProjectModel.ThornBadConditions badConditions = reviewModelHelper.getThorn().getThornBadConditions();
        khareZedeSooud = new GoodBadDataModel(context.getString(R.string.khare_zede_sooud), khareZedeSooudFieldsList, adapter,
                () -> reviewModelHelper.getThorn().getCondition(),
                (s) -> {
                    reviewModelHelper.getThorn().setCondition(s);
                    reviewModel.getThorn().setCondition(s);
                    if (Objects.equals(s, Constants.IS_BAD))
                        reviewModel.getThorn().setThornBadConditions(badConditions);
                    else if (Objects.equals(s, Constants.IS_GOOD))
                        reviewModel.getThorn().setThornBadConditions(null);
                },
                badConditions::getDescription,
                badConditions::setDescription);
        NumberEditViewDataModel kasriKhareZedeSooud = new NumberEditViewDataModel(context.getString(R.string.kasri),
                badConditions::getAmount,
                badConditions::setAmount);
        NumberEditViewDataModel zangZadegiKhareZedeSooud = new NumberEditViewDataModel(context.getString(R.string.zang_zadegi),
                badConditions::getRustiness,
                badConditions::setRustiness);
        GeneralDataModel[] khareZedeSooudFields = {kasriKhareZedeSooud, zangZadegiKhareZedeSooud};
        khareZedeSooudFieldsList.clear();
        khareZedeSooudFieldsList.addAll(Arrays.asList(khareZedeSooudFields));
    }

    private void pichePeleFieldsFilling() {
        ProjectModel.StairBoltsBadConditions badConditions = reviewModelHelper.getStairBolts().getStairBoltsBadConditions();
        pichePele = new GoodBadDataModel(context.getString(R.string.piche_pele), pichePeleFieldsList, adapter,
                () -> reviewModelHelper.getStairBolts().getCondition(),
                (s) -> {
                    reviewModelHelper.getStairBolts().setCondition(s);
                    reviewModel.getStairBolts().setCondition(s);
                    if (Objects.equals(s, Constants.IS_GOOD))
                        reviewModel.getStairBolts().setStairBoltsBadConditions(null);
                    else if (Objects.equals(s, Constants.IS_BAD))
                        reviewModel.getStairBolts().setStairBoltsBadConditions(badConditions);
                },
                badConditions::getDescription,
                badConditions::setDescription);
        NumberEditViewDataModel kasriPichePele = new NumberEditViewDataModel(context.getString(R.string.kasri),
                badConditions::getAmount,
                badConditions::setAmount);
        NumberEditViewDataModel zangZadegiPichePele = new NumberEditViewDataModel(context.getString(R.string.zang_zadegi),
                badConditions::getRustiness,
                badConditions::setRustiness);
        NumberEditViewDataModel niazBeTamirPichePele = new NumberEditViewDataModel(context.getString(R.string.niaz_be_tamir),
                badConditions::getNiazeBeTamir,
                badConditions::setNiazeBeTamir);
        GeneralDataModel[] pichePeleFields = {kasriPichePele, zangZadegiPichePele, niazBeTamirPichePele};
        pichePeleFieldsList.clear();
        pichePeleFieldsList.addAll(Arrays.asList(pichePeleFields));
    }

    private void pichMohreFieldsFilling() {
        ProjectModel.BoltsAndNutsConditionsIfBad badConditions = reviewModelHelper.getBoltsAndNuts().getBoltsAndNutsConditionsIfBad();
        if (badConditions.getPichMohreItemList().isEmpty())
            badConditions.getPichMohreItemList().add(new ProjectModel.PichMohreItem(null, null, null));
        pichMohre = new GoodBadDataModel(context.getString(R.string.pich_mohre), pichMohreFieldsList, adapter,
                () -> reviewModelHelper.getBoltsAndNuts().getCondition(),
                (s) -> {
                    reviewModelHelper.getBoltsAndNuts().setCondition(s);
                    reviewModel.getBoltsAndNuts().setCondition(s);
                    if (Objects.equals(s, Constants.IS_BAD))
                        reviewModel.getBoltsAndNuts().setBoltsAndNutsConditionsIfBad(badConditions);
                    else if (Objects.equals(s, Constants.IS_GOOD))
                        reviewModel.getBoltsAndNuts().setBoltsAndNutsConditionsIfBad(null);
                },
                true, 4,
                badConditions::getDescription,
                badConditions::setDescription);
        pichMohreDoubleButton = new DoubleButtonDataModel(context.getString(R.string.ok), context.getString(R.string.adding), pichMohre.onClickListener(), onAddingPichMohreItemClicked(), () -> "", (s) -> {
        });
        pichMohreFieldsList.clear();
        pichMohreFieldsList.add(pichMohreDoubleButton);
        int j = badConditions.getPichMohreItemList().size();
        for (int i = 0; i < j; i++) {
            addPichMohreItems(i);
        }


    }

    private void tabloFieldsFilling() {
        ProjectModel.PanelBadConditions badConditions = reviewModelHelper.getPanel().getPanelBadConditions();
        SegmentedControlDataModel numberBoard = new SegmentedControlDataModel(context.getString(R.string.without_board), context.getString(R.string.board_fading_out), context.getString(R.string.board_cleaning), context.getString(R.string.rusty), context.getString(R.string.to_be_bolt_open), context.getString(R.string.number_board),
                () -> badConditions.getPanelNumber().getCondition(),
                (s) -> {
                    badConditions.getPanelNumber().setCondition(s);
                    reviewModel.getPanel().getPanelBadConditions().getPanelNumber().setCondition(s);
                });
        SegmentedControlDataModel tabloHavaee = new SegmentedControlDataModel(context.getString(R.string.without_board), context.getString(R.string.board_fading_out), context.getString(R.string.board_cleaning), context.getString(R.string.rusty), context.getString(R.string.to_be_bolt_open), context.getString(R.string.tablo_havaee),
                () -> badConditions.getPanelHavayee().getCondition(),
                (s) -> {
                    badConditions.getPanelHavayee().setCondition(s);
                    reviewModel.getPanel().getPanelBadConditions().getPanelHavayee().setCondition(s);
                });
        SegmentedControlDataModel tabloArayeshPhasi = new SegmentedControlDataModel(context.getString(R.string.without_board), context.getString(R.string.board_fading_out), context.getString(R.string.board_cleaning), context.getString(R.string.rusty), context.getString(R.string.to_be_bolt_open), context.getString(R.string.tablo_arayesh_phasi),
                () -> badConditions.getPhasePanel().getCondition(),
                (s) -> {
                    badConditions.getPhasePanel().setCondition(s);
                    reviewModel.getPanel().getPanelBadConditions().getPhasePanel().setCondition(s);
                });
        SegmentedControlDataModel tabloHarim = new SegmentedControlDataModel(context.getString(R.string.without_board), context.getString(R.string.board_fading_out), context.getString(R.string.board_cleaning), context.getString(R.string.rusty), context.getString(R.string.to_be_bolt_open), context.getString(R.string.tablo_harim),
                () -> badConditions.getPanelDomain().getCondition(),
                (s) -> {
                    badConditions.getPanelDomain().setCondition(s);
                    reviewModel.getPanel().getPanelBadConditions().getPanelDomain().setCondition(s);
                });
        CheckBoxDataModel numberBoardCheckBox = new CheckBoxDataModel(context.getString(R.string.number_board), adapter, tabloFiledsList, numberBoard,
                badConditions::getPanelNumberCondition,
                s1 -> {
                    badConditions.setPanelNumberCondition(s1);
                    reviewModel.getPanel().getPanelBadConditions().setPanelNumberCondition(s1);
                    if (Objects.equals(s1, Constants.NO_CHECKED))
                        reviewModel.getPanel().getPanelBadConditions().setPanelNumber(null);
                    else if (Objects.equals(s1, Constants.IS_CHECKED))
                        reviewModel.getPanel().getPanelBadConditions().setPanelNumber(badConditions.getPanelNumber());
                });
        CheckBoxDataModel tabloHavaeeCheckBox = new CheckBoxDataModel(context.getString(R.string.tablo_havaee), adapter, tabloFiledsList, tabloHavaee,
                badConditions::getPanelHavayeeCondition,
                s1 -> {
                    badConditions.setPanelHavayeeCondition(s1);
                    reviewModel.getPanel().getPanelBadConditions().setPanelHavayeeCondition(s1);
                    if (Objects.equals(s1, Constants.NO_CHECKED))
                        reviewModel.getPanel().getPanelBadConditions().setPanelHavayee(null);
                    else if (Objects.equals(s1, Constants.IS_CHECKED))
                        reviewModel.getPanel().getPanelBadConditions().setPanelHavayee(badConditions.getPanelHavayee());
                });
        CheckBoxDataModel tabloArayeshPhasiCheckBox = new CheckBoxDataModel(context.getString(R.string.tablo_arayesh_phasi), adapter, tabloFiledsList, tabloArayeshPhasi,
                badConditions::getPhasePanelCondition,
                s1 -> {
                    badConditions.setPhasePanelCondition(s1);
                    reviewModel.getPanel().getPanelBadConditions().setPhasePanelCondition(s1);
                    if (Objects.equals(s1, Constants.NO_CHECKED))
                        reviewModel.getPanel().getPanelBadConditions().setPhasePanel(null);
                    else if (Objects.equals(s1, Constants.IS_CHECKED))
                        reviewModel.getPanel().getPanelBadConditions().setPhasePanel(badConditions.getPhasePanel());
                });
        CheckBoxDataModel tabloHarimCheckBox = new CheckBoxDataModel(context.getString(R.string.tablo_harim), adapter, tabloFiledsList, tabloHarim,
                badConditions::getPanelDomainCondition,
                s1 -> {
                    badConditions.setPanelDomainCondition(s1);
                    reviewModel.getPanel().getPanelBadConditions().setPanelDomainCondition(s1);
                    if (Objects.equals(s1, Constants.NO_CHECKED))
                        reviewModel.getPanel().getPanelBadConditions().setPanelDomain(null);
                    else if (Objects.equals(s1, Constants.IS_CHECKED))
                        reviewModel.getPanel().getPanelBadConditions().setPanelDomain(badConditions.getPanelDomain());
                });
        GeneralDataModel[] tabloElements = {numberBoardCheckBox, tabloHavaeeCheckBox, tabloArayeshPhasiCheckBox, tabloHarimCheckBox};
        tabloFiledsList.clear();
        tabloFiledsList.addAll(Arrays.asList(tabloElements));
        tablo = new GoodBadDataModel(context.getString(R.string.tablo), tabloFiledsList, adapter,
                () -> reviewModelHelper.getPanel().getCondition(),
                (s) -> {
                    reviewModel.getPanel().setCondition(s);
                    reviewModelHelper.getPanel().setCondition(s);
                    if (Objects.equals(s, Constants.IS_BAD)) {
                        ProjectModel.PanelHavayee panelHavayee = Objects.equals(badConditions.getPanelHavayeeCondition(), Constants.IS_CHECKED) ? badConditions.getPanelHavayee() : null;
                        ProjectModel.PanelDomain panelDomain = Objects.equals(badConditions.getPanelDomainCondition(), Constants.IS_CHECKED) ? badConditions.getPanelDomain() : null;
                        ProjectModel.PhasePanel phasePanel = Objects.equals(badConditions.getPhasePanelCondition(), Constants.IS_CHECKED) ? badConditions.getPhasePanel() : null;
                        ProjectModel.PanelNumber panelNumber = Objects.equals(badConditions.getPanelNumberCondition(), Constants.IS_CHECKED) ? badConditions.getPanelNumber() : null;
                        reviewModel.getPanel().setPanelBadConditions(new ProjectModel.PanelBadConditions(panelHavayee, panelNumber, badConditions.getDescription(),
                                phasePanel, panelDomain, badConditions.getPanelHavayeeCondition(), badConditions.getPanelNumberCondition(), badConditions.getPhasePanelCondition(), badConditions.getPanelDomainCondition()));
                    } else if (Objects.equals(s, Constants.IS_GOOD)) {
                        reviewModel.getPanel().setPanelBadConditions(null);
                    }
                },
                () -> reviewModelHelper.getPanel().getPanelBadConditions().getDescription(),
                (s) -> {
                    if (reviewModel.getPanel().getPanelBadConditions() != null) {
                        reviewModel.getPanel().getPanelBadConditions().setDescription(s);
                        reviewModelHelper.getPanel().getPanelBadConditions().setDescription(s);
                    }
                });
    }

    private void foundationFieldsFilling() {
        ProjectModel.FoundationIfBad badConditions = reviewModelHelper.getFoundation().getFoundationIfBad();
        NumberEditViewDataModel excavationFoundationField = new NumberEditViewDataModel(context.getString(R.string.khak_bardari),
                badConditions::getKhakBardari,
                badConditions::setKhakBardari, true);
        NumberEditViewDataModel khakRiziFoundationField = new NumberEditViewDataModel(context.getString(R.string.khak_rizi),
                badConditions::getKhakRizi,
                badConditions::setKhakRizi, true);
        NumberEditViewDataModel numberOfHarnessesFoundationField = new NumberEditViewDataModel(context.getString(R.string.number_of_harnesses),
                badConditions::getTedadeMahar,
                badConditions::setTedadeMahar);
        CustomEditTextDataModel otherFoundationField = new CustomEditTextDataModel(context.getString(R.string.others),
                badConditions::getOthers
                , badConditions::setOthers);
        SegmentedControlDataModel concreteDestructionFoundationField = createFiveSegmentedDataModel(context.getString(R.string.concrete_destaruction),
                badConditions::getConcreteDamage,
                badConditions::setConcreteDamage);
        SegmentedControlDataModel horizontalCrackFoundationField = createFiveSegmentedDataModel(context.getString(R.string.horizental_crack),
                badConditions::getHorizontalCrack,
                badConditions::setHorizontalCrack);
        SegmentedControlDataModel concreteCorrosionFoundationField = createFiveSegmentedDataModel(context.getString(R.string.concrete_corrosion),
                badConditions::getConcreteEat,
                badConditions::setConcreteEat);
        SegmentedControlDataModel verticalCrackFoundationField = createFiveSegmentedDataModel(context.getString(R.string.vertical_crack),
                badConditions::getVerticalCrack,
                badConditions::setVerticalCrack);
        SegmentedControlDataModel needToFloodNetFoundationField = createFiveSegmentedDataModel(context.getString(R.string.need_to_flood_net),
                badConditions::getFloodNet,
                badConditions::setFloodNet);
        needToCleaningGrassesFoundationField = new CheckBoxDataModel(context.getString(R.string.need_to_cleaning_grasses), onNeedToCleaningGrassesCheckedChange(),
                badConditions::getGrassNeedsToBeCleaned,
                (s) -> {
                    if (reviewModel.getFoundation().getFoundationIfBad() != null) {
                        reviewModel.getFoundation().getFoundationIfBad().setGrassNeedsToBeCleaned(s);
                        badConditions.setGrassNeedsToBeCleaned(s);
                        if (Objects.equals(s, Constants.IS_CHECKED)) {
                            reviewModel.getFoundation().getFoundationIfBad().setIfGrassNeedsToBeCleaned(badConditions.getIfGrassNeedsToBeCleaned());
                        } else if (Objects.equals(s, Constants.NO_CHECKED))
                            reviewModel.getFoundation().getFoundationIfBad().setIfGrassNeedsToBeCleaned(null);
                    }
                });
        amountOfNeedToCleaningGrassesFoundationField = new SegmentedControlDataModel(context.getString(R.string.low), context.getString(R.string.medium), context.getString(R.string.high), context.getString(R.string.very_high), context.getString(R.string.amount_of_need_to_cleaning_grasses),
                () -> badConditions.getIfGrassNeedsToBeCleaned().getGrassCount(),
                (s) -> {
                    if (reviewModel.getFoundation().getFoundationIfBad() != null && reviewModel.getFoundation().getFoundationIfBad().getIfGrassNeedsToBeCleaned() != null) {
                        reviewModel.getFoundation().getFoundationIfBad().getIfGrassNeedsToBeCleaned().setGrassCount(s);
                        badConditions.getIfGrassNeedsToBeCleaned().setGrassCount(s);
                    }
                });
        GeneralDataModel[] foundationElements = {excavationFoundationField, khakRiziFoundationField, numberOfHarnessesFoundationField, otherFoundationField,
                concreteDestructionFoundationField, horizontalCrackFoundationField, verticalCrackFoundationField, concreteCorrosionFoundationField,
                needToFloodNetFoundationField, needToCleaningGrassesFoundationField};
        foundationElementList = new ArrayList<>();
        earthWireElementList = new ArrayList<>();

        foundation = new GoodBadDataModel(context.getString(R.string.foundation), foundationElementList, adapter,
                () -> reviewModelHelper.getFoundation().getCondition(), (String s) -> {
            reviewModel.getFoundation().setCondition(s);
            reviewModelHelper.getFoundation().setCondition(s);
            if (Objects.equals(s, Constants.IS_BAD) && reviewModel.getFoundation().getFoundationIfBad() == null)
                reviewModel.getFoundation().setFoundationIfBad(badConditions);
            else if (Objects.equals(s, Constants.IS_GOOD))
                reviewModel.getFoundation().setFoundationIfBad(null);
        },
                badConditions::getDescription,
                (s) -> {
                    if (reviewModel.getFoundation().getFoundationIfBad() != null) {
                        reviewModel.getFoundation().getFoundationIfBad().setDescription(s);
                        badConditions.setDescription(s);
                    }
                });
        foundationElementList.clear();
        foundationElementList.addAll(Arrays.asList(foundationElements));
        //numberOfHarnessesFoundationField.setItemFilled(true);
    }

    private void earthWireFieldsFilling() {
        ProjectModel.EarthWireBadConditions badConditions = reviewModelHelper.getEarthWire().getEarthWireBadConditions();
        CheckBoxDataModel stealingEarthWireField = new CheckBoxDataModel(context.getString(R.string.stealing), null,
                badConditions::getSerghtat,
                badConditions::setSerghtat);
        CheckBoxDataModel decayEarthWireField = new CheckBoxDataModel(context.getString(R.string.decay), null,
                badConditions::getPoosidegi,
                badConditions::setPoosidegi);
        CheckBoxDataModel toBeOutEarthWireField = new CheckBoxDataModel(context.getString(R.string.to_be_out), null,
                badConditions::getBiroonBoodaneSimeZamin,
                badConditions::setBiroonBoodaneSimeZamin);
        CheckBoxDataModel clampEarthWireField = new CheckBoxDataModel(context.getString(R.string.clamp), null,
                badConditions::getKolomp,
                badConditions::setKolomp);
        CheckBoxDataModel ruptureEarthWireField = new CheckBoxDataModel(context.getString(R.string.rupture), null,
                badConditions::getParegi,
                badConditions::setParegi);
        GeneralDataModel[] earthWireElements = {stealingEarthWireField, decayEarthWireField, toBeOutEarthWireField,
                clampEarthWireField, ruptureEarthWireField};
        earthWireElementList.clear();
        earthWireElementList.addAll(Arrays.asList(earthWireElements));
        earthWire = new GoodBadDataModel(context.getString(R.string.earth_wire), earthWireElementList, adapter,
                () -> reviewModelHelper.getEarthWire().getHealthCondition(),
                (s) -> {
                    reviewModelHelper.getEarthWire().setHealthCondition(s);
                    reviewModel.getEarthWire().setHealthCondition(s);
                    if (Objects.equals(s, Constants.IS_GOOD)) {
                        reviewModel.getEarthWire().setEarthWireBadConditions(null);
                    } else if (Objects.equals(s, Constants.IS_BAD)) {
                        reviewModel.getEarthWire().setEarthWireBadConditions(badConditions);
                    }
                },
                badConditions::getOthers,
                badConditions::setOthers);
    }


    private SegmentedControlDataModel createFiveSegmentedDataModel(String title, TextGetter getter, TextSetter setter) {
        return new SegmentedControlDataModel(context.getString(R.string.dose_not_have), context.getString(R.string.low),
                context.getString(R.string.medium), context.getString(R.string.high), context.getString(R.string.very_high), title, getter, setter);
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
        return context.getString(R.string.review_second_level_hint);
    }

    //for parcel

    private ReviewSecondLevelVM(Parcel in) {
        super(in);
        reviewModel = mainActivityVM.getReviewModel();
        reviewModelHelper = mainActivityVM.getReviewModelHelper();
    }

    public static Creator<ReviewSecondLevelVM> CREATOR = new Creator<ReviewSecondLevelVM>() {
        @Override
        public ReviewSecondLevelVM createFromParcel(Parcel source) {
            return new ReviewSecondLevelVM(source);
        }

        @Override
        public ReviewSecondLevelVM[] newArray(int size) {
            return new ReviewSecondLevelVM[size];
        }
    };
}

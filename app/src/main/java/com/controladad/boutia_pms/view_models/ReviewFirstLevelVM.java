package com.controladad.boutia_pms.view_models;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import androidx.databinding.DataBindingUtil;
import android.os.Parcel;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import com.controladad.boutia_pms.R;
import com.controladad.boutia_pms.adapters.ReviewFirstLevelAdapter;
import com.controladad.boutia_pms.adapters.StatefulRecyclerView;
import com.controladad.boutia_pms.databinding.FragmentReviewFirstLevelBinding;
import com.controladad.boutia_pms.location.LocationService;
import com.controladad.boutia_pms.models.data_models.ProjectModel;
import com.controladad.boutia_pms.utility.Constants;
import com.controladad.boutia_pms.view_models.items_view_models.CustomEditTextDataModel;
import com.controladad.boutia_pms.view_models.items_view_models.CustomTextViewDataModel;
import com.controladad.boutia_pms.view_models.items_view_models.GeneralDataModel;
import com.controladad.boutia_pms.view_models.items_view_models.NumberEditViewDataModel;
import com.controladad.boutia_pms.view_models.items_view_models.SegmentedControlDataModel;
import com.controladad.boutia_pms.view_models.items_view_models.SingleButtonDataModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import lombok.Getter;

import static com.controladad.boutia_pms.utility.Constants.MANUAL;

public class ReviewFirstLevelVM extends GeneralVM {

    @Getter
    private ReviewFirstLevelAdapter adapter = new ReviewFirstLevelAdapter();
    private StatefulRecyclerView recyclerView;
    private ProjectModel.ReviewModel reviewModel;
    private ProjectModel.ReviewModel reviewModelHelper;

    private SegmentedControlDataModel towerTipSegmentedControlDataModel;
    private SegmentedControlDataModel cementBeamTypeSegmentedControlDataModel;

    private List<GeneralDataModel> itemModelList = new ArrayList<>();
    private List<CustomTextViewDataModel> dispatchingCustomTextViewDataModelList = new ArrayList<>();
    private CustomTextViewDataModel towerNumberCustomEditTextDataModel;
    private CustomEditTextDataModel towerNameCustomEditTextDataModel;
    private SegmentedControlDataModel towerTypeSegmentedControlDataModel;
    private String role;

    public ReviewFirstLevelVM(String role) {
        super();
        this.role = role;
        this.reviewModel = mainActivityVM.getReviewModel();
        reviewModelHelper = mainActivityVM.getReviewModelHelper();
        fieldsFilling();
    }

    private void fieldsFilling() {
        CustomTextViewDataModel dateCustomTextViewDataModel = new CustomTextViewDataModel(context.getString(R.string.mission_date), () -> reviewModel.getCheckupDate().getDate(), (s) -> {
            reviewModel.getCheckupDate().setDate(s);
            reviewModelHelper.getCheckupDate().setDate(s);
        }, R.drawable.ic_mission_calender);
        CustomTextViewDataModel reviewTypeCustomTextViewDataModel = new CustomTextViewDataModel(context.getString(R.string.review_type), () -> reviewModel.getCheckupType().getType(), (s) -> {
            reviewModel.getCheckupType().setType(s);
            reviewModelHelper.getCheckupType().setType(s);
        }, R.drawable.ic_control_type);
        CustomTextViewDataModel voltageCustomTextViewDataModel = new CustomTextViewDataModel(context.getString(R.string.Voltage), () -> reviewModel.getVoltage().getVoltage(), (s) -> {
            reviewModel.getVoltage().setVoltage(s);
            reviewModelHelper.getVoltage().setVoltage(s);
        }, R.drawable.ic_voltage_control);
        CustomTextViewDataModel lineNameCustomTextViewDataModel = new CustomTextViewDataModel(context.getString(R.string.mission_name), () -> reviewModel.getMissionName().getName(), (s) -> {
            reviewModel.getMissionName().setName(s);
            reviewModelHelper.getMissionName().setName(s);
        }, R.drawable.ic_electrical_tower);
        towerTipSegmentedControlDataModel = new SegmentedControlDataModel(context.getString(R.string.cement_beam), context.getString(R.string.telescopic), context.getString(R.string.lattice), context.getString(R.string.tower_tip), () -> reviewModel.getElectricTowerType().getType(), (s) -> {
            reviewModel.getElectricTowerType().setType(s);
            reviewModelHelper.getElectricTowerType().setType(s);
            if (Objects.equals(s, context.getString(R.string.cement_beam)))
                reviewModel.getElectricTowerType().setCementElectricTowerType(reviewModelHelper.getElectricTowerType().getCementElectricTowerType());
            else reviewModel.getElectricTowerType().setCementElectricTowerType(null);
        });
        towerTypeSegmentedControlDataModel = new SegmentedControlDataModel(context.getString(R.string.angel), context.getString(R.string.pendant), context.getString(R.string.tower_type), () -> reviewModel.getNoeDakal().getType(), (s) -> {
            reviewModel.getNoeDakal().setType(s);
            reviewModelHelper.getNoeDakal().setType(s);
        });
        towerNameCustomEditTextDataModel = new CustomEditTextDataModel(context.getString(R.string.tower_name), () -> reviewModel.getTowerName(), (s) -> {
            if (s != null)
                s = s.toUpperCase();
            reviewModel.setTowerName(s);
            reviewModelHelper.setTowerName(s);
        });
        towerNumberCustomEditTextDataModel = new CustomTextViewDataModel(context.getString(R.string.tower_number), () -> reviewModel.getElectricTowerNumber().getNumber(), (s) -> {
            reviewModel.getElectricTowerNumber().setNumber(s);
            reviewModelHelper.getElectricTowerNumber().setNumber(s);
        }, R.drawable.ic_position);
        NumberEditViewDataModel circuitNumberCustomEditTextDataModel = new NumberEditViewDataModel(context.getString(R.string.circuit_number), () -> reviewModel.getCircuitCount().getCount(), (s) -> {
            reviewModel.getCircuitCount().setCount(toEnglishDigit(s));
            reviewModelHelper.getCircuitCount().setCount(toEnglishDigit(s));
            addDispatchingCodeFields(s);
        });
        GeneralDataModel[] dataModels = {dateCustomTextViewDataModel, reviewTypeCustomTextViewDataModel
                , voltageCustomTextViewDataModel, lineNameCustomTextViewDataModel, towerTipSegmentedControlDataModel, towerTypeSegmentedControlDataModel
                , towerNameCustomEditTextDataModel, towerNumberCustomEditTextDataModel, circuitNumberCustomEditTextDataModel};
        SingleButtonDataModel button = new SingleButtonDataModel(context.getString(R.string.ok_and_continue), onClickListener(), () -> "", (s) -> {
        });
        cementBeamTypeSegmentedControlDataModel = new SegmentedControlDataModel("H", "F", context.getString(R.string.beam_type), () -> {
            if (reviewModel.getElectricTowerType().getCementElectricTowerType() != null)
                return reviewModel.getElectricTowerType().getCementElectricTowerType().getCementElectricTowerType();
            return null;
        }, (s) -> {
            if (reviewModel.getElectricTowerType().getCementElectricTowerType() != null)
                reviewModel.getElectricTowerType().getCementElectricTowerType().setCementElectricTowerType(s);
            reviewModelHelper.getElectricTowerType().getCementElectricTowerType().setCementElectricTowerType(s);
        });
        itemModelList.clear();
        itemModelList.addAll(Arrays.asList(dataModels));
        // itemModelList.add(new ImagesRecyclerViewDataModel());
        itemModelList.add(button);
        adapter.updateData(itemModelList);
        towerTipSegmentedControlDataModel.setOnCheckedChangeListener(onTowerTipCheckedChange);
        if (Objects.equals(context.getString(R.string.cement_beam), towerTipSegmentedControlDataModel.getSelectedItemText()))
            onTowerTipCheckedChange.onCheckedChanged(null, true);
        if (mainActivityVM.getBazdidMission() != null) {

            String dtStart = mainActivityVM.getBazdidMission().getStartDate();
            String dtEnd = mainActivityVM.getBazdidMission().getEndDate();
            /*SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            try {
                Date date = format.parse( mainActivityVM.getBazdidMission().getMOpStart());
                Date dateEnd = format.parse( mainActivityVM.getBazdidMission().getMOpEnd());

                PersianDate pdate = new PersianDate(date);
                //PersianDateFormat persianDateFormat = new PersianDateFormat("Y/m/d H:i:s");
                PersianDateFormat persianDateFormat = new PersianDateFormat("Y/m/d");
                dtStart =  persianDateFormat.format(pdate);

                PersianDate pdateEnd = new PersianDate(dateEnd);
                //PersianDateFormat persianDateFormatEnd = new PersianDateFormat("Y/m/d H:i:s");
                PersianDateFormat persianDateFormatEnd = new PersianDateFormat("Y/m/d");
                dtEnd =  persianDateFormatEnd.format(pdateEnd);

            } catch (ParseException e) {
                e.printStackTrace();
            }*/
            dateCustomTextViewDataModel.setStrDataToShow(dtStart + " الی " + dtEnd);
            reviewTypeCustomTextViewDataModel.setStrDataToShow(mainActivityVM.getBazdidMission().getMissionType());
            /*lineNameCustomTextViewDataModel.setStrDataToShow(mainActivityVM.getBazdidMission().getMissionLine().getMissionName());
            voltageCustomTextViewDataModel.setStrDataToShow(mainActivityVM.getBazdidMission().getMissionLine().getLineVoltage());*/
        }
    }

    private void addDispatchingCodeFields(String s) {
        int numberOfCircles;
        try {
            numberOfCircles = Integer.valueOf(s);
        } catch (NumberFormatException e) {
            numberOfCircles = 0;
        }
        if (mainActivityVM.getBazdidMission() == null || !Objects.equals(mainActivityVM.getBazdidMission().getMissionType(), "تحویل و تحول")) {

            if (dispatchingCustomTextViewDataModelList.size() != 0) {
                int position = adapter.getItemsModelList().indexOf(dispatchingCustomTextViewDataModelList.get(0));
                for (CustomTextViewDataModel customTextViewDataModel : dispatchingCustomTextViewDataModelList) {
                    adapter.getItemsModelList().remove(customTextViewDataModel);
                }

                try {
                    adapter.notifyItemRangeRemoved(position, dispatchingCustomTextViewDataModelList.size());
                } catch (IllegalStateException e) {
                    adapter.setHasItemsChanged(true);
                }
                dispatchingCustomTextViewDataModelList.clear();
            }
            reviewModel.getBarCodeList().clear();
            for (int i = 0; i < numberOfCircles; i++) {
                if (reviewModelHelper.getBarCodeList().size() <= i) {
                    reviewModelHelper.getBarCodeList().add(i, new ProjectModel.BarCode(null, null));
                }
                reviewModel.getBarCodeList().add(i, reviewModelHelper.getBarCodeList().get(i));
                final int j = i;
                QRScannerVM qrScannerVM = new QRScannerVM();
                CustomTextViewDataModel customTextViewDataModel = new CustomTextViewDataModel(context.getString(R.string.dispatching) + " " + "مدار" + " " + String.valueOf(i + 1),
                        () -> {
                            String barCode = reviewModelHelper.getBarCodeList().get(j).getNumber();
                            if (barCode != null) {
                                return barCode.replaceAll(" ", "").substring(1, 6);
                            }

                            return null;
                        },
                        (s1) -> {
                            /*String dispatchingNumber = s1.replaceAll(" ", "").substring(1, 6);
                            reviewModelHelper.getBarCodeList().get(j).setNumber(dispatchingNumber);
                            reviewModel.getBarCodeList().get(j).setNumber(dispatchingNumber);*/
                        },
                        R.drawable.ic_dispaching_code,
                        v -> getRouter().navigateTo(Constants.QR_SCANNER_KEY, qrScannerVM)
                        , context.getString(R.string.bar_code_scan));
                qrScannerVM.setOnOkButtonClicked(v1 -> {
                    if (qrScannerVM.getBarCode() != null) {
                        String trimmedBarCode = qrScannerVM.getBarCodeTrimmed().toUpperCase();
                        String barCodeToSave = trimmedBarCode.substring(0, 3) + " "
                                + trimmedBarCode.substring(3, 6) + " " + trimmedBarCode.substring(6, 8) + " " + trimmedBarCode.substring(8);
                        reviewModelHelper.getBarCodeList().get(j).setNumber(barCodeToSave);
                        reviewModelHelper.getBarCodeList().get(j).setType(qrScannerVM.getScanType());
                        reviewModel.getBarCodeList().get(j).setType(qrScannerVM.getScanType());
                        reviewModel.getBarCodeList().get(j).setNumber(barCodeToSave);
                        towerNumberCustomEditTextDataModel.setStrDataToShow(
                                qrScannerVM.getBarCodeTrimmed().substring(trimmedBarCode.length() - 3)
                        );
                        getRouter().exit();
                    }
                });
                adapter.getItemsModelList().add(adapter.getItemsModelList().size() - 1, customTextViewDataModel);
                dispatchingCustomTextViewDataModelList.add(customTextViewDataModel);
                try {
                    adapter.notifyItemInserted(adapter.getItemsModelList().size() - 1);
                } catch (IllegalStateException e) {
                    adapter.setHasItemsChanged(true);
                }
            }
        } else if (Objects.equals(mainActivityVM.getBazdidMission().getMissionType(), "تحویل و تحول")) {
            reviewModel.getBarCodeList().clear();
            for (int i = 0; i < numberOfCircles; i++) {
                reviewModel.getBarCodeList().add(new ProjectModel.BarCode("", MANUAL));
            }
        }

    }

    private String toEnglishDigit(String strNum) {

        String[] pn = {"۰", "۱", "۲", "۳", "۴", "۵", "۶", "۷", "۸", "۹"};
        String[] en = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9"};
        String[] pe = {"٠", "١", "٢", "٣", "۴", "۵", "۶", "٧", "٨", "٩"};

        String englishDigit = strNum;
        for (int i = 0; i < 10; i++) {
            englishDigit = englishDigit.replaceAll(pn[i], en[i]);
            englishDigit = englishDigit.replaceAll(pe[i], en[i]);
        }
        return englishDigit;
    }

    @Getter
    private String vaziateTakhirText = "";
    @Getter
    private int lableColor;

    @Override
    public View binding(LayoutInflater inflater, @Nullable ViewGroup container) {
        FragmentReviewFirstLevelBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_review_first_level, container, false);
        binding.setReviewFirstLevelVM(this);
        recyclerView = binding.reviewFirstLevelRecyclerView;
        String vaziateTakhir = reviewModel.getVaziateTakhir();
        if (Objects.equals(vaziateTakhir, "delay")) {
            vaziateTakhirText = "ماموریت با تاخیر در حال انجام است";
            lableColor = ContextCompat.getColor(context,R.color.red_200);
        } else if (Objects.equals(vaziateTakhir, "hurry")) {
            vaziateTakhirText = "ماموریت زودتر از موعد در حال انجام است";
            lableColor = ContextCompat.getColor(context,R.color.green_200);
        } else {
            vaziateTakhirText = "ماموریت در زمان تعیین شده در حال انجام است";
            lableColor = ContextCompat.getColor(context,R.color.white);
        }
        return binding.getRoot();
    }

    @Override
    public void onCreateFragment() {
        super.onCreateFragment();
    }

    @Override
    public void onCreateView() {
        super.onCreateView();
    }


    @Override
    public int getLeftIconSource() {
        return R.drawable.ic_back;
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


    private CompoundButton.OnCheckedChangeListener onTowerTipCheckedChange = (buttonView, isChecked) -> {
        int position = adapter.getItemsModelList().indexOf(towerTipSegmentedControlDataModel);
        if (isChecked) {
            if (Objects.equals(towerTipSegmentedControlDataModel.getSelectedItemText(), context.getString(R.string.cement_beam)) &&
                    !adapter.getItemsModelList().contains(cementBeamTypeSegmentedControlDataModel)) {
                adapter.getItemsModelList().add(position + 1, cementBeamTypeSegmentedControlDataModel);
                try {
                    adapter.notifyItemInserted(position + 1);
                } catch (IllegalStateException e) {
                    adapter.setHasItemsChanged(true);
                }
                reviewModel.getElectricTowerType().setCementElectricTowerType(reviewModelHelper.getElectricTowerType().getCementElectricTowerType());
                if (adapter.getItemsModelList().contains(towerNameCustomEditTextDataModel)) {
                    int towerNamePosition = adapter.getItemsModelList().indexOf(towerNameCustomEditTextDataModel);
                    adapter.getItemsModelList().remove(towerNameCustomEditTextDataModel);
                    try {
                        adapter.notifyItemRemoved(towerNamePosition);
                    } catch (IllegalStateException e) {
                        adapter.setHasItemsChanged(true);
                    }
                    reviewModel.setTowerName(null);
                }
            } else if (!Objects.equals(towerTipSegmentedControlDataModel.getSelectedItemText(), context.getString(R.string.cement_beam)) &&
                    adapter.getItemsModelList().contains(cementBeamTypeSegmentedControlDataModel)) {
                adapter.getItemsModelList().remove(cementBeamTypeSegmentedControlDataModel);
                try {
                    adapter.notifyItemRemoved(position + 1);
                } catch (IllegalStateException e) {
                    adapter.setHasItemsChanged(true);
                }
                reviewModel.getElectricTowerType().setCementElectricTowerType(null);
                if (!adapter.getItemsModelList().contains(towerNameCustomEditTextDataModel)) {
                    int towerNamePosition = adapter.getItemsModelList().indexOf(towerTypeSegmentedControlDataModel);
                    adapter.getItemsModelList().add(towerNamePosition + 1, towerNameCustomEditTextDataModel);
                    try {
                        adapter.notifyItemRemoved(towerNamePosition);
                    } catch (IllegalStateException e) {
                        adapter.setHasItemsChanged(true);
                    }
                    reviewModel.setTowerName(reviewModelHelper.getTowerName());
                }
            }
        }
    };

    public View.OnClickListener onClickListener() {
        return v -> {
            for (GeneralDataModel dataModel : adapter.getItemsModelList()) {

                if (!dataModel.isItemFilled()) {
                    showSnackBar("لطفا فیلد " + dataModel.getItemName() + " را پر کنید.");
                    if (recyclerView.getLayoutManager() != null)
                        recyclerView.getLayoutManager().scrollToPosition(adapter.getItemsModelList().indexOf(dataModel));
                    return;
                }
            }

            if (adapter.getItemsModelList().contains(cementBeamTypeSegmentedControlDataModel) && !cementBeamTypeSegmentedControlDataModel.isItemFilled()) {
                showSnackBar("لطفا فیلد " + cementBeamTypeSegmentedControlDataModel.getItemName() + " را پر کنید.");
                if (recyclerView.getLayoutManager() != null)
                    recyclerView.getLayoutManager().scrollToPosition(adapter.getItemsModelList().indexOf(cementBeamTypeSegmentedControlDataModel));
                return;
            }

            if (Objects.equals(reviewModel.getCircuitCount().getCount(), "0")) {
                showSnackBar("لطفا تعداد مدار را درست وارد کنید");
                return;
            }

            for (CustomTextViewDataModel customTextViewDataModel : dispatchingCustomTextViewDataModelList) {
                if (customTextViewDataModel.getStrDataToShow() == null || customTextViewDataModel.getStrDataToShow().isEmpty()) {
                    showSnackBar("لطفا برای هر مدار بارکد مربوطه را اسکن نمایید");
                    return;
                }
            }
            if (!checkDispatchingCode()) {
                showSnackBar("کد دیسپاچینگ دکل با کد دیسپاچینگ ماموریت مطابقت ندارد.");
                return;
            }
            if (!Objects.equals("پرسنل حریم", role))
                getRouter().navigateTo(Constants.REVIEW_SECOND_LEVEL, new ReviewSecondLevelVM());
            else
                getRouter().navigateTo(Constants.REVIEW_FIFTH_LEVEL_SCREEN_KEY, new ReviewFifthLevelVM());
        };
    }

    private boolean checkDispatchingCode() {
        String dispatchingCodes = mainActivityVM.getBazdidMission().getDispatchingCode();
        List<ProjectModel.BarCode> barcodeList = mainActivityVM.getReviewModel().getBarCodeList();
        for (ProjectModel.BarCode barCode : barcodeList) {
            String barCodeNumber = barCode.getNumber();
            String dispatching = barCodeNumber.replaceAll(" ", "").toUpperCase().substring(1, 6);
            if (dispatchingCodes != null && dispatchingCodes.length() > 0 &&
                    !dispatchingCodes.toUpperCase().contains(dispatching))
                return false;
        }
        return true;
    }

    @Override
    String getPageHint() {
        return context.getString(R.string.review_first_level_hint);
    }

    // for parcel


    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(role);
    }

    public ReviewFirstLevelVM(Parcel in) {
        super(in);
        role = in.readString();
        reviewModel = mainActivityVM.getReviewModel();
        reviewModelHelper = mainActivityVM.getReviewModelHelper();
        fieldsFilling();
    }


    public static final Creator<ReviewFirstLevelVM> CREATOR = new Creator<ReviewFirstLevelVM>() {
        @Override
        public ReviewFirstLevelVM createFromParcel(Parcel source) {
            return new ReviewFirstLevelVM(source);
        }

        @Override
        public ReviewFirstLevelVM[] newArray(int size) {
            return new ReviewFirstLevelVM[size];
        }
    };
}

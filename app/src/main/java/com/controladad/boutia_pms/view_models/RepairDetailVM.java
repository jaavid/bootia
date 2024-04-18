package com.controladad.boutia_pms.view_models;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import androidx.databinding.Bindable;
import androidx.databinding.DataBindingUtil;
import android.os.Parcel;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.controladad.boutia_pms.BR;
import com.controladad.boutia_pms.R;
import com.controladad.boutia_pms.adapters.RepairDetailAdapter;
import com.controladad.boutia_pms.databinding.FragmentRepairDetailBinding;
import com.controladad.boutia_pms.location.LocationService;
import com.controladad.boutia_pms.models.SaveRepairToDBModel;
import com.controladad.boutia_pms.models.database.IradatDakalEntity;
import com.controladad.boutia_pms.models.database.TamirDakalEntity;
import com.controladad.boutia_pms.utility.Constants;
import com.controladad.boutia_pms.view_models.items_view_models.DoubleButtonDataModel;
import com.controladad.boutia_pms.view_models.items_view_models.GeneralDataModel;
import com.controladad.boutia_pms.view_models.items_view_models.RepairItemDataModel;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import lombok.Getter;

public class RepairDetailVM extends GeneralVM {

    private String scanType;
    private IradatDakalEntity iradatDakalEntity;
    private String elateTakhir;
    private String operationTime;
    @Bindable
    @Getter
    private String time = "0";

    public void setTime(String time) {
        this.time = time;
        notifyPropertyChanged(BR.time);
    }

    @Bindable
    @Getter
    private int coverVisibility = View.VISIBLE;

    public void setCoverVisibility(int coverVisibility) {
        this.coverVisibility = coverVisibility;
        notifyPropertyChanged(BR.coverVisibility);
    }

    @Getter
    private RepairDetailAdapter adapter = new RepairDetailAdapter();

    private List<GeneralDataModel> repairItemDataModelList;
    private List<GeneralDataModel> empetyRepairItemDataModelList;
    private TamirDakalEntity tamirDakalEntity = new TamirDakalEntity();
    private Long startTime = 0L;
    private Long endTime = 0L;

    @Override
    public View binding(LayoutInflater inflater, @Nullable ViewGroup container) {
        FragmentRepairDetailBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_repair_detail, container, false);
        binding.setRepairDetailVM(this);
        return binding.getRoot();

    }

    public View.OnClickListener onStartTimeClicked() {
        return v -> {
            if (startTime == 0) {
                setCoverVisibility(View.GONE);
                startTime = Calendar.getInstance().getTimeInMillis();
                startTimer();
            } else {
                showSnackBar("زمان بندی شما شروع شده است");
            }
        };
    }

    private Disposable timerDisposable;

    private void startTimer() {
        timerDisposable = Observable.interval((1000 - (Calendar.getInstance().getTimeInMillis() - startTime) % 1000) / 1000,
                1000, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(t -> {
                    Long thisTime = Calendar.getInstance().getTimeInMillis();
                    long time = (thisTime - startTime) / 1000;
                    setTime(time / 60 + ":" + time % 60);
                });

    }

    private void endTimer() {
        if (startTime != 0) {
            if (timerDisposable != null) {
                timerDisposable.dispose();
            }
        } else {
            showSnackBar("ابتدا باید زمانبندی را شروع کنید");
        }
    }

    public View.OnClickListener onEndTimeClicked() {
        return v -> {
            endTime = Calendar.getInstance().getTimeInMillis();
            endTimer();
        };
    }

    public View.OnClickListener onCoverClicked() {
        return v -> {
            showSnackBar("ابتدا روی شروع زمان ماموریت کلیک کنید.");
        };
    }

    @Override
    public void onCreateView() {
        super.onCreateView();
        addingDataToCards(iradatDakalEntity);
        repairItemDataModelList.addAll(empetyRepairItemDataModelList);
        adapter.updateData(repairItemDataModelList);
        if (iradatDakalEntity != null)
            showSnackBar(iradatDakalEntity.getBarcode());
        else
            showSnackBar("null : " + tamirDakalEntity.getBarcode());
        if (startTime != 0)
            setCoverVisibility(View.GONE);
    }

    public RepairDetailVM(IradatDakalEntity iradatDakalEntity, String elateTakhir, String operationTime, String scanType, String mid, String barCode) {
        this.iradatDakalEntity = iradatDakalEntity;
        tamirDakalEntity.setMId(Integer.valueOf(mid));
        tamirDakalEntity.setBarcode(barCode);
        String trimedBarcode = tamirDakalEntity.getBarcode();
        trimedBarcode = trimedBarcode.replace(" ", "");
        tamirDakalEntity.setDispatching_number(trimedBarcode.substring(1, 6));
        tamirDakalEntity.setDakalNumber(trimedBarcode.substring(trimedBarcode.length() - 3));
        tamirDakalEntity.setMScanType(scanType);
        this.elateTakhir = elateTakhir;
        this.operationTime = operationTime;
        this.scanType = scanType;
    }

    private void addingDataToCards(IradatDakalEntity iradatDakalEntity) {
        repairItemDataModelList = new ArrayList<>();
        empetyRepairItemDataModelList = new ArrayList<>();


        if (iradatDakalEntity != null && iradatDakalEntity.getFoundation() != null &&
                iradatDakalEntity.getFoundation().trim().length() > 0)
            repairItemDataModelList.add(new RepairItemDataModel("فونداسیون", iradatDakalEntity.getFoundation(), false,
                    s -> tamirDakalEntity.setFoundationRepairedDec(s),
                    null,
                    (checkBox, isChecked) -> {
                        tamirDakalEntity.setFoundationRepaired(isChecked);
                    }));
        else
            empetyRepairItemDataModelList.add(new RepairItemDataModel("فونداسیون", " ", false,
                    s -> tamirDakalEntity.setFoundationRepairedDec(s),
                    null,
                    (checkBox, isChecked) -> {
                        tamirDakalEntity.setFoundationRepaired(isChecked);
                    }));


        if (iradatDakalEntity != null && iradatDakalEntity.getTablo() != null &&
                iradatDakalEntity.getTablo().trim().length() > 0)
            repairItemDataModelList.add(new RepairItemDataModel("تابلو", iradatDakalEntity.getTablo(), false,
                    s -> tamirDakalEntity.setTabloRepairedDec(s),
                    null,
                    (checkBox, isChecked) -> {
                        tamirDakalEntity.setTabloRepaired(isChecked);
                    }));
        else
            empetyRepairItemDataModelList.add(new RepairItemDataModel("تابلو", " ", false,
                    s -> tamirDakalEntity.setTabloRepairedDec(s),
                    null,
                    (checkBox, isChecked) -> {
                        tamirDakalEntity.setTabloRepaired(isChecked);
                    }));


        if (iradatDakalEntity != null && iradatDakalEntity.getSimzamin() != null &&
                iradatDakalEntity.getSimzamin().trim().length() > 0)
            repairItemDataModelList.add(new RepairItemDataModel("سیم زمین", iradatDakalEntity.getSimzamin(), false,
                    s -> tamirDakalEntity.setSimzaminRepairedDec(s),
                    null,
                    (checkBox, isChecked) -> {
                        tamirDakalEntity.setSimzaminRepaired(isChecked);
                    }));
        else
            empetyRepairItemDataModelList.add(new RepairItemDataModel("سیم زمین", " ", false,
                    s -> tamirDakalEntity.setSimzaminRepairedDec(s),
                    null,
                    (checkBox, isChecked) -> {
                        tamirDakalEntity.setSimzaminRepaired(isChecked);
                    }));


        if (iradatDakalEntity != null && iradatDakalEntity.getPich() != null &&
                iradatDakalEntity.getPich().trim().length() > 0)
            repairItemDataModelList.add(new RepairItemDataModel("پیچ و مهره", iradatDakalEntity.getPich(), false,
                    s -> tamirDakalEntity.setPichRepairedDec(s),
                    null,
                    (checkBox, isChecked) -> {
                        tamirDakalEntity.setPichRepaired(isChecked);
                    }));
        else
            empetyRepairItemDataModelList.add(new RepairItemDataModel("پیچ و مهره", " ", false,
                    s -> tamirDakalEntity.setPichRepairedDec(s),
                    null,
                    (checkBox, isChecked) -> {
                        tamirDakalEntity.setPichRepaired(isChecked);
                    }));


        if (iradatDakalEntity != null && iradatDakalEntity.getKhar() != null &&
                iradatDakalEntity.getKhar().trim().length() > 0)
            repairItemDataModelList.add(new RepairItemDataModel("خار ضد صعود", iradatDakalEntity.getKhar(), false,
                    s -> tamirDakalEntity.setKharRepairedDec(s),
                    null,
                    (checkBox, isChecked) -> {
                        tamirDakalEntity.setKharRepaired(isChecked);
                    }));
        else
            empetyRepairItemDataModelList.add(new RepairItemDataModel("خار ضد صعود", " ", false,
                    s -> tamirDakalEntity.setKharRepairedDec(s),
                    null,
                    (checkBox, isChecked) -> {
                        tamirDakalEntity.setKharRepaired(isChecked);
                    }));


        if (iradatDakalEntity != null && iradatDakalEntity.getPlate() != null &&
                iradatDakalEntity.getPlate().trim().length() > 0)
            repairItemDataModelList.add(new RepairItemDataModel("پلیت", iradatDakalEntity.getPlate(), false,
                    s -> tamirDakalEntity.setPlateRepairedDec(s),
                    null,
                    (checkBox, isChecked) -> {
                        tamirDakalEntity.setPlateRepaired(isChecked);
                    }));
        else
            empetyRepairItemDataModelList.add(new RepairItemDataModel("پلیت", " ", false,
                    s -> tamirDakalEntity.setPlateRepairedDec(s),
                    null,
                    (checkBox, isChecked) -> {
                        tamirDakalEntity.setPlateRepaired(isChecked);
                    }));


        if (iradatDakalEntity != null && iradatDakalEntity.getNabshi() != null &&
                iradatDakalEntity.getNabshi().trim().length() > 0)
            repairItemDataModelList.add(new RepairItemDataModel("نبشی", iradatDakalEntity.getNabshi(), false,
                    s -> tamirDakalEntity.setNabshiRepairedDec(s),
                    null,
                    (checkBox, isChecked) -> {
                        tamirDakalEntity.setNabshiRepaired(isChecked);
                    }));
        else
            empetyRepairItemDataModelList.add(new RepairItemDataModel("نبشی", " ", false,
                    s -> tamirDakalEntity.setNabshiRepairedDec(s),
                    null,
                    (checkBox, isChecked) -> {
                        tamirDakalEntity.setNabshiRepaired(isChecked);
                    }));


        if (iradatDakalEntity != null && iradatDakalEntity.getSeil() != null &&
                iradatDakalEntity.getSeil().trim().length() > 0)
            repairItemDataModelList.add(new RepairItemDataModel("سیل", iradatDakalEntity.getSeil(), false,
                    s -> tamirDakalEntity.setSeilRepairedDec(s),
                    null,
                    (checkBox, isChecked) -> {
                        tamirDakalEntity.setSeilRepaired(isChecked);
                    }));
        else
            empetyRepairItemDataModelList.add(new RepairItemDataModel("سیل", " ", false,
                    s -> tamirDakalEntity.setSeilRepairedDec(s),
                    null,
                    (checkBox, isChecked) -> {
                        tamirDakalEntity.setSeilRepaired(isChecked);
                    }));


        if (iradatDakalEntity != null && iradatDakalEntity.getHadi() != null &&
                iradatDakalEntity.getHadi().trim().length() > 0)
            repairItemDataModelList.add(new RepairItemDataModel("هادی های فاز", iradatDakalEntity.getHadi(), false,
                    s -> tamirDakalEntity.setHadiRepairedDec(s),
                    null,
                    (checkBox, isChecked) -> {
                        tamirDakalEntity.setHadiRepaired(isChecked);
                    }));
        else
            empetyRepairItemDataModelList.add(new RepairItemDataModel("هادی های فاز", " ", false,
                    s -> tamirDakalEntity.setHadiRepairedDec(s),
                    null,
                    (checkBox, isChecked) -> {
                        tamirDakalEntity.setHadiRepaired(isChecked);
                    }));


        if (iradatDakalEntity != null && iradatDakalEntity.getYaragh() != null &&
                iradatDakalEntity.getYaragh().trim().length() > 0)
            repairItemDataModelList.add(new RepairItemDataModel("یراق", iradatDakalEntity.getYaragh(), false,
                    s -> tamirDakalEntity.setYaraghRepairedDec(s),
                    null,
                    (checkBox, isChecked) -> {
                        tamirDakalEntity.setYaraghRepaired(isChecked);
                    }));
        else
            empetyRepairItemDataModelList.add(new RepairItemDataModel("یراق", " ", false,
                    s -> tamirDakalEntity.setYaraghRepairedDec(s),
                    null,
                    (checkBox, isChecked) -> {
                        tamirDakalEntity.setYaraghRepaired(isChecked);
                    }));

        if (iradatDakalEntity != null && iradatDakalEntity.getPichPelle() != null &&
                iradatDakalEntity.getPichPelle().trim().length() > 0)
            repairItemDataModelList.add(new RepairItemDataModel("پیچ پله", iradatDakalEntity.getPichPelle(), false,
                    s -> tamirDakalEntity.setPichPelleRepairedDec(s),
                    null,
                    (checkBox, isChecked) -> {
                        tamirDakalEntity.setPichPelleRepaired(isChecked);
                    }));
        else
            empetyRepairItemDataModelList.add(new RepairItemDataModel("پیچ پله", " ", false,
                    s -> tamirDakalEntity.setPichPelleRepairedDec(s),
                    null,
                    (checkBox, isChecked) -> {
                        tamirDakalEntity.setPichPelleRepaired(isChecked);
                    }));


        if (iradatDakalEntity != null && iradatDakalEntity.getZmm() != null &&
                iradatDakalEntity.getZmm().trim().length() > 0)
            repairItemDataModelList.add(new RepairItemDataModel("زنجیره مقره و ملحقات", iradatDakalEntity.getZmm(), false,
                    s -> tamirDakalEntity.setZmmRepairedDec(s),
                    null,
                    (checkBox, isChecked) -> {
                        tamirDakalEntity.setZmmRepaired(isChecked);
                    }));
        else
            empetyRepairItemDataModelList.add(new RepairItemDataModel("زنجیره مقره و ملحقات", " ", false,
                    s -> tamirDakalEntity.setZmmRepairedDec(s),
                    null,
                    (checkBox, isChecked) -> {
                        tamirDakalEntity.setZmmRepaired(isChecked);
                    }));

        if (iradatDakalEntity != null && iradatDakalEntity.getMohafez() != null &&
                iradatDakalEntity.getMohafez().trim().length() > 0)
            repairItemDataModelList.add(new RepairItemDataModel("سیم محافظ", iradatDakalEntity.getMohafez(), false,
                    s -> tamirDakalEntity.setMohafezRepairedDec(s),
                    null,
                    (checkBox, isChecked) -> {
                        tamirDakalEntity.setMohafezRepaired(isChecked);
                    }));
        else
            empetyRepairItemDataModelList.add(new RepairItemDataModel("سیم محافظ", " ", false,
                    s -> tamirDakalEntity.setMohafezRepairedDec(s),
                    null,
                    (checkBox, isChecked) -> {
                        tamirDakalEntity.setMohafezRepaired(isChecked);
                    }));

        if (iradatDakalEntity != null && iradatDakalEntity.getEzafe() != null &&
                iradatDakalEntity.getEzafe().trim().length() > 0)
            repairItemDataModelList.add(new RepairItemDataModel("اشیاء اضافه", iradatDakalEntity.getEzafe(), false,
                    s -> tamirDakalEntity.setEzafeRepairedDec(s),
                    null,
                    (checkBox, isChecked) -> {
                        tamirDakalEntity.setEzafeRepaired(isChecked);
                    }));
        else
            empetyRepairItemDataModelList.add(new RepairItemDataModel("اشیاء اضافه", " ", false,
                    s -> tamirDakalEntity.setEzafeRepairedDec(s),
                    null,
                    (checkBox, isChecked) -> {
                        tamirDakalEntity.setEzafeRepaired(isChecked);
                    }));


        empetyRepairItemDataModelList.add(new DoubleButtonDataModel("ادامه", "پایان",
                v -> {
                    if (endTime != 0) {
                        tamirDakalEntity.setElateTakhir(elateTakhir);
                        tamirDakalEntity.setOperationTime(operationTime);
                        //tamirDakalEntity.setMScanType(scanType);
                        showLoader();
                        SaveRepairToDBModel saveRepairToDBModel = new SaveRepairToDBModel();
                        saveRepairToDBModel.saveRepairToDB(tamirDakalEntity, startTime, endTime, () -> {
                            mainActivityVM.setShouldGoToScanCodePage(true);
                            getRouter().exit();
                            hideLoader();
                        }, () -> {
                            showSnackBar("خطا در ذخیره اطلاعات، دوباره تلاش کنید");
                            hideLoader();
                        });
                    } else {
                        showSnackBar("ابتدا روی پایان زمان ماموریت کلیک کنید");
                    }
                },
                v -> {
                    if (endTime != 0) {
                        tamirDakalEntity.setElateTakhir(elateTakhir);
                        tamirDakalEntity.setOperationTime(operationTime);
                        //tamirDakalEntity.setMScanType(scanType);
                        showLoader();
                        SaveRepairToDBModel saveRepairToDBModel = new SaveRepairToDBModel();
                        saveRepairToDBModel.saveRepairToDB(tamirDakalEntity, startTime, endTime, () -> {
                            getRouter().backTo(Constants.HOME_SCREEN_KEY);
                            Intent intent = new Intent(getActivity(), LocationService.class);
                            getActivity().stopService(intent);

                            NotificationManager notificationManager = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);

                            if (notificationManager != null) {
                                notificationManager.cancelAll();
                            }
                            hideLoader();
                        }, () -> {
                            showSnackBar("خطا در ذخیره اطلاعات، دوباره تلاش کنید");
                            hideLoader();
                        });
                    } else {
                        showSnackBar("ابتدا روی پایان زمان ماموریت کلیک کنید");
                    }

                }, () -> "", (s) -> {
        }));
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        Gson gson = new GsonBuilder().create();
        dest.writeInt(tamirDakalEntity.getMId());
        dest.writeString(tamirDakalEntity.getBarcode());
        dest.writeString(elateTakhir);
        dest.writeString(operationTime);
        dest.writeString(scanType);
        if (iradatDakalEntity != null) {
            dest.writeInt(1);
            dest.writeString(gson.toJson(iradatDakalEntity));
        } else {
            dest.writeInt(0);
        }
        dest.writeLong(startTime);
        dest.writeLong(endTime);
    }

    private RepairDetailVM(Parcel in) {
        super(in);
        Gson gson = new GsonBuilder().create();
        tamirDakalEntity.setMId(in.readInt());
        tamirDakalEntity.setBarcode(in.readString());
        elateTakhir = in.readString();
        operationTime = in.readString();
        scanType = in.readString();
        int i = in.readInt();
        if (i == 1) {
            iradatDakalEntity = gson.fromJson(in.readString(), IradatDakalEntity.class);
        }
        startTime = in.readLong();
        endTime = in.readLong();
        startTimer();
    }

    public static Creator<RepairDetailVM> CREATOR = new Creator<RepairDetailVM>() {
        @Override
        public RepairDetailVM createFromParcel(Parcel source) {
            return new RepairDetailVM(source);
        }

        @Override
        public RepairDetailVM[] newArray(int size) {
            return new RepairDetailVM[size];
        }
    };


}

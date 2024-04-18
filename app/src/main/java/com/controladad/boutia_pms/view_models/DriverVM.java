package com.controladad.boutia_pms.view_models;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import androidx.databinding.DataBindingUtil;
import android.os.Parcel;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.controladad.boutia_pms.R;
import com.controladad.boutia_pms.databinding.FragmentDriverBinding;
import com.controladad.boutia_pms.location.LocationManager;
import com.controladad.boutia_pms.location.LocationService;
import com.controladad.boutia_pms.models.RepairOtherTypeModel;
import com.controladad.boutia_pms.utility.BoutiaApplication;
import com.controladad.boutia_pms.utility.Constants;

import java.util.Calendar;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import lombok.Getter;
import lombok.Setter;

public class DriverVM extends GeneralVM {

    private String selectedMissionId = "";
    private String elateTakhir = "";
    private String operationTime = "";
    private String repairType = "";
    private boolean isDriver = false;
    @Getter
    private int descriptionEditTextVisibility = View.VISIBLE;

    @Getter
    private String trackCountText = "تعداد ترکهای ثبت شده: ۰";

    public DriverVM(String selectedMissionId, String elateTakhir, String operationTime, String repairType) {
        this.selectedMissionId = selectedMissionId;
        this.elateTakhir = elateTakhir;
        this.operationTime = operationTime;
        this.repairType = repairType;
    }

    public DriverVM() {
        isDriver = true;
        descriptionEditTextVisibility = View.GONE;
        startTime = Calendar.getInstance().getTimeInMillis();
    }

    @Getter
    @Setter
    private String description = "";

    private Long startTime = 0L;

    @Override
    public View binding(LayoutInflater inflater, @Nullable ViewGroup container) {
        FragmentDriverBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_driver, container, false);
        binding.setDriverVM(this);
        mainActivityVM.setBackDisable(true);
        return binding.getRoot();
    }

    public View.OnClickListener onFinishClicked() {
        return v -> {
            if (!isDriver && (description == null || description.isEmpty())) {
                MissionStopDialogVM dialogVM = new MissionStopDialogVM(this::finish, "توضیحات", "شما توضیحی برای این ماموریت ثبت نکرده اید. آیا میخواهید بدون ثبت توضیح از ماموریت خارج شوید؟");
                getRouter().navigateToDialogFragment("", dialogVM);
            } else
                finish();
        };
    }

    private void finish() {
        mainActivityVM.setBackDisable(false);
        if (!isDriver) {
            RepairOtherTypeModel model = new RepairOtherTypeModel();
            Long finishTime = Calendar.getInstance().getTimeInMillis();
            model.saveMissionToDataBase(selectedMissionId, elateTakhir, operationTime, repairType, description, startTime,
                    finishTime, this::stopTracking,
                    () -> showSnackBar("خطا در ذخیره ی اطلاعات. دوباره تلاش کنید."));
        } else
            stopTracking();
    }

    private void stopTracking() {
        Intent intent = new Intent(getActivity(), LocationService.class);
        getActivity().stopService(intent);
        NotificationManager notificationManager = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            notificationManager.cancelAll();
        }
        getRouter().backTo(Constants.HOME_SCREEN_KEY);
    }

    @Override
    public void onDestroyFragment() {
        super.onDestroyFragment();
        mainActivityVM.setBackDisable(false);
    }

    @Override
    public void onResumeFragment() {
        super.onResumeFragment();
        LocationManager locationManager = LocationManager.locationManager;
        if (locationManager != null)
            locationManager.setOnTrakGet(() -> {
                setTrackCountText();
            });
        setTrackCountText();
    }

    private void setTrackCountText() {
        if (!isDriver)
            BoutiaApplication.INSTANCE.getDb()
                    .getTrackDao()
                    .getTrackCount(Integer.valueOf(selectedMissionId), repairType)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(count -> {
                        trackCountText = "تعداد ترکهای ثبت شده: " + String.valueOf(count);
                        notifyChange();
                    });
        else
            BoutiaApplication.INSTANCE.getDb()
                    .getTrackDao()
                    .getTrackCount()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(count -> {
                        trackCountText = "تعداد ترکهای ثبت شده: " + String.valueOf(count);
                        notifyChange();
                    });
    }

    @Override
    public void onPauseFragment() {
        super.onPauseFragment();
        LocationManager locationManager = LocationManager.locationManager;
        if (locationManager != null)
            locationManager.setOnTrakGet(null);
    }

    //for parcel

    public DriverVM(Parcel in) {
        super(in);
        selectedMissionId = in.readString();
        elateTakhir = in.readString();
        operationTime = in.readString();
        repairType = in.readString();
        startTime = in.readLong();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(selectedMissionId);
        dest.writeString(elateTakhir);
        dest.writeString(operationTime);
        dest.writeString(repairType);
        dest.writeLong(startTime);
    }

    public static Creator<DriverVM> CREATOR = new Creator<DriverVM>() {
        @Override
        public DriverVM createFromParcel(Parcel source) {
            return new DriverVM(source);
        }

        @Override
        public DriverVM[] newArray(int size) {
            return new DriverVM[size];
        }
    };
}

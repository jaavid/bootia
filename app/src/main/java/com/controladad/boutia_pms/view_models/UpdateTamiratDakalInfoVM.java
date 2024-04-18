package com.controladad.boutia_pms.view_models;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import androidx.databinding.DataBindingUtil;
import android.os.IBinder;
import android.os.Parcel;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.controladad.boutia_pms.R;
import com.controladad.boutia_pms.adapters.UpdateDakalTamiratAdapter;
import com.controladad.boutia_pms.databinding.FragmentUpdateDakalTamiratBinding;
import com.controladad.boutia_pms.models.UpdateTamiratDakalInfoModel;
import com.controladad.boutia_pms.models.UpdateTamiratDakalInfoService;
import com.controladad.boutia_pms.models.database.Mission_Tamirat_Entity;
import com.controladad.boutia_pms.utility.Constants;
import com.controladad.boutia_pms.view_models.items_view_models.UpdateDakalTamiratDataModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import lombok.Getter;
import saman.zamani.persiandate.PersianDate;
import saman.zamani.persiandate.PersianDateFormat;

import static android.content.Context.ACTIVITY_SERVICE;

public class UpdateTamiratDakalInfoVM extends GeneralVM implements UpdateDakalVM{
    private UpdateTamiratDakalInfoService mService;
    @Getter
    private UpdateDakalTamiratAdapter adapter = new UpdateDakalTamiratAdapter();
    private List<UpdateDakalTamiratDataModel> dakalTamiratDataModelList = new ArrayList<>();
    @Getter
    private String searchBoxInput = "";
    private UpdateTamiratDakalInfoModel updateTamiratDakalInfoModel;
    private UpdateDakalTamiratDataModel mUpdateDakalTamiratDataModel;
    private boolean isServiceBound = false;


    public void setSearchBoxInput(String searchBoxInput) {
        this.searchBoxInput = searchBoxInput;
        search(searchBoxInput);
    }

    private void search(String searchBoxInput) {
        List<UpdateDakalTamiratDataModel> newDakalTamiratDataModelList = new ArrayList<>();
        for (UpdateDakalTamiratDataModel dataModel : dakalTamiratDataModelList) {
            if ((dataModel.getItemName() != null && dataModel.getItemName().contains(searchBoxInput)) || searchBoxInput.isEmpty())
                newDakalTamiratDataModelList.add(dataModel);
        }
        adapter.updateData(newDakalTamiratDataModelList);
    }

    @Override
    public View binding(LayoutInflater inflater, @Nullable ViewGroup container) {
        FragmentUpdateDakalTamiratBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_update_dakal_tamirat,
                container, false);
        Intent intent = new Intent(getActivity(), UpdateTamiratDakalInfoService.class);

        if (isServiceRunning()) {
            getActivity().bindService(intent, mConnection, Context.BIND_ADJUST_WITH_ACTIVITY);
            isServiceBound = true;
        } else {
            getActivity().bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        }


        binding.setViewModel(this);

        return binding.getRoot();
    }

    private void getDataFromServer() {

        updateTamiratDakalInfoModel.getMissionTamiratFromDataBase(mission_tamirat_entities -> {
            for (Mission_Tamirat_Entity m : mission_tamirat_entities) {
                String lastUpdate = m.getLastUpdate();
                if (lastUpdate == null)
                    lastUpdate = "-";


                boolean isLoading = false;


                if (isServiceBound && Objects.equals(mService.getMid(), String.valueOf(m.getMId()))) {
                    isLoading = true;
                }

                UpdateDakalTamiratDataModel dakalTamiratDataModel = new
                        UpdateDakalTamiratDataModel(m.getMTitle(),
                        String.valueOf(m.getMId()), isLoading, lastUpdate, (key, ob) -> {
                    for (UpdateDakalTamiratDataModel updatDakalTamiratDataModel : dakalTamiratDataModelList) {
                        if (updatDakalTamiratDataModel.isLoading()) {
                            showSnackBar("برای دریافت اطلاعات جدید باید دریافت اطلاعات مربوط به " + updatDakalTamiratDataModel.getItemName() + " تمام شود.");
                            return;
                        }
                    }
                    updateTamiratDakalInfoModel.updateDakalList(key);
                    mService.setMid(key);
                    mUpdateDakalTamiratDataModel = ob;
                    ob.setLoading(true);
                });


                if (isLoading) {
                    mUpdateDakalTamiratDataModel = dakalTamiratDataModel;
                }
                dakalTamiratDataModelList.add(dakalTamiratDataModel);
            }
            afterDataReceivedComplete();
            updateTamiratDakalInfoModel.getDisposable().dispose();
        });
    }

    private boolean isServiceRunning() {
        ActivityManager manager = (ActivityManager) getActivity().getSystemService(ACTIVITY_SERVICE);
        if (manager != null) {
            for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
                if (UpdateTamiratDakalInfoService.class.getName().equals(service.service.getClassName())) {
                    return true;
                }
            }
        }
        return false;
    }

    private Function onSuccess() {
        return () -> {
            mService.setMid(null);
            if(mUpdateDakalTamiratDataModel != null){
                mUpdateDakalTamiratDataModel.setLoading(false);
                mUpdateDakalTamiratDataModel.setState(Constants.SENT_STATE);
                PersianDate pdate = new PersianDate();
                PersianDateFormat persianDateFormat = new PersianDateFormat("Y/m/d H:i:s");
                String date = persianDateFormat.format(pdate);
                mUpdateDakalTamiratDataModel.setLastUpdateDate(date);
            }
            mService.stopSelf();
        };
    }

    private Function onFailed() {
        return () -> {
            showSnackBar("خطا در دریافت اطلاعات");
            mService.setMid(null);
            if(mUpdateDakalTamiratDataModel != null){
                mUpdateDakalTamiratDataModel.setState(Constants.ERROR_STATE);
                mUpdateDakalTamiratDataModel.setLoading(false);
            }
            mService.stopSelf();
        };
    }

    private GenericMethod<Integer> progressUpdate = s ->{
        mUpdateDakalTamiratDataModel.setProgressPercent(s);
    };
    private Function onNoDakal = () ->{
        onSuccess().fun();
        showSnackBar("دکلی برای این ماموریت ثبت نشده است");
    };
    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {

            UpdateTamiratDakalInfoService.UpdateTamiratBinder mBinder = (UpdateTamiratDakalInfoService.UpdateTamiratBinder) service;
            mService = mBinder.getService();
            if (!isServiceBound) {
                updateTamiratDakalInfoModel = mService.getUpdateTamiratDakalInfoModel(
                        UpdateTamiratDakalInfoVM.this, onSuccess(),
                        onFailed(), progressUpdate,onNoDakal);
            } else {
                updateTamiratDakalInfoModel = mService.getmUpdateTamiratDakalInfoModel();
                updateTamiratDakalInfoModel.setOnFailed(onFailed());
                updateTamiratDakalInfoModel.setOnSuccess(onSuccess());
                updateTamiratDakalInfoModel.setUpdateProgress(progressUpdate);
                updateTamiratDakalInfoModel.setOnNoDakal(onNoDakal);
            }
            getDataFromServer();
            isServiceBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isServiceBound = false;
        }
    };

    @Getter
    private View.OnClickListener onNextClicked = v -> getRouter().exit();

    @Override
    public void afterDataReceivedComplete() {
        super.afterDataReceivedComplete();
        adapter.updateData(dakalTamiratDataModelList);
    }

    public UpdateTamiratDakalInfoVM() {
        super();
    }

    @Override
    public int getTitleId() {
        return R.string.update_repair;
    }

    @Override
    public int getLeftIconSource() {
        return R.drawable.ic_back;
    }

    //for parcel


    public UpdateTamiratDakalInfoVM(Parcel in) {
        super(in);
    }

    public static Creator<UpdateTamiratDakalInfoVM> CREATOR = new Creator<UpdateTamiratDakalInfoVM>() {
        @Override
        public UpdateTamiratDakalInfoVM createFromParcel(Parcel source) {
            return new UpdateTamiratDakalInfoVM(source);
        }

        @Override
        public UpdateTamiratDakalInfoVM[] newArray(int size) {
            return new UpdateTamiratDakalInfoVM[size];
        }
    };
}

package com.controladad.boutia_pms.view_models;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.Parcel;
import android.preference.PreferenceManager;
import androidx.fragment.app.DialogFragment;
import android.view.View;

import com.controladad.boutia_pms.R;
import com.controladad.boutia_pms.location.LocationService;
import com.controladad.boutia_pms.models.UpdateTamiratDakalInfoModel;
import com.controladad.boutia_pms.models.UpdateTamiratDakalInfoService;
import com.controladad.boutia_pms.models.database.Mission_Tamirat_Entity;
import com.controladad.boutia_pms.models.shared_preferences_models.SharedPreferencesModels;
import com.controladad.boutia_pms.utility.Constants;
import com.controladad.boutia_pms.view_models.items_view_models.MissionsDataModel;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Objects;

import lombok.Getter;
import saman.zamani.persiandate.PersianDate;
import saman.zamani.persiandate.PersianDateFormat;

import static android.content.Context.ACTIVITY_SERVICE;

public class RepairMissionChoseVM extends MissionChoseVM implements UpdateDakalVM {
    private SharedPreferencesModels.SharedPreferencesUserModel preferencesUserModel;
    @Getter
    private String role;
    private boolean isServiceBound = false;
    private UpdateTamiratDakalInfoService mService;
    private MissionsDataModel mUpdateDakalTamiratDataModel;
    private UpdateTamiratDakalInfoModel updateTamiratDakalInfoModel;


    @Override
    void getDataFromDataBase() {
        role = getRoleFromSharePreferences();
        if (repairType.equals("repair")) {
            Intent intent = new Intent(getActivity(), UpdateTamiratDakalInfoService.class);

            if (isServiceRunning()) {
                getActivity().bindService(intent, mConnection, Context.BIND_ADJUST_WITH_ACTIVITY);
                isServiceBound = true;
            } else {
                getActivity().bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
            }
        } else {
            super.getDataFromDataBase();
        }
    }

    private String repairType;
    private boolean isForMissionChoosing;

    RepairMissionChoseVM(String repairType, boolean isForMissionChoosing) {
        super();
        this.repairType = repairType;
        this.isForMissionChoosing = isForMissionChoosing;
    }

    @Override
    public int getTitleId() {
        if (isForMissionChoosing)
            return super.getTitleId();
        else return R.string.update_repair;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(repairType);
    }

    private RepairMissionChoseVM(Parcel in) {
        super(in);
        repairType = in.readString();
    }

    public static Creator<RepairMissionChoseVM> CREATOR = new Creator<RepairMissionChoseVM>() {
        @Override
        public RepairMissionChoseVM createFromParcel(Parcel source) {
            return new RepairMissionChoseVM(source);
        }

        @Override
        public RepairMissionChoseVM[] newArray(int size) {
            return new RepairMissionChoseVM[size];
        }
    };

    private String getRoleFromSharePreferences() {
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

    @Override
    void navigateToDialog(MissionsDataModel missionChooseDialogVM, int vaziateTakhirTajil, String role) {
        if (isForMissionChoosing)
            getRouter().navigateToDialogFragment("",
                    new RepairDialogMissionChooseVM(missionChooseDialogVM, vaziateTakhirTajil, role, repairType));
    }

    @Override
    View.OnClickListener qrScannerClickListener(QRScannerVM qrScannerVM) {
        return v -> {
            if (qrScannerVM.getBarCode() != null) {
                String barCodeTrimmed = qrScannerVM.getBarCodeTrimmed().toUpperCase();
                setDispatchingSearchBoxInput(barCodeTrimmed.substring(1, 6));
                getRouter().exit();
            }
        };
    }

    private void getDataFromServer() {
        updateTamiratDakalInfoModel.getTakhMissionTamiratFromDataBase(takh_mission_tamirat_entities -> {
            for (Mission_Tamirat_Entity m : takh_mission_tamirat_entities) {
                String lastUpdate = m.getLastUpdate();
                if (lastUpdate == null)
                    lastUpdate = "-";


                boolean isLoading = false;


                if (isServiceBound && Objects.equals(mService.getMid(), String.valueOf(m.getMId()))) {
                    isLoading = true;
                }

                MissionsDataModel mission = new MissionsDataModel(m.getMDispatchingCode(),
                        m.getMGroup(),
                        m.getMOpStart(),
                        m.getMOpEnd(),
                        m.getMType(), m.getMTitle(), m.getMId(), View.VISIBLE, isLoading,
                        (key, ob) -> {
                            for (MissionsDataModel missionsDataModel : getMissionsList()) {
                                if (missionsDataModel.isLoading()) {
                                    showSnackBar("برای دریافت اطلاعات جدید باید دریافت اطلاعات مربوط به " + missionsDataModel.getItemName() + " تمام شود.");
                                    return;
                                }
                            }
                            updateTamiratDakalInfoModel.updateDakalList(key);
                            mService.setMid(key);
                            mUpdateDakalTamiratDataModel = ob;
                            ob.setLoading(true);
                        }, Constants.TAKHIR);


                if (isLoading) {
                    mUpdateDakalTamiratDataModel = mission;
                }
                getTakhiriMissionsList().add(mission);
            }
            updateTamiratDakalInfoModel.getDisposable().dispose();
            updateTamiratDakalInfoModel.getTajilMissionTamiratFromDataBase(taj_mission_tamirat_entities -> {
                for (Mission_Tamirat_Entity m : taj_mission_tamirat_entities) {
                    String lastUpdate = m.getLastUpdate();
                    if (lastUpdate == null)
                        lastUpdate = "-";


                    boolean isLoading = false;


                    if (isServiceBound && Objects.equals(mService.getMid(), String.valueOf(m.getMId()))) {
                        isLoading = true;
                    }

                    MissionsDataModel mission = new MissionsDataModel(m.getMDispatchingCode(),
                            m.getMGroup(),
                            m.getMOpStart(),
                            m.getMOpEnd(),
                            m.getMType(), m.getMTitle(), m.getMId(), View.VISIBLE, isLoading,
                            (key, ob) -> {
                                for (MissionsDataModel missionsDataModel : getMissionsList()) {
                                    if (missionsDataModel.isLoading()) {
                                        showSnackBar("برای دریافت اطلاعات جدید باید دریافت اطلاعات مربوط به " + missionsDataModel.getItemName() + " تمام شود.");
                                        return;
                                    }
                                }
                                updateTamiratDakalInfoModel.updateDakalList(key);
                                mService.setMid(key);
                                mUpdateDakalTamiratDataModel = ob;
                                ob.setLoading(true);
                            }, Constants.TAJIL);


                    if (isLoading) {
                        mUpdateDakalTamiratDataModel = mission;
                    }
                    getTajiliMissionsList().add(mission);
                }
                updateTamiratDakalInfoModel.getDisposable().dispose();
                updateTamiratDakalInfoModel.getOnTimeMissionTamiratFromDataBase(onTime_mission_tamirat_entities -> {
                    for (Mission_Tamirat_Entity m : onTime_mission_tamirat_entities) {
                        String lastUpdate = m.getLastUpdate();
                        if (lastUpdate == null)
                            lastUpdate = "-";


                        boolean isLoading = false;


                        if (isServiceBound && Objects.equals(mService.getMid(), String.valueOf(m.getMId()))) {
                            isLoading = true;
                        }

                        MissionsDataModel mission = new MissionsDataModel(m.getMDispatchingCode(),
                                m.getMGroup(),
                                m.getMOpStart(),
                                m.getMOpEnd(),
                                m.getMType(), m.getMTitle(), m.getMId(), View.VISIBLE, isLoading,
                                (key, ob) -> {
                                    for (MissionsDataModel missionsDataModel : getMissionsList()) {
                                        if (missionsDataModel.isLoading()) {
                                            showSnackBar("برای دریافت اطلاعات جدید باید دریافت اطلاعات مربوط به " + missionsDataModel.getItemName() + " تمام شود.");
                                            return;
                                        }
                                    }
                                    updateTamiratDakalInfoModel.updateDakalList(key);
                                    mService.setMid(key);
                                    mUpdateDakalTamiratDataModel = ob;
                                    ob.setLoading(true);
                                }, Constants.ON_TIME);


                        if (isLoading) {
                            mUpdateDakalTamiratDataModel = mission;
                        }
                        getOnTimeMissionsList().add(mission);
                    }
                    updateTamiratDakalInfoModel.getDisposable().dispose();
                    getMissionsList().addAll(getOnTimeMissionsList());
                    getMissionsList().addAll(getTajiliMissionsList());
                    getMissionsList().addAll(getTakhiriMissionsList());
                    int selectedPosition = getTabLayout().getSelectedTabPosition();
                    switch (selectedPosition) {
                        case 0:
                            getAdapter().updateData(getOnTimeMissionsList());
                            if (getNameSearchBoxInput() != null && getGroupSearchBoxInput() != null && getDispatchingSearchBoxInput() != null){
                                getAdapter().updateData(this.search(this.getNameSearchBoxInput(),this.getGroupSearchBoxInput(),this.getDispatchingSearchBoxInput()));
                            }
                            break;
                        case 1:
                            getAdapter().updateData(getTakhiriMissionsList());
                            if (getNameSearchBoxInput() != null && getGroupSearchBoxInput() != null && getDispatchingSearchBoxInput() != null){
                                getAdapter().updateData(this.search(this.getNameSearchBoxInput(),this.getGroupSearchBoxInput(),this.getDispatchingSearchBoxInput()));
                            }
                            break;
                        case 2:
                            getAdapter().updateData(getTajiliMissionsList());
                            if (getNameSearchBoxInput() != null && getGroupSearchBoxInput() != null && getDispatchingSearchBoxInput() != null){
                                getAdapter().updateData(this.search(this.getNameSearchBoxInput(),this.getGroupSearchBoxInput(),this.getDispatchingSearchBoxInput()));
                            }
                            break;
                        case 3:
                            getAdapter().updateData(getMissionsList());
                            if (getNameSearchBoxInput() != null && getGroupSearchBoxInput() != null && getDispatchingSearchBoxInput() != null){
                                getAdapter().updateData(this.search(this.getNameSearchBoxInput(),this.getGroupSearchBoxInput(),this.getDispatchingSearchBoxInput()));
                            }
                            break;
                    }
                });
            });
        });

        // });
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
            if (mUpdateDakalTamiratDataModel != null) {
                mUpdateDakalTamiratDataModel.setLoading(false);
                mUpdateDakalTamiratDataModel.setProgressPercent(0);
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
            if (mUpdateDakalTamiratDataModel != null) {
                mUpdateDakalTamiratDataModel.setState(Constants.ERROR_STATE);
                mUpdateDakalTamiratDataModel.setLoading(false);
            }
            mService.stopSelf();
        };
    }

    private GenericMethod<Integer> progressUpdate = s -> {
        mUpdateDakalTamiratDataModel.setProgressPercent(s);
    };
    private Function onNoDakal = () -> {
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
                        RepairMissionChoseVM.this, onSuccess(),
                        onFailed(), progressUpdate, onNoDakal);
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
}

class RepairDialogMissionChooseVM extends MissionChooseDialogVM {
    private String elateTakhir = "";
    private String repairType;

    public RepairDialogMissionChooseVM(MissionsDataModel missionDataModel, int vaziateTakhir,
                                       String role, String repairType) {
        super(missionDataModel, vaziateTakhir, role);
        this.repairType = repairType;
    }

    @Override
    public View.OnClickListener getOnOkClicked() {
        return v -> {
            if (getTakhirTajilVisibility() == View.VISIBLE) {
                if (getElateTakhirTajilPosition() == -1) {
                    showSnackBar(getTakhirTajilText() + " را انتخاب کنید.");
                } else {
                    setElateTakhirTajil(getElateTakhirTajilPosition());
                    Intent intent = new Intent(getActivity(), LocationService.class);
                    intent.putExtra("mid", getMissionDataModel().getMissionId());
                    intent.putExtra("type", repairType);
                    getActivity().startService(intent);
                    if (Objects.equals(getRole(), "راننده") || !Objects.equals(repairType, "repair")) {
                        ((DialogFragment) getFragment()).dismiss();
                        mainActivityVM.getRouter(mainActivityVM.MAIN_ROUTER).navigateTo(Constants.DRIVER_SCREEN_KEY, new DriverVM(
                                String.valueOf(getMissionDataModel().getMissionId())
                                , elateTakhir, getVaziateTakhir(), repairType));
                    } else {

                        QRScannerVM qrScannerVM = new QRScannerVM();
                        qrScannerVM.setOnOkButtonClicked(
                                v1 -> {
                                    if (getMissionDataModel().getDispatchingCode() != null &&
                                            !getMissionDataModel().getDispatchingCode().toUpperCase().contains(
                                                    qrScannerVM.getBarCodeTrimmed().toUpperCase().substring(1, 6)
                                            )) {
                                        qrScannerVM.showSnackBar("کد دیسپاچینگ دکل با کد دیسپاچینگ ماموریت تطابق ندارد.");
                                    } else {
                                        String barCodeTrimmed = qrScannerVM.getBarCodeTrimmed().toUpperCase();
                                        String barCodeToSave = barCodeTrimmed.substring(0, 3) + " "
                                                + barCodeTrimmed.substring(3, 6) + " " +
                                                barCodeTrimmed.substring(6, 8) + " " +
                                                barCodeTrimmed.substring(8);
                                        mainActivityVM.getRouter(mainActivityVM.MAIN_ROUTER)
                                                .replaceScreen(Constants.REPAIR_TOWER_ITEM_SCREEN_KEY,
                                                        new RepairTowerChoseVM(
                                                                String.valueOf(getMissionDataModel().getMissionId()),
                                                                elateTakhir, getVaziateTakhir(), qrScannerVM.getScanType(),
                                                                barCodeToSave));
                                    }
                                }
                        );
                        ((DialogFragment) getFragment()).dismiss();
                        mainActivityVM.getRouter(mainActivityVM.MAIN_ROUTER)
                                .navigateTo(Constants.QR_SCANNER_KEY, qrScannerVM);
                    }
                }
            } else {
                Intent intent = new Intent(getActivity(), LocationService.class);
                intent.putExtra("mid", getMissionDataModel().getMissionId());
                intent.putExtra("type", repairType);
                getActivity().startService(intent);
                if (Objects.equals(getRole(), "راننده") || !Objects.equals(repairType, "repair")) {
                    ((DialogFragment) getFragment()).dismiss();
                    mainActivityVM.getRouter(mainActivityVM.MAIN_ROUTER).navigateTo(Constants.DRIVER_SCREEN_KEY, new DriverVM(
                            String.valueOf(getMissionDataModel().getMissionId())
                            , elateTakhir, getVaziateTakhir(), repairType));
                } else {

                    QRScannerVM qrScannerVM = new QRScannerVM();
                    qrScannerVM.setOnOkButtonClicked(
                            v1 -> {
                                if (getMissionDataModel().getDispatchingCode() != null &&
                                        !getMissionDataModel().getDispatchingCode().toUpperCase().contains(
                                                qrScannerVM.getBarCodeTrimmed().toUpperCase().substring(1, 6)
                                        )) {
                                    qrScannerVM.showSnackBar("کد دیسپاچینگ دکل با کد دیسپاچینگ ماموریت تطابق ندارد.");
                                } else {
                                    String barCodeTrimmed = qrScannerVM.getBarCodeTrimmed().toUpperCase();
                                    String barCodeToSave = barCodeTrimmed.substring(0, 3) + " "
                                            + barCodeTrimmed.substring(3, 6) + " " +
                                            barCodeTrimmed.substring(6, 8) + " " +
                                            barCodeTrimmed.substring(8);
                                    mainActivityVM.getRouter(mainActivityVM.MAIN_ROUTER)
                                            .replaceScreen(Constants.REPAIR_TOWER_ITEM_SCREEN_KEY,
                                                    new RepairTowerChoseVM(
                                                            String.valueOf(getMissionDataModel().getMissionId()),
                                                            elateTakhir, getVaziateTakhir(), qrScannerVM.getScanType(),
                                                            barCodeToSave));
                                }
                            }
                    );
                    ((DialogFragment) getFragment()).dismiss();
                    mainActivityVM.getRouter(mainActivityVM.MAIN_ROUTER)
                            .navigateTo(Constants.QR_SCANNER_KEY, qrScannerVM);
                }
            }
        };
    }

    private void setElateTakhirTajil(int position) {
        switch (position) {
            case 0:
                elateTakhir = "dispatching";
                break;
            case 1:
                elateTakhir = "tajhizat";
                break;
            case 2:
                elateTakhir = "sanat";
                break;
            case 3:
                elateTakhir = "niroogah";
                break;
            case 4:
                elateTakhir = "tozi";
                break;
            case 5:
                elateTakhir = "sarparast";
                break;
            case 6:
                elateTakhir = "weather";
                break;
            case 7:
                elateTakhir = "other";
                break;
            case 8:
                elateTakhir = "imeni";
                break;
        }
    }


    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(repairType);
    }

    private RepairDialogMissionChooseVM(Parcel in) {
        super(in);
        repairType = in.readString();
    }

    public static Creator<RepairDialogMissionChooseVM> CREATOR = new Creator<RepairDialogMissionChooseVM>() {
        @Override
        public RepairDialogMissionChooseVM createFromParcel(Parcel source) {
            return new RepairDialogMissionChooseVM(source);
        }

        @Override
        public RepairDialogMissionChooseVM[] newArray(int size) {
            return new RepairDialogMissionChooseVM[size];
        }
    };
}

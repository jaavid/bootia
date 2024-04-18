package com.controladad.boutia_pms.view_models;

import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import androidx.databinding.Bindable;
import androidx.databinding.DataBindingUtil;
import android.os.Parcel;
import android.preference.PreferenceManager;
import androidx.annotation.Nullable;

import com.google.android.gms.common.internal.Objects;
import com.google.android.material.tabs.TabLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.controladad.boutia_pms.R;
import com.controladad.boutia_pms.adapters.GeneralAdapter;
import com.controladad.boutia_pms.adapters.anims.MissionsAdapter;
import com.controladad.boutia_pms.databinding.FragmentMissionChoseBinding;
import com.controladad.boutia_pms.models.MissionChoseModel;
import com.controladad.boutia_pms.models.data_models.ProjectModel;
import com.controladad.boutia_pms.models.database.Mission_Entity;
import com.controladad.boutia_pms.models.shared_preferences_models.SharedPreferencesModels;
import com.controladad.boutia_pms.utility.BindHelpers;
import com.controladad.boutia_pms.utility.Constants;
import com.controladad.boutia_pms.view_models.items_view_models.MissionsDataModel;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lombok.Getter;
import saman.zamani.persiandate.PersianDate;
import saman.zamani.persiandate.PersianDateFormat;

import static com.controladad.boutia_pms.utility.Constants.MANUAL;

public class MissionChoseVM extends GeneralVM {
    private SharedPreferencesModels.SharedPreferencesUserModel preferencesUserModel;
    @Getter
    private MissionChoseModel missionChoseModel;
    @Getter
    private String role;
    @Getter
    @Bindable
    private int outOfDateShowVisibility = View.GONE;
    @Getter
    private List<MissionsDataModel> missionsList = new ArrayList<>();
    @Getter
    private List<MissionsDataModel> onTimeMissionsList = new ArrayList<>();
    @Getter
    private List<MissionsDataModel> takhiriMissionsList = new ArrayList<>();
    @Getter
    private List<MissionsDataModel> tajiliMissionsList = new ArrayList<>();
    @Getter
    private MissionsAdapter adapter = new MissionsAdapter();


    @Getter
    private TabLayout tabLayout;


    @Getter
    private String nameSearchBoxInput = "";

    @Getter
    private String groupSearchBoxInput = "";

    @Getter
    private String dispatchingSearchBoxInput = "";

    public void setNameSearchBoxInput(String nameSearchBoxInput) {
        this.nameSearchBoxInput = nameSearchBoxInput;
        adapter.updateData(search(nameSearchBoxInput, groupSearchBoxInput , dispatchingSearchBoxInput));
    }

    public void setGroupSearchBoxInput (String groupSearchBoxInput) {
        this.groupSearchBoxInput = groupSearchBoxInput;
        adapter.updateData(search(nameSearchBoxInput, groupSearchBoxInput , dispatchingSearchBoxInput));
    }

    public void setDispatchingSearchBoxInput (String dispatchingSearchBoxInput) {
        this.dispatchingSearchBoxInput = dispatchingSearchBoxInput;
        adapter.updateData(search(nameSearchBoxInput, groupSearchBoxInput , dispatchingSearchBoxInput));
    }


    public MissionChoseVM() {
        super();
    }

    @Override
    public View binding(LayoutInflater inflater, @Nullable ViewGroup container) {
        FragmentMissionChoseBinding binding = DataBindingUtil.inflate(inflater,
                R.layout.fragment_mission_chose, container, false);
        binding.setMissionChoseVM(this);
        tabLayout = binding.tabLayout;
        tabLayout.addOnTabSelectedListener(onTabSelected());
        changeTabsFont(tabLayout);
        return binding.getRoot();
    }

    private void changeTabsFont(TabLayout tabLayout) {
        ViewGroup vg = (ViewGroup) tabLayout.getChildAt(0);
        int tabsCount = vg.getChildCount();
        for (int j=0; j<tabsCount; j++) {
            ViewGroup vgTab = (ViewGroup) vg.getChildAt(j);
            int tabChildesCount = vgTab.getChildCount();
            for (int i=0; i<tabChildesCount; i++) {
                View tabViewChild = vgTab.getChildAt(i);
                if (tabViewChild instanceof TextView) {
                   BindHelpers.setFont((TextView)tabViewChild,"faNumRegular");
                }
            }
        }
    }

    public TabLayout.OnTabSelectedListener onTabSelected() {
        return new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int pos = tab.getPosition();
                switch (pos) {
                    case 0:
                        adapter.updateData(onTimeMissionsList);
                        if (nameSearchBoxInput != null && groupSearchBoxInput != null && dispatchingSearchBoxInput != null){
                            adapter.updateData(search(nameSearchBoxInput,groupSearchBoxInput,dispatchingSearchBoxInput));
                        }
                        break;
                    case 1:
                        adapter.updateData(takhiriMissionsList);
                        if (nameSearchBoxInput != null && groupSearchBoxInput != null && dispatchingSearchBoxInput != null){
                            adapter.updateData(search(nameSearchBoxInput,groupSearchBoxInput,dispatchingSearchBoxInput));
                        }
                        break;
                    case 2:
                        adapter.updateData(tajiliMissionsList);
                        if (nameSearchBoxInput != null && groupSearchBoxInput != null && dispatchingSearchBoxInput != null){
                            adapter.updateData(search(nameSearchBoxInput,groupSearchBoxInput,dispatchingSearchBoxInput));
                        }
                        break;
                    case 3:
                        adapter.updateData(missionsList);
                        if (nameSearchBoxInput != null && groupSearchBoxInput != null && dispatchingSearchBoxInput != null){
                            adapter.updateData(search(nameSearchBoxInput,groupSearchBoxInput,dispatchingSearchBoxInput));
                        }
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        };
    }



    @Override
    public void onCreateView() {
        super.onCreateView();
        role = getRoleFromSharePreferences();
        adapter.setItemClickListener(itemClickListener);
        getDataFromDataBase();
        String groupName = preferencesUserModel.getGroupName();
        //groupName = "b";
        if (
                Objects.equal(groupName,"a") || Objects.equal(groupName,"b") ||
                Objects.equal(groupName,"c") || Objects.equal(groupName,"d") ||
                Objects.equal(groupName,"e") ||Objects.equal(groupName,"f") ||
                Objects.equal(groupName,"g") ||Objects.equal(groupName,"h") ||
                Objects.equal(groupName,"i") || Objects.equal(groupName,"j") ||
                Objects.equal(groupName,"A") || Objects.equal(groupName,"B") ||
                Objects.equal(groupName,"C") || Objects.equal(groupName,"D") ||
                Objects.equal(groupName,"E") ||Objects.equal(groupName,"F") ||
                Objects.equal(groupName,"G") ||Objects.equal(groupName,"H") ||
                Objects.equal(groupName,"I") || Objects.equal(groupName,"J")
        )
        {

            setGroupSearchBoxInput(groupName);
        }
        else
        {
            setGroupSearchBoxInput("");
        }
    }

    void getDataFromDataBase() {
        if (missionChoseModel == null) {
            missionChoseModel = new MissionChoseModel();
            missionChoseModel.getBazdidMissionsFromDataBase(this::afterDataReceivedComplete);
        }
    }

    private GeneralAdapter.ItemClickListener itemClickListener = dataModel -> {
        int vaziateTakhirTajil = Constants.ON_TIME;
        MissionsDataModel missionChooseDataModel = (MissionsDataModel) dataModel;
        if (dateCompare(missionChooseDataModel.getStartDate()) == 1)
            vaziateTakhirTajil = Constants.TAJIL;
        else if (dateCompare(missionChooseDataModel.getEndDate()) == -1)
            vaziateTakhirTajil = Constants.TAKHIR;
        else if (dateCompare(missionChooseDataModel.getStartDate()) == 10)
            showSnackBar("لطفا ماموریت های خود را بروزرسانی کنید.");
        navigateToDialog(missionChooseDataModel, vaziateTakhirTajil, role);
    };

    private int getVaziateTakhirTajil(String startDate,String endDate){
        int vaziateTakhirTajil = Constants.ON_TIME;
        if (dateCompare(startDate) == 1)
            vaziateTakhirTajil = Constants.TAJIL;
        else if (dateCompare(endDate) == -1)
            vaziateTakhirTajil = Constants.TAKHIR;

        return vaziateTakhirTajil;
    }

    void navigateToDialog(MissionsDataModel missionChooseDialogVM, int vaziateTakhirTajil, String role) {
        getRouter().navigateToDialogFragment("",
                new MissionChooseDialogVM(missionChooseDialogVM, vaziateTakhirTajil, role));
    }

    @Override
    public void afterDataReceivedComplete() {
        super.afterDataReceivedComplete();
        if (missionChoseModel.getMissionEntitiesList() != null) {
            for (Mission_Entity mission : missionChoseModel.getTakhMissionEntitiesList()) {
                MissionsDataModel missionsDataModel = new MissionsDataModel(mission.getMDispatchingCode(), mission.getMGroupName(),
                        mission.getMOpStart(),
                        mission.getMOpEnd(),
                        mission.getMType(),mission.getMTitle(),mission.getMId(),View.GONE,false,null,
                        Constants.TAKHIR);
                takhiriMissionsList.add(missionsDataModel);
            }
            for (Mission_Entity mission : missionChoseModel.getTajMissionEntitiesList()) {
                MissionsDataModel missionsDataModel = new MissionsDataModel(mission.getMDispatchingCode(), mission.getMGroupName(),
                        mission.getMOpStart(),
                        mission.getMOpEnd(),
                        mission.getMType(),mission.getMTitle(),mission.getMId(),View.GONE,false,null,
                        Constants.TAJIL);
                tajiliMissionsList.add(missionsDataModel);
            }
            for (Mission_Entity mission : missionChoseModel.getOnTimeMissionEntitiesList()) {
                MissionsDataModel missionsDataModel = new MissionsDataModel(mission.getMDispatchingCode(), mission.getMGroupName(),
                        mission.getMOpStart(),
                        mission.getMOpEnd(),
                        mission.getMType(),mission.getMTitle(),mission.getMId(),View.GONE,false,null,
                        Constants.ON_TIME);
                onTimeMissionsList.add(missionsDataModel);
            }
            missionsList.addAll(onTimeMissionsList);
            missionsList.addAll(tajiliMissionsList);
            missionsList.addAll(takhiriMissionsList);
            int selectedPosition = tabLayout.getSelectedTabPosition();
            switch (selectedPosition) {
                case 0:
                    adapter.updateData(onTimeMissionsList);
                    if (nameSearchBoxInput != null && groupSearchBoxInput != null && dispatchingSearchBoxInput != null){
                        adapter.updateData(search(nameSearchBoxInput,groupSearchBoxInput,dispatchingSearchBoxInput));
                    }
                    break;
                case 1:
                    adapter.updateData(takhiriMissionsList);
                    if (nameSearchBoxInput != null && groupSearchBoxInput != null && dispatchingSearchBoxInput != null){
                        adapter.updateData(search(nameSearchBoxInput,groupSearchBoxInput,dispatchingSearchBoxInput));
                    }
                    break;
                case 2:
                    adapter.updateData(tajiliMissionsList);
                    if (nameSearchBoxInput != null && groupSearchBoxInput != null && dispatchingSearchBoxInput != null){
                        adapter.updateData(search(nameSearchBoxInput,groupSearchBoxInput,dispatchingSearchBoxInput));
                    }
                    break;
                case 3:
                    adapter.updateData(missionsList);
                    if (nameSearchBoxInput != null && groupSearchBoxInput != null && dispatchingSearchBoxInput != null){
                        adapter.updateData(search(nameSearchBoxInput,groupSearchBoxInput,dispatchingSearchBoxInput));
                    }
                    break;
            }
        }
    }


    @Override
    public int getLeftIconSource() {
        return R.drawable.ic_back;
    }


    @Override
    public int getTitleId() {
        return R.string.mission_chose;
    }

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
    public int getRightIconSource() {
        return R.drawable.ic_qr_code;
    }

    @Override
    public View.OnClickListener getOnRightToolBarIconClickListener() {
        return v -> {
            if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.CAMERA},
                        Constants.MY_PERMISSIONS_REQUEST_CAMERA);

            } else {
                QRScannerVM qrScannerVM = new QRScannerVM();
                qrScannerVM.setOnOkButtonClicked(qrScannerClickListener(qrScannerVM));
                getRouter().navigateTo(Constants.QR_SCANNER_KEY, qrScannerVM);
            }
        };
    }

    View.OnClickListener qrScannerClickListener(QRScannerVM qrScannerVM) {
        return v1 -> {
            if (qrScannerVM.getBarCode() != null) {
                if (mainActivityVM.getReviewModelHelper().getBarCodeList().isEmpty()) {
                    mainActivityVM.getReviewModelHelper().getBarCodeList().add(new ProjectModel.BarCode(null, MANUAL));
                    mainActivityVM.getReviewModel().getBarCodeList().add(new ProjectModel.BarCode(null, MANUAL));
                }
                String barCodeTrimmed = qrScannerVM.getBarCodeTrimmed().toUpperCase();
                String barCodeToSave = barCodeTrimmed.substring(0, 3) + " "
                        + barCodeTrimmed.substring(3, 6) + " " + barCodeTrimmed.substring(6, 8) + " " + barCodeTrimmed.substring(8);
                mainActivityVM.getReviewModelHelper().getBarCodeList().get(0).setNumber(barCodeToSave);
                mainActivityVM.getReviewModel().getBarCodeList().get(0).setNumber(barCodeToSave);
                mainActivityVM.getReviewModel().getElectricTowerNumber().setNumber(barCodeTrimmed.substring(barCodeTrimmed.length() - 3));
                mainActivityVM.getReviewModelHelper().getElectricTowerNumber().setNumber(barCodeTrimmed.substring(barCodeTrimmed.length() - 3));
                setDispatchingSearchBoxInput(barCodeTrimmed.substring(1, 6));
                getRouter().exit();
            }
        };
    }

    List<MissionsDataModel> search(String title, String group, String dispatching) {
        List<MissionsDataModel> searchResults = new ArrayList<>();
        List<MissionsDataModel> missons = missionsList;
        int selectedPosition = tabLayout.getSelectedTabPosition();
        switch (selectedPosition) {
            case 0:
                missons = (onTimeMissionsList);
                break;
            case 1:
                missons = (takhiriMissionsList);
                break;
            case 2:
                missons = (tajiliMissionsList);
                break;
        }

            for (MissionsDataModel missionsDataModel : missons) {

                if (missionsDataModel.getMissionName() != null &&
                        missionsDataModel.getMissionName().replace(" ","")
                                .replace("ک","ك")
                                .replace("ی","ي")
                                .contains(title.replace(" ","")
                                        .replace("ک","ك")
                                        .replace("ی","ي"))&&

                        missionsDataModel.getGroupCode() != null &&
                        missionsDataModel.getGroupCode().replace(" ","")
                                .replace("ک","ك")
                                .replace("ی","ي")
                                .toUpperCase()
                                .contains(group.replace(" ","")
                                        .replace("ک","ك")
                                        .replace("ی","ي").toUpperCase()) &&


                        missionsDataModel.getDispatchingCode() != null &&
                        missionsDataModel.getDispatchingCode()
                                .replace(" ","")
                                .replace("ک","ك")
                                .replace("ی","ي")
                                .toUpperCase().contains(dispatching
                                .replace(" ","")
                                .replace("ک","ك")
                                .replace("ی","ي")
                                .toUpperCase())
                )
                    searchResults.add(missionsDataModel);

            }

        return searchResults;
    }

    private int dateCompare(String date) {
        try {
            if (date.substring(0, 1).equals("2")) {
                String shamsiDate = convertToPersianDate(date);
                date = shamsiDate;
            }
            PersianDate persianDate = new PersianDate();
            String[] dateYMDArray = date.split("/");
            persianDate.initJalaliDate(Integer.valueOf(dateYMDArray[0]),
                    Integer.valueOf(dateYMDArray[1]),
                    Integer.valueOf(dateYMDArray[2]));
            PersianDate currentDate = new PersianDate();
            PersianDateFormat persianDateFormat = new PersianDateFormat("Y/m/d");
            String currentDateString = persianDateFormat.format(currentDate);
            String[] currentDateYMDArray = currentDateString.split("/");
            currentDate.initJalaliDate(Integer.valueOf(currentDateYMDArray[0]),
                    Integer.valueOf(currentDateYMDArray[1]),
                    Integer.valueOf(currentDateYMDArray[2]));
            return (persianDate.getTime() < currentDate.getTime() ? -1 :
                    (persianDate.getTime().equals(currentDate.getTime()) ? 0 : 1));
        } catch (NumberFormatException nfe) {
            return 10;
        }
    }

    private String convertToPersianDate(String inputDate) {

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = null;
        try {
            date = format.parse(inputDate);
            PersianDate pdate = new PersianDate(date);
            PersianDateFormat persianDateFormat = new PersianDateFormat("Y/m/d");
            return persianDateFormat.format(pdate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return inputDate;

    }

    @Override
    String getPageHint() {
        return context.getString(R.string.mission_chose_hint);
    }

    //for parcel


    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
    }

    public MissionChoseVM(Parcel in) {
        super(in);
    }

    public static Creator<MissionChoseVM> CREATOR = new Creator<MissionChoseVM>() {
        @Override
        public MissionChoseVM createFromParcel(Parcel source) {
            return new MissionChoseVM(source);
        }

        @Override
        public MissionChoseVM[] newArray(int size) {
            return new MissionChoseVM[size];
        }
    };
}

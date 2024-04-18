package com.controladad.boutia_pms.view_models;

import android.Manifest;
import android.content.pm.PackageManager;
import androidx.databinding.Bindable;
import androidx.databinding.DataBindingUtil;
import android.os.Parcel;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.controladad.boutia_pms.BR;
import com.controladad.boutia_pms.R;
import com.controladad.boutia_pms.adapters.TowersChooseAdapter;
import com.controladad.boutia_pms.databinding.FragmentRepairTowerChoseBinding;
import com.controladad.boutia_pms.models.RepairDetailModel;
import com.controladad.boutia_pms.models.database.IradatDakalEntity;
import com.controladad.boutia_pms.utility.Constants;
import com.controladad.boutia_pms.view_models.items_view_models.TowerChooseIVM;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import lombok.Getter;
import lombok.Setter;


public class RepairTowerChoseVM extends GeneralVM {
    private String barCode;
    private String operationTime;
    private String mid;
    private String elateTakhir;
    private List<TowerChooseIVM> repairMissionEntities = new ArrayList<>();
    private List<TowerChooseIVM> searchResults = new ArrayList<>();
    private RepairDetailModel repairDetailModel;
    private IradatDakalEntity selectedIradatDakalEntity;
    @Getter
    private TowersChooseAdapter adapter = new TowersChooseAdapter();
    @Bindable

    @Getter
    private String nameSearchBoxInput = "";

    public void setNameSearchBoxInput(String nameSearchBoxInput) {
        this.nameSearchBoxInput = nameSearchBoxInput;
        searchResults.clear();
        for (TowerChooseIVM towerChooseIVM : repairMissionEntities) {
            if (towerChooseIVM.getItemName().replace("ك", "ک")
                    .contains(nameSearchBoxInput.trim().replace("ك", "ک")))
                searchResults.add(towerChooseIVM);
        }
        adapter.updateData(searchResults);
    }

    private String scanType;

    @Bindable
    @Getter
    private String selectedBarcode;

    @Bindable
    @Setter
    @Getter
    private int filterTowerVisibility = View.GONE;

    public View.OnClickListener onClearFilterClick() {
        return v -> {
            searchResults.clear();
            searchResults.addAll(repairMissionEntities);
            filterTowerVisibility = View.GONE;
            notifyPropertyChanged(BR.filterTowerVisibility);
        };
    }

    @Override
    public void onStopFragment() {
        super.onStopFragment();
    }

    @Override
    public void afterDataReceivedComplete() {
        super.afterDataReceivedComplete();
        if (repairDetailModel.getIradatDakalEntities() != null) {
            repairMissionEntities.clear();
            for (IradatDakalEntity iradatDakalEntity : repairDetailModel.getIradatDakalEntityFilteredArrayList()) {
                boolean listContainsThisEntity = false;
                boolean isSelected = false;
                if (!repairMissionEntities.isEmpty()) {
                    for (int j = repairMissionEntities.size() - 1; j > -1; j--) {
                        IradatDakalEntity m = repairMissionEntities.get(j).getIradatDakalEntity();
                        if (Objects.equals(m.getBarcode(), iradatDakalEntity.getBarcode())) {
                            if (Long.valueOf(m.getTimeStamp()) < Long.valueOf(iradatDakalEntity.getTimeStamp())) {
                                repairMissionEntities.remove(j);
                            } else {
                                listContainsThisEntity = true;
                            }
                        }
                        if (Objects.equals(barCode, iradatDakalEntity.getBarcode()))
                            isSelected = true;
                    }
                }
                if (!listContainsThisEntity) {
                    TowerChooseIVM towerChooseIVM = new TowerChooseIVM(iradatDakalEntity);
                    if (isSelected) {
                        towerChooseIVM.setSelected(true);
                        selectedIradatDakalEntity = iradatDakalEntity;
                    }

                    repairMissionEntities.add(towerChooseIVM);
                }

            }
            searchResults.clear();
            searchResults.addAll(repairMissionEntities);
            adapter.updateData(repairMissionEntities);
        }
        notifyPropertyChanged(BR.filterTowerVisibility);
    }

    @Override
    public void onCreateView() {
        super.onCreateView();
        if (repairDetailModel == null) {
            repairDetailModel = new RepairDetailModel(this);
            repairDetailModel.getIradatDakalFromDataBase(Integer.valueOf(mid));
        }
        if (mainActivityVM.isShouldGoToScanCodePage()) {
            mainActivityVM.setShouldGoToScanCodePage(false);
            getOnRightToolBarIconClickListener().onClick(null);
        }
        adapter.setItemClickListener(dataModel -> {
            for (TowerChooseIVM t : repairMissionEntities) {
                t.setSelected(false);
            }
            TowerChooseIVM towerChooseIVM = (TowerChooseIVM) dataModel;
            towerChooseIVM.setSelected(true);
            barCode = towerChooseIVM.getIradatDakalEntity().getBarcode();
            selectedBarcode = barCode.substring(barCode.length() - 3);
            notifyPropertyChanged(BR.selectedBarcode);
            selectedIradatDakalEntity = towerChooseIVM.getIradatDakalEntity();
        });
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
                qrScannerVM.setOnOkButtonClicked(
                        v1 -> {
                            if (qrScannerVM.getBarCode() != null) {
                                String barCodeTrimmed = qrScannerVM.getBarCodeTrimmed().toUpperCase();
                                String barCodeToSave = barCodeTrimmed.substring(0, 3) + " "
                                        + barCodeTrimmed.substring(3, 6) + " " +
                                        barCodeTrimmed.substring(6, 8) + " " +
                                        barCodeTrimmed.substring(8);
                                barCode = barCodeToSave;
                                selectedBarcode = barCode.substring(barCode.length() - 3);
                                notifyPropertyChanged(BR.selectedBarcode);
                                selectedIradatDakalEntity = null;
                                setNameSearchBoxInput(selectedBarcode);
                                for (TowerChooseIVM towerChooseIVM : repairMissionEntities) {
                                    towerChooseIVM.setSelected(false);
                                    if (Objects.equals(towerChooseIVM.getIradatDakalEntity().getBarcode(),barCodeToSave)){
                                        towerChooseIVM.setSelected(true);
                                        selectedIradatDakalEntity = towerChooseIVM.getIradatDakalEntity();
                                    }
                                }
                                getRouter().exit();
                            }
                        }
                );
                getRouter().navigateTo(Constants.QR_SCANNER_KEY, qrScannerVM);
            }
        };
    }

    public View.OnClickListener onOkButtonClicked() {
        return v -> {
            getRouter().navigateTo(Constants.REPAIR_ITEM_SCREEN_KEY,
                    new RepairDetailVM(selectedIradatDakalEntity, elateTakhir, operationTime, scanType, mid, barCode));
        };
    }

    @Override
    public View binding(LayoutInflater inflater, @Nullable ViewGroup container) {
        FragmentRepairTowerChoseBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_repair_tower_chose, container, false);
        binding.setRepairTowerChoseVM(this);
        return binding.getRoot();

    }

    public RepairTowerChoseVM(String mid, String elateTakhir, String operationTime, String scanType,
                              String barCode) {
        super();
        this.mid = mid;
        this.elateTakhir = elateTakhir;
        this.operationTime = operationTime;
        this.scanType = scanType;
        this.barCode = barCode;
        this.selectedBarcode = barCode.substring(barCode.length() - 3);
    }


    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(mid);
        dest.writeString(elateTakhir);
        dest.writeString(operationTime);
        dest.writeString(scanType);
        dest.writeString(barCode);
    }

    public RepairTowerChoseVM(Parcel in) {
        super(in);
        mid = in.readString();
        elateTakhir = in.readString();
        operationTime = in.readString();
        scanType = in.readString();
        barCode = in.readString();
        selectedBarcode = barCode.substring(barCode.length() - 3);
    }

    public static Creator<RepairTowerChoseVM> CREATOR = new Creator<RepairTowerChoseVM>() {
        @Override
        public RepairTowerChoseVM createFromParcel(Parcel source) {
            return new RepairTowerChoseVM(source);
        }

        @Override
        public RepairTowerChoseVM[] newArray(int size) {
            return new RepairTowerChoseVM[size];
        }
    };
}

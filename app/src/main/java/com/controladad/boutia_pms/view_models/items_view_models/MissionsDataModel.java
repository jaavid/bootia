package com.controladad.boutia_pms.view_models.items_view_models;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.library.baseAdapters.BR;

import android.os.Parcel;
import android.os.Parcelable;
import android.view.View;

//import com.android.databinding.library.baseAdapters.BR;

import com.controladad.boutia_pms.utility.Constants;
import com.controladad.boutia_pms.view_models.GenericMethodTwo;

import lombok.Getter;
import saman.zamani.persiandate.PersianDate;
import saman.zamani.persiandate.PersianDateFormat;

public class MissionsDataModel extends BaseObservable implements GeneralDataModel, Parcelable {

    private GenericMethodTwo<String, MissionsDataModel> update;
    @Getter
    private String dispatchingCode;
    @Getter
    private String groupCode;
    @Getter
    private String startDate;
    @Getter
    private String endDate;
    @Getter
    private String missionDate;
    @Getter
    private String missionType;
    @Getter
    private String missionName;
    @Getter
    private int missionId;
    @Getter
    private int updateVisibility;
    @Getter
    private boolean isLoading;
    @Getter
    private int state = Constants.READY_STATE;


    @Getter
    private int vaziateTakhirTajil = Constants.ON_TIME;

    protected MissionsDataModel(Parcel in) {
        dispatchingCode = in.readString();
        groupCode = in.readString();
        startDate = in.readString();
        endDate = in.readString();
        missionDate = in.readString();
        missionType = in.readString();
        missionName = in.readString();
        missionId = in.readInt();
        updateVisibility = in.readInt();
        isLoading = in.readByte() != 0;
        state = in.readInt();
        lastUpdateDate = in.readString();
        progressPercent = in.readInt();
        vaziateTakhirTajil = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(dispatchingCode);
        dest.writeString(groupCode);
        dest.writeString(startDate);
        dest.writeString(endDate);
        dest.writeString(missionDate);
        dest.writeString(missionType);
        dest.writeString(missionName);
        dest.writeInt(missionId);
        dest.writeInt(updateVisibility);
        dest.writeByte((byte) (isLoading ? 1 : 0));
        dest.writeInt(state);
        dest.writeString(lastUpdateDate);
        dest.writeInt(progressPercent);
        dest.writeInt(vaziateTakhirTajil);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<MissionsDataModel> CREATOR = new Creator<MissionsDataModel>() {
        @Override
        public MissionsDataModel createFromParcel(Parcel in) {
            return new MissionsDataModel(in);
        }

        @Override
        public MissionsDataModel[] newArray(int size) {
            return new MissionsDataModel[size];
        }
    };

    public void setState(int state) {
        this.state = state;
        notifyChange();
    }

    public void setLoading(boolean loading) {
        isLoading = loading;
        notifyChange();
    }

    @Getter
    private String lastUpdateDate;

    public void setLastUpdateDate(String lastUpdateDate) {
        this.lastUpdateDate = lastUpdateDate;
        notifyChange();
    }

    @Bindable
    @Getter
    private int progressPercent = 0;

    public void setProgressPercent(int progressPercent) {
        this.progressPercent = progressPercent;
        notifyPropertyChanged(BR.progressPercent);
    }

    public MissionsDataModel(String dispatchingCode, String groupCode, String startDate,
                             String endDate, String missionType, String missionName, int missionId,
                             int updateVisibility, boolean isLoading, GenericMethodTwo<String, MissionsDataModel> update, int elateTakhirTajil) {
        this.dispatchingCode = dispatchingCode;
        this.groupCode = groupCode;
        this.startDate = startDate;
        this.endDate = endDate;
        this.missionDate = startDate + " - " + endDate;
        this.missionType = missionType;
        this.missionName = missionName;
        this.missionId = missionId;
        this.updateVisibility = updateVisibility;
        this.isLoading = isLoading;
        this.update = update;
        this.vaziateTakhirTajil = elateTakhirTajil;
    }

    public String middleTextDescription() {
        try {


            PersianDate currentDate = new PersianDate();
            PersianDateFormat persianDateFormat = new PersianDateFormat("Y/m/d");
            String currentDateString = persianDateFormat.format(currentDate);
            switch (vaziateTakhirTajil) {
                case Constants.TAKHIR:
                    return "امروز " + currentDateString + " \n" + caculateDateDiff(endDate, currentDateString) + " روز از پایان ماموریت گذشته است.";
                case Constants.TAJIL:
                    return "امروز " + currentDateString + " \n" + caculateDateDiff(currentDateString, startDate) + " روز به شروع ماموریت مانده است.";
                default:
                    return "امروز " + currentDateString + " \n" + caculateDateDiff(currentDateString, endDate) + "روز تا پایان زمانبندی";
            }

        } catch (NumberFormatException nfe) {
            return "";
        }
    }

    private int caculateDateDiff(String sDate, String eDate) {
        try {
            PersianDate persianDate = new PersianDate();
            String[] startDateYMDArray = sDate.split("/");
            String[] endDateYMDArray = eDate.split("/");

            return ((Integer.valueOf(endDateYMDArray[0]) - Integer.valueOf(startDateYMDArray[0])) * 365 +
                    (Integer.valueOf(endDateYMDArray[1]) - Integer.valueOf(startDateYMDArray[1])) * 30 +
                    (Integer.valueOf(endDateYMDArray[2]) - Integer.valueOf(startDateYMDArray[2])));
        } catch (Exception ex) {
            return 0;
        }

    }

    public View.OnClickListener onClickListener = v -> {
        if (update != null)
            update.fun(String.valueOf(missionId), this);
    };

    @Override
    public int getKey() {
        return 0;
    }

    @Override
    public String getItemName() {
        return missionName;
    }

    @Override
    public void setItemName(String itemName) {
        this.missionName = itemName;
    }

    @Override
    public boolean isItemFilled() {
        return false;
    }
}

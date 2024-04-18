package com.controladad.boutia_pms.view_models.items_view_models;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import android.view.View;
import android.widget.Button;

import com.controladad.boutia_pms.BR;
import com.controladad.boutia_pms.R;
import com.controladad.boutia_pms.utility.BoutiaApplication;
import com.controladad.boutia_pms.utility.Constants;

import java.util.Objects;

import lombok.Getter;
import lombok.Setter;


public class TrackReportItemDataModel extends BaseObservable implements GeneralDataModel {


    @Getter
    private String missionPointWithMissionName;
    @Getter
    private String pointNumber;
    @Getter
    private String trackDate;
    @Bindable
    @Getter
    private String buttonText = "آماده ارسال";
    @Setter
    protected View.OnClickListener onSendCordinationClickListener;
    @Bindable
    @Getter
    private int reportTrackProgressVisibility = View.GONE;
    @Bindable
    @Getter
    private int reportTrackButtonVisibility = View.VISIBLE;
    @Bindable
    @Getter
    private int imageTrack = R.drawable.ic_gps;
    @Getter
    private String mid;
    @Getter
    private int buttonColor = R.color.colorAccent;

    private int state;

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
        setTextAndColors(state);
    }

    @Setter
    private Button sendingButton;
    @Getter
    @Setter
    private boolean isSending;

    @Override
    public boolean equals(Object obj) {
        if(obj!=null && obj instanceof TrackReportItemDataModel){
            TrackReportItemDataModel trackReportItemDataModel = (TrackReportItemDataModel) obj;
            return Objects.equals(trackReportItemDataModel.getMid(),mid);
        }
        return false;
    }

    public void setReportTrackButtonVisibility(int reportTrackButtonVisibility) {
        if(reportTrackButtonVisibility == View.GONE)
            this.reportTrackProgressVisibility = View.VISIBLE;
        else
            this.reportTrackProgressVisibility = View.GONE;

        this.reportTrackButtonVisibility = reportTrackButtonVisibility;
        notifyPropertyChanged(BR.reportTrackButtonVisibility);
        notifyPropertyChanged(BR.reportTrackProgressVisibility);
    }

    public TrackReportItemDataModel(String missionPointWithMissionName, String pointNumber, String buttonText, String trackDate) {
        this.missionPointWithMissionName = missionPointWithMissionName;
        this.pointNumber = pointNumber;
        this.buttonText = buttonText;
        this.trackDate = trackDate;

    }

    public View.OnClickListener onSendCordinationClickListener() {

        return v -> {
            if (onSendCordinationClickListener != null) {
                onSendCordinationClickListener.onClick(v);
            }
        };
    }

    private void setButtonText( int state) {
        switch (state) {

            case Constants.SENT_STATE:
                buttonText = "ارسال شد";

                break;

            case Constants.ERROR_STATE:
                buttonText = "تلاش مجدد";

                break;

            default:
                buttonText = "آماده ارسال";

                break;
        }
        notifyPropertyChanged(BR.buttonText);
    }

    private void setImageTrack(int imageTrack) {
        switch (imageTrack) {
            case Constants.READY_STATE:
                this.imageTrack = R.drawable.ic_gps;
                break;
            case Constants.ERROR_STATE:
                this.imageTrack = R.drawable.ic_gps_faild;
                break;
            case Constants.SENT_STATE:
                this.imageTrack = R.drawable.ic_gps_done;
                break;
        }
        notifyPropertyChanged(BR.imageTrack);
    }

    private void setButtonColor(int imageTrack) {
        if(sendingButton!=null){
            switch (imageTrack) {
                case Constants.READY_STATE:
                    sendingButton.setBackgroundColor(BoutiaApplication.INSTANCE.getResources().getColor(R.color.colorAccent));
                    //this.buttonColor = R.color.colorAccent;
                    break;
                case Constants.ERROR_STATE:
                    sendingButton.setBackgroundColor(BoutiaApplication.INSTANCE.getResources().getColor(R.color.red_500));
                    //       this.buttonColor = R.color.red_500;
                    break;
                case Constants.SENT_STATE:
                    sendingButton.setBackgroundColor(BoutiaApplication.INSTANCE.getResources().getColor(R.color.green_500));
                    //       this.buttonColor = R.color.green_500;
                    break;
            }
        }

       // notifyPropertyChanged(BR.imageTrack);
    }

    private void setTextAndColors(int state){
        setButtonColor(state);
        setImageTrack(state);
        setButtonText(state);
    }


   /* public void setTrack(int isTrackSent) {
        switch (isTrackSent){
            case Constants.READY_STATE:
                this.imageUploaded = R.drawable.ic_gps;
                break;
            case Constants.ERROR_STATE:
                this.imageUploaded = R.drawable.ic_gps_faild;
                break;
            case Constants.SENT_STATE:
                this.imageUploaded = R.drawable.ic_gps_done;
                break;
        }
    }*/


    @Override
    public int getKey() {
        return Constants.TRACK_REPORT_ITEM_KEY;
    }

    @Override
    public String getItemName() {
        return null;
    }

    @Override
    public void setItemName(String itemName) {

    }


    @Override
    public boolean isItemFilled() {
        return false;
    }

    public void setMid(String mid) {
        this.mid = mid;
    }

}

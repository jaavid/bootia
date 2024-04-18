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

public class ReportItemDataModel extends BaseObservable implements GeneralDataModel {

    @Getter
    private String towerCode;
    /* @Getter
     private String dispatchingCode;*/
    @Getter
    @Setter
    int mid;
    @Getter
    String missionName;
    @Getter
    String reviewType;
    @Getter
    String reviewDate;
    @Bindable
    @Getter
    int reportProgressVisibility = View.GONE;
    @Bindable
    @Getter
    int reportButtonVisibility = View.VISIBLE;
    @Bindable
    @Getter
    String buttonText = "آماده ارسال";

    @Bindable
    @Getter
    int imageUploaded = R.drawable.ic_picture;

    @Bindable
    @Getter
    int dataCollectionUploaded = R.drawable.ic_data;

    @Bindable
    @Getter
    int submitted = R.drawable.ic_floppy;

    @Setter
    View.OnClickListener onSendDataClickListener;
    @Setter
    Button sendingButton;
    @Getter
    int state;
    @Setter
    @Getter
    boolean isSending;

    public void setState(int state) {
        this.state = state;
        setTextAndColors(state);
    }

    public ReportItemDataModel(int state) {
        this.state = state;

    }

    public void setReportButtonVisibility(int reportButtonVisibility) {
        if(reportButtonVisibility == View.GONE)
            this.reportProgressVisibility = View.VISIBLE;
        else
            this.reportProgressVisibility = View.GONE;

        this.reportButtonVisibility = reportButtonVisibility;
        notifyPropertyChanged(BR.reportButtonVisibility);
        notifyPropertyChanged(BR.reportProgressVisibility);
    }



    public ReportItemDataModel(String towerCode, String missionName,
                               String reviewType, String reviewDate, String buttonText) {//, String dispatchingCode
        this.towerCode = towerCode;
        // this.dispatchingCode = dispatchingCode;
        this.missionName = missionName;
        this.reviewType = reviewType;
        this.reviewDate = reviewDate;
        this.buttonText = buttonText;
    }

    public View.OnClickListener onSendDataClickListener() {
        return v -> {
            if (onSendDataClickListener != null)
                onSendDataClickListener.onClick(v);
        };
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

    private void setButtonText(int state) {
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

    private void setImageUploaded(int isImageUploaded) {
        switch (isImageUploaded) {
            case Constants.READY_STATE:
                this.imageUploaded = R.drawable.ic_picture;
                break;
            case Constants.ERROR_STATE:
                this.imageUploaded = R.drawable.ic_picture_faild;
                break;
            case Constants.SENT_STATE:
                this.imageUploaded = R.drawable.ic_picture_done;
                break;
        }
        notifyPropertyChanged(BR.imageUploaded);
    }

  /*  public void setDataCollectionUploaded(int isDataCollectionUploaded) {
        switch (isDataCollectionUploaded) {
            case Constants.READY_STATE:
                this.imageUploaded = R.drawable.ic_data;
                break;
            case Constants.ERROR_STATE:
                this.imageUploaded = R.drawable.ic_data_faild;
                break;
            case Constants.SENT_STATE:
                this.imageUploaded = R.drawable.ic_data_done;
                break;
        }
        notifyPropertyChanged(BR.dataCollectionUploaded);
    }
*/
    public void setSubmitted(int isSubmitted) {
        switch (isSubmitted) {
            case Constants.READY_STATE:
                this.submitted = R.drawable.ic_floppy;
                break;
            case Constants.ERROR_STATE:
                this.submitted = R.drawable.ic_floppy_faild;
                break;
            case Constants.SENT_STATE:
                this.submitted = R.drawable.ic_floppy_done;
                break;
        }
        notifyPropertyChanged(BR.submitted);
    }

    private void setTextAndColors(int state){
        setButtonColor(state);
        setImageUploaded(state);
        setSubmitted(state);
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
    public boolean equals(Object obj) {
        if(obj instanceof ReportItemDataModel) {
            ReportItemDataModel reportItemDataModel = (ReportItemDataModel) obj;
            return Objects.equals(reviewDate,reportItemDataModel.getReviewDate());
        }
        return super.equals(obj);
    }

    @Override
    public int getKey() {
        return Constants.REPORT_ITEM_CARD_ITEM_KEY;
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

}

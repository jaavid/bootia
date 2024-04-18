package com.controladad.boutia_pms.view_models;

import androidx.databinding.Bindable;
import androidx.databinding.DataBindingUtil;
import android.os.Handler;
import android.os.Parcel;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.controladad.boutia_pms.BR;
import com.controladad.boutia_pms.R;
import com.controladad.boutia_pms.databinding.FragmentQrScannerBinding;

import lombok.Getter;
import lombok.Setter;
import me.dm7.barcodescanner.zbar.Result;
import me.dm7.barcodescanner.zbar.ZBarScannerView;
import timber.log.Timber;

import static com.controladad.boutia_pms.utility.Constants.CAMERA;
import static com.controladad.boutia_pms.utility.Constants.MANUAL;

public class QRScannerVM extends GeneralVM implements ZBarScannerView.ResultHandler{


    private ZBarScannerView mScannerView;
    private  ViewGroup contentFrame;
    @Bindable
    @Getter
    @Setter
    private String barCode;
    @Setter
    private View.OnClickListener onOkButtonClicked;
    @Getter
    private String scanType=MANUAL;

    public String getBarCodeTrimmed(){
        if(barCode!=null)
            return barCode.replaceAll(" ","");
        return null;
    }

    public View.OnClickListener onOkButtonClicked(){
        return v -> {
            Timber.d(barCode);
            if(barCodeChecker(barCode)){
                barCode = toEnglishDigit(barCode);
                if(onOkButtonClicked != null)
                    onOkButtonClicked.onClick(v);
            }
            else showSnackBar(R.string.invalid_code);
        };
    }

    private boolean barCodeChecker(String barCode){
        String barCodeTrimmed=null;
        if(barCode!=null)
            barCodeTrimmed = barCode.replaceAll(" ","");
        return (barCodeTrimmed!=null && barCodeTrimmed.length()>=11 && (barCodeTrimmed.charAt(0) == 'T' || barCodeTrimmed.charAt(0) == 'B'
                || barCodeTrimmed.charAt(0) == 't' || barCodeTrimmed.charAt(0) == 'b') &&
                isNumber(barCodeTrimmed.substring(barCodeTrimmed.length()-3)));
    }

    @Override
    public View binding(LayoutInflater inflater, ViewGroup container) {
        FragmentQrScannerBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_qr_scanner,container,false);

        binding.setQrScannerVM(this);
        contentFrame = binding.contentFrame;
        return binding.getRoot();
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

    private boolean isNumber(String s){
        try {
            Integer integer = Integer.valueOf(s);
        }catch (NumberFormatException e){
            return false;
        }

        return true;
    }


    @Override
    public int getLeftIconSource() {
        return R.drawable.ic_back;
    }

    @Override
    public void onCreateView() {
        super.onCreateView();
        mScannerView = new ZBarScannerView(getActivity());
        mScannerView.setAspectTolerance(0.5f);
        contentFrame.addView(mScannerView);
    }

    @Override
    public void afterDataReceivedComplete() {
        super.afterDataReceivedComplete();
    }

    @Override
    public void onResumeFragment() {
        super.onResumeFragment();
        mScannerView.setResultHandler(this);
        mScannerView.startCamera();
    }

    @Override
    public void onPauseFragment() {
        super.onPauseFragment();
        mScannerView.stopCamera();
    }




    @Override
    public void handleResult(Result result) {
        // Note:
        // * Wait 2 seconds to resume the preview.
        // * On older devices continuously stopping and resuming camera preview can result in freezing the app.
        // * I don't know why this is the case but I don't have the time to figure out.
        if(barCodeChecker(result.getContents())){
            barCode = result.getContents();
            scanType = CAMERA;
        }
        notifyPropertyChanged(BR.barCode);
        Handler handler = new Handler();
        handler.postDelayed(() -> mScannerView.resumeCameraPreview(QRScannerVM.this), 2000);

    }

    //for parcel
    public static final Creator<QRScannerVM> CREATOR = new Creator<QRScannerVM>() {
        @Override
        public QRScannerVM createFromParcel(Parcel in) {
            return new QRScannerVM(in);
        }

        @Override
        public QRScannerVM[] newArray(int size) {
            return new QRScannerVM[size];
        }
    };

    public QRScannerVM() {
    }

    public QRScannerVM(Parcel in) {
        super(in);
    }
}

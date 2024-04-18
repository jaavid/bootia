package com.controladad.boutia_pms.view_models;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import androidx.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Parcel;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.controladad.boutia_pms.R;
import com.controladad.boutia_pms.databinding.MissionChooseDialogFragmentBinding;
import com.controladad.boutia_pms.location.LocationService;
import com.controladad.boutia_pms.models.MissionChoseModel;
import com.controladad.boutia_pms.utility.Constants;
import com.controladad.boutia_pms.view_models.items_view_models.MissionsDataModel;

import java.util.Objects;

import lombok.Getter;

public class MissionChooseDialogVM extends GeneralVM {
    @Getter
    private MissionsDataModel missionDataModel;
    private Spinner spinner;
    private int vaziateTakhir;
    @Getter
    private int takhirTajilVisibility = View.VISIBLE;
    @Getter
    private String takhirTajilText;
    @Getter
    private int elateTakhirTajilPosition = -1;
    @Getter
    private String role;
    private String[] tekhirTajilItems = new String[]{"عدم موافقت دیسپاچینگ به علت شرایط شبکه",
            "نداشتن تجهیزات مناسب",
            "عدم موافقت صنعت",
            "عدم موافقت نیروگاه ها",
            "عدم موافقت شرکت توزیع",
            "نبودن سرپرست",
            "شرایط بد اب و هوایی",
            "سایر موارد(اعیاد و سایر مراسم ها)",
            "عدم موافقت گروه ایمنی"};

    public MissionChooseDialogVM(MissionsDataModel missionDataModel, int vaziateTakhir,String role) {
        this.missionDataModel = missionDataModel;
        this.vaziateTakhir = vaziateTakhir;
        this.role = role;
        initfields();
    }

    private void initfields(){
        if (vaziateTakhir == Constants.ON_TIME)
            takhirTajilVisibility = View.GONE;
        else {
            if(vaziateTakhir == Constants.TAKHIR)
                takhirTajilText = "علت تاخیر";
            else
                takhirTajilText = "علت تعجیل";
        }
    }

    String getVaziateTakhir(){
        if(vaziateTakhir == Constants.TAKHIR)
            return "delay";
        else if(vaziateTakhir == Constants.TAJIL)
            return "hurry";
        else
           return "ontime";
    }

    @Getter
    private View.OnClickListener onOkClicked = v->{
        if(takhirTajilVisibility == View.VISIBLE){
            if(elateTakhirTajilPosition == -1){
                showSnackBar(takhirTajilText + " را انتخاب کنید.");
                //Toast.makeText(context,takhirTajilText + " را انتخاب کنید.",Toast.LENGTH_SHORT).show();
                return;
            }
            else
                setElateTakhirTajil(elateTakhirTajilPosition);
        }

        mainActivityVM.setReviewModel(null);
        mainActivityVM.setReviewModelHelper(null);

        mainActivityVM.getReviewModel().setVaziateTakhir(getVaziateTakhir());

        if(missionDataModel!=mainActivityVM.getBazdidMission()){
            Intent intent = new Intent(getActivity(), LocationService.class);
            getActivity().stopService(intent);

            NotificationManager notificationManager = (NotificationManager) getActivity()
                    .getSystemService(Context.NOTIFICATION_SERVICE);

            if (notificationManager != null) {
                notificationManager.cancelAll();
            }

        }
        mainActivityVM.setBazdidMission(missionDataModel);
        mainActivityVM.getReviewModelHelper().setMid(missionDataModel.getMissionId());
        mainActivityVM.getReviewModel().setMid(missionDataModel.getMissionId());
        mainActivityVM.getReviewModel().getMissionName().setName(missionDataModel.getItemName());
        MissionChoseModel missionChoseModel = new MissionChoseModel();
        missionChoseModel.getVoltage(missionDataModel.getMissionId(),s->{
            mainActivityVM.getReviewModel().getVoltage().setVoltage(s);
            mainActivityVM.getReviewModelHelper().getVoltage().setVoltage(s);
            ((DialogFragment)getFragment()).dismiss();
            Intent intent = new Intent(getActivity(), LocationService.class);
            intent.putExtra("mid", mainActivityVM.getReviewModel().getMid());
            intent.putExtra("type", Constants.BAZDID);
            getActivity().startService(intent);
            if (Objects.equals(role, "راننده"))
                mainActivityVM.getRouter(mainActivityVM.MAIN_ROUTER).navigateTo(Constants.DRIVER_SCREEN_KEY, new DriverVM());
            else
                mainActivityVM.getRouter(mainActivityVM.MAIN_ROUTER).navigateTo(Constants.REVIEW_FIRST_LEVEL, new ReviewFirstLevelVM(role));
        });
    };

    @Getter
    private View.OnClickListener onCancelClicked = v -> ((DialogFragment)getFragment()).dismiss();

    private void setElateTakhirTajil(int i){
        switch (i){
            case 0:
                mainActivityVM.getReviewModel().setElateTakhir("dispatching");
                mainActivityVM.getReviewModelHelper().setElateTakhir("dispatching");
                break;
            case 1:
                mainActivityVM.getReviewModel().setElateTakhir("tajhizat");
                mainActivityVM.getReviewModelHelper().setElateTakhir("tajhizat");
                break;
            case 2:
                mainActivityVM.getReviewModel().setElateTakhir("sanat");
                mainActivityVM.getReviewModelHelper().setElateTakhir("sanat");
                break;
            case 3:
                mainActivityVM.getReviewModel().setElateTakhir("niroogah");
                mainActivityVM.getReviewModelHelper().setElateTakhir("niroogah");
                break;
            case 4:
                mainActivityVM.getReviewModel().setElateTakhir("tozi");
                mainActivityVM.getReviewModelHelper().setElateTakhir("tozi");
                break;
            case 5:
                mainActivityVM.getReviewModel().setElateTakhir("sarparast");
                mainActivityVM.getReviewModelHelper().setElateTakhir("sarparast");
                break;
            case 6:
                mainActivityVM.getReviewModel().setElateTakhir("weather");
                mainActivityVM.getReviewModelHelper().setElateTakhir("weather");
                break;
            case 7:
                mainActivityVM.getReviewModel().setElateTakhir("other");
                mainActivityVM.getReviewModelHelper().setElateTakhir("other");
                break;
            case 8:
                mainActivityVM.getReviewModel().setElateTakhir("imeni");
                mainActivityVM.getReviewModelHelper().setElateTakhir("imeni");
                break;
        }
    }

    @Override
    public View binding(LayoutInflater inflater, @Nullable ViewGroup container) {
        MissionChooseDialogFragmentBinding binding = DataBindingUtil.inflate(inflater,
                R.layout.mission_choose_dialog_fragment,
                container,false);
        binding.setViewModel(this);
        spinner = binding.spinner;
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context,
                R.layout.spinner_text_view, tekhirTajilItems){
            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView text = view.findViewById(android.R.id.text1);
                text.setTextColor(Color.BLACK);
                return view;
            }
        };
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spinner.setOnItemSelectedListener(onItemSelectedListener);
        spinner.setSelection(-1);
        spinner.setAdapter(adapter);
        return binding.getRoot();
    }

    private AdapterView.OnItemSelectedListener onItemSelectedListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            elateTakhirTajilPosition = position;
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    @Override
    public void onStartFragment() {
        DialogFragment dialogFragment = (DialogFragment) getFragment();
        super.onStartFragment();
        if (dialogFragment.getDialog() == null) {
            return;
        }
        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        int h = dm.heightPixels;
        int w = dm.widthPixels;
        int dialogWidth = (9*w)/10;
        Objects.requireNonNull(dialogFragment.getDialog().getWindow()).setLayout(dialogWidth, 9*h/10);
    }

    //for parcel


    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeParcelable(missionDataModel,flags);
        dest.writeInt(vaziateTakhir);
        dest.writeString(role);
    }

    MissionChooseDialogVM(Parcel in) {
        super(in);
        missionDataModel = in.readParcelable(MissionsDataModel.class.getClassLoader());
        vaziateTakhir = in.readInt();
        role = in.readString();
        initfields();
    }

    public static Creator<MissionChooseDialogVM> CREATOR = new Creator<MissionChooseDialogVM>() {
        @Override
        public MissionChooseDialogVM createFromParcel(Parcel source) {
            return new MissionChooseDialogVM(source);
        }

        @Override
        public MissionChooseDialogVM[] newArray(int size) {
            return new MissionChooseDialogVM[size];
        }
    };
}

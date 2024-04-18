package com.controladad.boutia_pms.view_models;

import androidx.databinding.DataBindingUtil;
import android.os.Parcel;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;

import com.controladad.boutia_pms.R;
import com.controladad.boutia_pms.databinding.FragmentRepairTypeChoseBinding;
import com.controladad.boutia_pms.utility.Constants;

import java.util.Objects;

public class RepairTypeChoseVM extends GeneralVM {
    private RadioGroup typeGroup;

    private String trackType;
    @Override
    public View binding(LayoutInflater inflater, @Nullable ViewGroup container) {
        FragmentRepairTypeChoseBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_repair_type_chose,container,false);
        typeGroup = binding.typeGroup;
        binding.setViewModel(this);

        return binding.getRoot();
    }

   /* public RadioGroup.OnCheckedChangeListener onAnTypeSelected(){

        notifyPropertyChanged(BR.trackType);
        return (v,n)->{};
    }*/

    public View.OnClickListener onNextButtonClicked(){
        return v -> {
            int selectedID = typeGroup.getCheckedRadioButtonId();

            switch (selectedID)
            {
                case R.id.radio_routine:
                    trackType = "repair";
                    break;
                case R.id.radio_pahpad:
                    trackType = "pahpad";
                    break;
                case R.id.radio_khodro:
                    trackType = "khodro";
                    break;
                case R.id.radio_goroh:
                    trackType = "gorooh";
                    break;
                case R.id.radio_karshenas:
                    trackType = "karshenas";
                    break;
                case R.id.radio_harim:
                    trackType = "harim";
                    break;
                case R.id.radio_tahvil:
                    trackType = "tahvil";
                    break;
                case R.id.radio_khas:
                    trackType = "khas";
                    break;
                case R.id.radio_termo:
                    trackType = "termo";
                    break;

                case R.id.radio_zamin:
                    trackType = "zamin";
                    break;

            }

            if(trackType!=null) {
                if (!Objects.equals(selectedID,R.id.radio_routine))
                    getRouter().navigateTo(Constants.REPAIR_SCREEN_KEY, new RepairMissionChoseVM(trackType,true));
                else
                    getRouter().navigateTo(Constants.UPDATE_TAMIRAT_DAKAL_KEY, new RepairMissionChoseVM("repair",true));
            }
            else
                showSnackBar("لطفا نوع مامویت تعمیر را انتخاب نمایید.");

        };

    }

    @Override
    public int getLeftIconSource() {
        return R.drawable.ic_back;
    }

    public RepairTypeChoseVM() {
    }

    public RepairTypeChoseVM(Parcel in) {
        super(in);
    }

    public static Creator<RepairTypeChoseVM> CREATOR = new Creator<RepairTypeChoseVM>() {
        @Override
        public RepairTypeChoseVM createFromParcel(Parcel source) {
            return new RepairTypeChoseVM(source);
        }

        @Override
        public RepairTypeChoseVM[] newArray(int size) {
            return new RepairTypeChoseVM[size];
        }
    };
}

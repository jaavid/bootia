package com.controladad.boutia_pms.view_models;

import androidx.databinding.DataBindingUtil;
import android.os.Parcel;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;

import com.controladad.boutia_pms.R;
import com.controladad.boutia_pms.databinding.DialogFragmentCodeScanBinding;

import java.util.Objects;

import lombok.Getter;
import lombok.Setter;

public class DialogVM extends GeneralVM {


    @Getter
    private String title;
    @Getter
    private String text;
    @Getter
    private String buttonText;
    @Getter
    private String firstCheckBoxText;
    @Getter
    private String secondCheckBoxText;
    @Setter
    @Getter
    private View.OnClickListener onButtonClicked;
    @Getter
    private int radioGroupVisibility = View.GONE;
    @Getter
    private String checkedItemTitle;
    @Getter
    @Setter
    private String description;
    @Getter
    @Setter
    private String upButtonText;
    @Setter
    private View.OnClickListener onUpButtonClicked;
    @Getter
    private int upButtonVisibility = View.GONE;

    public DialogVM(String title, String text, String buttonText, View.OnClickListener onButtonClicked) {
        this.title = title;
        this.text = text;
        this.buttonText = buttonText;
        this.onButtonClicked = onButtonClicked;
    }

    public DialogVM(String title, String text, String buttonText, String upButtonText, String firstCheckBoxText, String secondCheckBoxText) {
        this.title = title;
        this.text = text;
        this.buttonText = buttonText;
        this.firstCheckBoxText = firstCheckBoxText;
        this.secondCheckBoxText = secondCheckBoxText;
        radioGroupVisibility = View.VISIBLE;
        upButtonVisibility = View.VISIBLE;
        this.upButtonText = upButtonText;
    }

    public RadioGroup.OnCheckedChangeListener onCheckedChanged(){
        return (group, checkedId) -> {
            if(Objects.equals(checkedId,R.id.first_check_box))
                checkedItemTitle = firstCheckBoxText;
            else if(Objects.equals(checkedId,R.id.second_check_box))
                checkedItemTitle = secondCheckBoxText;
        };
    }

    public View.OnClickListener onButtonClicked(){
        return v -> {
            ((DialogFragment)getFragment()).dismiss();
            if(onButtonClicked != null) onButtonClicked.onClick(v);
        };
    }

    public View.OnClickListener onUpButtonClicked(){
        return v -> {
            ((DialogFragment)getFragment()).dismiss();
            if(onUpButtonClicked != null) onUpButtonClicked.onClick(v);
        };
    }

    @Override
    public View binding(LayoutInflater inflater, @Nullable ViewGroup container) {
        DialogFragmentCodeScanBinding binding = DataBindingUtil.inflate(inflater, R.layout.dialog_fragment_code_scan,container,false);
        binding.setDialogVM(this);
        return binding.getRoot();
    }



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
        int dialogWidth = (5*w)/6;
        int dialogHeight;
        if(Objects.equals(radioGroupVisibility,View.VISIBLE))
            dialogHeight = (5*w)/4 ;
        else dialogHeight = (2*w)/3 ;

        dialogFragment.getDialog().getWindow().setLayout(dialogWidth, dialogHeight);
    }


    //for parcel

    public DialogVM(Parcel in) {
        super(in);
    }

    public static Creator<DialogVM> CREATOR = new Creator<DialogVM>() {
        @Override
        public DialogVM createFromParcel(Parcel source) {
            return new DialogVM(source);
        }

        @Override
        public DialogVM[] newArray(int size) {
            return new DialogVM[size];
        }
    };
}

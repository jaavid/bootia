package com.controladad.boutia_pms.view_models;

import android.annotation.SuppressLint;
import androidx.databinding.DataBindingUtil;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.controladad.boutia_pms.R;
import com.controladad.boutia_pms.databinding.DialogFragmentMissionStopBinding;

import java.util.Objects;

import lombok.Getter;

@SuppressLint("ParcelCreator")
public class MissionStopDialogVM extends GeneralVM {

    private Function missionStopping;
    private Function onCancelClicked;
    @Getter
    private String title = context.getString(R.string.mission_stop);
    @Getter
    private String question = context.getString(R.string.mission_stop_question);

    public MissionStopDialogVM(Function missionStopping) {
        this.missionStopping = missionStopping;
    }
    public MissionStopDialogVM(Function missionStopping , String title, String question) {
        this.missionStopping = missionStopping;
        this.title = title;
        this.question = question;
    }

    @Override
    public View binding(LayoutInflater inflater, @Nullable ViewGroup container) {
        DialogFragmentMissionStopBinding binding = DataBindingUtil.inflate(inflater, R.layout.dialog_fragment_mission_stop,container,false);
        binding.setMissionStopDialogVM(this);
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
        int dialogWidth;
        int dialogHeight;
        if(Objects.equals(title , context.getString(R.string.mission_stop))){
            dialogWidth = (5*w)/6;
            dialogHeight = 3*w/4 ;
        }else {
            dialogWidth = (5*w)/6;
            dialogHeight = w;
        }


        dialogFragment.getDialog().getWindow().setLayout(dialogWidth, dialogHeight);
    }

    @Getter
    private View.OnClickListener onYesButtonClicked = v->{
            ((DialogFragment)getFragment()).dismiss();
            missionStopping.fun();
    };

    @Getter
    private View.OnClickListener onNoButtonClicked = v->{
            ((DialogFragment)getFragment()).dismiss();
            if(onCancelClicked!=null)
                onCancelClicked.fun();
    };

    public void setOnCancelClicked(Function onCancelClicked) {
        this.onCancelClicked = onCancelClicked;
    }
}

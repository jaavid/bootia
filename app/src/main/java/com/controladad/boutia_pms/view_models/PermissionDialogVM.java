package com.controladad.boutia_pms.view_models;

import android.annotation.SuppressLint;
import androidx.databinding.DataBindingUtil;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.controladad.boutia_pms.R;
import com.controladad.boutia_pms.databinding.FragmentDialogPermissionBinding;

import lombok.Getter;
import lombok.Setter;

@SuppressLint("ParcelCreator")
public class PermissionDialogVM extends GeneralVM {
    @Getter
    private String title;
    @Getter
    private String text;
    @Getter
    private String buttonText;
    @Setter
    @Getter
    private View.OnClickListener onClickListener;

    public PermissionDialogVM(String title, String text, String buttonText, View.OnClickListener onClickListener) {
        this.title = title;
        this.text = text;
        this.buttonText = buttonText;
        this.onClickListener = onClickListener;
    }

    @Override
    public View binding(LayoutInflater inflater, @Nullable ViewGroup container) {
        FragmentDialogPermissionBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_dialog_permission,container,false);
        binding.setPermissionDialogVM(this);
        return binding.getRoot();
    }


}

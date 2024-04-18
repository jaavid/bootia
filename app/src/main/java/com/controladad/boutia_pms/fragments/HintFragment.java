package com.controladad.boutia_pms.fragments;


import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.controladad.boutia_pms.R;
import com.controladad.boutia_pms.databinding.FragmentHintBinding;
import com.controladad.boutia_pms.view_models.HintVM;

import lombok.Getter;
import lombok.Setter;

public class HintFragment extends Fragment{
    @Setter
    @Getter
    private String hintText;
    private final String AVATAR_KEY = "avatar_key";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        HintVM hintVM = new HintVM(hintText);
        FragmentHintBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_hint,container,false);
        binding.setHintVM(hintVM);
        hintVM.setFragment(this);
        return binding.getRoot();
    }

    public static HintFragment newInstance(String hintText) {
        Bundle args = new Bundle();
        HintFragment fragment = new HintFragment();
        args.putString(fragment.AVATAR_KEY,hintText);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //noinspection ConstantConditions
        hintText = getArguments().getString(AVATAR_KEY);
    }
}

package com.controladad.boutia_pms.fragments;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.controladad.boutia_pms.view_models.GeneralVM;

public class GeneralDialogFragment extends DialogFragment {
    private GeneralVM viewModel;
    private final static String VIEW_MODEL_PARCELABLE_KEY = "view_model_parcelable_key";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = viewModel.binding(inflater,container);
        viewModel.onCreateView();

        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        if(viewModel==null) viewModel = getArguments().getParcelable(VIEW_MODEL_PARCELABLE_KEY);
        assert viewModel != null;
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = this.getArguments().getParcelable(VIEW_MODEL_PARCELABLE_KEY);
        if(viewModel != null){
            viewModel.onCreateFragment();
            viewModel.setFragment(this);
        }
        else {
            getFragmentManager().beginTransaction()
                    .remove(this)
                    .commit();
        }
    }

    public static GeneralDialogFragment newInstance(GeneralVM viewModel) {
        GeneralDialogFragment fragment = new GeneralDialogFragment();
        Bundle args = new Bundle();
        args.putParcelable(VIEW_MODEL_PARCELABLE_KEY,viewModel);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onStart() {
        super.onStart();
        viewModel.onStartFragment();
    }
}

package com.controladad.boutia_pms.fragments;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.controladad.boutia_pms.utility.BoutiaApplication;
import com.controladad.boutia_pms.view_models.GeneralVM;
import com.controladad.boutia_pms.view_models.MainActivityVM;

import javax.inject.Inject;

public class GeneralFragment extends Fragment {

    private GeneralVM viewModel;
    private final static String VIEW_MODEL_PARCELABLE_KEY = "view_model_parcelable_key";
    @Inject
    MainActivityVM mainActivityVM;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = viewModel.binding(inflater,container);
        viewModel.onCreateView();
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BoutiaApplication.INSTANCE.getAppComponents().inject(this);
        viewModel = this.getArguments().getParcelable(VIEW_MODEL_PARCELABLE_KEY);
        mainActivityVM.setCurrentFragment(this);
        if (viewModel != null) {
            viewModel.setFragment(this);
        }
        if(viewModel != null)viewModel.onCreateFragment();
        else {
            getFragmentManager().beginTransaction()
                    .remove(this)
                    .commit();
        }
}

    public static GeneralFragment newInstance(GeneralVM viewModel , String routerKey) {
        GeneralFragment fragment = new GeneralFragment();
        BoutiaApplication.INSTANCE.getAppComponents().inject(fragment);
        Bundle args = new Bundle();
        viewModel.setRouterKey(routerKey);
        args.putParcelable(VIEW_MODEL_PARCELABLE_KEY,viewModel);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onPause() {
        super.onPause();
        if(viewModel != null) viewModel.onPauseFragment();
    }

    @Override
    public void onStop() {
        super.onStop();
        if(viewModel != null) viewModel.onStopFragment();
        if(mainActivityVM.getCurrentFragment() == this)
            mainActivityVM.setCurrentFragment(null);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(viewModel != null) viewModel.onDestroyFragment();
    }

    @Override
    public void onResume() {
        super.onResume();
        if(viewModel != null) viewModel.onResumeFragment();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if(viewModel != null) viewModel.onDestroyView();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(viewModel != null) viewModel.onAttach();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if(viewModel != null) viewModel.onDetach();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(VIEW_MODEL_PARCELABLE_KEY,viewModel);
    }
}

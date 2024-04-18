package com.controladad.boutia_pms.view_models;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.databinding.BaseObservable;
import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.Nullable;
import com.google.android.material.snackbar.Snackbar;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.controladad.boutia_pms.R;
import com.controladad.boutia_pms.activities.MainActivity;
import com.controladad.boutia_pms.fragment_navigation_handler.FragmentRouter;
import com.controladad.boutia_pms.network.dagger.RetrofitProviderComponent;
import com.controladad.boutia_pms.utility.BoutiaApplication;
import com.controladad.boutia_pms.utility.Constants;

import javax.inject.Inject;

import lombok.Getter;
import lombok.Setter;


public abstract class GeneralVM extends BaseObservable implements Parcelable {

    @Inject
    Context context;
    @Inject
    MainActivityVM mainActivityVM;
    @Setter
    private String routerKey;
    @Getter
    @Setter
    private Fragment fragment;

    private boolean isLoading;
    private boolean isFirstTime;


    public MainActivity getActivity() {
        return mainActivityVM.getActivity();
    }

    public FragmentRouter getRouter(){
        return mainActivityVM.getRouter(routerKey);
    }

    public void onCreateActivity(){
        if(isLoading) showLoader();
        else hideLoader();
    }
    public void onCreateFragment(){
        if(isLoading) showLoader();
    }
    public void onCreateView(){}
    public void onResumeFragment(){
        if(getLeftIconSource()!=0)mainActivityVM.setLeftIconSourceId(getLeftIconSource());
        if(getRightIconSource()!=0){
            mainActivityVM.setRightIconSourceId(getRightIconSource());
            mainActivityVM.setRightIconVisibility(View.VISIBLE);
        }
        if(getTitleId() != 0)mainActivityVM.setTitleId(getTitleId());
        mainActivityVM.setCurrentPageSubTitle();
        mainActivityVM.setOnLeftToolBarIconClickListener(getOnLeftToolBarIconClickListener());
        mainActivityVM.setOnRightToolBarIconClickListener(getOnRightToolBarIconClickListener());
        if(getActivity()!=null && !checkForHint() && getPageHint()!=null){
            mainActivityVM.showHint(getPageHint());
            isFirstTime = true;
        }
    }

    String getPageHint() {
        return null;
    }

    public void onPauseFragment(){
        //mainActivityVM.setLeftIconSourceId(R.mipmap.ic_launcher);
        mainActivityVM.setRightIconVisibility(View.GONE);
        mainActivityVM.setTitleId(R.string.boutia);
    }
    public void onStopFragment(){
        if(isFirstTime)
            mainActivityVM.hideHint();
    }
    public void onDestroyFragment(){}
    public void onDestroyView(){}
    public void onAttach(){}
    public void onDetach(){}
    public void onStartFragment() {}
    void showLoader(){
        mainActivityVM.showLoader();
        isLoading = true;
    }
    public void hideLoader(){
        mainActivityVM.hideLoader();
        isLoading = false;
    }

    void showSnackBar(int textId){
        if(getActivity()!=null)
            showSnackBar(textId,getActivity().getResources().getColor(R.color.colorPrimary));
    }

    void showSnackBar(String s){
        if(getActivity()!=null)
            showSnackBar(s,getActivity().getResources().getColor(R.color.colorPrimary),0,null);
    }
    void showSnackBar(int textID, int colorId){
        showSnackBar(textID,colorId,0,null);
    }
    void showSnackBar(int textID, int colorId, int actionBarTextId, View.OnClickListener onActionBarClicked){
        if(fragment != null && fragment.getView() != null){
            Snackbar snackbar = Snackbar.make(fragment.getView(),textID,Snackbar.LENGTH_LONG);
            snackbar.getView().setBackgroundColor(colorId);
            if(onActionBarClicked!=null)
                snackbar.setAction(actionBarTextId,onActionBarClicked);
            snackbar.show();
        }
    }

    void showSnackBar(String s, int colorId, int actionBarTextId, View.OnClickListener onActionBarClicked){
        if(fragment != null){
            showSnackBar(s,colorId,actionBarTextId,onActionBarClicked,fragment.getView());
        }
    }

    void showSnackBar(String s, int colorId, int actionBarTextId, View.OnClickListener onActionBarClicked,View view){
        if(view != null){
            Snackbar snackbar = Snackbar.make(view,s,Snackbar.LENGTH_LONG);
            snackbar.getView().setBackgroundColor(colorId);
            if(onActionBarClicked!=null)
                snackbar.setAction(actionBarTextId,onActionBarClicked);
            snackbar.show();
        }
    }
    void showSnackBarOnActivityView(String s){
        if(getActivity()!=null)
            showSnackBar(s,getActivity().getResources().getColor(R.color.colorPrimary),
                    0,null,getActivity().findViewById(R.id.fragment_container));
    }
    public int getTitleId(){
        return 0;
    }
    public int getLeftIconSource(){
        return 0;
    }
    public int getRightIconSource(){
        return 0;
    }
    public View.OnClickListener getOnLeftToolBarIconClickListener(){
        return v -> mainActivityVM.onBackPressed();
    }
    public View.OnClickListener getOnRightToolBarIconClickListener(){
        return null;
    }

    public void afterDataReceivedComplete(){
  //      hideLoader();
    }

    public void onNoNetworkException(){
        hideLoader();
        showSnackBar(R.string.no_network);
    }

    private boolean checkForHint(){
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(Constants.HINT_PREF, Context.MODE_PRIVATE);
        boolean bool = sharedPreferences.getBoolean(getClass().getSimpleName(),false);
        if(!bool)
            sharedPreferences.edit().putBoolean(getClass().getSimpleName(),true).apply();
        return bool;
    }


    public GeneralVM() {
        BoutiaApplication.INSTANCE.getAppComponents().inject(this);
    }

    public abstract View binding(LayoutInflater inflater, @Nullable ViewGroup container);


    protected GeneralVM(Parcel in) {
        BoutiaApplication.INSTANCE.getAppComponents().inject(this);
        routerKey = in.readString();
    }



    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(routerKey);
    }


    public RetrofitProviderComponent getComponent(){
        return getActivity().getComponent();
    }
}

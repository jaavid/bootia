package com.controladad.boutia_pms.view_models;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import androidx.databinding.DataBindingUtil;
import android.os.Parcel;
import android.preference.PreferenceManager;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.controladad.boutia_pms.R;
import com.controladad.boutia_pms.databinding.FragmentSettingBinding;
import com.controladad.boutia_pms.location.LocationService;
import com.controladad.boutia_pms.utility.Constants;

public class SettingVM extends GeneralVM {

    public SettingVM() {
    }

    @Override
    public View binding(LayoutInflater inflater, @Nullable ViewGroup container) {
        FragmentSettingBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_setting,container,false);
        binding.setSettingVM(this);
        return binding.getRoot();
    }

    public View.OnClickListener onLogoutClicked(){
        return v ->{
            Intent intent = new Intent(getActivity(), LocationService.class);
            getActivity().stopService(intent);

            NotificationManager notificationManager = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);

            if (notificationManager != null) {
                notificationManager.cancelAll();
            }

            removeSharedPreferencesCredentials();
            getRouter().newRootScreen(Constants.LOGIN_SCREEN_KEY,new LoginVM());
        };
    }

    public View.OnClickListener onHintButtonClicked(){
        return v -> {
            removeHintSharedPreferences();
            mainActivityVM.showHint("با ورود به هر صفحه، راهنمای مربوط به آن برای شما ظاهر میشود و با کلیک بر دگمه تایید میتوانید آن را از صفحه محو کنید.");
        };
    }

    @Override
    public int getLeftIconSource() {
        return R.drawable.ic_back;
    }

    private void removeSharedPreferencesCredentials() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        preferences.edit().clear().apply();

    }

    private void removeHintSharedPreferences(){
        SharedPreferences preferences = getActivity().getSharedPreferences(Constants.HINT_PREF, Context.MODE_PRIVATE);
        preferences.edit().clear().apply();
    }



    //for parcel


    public SettingVM(Parcel in) {
        super(in);
    }

    public final static Creator<SettingVM> CREATOR = new Creator<SettingVM>() {
        @Override
        public SettingVM createFromParcel(Parcel source) {
            return new SettingVM(source);
        }

        @Override
        public SettingVM[] newArray(int size) {
            return new SettingVM[size];
        }
    };
}

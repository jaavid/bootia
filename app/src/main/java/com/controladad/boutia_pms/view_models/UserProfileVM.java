package com.controladad.boutia_pms.view_models;

import android.content.SharedPreferences;
import androidx.databinding.DataBindingUtil;
import android.os.Parcel;
import android.preference.PreferenceManager;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.controladad.boutia_pms.R;
import com.controladad.boutia_pms.databinding.FragmentUserProfileBinding;
import com.controladad.boutia_pms.models.shared_preferences_models.SharedPreferencesModels;
import com.controladad.boutia_pms.utility.Constants;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Objects;

import lombok.Getter;


public class UserProfileVM extends GeneralVM {

    @Getter
    private String userName;
    @Getter
    private String userGroup;
    @Getter
    private String userMobileNumber;
    @Getter
    private String userRole;
    @Getter
    private String userImageURI;

    private SharedPreferencesModels.SharedPreferencesUserModel preferencesUserModel;

    @Override
    public View binding(LayoutInflater inflater, @Nullable ViewGroup container) {
        FragmentUserProfileBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_user_profile,container,false);
        binding.setUserProfileVM(this);
        return binding.getRoot();
    }


    private String getRoleFromSharePreferences(){
        //SharedPreferences preferences = getActivity().getSharedPreferences(Constants.USER_MODEL_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        Gson gson = new GsonBuilder().create();

        if (preferencesUserModel == null) {
            String value = preferences.getString(Constants.USER_MODEL_PREFERENCES, "");

            if (value.equals("")) {
                preferencesUserModel = null;
            } else {
                preferencesUserModel = gson.fromJson(value, SharedPreferencesModels.SharedPreferencesUserModel.class);
            }
        }
        return preferencesUserModel !=null? preferencesUserModel.getRole():null;
    }

    public UserProfileVM(String userName, String userLastName, String userMobileNumber, String userRole,String userImageURI) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        Gson gson = new GsonBuilder().create();

        if (preferencesUserModel == null) {
            String value = preferences.getString(Constants.USER_MODEL_PREFERENCES, "");

            if (value.equals("")) {
                preferencesUserModel = null;
            } else {
                preferencesUserModel = gson.fromJson(value, SharedPreferencesModels.SharedPreferencesUserModel.class);
            }
        }
        this.userName = preferencesUserModel.getName();
        this.userGroup = preferencesUserModel.getGroupName();
        this.userMobileNumber = preferencesUserModel.getPhone();
        this.userRole = preferencesUserModel.getRole();

        if (!Objects.equals(preferencesUserModel.getPictureUri(),""))
            this.userImageURI = preferencesUserModel.getPictureUri();
        else
            this.userImageURI = userImageURI;
    }

    @Override
    public int getLeftIconSource() {
        return R.drawable.ic_back;
    }

    //for parcel
    public UserProfileVM(Parcel in) {
        super(in);
    }
    public static Creator<UserProfileVM> CREATOR = new Creator<UserProfileVM>() {

        @Override
        public UserProfileVM createFromParcel(Parcel source) {
            return new UserProfileVM(source);
        }

        @Override
        public UserProfileVM[] newArray(int size) {
            return new UserProfileVM[size];
        }
    };
}

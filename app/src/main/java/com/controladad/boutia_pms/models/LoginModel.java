package com.controladad.boutia_pms.models;

import android.Manifest;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import androidx.core.app.ActivityCompat;

import com.controladad.boutia_pms.fragments.GeneralDialogFragment;
import com.controladad.boutia_pms.models.shared_preferences_models.SharedPreferencesModels;
import com.controladad.boutia_pms.network.network_check.NoNetworkException;
import com.controladad.boutia_pms.network.retrofit.RetrofitApiService;
import com.controladad.boutia_pms.view_models.LoginVM;
import com.controladad.boutia_pms.view_models.PermissionDialogVM;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import lombok.Getter;
import retrofit2.HttpException;
import retrofit2.Response;
import timber.log.Timber;

import static com.controladad.boutia_pms.utility.Constants.CREDENTIALS_PREFERENCES;
import static com.controladad.boutia_pms.utility.Constants.READ_PHONE_STATE_PERMISSION;
import static com.controladad.boutia_pms.utility.Constants.USER_MODEL_PREFERENCES;

public class LoginModel {


    private LoginVM loginVM;
    @Getter
    private Disposable loginDisposable;

    public LoginModel(LoginVM loginVM) {
        this.loginVM = loginVM;
    }

    public void getUserFromServer(String imei) {

        RetrofitApiService retrofitApiService = loginVM.getComponent().getService();

        loginDisposable =
                retrofitApiService
                        .getUser(imei)
                        //.getUser("359001070653832")
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(preUser -> {
                            Timber.d(preUser.getUser().getUserName());

                            //Setting Credentials Preferences
                            final String token = preUser.getToken();
                            final String cookie = preUser.getSessionName() + "=" + preUser.getSessionId();
                            setSharedPreferencesCredentials(token, cookie, imei);

                            Observable.just(preUser.getUser().getUserRole())
                                    .flatMapIterable(Map::values)
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(v -> {
                                        if (!v.equals("authenticated user")) {
                                            //User Object
                                            final String role = v;
                                            final String uid = preUser.getUser().getUserId();
                                            final String name = preUser.getUser().getUserName() != null ? preUser.getUser().getUserName() : "";
                                            final String phone = preUser.getUser().getPhoneNumber() != null ? preUser.getUser().getPhoneNumber() : "";
                                            final String pictureUri = preUser.getUser().getPhoto() != null ? preUser.getUser().getPhoto().getPictureUri() : "";
                                            final String groupName = preUser.getUser().getGroup() != null && preUser.getUser().getGroup().getGroupName() != null ? preUser.getUser().getGroup().getGroupName() : "";

                                            setSharedPreferencesUserModel(uid, name, role, phone, pictureUri, groupName);
                                            loginVM.afterDataReceivedComplete();
                                        }
                                    });

                        }, throwable -> {
                            loginVM.hideLoader();
                            if (throwable instanceof HttpException) {
                                Response response = ((HttpException) throwable).response();

                                switch (response.code()) {

                                    case 401:

                                        break;

                                    case 400:


                                        break;

                                    case 403:
                                        PermissionDialogVM permissionDialogVM = new PermissionDialogVM("خطا", "IMEI شما در سامانه موجود نیست", "بستن", null);
                                        GeneralDialogFragment dialogFragment = GeneralDialogFragment.newInstance(permissionDialogVM);
                                        permissionDialogVM.setOnClickListener(v -> {
                                            ActivityCompat.requestPermissions(loginVM.getActivity()
                                                    , new String[]{Manifest.permission.READ_PHONE_STATE}
                                                    , READ_PHONE_STATE_PERMISSION);
                                            dialogFragment.dismiss();
                                        });
//                                        GeneralDialogFragment dialogFragment = GeneralDialogFragment.newInstance(new PermissionDialogVM(
//                                                "خطا", "IMEI شما در سامانه موجود نیست", "بستن",
//                                                v -> {
//                                                    ActivityCompat.requestPermissions(loginVM.getActivity()
//                                                            , new String[]{Manifest.permission.READ_PHONE_STATE}
//                                                            , READ_PHONE_STATE_PERMISSION);
//
//                                                }
//                                        ));
                                        //dialogFragment.setCancelable(true);
                                        dialogFragment.show(loginVM.getFragment().getFragmentManager(), "");
                                        break;
                                }

                            } else if (throwable instanceof NoNetworkException) {
                                Timber.d(throwable.getMessage());
                                loginVM.onNoNetworkException();
                            } else {
                                Timber.d(throwable.getMessage());
                                loginVM.onNoNetworkException();
                            }

                        });
    }

    private void setSharedPreferencesUserModel(String uid, String name, String role, String phone, String pictureUri, String groupName) {

        SharedPreferencesModels.SharedPreferencesUserModel sharedPreferencesUserModel = new SharedPreferencesModels.SharedPreferencesUserModel(uid, name, role, phone, pictureUri, groupName);
        Gson gson = new GsonBuilder().create();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(loginVM.getActivity());
        preferences.edit().putString(USER_MODEL_PREFERENCES, gson.toJson(sharedPreferencesUserModel)).apply();

    }

    private void setSharedPreferencesCredentials(String token, String cookie, String imei) {

        SharedPreferencesModels.SharedPreferencesCredentials sharedPreferencesCredentials = new SharedPreferencesModels.SharedPreferencesCredentials(token, cookie, imei);
        Gson gson = new GsonBuilder().create();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(loginVM.getActivity());
        preferences.edit().putString(CREDENTIALS_PREFERENCES, gson.toJson(sharedPreferencesCredentials)).apply();
    }
}

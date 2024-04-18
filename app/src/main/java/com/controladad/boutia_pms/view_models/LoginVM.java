package com.controladad.boutia_pms.view_models;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;

import androidx.databinding.Bindable;
import androidx.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Parcel;
import android.provider.Settings;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.content.PermissionChecker;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.controladad.boutia_pms.BR;
import com.controladad.boutia_pms.R;
import com.controladad.boutia_pms.databinding.FragmentLoginBinding;
import com.controladad.boutia_pms.fragments.GeneralDialogFragment;
import com.controladad.boutia_pms.models.LoginModel;
import com.controladad.boutia_pms.utility.Constants;

import java.lang.reflect.Method;

import lombok.Getter;
import timber.log.Timber;

public class LoginVM extends GeneralVM {

    private LoginModel loginModel;

    @Bindable
    public String strDeviceID = "";
    @Bindable
    @Getter
    private int showDeviceIDView = View.GONE;

    public LoginVM() {

    }


    @Override
    public View binding(LayoutInflater inflater, @Nullable ViewGroup container) {
        FragmentLoginBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_login, container, false);
        binding.setLoginVM(this);
        return binding.getRoot();
    }

    @Override
    public void onResumeFragment() {
        super.onResumeFragment();

        //Disposing login model disposable if it is not disposed
        if (loginModel.getLoginDisposable() != null)
            if (!loginModel.getLoginDisposable().isDisposed())
                loginModel.getLoginDisposable().dispose();

        mainActivityVM.setToolbarVisibility(View.GONE);
    }

    @Override
    public void onCreateView() {
        super.onCreateView();
      /*      //Remove title bar
        //getActivity().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getActivity().requestWindowFeature(Window.FEATURE_NO_TITLE);

        //Remove notification bar
       getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        */
        if(Build.VERSION.SDK_INT >= 29) {
            showDeviceIDView = View.VISIBLE;
            notifyPropertyChanged(BR.showDeviceIDView);
        }

        loginModel = new LoginModel(this);
    }

    @Override
    public void afterDataReceivedComplete() {
        super.afterDataReceivedComplete();
        hideLoader();
        Timber.d("We Are in afterDataReceived");
        getRouter().replaceScreen(Constants.HOME_SCREEN_KEY, new HomeVM());
    }

    public View.OnClickListener onDeviceIDClicked(){
        return v -> {
            strDeviceID = Settings.Secure.getString(context.getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
            notifyPropertyChanged(BR.strDeviceID);
            Log.d("test DeviceID =======>", strDeviceID);
        };
    }

    @SuppressLint("HardwareIds")
    public View.OnClickListener onLoginClick() {
        return v -> {
            String sim1 = "1";
            String sim2 = "1";

            TelephonyManager telephonyManager = (TelephonyManager) getActivity().getSystemService(Context.TELEPHONY_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                if (PermissionChecker.checkSelfPermission(getActivity(), Manifest.permission.READ_PHONE_STATE) != PermissionChecker.PERMISSION_GRANTED) {
                    getActivity().checkPermissions();
                } else {
                    if(Build.VERSION.SDK_INT >= 29) {
                        sim1 = Settings.Secure.getString(context.getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
                    }
                    else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        try {
                            assert telephonyManager != null;
                            if (telephonyManager.getPhoneCount() == 1) {
                                sim1 = telephonyManager.getImei();
                            } else {
                                sim1 = telephonyManager.getImei(0);
                                sim2 = telephonyManager.getImei(1);
                            }
                        } catch (NullPointerException e) {
                            e.printStackTrace();
                        }
                    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                        try {
                            assert telephonyManager != null;
                            if (telephonyManager.getPhoneCount() == 1) {
                                sim1 = telephonyManager.getDeviceId();
                            } else {
                                sim1 = telephonyManager.getDeviceId(0);
                                sim2 = telephonyManager.getDeviceId(1);
                            }
                        } catch (NullPointerException e) {
                            e.printStackTrace();
                        }
                    } else {
                        try {

                            assert telephonyManager != null;
                            Class<?> telephonyClass = Class.forName(telephonyManager.getClass().getName());
                            Class<?>[] parameter = new Class[1];
                            parameter[0] = int.class;
                            Method getFirstMethod = telephonyClass.getMethod("getDeviceId", parameter);
                            Object[] obParameter = new Object[1];
                            obParameter[0] = 0;
                            sim1 = (String) getFirstMethod.invoke(telephonyManager, obParameter);
                            obParameter[0] = 1;
                            sim2 = (String) getFirstMethod.invoke(telephonyManager, obParameter);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            } else {

                try {
                    assert telephonyManager != null;
                    Class<?> telephonyClass = Class.forName(telephonyManager.getClass().getName());
                    Class<?>[] parameter = new Class[1];
                    parameter[0] = int.class;
                    Method getFirstMethod = telephonyClass.getMethod("getDeviceId", parameter);
                    Object[] obParameter = new Object[1];
                    obParameter[0] = 0;
                    sim1 = (String) getFirstMethod.invoke(telephonyManager, obParameter);
                    obParameter[0] = 1;
                    sim2 = (String) getFirstMethod.invoke(telephonyManager, obParameter);
                } catch (Exception e) {
                    assert telephonyManager != null;
                    sim1 = telephonyManager.getDeviceId();
                    e.printStackTrace();
                }
            }
            if (sim1 != null && sim1.equals("1")) {

                GeneralDialogFragment dialogFragment = GeneralDialogFragment.newInstance(new PermissionDialogVM(
                        "خطا", "دستگاه قادر به گرفتن IMEI موبایل شما نبوده است، لطفا با مرکز تماس بگیرید.", "بستن",
                        v1 -> getActivity().finish()
                ));
                dialogFragment.setCancelable(false);
                dialogFragment.show(getFragment().getFragmentManager(), "");

               /* AlertDialog.Builder builder;
                builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("خطا")
                        .setMessage("دستگاه قادر به گرفتن IMEI موبایل شما نبود")
                        .setPositiveButton("باشه", (dialog, which) -> {

                        })
                        .show();*/
            } else {
                showLoader();
                loginModel.getUserFromServer(sim1);
            }
        };

    }

    //for parcel
    public LoginVM(Parcel in) {
        super(in);
    }

    public static Creator<LoginVM> CREATOR = new Creator<LoginVM>() {
        @Override
        public LoginVM createFromParcel(Parcel source) {
            return new LoginVM(source);
        }

        @Override
        public LoginVM[] newArray(int size) {
            return new LoginVM[size];
        }
    };
}

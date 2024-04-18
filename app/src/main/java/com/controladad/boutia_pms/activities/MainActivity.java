package com.controladad.boutia_pms.activities;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import androidx.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.PermissionChecker;
import androidx.appcompat.app.AppCompatActivity;

import com.controladad.boutia_pms.R;
import com.controladad.boutia_pms.databinding.ActivityMainBinding;
import com.controladad.boutia_pms.fragments.GeneralDialogFragment;
import com.controladad.boutia_pms.location.CheckForLocation;
import com.controladad.boutia_pms.location.LocationManager;
import com.controladad.boutia_pms.network.dagger.ContextModule;
import com.controladad.boutia_pms.network.dagger.DaggerRetrofitProviderComponent;
import com.controladad.boutia_pms.network.dagger.RetrofitAPIServiceModule;
import com.controladad.boutia_pms.network.dagger.RetrofitProviderComponent;
import com.controladad.boutia_pms.utility.BoutiaApplication;
import com.controladad.boutia_pms.utility.Constants;
import com.controladad.boutia_pms.view_models.MainActivityVM;
import com.controladad.boutia_pms.view_models.PermissionDialogVM;
import com.controladad.boutia_pms.view_models.items_view_models.ImagesRecyclerViewDataModel;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.tasks.Task;

import javax.inject.Inject;

import lombok.Getter;
import lombok.Setter;
import timber.log.Timber;

import static com.controladad.boutia_pms.utility.Constants.BASE_URL;
import static com.controladad.boutia_pms.utility.Constants.READ_PHONE_STATE_PERMISSION;

public class MainActivity extends AppCompatActivity {

    @Inject
    MainActivityVM mainActivityVM;
    private static final String MAIN_ACTIVITY_VM_PARCEL_KEY = "main_activity_vm_parcel_key";
    @Setter
    private ImagesRecyclerViewDataModel imagesRecyclerViewDataModel;

    @Getter
    private RetrofitProviderComponent component;
    AlertDialog.Builder builder;
    androidx.appcompat.app.AlertDialog gpsChecker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BoutiaApplication.INSTANCE.setMainActivity(this);
        BoutiaApplication.INSTANCE.getAppComponents().inject(this);
        if (savedInstanceState != null)
            mainActivityVM = savedInstanceState.getParcelable(MAIN_ACTIVITY_VM_PARCEL_KEY);
        ActivityMainBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        binding.setMainActivityVM(mainActivityVM);
        mainActivityVM.onCreateActivity(getIntent().getStringExtra(Constants.NOTIFICATION_INTENT_KEY));

        //This Alert Dialog is for the permissions
        builder = new AlertDialog.Builder(this);

        Timber.plant(new Timber.DebugTree());
        component = DaggerRetrofitProviderComponent
                .builder()
                .contextModule(new ContextModule(this))
                .retrofitAPIServiceModule(new RetrofitAPIServiceModule(BASE_URL))
                .build();

        Task<LocationSettingsResponse> task = LocationManager.getTask(this);

        task.addOnSuccessListener(this, locationSettingsResponse -> Timber.d("Yay"));

        task.addOnFailureListener(this, e -> {

            if (e instanceof ResolvableApiException) {

                try {
                    ResolvableApiException resolvableApiException = (ResolvableApiException) e;
                    resolvableApiException.startResolutionForResult(MainActivity.this, 1);
                    Timber.d("Wr are doomed :(");
                } catch (IntentSender.SendIntentException sendEx) {
                    sendEx.printStackTrace();
                }
            }

        });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.ADD_IMAGE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            if (imagesRecyclerViewDataModel != null)
                imagesRecyclerViewDataModel.saveImage(data);
        }
    }

    @Override
    public void onBackPressed() {
        mainActivityVM.onBackPressed();
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        mainActivityVM.onResumeFragment();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();
        checkPermissions();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mainActivityVM.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (gpsChecker == null) {
            Timber.d("Dialog: onResume(): gpsChecker is null");
            gpsChecker = CheckForLocation.gpsCheck(this);
            if (gpsChecker != null) {
                Timber.d("Dialog: onResume(): gpsChecker is null and gpsChecker created and is being shown for the first time");
                gpsChecker.show();
            }

        } else {
            Timber.d("Dialog: onResume(): gpsChecker is not null");
            if (!gpsChecker.isShowing() && !CheckForLocation.checkIfGpsIsEnabled(this)) {
                Timber.d("Dialog: onResume(): gpsChecker is not null and gps is not showing and location is not enabled");
                gpsChecker.show();
            } else if (gpsChecker.isShowing() && CheckForLocation.checkIfGpsIsEnabled(this)) {
                gpsChecker.dismiss();
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(MAIN_ACTIVITY_VM_PARCEL_KEY, mainActivityVM);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {

            case READ_PHONE_STATE_PERMISSION:

                String permission = permissions[0];

                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {


                } else {
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R){
                        if (!Environment.isExternalStorageManager()) {
                            Intent intent = new Intent();
                            intent.setAction(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                            Uri uri = Uri.fromParts("package", this.getPackageName(), null);
                            intent.setData(uri);
                            startActivity(intent);
                        }
                    }
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {

                        final boolean showRational = shouldShowRequestPermissionRationale(permission);

                        if (!showRational) {

                            GeneralDialogFragment dialogFragment = GeneralDialogFragment.newInstance(new PermissionDialogVM(
                                    "درخواست مجوز دسترسی", "جهت استفاده از نرم افزار لطفا مجوز دسترسی را تایید نمایید.", "بستن",
                                    v -> {
                                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        Uri uri = Uri.fromParts("package", getPackageName(), null);
                                        intent.setData(uri);
                                        startActivity(intent);
                                    }
                            ));
                            dialogFragment.setCancelable(false);
                            dialogFragment.show(getSupportFragmentManager(), "");


                        } else {

                            GeneralDialogFragment dialogFragment = GeneralDialogFragment.newInstance(new PermissionDialogVM(
                                    "درخواست مجوز دسترسی", "جهت استفاده از نرم افزار لطفا مجوز دسترسی را تایید نمایید.", "بستن",
                                    v -> {
                                        ActivityCompat.requestPermissions(this
                                                , new String[]{Manifest.permission.READ_PHONE_STATE,
                                                        Manifest.permission.ACCESS_FINE_LOCATION,
                                                        Manifest.permission.ACCESS_COARSE_LOCATION,
                                                        Manifest.permission.CAMERA,
                                                        Manifest.permission.READ_EXTERNAL_STORAGE,
                                                        Manifest.permission.WRITE_EXTERNAL_STORAGE}
                                                , READ_PHONE_STATE_PERMISSION);
                                    }
                            ));
                            dialogFragment.setCancelable(false);
                            dialogFragment.show(getSupportFragmentManager(), "");

                        }


                    }

                }

                break;
        }
    }

    public void checkPermissions() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R){
            if (!Environment.isExternalStorageManager()) {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                Uri uri = Uri.fromParts("package", this.getPackageName(), null);
                intent.setData(uri);
                startActivity(intent);
            }
        }

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {

            if (PermissionChecker.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PermissionChecker.PERMISSION_GRANTED) {

                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_PHONE_STATE)) {

                    builder.setTitle("لطفا اجازه دسترسی بدهید")
                            .setMessage("برای استفاده از این نرم افزار شما مجبور به دادن اجازه هستید")
                            .setPositiveButton("باشه", (dialog, which) -> {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                                    ActivityCompat.requestPermissions(this
                                            , new String[]{Manifest.permission.READ_PHONE_STATE,
                                                    Manifest.permission.ACCESS_FINE_LOCATION,
                                                    Manifest.permission.ACCESS_COARSE_LOCATION,
                                                    Manifest.permission.CAMERA,
                                                    Manifest.permission.READ_EXTERNAL_STORAGE,
                                                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                                    Manifest.permission.MANAGE_EXTERNAL_STORAGE}
                                            , READ_PHONE_STATE_PERMISSION);
                                }
                                else
                                {
                                    ActivityCompat.requestPermissions(this
                                            , new String[]{Manifest.permission.READ_PHONE_STATE,
                                                    Manifest.permission.ACCESS_FINE_LOCATION,
                                                    Manifest.permission.ACCESS_COARSE_LOCATION,
                                                    Manifest.permission.CAMERA,
                                                    Manifest.permission.READ_EXTERNAL_STORAGE,
                                                    Manifest.permission.WRITE_EXTERNAL_STORAGE}
                                            , READ_PHONE_STATE_PERMISSION);
                                }
                            })
                            .setCancelable(false)
                            .show();

                } else {
                    ActivityCompat.requestPermissions(this
                            , new String[]{Manifest.permission.READ_PHONE_STATE,
                                    Manifest.permission.ACCESS_FINE_LOCATION,
                                    Manifest.permission.ACCESS_COARSE_LOCATION,
                                    Manifest.permission.CAMERA,
                                    Manifest.permission.READ_EXTERNAL_STORAGE,
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE}
                            , READ_PHONE_STATE_PERMISSION);
                }
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        mainActivityVM.onStop();
    }
}

package com.controladad.boutia_pms.view_models;


import android.app.ActivityManager;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import android.os.Parcel;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import androidx.fragment.app.FragmentManager;
import android.util.DisplayMetrics;
import android.view.View;

import com.controladad.boutia_pms.BR;
import com.controladad.boutia_pms.R;
import com.controladad.boutia_pms.activities.MainActivity;
import com.controladad.boutia_pms.fragment_navigation_handler.FragmentNavigator;
import com.controladad.boutia_pms.fragment_navigation_handler.FragmentRouter;
import com.controladad.boutia_pms.fragments.GeneralFragment;
import com.controladad.boutia_pms.fragments.HintFragment;
import com.controladad.boutia_pms.location.LocationService;
import com.controladad.boutia_pms.models.data_models.ProjectModel;
import com.controladad.boutia_pms.models.shared_preferences_models.SharedPreferencesModels;
import com.controladad.boutia_pms.utility.BoutiaApplication;
import com.controladad.boutia_pms.utility.Constants;
import com.controladad.boutia_pms.view_models.items_view_models.GeneralDataModel;
import com.controladad.boutia_pms.view_models.items_view_models.MissionsDataModel;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.inject.Inject;

import lombok.Getter;
import lombok.Setter;
import timber.log.Timber;

import static android.content.Context.ACTIVITY_SERVICE;
import static com.controladad.boutia_pms.utility.Constants.HOME_SCREEN_KEY;
import static com.controladad.boutia_pms.utility.Constants.USER_MODEL_PREFERENCES;


public class MainActivityVM extends BaseObservable implements Parcelable {
    @Inject
    Context context;
    @Getter
    @Setter
    private boolean shouldLoaderBeShown;
    public static int displayHeight;
    public static int displayWidth;
    public final String MAIN_ROUTER = "main_router_key";
    @Bindable
    @Getter
    String currentPageTitle;
    @Bindable
    @Getter
    String currentPageSubTitle;
    @Bindable
    @Getter
    private int leftIconSourceId;
    @Bindable
    @Getter
    private int rightIconSourceId;
    private boolean shouldTitleSet;
    private boolean shouldLeftIconSet;
    private boolean shouldRightIconSet;
    @Getter
    @Setter
    private GeneralFragment currentFragment;
    @Bindable
    @Getter
    private View.OnClickListener onLeftToolBarIconClickListener;
    @Bindable
    @Getter
    private View.OnClickListener onRightToolBarIconClickListener;
    @Bindable
    @Getter
    private int rightIconVisibility = View.VISIBLE;

    @Bindable
    @Getter
    private int toolbarVisibility = View.VISIBLE;
    @Getter
    @Setter
    private MissionsDataModel bazdidMission;
    @Setter
    @Getter
    private List<GeneralDataModel> reportViewItems;
    private SharedPreferencesModels.SharedPreferencesUserModel sharedPreferencesUserModel;
    private ProgressDialog dialog;
    private HintFragment hintFragment;
    @Setter
    private boolean isBackDisable = false;

    private SharedPreferencesModels.SharedPreferencesUserModel preferencesUserModel;

    @Getter
    @Setter
    private boolean shouldGoToScanCodePage = false;


    public void setToolbarVisibility(int toolbarVisibility) {
        this.toolbarVisibility = toolbarVisibility;
        notifyPropertyChanged(BR.toolbarVisibility);
    }

    public void setRightIconVisibility(int rightIconVisibility) {
        this.rightIconVisibility = rightIconVisibility;
        notifyPropertyChanged(BR.rightIconVisibility);
    }

    public void setOnLeftToolBarIconClickListener(View.OnClickListener onLeftToolBarIconClickListener) {
        this.onLeftToolBarIconClickListener = onLeftToolBarIconClickListener;
        notifyPropertyChanged(BR.onLeftToolBarIconClickListener);
    }

    public void setOnRightToolBarIconClickListener(View.OnClickListener onRightToolBarIconClickListener) {
        this.onRightToolBarIconClickListener = onRightToolBarIconClickListener;
        notifyPropertyChanged(BR.onRightToolBarIconClickListener);
    }

    private Map<String, FragmentRouter> routerMap = new HashMap<>();

    public FragmentRouter getRouter(String key) {
        if (!routerMap.containsKey(key)) {
            FragmentRouter router = new FragmentRouter();
            routerMap.put(key, router);
        }
        return routerMap.get(key);
    }


    private FragmentManager getFragmentManager() {
        if (getActivity() != null)
            return getActivity().getSupportFragmentManager();
        return null;
    }

    public MainActivity getActivity() {
        return BoutiaApplication.INSTANCE.getMainActivity();
    }


    public void onCreateActivity(String notificationKey) {
        getHeightAndWidthOfDisplay();
        if (currentFragment == null) {
            routerMap.clear();
            getSharedPreferences();
            if (sharedPreferencesUserModel != null) {
                getRouter(MAIN_ROUTER).navigateTo(HOME_SCREEN_KEY, new HomeVM());
            } else {
                //for Others ->
                getRouter(MAIN_ROUTER).navigateTo(Constants.LOGIN_SCREEN_KEY, new LoginVM());
                //for saavy ->
                //getRouter(MAIN_ROUTER).navigateTo(HOME_SCREEN_KEY, new HomeVM());
            }
        }

        if (currentFragment != null && Objects.equals(notificationKey, Constants.NOTIFICATION_INTENT_VALUE)) {
            getRouter(MAIN_ROUTER).navigateTo(HOME_SCREEN_KEY, new HomeVM());
        }

        for (String key : routerMap.keySet()) {
            FragmentRouter router = routerMap.get(key);
            router.setNavigator(new FragmentNavigator(getActivity().getSupportFragmentManager(),
                    R.id.fragment_container, key));
        }
    }

    void setTitleId(int titleId) {
        if (getActivity() != null) {
            this.currentPageTitle = context.getResources().getString(titleId);
            notifyPropertyChanged(BR.currentPageTitle);
        } else shouldTitleSet = true;
    }

    void setCurrentPageSubTitle() {
        getUserSharedPreferences();
        String version = "";
        try {
            PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            version = pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        String groupName = "";
        String uid = "";

        if (preferencesUserModel != null){
            groupName = preferencesUserModel.getGroupName();
            uid = preferencesUserModel.getUid();
        }

        if (getActivity() != null) {
            this.currentPageSubTitle = (groupName != null? groupName :"") + " , " + (uid != null? uid :"") + " , " + version ;
            notifyPropertyChanged(BR.currentPageSubTitle);
        }
    }

    void getUserSharedPreferences(){
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
    }

    public void setLeftIconSourceId(int leftIconSourceId) {
        if (getActivity() != null) {
            this.leftIconSourceId = leftIconSourceId;
            notifyPropertyChanged(BR.leftIconSourceId);
        } else shouldLeftIconSet = true;
    }

    public void setRightIconSourceId(int rightIconSourceId) {
        if (getActivity() != null) {
            this.rightIconSourceId = rightIconSourceId;
            notifyPropertyChanged(BR.rightIconSourceId);
        } else shouldRightIconSet = true;
    }


    public void showLoader() {
        dialog = ProgressDialog.show(getActivity(), "",
                "در حال بارگذاری لطفا شکیبا باشید...", true);
        dialog.show();
    }

    public void hideLoader() {
        if (dialog != null)
            dialog.dismiss();

    }



    public void onBackPressed() {
        if (!isBackDisable){
            if (getRouter(MAIN_ROUTER).getChainOrder() < 2) {
                if (isServiceRunning()) {
                    MissionStopDialogVM missionStopDialogVM = new MissionStopDialogVM(() -> {
                        Intent intent = new Intent(getActivity(), LocationService.class);
                        getActivity().stopService(intent);
                        currentFragment = null;
                        routerMap.clear();

                        NotificationManager notificationManager = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);

                        if (notificationManager != null) {
                            notificationManager.cancelAll();
                        }

                        getActivity().finish();
                    });
                    missionStopDialogVM.setOnCancelClicked(() -> {
                        currentFragment = null;
                        routerMap.clear();
                        getActivity().finish();
                    });
                    getRouter(MAIN_ROUTER).navigateToDialogFragment("", missionStopDialogVM);
                } else {
                    currentFragment = null;
                    routerMap.clear();
                    getActivity().finish();
                }


            } else
                getRouter(MAIN_ROUTER).exit();
        }

    }

    public void onResumeFragment() {
        for (String key : routerMap.keySet()) {
            FragmentRouter router = routerMap.get(key);
            if (router.getNavigator() == null)
                router.setNavigator(new FragmentNavigator(getActivity().getSupportFragmentManager(),
                        R.id.fragment_container, key));
        }
    }

    public void onPause() {
        for (String key : routerMap.keySet()) {
            FragmentRouter router = routerMap.get(key);
            router.removeNavigator();
        }
    }

    public void onStop() {
    }


    private void getHeightAndWidthOfDisplay() {
        DisplayMetrics dm = new DisplayMetrics();
        if (getActivity() != null)
            getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        displayHeight = dm.heightPixels;
        displayWidth = dm.widthPixels;
    }

    private void getSharedPreferences() {

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        Gson gson = new GsonBuilder().create();

        //    if (sharedPreferencesUserModel == null) {
        String value = preferences.getString(USER_MODEL_PREFERENCES, "");

        if (value.equals("")) {
            sharedPreferencesUserModel = null;
        } else {
            sharedPreferencesUserModel = gson.fromJson(value, SharedPreferencesModels.SharedPreferencesUserModel.class);
        }
        //     }

    }

    private boolean isServiceRunning() {
        ActivityManager manager = (ActivityManager) getActivity().getSystemService(ACTIVITY_SERVICE);
        if (manager != null) {
            for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
                if (LocationService.class.getName().equals(service.service.getClassName())) {
                    return true;
                }
            }
        }
        return false;
    }

    private HintFragment getHintFragment(String avatarText) {
        if (getActivity() != null) {
            if (hintFragment == null) {
                hintFragment = HintFragment.newInstance(avatarText);
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .add(R.id.hint_fragment_container, hintFragment)
                        .detach(hintFragment)
                        .commit();
            }
            hintFragment.setHintText(avatarText);
        }

        return hintFragment;
    }

    void showHint(String hintText) {
        if (getHintFragment(hintText) != null && getFragmentManager() != null) {
            getFragmentManager()
                    .beginTransaction()
                    .detach(getHintFragment(hintText))
                    .attach(getHintFragment(hintText))
                    .commit();
        }
    }

    void hideHint() {
        try {
            if (hintFragment != null && getFragmentManager() != null) {
                getFragmentManager()
                        .beginTransaction()
                        .detach(hintFragment)
                        .commit();
            }
        } catch (IllegalStateException e) {
            Timber.d(e.getMessage());
        }
    }

    //for parcel

    public static final Creator<MainActivityVM> CREATOR = new MainActivityVMCreator();

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(routerMap.size());
        for (String key : routerMap.keySet()) {
            dest.writeString(key);
            dest.writeParcelable(routerMap.get(key), flags);
        }
        Gson gson = new GsonBuilder().create();
        dest.writeString(gson.toJson(reviewModel));
        dest.writeString(gson.toJson(reviewModelHelper));
    }

    @Setter
    private ProjectModel.ReviewModel reviewModel;
    @Setter
    private ProjectModel.ReviewModel reviewModelHelper;

    public ProjectModel.ReviewModel getReviewModel() {
        if (reviewModel == null)
            reviewModel = ProjectModel.ReviewModel.Companion.create();
        return reviewModel;
    }

    public ProjectModel.ReviewModel getReviewModelHelper() {
        if (reviewModelHelper == null)
            reviewModelHelper = ProjectModel.ReviewModel.Companion.helperCreate();
        return reviewModelHelper;
    }


    public static class MainActivityVMCreator implements Creator<MainActivityVM> {

        @Inject
        MainActivityVM mainActivityVM;

        @Override
        public MainActivityVM createFromParcel(Parcel source) {
            BoutiaApplication.INSTANCE.getAppComponents().inject(this);
            int routerMapSize = source.readInt();
            for (int i = 0; i < routerMapSize; i++) {
                String key = source.readString();
                FragmentRouter router = source.readParcelable(FragmentRouter.class.getClassLoader());
                if (!mainActivityVM.routerMap.containsKey(key))
                    mainActivityVM.routerMap.put(key, router);
            }
            Gson gson = new GsonBuilder().create();
            mainActivityVM.reviewModel = gson.fromJson(source.readString(), ProjectModel.ReviewModel.class);
            mainActivityVM.reviewModelHelper = gson.fromJson(source.readString(), ProjectModel.ReviewModel.class);
            return mainActivityVM;
        }

        @Override
        public MainActivityVM[] newArray(int size) {
            return new MainActivityVM[size];
        }
    }
}

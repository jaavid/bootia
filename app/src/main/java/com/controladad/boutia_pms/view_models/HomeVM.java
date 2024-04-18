package com.controladad.boutia_pms.view_models;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import androidx.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Handler;
import android.os.Parcel;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.controladad.boutia_pms.R;
import com.controladad.boutia_pms.databinding.FragmentHomeBinding;
import com.controladad.boutia_pms.network.dagger.ContextModule;
import com.controladad.boutia_pms.network.dagger.DaggerRetrofitProviderComponent;
import com.controladad.boutia_pms.network.dagger.RetrofitAPIServiceModule;
import com.controladad.boutia_pms.network.dagger.RetrofitProviderComponent;
import com.controladad.boutia_pms.utility.Constants;

import java.util.Timer;
import java.util.TimerTask;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

import static android.content.Context.MODE_PRIVATE;
import static com.controladad.boutia_pms.utility.Constants.BASE_URL;

public class HomeVM extends GeneralVM {

    //private HomeModel homeModel;
    private Timer timer;
    private TimerTask timerTask;
    private Handler handler = new Handler();
    private int counter = 0;
    private SharedPreferences sharedPreferences;

    private Disposable disposable;
    private RetrofitProviderComponent component;

    public SharedPreferences getSharedPreferences() {
        if (sharedPreferences == null && getActivity() != null)
            sharedPreferences = getActivity().getSharedPreferences("Version", MODE_PRIVATE);
        return sharedPreferences;
    }

    public HomeVM() {
    }

    @Override
    public View binding(LayoutInflater inflater, @Nullable ViewGroup container) {
        FragmentHomeBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false);
        binding.setHomeVM(this);
        return binding.getRoot();
    }

    @Override
    public void onResumeFragment() {
        super.onResumeFragment();
        mainActivityVM.setToolbarVisibility(View.VISIBLE);
    }


    @Override
    public void onPauseFragment() {
        super.onPauseFragment();
        mainActivityVM.setOnLeftToolBarIconClickListener(null);

    }

    private void checkForUpdate() {
        component = DaggerRetrofitProviderComponent
                .builder()
                .contextModule(new ContextModule(context))
                .retrofitAPIServiceModule(new RetrofitAPIServiceModule(BASE_URL))
                .build();
        disposable = component.getVersionService()
                .getVersion()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(versionCheck -> {

                    try {
                        PackageInfo pi = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
                        String version = pi.versionName;
                        String[] curVer = version.split("\\.");
                        String[] sentVer = versionCheck.getVersion().split("\\.");
                        boolean shouldUpdate = false;
                        if (Integer.valueOf(curVer[0]) < Integer.valueOf(sentVer[0]) ||
                                Integer.valueOf(curVer[1]) < Integer.valueOf(sentVer[1]) ||
                                Integer.valueOf(curVer[2]) < Integer.valueOf(sentVer[2])) {
                            shouldUpdate = true;
                        }
                        if (shouldUpdate)
                            getRouter().navigateToDialogFragment("", new MissionStopDialogVM(this::installUpdate,
                                    "دانلود نسخه جدید برنامه", "نسخه جدید برنامه آماده دانلود است، آیا مایل به دانلود آن هستید؟"));
                    } catch (PackageManager.NameNotFoundException e) {
                        e.printStackTrace();
                    }
                }, throwable -> {
                    Timber.d("kehrs");
                });
    }

    private void installUpdate() {
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(getSharedPreferences().getString("url", "")));
        getActivity().startActivity(i);
    }

    @Override
    public void onCreateView() {
        super.onCreateView();
        //sharedPreferences = getActivity().getSharedPreferences("Version",Context.MODE_PRIVATE);
        getSharedPreferences();
        //checkForUpdate();
    }

    public View.OnClickListener onReviewClick() {
        return v -> getRouter().navigateTo(Constants.MISSION_CHOSE_SCREEN_KEY, new MissionChoseVM());
    }

    public View.OnClickListener onReportButtonClicked() {
        return v -> getRouter().navigateTo(Constants.REPORT_SCREEN_KEY, new ReportVM());
    }

    public View.OnClickListener onSettingButtonClicked() {
        return v -> getRouter().navigateTo(Constants.SETTING_SCREEN_KEY, new SettingVM());
    }

    public View.OnClickListener onRepairButtonClicked() {
        return v -> getRouter().navigateTo(Constants.REPAIR_TYPE_SCREEN_KEY, new RepairTypeChoseVM());
    }

    @Override
    public int getLeftIconSource() {
        return R.drawable.ic_sync;
    }

    @Override
    public int getRightIconSource() {
        return R.drawable.ic_user;

    }

    @Override
    public View.OnClickListener getOnRightToolBarIconClickListener() {
        return v -> getRouter().navigateTo(Constants.USER_PROFILE_KEY, new UserProfileVM("تست", "تستی", "09887776655", "راننده", "XXXXXXXX"));
    }

    @Override
    public View.OnClickListener getOnLeftToolBarIconClickListener() {
        return v -> {
            Timber.d("this is clicked");
            getRouter().navigateTo(Constants.DOWNLOAD_DIALOG_FRAGMENT_KEY, new DialogFragmentDownloadDataVM());

        };
    }


    @Override
    String getPageHint() {
        return context.getString(R.string.home_page_hint);
    }


    @Override
    public int getTitleId() {
        return R.string.boutia;
    }

    //for parcel
    public HomeVM(Parcel in) {
        super(in);
    }

    public static Creator<HomeVM> CREATOR = new Creator<HomeVM>() {
        @Override
        public HomeVM createFromParcel(Parcel source) {
            return new HomeVM(source);
        }

        @Override
        public HomeVM[] newArray(int size) {
            return new HomeVM[size];
        }
    };
}

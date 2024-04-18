package com.controladad.boutia_pms.view_models;

import android.content.SharedPreferences;
import androidx.databinding.Bindable;
import androidx.databinding.DataBindingUtil;
import android.os.Parcel;
import android.preference.PreferenceManager;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.core.content.ContextCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.controladad.boutia_pms.BR;
import com.controladad.boutia_pms.R;
import com.controladad.boutia_pms.databinding.DialogFragmentDownloadDataBinding;
import com.controladad.boutia_pms.models.HomeModel;
import com.controladad.boutia_pms.network.dagger.ContextModule;
import com.controladad.boutia_pms.network.dagger.DaggerRetrofitProviderComponent;
import com.controladad.boutia_pms.network.dagger.RetrofitAPIServiceModule;
import com.controladad.boutia_pms.network.dagger.RetrofitProviderComponent;
import com.controladad.boutia_pms.network.network_check.NoNetworkException;
import com.controladad.boutia_pms.utility.Constants;
import com.google.android.gms.common.internal.Objects;

import java.util.Calendar;
import java.util.concurrent.atomic.AtomicLong;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import lombok.Getter;
import lombok.Setter;
import retrofit2.HttpException;
import retrofit2.Response;
import saman.zamani.persiandate.PersianDate;
import timber.log.Timber;
import static com.controladad.boutia_pms.utility.Constants.DATE_TIME_SERVER_BASE_URL;

public class DialogFragmentDownloadDataVM extends GeneralVM {


    @Bindable
    @Setter
    @Getter
    int progressReview = 0;

    @Bindable
    @Setter
    @Getter
    int progressRepair = 0;

    @Bindable
    @Getter
    int btnMissionVisibility = View.GONE;

    @Bindable
    @Getter
    boolean btnUpdateEnable = false;

    private boolean backButtonEnabled = true;

    @Bindable
    @Getter
    String lblLastUpdateTime = "-";

    SharedPreferences preferences;

    private HomeModel homeModel = new HomeModel(getComponent());

    @Bindable
    @Getter
    private int loaderVisibility = View.VISIBLE;
    public void setLoaderVisibility(int loaderVisibility) {
        this.loaderVisibility = loaderVisibility;
        notifyPropertyChanged(BR.loaderVisibility);
    }

    @Bindable
    @Getter
    private int timeLabelVisibility = View.INVISIBLE;
    public void setTimeLabelVisibility(int timeLabelVisibility) {
        this.timeLabelVisibility = timeLabelVisibility;
        notifyPropertyChanged(BR.timeLabelVisibility);
    }

    @Bindable
    @Getter
    private String timeLabelText = "ساعت سیستم شما درست است.";
    public void setTimeLabelText(String timeLabelText) {
        this.timeLabelText = timeLabelText;
        notifyPropertyChanged(BR.timeLabelText);
    }

    @Bindable
    @Getter
    private int lableColor;
    public void setLableColor(int lableColor) {
        this.lableColor = lableColor;
        notifyPropertyChanged(BR.lableColor);
    }

    @Override
    public View binding(LayoutInflater inflater, @Nullable ViewGroup container) {
        DialogFragmentDownloadDataBinding binding = DataBindingUtil.inflate(inflater, R.layout.dialog_fragment_download_data,container,false);
        binding.setViewModel(this);
        return binding.getRoot();
    }

    public DialogFragmentDownloadDataVM() {

    }

    @Override
    public void onStartFragment() {
        super.onStartFragment();
        DialogFragment dialogFragment = (DialogFragment) getFragment();
        super.onStartFragment();
        if (dialogFragment.getDialog() == null) {
            return;
        }
        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        int h = dm.heightPixels;
        int w = dm.widthPixels;
        int dialogWidth = (5*w)/6;
        int dialogHeight;
        dialogHeight = (2*w)/3 ;

        dialogFragment.getDialog().getWindow().setLayout(dialogWidth, dialogHeight);
    }

    @Override
    public void onCreateView() {
        super.onCreateView();

        preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        lblLastUpdateTime = preferences.getString(Constants.LAST_UPDATE_PREFERENCES, "-");
        notifyPropertyChanged(BR.lblLastUpdateTime);
        timeNow();
        //isTimeValid();
       // ((DialogFragment)getFragment()).getDialog().setCanceledOnTouchOutside(false);
        //((DialogFragment)getFragment()).setCancelable(false);
    }
    public View.OnClickListener onUpdateClick() {
        return v ->{
            progressRepair = 0;
            progressReview = 0;
            btnUpdateEnable = false;
            backButtonEnabled = false;
            btnMissionVisibility = View.GONE;
            mainActivityVM.setBackDisable(true);
            notifyPropertyChanged(BR.btnUpdateEnable);
            notifyPropertyChanged(BR.progressRepair);
            notifyPropertyChanged(BR.progressReview);
            notifyPropertyChanged(BR.btnMissionVisibility);

            homeModel.getCurrentDateAndUpdateData(progressUpdateReview ,
                    progressUpdateRepair,
                    ()->{try {

                        //((DialogFragment) getFragment()).dismiss();
                        //getRouter().exit();
                        showSnackBarOnActivityView(context.getString(R.string.data_received_successfully));
                        btnMissionVisibility = View.VISIBLE;
                        notifyPropertyChanged(BR.btnMissionVisibility);
                        btnUpdateEnable = true;
                        notifyPropertyChanged(BR.btnUpdateEnable);
                        backButtonEnabled = true;
                        mainActivityVM.setBackDisable(false);

                        String currentTime = getCurrentDate();
                        preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
                        preferences.edit().putString(Constants.LAST_UPDATE_PREFERENCES, currentTime).apply();
                        lblLastUpdateTime = currentTime;
                        notifyPropertyChanged(BR.lblLastUpdateTime);

                    }catch (IllegalStateException e){}
                    },
                    ()->{try {
                        //((DialogFragment) getFragment()).dismiss();
                        //getRouter().exit();
                        showSnackBarOnActivityView(context.getString(R.string.data_receive_unsuccessfully));
                        btnUpdateEnable = true;
                        notifyPropertyChanged(BR.btnUpdateEnable);
                        backButtonEnabled = true;
                        mainActivityVM.setBackDisable(false);

                    }catch (IllegalStateException e){}
                    },
                    ()->{try {
                        //((DialogFragment) getFragment()).dismiss();
                        //getRouter().exit();
                        showSnackBarOnActivityView(context.getString(R.string.date_time_set));
                        btnUpdateEnable = true;
                        notifyPropertyChanged(BR.btnUpdateEnable);
                        backButtonEnabled = true;
                        mainActivityVM.setBackDisable(false);

                    }catch (IllegalStateException e){}
                    });

        };
    }


    private String getCurrentDate()
    {
        PersianDate currentDate = new PersianDate();
        int m = currentDate.getShMonth();
        String month;
        if (m < 10)
            month = "0" + m;
        else
            month = String.valueOf(m);
        int d = currentDate.getShDay();
        String day;
        if (d < 10)
            day = "0" + d;
        else
            day = String.valueOf(d);
        String currentDateString = currentDate.getShYear() + "/" +
                month + "/" + day + " " + currentDate.getHour() + ":" + currentDate.getMinute();
        return currentDateString;
    }

    private void isTimeValid(){
        //long tsLong = System.currentTimeMillis()/1000;
//        AtomicLong res = new AtomicLong();
//        RetrofitProviderComponent component = DaggerRetrofitProviderComponent
//                .builder()
//                .contextModule(new ContextModule(context))
//                .retrofitAPIServiceModule(new RetrofitAPIServiceModule(DATE_TIME_SERVER_BASE_URL))
//                .build();
//        Disposable disposable = component.getDateTimeService()
//                .getCurrentDateTime()
//                .subscribeOn(Schedulers.newThread())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(dateTimeModel -> {
//                    try {
//                        long serverTS = dateTimeModel.getUnixTime();
//                        res.set(serverTS - tsLong);
//                        Log.d("resssssssssssult",String.valueOf(res.get()));
//                        backButtonEnabled = true;
//                        mainActivityVM.setBackDisable(false);
//
//                        if (res.get() < 15 && res.get() > -15)
//                        {
//                            setLoaderVisibility(View.GONE);
//                            setTimeLabelVisibility(View.VISIBLE);
//                            setLableColor(ContextCompat.getColor(context,R.color.green_200));
//                            btnUpdateEnable = true;
//                            notifyPropertyChanged(BR.btnUpdateEnable);
//                        }
//                        else
//                        {
//                            setLoaderVisibility(View.GONE);
//                            setTimeLabelVisibility(View.VISIBLE);
//                            setLableColor(ContextCompat.getColor(context,R.color.red_200));
//                            setTimeLabelText("ساعت و یا تاریخ سیستم شما تنظیم نیست.\n برای ادامه کار ساعت و تاریخ گوشی خود را تنظیم کنید.");
//
//                        }
//                    } catch (Exception e) {
//                        e.printStackTrace();
//
//                        setLoaderVisibility(View.GONE);
//                        setTimeLabelVisibility(View.VISIBLE);
//                        setLableColor(ContextCompat.getColor(context,R.color.orange_200));
//                        setTimeLabelText("خطا در برقراری ارتباط با سرور زمان و تاریخ.\n لطفا از درست بودن ساعت و تاریخ گوشی خود اطمینان حاصل کنید.");
//                        btnUpdateEnable = true;
//                        notifyPropertyChanged(BR.btnUpdateEnable);
//                    }
//                }, throwable -> {
//                    Timber.d("kehrs");
//                    setLoaderVisibility(View.GONE);
//                    setTimeLabelVisibility(View.VISIBLE);
//                    setLableColor(ContextCompat.getColor(context,R.color.orange_200));
//                    setTimeLabelText("خطا در برقراری ارتباط با سرور زمان و تاریخ.\n لطفا از درست بودن ساعت و تاریخ گوشی خود اطمینان حاصل کنید.");
//                    btnUpdateEnable = true;
//                    notifyPropertyChanged(BR.btnUpdateEnable);
//                });



    }

    private void timeNow()
    {
        final String[] result = {""};

        RetrofitProviderComponent component = getComponent() ;
        Disposable disposable = component.getService().getTime()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(mission -> {
                    Log.d("sdsd",mission.get(1));
                    result[0] = mission.get(1);
                    long tsLong = System.currentTimeMillis()/1000;
                    AtomicLong res = new AtomicLong();
                    if (!Objects.equal(result[0],""))
                    {
                        long serverTS = Long.parseLong(result[0]);
                        Log.d("sdsd", (String.valueOf(tsLong)));
                        res.set(serverTS - tsLong);

                        Log.d("sdsd",String.valueOf(res.get()));
                        backButtonEnabled = true;
                        mainActivityVM.setBackDisable(false);

                        if (res.get() < 900 && res.get() > -900) {
                            setLoaderVisibility(View.GONE);
                            setTimeLabelVisibility(View.VISIBLE);
                            setLableColor(ContextCompat.getColor(context,R.color.green_200));
                            btnUpdateEnable = true;
                            notifyPropertyChanged(BR.btnUpdateEnable);

                        }
                        else
                        {
                            setLoaderVisibility(View.GONE);
                            setTimeLabelVisibility(View.VISIBLE);
                            setLableColor(ContextCompat.getColor(context, R.color.red_200));
                            setTimeLabelText("ساعت و یا تاریخ سیستم شما تنظیم نیست.\n برای ادامه کار ساعت و تاریخ گوشی خود را تنظیم کنید.");
                        }
                    }
                    else
                    {
                        setLoaderVisibility(View.GONE);
                        setTimeLabelVisibility(View.VISIBLE);
                        setLableColor(ContextCompat.getColor(context,R.color.orange_200));
                        setTimeLabelText("خطا در برقراری ارتباط با سرور زمان و تاریخ.\n لطفا از درست بودن ساعت و تاریخ گوشی خود اطمینان حاصل کنید.");
                        btnUpdateEnable = true;
                        notifyPropertyChanged(BR.btnUpdateEnable);
                    }


                }, throwable -> {
                    setLoaderVisibility(View.GONE);
                    setTimeLabelVisibility(View.VISIBLE);
                    setLableColor(ContextCompat.getColor(context,R.color.orange_200));
                    setTimeLabelText("خطا در برقراری ارتباط با سرور زمان و تاریخ.\n لطفا از درست بودن ساعت و تاریخ گوشی خود اطمینان حاصل کنید.");
                    btnUpdateEnable = true;
                    notifyPropertyChanged(BR.btnUpdateEnable);

                    if (throwable instanceof HttpException) {

                        Response response = ((HttpException) throwable).response();

                        switch (response.code()) {

                            case 401:

                                break;

                            case 400:

                                break;

                            case 403:

                                break;
                        }

                    } else if (throwable instanceof NoNetworkException) {
                        Timber.d(throwable.getMessage());
                    } else {
                        Timber.d(throwable.getMessage());
                    }
                }, () -> {

                });
    }


    public View.OnClickListener onNavigateToMissionClick(){
        return v -> mainActivityVM.getRouter(mainActivityVM.MAIN_ROUTER).navigateTo("",
                new RepairMissionChoseVM("repair",false));

    }

    public View.OnClickListener onNavigateToReviewMissionClick(){
        return v -> getRouter().navigateTo(Constants.MISSION_CHOSE_SCREEN_KEY, new MissionChoseVM());


    }

    @Override
    public int getLeftIconSource() {
        return R.drawable.ic_back;
    }

    @Override
    public View.OnClickListener getOnLeftToolBarIconClickListener() {
        return v -> {
            Timber.d("this is clicked");
            if (backButtonEnabled)
                getRouter().exit();
            else
                showSnackBar("لطفا تا پایان گرفتن ماموریت ها صبر کنید.");
        };
    }

    private GenericMethod<Integer> progressUpdateRepair = integer -> {
        progressRepair = integer;
        notifyPropertyChanged(BR.progressRepair);
    };

    private GenericMethod<Integer> progressUpdateReview = integer -> {
        progressReview = integer;
        notifyPropertyChanged(BR.progressReview);
    };

    //for parcel

    public DialogFragmentDownloadDataVM(Parcel in) {
        super(in);
    }

    public static Creator<DialogFragmentDownloadDataVM> CREATOR = new Creator<DialogFragmentDownloadDataVM>() {
        @Override
        public DialogFragmentDownloadDataVM createFromParcel(Parcel source) {
            return new DialogFragmentDownloadDataVM(source);
        }

        @Override
        public DialogFragmentDownloadDataVM[] newArray(int size) {
            return new DialogFragmentDownloadDataVM[size];
        }
    };
}

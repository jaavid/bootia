package com.controladad.boutia_pms.view_models;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import androidx.databinding.Bindable;
import androidx.databinding.DataBindingUtil;
import android.os.Build;
import android.os.IBinder;
import android.os.Parcel;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.controladad.boutia_pms.BR;
import com.controladad.boutia_pms.R;
import com.controladad.boutia_pms.adapters.ReportAdapter;
import com.controladad.boutia_pms.databinding.FragmentReportBinding;
import com.controladad.boutia_pms.models.CallBackMethod;
import com.controladad.boutia_pms.models.ReportModel;
import com.controladad.boutia_pms.network.UploadService;
import com.controladad.boutia_pms.utility.Constants;
import com.controladad.boutia_pms.view_models.items_view_models.GeneralDataModel;
import com.controladad.boutia_pms.view_models.items_view_models.RepairReportDataModel;
import com.controladad.boutia_pms.view_models.items_view_models.ReportItemDataModel;
import com.controladad.boutia_pms.view_models.items_view_models.TrackReportItemDataModel;

import java.util.Objects;

import lombok.Getter;
import timber.log.Timber;

import static android.content.Context.ACTIVITY_SERVICE;

public class ReportVM extends GeneralVM {

    @Getter
    private ReportAdapter adapter = new ReportAdapter();
    @Getter
    private ReportModel reportModel;
    @Bindable
    @Getter
    private int textVisibility = View.GONE;
    private Intent intent;
    private boolean isServiceBound;
    private UploadService uploadService;
    private Function function;

    public ReportVM() {
        super();
    }

    @Override
    public View binding(LayoutInflater inflater, @Nullable ViewGroup container) {
        FragmentReportBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_report, container, false);
        binding.setReportVM(this);
        return binding.getRoot();
    }

    @Override
    public void onCreateView() {
        super.onCreateView();
        intent = new Intent(getActivity(), UploadService.class);
        reportModel = new ReportModel();

        if (isServiceRunning()) {
            getActivity().bindService(intent, serviceConnection, Context.BIND_ADJUST_WITH_ACTIVITY);
            function = () -> {
                if (adapter.getItemsModelList().isEmpty())
                    reportModel.getDataFromDatabase(itemManipulation(), () -> changeTextVisibility(false));
            };
        } else {

            if (adapter.getItemsModelList().isEmpty())
                reportModel.getDataFromDatabase(itemManipulation(), () -> changeTextVisibility(false));
        }

        mainActivityVM.setReportViewItems(adapter.getItemsModelList());
    }

    private boolean isServiceRunning() {
        ActivityManager manager = (ActivityManager) getActivity().getSystemService(ACTIVITY_SERVICE);
        if (manager != null) {
            for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
                if (UploadService.class.getName().equals(service.service.getClassName())) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void onDestroyFragment() {
        super.onDestroyFragment();
        mainActivityVM.setReportViewItems(null);
    }

    private void changeTextVisibility(boolean notShow) {
        if (notShow) {
            textVisibility = View.GONE;
            notifyPropertyChanged(BR.textVisibility);
        } else if(adapter.getItemsModelList().isEmpty()){
            textVisibility = View.VISIBLE;
            notifyPropertyChanged(BR.textVisibility);
        }
    }

    private void addItem(GeneralDataModel dataModel) {
        adapter.getItemsModelList().add(dataModel);
        try {
            adapter.notifyItemInserted(adapter.getItemsModelList().size() - 1);
        } catch (IllegalStateException e) {
            adapter.setHasItemsChanged(true);
        }
    }

    private GenericMethod<GeneralDataModel> itemManipulation() {
        return dataModel -> {
            changeTextVisibility(true);
            if (dataModel instanceof RepairReportDataModel) {
                RepairReportDataModel repairReportDataModel = (RepairReportDataModel) dataModel;
                if (isServiceBound && uploadService.getRepairMissionListTracker().contains(repairReportDataModel.getReviewDate())) {
                    repairReportDataModel.setSending(true);
                    repairReportDataModel.setReportButtonVisibility(View.GONE);
                }
                if(repairReportDataModel.isRootin()){
                    repairReportDataModel.setOnSendDataClickListener(v -> {
                        repairReportDataModel.setSending(true);
                        repairReportDataModel.setReportButtonVisibility(View.GONE);
                        intent.putExtra("date", repairReportDataModel.getReviewDate());
                        intent.putExtra("nid", repairReportDataModel.getNId());
                        intent.putExtra("type", 0);
                        intent.putExtra("isBound", isServiceBound);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            getActivity().startForegroundService(intent);
                        } else {

                            getActivity().startService(intent);
                        }
                        getActivity().bindService(intent, serviceConnection, Context.BIND_ADJUST_WITH_ACTIVITY);
                    });
                }
                else {
                    repairReportDataModel.setOnSendDataClickListener(v -> {
                        repairReportDataModel.setSending(true);
                        repairReportDataModel.setReportButtonVisibility(View.GONE);
                        intent.putExtra("date", repairReportDataModel.getReviewDate());
                        intent.putExtra("nid", repairReportDataModel.getNId());
                        intent.putExtra("type", 3);
                        intent.putExtra("isBound", isServiceBound);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            getActivity().startForegroundService(intent);
                        } else {

                            getActivity().startService(intent);
                        }
                        getActivity().bindService(intent, serviceConnection, Context.BIND_ADJUST_WITH_ACTIVITY);
                    });
                }
            } else if (dataModel instanceof ReportItemDataModel) {
                ReportItemDataModel reportItemDataModel = (ReportItemDataModel) dataModel;
                if (isServiceBound && uploadService.getMissionListTracker().contains(reportItemDataModel.getReviewDate())) {
                    reportItemDataModel.setSending(true);
                    reportItemDataModel.setReportButtonVisibility(View.GONE);
                }
                reportItemDataModel.setOnSendDataClickListener(v -> {
                    reportItemDataModel.setSending(true);
                    reportItemDataModel.setReportButtonVisibility(View.GONE);
                    intent.putExtra("date", reportItemDataModel.getReviewDate());
                    intent.putExtra("mid", reportItemDataModel.getMid());
                    intent.putExtra("type", 1);
                    intent.putExtra("isBound", isServiceBound);
                    Timber.d("Click(): Send Data To The IntentService");
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        getActivity().startForegroundService(intent);
                    } else {

                        getActivity().startService(intent);
                    }
                    getActivity().bindService(intent, serviceConnection, Context.BIND_ADJUST_WITH_ACTIVITY);
                });
            } else if (dataModel instanceof TrackReportItemDataModel) {
                TrackReportItemDataModel trackReportItemDataModel = (TrackReportItemDataModel) dataModel;
                if (isServiceBound && uploadService.getTrackList().contains(trackReportItemDataModel.getMid())) {
                    trackReportItemDataModel.setSending(true);
                    trackReportItemDataModel.setReportTrackButtonVisibility(View.GONE);
                }
                trackReportItemDataModel.setOnSendCordinationClickListener(v -> {
                    trackReportItemDataModel.setSending(true);
                    trackReportItemDataModel.setReportTrackButtonVisibility(View.GONE);
                    intent.putExtra("mid", Integer.valueOf(trackReportItemDataModel.getMid()));
                    intent.putExtra("type", 2);
                    intent.putExtra("isBound", isServiceBound);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        getActivity().startForegroundService(intent);
                    } else {
                        getActivity().startService(intent);
                    }
                    getActivity().bindService(intent, serviceConnection, Context.BIND_ADJUST_WITH_ACTIVITY);
                });
            }
            addItem(dataModel);
        };
    }

    private CallBackMethod<String> whenDataSentCallback() {

        return new CallBackMethod<String>() {

            @Override
            public void callBackIfSuccessFull(String callback, Boolean isMission) {

                    if (isMission) {
                        if (mainActivityVM.getReportViewItems() != null) {
                            for (GeneralDataModel dataModel : mainActivityVM.getReportViewItems()) {
                                if (dataModel instanceof ReportItemDataModel) {
                                    ReportItemDataModel itemDataModel = (ReportItemDataModel) dataModel;
                                    if (Objects.equals(itemDataModel.getReviewDate(), callback)) {
                                        itemDataModel.setReportButtonVisibility(View.VISIBLE);
                                        itemDataModel.setSubmitted(Constants.SENT_STATE);
                                        itemDataModel.setState(Constants.SENT_STATE);
                                        itemDataModel.setOnSendDataClickListener(null);
                                    }
                                }
                            }
                        }
                    } else {

                        if (mainActivityVM.getReportViewItems() != null)
                            for (GeneralDataModel dataModel : mainActivityVM.getReportViewItems())
                                if (dataModel instanceof TrackReportItemDataModel) {
                                    TrackReportItemDataModel itemDataModel = (TrackReportItemDataModel) dataModel;
                                    if (Objects.equals(String.valueOf(itemDataModel.getMid()), callback) && itemDataModel.getState() != Constants.ERROR_STATE) {
                                        itemDataModel.setReportTrackButtonVisibility(View.VISIBLE);
                                        itemDataModel.setState(Constants.SENT_STATE);
                                        itemDataModel.setOnSendCordinationClickListener(null);
                                    }
                                }
                    }
            }


            @Override
            public void callBackIfFailed(String callback, Boolean isMission) {
                    if (isMission) {

                        if (mainActivityVM.getReportViewItems() != null)
                            for (GeneralDataModel dataModel : mainActivityVM.getReportViewItems()) {
                                if (dataModel instanceof ReportItemDataModel) {
                                    ReportItemDataModel itemDataModel = (ReportItemDataModel) dataModel;
                                    if (Objects.equals(itemDataModel.getReviewDate(), callback)) {
                                        itemDataModel.setState(Constants.ERROR_STATE);
                                        itemDataModel.setReportButtonVisibility(View.VISIBLE);
                                        itemDataModel.setSending(false);
                                    }
                                }
                            }
                    } else {

                        if (mainActivityVM.getReportViewItems() != null)
                            for (GeneralDataModel dataModel : mainActivityVM.getReportViewItems())
                                if (dataModel instanceof TrackReportItemDataModel) {
                                    TrackReportItemDataModel itemDataModel = (TrackReportItemDataModel) dataModel;
                                    if (Objects.equals(String.valueOf(itemDataModel.getMid()), callback)) {
                                        itemDataModel.setState(Constants.ERROR_STATE);
                                        itemDataModel.setReportTrackButtonVisibility(View.VISIBLE);
                                        itemDataModel.setSending(false);
                                    }
                                }
                    }

            }

        };
    }


    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            UploadService.MyBinder myBinder = (UploadService.MyBinder) service;
            uploadService = myBinder.getService();
            uploadService.setCallBackMethod(whenDataSentCallback());
            isServiceBound = true;
            if (function != null)
                function.fun();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isServiceBound = false;
        }
    };


    @Override
    public int getLeftIconSource() {
        return R.drawable.ic_back;
    }

    @Override
    public int getRightIconSource() {
        return R.drawable.ic_upload;
    }

    @Override
    public View.OnClickListener getOnRightToolBarIconClickListener() {
        return v -> {
            for (GeneralDataModel generalDataModel : adapter.getItemsModelList()) {
                if (generalDataModel instanceof ReportItemDataModel) {
                    ReportItemDataModel dataModel = (ReportItemDataModel) generalDataModel;
                    if (!dataModel.isSending())
                        dataModel.onSendDataClickListener().onClick(null);
                } else if (generalDataModel instanceof TrackReportItemDataModel) {
                    TrackReportItemDataModel dataModel = (TrackReportItemDataModel) generalDataModel;
                    if (!dataModel.isSending())
                        dataModel.onSendCordinationClickListener().onClick(null);
                }
            }
        };
    }

    @Override
    String getPageHint() {
        return context.getString(R.string.report_page_hint);
    }

    //for parcel
    public ReportVM(Parcel in) {
        super(in);
    }

    public final static Creator<ReportVM> CREATOR = new Creator<ReportVM>() {
        @Override
        public ReportVM createFromParcel(Parcel source) {
            return new ReportVM(source);
        }

        @Override
        public ReportVM[] newArray(int size) {
            return new ReportVM[size];
        }
    };


}

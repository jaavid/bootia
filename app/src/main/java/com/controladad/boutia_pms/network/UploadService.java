package com.controladad.boutia_pms.network;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.controladad.boutia_pms.R;
import com.controladad.boutia_pms.dagger.AppComponents;
import com.controladad.boutia_pms.dagger.DaggerAppComponents;
import com.controladad.boutia_pms.dagger.NotificationModule;
import com.controladad.boutia_pms.models.CallBackMethod;
import com.controladad.boutia_pms.models.ReportModel;
import com.controladad.boutia_pms.network.dagger.ContextModule;
import com.controladad.boutia_pms.network.dagger.DaggerRetrofitProviderComponent;
import com.controladad.boutia_pms.network.dagger.RetrofitAPIServiceModule;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;

import static com.controladad.boutia_pms.utility.Constants.BASE_URL;


public class UploadService extends Service {

    private final static int NOTIFICATION_ID = 2300;
    private IBinder iBinder = new MyBinder();
    private CallBackMethod<String> callBackMethod;
    private boolean isServiceStarted = false;
    private List<Intent> intentList = new ArrayList<>();
    private List<Intent> itemsToRemove = new ArrayList<>();
    private ReportModel reportModel;
    @Getter
    private Context context = this;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (!isServiceStarted) {
            AppComponents app = DaggerAppComponents
                    .builder()
                    .contextModule(new com.controladad.boutia_pms.dagger.ContextModule(this))
                    .notificationModule(new NotificationModule(getString(R.string.sending_data_to_the_server)))
                    .build();


            startForegroundService(app.getNotificationBuilder());

            reportModel = new ReportModel(this, this,

                    DaggerRetrofitProviderComponent
                            .builder()
                            .contextModule(new ContextModule(this))
                            .retrofitAPIServiceModule(new RetrofitAPIServiceModule(BASE_URL))
                            .build()
                            .getService()
            );
        }


        intentList.add(intent);
        sendDataServer();

        isServiceStarted = true;
        super.onStartCommand(intent, flags, startId);
        return Service.START_REDELIVER_INTENT;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    private void startForegroundService(NotificationCompat.Builder builder) {
        startForeground(NOTIFICATION_ID, builder.build());
    }

    private void sendDataServer() {


        if (callBackMethod != null) {


            for (Intent intent : intentList) {
                if (intent != null) {

                    final String date;
                    final int mid;
                    final int nid;

                    switch (intent.getIntExtra("type", 0)) {

                        case 0:

                            date = intent.getStringExtra("date");
                            nid = intent.getIntExtra("nid", 0);
                            reportModel.sendingRepairReport(nid, date, callBackMethod);
                            break;


                        case 1:

                            mid = intent.getIntExtra("mid", 0);
                            date = intent.getStringExtra("date");
                            reportModel.prepareData(mid, date, callBackMethod);
                            break;


                        case 2:

                            mid = intent.getIntExtra("mid", 0);
                            reportModel.sendTrack(String.valueOf(mid), callBackMethod);
                            break;

                        case 3:

                            date = intent.getStringExtra("date");
                            nid = intent.getIntExtra("nid", 0);
                            reportModel.sendingRepairReportOtherType(nid, date, callBackMethod);
                            break;

                    }

                }

                itemsToRemove.add(intent);
            }

            intentList.removeAll(itemsToRemove);
            itemsToRemove.clear();
        }

    }

    public void setCallBackMethod(CallBackMethod<String> callBackMethod) {
        this.callBackMethod = callBackMethod;
        sendDataServer();
    }


    public class MyBinder extends Binder {
        public UploadService getService() {
            return UploadService.this;
        }
    }

    public List<String> getTrackList() {
        return reportModel.getTrackListTracker();
    }

    public List<String> getMissionListTracker() {
        return reportModel.getMissionListTracker();
    }

    public List<String> getRepairMissionListTracker() {
        return reportModel.getRepairMissionListTracker();
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return iBinder;
    }
}

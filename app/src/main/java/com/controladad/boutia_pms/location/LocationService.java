package com.controladad.boutia_pms.location;


import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioAttributes;
import android.net.Uri;
import android.os.IBinder;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.controladad.boutia_pms.R;
import com.controladad.boutia_pms.activities.MainActivity;
import com.controladad.boutia_pms.utility.BoutiaApplication;
import com.controladad.boutia_pms.utility.Constants;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import timber.log.Timber;

public class LocationService extends Service {

    private boolean isServiceStarted = false;
    public static final String CHANNEL_ID = "default";
    public static final String CHANNEL_NAME = "gps_channel";
    private int mid;
    private String type;
    private LocationManager mLocationManager;
    private Disposable disposable;


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Timber.d("onStartCommand" + flags + startId);


        if (intent != null) {
            mid = intent.getExtras().getInt("mid");
            type = intent.getExtras().getString("type");
            SharedPreferences.Editor editor = getSharedPreferences(Constants.MY_PREFS_MID, MODE_PRIVATE).edit();
            editor.clear();
            editor.putInt("mid", mid);
            editor.putString("type", type);
            editor.apply();
        } else {
            SharedPreferences sharedPreferences = getSharedPreferences(Constants.MY_PREFS_MID, MODE_PRIVATE);
            mid = sharedPreferences.getInt("mid", mid);
            type = sharedPreferences.getString("type", type);
        }

        disposable = Observable
                .interval(15, TimeUnit.MINUTES)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(n -> {
                    if (mLocationManager == null)
                        mLocationManager = LocationManager.getInstance(this, mid, type);
                    else
                        mLocationManager.startLocationUpdates(type);
                })
                .subscribe(n -> {

                }, throwable -> {
                    Timber.d("error");
                });


        if (mLocationManager == null)
            mLocationManager = LocationManager.getInstance(this, mid, type);
        else
            mLocationManager.startLocationUpdates(type);

        super.onStartCommand(intent, flags, startId);
        return START_REDELIVER_INTENT;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Timber.d("onCreateService");
        createService();

    }


    private void createService() {
        if (!isServiceStarted) {

            Intent intent = new Intent(this, MainActivity.class).putExtra(Constants.NOTIFICATION_INTENT_KEY, Constants.NOTIFICATION_INTENT_VALUE);

            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

            PendingIntent pendingIntent =
                    PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {

                AudioAttributes at = new AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build();

                NotificationManager notificationManager =
                        (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

                NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                        CHANNEL_NAME,
                        NotificationManager.IMPORTANCE_DEFAULT);
                channel.enableVibration(true);
                channel.setSound(Uri.parse("android.resource://" + BoutiaApplication.INSTANCE.getPackageName() + "/" + R.raw.alert), at);
                channel.setVibrationPattern(new long[]{0, 100, 500, 600, 1000});
                channel.setDescription(getString(R.string.notification_description));


                assert notificationManager != null;
                notificationManager.createNotificationChannel(channel);
            }

            try {
                NotificationCompat.Builder notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                        .setContentTitle(getString(R.string.notification_bootia))
                        .setContentText(getString(R.string.notification_gps))
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setTicker(getString(R.string.notification_in_progress))
                        .setContentIntent(pendingIntent)
                        .setSound(Uri.parse("android.resource://" + BoutiaApplication.INSTANCE.getPackageName() + "/" + R.raw.alert))
                        .setVibrate(new long[]{0, 100, 500, 600, 1000})
                        .setOngoing(true);

                startForeground(1000, notification.build());

            } catch (NullPointerException e) {
                e.printStackTrace();
            }

        }


        isServiceStarted = true;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();

        if (disposable != null)
            if (!disposable.isDisposed())
                disposable.dispose();

        mLocationManager.stopLocationTracking();
        LocationManager.locationManager = null;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}

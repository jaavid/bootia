package com.controladad.boutia_pms.location;


import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.media.AudioAttributes;
import android.net.Uri;
import android.os.Build;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.controladad.boutia_pms.R;
import com.controladad.boutia_pms.activities.MainActivity;
import com.controladad.boutia_pms.models.database.TrackEntity;
import com.controladad.boutia_pms.utility.BoutiaApplication;
import com.controladad.boutia_pms.utility.Constants;
import com.controladad.boutia_pms.view_models.Function;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import lombok.Getter;
import lombok.Setter;
import saman.zamani.persiandate.PersianDate;
import saman.zamani.persiandate.PersianDateFormat;
import timber.log.Timber;

public class LocationManager {

    private final int FIVE_MINUTES = 1000 * 300;
    private final int HUNDRED_METERS = 100;

    public static LocationManager locationManager = null;
    private static LocationRequest mLocationRequest = null;
    private final String WARNING = "Warning";

    private LocationCallback mLocationCallback;
    private Disposable disposable;
    private Location mLocation;
    private static int mids;
    private static String types;
    private FusedLocationProviderClient mFusedLocationClient;
    private String mLastDate;
    private Location mLastKnownLocation = null;
    @Getter
    @Setter
    private Function onTrakGet;

    @SuppressLint("MissingPermission")
    private LocationManager(Context context) {


        //Setting up location updates
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(context);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference();

        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {


                    mLocation = location;

                    disposable = Observable.fromCallable(() -> mLocation)
                            .observeOn(Schedulers.io())
                            .subscribe(location1 -> {

                                myRef.child("Latitude").push().setValue(String.valueOf(location1.getLatitude()));
                                myRef.child("Longitude").push().setValue(location1.getLongitude());

                                PersianDate pdate = new PersianDate();
                                PersianDateFormat persianDateFormat = new PersianDateFormat("Y/m/d H:i:s");
                                String date = persianDateFormat.format(pdate);

                                String[] dateForSending = date.split("[ \t]");
                                String[] dateReverseArray = dateForSending[0].split("/");
                                String dateReverse = dateReverseArray[2] + "/" + dateReverseArray[1] + "/" + dateReverseArray[0];
                                String finalDateForSending = dateReverse + " - " + dateForSending[1];

                                myRef.child("hasSpeed?").push().setValue(location1.hasSpeed());
                                int subTime = -1;
                                double distance = -1;

                                if (mLastKnownLocation == null) {
                                    mLastKnownLocation = location1;
                                    PersianDate pdate2 = new PersianDate();
                                    PersianDateFormat persianDateFormat2 = new PersianDateFormat("H:i:s");
                                    String date2 = persianDateFormat2.format(pdate2);
                                    mLastDate = date2;
                                } else {

                                    double a = Math.cos(Math.toRadians(location1.getLatitude())) * Math.cos(Math.toRadians(mLastKnownLocation.getLatitude()))
                                            * Math.cos(Math.toRadians(location1.getLongitude()) - Math.toRadians(mLastKnownLocation.getLongitude())) +
                                            Math.sin(Math.toRadians(location.getLatitude())) * Math.sin(Math.toRadians(mLastKnownLocation.getLatitude()));

                                    distance = 6371 * Math.acos(a);


                                    PersianDate pdate2 = new PersianDate();
                                    PersianDateFormat persianDateFormat2 = new PersianDateFormat("H:i:s");
                                    String date2 = persianDateFormat2.format(pdate2);

                                    if (!mLastDate.equals(date2)) {

                                        int currentDateInSeconds = Integer.valueOf(date2.split(":")[0]) * 3600 + Integer.valueOf(date2.split(":")[1]) * 60 + Integer.valueOf(date2.split(":")[2]);
                                        int lasteDateInSeconds = Integer.valueOf(mLastDate.split(":")[0]) * 3600 + Integer.valueOf(mLastDate.split(":")[1]) * 60 + Integer.valueOf(mLastDate.split(":")[2]);

                                        subTime = currentDateInSeconds - lasteDateInSeconds;

                                        double speed = (distance / subTime) * 3600;

                                        myRef.child("speed").push().setValue(speed);

                                        if (speed > 110) {


                                            Intent intent = new Intent(context, MainActivity.class).putExtra(Constants.NOTIFICATION_INTENT_KEY, Constants.NOTIFICATION_INTENT_VALUE);

                                            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

                                            PendingIntent pendingIntent =
                                                    PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);

                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                                CharSequence name = context.getString(R.string.warning);
                                                String description = context.getString(R.string.speed_limit);
                                                AudioAttributes at = new AudioAttributes.Builder()
                                                        .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                                                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                                                        .build();

                                                int importance = NotificationManager.IMPORTANCE_HIGH;
                                                NotificationChannel channel = new NotificationChannel(WARNING, name, importance);
                                                channel.enableVibration(true);
                                                channel.setSound(Uri.parse("android.resource://" + BoutiaApplication.INSTANCE.getPackageName() + "/" + R.raw.siren), at);
                                                channel.setVibrationPattern(new long[]{0, 100, 500, 600, 1000});
                                                channel.setDescription(description);
                                                // Register the channel with the system; you can't change the importance
                                                // or other notification behaviors after this
                                                NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
                                                notificationManager.createNotificationChannel(channel);
                                            }

                                            NotificationCompat.Builder notificationBuilder =
                                                    new NotificationCompat.Builder(context, WARNING)
                                                            .setSmallIcon(R.mipmap.ic_launcher)
                                                            .setContentTitle(context.getString(R.string.warning))
                                                            .setContentText(context.getString(R.string.speed_limit))
                                                            .setSound(Uri.parse("android.resource://" + BoutiaApplication.INSTANCE.getPackageName() + "/" + R.raw.siren))
                                                            .setVibrate(new long[]{0, 100, 500, 600, 1000})
                                                            .setContentIntent(pendingIntent)
                                                            .setPriority(NotificationCompat.PRIORITY_HIGH)
                                                            .setAutoCancel(true);


                                            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
                                            notificationManager.notify(1002, notificationBuilder.build());

                                        }

                                        if (!Objects.equals(types, "harim") || subTime >= 15 * 60 || distance >= 0.5){
                                            mLastKnownLocation = location1;
                                            mLastDate = date2;
                                        }
                                    }

                                }

                                if (!Objects.equals(types, "harim") || subTime >= 14 * 60 || distance >= 0.45 || subTime == -1 || distance == -1){
                                    BoutiaApplication.INSTANCE.getDb().getTrackDao()
                                        .insertTrack(new TrackEntity(mids, String.valueOf(location1.getLatitude()),
                                                String.valueOf(location1.getLongitude()), finalDateForSending,String.valueOf(pdate.getShYear()),
                                                String.valueOf(pdate.getShMonth()),String.valueOf(pdate.getShDay()),String.valueOf(pdate.getHour()),
                                                String.valueOf(pdate.getMinute()),String.valueOf(pdate.getSecond()), false, types));
                                    if (onTrakGet != null)
                                        onTrakGet.fun();
                                    Timber.d("ba movafaghiat save shod");
                                }

                            },Throwable ->{
                                Timber.d("err");
                            });

                }
            }
        };
        startLocationUpdates(types);
    }

    public Flowable<Location> getLocation() {

        return Flowable.fromCallable(() -> mLocation);
    }

    @SuppressLint("MissingPermission")
    public void startLocationUpdates(String type) {
        if (!Objects.equals(type,"harim")){
            mLocationRequest = LocationRequest.create();
            mLocationRequest.setInterval(1000);
            mLocationRequest.setFastestInterval(500);
            mLocationRequest.setSmallestDisplacement(150);
            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        }else {
            mLocationRequest = LocationRequest.create();
            mLocationRequest.setInterval(1000);
            mLocationRequest.setFastestInterval(500);
            mLocationRequest.setSmallestDisplacement(500);
            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        }
        mFusedLocationClient
                .requestLocationUpdates(mLocationRequest, mLocationCallback, null);
    }

    public static Task<LocationSettingsResponse> getTask(Context context) {

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(createLocationRequest());

        SettingsClient client = LocationServices.getSettingsClient(context);
        return client.checkLocationSettings(builder.build());
    }


    private static LocationRequest createLocationRequest() {

        if (mLocationRequest == null) {
            mLocationRequest = LocationRequest.create();
            mLocationRequest.setInterval(1000);
            mLocationRequest.setFastestInterval(500);
            mLocationRequest.setSmallestDisplacement(150);
            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        }

        return mLocationRequest;
    }

    public void stopLocationTracking() {
        mFusedLocationClient.removeLocationUpdates(mLocationCallback);
    }

    public static LocationManager getInstance(Context context, int mid, String type) {

        locationManager = locationManager == null ? new LocationManager(context) : locationManager;
        mids = mid;
        types = type;
        return locationManager;
    }
}

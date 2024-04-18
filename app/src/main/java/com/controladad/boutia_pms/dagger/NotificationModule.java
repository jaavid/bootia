package com.controladad.boutia_pms.dagger;


import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import androidx.core.app.NotificationCompat;

import com.controladad.boutia_pms.R;
import com.controladad.boutia_pms.activities.MainActivity;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class NotificationModule {


    private final static String CHANNEL_ID = "upload_channel";
    private String notificationContentTitle;

    public NotificationModule(String notificationContentTitle) {
        this.notificationContentTitle = notificationContentTitle;
    }


    @Singleton
    @Provides
    public Intent createIntent(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.setAction(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("msg", "Hey its me");

        return intent;
    }

    @Singleton
    @Provides
    public PendingIntent createPendingIntent(Intent intent, Context context) {
        int uniqueInt = (int) (System.currentTimeMillis() & 0xfffffff);
        return PendingIntent.getActivity(
                context,
                uniqueInt,
                intent,
                PendingIntent.FLAG_CANCEL_CURRENT
        );
    }

    @Singleton
    @Provides
    public NotificationCompat.Builder createNotification(PendingIntent pendingIntent, Context context) {

        return new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(notificationContentTitle)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

    }
}

package com.controladad.boutia_pms.utility;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import androidx.room.Room;
import android.os.Build;
import android.os.Environment;
import androidx.multidex.MultiDexApplication;

import com.controladad.boutia_pms.activities.MainActivity;
import com.controladad.boutia_pms.dagger.AppComponents;
import com.controladad.boutia_pms.dagger.ContextModule;
import com.controladad.boutia_pms.dagger.DaggerAppComponents;
import com.controladad.boutia_pms.dagger.NotificationModule;
import com.controladad.boutia_pms.models.database.BoutiaDataBase;
import com.controladad.boutia_pms.view_models.MainActivityVM;
//import com.crashlytics.android.Crashlytics;

import java.io.File;

import javax.inject.Inject;

//import io.fabric.sdk.android.Fabric;
import lombok.Getter;
import lombok.Setter;
import timber.log.Timber;

import static com.controladad.boutia_pms.models.database.BoutiaDataBase.MIGRATION_64_65;
import static com.controladad.boutia_pms.models.database.BoutiaDataBase.MIGRATION_65_66;
import static com.controladad.boutia_pms.models.database.BoutiaDataBase.MIGRATION_66_67;
import static com.controladad.boutia_pms.models.database.BoutiaDataBase.MIGRATION_67_68;
import static com.controladad.boutia_pms.models.database.BoutiaDataBase.MIGRATION_68_69;
import static com.controladad.boutia_pms.models.database.BoutiaDataBase.MIGRATION_69_70;
import static com.controladad.boutia_pms.models.database.BoutiaDataBase.MIGRATION_70_71;
import static com.controladad.boutia_pms.models.database.BoutiaDataBase.MIGRATION_71_72;
import static com.controladad.boutia_pms.models.database.BoutiaDataBase.MIGRATION_72_73;
import static com.controladad.boutia_pms.models.database.BoutiaDataBase.MIGRATION_73_74;
import static com.controladad.boutia_pms.models.database.BoutiaDataBase.MIGRATION_74_75;
import static com.controladad.boutia_pms.models.database.BoutiaDataBase.MIGRATION_75_76;
import static com.controladad.boutia_pms.models.database.BoutiaDataBase.MIGRATION_76_77;

public class BoutiaApplication extends MultiDexApplication {
    private final static String CHANNEL_ID = "upload_channel";
    private final static String CHANNEL_NAME = "upload_channel";
    ViewsCustomFonts viewsCustomFonts;
    public static BoutiaApplication INSTANCE;
    private AppComponents appComponents;
    @Setter
    @Getter
    private MainActivity mainActivity;
    @Inject
    @Getter
    MainActivityVM mainActivityVM;
    private BoutiaDataBase db;
    private File dir;

    public BoutiaDataBase getDb() {
        if (db == null || !db.isOpen()) {

            File dir = new File(Environment.getExternalStorageDirectory() + "/" + "samanet_db", "db");
            if (!dir.exists()) {
               boolean res = dir.mkdirs();
               //Timber.d("Create db dir:", String.valueOf(res));

            }

            db = Room.databaseBuilder(this,
                    BoutiaDataBase.class, Environment.getExternalStorageDirectory()+"/samanet_db/db/b_database.db")
                    .addMigrations(MIGRATION_64_65, MIGRATION_65_66, MIGRATION_66_67, MIGRATION_67_68,
                            MIGRATION_68_69, MIGRATION_69_70, MIGRATION_70_71, MIGRATION_71_72, MIGRATION_72_73,
                            MIGRATION_73_74,MIGRATION_74_75, MIGRATION_75_76,MIGRATION_76_77).build();

        }
        return db;
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance);
            channel.setDescription("کانال فرستادن دیتا");
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            assert notificationManager != null;
            notificationManager.createNotificationChannel(channel);
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //Fabric.with(this, new Crashlytics());
        INSTANCE = this;
        getAppComponents().inject(this);
        createNotificationChannel();

        viewsCustomFonts = viewsCustomFonts.getInstance();
        // add your custom fonts here with your own custom name.
        viewsCustomFonts.addFont("faNumLight", "IRANYekanLightMobile(FaNum).ttf");
        viewsCustomFonts.addFont("faNumRegular", "IRANYekanRegularMobile(FaNum).ttf");
        viewsCustomFonts.addFont("faNumBold", "IRANYekanMobileBold(FaNum).ttf");
        viewsCustomFonts.addFont("faLight", "IRANYekanMobileLight.ttf");
        viewsCustomFonts.addFont("faRegular", "IRANYekanMobileRegular.ttf");
        viewsCustomFonts.addFont("faBold", "IRANYekanMobileBold.ttf");


       /* File dir = new File(Environment.getExternalStorageDirectory() + "/" + "samanet_db", "db");
        if (!dir.exists()) {
            dir.mkdirs();
        }*/

    }


    public AppComponents getAppComponents() {
        if (appComponents == null)
            appComponents = DaggerAppComponents.builder()
                    .contextModule(new ContextModule(this))
                    .notificationModule(new NotificationModule("kehrsx"))
                    .build();
        return appComponents;
    }
}

package com.controladad.boutia_pms.dagger;


import androidx.core.app.NotificationCompat;

import com.controladad.boutia_pms.activities.MainActivity;
import com.controladad.boutia_pms.adapters.GeneralAdapter;
import com.controladad.boutia_pms.fragment_navigation_handler.FragmentNavigator;
import com.controladad.boutia_pms.fragments.GeneralFragment;
import com.controladad.boutia_pms.utility.BoutiaApplication;
import com.controladad.boutia_pms.view_models.GeneralVM;
import com.controladad.boutia_pms.view_models.MainActivityVM;

import javax.inject.Singleton;

import dagger.Component;

@Component(modules = {ContextModule.class,MainActivityVMModule.class,NotificationModule.class})
@Singleton
public interface AppComponents {
    void inject(MainActivity mainActivity);
    void inject(GeneralVM generalVM);
    void inject(MainActivityVM mainActivityVM);
    void inject(BoutiaApplication boutiaApplication);
    void inject(FragmentNavigator navigator);
    void inject(GeneralFragment fragment);
    void inject(MainActivityVM.MainActivityVMCreator mainActivityVMCreator);
    void inject(GeneralAdapter generalAdapter);
    NotificationCompat.Builder getNotificationBuilder();
}

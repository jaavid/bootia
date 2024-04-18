package com.controladad.boutia_pms.dagger;

import com.controladad.boutia_pms.utility.BoutiaApplication;
import com.controladad.boutia_pms.view_models.MainActivityVM;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class MainActivityVMModule {

    @Singleton
    @Provides
    MainActivityVM mainActivityVM(){
        MainActivityVM mainActivityVM =  new MainActivityVM();
        BoutiaApplication.INSTANCE.getAppComponents().inject(mainActivityVM);
        return mainActivityVM;
    }
}

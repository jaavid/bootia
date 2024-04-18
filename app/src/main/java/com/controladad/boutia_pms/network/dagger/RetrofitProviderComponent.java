package com.controladad.boutia_pms.network.dagger;

import com.controladad.boutia_pms.network.retrofit.RetrofitApiService;
//import com.controladad.boutia_pms.network.retrofit.RetrofitDateTimeCheckerService;
import com.controladad.boutia_pms.network.retrofit.RetrofitDateTimeCheckerService;
import com.controladad.boutia_pms.network.retrofit.RetrofitVersionCheckService;

import dagger.Component;

@RetrofitProviderScope
@Component(modules = {RetrofitAPIServiceModule.class})
public interface RetrofitProviderComponent {

    RetrofitApiService getService();

    RetrofitVersionCheckService getVersionService();

    RetrofitDateTimeCheckerService getDateTimeService();

}

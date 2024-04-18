package com.controladad.boutia_pms.network.retrofit;

import com.controladad.boutia_pms.models.retrofit_models.RetrofitModels;

import io.reactivex.Observable;
import retrofit2.http.GET;

public interface RetrofitVersionCheckService {

    @GET("/info.json")
    Observable<RetrofitModels.VersionCheck> getVersion();
}

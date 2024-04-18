package com.controladad.boutia_pms.network.dagger;


import com.controladad.boutia_pms.BuildConfig;
import com.controladad.boutia_pms.network.retrofit.RetrofitApiService;
import com.controladad.boutia_pms.network.retrofit.RetrofitDateTimeCheckerService;
import com.controladad.boutia_pms.network.retrofit.RetrofitVersionCheckService;

import java.util.concurrent.TimeUnit;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

@Module(includes = NetworkModule.class)
public class RetrofitAPIServiceModule {

    private String baseUrl;

    @Provides
    @RetrofitProviderScope
    public RetrofitApiService retrofitAPIService(@Named("samant") Retrofit retrofit) {

        return retrofit.create(RetrofitApiService.class);
    }

    @Provides
    @RetrofitProviderScope
    public RetrofitVersionCheckService retrofitVersionCheckService(@Named("version") Retrofit retrofit) {
        return retrofit.create(RetrofitVersionCheckService.class);
    }

    @Provides
    @RetrofitProviderScope
    public RetrofitDateTimeCheckerService retrofitDateTimeCheckerService(@Named("dateTime") Retrofit retrofit) {
        return retrofit.create(RetrofitDateTimeCheckerService.class);
    }

    public RetrofitAPIServiceModule(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    @Provides
    @RetrofitProviderScope
    @Named("samant")
    public Retrofit retrofit(OkHttpClient okHttpClient) {

        return new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(okHttpClient)
                .build();
    }

    @Provides
    @RetrofitProviderScope
    @Named("dateTime")
    public Retrofit retrofitDateTime(OkHttpClient okHttpClient) {

        return new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(okHttpClient)
                .build();
    }

    @Provides
    @RetrofitProviderScope
    @Named("version")
    public Retrofit retrofitVersion() {

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .readTimeout(60, TimeUnit.SECONDS)
                .connectTimeout(60 / 2, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .cache(null)
                .build();

        return new Retrofit.Builder()
                .baseUrl("http://samant.adad.ws")///TODO should add to the project for now disabled
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(okHttpClient)
                .build();
    }
}

package com.controladad.boutia_pms.network.dagger;


import android.content.Context;

import com.controladad.boutia_pms.network.network_check.LiveNetworkMonitor;
import com.controladad.boutia_pms.network.network_check.NoNetworkException;

import java.io.File;
import java.util.concurrent.TimeUnit;

import dagger.Module;
import dagger.Provides;
import okhttp3.Cache;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import timber.log.Timber;

@Module(includes = ContextModule.class)
public class NetworkModule {


    @Provides
    @RetrofitProviderScope
    public HttpLoggingInterceptor httpLoggingInterceptor() {

        HttpLoggingInterceptor interceptor;
        interceptor = new HttpLoggingInterceptor(message -> Timber.i(message));
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        return interceptor;
    }

    @Provides
    @RetrofitProviderScope
    public Cache cache(File cacheFile) {

        return new Cache(cacheFile, 10 * 1000 * 1000); // 10 Mb cache
    }

    @Provides
    @RetrofitProviderScope
    public File file(Context context) {

        return new File(context.getCacheDir(), "okhttp_cache");
    }

    @Provides
    @RetrofitProviderScope
    public LiveNetworkMonitor networkMonitor(Context context) {
        return new LiveNetworkMonitor(context);
    }

    @Provides
    @RetrofitProviderScope
    public OkHttpClient okHttpClient(HttpLoggingInterceptor interceptor, Cache cache, LiveNetworkMonitor networkMonitor) {

        return new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .addInterceptor(chain -> {
                    if (networkMonitor.isConnected()) {
                        return chain.proceed(chain.request());
                    } else {
                        throw new NoNetworkException();
                    }
                }).connectTimeout(100, TimeUnit.SECONDS)
                .readTimeout(100, TimeUnit.SECONDS)
                .cache(cache)
                .build();
    }
}

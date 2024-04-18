package com.controladad.boutia_pms.network.dagger;


import android.content.Context;

import dagger.Module;
import dagger.Provides;

@Module
public class ContextModule {

    private final Context context;

    public ContextModule(Context context){
        this.context = context;
    }

    @Provides
    @RetrofitProviderScope
    public Context context(){
        return context;
    }
}

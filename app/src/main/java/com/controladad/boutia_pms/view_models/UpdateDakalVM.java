package com.controladad.boutia_pms.view_models;

import com.controladad.boutia_pms.activities.MainActivity;
import com.controladad.boutia_pms.network.dagger.RetrofitProviderComponent;

public interface UpdateDakalVM {
    RetrofitProviderComponent getComponent();
    MainActivity getActivity();
}

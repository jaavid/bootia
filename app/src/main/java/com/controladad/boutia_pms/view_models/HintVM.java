package com.controladad.boutia_pms.view_models;

import androidx.databinding.BaseObservable;
import android.view.View;

import com.controladad.boutia_pms.fragments.HintFragment;

import lombok.Getter;
import lombok.Setter;

public class HintVM extends BaseObservable{

    @Getter
    private String hintText;
    @Setter
    private HintFragment fragment;

    public HintVM(String hintText) {
            this.hintText = hintText;
        }

        public View.OnClickListener onClickListener(){
            return v -> {
                if(fragment!=null && fragment.getFragmentManager()!=null)
                    fragment.getFragmentManager()
                            .beginTransaction()
                            .detach(fragment)
                            .commit();
            };
        }


    public HintFragment getFragment() {
        return fragment;
    }
}

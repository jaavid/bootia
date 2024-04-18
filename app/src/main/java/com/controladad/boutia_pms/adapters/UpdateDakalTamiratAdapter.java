package com.controladad.boutia_pms.adapters;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.controladad.boutia_pms.databinding.CardItemUpdateDakalTamiratBinding;

public class UpdateDakalTamiratAdapter extends GeneralAdapter {
    @Override
    public void itemResizing(RecyclerView.ViewHolder holder) {

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        CardItemUpdateDakalTamiratBinding binding = CardItemUpdateDakalTamiratBinding.inflate(layoutInflater,parent,false);
        return new ViewHolders.UpdateDakalTamiratViewHolder(binding);
    }
}

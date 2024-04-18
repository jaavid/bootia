package com.controladad.boutia_pms.adapters.anims;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.controladad.boutia_pms.adapters.GeneralAdapter;
import com.controladad.boutia_pms.databinding.CardItemMissionChooseBinding;
import com.controladad.boutia_pms.view_models.items_view_models.GeneralDataModel;
import com.controladad.boutia_pms.view_models.items_view_models.MissionsDataModel;

public class MissionsAdapter extends GeneralAdapter {

    @Override
    public void itemResizing(RecyclerView.ViewHolder holder) {

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        CardItemMissionChooseBinding cardItemMissionChooseBinding = CardItemMissionChooseBinding.inflate(
                layoutInflater, parent, false);
        return new MissionsViewHolder(cardItemMissionChooseBinding);
    }

    private class MissionsViewHolder extends GeneralViewHolder {

        private CardItemMissionChooseBinding binding;

        private MissionsViewHolder(CardItemMissionChooseBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        @Override
        protected void bind(GeneralDataModel dataModel) {
            binding.setViewModel((MissionsDataModel) dataModel);
            binding.executePendingBindings();
        }
    }
}

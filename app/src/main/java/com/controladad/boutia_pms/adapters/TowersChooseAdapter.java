package com.controladad.boutia_pms.adapters;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.controladad.boutia_pms.databinding.CardItemTowerChooseBinding;
import com.controladad.boutia_pms.view_models.items_view_models.GeneralDataModel;
import com.controladad.boutia_pms.view_models.items_view_models.TowerChooseIVM;

public class TowersChooseAdapter extends GeneralAdapter {
    @Override
    public void itemResizing(RecyclerView.ViewHolder holder) {

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        CardItemTowerChooseBinding cardItemTowerChooseBinding = CardItemTowerChooseBinding.inflate(layoutInflater, parent, false);        return new TowerChooseViewHolder(cardItemTowerChooseBinding);
    }

    class TowerChooseViewHolder extends GeneralViewHolder{
        private CardItemTowerChooseBinding binding;

        public TowerChooseViewHolder(CardItemTowerChooseBinding cardItemTowerChooseBinding) {
            super(cardItemTowerChooseBinding.getRoot());
            this.binding = cardItemTowerChooseBinding;
        }

        @Override
        protected void bind(GeneralDataModel dataModel) {
            binding.setViewModel((TowerChooseIVM) dataModel);
            binding.executePendingBindings();
        }
    }
}

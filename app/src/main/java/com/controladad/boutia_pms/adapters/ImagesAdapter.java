package com.controladad.boutia_pms.adapters;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.controladad.boutia_pms.databinding.CardItemImagesBinding;
import com.controladad.boutia_pms.view_models.items_view_models.GeneralDataModel;
import com.controladad.boutia_pms.view_models.items_view_models.ImagesItemsDataModel;

public class ImagesAdapter extends GeneralAdapter {
    @Override
    public void itemResizing(RecyclerView.ViewHolder holder) {

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        CardItemImagesBinding cardItemImagesBinding = CardItemImagesBinding.inflate(layoutInflater,parent,false);
        return new ImagesViewHolder(cardItemImagesBinding);
    }

    private class ImagesViewHolder extends GeneralViewHolder{

        private final CardItemImagesBinding binding;
        public ImagesViewHolder(CardItemImagesBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        @Override
        protected void bind(GeneralDataModel dataModel) {
            binding.setImagesDataModel((ImagesItemsDataModel) dataModel);
            binding.executePendingBindings();
        }
    }
}

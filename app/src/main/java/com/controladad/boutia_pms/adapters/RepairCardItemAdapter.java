package com.controladad.boutia_pms.adapters;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.controladad.boutia_pms.databinding.CardItemSimpleTextViewFormElementsBinding;


public class RepairCardItemAdapter extends GeneralAdapter {



    @Override
    public void itemResizing(RecyclerView.ViewHolder holder) {

    }



    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        CardItemSimpleTextViewFormElementsBinding binding = CardItemSimpleTextViewFormElementsBinding.inflate(layoutInflater, parent, false);
        return new ViewHolders.SimpleTextViewHolder(binding);
    }

/*    private class RepairItemViewHolder extends GeneralAdapter.GeneralViewHolder {
        private final CardItemCheckBoxFormElementBinding binding;
        public RepairItemViewHolder(CardItemCheckBoxFormElementBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        @Override
        void bind(GeneralDataModel dataModel) {
            binding.setCheckBoxDataModel((CheckBoxDataModel) dataModel);

            binding.executePendingBindings();
        }
    }*/
}

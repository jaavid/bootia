package com.controladad.boutia_pms.adapters;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.controladad.boutia_pms.databinding.CardItemDoubleButtonFormElementBinding;
import com.controladad.boutia_pms.databinding.CardItemRepairFormBinding;
import com.controladad.boutia_pms.view_models.items_view_models.GeneralDataModel;
import com.controladad.boutia_pms.view_models.items_view_models.RepairItemDataModel;


public class RepairDetailAdapter extends GeneralAdapter {

    @Override
    public void itemResizing(RecyclerView.ViewHolder holder) {

    }

    @Override
    public int getItemViewType(int position) {
        return position!= getItemCount()-1? 0:1;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        switch (viewType){
            case 0:
                CardItemRepairFormBinding binding = CardItemRepairFormBinding.inflate(layoutInflater, parent, false);
                return new RepairDetailViewHolder(binding);
            case 1:
                CardItemDoubleButtonFormElementBinding cardItemDoubleButtonFormElementBinding = CardItemDoubleButtonFormElementBinding.inflate(layoutInflater, parent, false);
                return new ViewHolders.DoubleButtonHolder(cardItemDoubleButtonFormElementBinding);
        }
        return null;
    }

    private class RepairDetailViewHolder extends GeneralAdapter.GeneralViewHolder {
        private final CardItemRepairFormBinding binding;
        public RepairDetailViewHolder(CardItemRepairFormBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        @Override
        protected void bind(GeneralDataModel dataModel) {
            RepairItemDataModel repairItemDataModel = (RepairItemDataModel) dataModel;
            binding.setRepairItemDataModel(repairItemDataModel);
            repairItemDataModel.setSmoothCheckBox(binding.repairCheckBox);
            binding.repairCheckBox.setChecked(repairItemDataModel.isDone());
            binding.executePendingBindings();
        }
    }
}

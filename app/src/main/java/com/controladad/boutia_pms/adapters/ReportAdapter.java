package com.controladad.boutia_pms.adapters;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.controladad.boutia_pms.databinding.CardItemRepairReportBinding;
import com.controladad.boutia_pms.databinding.CardItemReportBinding;
import com.controladad.boutia_pms.databinding.CardItemReportTrackBinding;
import com.controladad.boutia_pms.utility.Constants;
import com.controladad.boutia_pms.view_models.items_view_models.GeneralDataModel;
import com.controladad.boutia_pms.view_models.items_view_models.RepairReportDataModel;
import com.controladad.boutia_pms.view_models.items_view_models.ReportItemDataModel;
import com.controladad.boutia_pms.view_models.items_view_models.TrackReportItemDataModel;


public class ReportAdapter extends GeneralAdapter {

    @Override
    public void itemResizing(RecyclerView.ViewHolder holder) {

    }

    @Override
    public int getItemViewType(int position) {
        return getItemsModelList().get(position).getKey();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        switch (viewType) {

            case Constants.REPORT_ITEM_CARD_ITEM_KEY:
                CardItemReportBinding binding = CardItemReportBinding.inflate(layoutInflater, parent, false);
                return new ReportItemViewHolder(binding);
            case Constants.TRACK_REPORT_ITEM_KEY:
                CardItemReportTrackBinding trackBinding = CardItemReportTrackBinding.inflate(layoutInflater, parent, false);
                return new TrackReportItemViewHolder(trackBinding);
            case Constants.REPAIR_REPORT_KEY:
                CardItemRepairReportBinding repairReportBinding = CardItemRepairReportBinding.inflate(layoutInflater, parent, false);
                return new RepairReportViewHolder(repairReportBinding);
        }
        return null;
    }



    private class ReportItemViewHolder extends GeneralAdapter.GeneralViewHolder {
        private final CardItemReportBinding binding;
        public ReportItemViewHolder(CardItemReportBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        @Override
        protected void bind(GeneralDataModel dataModel) {
            ReportItemDataModel reportItemDataModel = (ReportItemDataModel) dataModel;
            reportItemDataModel.setSendingButton(binding.btnSendToSite);
            reportItemDataModel.setState(reportItemDataModel.getState());
            binding.setReportItemDataModel((ReportItemDataModel) dataModel);
            binding.executePendingBindings();
        }
    }

    private class RepairReportViewHolder extends GeneralAdapter.GeneralViewHolder {
        private final CardItemRepairReportBinding binding;
        public RepairReportViewHolder(CardItemRepairReportBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        @Override
        protected void bind(GeneralDataModel dataModel) {
            RepairReportDataModel repairReportDataModel = (RepairReportDataModel) dataModel;
            repairReportDataModel.setSendingButton(binding.btnSendToSite);
            repairReportDataModel.setState(repairReportDataModel.getState());
            binding.setDataModel(repairReportDataModel);
            binding.executePendingBindings();
        }
    }


    private class TrackReportItemViewHolder extends GeneralAdapter.GeneralViewHolder {
        private final CardItemReportTrackBinding binding;
        public TrackReportItemViewHolder(CardItemReportTrackBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        @Override
        protected void bind(GeneralDataModel dataModel) {
            TrackReportItemDataModel trackReportItemDataModel = (TrackReportItemDataModel) dataModel;
            trackReportItemDataModel.setSendingButton(binding.btnTrackSendToSite);
            trackReportItemDataModel.setState(trackReportItemDataModel.getState());
            binding.setTrackReportItemDataModel(trackReportItemDataModel);
            binding.executePendingBindings();
        }
    }

}

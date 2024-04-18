package com.controladad.boutia_pms.adapters;

import android.content.Context;
import android.os.Build;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

import com.controladad.boutia_pms.utility.BoutiaApplication;
import com.controladad.boutia_pms.view_models.MainActivityVM;
import com.controladad.boutia_pms.view_models.RecyclerViewItemsEndListener;
import com.controladad.boutia_pms.view_models.items_view_models.GeneralDataModel;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import lombok.Getter;
import lombok.Setter;


public abstract class GeneralAdapter extends RecyclerView.Adapter implements ResizableView {
    @Inject
    Context context;

    public GeneralAdapter() {
        BoutiaApplication.INSTANCE.getAppComponents().inject(this);
    }

    private List<GeneralDataModel> itemsModelList = new ArrayList<>();
    int displayWidth;
    int displayHeight;

    private ItemClickListener itemClickListener;

    @Setter
    @Getter
    private boolean hasItemsChanged = false;


    @Setter
    private RecyclerViewItemsEndListener recyclerViewItemsEndListener;

    @Setter
    private ItemViewClickListener itemLongClickListener;

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    private ItemViewClickListener itemViewClickListener;

    public void setItemClickListener(ItemViewClickListener itemViewClickListener) {
        this.itemViewClickListener = itemViewClickListener;
    }


    public List<GeneralDataModel> getItemsModelList() {
        return itemsModelList;
    }

    public void setItemsModelList(List<GeneralDataModel> itemsModelList) {
        this.itemsModelList = itemsModelList;
    }

    public void updateData(List<? extends GeneralDataModel> itemsModelList){
        if(itemsModelList != this.itemsModelList && itemsModelList!=null)
        {
            this.itemsModelList.clear();
            this.itemsModelList.addAll(itemsModelList);
        }
        try {
            notifyDataSetChanged();
        }catch (IllegalStateException e){
            hasItemsChanged = true;
        }

    }



    @Override
    public int getItemCount() {
        if (itemsModelList !=null) return itemsModelList.size();
        return 0;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        GeneralDataModel dataModel = itemsModelList.get(position);
        ((GeneralViewHolder)holder).bind(dataModel);
        displayWidth = MainActivityVM.displayWidth;
        displayHeight = MainActivityVM.displayHeight;
        itemResizing(holder);

        if(itemClickListener!=null) holder.itemView.setOnClickListener(v->itemClickListener.onItemClickListener(dataModel));
        else if(itemViewClickListener != null){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                holder.itemView.setTransitionName(this.toString()+String.valueOf(position));
            }
            holder.itemView.setOnClickListener(
                    v -> itemViewClickListener.onItemClickListener(dataModel,holder.itemView,position)
            );
        }
        if(itemLongClickListener != null) holder.itemView.setOnLongClickListener(
                v ->{
                    itemLongClickListener.onItemClickListener(dataModel, holder.itemView,position);
                    return holder.itemView.isSelected();
                }
        );

        if(recyclerViewItemsEndListener!=null && position == itemsModelList.size()-1){
            recyclerViewItemsEndListener.onEndItems();
        }


    }

    public abstract static class GeneralViewHolder extends RecyclerView.ViewHolder {
        public GeneralViewHolder(View itemView) {
            super(itemView);
        }
        protected abstract void bind(GeneralDataModel dataModel);
    }

    public abstract void itemResizing(RecyclerView.ViewHolder holder);

    public interface ItemClickListener{
        void onItemClickListener(GeneralDataModel dataModel);
    }

    public interface ItemViewClickListener{
        void onItemClickListener(GeneralDataModel dataModel, View view, int position);
    }



}


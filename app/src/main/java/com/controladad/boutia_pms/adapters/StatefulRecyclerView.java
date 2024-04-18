package com.controladad.boutia_pms.adapters;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import android.util.AttributeSet;


public class StatefulRecyclerView extends RecyclerView {
    private static final String SAVED_SUPER_STATE = "super-state";
    private static final String SAVED_LAYOUT_MANAGER = "layout-manager-state";

    private Parcelable mLayoutManagerSavedState;

    public StatefulRecyclerView(Context context) {
        super(context);
        addOnScrollListener(onScrollListener);
    }

    public StatefulRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        addOnScrollListener(onScrollListener);
    }

    public StatefulRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        addOnScrollListener(onScrollListener);
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable(SAVED_SUPER_STATE, super.onSaveInstanceState());
        if(this.getLayoutManager()!=null)bundle.putParcelable(SAVED_LAYOUT_MANAGER, this.getLayoutManager().onSaveInstanceState());
        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            Bundle bundle = (Bundle) state;
            mLayoutManagerSavedState = bundle.getParcelable(SAVED_LAYOUT_MANAGER);
            state = bundle.getParcelable(SAVED_SUPER_STATE);
        }
        super.onRestoreInstanceState(state);
    }

    /**
     * Restores scroll position after configuration change.
     * <p>
     * <b>NOTE:</b> Must be called after adapter has been set.
     */
    private void restorePosition() {
        if (mLayoutManagerSavedState != null) {
            this.getLayoutManager().onRestoreInstanceState(mLayoutManagerSavedState);
            mLayoutManagerSavedState = null;
        }
    }

    @Override
    public void setAdapter(Adapter adapter) {
        super.setAdapter(adapter);
        restorePosition();
    }

    private OnScrollListener onScrollListener = new OnScrollListener(){
        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy){
            Adapter adapter = recyclerView.getAdapter();
            if(adapter instanceof GeneralAdapter){
                GeneralAdapter generalAdapter = (GeneralAdapter) adapter;
                if(generalAdapter.isHasItemsChanged()){
                    try{
                        adapter.notifyDataSetChanged();
                        generalAdapter.setHasItemsChanged(false);
                    }catch (IllegalStateException e){
                        generalAdapter.setHasItemsChanged(true);
                    }
                }
            }
        }
    };
}

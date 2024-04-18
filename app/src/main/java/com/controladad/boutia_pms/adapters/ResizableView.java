package com.controladad.boutia_pms.adapters;


import androidx.recyclerview.widget.RecyclerView;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import static com.controladad.boutia_pms.view_models.MainActivityVM.displayHeight;
import static com.controladad.boutia_pms.view_models.MainActivityVM.displayWidth;


public interface ResizableView {
    default ViewGroup.LayoutParams layoutParams(LayoutParamsType type, float displayHeightPerViewHeight, float displayWidthPerViewWidth, int leftMargin, int topMargin, int rightMargin, int bottomMargin, int gravity, boolean isHeightRelatedToDisplayWidth, boolean isWidthRelatedToDisplayHeight){

        int mDisplayHeight = displayHeight;
        int mDisplayWidth = displayWidth;
        if(isHeightRelatedToDisplayWidth) mDisplayHeight = displayWidth;
        if(isWidthRelatedToDisplayHeight) mDisplayWidth = displayHeight;

        switch (type){
            case FrameLayoutParams: FrameLayout.LayoutParams frameParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                if(displayHeightPerViewHeight != 0) frameParams.height =  Math.round(mDisplayHeight/displayHeightPerViewHeight);
                if(displayWidthPerViewWidth != 0) frameParams.width = Math.round(mDisplayWidth/displayWidthPerViewWidth);
                frameParams.setMargins(leftMargin,topMargin,rightMargin,bottomMargin);
                frameParams.gravity = gravity;
                return frameParams;

            case LinearLayoutParams: LinearLayout.LayoutParams linearParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                if(displayHeightPerViewHeight != 0) linearParams.height =  Math.round(mDisplayHeight/displayHeightPerViewHeight);
                if(displayWidthPerViewWidth != 0) linearParams.width = Math.round(mDisplayWidth/displayWidthPerViewWidth);
                linearParams.setMargins(leftMargin,topMargin,rightMargin,bottomMargin);
                linearParams.gravity = gravity;
                return linearParams;

            case RecyclerViewLayoutParams: RecyclerView.LayoutParams recyclerParams = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                if(displayHeightPerViewHeight != 0) recyclerParams.height =  Math.round(mDisplayHeight/displayHeightPerViewHeight);
                if(displayWidthPerViewWidth != 0) recyclerParams.width = Math.round(mDisplayWidth/displayWidthPerViewWidth);
                recyclerParams.setMargins(leftMargin,topMargin,rightMargin,bottomMargin);
                return recyclerParams;

            /*case CardViewLayoutParams:CardView.LayoutParams cardParams = new CardView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                if(displayHeightPerViewHeight != 0) cardParams.height =  Math.round(mDisplayHeight/displayHeightPerViewHeight);
                if(displayWidthPerViewWidth != 0) cardParams.width = Math.round(mDisplayWidth/displayWidthPerViewWidth);
                cardParams.setMargins(leftMargin,topMargin,rightMargin,bottomMargin);
                cardParams.gravity = gravity;
                return cardParams;

            case SlidingUpPanelLayoutParams:SlidingUpPanelLayout.LayoutParams slidingParams = new SlidingUpPanelLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                if(displayHeightPerViewHeight != 0) slidingParams.height =  Math.round(mDisplayHeight/displayHeightPerViewHeight);
                if(displayWidthPerViewWidth != 0) slidingParams.width = Math.round(mDisplayWidth/displayWidthPerViewWidth);
                slidingParams.setMargins(leftMargin,topMargin,rightMargin,bottomMargin);
                return slidingParams;*/
        }

        return null;
    }

    default ViewGroup.LayoutParams layoutParams(LayoutParamsType type, float displayHeightPerViewHeight, float displayWidthPerViewWidth, int leftMargin, int topMargin, int rightMargin, int bottomMargin, int gravity){
        return layoutParams(type, displayHeightPerViewHeight, displayWidthPerViewWidth, leftMargin, topMargin, rightMargin, bottomMargin, gravity, false, false);
    }
}

package com.controladad.boutia_pms.adapters.anims;

import android.content.Context;
import androidx.annotation.DrawableRes;
import androidx.appcompat.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import com.controladad.boutia_pms.R;

class ExpandableItemIndicatorImplNoAnim extends ExpandableItemIndicator.Impl {
    private AppCompatImageView mImageView;

    @Override
    public void onInit(Context context, AttributeSet attrs, int defStyleAttr, ExpandableItemIndicator thiz) {
        View v = LayoutInflater.from(context).inflate(R.layout.widget_expandable_item_indicator, thiz, true);
        mImageView = v.findViewById(R.id.image_view);
    }

    @Override
    public void setExpandedState(boolean isExpanded, boolean animate) {
        @DrawableRes int resId = (isExpanded) ? R.drawable.ic_expand_less : R.drawable.ic_expand_more;
        mImageView.setImageResource(resId);
    }
}

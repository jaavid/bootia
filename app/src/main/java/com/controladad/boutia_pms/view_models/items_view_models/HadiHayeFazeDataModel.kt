package com.controladad.boutia_pms.view_models.items_view_models

import android.view.View
import com.controladad.boutia_pms.utility.Constants
import com.controladad.boutia_pms.utility.collapse
import com.controladad.boutia_pms.utility.expand

class HadiHayeFazeDataModel(val title: String, val setter: TextSetter) {
    var expandableAView: View? = null
    var good: Boolean = false
        set(value) {
            field = value
            if (value)
                setter.setText(Constants.IS_GOOD)
            else {
                setter.setText(Constants.IS_BAD)
                expandABadDetails()
            }
        }

    private fun expandABadDetails() {
        expandableAView?.expand()
    }

    private fun collapsABadDetails() {
        expandableAView?.collapse()
    }

    fun expandOrCollapsABadDetails() {
        if (expandableAView?.visibility == View.VISIBLE)
            collapsABadDetails()
        else
            expandABadDetails()
    }
}
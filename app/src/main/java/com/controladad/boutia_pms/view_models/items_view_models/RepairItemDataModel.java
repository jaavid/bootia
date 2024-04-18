package com.controladad.boutia_pms.view_models.items_view_models;


import android.view.View;

import com.controladad.boutia_pms.adapters.RepairCardItemAdapter;
import com.controladad.boutia_pms.utility.Constants;

import java.util.ArrayList;
import java.util.List;

import cn.refactor.library.SmoothCheckBox;
import lombok.Getter;
import lombok.Setter;

public class RepairItemDataModel implements GeneralDataModel {
    private final SmoothCheckBox.OnCheckedChangeListener onCheckedChangeListener;
    @Getter
    private String repairTitle;
    @Setter
    @Getter
    private String repairDescription;
    @Setter
    @Getter
    private boolean isDone;
    private TextSetter setter;
    private TextGetter getter;
    @Getter
    private String userDescription;
    @Setter
    private SmoothCheckBox smoothCheckBox;

    public void setUserDescription(String userDescription) {
        this.userDescription = userDescription;
        setter.setText(userDescription);
    }

    @Getter
    private RepairCardItemAdapter adapter = new RepairCardItemAdapter();

    public RepairItemDataModel(String repairTitle, String repairDescription, boolean isDone,
                               TextSetter setter, TextGetter getter, SmoothCheckBox.OnCheckedChangeListener onCheckedChangeListener) {

        this.repairTitle = repairTitle;
        this.repairDescription = repairDescription;
        this.isDone = isDone;
        this.setter = setter;
        this.getter = getter;
        this.onCheckedChangeListener = onCheckedChangeListener;

        List<SimpleTextViewDataModel> textViewDataModels = new ArrayList<>();
        textViewDataModels.add(new SimpleTextViewDataModel(repairDescription, ()->"", (s)->{}));
        adapter.updateData(textViewDataModels);
    }

    public View.OnClickListener onClickListener(){
        return v -> {
            isDone = !isDone;
            smoothCheckBox.setChecked(isDone,true);
            if(onCheckedChangeListener!=null)
                onCheckedChangeListener.onCheckedChanged(null,isDone);
        };
    }

    @Override
    public int getKey() {
        return Constants.REPAIR_ITEM_KEY;
    }

    @Override
    public String getItemName() {
        return null;
    }

    @Override
    public void setItemName(String itemName) {

    }

    @Override
    public boolean isItemFilled() {
        return false;
    }
}

package com.controladad.boutia_pms.view_models.items_view_models;

import android.view.View;

import com.controladad.boutia_pms.adapters.GeneralAdapter;
import com.controladad.boutia_pms.utility.Constants;

import java.util.List;
import java.util.Objects;

import cn.refactor.library.SmoothCheckBox;
import lombok.Getter;
import lombok.Setter;

public class CheckBoxDataModel implements GeneralDataModel {
    @Setter
    @Getter
    private boolean isUserSelected;
    @Getter
    private String text;
    @Getter
    private SmoothCheckBox.OnCheckedChangeListener onCheckedChangeListener;
    private TextSetter setter;
    private TextGetter getter;
    private GeneralAdapter adapter;
    private List<GeneralDataModel> fatherDataModelList;
    @Getter
    private GeneralDataModel childDataModel;
    @Setter
    private View checkBox;

    @Setter
    private String itemName;

    public String getItemName() {
        if(itemName == null)
            itemName = text;
        if(!isItemFilled())
            return childDataModel.getItemName()+" مربوط به "+itemName;
        return itemName;
    }

    @Override
    public boolean isItemFilled() {
        return !(isUserSelected && childDataModel != null && !childDataModel.isItemFilled());
    }

    public CheckBoxDataModel(String text, SmoothCheckBox.OnCheckedChangeListener onCheckedChangeListener, TextGetter getter, TextSetter setter) {
        this.text = text;
        this.onCheckedChangeListener = onCheckedChangeListener;
        this.setter = setter;
        this.getter = getter;
        if(Objects.equals(getter.getText(),Constants.IS_CHECKED)) {
            isUserSelected = true;
        }
        else isUserSelected = false;
    }

    public CheckBoxDataModel(String text, GeneralAdapter adapter, List<GeneralDataModel> fatherDataModelList, GeneralDataModel childDataModel, TextGetter getter, TextSetter setter) {
        this.text = text;
        this.setter = setter;
        this.getter = getter;
        this.adapter = adapter;
        this.fatherDataModelList = fatherDataModelList;
        this.childDataModel = childDataModel;
        if(Objects.equals(getter.getText(),Constants.IS_CHECKED)) {
            isUserSelected = true;
        }
        else isUserSelected = false;    }

    public View.OnClickListener onClickListener(){
        return v -> {
            isUserSelected = !isUserSelected;
            ((SmoothCheckBox)v).setChecked(isUserSelected,true);
            if(onCheckedChangeListener!=null)
                onCheckedChangeListener.onCheckedChanged((SmoothCheckBox) v,isUserSelected);
            if(childDataModel !=null) addChildToCheckBox();
            if(isUserSelected)
            setter.setText(Constants.IS_CHECKED);
            else setter.setText(Constants.NO_CHECKED);
        };
    }
    public View.OnClickListener onTextClicked(){
        return v -> {
           onClickListener().onClick(checkBox);
        };
    }

    private void addChildToCheckBox(){
            List<GeneralDataModel> dataModelList = adapter.getItemsModelList();
            int position = dataModelList.indexOf(this)+1;
            if(isUserSelected && !dataModelList.contains(childDataModel)) {
                dataModelList.add(position , childDataModel);
                fatherDataModelList.add(fatherDataModelList.indexOf(this)+1, childDataModel);
                try {
                    adapter.notifyItemInserted(position);
                }catch (IllegalStateException e){
                    adapter.setHasItemsChanged(true);
                }
            }
            else if(!isUserSelected && dataModelList.contains(childDataModel)){
                dataModelList.remove(childDataModel);
                fatherDataModelList.remove(childDataModel);
                try {
                    adapter.notifyItemRemoved(position);
                }catch (IllegalStateException e){
                    adapter.setHasItemsChanged(true);
                }
            }
    }


    @Override
    public int getKey() {
        return Constants.CHECK_BOX_CARD_ITEM_KEY;
    }
}

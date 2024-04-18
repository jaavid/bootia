package com.controladad.boutia_pms.view_models.items_view_models;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import android.view.View;
import android.widget.RadioButton;

import com.controladad.boutia_pms.BR;
import com.controladad.boutia_pms.adapters.GeneralAdapter;
import com.controladad.boutia_pms.adapters.ReviewFirstLevelAdapter;
import com.controladad.boutia_pms.utility.Constants;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import lombok.Getter;
import lombok.Setter;

public class GoodBadDataModel extends BaseObservable implements GeneralDataModel {

    private int numberOfAddingItems;
    @Getter
    private String itemTitle;

    @Getter
    private boolean isGood;

    @Setter
    private String itemName;

    public String getItemName() {
        if (itemName == null)
            itemName = itemTitle;
        return itemName;
    }

    public boolean isItemFilled() {
        if (isBad) {
            if (childDataModel.size() == 0)
                return true;
            /*if (descriptionTextDataModel.isItemFilled())
                return true;*/
            for (GeneralDataModel dataModel : childDataModel) {
                if (dataModel.isItemFilled() && !(dataModel instanceof CheckBoxDataModel)
                        && !(dataModel instanceof DoubleButtonDataModel)
                        && !(dataModel instanceof SimpleTextViewDataModel)
                        && !(dataModel instanceof BugsDataModel)
                )
                    return true;
                if(dataModel instanceof CheckBoxDataModel && ((CheckBoxDataModel)dataModel).isUserSelected()){
                    return true;
                }
            }
            return false;
        }
        return isGood;
    }

    @Bindable
    @Getter
    private String goodButtonText = "خوب";

    @Bindable
    @Getter
    private String badButtonText = "بد";

    public void setGoodButtonText(String goodButtonText) {
        this.goodButtonText = goodButtonText;
        notifyPropertyChanged(BR.goodButtonText);
    }

    public void setBadButtonText(String badButtonText) {
        this.badButtonText = badButtonText;
        notifyPropertyChanged(BR.badButtonText);
    }

    @Getter
    private boolean isBad;
    private TextGetter getter;
    private TextSetter setter;
    private TextGetter discriptionGetter;
    private TextSetter discriptionSetter;
    @Bindable
    @Getter
    private int expandingItemVisibility = View.GONE;
    private int sizeOfChilds;

    @Bindable
    @Getter
    private boolean isExpanded;

    @Bindable
    @Setter
    private boolean animate;

    public boolean isAnimate() {
        if (animate) {
            animate = false;
            return true;
        }
        return false;
    }

    public void setExpanded(boolean expanded) {
        isExpanded = expanded;
        animate = true;
        notifyPropertyChanged(BR.isExpanded);
        notifyPropertyChanged(BR.animate);
    }

    private BugsDataModel bugsDataModel;
    private SingleButtonDataModel buttonDataModel;
    private boolean isWithoutOkButton;
    private CustomEditTextDataModel descriptionTextDataModel;


    @Getter
    private List<GeneralDataModel> childDataModel = new ArrayList<>();

    private GeneralAdapter adapter;

    public GoodBadDataModel(String itemTitle, List<GeneralDataModel> childDataModel, GeneralAdapter adapter, TextGetter getter, TextSetter setter, TextGetter descriptionGetter, TextSetter descriptionSetter) {
        this.itemTitle = itemTitle;
        this.childDataModel = childDataModel;
        this.adapter = adapter;
        this.getter = getter;
        this.setter = setter;
        isBad = Objects.equals(Constants.IS_BAD, getter.getText());
        isGood = Objects.equals(Constants.IS_GOOD, getter.getText());
        buttonDataModel = new SingleButtonDataModel("تایید", onClickListener(), () -> "", (s) -> {
        });
        bugsDataModel = new BugsDataModel();
        if (isBad) expandingItemVisibility = View.VISIBLE;
        int j = 0;
        int k = childDataModel.size();
        for (int i = 0; i < k; i++) {
            GeneralDataModel dataModel = childDataModel.get(i + j);
            if (dataModel instanceof CheckBoxDataModel) {
                CheckBoxDataModel checkBoxDataModel = (CheckBoxDataModel) dataModel;
                if (checkBoxDataModel.getChildDataModel() != null && checkBoxDataModel.isUserSelected()
                        && !childDataModel.contains(checkBoxDataModel.getChildDataModel())) {
                    childDataModel.add(i + j + 1, ((CheckBoxDataModel) dataModel).getChildDataModel());
                    j = j + 1;
                }
            }
        }
        sizeOfChilds = childDataModel.size();
        descriptionTextDataModel = new CustomEditTextDataModel("توضیحات", descriptionGetter, descriptionSetter);
        this.discriptionGetter = descriptionGetter;
        this.discriptionSetter = descriptionSetter;
    }

    public GoodBadDataModel(String itemTitle, List<GeneralDataModel> childDataModel, GeneralAdapter adapter, TextGetter getter, TextSetter setter, String bugsTitle, TextGetter discriptionGetter, TextSetter discriptionSetter) {
        this.itemTitle = itemTitle;
        this.childDataModel = childDataModel;
        this.adapter = adapter;
        this.getter = getter;
        this.setter = setter;
        isBad = Objects.equals(Constants.IS_BAD, getter.getText());
        isGood = Objects.equals(Constants.IS_GOOD, getter.getText());
        buttonDataModel = new SingleButtonDataModel("تایید", onClickListener(), () -> "", (s) -> {
        });
        bugsDataModel = new BugsDataModel(bugsTitle);
        if (isBad) expandingItemVisibility = View.VISIBLE;
        int j = 0;
        int k = childDataModel.size();
        for (int i = 0; i < k; i++) {
            GeneralDataModel dataModel = childDataModel.get(i + j);
            if (dataModel instanceof CheckBoxDataModel) {
                CheckBoxDataModel checkBoxDataModel = (CheckBoxDataModel) dataModel;
                if (checkBoxDataModel.getChildDataModel() != null && checkBoxDataModel.isUserSelected()
                        && !childDataModel.contains(checkBoxDataModel.getChildDataModel())) {
                    childDataModel.add(i + j + 1, ((CheckBoxDataModel) dataModel).getChildDataModel());
                    j = j + 1;
                }
            }
        }
        sizeOfChilds = childDataModel.size();
        descriptionTextDataModel = new CustomEditTextDataModel("توضیحات", discriptionGetter, discriptionSetter);
        this.discriptionGetter = discriptionGetter;
        this.discriptionSetter = discriptionSetter;
    }

    public GoodBadDataModel(String itemTitle, List<GeneralDataModel> childDataModelList, ReviewFirstLevelAdapter adapter, TextGetter getter, TextSetter setter, boolean withoutOkButton, int numberOfAddingItems, TextGetter discriptionGetter, TextSetter discriptionSetter) {
        this.itemTitle = itemTitle;
        this.childDataModel = childDataModelList;
        this.adapter = adapter;
        this.getter = getter;
        this.setter = setter;
        isBad = Objects.equals(Constants.IS_BAD, getter.getText());
        isGood = Objects.equals(Constants.IS_GOOD, getter.getText());
        buttonDataModel = new SingleButtonDataModel("تایید", onClickListener(), () -> "", (s) -> {
        });
        bugsDataModel = new BugsDataModel();
        isWithoutOkButton = withoutOkButton;
        this.numberOfAddingItems = numberOfAddingItems;
        if (isBad) expandingItemVisibility = View.VISIBLE;
        int j = 0;
        int k = childDataModel.size();
        for (int i = 0; i < k; i++) {
            GeneralDataModel dataModel = childDataModel.get(i + j);
            if (dataModel instanceof CheckBoxDataModel) {
                CheckBoxDataModel checkBoxDataModel = (CheckBoxDataModel) dataModel;
                if (checkBoxDataModel.getChildDataModel() != null && checkBoxDataModel.isUserSelected()
                        && !childDataModel.contains(checkBoxDataModel.getChildDataModel())) {
                    childDataModel.add(i + j + 1, ((CheckBoxDataModel) dataModel).getChildDataModel());
                    j = j + 1;
                }
            }
        }
        sizeOfChilds = childDataModel.size();
        descriptionTextDataModel = new CustomEditTextDataModel("توضیحات", discriptionGetter, discriptionSetter);
        this.discriptionGetter = discriptionGetter;
        this.discriptionSetter = discriptionSetter;
    }

    public View.OnClickListener onClickListener() {
        return v -> {
            int position = adapter.getItemsModelList().indexOf(this);
            for (GeneralDataModel dataModel : childDataModel) {
                adapter.getItemsModelList().remove(dataModel);
            }
            int itemCount = childDataModel.size() + 1;
            adapter.getItemsModelList().remove(bugsDataModel);
            adapter.getItemsModelList().remove(descriptionTextDataModel);
            itemCount += 1;
            if (!isWithoutOkButton) {
                adapter.getItemsModelList().remove(buttonDataModel);
                itemCount += 1;
            }
            try {
                adapter.notifyItemRangeRemoved(position + 1, itemCount);
            } catch (IllegalStateException e) {
                adapter.setHasItemsChanged(true);
            }
            setExpanded(false);
        };
    }

    public View.OnClickListener onExpandingClicked() {
        return v -> {
            if (isExpanded) onClickListener().onClick(v);
            else {
                int position = adapter.getItemsModelList().indexOf(this);
                int addedItemsNumber = childDataModel.size();
                adapter.getItemsModelList().add(position + 1, bugsDataModel);
                addedItemsNumber += 1;
                for (int i = 0; i < childDataModel.size(); i++) {
                    adapter.getItemsModelList().add(position + i + 2, childDataModel.get(i));
                }
                adapter.getItemsModelList().add(position + childDataModel.size() + 2, descriptionTextDataModel);
                addedItemsNumber += 1;
                if (!isWithoutOkButton) {
                    addedItemsNumber += 1;
                    adapter.getItemsModelList().add(position + childDataModel.size() + 3, buttonDataModel);
                }
                try {
                    adapter.notifyItemRangeInserted(position + 1, addedItemsNumber);
                } catch (IllegalStateException e) {
                    adapter.setHasItemsChanged(true);
                }
                setExpanded(true);
            }
        };
    }


    @Override
    public int getKey() {
        return Constants.GOOD_BAD_CARD_ITEM_KEY;
    }

    public View.OnClickListener onBadClickListener() {
        return v -> {
            isBad = ((RadioButton) v).isChecked();
            isGood = !isBad;
            setter.setText(Constants.IS_BAD);
            onCheckedChangedListener();
            if (isGood) expandingItemVisibility = View.GONE;
            if (isBad) expandingItemVisibility = View.VISIBLE;
            notifyPropertyChanged(BR.expandingItemVisibility);
        };
    }

    public View.OnClickListener onGoodClickListener() {
        return v -> {
            isGood = ((RadioButton) v).isChecked();
            isBad = !isGood;
            setter.setText(Constants.IS_GOOD);
            onCheckedChangedListener();
            if (isGood) expandingItemVisibility = View.GONE;
            if (isBad) expandingItemVisibility = View.VISIBLE;
            notifyPropertyChanged(BR.expandingItemVisibility);
        };
    }


    public void onCheckedChangedListener() {
        int position = adapter.getItemsModelList().indexOf(this);
        int addedItemsNumber = childDataModel.size();
        if (isBad && !adapter.getItemsModelList().contains(bugsDataModel)) {
            adapter.getItemsModelList().add(position + 1, bugsDataModel);
            addedItemsNumber += 1;
            for (int i = 0; i < childDataModel.size(); i++) {
                adapter.getItemsModelList().add(position + i + 2, childDataModel.get(i));
            }
            adapter.getItemsModelList().add(position + childDataModel.size() + 2, descriptionTextDataModel);
            addedItemsNumber += 1;
            if (!isWithoutOkButton) {
                addedItemsNumber += 1;
                adapter.getItemsModelList().add(position + childDataModel.size() + 3, buttonDataModel);
            }
            try {
                adapter.notifyItemRangeInserted(position + 1, addedItemsNumber);
            } catch (IllegalStateException e) {
                adapter.setHasItemsChanged(true);
            }
            setExpanded(true);
        } else if (isGood && adapter.getItemsModelList().contains(bugsDataModel)) {
            for (GeneralDataModel dataModel : childDataModel) {
                adapter.getItemsModelList().remove(dataModel);
            }
            adapter.getItemsModelList().remove(bugsDataModel);
            addedItemsNumber += 1;
            adapter.getItemsModelList().remove(descriptionTextDataModel);
            addedItemsNumber += 1;
            if (!isWithoutOkButton) {
                adapter.getItemsModelList().remove(buttonDataModel);
                addedItemsNumber += 1;
            }
            try {
                adapter.notifyItemRangeRemoved(position + 1, addedItemsNumber);
            } catch (IllegalStateException e) {
                adapter.setHasItemsChanged(true);
            }
            setExpanded(false);
        }
    }

    public View.OnClickListener onRemoveItemClicked(BugsDataModel bugsDataModel) {
        return v -> {
            if (sizeOfChilds < childDataModel.size()) {
                int position = adapter.getItemsModelList().indexOf(bugsDataModel);
                for (int i = 0; i < numberOfAddingItems; i++) {
                    try {

                        GeneralDataModel dataModel = adapter.getItemsModelList().get(position);
                        adapter.getItemsModelList().remove(position);
                        childDataModel.remove(dataModel);
                    } catch (ArrayIndexOutOfBoundsException e) {
                        e.printStackTrace();
                    }
                }
                try {
                    adapter.notifyItemRangeRemoved(position, numberOfAddingItems);
                } catch (IllegalStateException e) {
                    adapter.setHasItemsChanged(true);
                }
            }
        };
    }
}

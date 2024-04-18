package com.controladad.boutia_pms.view_models.items_view_models;

import com.controladad.boutia_pms.utility.Constants;

import lombok.Getter;
import lombok.Setter;

public class ImagesItemsDataModel implements GeneralDataModel {

    @Getter
    private String imagePath;

    @Setter
    @Getter
    private String itemName;
    @Getter
    private boolean isItemFilled = true;

    public ImagesItemsDataModel(String imagePath) {
        this.imagePath = imagePath;
    }

    @Override
    public int getKey() {
        return Constants.IMAGES_ITEM_KEY;
    }
}

package com.controladad.boutia_pms.models.database;

import lombok.Getter;
import lombok.Setter;

public class MissionModelWithDispatching {
    @Getter
    @Setter
    private int mId;
    @Getter
    @Setter
    private String mTitle;
    @Getter
    @Setter
    private String mType;
    @Getter
    @Setter
    private String mOpStart;
    @Getter
    @Setter
    private String mOpEnd;


    @Getter
    @Setter
    private String mVoltage;

    @Getter
    @Setter
    private String mCode;

    @Getter
    @Setter
    private String mWidth;
}

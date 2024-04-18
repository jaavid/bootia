package com.controladad.boutia_pms.models.database;

import lombok.Getter;
import lombok.Setter;
public class ReportTamir {

    @Setter
    @Getter
    private int nId;

    @Setter
    @Getter
    private String dakalNumber;

    @Setter
    @Getter
    private String title;

    @Setter
    @Getter
    private String type;

    @Setter
    @Getter
    private String date;

    @Setter
    @Getter
    private boolean isUploading;
}

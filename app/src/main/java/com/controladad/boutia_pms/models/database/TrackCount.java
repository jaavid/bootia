package com.controladad.boutia_pms.models.database;


import lombok.Getter;
import lombok.Setter;
public class TrackCount {

        //Declare student id as a primary key in database will auto generate by Room
    @Setter
    @Getter
    private String missionName;

    @Setter
    @Getter
    private String mId;

    @Setter
    @Getter
    private String mIdCount;

    @Setter
    @Getter
    private int isUploadingCount;

    @Setter
    @Getter
    private int id;

    @Setter
    @Getter
    private String type;

    @Setter
    @Getter
    private String date;

}

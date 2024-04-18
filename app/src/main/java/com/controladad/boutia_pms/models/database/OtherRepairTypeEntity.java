package com.controladad.boutia_pms.models.database;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import lombok.Getter;
import lombok.Setter;

@Entity(tableName = "Tamir_Gheyre_Routin")
public class OtherRepairTypeEntity {

    @PrimaryKey(autoGenerate = true)
    @Getter
    @Setter
    private int nId;

    @Getter
    @Setter
    private String mId;


    @ColumnInfo(name = "lat")
    @Getter
    @Setter
    private String lat;


    @ColumnInfo(name = "lng")
    @Getter
    @Setter
    private String lng;


    @ColumnInfo(name = "submit_date")
    @Getter
    @Setter
    private String submitDate;

    @ColumnInfo(name = "elate_takhir")
    @Getter
    @Setter
    private String elateTakhir;

    @ColumnInfo(name = "operation_time")
    @Getter
    @Setter
    private String operationTime;

    @ColumnInfo(name = "repair_type")
    @Getter
    @Setter
    private String repairType;

    @ColumnInfo(name = "repair_dec")
    @Getter
    @Setter
    private String repairDescription;

    public OtherRepairTypeEntity(String  mId, String lat, String lng, String submitDate, String elateTakhir,
                                 String operationTime, String repairType, String repairDescription) {
        this.mId = mId;
        this.lat = lat;
        this.lng = lng;
        this.submitDate = submitDate;
        this.elateTakhir = elateTakhir;
        this.operationTime = operationTime;
        this.repairType = repairType;
        this.repairDescription = repairDescription;
    }
}

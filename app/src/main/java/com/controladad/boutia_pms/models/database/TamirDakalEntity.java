package com.controladad.boutia_pms.models.database;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import lombok.Getter;
import lombok.Setter;
@Entity(tableName = "Tamir_Dakal")
public class TamirDakalEntity {

    @PrimaryKey(autoGenerate = true)
    @Getter
    @Setter
    private int nId;

    @Getter
    @Setter
    private int mId;


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


    @ColumnInfo(name = "barcode")
    @Getter
    @Setter
    private String barcode;


    @ColumnInfo(name = "dispatching_number")
    @Getter
    @Setter
    private String dispatching_number;


    @ColumnInfo(name = "dakal_number")
    @Getter
    @Setter
    private String dakalNumber;

    @ColumnInfo(name = "is_uploading")
    private boolean isUploading;

    public boolean getIsUploading() {
        return isUploading;
    }

    public void setIsUploading(boolean uploading) {
        isUploading = uploading;
    }

    @ColumnInfo(name = "elate_takhir")
    @Getter
    @Setter
    private String elateTakhir;

    @ColumnInfo(name = "operation_time")
    @Getter
    @Setter
    private String operationTime;

    @ColumnInfo(name = "foundation_repaired")
    @Getter
    @Setter
    private boolean foundationRepaired;

    @ColumnInfo(name = "foundation_repair_dec")
    @Getter
    @Setter
    private String foundationRepairedDec;


    @ColumnInfo(name = "tablo_repaired")
    @Getter
    @Setter
    private boolean tabloRepaired;

    @ColumnInfo(name = "tablo_repair_dec")
    @Getter
    @Setter
    private String tabloRepairedDec;


    @ColumnInfo(name = "simzamin_repaired")
    @Getter
    @Setter
    private boolean simzaminRepaired;

    @ColumnInfo(name = "simzamin_repair_dec")
    @Getter
    @Setter
    private String simzaminRepairedDec;


    @ColumnInfo(name = "pich_repaired")
    @Getter
    @Setter
    private boolean pichRepaired;

    @ColumnInfo(name = "pich_repair_dec")
    @Getter
    @Setter
    private String pichRepairedDec;


    @ColumnInfo(name = "pich_pelle_repaired")
    @Getter
    @Setter
    private boolean pichPelleRepaired;

    @ColumnInfo(name = "pich_pelle_repair_dec")
    @Getter
    @Setter
    private String pichPelleRepairedDec;


    @ColumnInfo(name = "khar_repaired")
    @Getter
    @Setter
    private boolean kharRepaired;

    @ColumnInfo(name = "khar_repair_dec")
    @Getter
    @Setter
    private String kharRepairedDec;


    @ColumnInfo(name = "plate_repaired")
    @Getter
    @Setter
    private boolean plateRepaired;

    @ColumnInfo(name = "plate_repair_dec")
    @Getter
    @Setter
    private String plateRepairedDec;


    @ColumnInfo(name = "nabshi_repaired")
    @Getter
    @Setter
    private boolean nabshiRepaired;

    @ColumnInfo(name = "nabshi_repair_dec")
    @Getter
    @Setter
    private String nabshiRepairedDec;



    @ColumnInfo(name = "seil_repaired")
    @Getter
    @Setter
    private boolean seilRepaired;

    @ColumnInfo(name = "seil_repair_dec")
    @Getter
    @Setter
    private String seilRepairedDec;


    @ColumnInfo(name = "hadi_repaired")
    @Getter
    @Setter
    private boolean hadiRepaired;

    @ColumnInfo(name = "hadi_repair_dec")
    @Getter
    @Setter
    private String hadiRepairedDec;


    @ColumnInfo(name = "yaragh_repaired")
    @Getter
    @Setter
    private boolean yaraghRepaired;

    @ColumnInfo(name = "yaragh_repair_dec")
    @Getter
    @Setter
    private String yaraghRepairedDec;


    @ColumnInfo(name = "zmm_repaired")
    @Getter
    @Setter
    private boolean zmmRepaired;

    @ColumnInfo(name = "zmm_repair_dec")
    @Getter
    @Setter
    private String zmmRepairedDec;


    @ColumnInfo(name = "mohafez_repaired")
    @Getter
    @Setter
    private boolean mohafezRepaired;

    @ColumnInfo(name = "mohafez_repair_dec")
    @Getter
    @Setter
    private String mohafezRepairedDec;


    @ColumnInfo(name = "lane_repaired")
    @Getter
    @Setter
    private boolean laneRepaired;

    @ColumnInfo(name = "lane_repair_dec")
    @Getter
    @Setter
    private String laneRepairedDec;


    @ColumnInfo(name = "ezafe_repaired")
    @Getter
    @Setter
    private boolean ezafeRepaired;

    @ColumnInfo(name = "ezafe_repair_dec")
    @Getter
    @Setter
    private String ezafeRepairedDec;

    @ColumnInfo(name = "scan_type")
    @Getter
    @Setter
    private String mScanType;

    @ColumnInfo(name = "start_time")
    @Getter
    @Setter
    private String startTime;

    @ColumnInfo(name = "end_time")
    @Getter
    @Setter
    private String endTime;


}

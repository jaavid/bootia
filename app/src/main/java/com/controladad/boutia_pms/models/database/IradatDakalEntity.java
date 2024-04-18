package com.controladad.boutia_pms.models.database;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import lombok.Getter;
import lombok.Setter;
@Entity(tableName = "Iradat_Dakal")
public class IradatDakalEntity {

        @PrimaryKey(autoGenerate = true)
        @Getter
        @Setter
        private int nId;

        @Getter
        @Setter
        private int mId;

        @ColumnInfo(name = "is_repaired")
        @Getter
        @Setter
        private boolean isRepaired;

        @ColumnInfo(name = "barcode")
        @Getter
        @Setter
        private String barcode;

        @ColumnInfo(name = "foundation")
        @Getter
        @Setter
        private String foundation;

        @ColumnInfo(name = "tablo")
        @Getter
        @Setter
        private String tablo;

        @ColumnInfo(name = "simzamin")
        @Getter
        @Setter
        private String simzamin;

        @ColumnInfo(name = "pich")
        @Getter
        @Setter
        private String pich;

        @ColumnInfo(name = "pich_pelle")
        @Getter
        @Setter
        private String pichPelle;

        @ColumnInfo(name = "khar")
        @Getter
        @Setter
        private String khar;

        @ColumnInfo(name = "plate")
        @Getter
        @Setter
        private String plate;

        @ColumnInfo(name = "nabshi")
        @Getter
        @Setter
        private String nabshi;

        @ColumnInfo(name = "seil")
        @Getter
        @Setter
        private String seil;

        @ColumnInfo(name = "hadi")
        @Getter
        @Setter
        private String hadi;

        @ColumnInfo(name = "yaragh")
        @Getter
        @Setter
        private String yaragh;

        @ColumnInfo(name = "zmm")
        @Getter
        @Setter
        private String zmm;

        @ColumnInfo(name = "mohafez")
        @Getter
        @Setter
        private String mohafez;

        @ColumnInfo(name = "lane")
        @Getter
        @Setter
        private String lane;

        @ColumnInfo(name = "ezafe")
        @Getter
        @Setter
        private String ezafe;

        @ColumnInfo(name = "time")
        @Getter
        @Setter
        private String timeStamp;

        public IradatDakalEntity(int mId, boolean isRepaired, String barcode, String foundation,
                                 String tablo, String simzamin, String pich, String pichPelle,
                                 String khar, String plate, String nabshi, String seil, String hadi,
                                 String yaragh, String zmm, String mohafez, String lane, String ezafe, String timeStamp) {
                this.mId = mId;
                this.isRepaired = isRepaired;
                this.barcode = barcode;
                this.foundation = foundation;
                this.tablo = tablo;
                this.simzamin = simzamin;
                this.pich = pich;
                this.pichPelle = pichPelle;
                this.khar = khar;
                this.plate = plate;
                this.nabshi = nabshi;
                this.seil = seil;
                this.hadi = hadi;
                this.yaragh = yaragh;
                this.zmm = zmm;
                this.mohafez = mohafez;
                this.lane = lane;
                this.ezafe = ezafe;
                this.timeStamp = timeStamp;
        }
}

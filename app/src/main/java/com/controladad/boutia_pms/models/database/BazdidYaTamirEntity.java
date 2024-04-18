package com.controladad.boutia_pms.models.database;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import lombok.Getter;
import lombok.Setter;

@Entity(tableName = "BazdidYaTamir")//, foreignKeys = @ForeignKey(entity = Mission_Entity.class, parentColumns = "mId", childColumns = "mId", onDelete = CASCADE)
public class BazdidYaTamirEntity {
    @PrimaryKey(autoGenerate = true)
    @Setter
    @Getter
    private int nId;//P.K

    @Getter
    @Setter
    private int mId;//F.K

    @ColumnInfo(name = "lat")
    @Getter
    @Setter
    private String mLat;

    @ColumnInfo(name = "lng")
    @Getter
    @Setter
    private String mLng;

    @ColumnInfo(name = "date")
    @Getter
    @Setter
    private String mDate;

    @ColumnInfo(name = "is_uploading")
    @Getter
    @Setter
    private boolean mIsUploading;


    @ColumnInfo(name = "type")
    @Getter
    @Setter
    private String mType;

    @ColumnInfo(name = "field_dakal")
    @Getter
    @Setter
    private String mFieldDakal;

    @ColumnInfo(name = "fondasion")
    @Getter
    @Setter
    private String mFondasion;

    @ColumnInfo(name = "sim_zamin")
    @Getter
    @Setter
    private String mSimZamin;

    @ColumnInfo(name = "tablo")
    @Getter
    @Setter
    private String mTablo;

    @ColumnInfo(name = "pich")
    @Getter
    @Setter
    private String mPich;

    @ColumnInfo(name = "pich_pelle")
    @Getter
    @Setter
    private String mPichPelle;

    @ColumnInfo(name = "khaar")
    @Getter
    @Setter
    private String mKhaar;

    @ColumnInfo(name = "plate")
    @Getter
    @Setter
    private String mPlate;

    @ColumnInfo(name = "joshkari")
    @Getter
    @Setter
    private String mJoshkari;

    @ColumnInfo(name = "nabshi")
    @Getter
    @Setter
    private String mNabshi;

    @ColumnInfo(name = "seil")
    @Getter
    @Setter
    private String mSeil;

    @ColumnInfo(name = "hadi")
    @Getter
    @Setter
    private String mHadi;

    @ColumnInfo(name = "yaragh")
    @Getter
    @Setter
    private String mYaragh;

    @ColumnInfo(name = "zanjire_maghare")
    @Getter
    @Setter
    private String mZanjireMaghare;

    @ColumnInfo(name = "sim_mohafez")
    @Getter
    @Setter
    private String mSimMohafez;

    @ColumnInfo(name = "lane_parande")
    @Getter
    @Setter
    private String mLaneParande;

    @ColumnInfo(name = "ashya_ezafe")
    @Getter
    @Setter
    private String mAshyaEzafe;

    @ColumnInfo(name = "khamoshi_khat")
    @Getter
    @Setter
    private String mKhamoshiKhat;

    @ColumnInfo(name = "dakal_bohrani")
    @Getter
    @Setter
    private String mDakalBohrani;

    @ColumnInfo(name = "jade_asli")
    @Getter
    @Setter
    private String mJadeAsli;

    @ColumnInfo(name = "mavane")
    @Getter
    @Setter
    private String mMavane;

    @ColumnInfo(name = "ensheab")
    @Getter
    @Setter
    private String mEnsheab;

    @ColumnInfo(name = "taghato_ba_bist")
    @Getter
    @Setter
    private String mTaghatoBaBist;

    @ColumnInfo(name = "loole")
    @Getter
    @Setter
    private String mLoole;

    @ColumnInfo(name = "goy")
    @Getter
    @Setter
    private String mGoyEkhtar;

    @ColumnInfo(name = "harim")
    @Getter
    @Setter
    private String mHarim;

    @ColumnInfo(name = "masir")
    @Getter
    @Setter
    private String mMasir;
    @ColumnInfo(name = "images_path")
    @Getter
    @Setter
    private String mImagesPath;
    @ColumnInfo(name = "images_fid")
    @Getter
    @Setter
    private String mImagesFid;

    @ColumnInfo(name = "scan_type")
    @Getter
    @Setter
    private String mScanType;

    public BazdidYaTamirEntity(int mId, String mLat, String mLng, String mDate, boolean mIsUploading, String mType,
                               String mFieldDakal, String mFondasion, String mSimZamin, String mTablo, String mPich,
                               String mPichPelle, String mKhaar, String mPlate, String mNabshi, String mSeil, String mHadi,
                               String mYaragh, String mZanjireMaghare, String mSimMohafez, String mLaneParande,
                               String mAshyaEzafe, String mKhamoshiKhat, String mJadeAsli, String mMavane,
                               String mEnsheab, String mTaghatoBaBist, String mLoole, String mHarim, String mMasir,
                               String mImagesPath, String mImagesFid, String mScanType, String mGoyEkhtar, String mDakalBohrani) {
        this.mId = mId;
        this.mLat = mLat;
        this.mLng = mLng;
        this.mDate = mDate;
        this.mIsUploading = mIsUploading;
        this.mType = mType;
        this.mFieldDakal = mFieldDakal;
        this.mFondasion = mFondasion;
        this.mSimZamin = mSimZamin;
        this.mTablo = mTablo;
        this.mPich = mPich;
        this.mPichPelle = mPichPelle;
        this.mKhaar = mKhaar;
        this.mPlate = mPlate;
        this.mNabshi = mNabshi;
        this.mSeil = mSeil;
        this.mHadi = mHadi;
        this.mYaragh = mYaragh;
        this.mZanjireMaghare = mZanjireMaghare;
        this.mSimMohafez = mSimMohafez;
        this.mLaneParande = mLaneParande;
        this.mAshyaEzafe = mAshyaEzafe;
        this.mKhamoshiKhat = mKhamoshiKhat;
        this.mJadeAsli = mJadeAsli;
        this.mMavane = mMavane;
        this.mEnsheab = mEnsheab;
        this.mTaghatoBaBist = mTaghatoBaBist;
        this.mLoole = mLoole;
        this.mHarim = mHarim;
        this.mMasir = mMasir;
        this.mImagesPath = mImagesPath;
        this.mImagesFid = mImagesFid;
        this.mScanType = mScanType;
        this.mGoyEkhtar = mGoyEkhtar;
        this.mDakalBohrani = mDakalBohrani;
    }
}


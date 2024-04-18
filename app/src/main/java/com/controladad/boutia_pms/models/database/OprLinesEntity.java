package com.controladad.boutia_pms.models.database;


import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import lombok.Getter;
import lombok.Setter;

import static androidx.room.ForeignKey.CASCADE;

@Entity(tableName = "OprLines",foreignKeys = @ForeignKey(entity = Mission_Entity.class, parentColumns = "mId", childColumns = "mId",onDelete = CASCADE))
public class OprLinesEntity {

        //Declare OprLinesEntity id as a primary key in database will auto generate by Room
        @PrimaryKey(autoGenerate = true)
        @Getter
        @Setter
        private int nId;


        @Getter
        @Setter
        private int mId;

        @ColumnInfo(name = "title")
        @Getter
        @Setter
        private String mTitle;

        @ColumnInfo (name = "voltage")
        @Getter
        @Setter
        private String mVoltage;

        @ColumnInfo (name = "code")
        @Getter
        @Setter
        private String mCode;

        @ColumnInfo (name = "width")
        @Getter
        @Setter
        private String mWidth;

    public OprLinesEntity( int mId, String mTitle, String mVoltage, String mCode, String mWidth) {

        this.mId = mId;
        this.mTitle = mTitle;
        this.mVoltage = mVoltage;
        this.mCode = mCode;
        this.mWidth = mWidth;
    }
}

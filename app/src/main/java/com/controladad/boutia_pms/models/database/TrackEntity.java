package com.controladad.boutia_pms.models.database;


import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import lombok.Getter;
import lombok.Setter;

@Entity(tableName = "Track")//,foreignKeys = @ForeignKey(entity = Mission_Entity.class, parentColumns = "mId", childColumns = "mId",onDelete = CASCADE)
public class TrackEntity {

        //Declare OprLinesEntity id as a primary key in database will auto generate by Room
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
        private String mLat;

        @ColumnInfo (name = "lng")
        @Getter
        @Setter
        private String mLng;

        @ColumnInfo (name = "date")
        @Getter
        @Setter
        private String mDate;

        @ColumnInfo (name = "year")
        @Getter
        @Setter
        private String mYear;

        @ColumnInfo (name = "month")
        @Getter
        @Setter
        private String mMonth;

        @ColumnInfo (name = "day")
        @Getter
        @Setter
        private String mDay;

        @ColumnInfo (name = "hour")
        @Getter
        @Setter
        private String mHour;

        @ColumnInfo (name = "minute")
        @Getter
        @Setter
        private String mMinute;

        @ColumnInfo (name = "second")
        @Getter
        @Setter
        private String mSecond;

        @ColumnInfo (name = "getIsUploading")
        @Getter
        @Setter
        private boolean isUploading;

        @ColumnInfo (name = "type")
        @Getter
        @Setter
        private String mType;

        public TrackEntity() {
        }

        public TrackEntity( int mId, String mLat, String mLng, String mDate, String mYear,
                            String mMonth, String mDay, String mHour, String mMinute,
                            String mSecond, boolean isUploading, String mType) {
                this.mId = mId;
                this.mLat = mLat;
                this.mLng = mLng;
                this.mDate = mDate;
                this.mYear = mYear;
                this.mMonth = mMonth;
                this.mDay = mDay;
                this.mHour = mHour;
                this.mMinute = mMinute;
                this.mSecond = mSecond;
                this.isUploading = isUploading;
                this.mType = mType;
        }
}

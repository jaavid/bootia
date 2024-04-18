package com.controladad.boutia_pms.models.database;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

import lombok.Getter;
import lombok.Setter;

@Entity(tableName = "Mission")
public class Mission_Entity {


    //Declare student id as a primary key in database will auto generate by Room
    @PrimaryKey(autoGenerate = true)
    @NonNull
    @Getter
    @Setter
    private int mId;
    @ColumnInfo(name = "title")
    @Getter
    @Setter
    private String mTitle;
    @ColumnInfo(name = "type")
    @Getter
    @Setter
    private String mType;
    @ColumnInfo(name = "op_start")
    @Getter
    @Setter
    private String mOpStart;
    @ColumnInfo(name = "op_end")
    @Getter
    @Setter
    private String mOpEnd;
    @ColumnInfo(name = "group_name")
    @Getter
    @Setter
    private String mGroupName;
    @ColumnInfo(name = "dispatching_code")
    @Getter
    @Setter
    private String mDispatchingCode;
    public Mission_Entity(int mId, String mTitle, String mType, String mOpStart, String mOpEnd, String mGroupName, String mDispatchingCode) {
        this.mId = mId;
        this.mTitle = mTitle;
        this.mType = mType;
        this.mOpStart = mOpStart;
        this.mOpEnd = mOpEnd;
        this.mGroupName = mGroupName;
        this.mDispatchingCode = mDispatchingCode;
    }
}

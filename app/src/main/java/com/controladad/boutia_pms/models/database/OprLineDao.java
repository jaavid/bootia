package com.controladad.boutia_pms.models.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.ArrayList;
import java.util.List;

@Dao
public interface OprLineDao {
    //Flowable or observable is used to emits Mission model types of data and it emits whenever database is updated
    @Query("SELECT * FROM OprLines")
    List<OprLinesEntity> getOprLines();

    @Query("SELECT * FROM OprLines WHERE mId = :mid")
    List<OprLinesEntity> getOprLinesMid(int mid);

    //Insert the parameters in table like list of Missions
    @Insert
    void insertOprLines(ArrayList<OprLinesEntity> oprLinesEntityArrayList);

    //This Query will delete all the students from table Missions
    @Query("DELETE FROM OprLines")
    void deleteAllOprLines();
}

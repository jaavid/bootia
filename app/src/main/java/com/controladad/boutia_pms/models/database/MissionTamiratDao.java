package com.controladad.boutia_pms.models.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Flowable;

@Dao
public interface MissionTamiratDao {

        @Query("SELECT * FROM Mission_Tamirat ORDER BY op_end DESC")
        Flowable<List<Mission_Tamirat_Entity>> getMissionTamirat();
        @Query("SELECT * FROM Mission_Tamirat WHERE op_end<:currentDate ORDER BY op_end DESC")
        Flowable<List<Mission_Tamirat_Entity>> getTakhirMission(String currentDate);

        @Query("SELECT * FROM Mission_Tamirat WHERE op_start>:currentDate ORDER BY op_end DESC")
        Flowable<List<Mission_Tamirat_Entity>> getTajilMission(String currentDate);

        @Query("SELECT * FROM Mission_Tamirat WHERE (op_start<:currentDate OR op_start=:currentDate) AND (op_end>:currentDate OR op_end=:currentDate) ORDER BY op_end DESC")
        Flowable<List<Mission_Tamirat_Entity>> getOnTimeMission(String currentDate);

        @Query("SELECT * FROM Mission_Tamirat WHERE dispatching_code LIKE :dispatchingCode")
        Flowable<List<Mission_Tamirat_Entity>> getRepairMissionWithCode(String dispatchingCode);

        //Insert the parameters in table like list of Missions
        @Insert
        void insertMissionTamirat(ArrayList<Mission_Tamirat_Entity> missionTamiratEntityArrayList);

        @Query("DELETE FROM Mission_Tamirat")
        void deleteAllTamiratMissions();

        //Update the status of track which has been sent
        @Query("UPDATE Mission_Tamirat SET last_update = :date WHERE mid = :mid")
        void updateTamirDate(int mid,String date);
}

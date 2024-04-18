package com.controladad.boutia_pms.models.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Flowable;

@Dao
public interface Mission_Dao {

    //Flowable or observable is used to emits Mission model types of data and it emits whenever database is updated
     /*   @Query("SELECT * FROM Mission")
        List<Mission_Entity> getMission();*/
    @Query("SELECT * FROM Mission ORDER BY op_end DESC")
    Flowable<List<Mission_Entity>> getMission();

    @Query("SELECT * FROM Mission WHERE op_end<:currentDate ORDER BY op_end DESC")
    Flowable<List<Mission_Entity>> getTakhirMission(String currentDate);

    @Query("SELECT * FROM Mission WHERE op_start>:currentDate ORDER BY op_end DESC")
    Flowable<List<Mission_Entity>> getTajilMission(String currentDate);

    @Query("SELECT * FROM Mission WHERE (op_start<:currentDate OR op_start=:currentDate) AND (op_end>:currentDate OR op_end=:currentDate) ORDER BY op_end DESC")
    Flowable<List<Mission_Entity>> getOnTimeMission(String currentDate);


       /* @Query("SELECT Mission.mId,Mission.title,Mission.type,Mission.op_start,Mission.op_end," +
                "OprLines.code,OprLines.voltage,OprLines.width FROM Mission" +
                " INNER JOIN OprLines ON Mission.mId = OprLines.mId ")
        Flowable<List<MissionModelWithDispatching>> getMissionWithLinesInfo();*/

    //test for list decrement
    @Query("SELECT Mission.mId,Mission.title,Mission.type,Mission.op_start,Mission.op_end FROM Mission" +
            " LEFT JOIN OprLines ON Mission.mId = OprLines.mId" +
            " WHERE OprLines.code LIKE :dispatchingCode")
    Flowable<List<Mission_Entity>> getMissionWithCode(String dispatchingCode);

    //Insert the parameters in table like list of Missions
    @Insert
    void insertMission(ArrayList<Mission_Entity> missionEntityArrayList);

    //This Query will delete all the students from table Missions
    @Query("DELETE FROM Mission")
    void deleteAllMissions();

/*        //This Maybe class is used to emit only a single row data
        @Query("SELECT * FROM Students WHERE StudentName =:aStudentName ")
        Maybe<StudentModel> getStudentByName(String aStudentName);

        //This Query will delete all the students from table Students
        @Query("DELETE FROM Students")
        void deleteAllStudents();*/


}

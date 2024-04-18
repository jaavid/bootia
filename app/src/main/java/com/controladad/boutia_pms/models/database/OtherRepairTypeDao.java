package com.controladad.boutia_pms.models.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import io.reactivex.Maybe;

@Dao
public interface OtherRepairTypeDao {

    @Query("SELECT * FROM Tamir_Gheyre_Routin WHERE mId = :mid")
    Maybe<OtherRepairTypeEntity> getTamirDakalByMid(int mid);

    @Query("SELECT * FROM Tamir_Gheyre_Routin WHERE nId = :nid")
    Maybe<OtherRepairTypeEntity> getTamirDakalByNid(int nid);

    @Query("DELETE FROM Tamir_Gheyre_Routin WHERE mId = :mid")
    void  deleteTamirDakal(int mid);

    @Query("SELECT * FROM Tamir_Gheyre_Routin")
    Maybe<List<OtherRepairTypeEntity>> getAllTamirDakalEntity();

    @Query("SELECT Tamir_Gheyre_Routin.nId AS nId, Tamir_Gheyre_Routin.repair_type AS repairType, Mission.title AS title," +
            "Mission.type AS type, Tamir_Gheyre_Routin.submit_date AS date" +
            " FROM Tamir_Gheyre_Routin LEFT JOIN Mission " +
            "ON Tamir_Gheyre_Routin.mId = Mission.mId")
    Maybe<List<ReportTamirOtherType>> getReportTamirOtherWithName();

    @Insert
    void insertIradatDakal(OtherRepairTypeEntity iradatDakalEntity);

    @Query("DELETE FROM Tamir_Gheyre_Routin")
    void deleteAllIradatDakal();

    @Query("DELETE FROM Tamir_Gheyre_Routin WHERE nId = :nid")
    void deleteTamirByNid(int nid);

}

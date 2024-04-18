package com.controladad.boutia_pms.models.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import io.reactivex.Maybe;

@Dao
public interface TamirDakalDao {


    @Query("SELECT * FROM tamir_dakal WHERE mId = :mid")
    Maybe<TamirDakalEntity> getTamirDakalByMid(int mid);

    @Query("SELECT * FROM tamir_dakal WHERE nId = :nid")
    Maybe<TamirDakalEntity> getTamirDakalByNid(int nid);

    @Query("DELETE FROM tamir_dakal WHERE mId = :mid")
    void  deleteTamirDakal(int mid);

    @Query("SELECT * FROM tamir_dakal")
    Maybe<List<TamirDakalEntity>> getAllTamirDakalEntity();

    @Query("SELECT Tamir_Dakal.nId AS nId,Tamir_dakal.dakal_number AS dakalNumber, Mission_Tamirat.title AS title," +
            "Mission_Tamirat.type AS type, Tamir_Dakal.submit_date AS date, Tamir_Dakal.is_uploading AS isUploading" +
            " FROM Tamir_dakal LEFT JOIN Mission_Tamirat " +
            "ON Tamir_dakal.mId = Mission_Tamirat.mId")
    Maybe<List<ReportTamir>> getReportTamirWithName();

    @Insert
    void insertIradatDakal(TamirDakalEntity iradatDakalEntity);

    //Update the status of track which has been sent
    @Query("UPDATE Tamir_Dakal SET is_uploading = 0 WHERE nId = :nid")
    void updateTamirUploadStatusToNotUploading(int nid);

    //Update the status of track which has been sent
    @Query("UPDATE Tamir_Dakal SET is_uploading = 1 WHERE nId = :nid")
    void updateTamirUploadStatusToUploading(int nid);

    @Query("DELETE FROM tamir_dakal")
    void deleteAllIradatDakal();

    @Query("DELETE FROM tamir_dakal WHERE nId = :nid")
    void deleteTamirByNid(int nid);

}
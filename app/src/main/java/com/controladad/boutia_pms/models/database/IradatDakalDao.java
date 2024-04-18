package com.controladad.boutia_pms.models.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Flowable;

@Dao
public interface IradatDakalDao {


    @Query("SELECT * FROM Iradat_Dakal")
    Flowable<List<IradatDakalEntity>> getIradatDakal();

    @Query("SELECT * FROM Iradat_Dakal WHERE mId LIKE :mid AND is_repaired = 0")
    Flowable<List<IradatDakalEntity>> getIradatDakalNotRepaired(String mid);


    @Query("SELECT * FROM Iradat_Dakal WHERE mId = :mid")
    Flowable<List<IradatDakalEntity>> getIradatDakalMid(int mid);

    @Query("SELECT * FROM Iradat_Dakal WHERE barcode = :qrCode")
    Flowable<List<IradatDakalEntity>> getIradatDakalNid(String qrCode);

    @Query("SELECT * FROM Iradat_Dakal WHERE mId = :mid AND is_repaired = 0 ORDER BY time DESC")
    Flowable<List<IradatDakalEntity>> getIradatDakal(int mid);

    @Insert
    void insertIradatDakal(ArrayList<IradatDakalEntity> iradatDakalEntityArrayList);

    @Query("DELETE FROM Iradat_Dakal")
    void deleteAllIradatDakal();

    @Query("DELETE FROM Iradat_Dakal WHERE Iradat_dakal.mId = :mid")
    void deleteIradatDakalWithMID(int mid);

}

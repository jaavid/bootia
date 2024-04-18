package com.controladad.boutia_pms.models.database;


import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import io.reactivex.Maybe;

@Dao
public interface BazdidYaTamirDao {
    //Flowable or observable is used to emits BazdidYaTamir model types of data and it emits whenever database is updated
   /* @Query("SELECT * FROM BazdidYaTamir")
    Maybe<List<BazdidYaTamirEntity>> getBazdidYaTamirNotUpload();*/

    @Query("SELECT * FROM BazdidYaTamir WHERE mId = :mid AND date = :date")
    Maybe<BazdidYaTamirEntity> getBazdidYaTamirNotUploadByMid(int mid, String date);

    @Query("DELETE FROM BazdidYaTamir WHERE mId = :mid AND date = :date")
    void  deleteBazdidYaTamir(int mid, String date);

    @Query("SELECT * FROM BazdidYaTamir")
    Maybe<List<BazdidYaTamirEntity>> getAllBazdidYaTamir();

    //Update the status of track which has been sent
    @Query("UPDATE BazdidYaTamir SET is_uploading = 1 WHERE mId = :mid")
    void updateMissionUploadStatusToUploading(int mid);

    //Update the status of track which has been sent
    @Query("UPDATE BazdidYaTamir SET is_uploading = 0 WHERE mId = :mid")
    void updateMissionUploadStatusToNotUploaded(int mid);

    //Insert the parameters in table like list of Bazdids
    @Insert
    void insertBazdidYaTamir(BazdidYaTamirEntity bazdidYaTamirEntity);


    @Query("UPDATE BazdidYaTamir SET images_fid = :fid WHERE nId = :nId")
    void updateBazdidYaTamirFid(String fid, int nId);


    @Query("DELETE FROM BazdidYaTamir WHERE nId = :nId")
    void deleteOneBazdidYaTamirRows(int nId);

    //This Query will delete all the students from table Bazdids
    @Query("DELETE FROM BazdidYaTamir")
    void deleteAllBazdidYaTamirRows();
}

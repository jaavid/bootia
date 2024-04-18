package com.controladad.boutia_pms.models.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import io.reactivex.Maybe;

@Dao
public interface TrackDao {
    //Flowable or observable is used to emits Mission model types of data and it emits whenever database is updated
    @Query("SELECT * FROM Track WHERE mId = :mid")
    List<TrackEntity> getTrack(int mid);


    @Query("SELECT * FROM Track WHERE mId = :mid AND getIsUploading = 0")
    List<TrackEntity> getTrackIsNotUploaded(int mid);

    @Query("SELECT * FROM Track WHERE mId = :mid AND getIsUploading = 1")
    List<TrackEntity> getTrackIsUploading(int mid);

    //@Query("SELECT mId, COUNT(mid) AS mIdCount FROM Track GROUP BY mId")
    @Query("SELECT Mission.title AS missionName, Track.mId, COUNT(Track.mid) AS mIdCount ," +
            " SUM(Track.getIsUploading) AS isUploadingCount , Track.nId As id, Track.type As type, Track.date As date FROM Track " +
            "LEFT JOIN Mission ON Track.mId = Mission.mId WHERE Track.type LIKE 'bazdid' GROUP BY Track.mId, Track.day")
    Maybe<List<TrackCount>> getTrackCountWithNameForBazdid();

    //@Query("SELECT mId, COUNT(mid) AS mIdCount FROM Track GROUP BY mId")
    @Query("SELECT Mission.title AS missionName, Track.mId, COUNT(Track.mid) AS mIdCount ," +
            " SUM(Track.getIsUploading) AS isUploadingCount , Track.nId As id , Track.type As type , Track.date As date FROM Track " +
            "LEFT JOIN Mission ON Track.mId = Mission.mId WHERE Track.type IN " +
            "( 'pahpad', 'khodro', 'gorooh', 'karshenas', 'harim', 'tahvil', 'khas'," +
            "'termo', 'zamin') GROUP BY Track.type")
    Maybe<List<TrackCount>> getTrackCountWithNameForTamirOtherType();

    //@Query("SELECT mId, COUNT(mid) AS mIdCount FROM Track GROUP BY mId")
    @Query("SELECT Mission_Tamirat.title AS missionName, Track.mId, COUNT(Track.mid) AS mIdCount ," +
            " SUM(Track.getIsUploading) AS isUploadingCount , Track.nId As id, Track.type As type FROM Track " +
            "LEFT JOIN Mission_Tamirat ON Track.mId = Mission_Tamirat.mId WHERE Track.type LIKE 'repair' GROUP BY Track.mId, Track.day")
    Maybe<List<TrackCount>> getTrackCountWithNameForTamir();

    @Query("SELECT COUNT(*) FROM TRACK WHERE mId = :mid AND type = :trackType")
    Maybe<Integer> getTrackCount(int mid, String trackType);

    @Query("SELECT COUNT(*)")
    Maybe<Integer> getTrackCount();

/*    @Query("SELECT Mission.title AS missionName, Track.mId, COUNT(Track.mId) AS mIdCount FROM Track LEFT JOIN Mission ON Track.mId = Mission.mId WHERE isUploaded = 1 GROUP BY Track.mId Having isUploaded = 1")
    Maybe<List<TrackCount>> getNumberOfUploadedTracks();*/

   /* @Query("SELECT Mission.title AS missionName, Track.mId, SUM(Track.isUploaded) AS mIdCount FROM Track LEFT JOIN Mission ON Track.mId = Mission.mId GROUP BY Track.mId")
    Maybe<List<TrackCount>> getNumberOfUploadedTracks();*/

    //Update the status of track which has been sent
    @Query("UPDATE Track SET getIsUploading = 1 WHERE nId = :nid")
    void updateTrackUploadStatusToUploading(int nid);

    //Update the status of track which has been sent
    @Query("UPDATE Track SET getIsUploading = 0 WHERE nId = :nid")
    void updateTrackUploadStatusToNotUpload(int nid);

    //Insert the parameters in table like list of Missions
    @Insert
    void insertTrack(TrackEntity trackEntity);

    //This Query will delete all the students from table Missions
    @Query("DELETE  FROM Track")
    void deleteAllTracks();

    @Query("DELETE FROM Track WHERE nId = :nid")
    void deleteTrack(int nid);
}

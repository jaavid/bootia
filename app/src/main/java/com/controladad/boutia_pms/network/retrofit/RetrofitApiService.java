package com.controladad.boutia_pms.network.retrofit;

import com.controladad.boutia_pms.models.retrofit_models.RetrofitModels;

import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface RetrofitApiService {

    //Get user authentication
    @FormUrlEncoded
    @POST("api/boutia/imeicheck")
    Observable<RetrofitModels.PreUser> getUser(@Field("imei") String imei);

    //Check if the user token is not expired
    @FormUrlEncoded
    @POST("api/system/connect")
    Observable<RetrofitModels.PreUserForCheck> checkToken(@Header("X-CSRF-Token") String token,
                                                  @Header("cookie") String cookie,
                                                  @Field("n") String hashMap);

    /*//Get bazdid Mission Details
    @FormUrlEncoded
    @POST("api/boutia/bazdid")
    Observable<List<RetrofitModels.Missions>> getBazdidMissions(@Header("X-CSRF-Token") String token,
                                                                @Header("cookie") String cookie,
                                                                @Field("limit") String limit,
                                                                @Field("offset") String offset);*/

    //Get bazdid missions from new API
    @FormUrlEncoded
    @POST("api/boutia/bazdidx")
    Observable<RetrofitModels.MissionBazdidX> getBazdidMissions(@Header("X-CSRF-Token") String token,
                                                                @Header("cookie") String cookie,
                                                                @Field("limit") String limit,
                                                                @Field("offset") String offset);

    //Get bazdid Mission Details
    @FormUrlEncoded
    @POST("api/boutia/repair")
    Observable<List<RetrofitModels.Missions>> getRepairMissions(@Header("X-CSRF-Token") String token,
                                                                @Header("cookie") String cookie,
                                                                @Field("limit") String limit,
                                                                @Field("offset") String offset);

    @FormUrlEncoded
    @POST("api/boutia/repairlistx")
    Observable<RetrofitModels.RepairListX> getRepairListsX(@Header("X-CSRF-Token") String token,
                                                           @Header("cookie") String cookie,
                                                           @Field("limit") String limit,
                                                           @Field("offset") String offset);

    @FormUrlEncoded
    @POST("api/boutia/alldakals")
    Observable<List<RetrofitModels.DakalList>> getDakalsList(@Header("X-CSRF-Token") String token,
                                                             @Header("cookie") String cookie,
                                                             @Field("limit") String limit,
                                                             @Field("offset") String offset);

    @FormUrlEncoded
    @POST("api/boutia/dakallist")
    Observable<List<RetrofitModels.DakalList>> getDakalsListById(@Header("X-CSRF-Token") String token,
                                                                 @Header("cookie") String cookie,
                                                                 @Field("limit") String limit,
                                                                 @Field("offset") String offset,
                                                                 @Field("operation") String mid);


    @FormUrlEncoded
    @POST("api/node")
    Observable<ResponseBody> sendCordinations(@Header("X-CSRF-Token") String token,
                                              @Header("cookie") String cookie,
                                              @FieldMap Map<String, String> map);

    @FormUrlEncoded
    @POST("api/node")
    Observable<ResponseBody> sendData(@Header("X-CSRF-Token") String token,
                                      @Header("cookie") String cookie,
                                      @FieldMap Map<String, String> map);

    @Multipart
    @POST("api/file/create_raw")
    Observable<List<RetrofitModels.ImageResponseModel>> sendPicture(@Header("X-CSRF-Token") String token,
                                                                    @Header("cookie") String cookie,
                                                                    @Part List<MultipartBody.Part> photos);


    @POST("api/boutia/now")
    Observable<List<String>> getTime();

}


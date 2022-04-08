package com.aariyan.pickingplan.Interface;

import io.reactivex.rxjava3.core.Observable;
import okhttp3.Response;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface RestApis {

    //For RxJava
//    @FormUrlEncoded
//    @POST("users.php")
//    Observable<ResponseBody> logInResponse(@Field("pincode") String pinCode);

    //For Retrofit:
    @FormUrlEncoded
    @POST("users.php")
    Call<ResponseBody> logInResponse(@Field("pincode") String pinCode);

    @GET("GetPlan.php?")
    Observable<ResponseBody> getPlan(@Query("ref") String qrCode);

    @FormUrlEncoded
    @POST("PostPickedQty.php")
    Observable<ResponseBody> postPickedQty(@Field("intPickingId") int itemId, @Field("Qty") String quantity,
                                           @Field("userId") int userId);

    @GET("GetPickingHeaders.php?")
    Observable<ResponseBody> getReference(@Query("userid") int userId);

//    @FormUrlEncoded
//    @POST("PostExtras.php")
//    Observable<ResponseBody> postExtras(@Field("strCheckerName") String checkerName, @Field("intDunnages") String dunnages,
//                                        @Field("intPallets") String pallets, @Field("intStraps") String intStraps,
//                                        @Field("intPlasticCorners") String plasticCorners, @Field("intTarps") String tarps,
//                                        @Field("intStans") String stans, @Field("strTrailorNo") String trailNo,
//                                        @Field("intBelts") String belts, @Field("intNets") String nets, @Field("strLoadComplete") String loadComplete,
//                                        @Field("strLoadSecured") String loadSecured, @Field("reference") String reference, @Field("userId") String userId);

    @FormUrlEncoded
    @POST("PostExtras.php")
    Observable<ResponseBody> postExtras(@Field("strCheckerName") String checkerName, @Field("intDunnages") int dunnages,
                                        @Field("intPallets") int pallets, @Field("intStraps") int intStraps,
                                        @Field("intPlasticCorners") int plasticCorners, @Field("intTarps") int tarps,
                                        @Field("intStans") int stans, @Field("strTrailorNo") String trailNo,
                                        @Field("intBelts") int belts, @Field("intNets") int nets, @Field("strLoadComplete") String loadComplete,
                                        @Field("strLoadSecured") String loadSecured, @Field("reference") String reference, @Field("userId") int userId);

    @FormUrlEncoded
    @POST("ProcessInvoice.php")
    Observable<ResponseBody> processInvoice(@Field("reference") String reference, @Field("userId") int userId);
}

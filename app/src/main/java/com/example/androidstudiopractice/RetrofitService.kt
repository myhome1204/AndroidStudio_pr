package com.example.androidstudiopractice

import com.google.gson.JsonElement
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST


interface RetrofitService {
    @FormUrlEncoded
    @POST("SignUp.php")
    fun Register(
        @Field("userId") userId:String,
        @Field("userPassword") userPassword: String,
        @Field("userName") userName:String
    ): Call<Redata>


    @FormUrlEncoded
    @POST("check_login.php")
    fun checkLogin(
        @Field("userId") userId:String,
        @Field("userPassword") userPassword: String
    ) : Call<LoginCheckResponse>

    @FormUrlEncoded
    @POST("check_duplicate.php")
    fun checkDuplicateId(@Field("userId") userId: String): Call<JsonElement>
}
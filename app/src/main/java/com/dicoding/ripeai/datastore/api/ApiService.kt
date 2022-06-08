package com.dicoding.ripeai.datastore.api

import com.dicoding.ripeai.datastore.response.HistoryResponse
import com.dicoding.ripeai.datastore.response.LoginResponse
import com.dicoding.ripeai.datastore.response.RegisterResponse
import com.dicoding.ripeai.datastore.response.UploadResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface ApiService {
    @FormUrlEncoded
    @POST("authentication/register")
    suspend fun register(
        @Field("firstname") firstname: String,
        @Field("lastname") lastname: String,
        @Field("phone") phone: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): RegisterResponse

    @FormUrlEncoded
    @POST("authentication/login")
    suspend fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): LoginResponse

    @GET("history")
    suspend fun getStories(
        @Query("email") email: String,
        @Query("ripeness") ripeness: String
    ): HistoryResponse

    @GET("history")
    fun getHistory(
        @Path("email") email: String
    ): Call<HistoryResponse>

    suspend fun uploadStory(
        @Header("Authorization") token: String,
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody,
    ): UploadResponse
}
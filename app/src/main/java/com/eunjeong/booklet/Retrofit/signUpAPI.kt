package com.eunjeong.booklet.Retrofit

import retrofit2.http.Field
import retrofit2.http.FieldMap
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface signUpAPI {
    @FormUrlEncoded
    @POST("/api/v1/join")
    fun postSignUp(@FieldMap param: HashMap<String, String>): Call<SignUpData>

}
package com.eunjeong.booklet.login

import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.http.*

interface LoginService {

    @POST("api/v1/login") // POST
    fun requestLogin( // Input
        @Body loginData: JsonObject
    ): Call<LoginResponse> // Output
}

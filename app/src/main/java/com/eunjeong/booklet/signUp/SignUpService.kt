package com.eunjeong.booklet.signUp

import com.google.gson.JsonElement
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface SignUpService {
    @Headers("Content-Type: application/json")
    @POST("/api/v1/join")
    fun postSignUp(
        @Body signUpData: SignUpRequest
    ) :Call<Response<Void>>

    

}
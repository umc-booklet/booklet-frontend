package com.eunjeong.booklet.login

import retrofit2.Call
import retrofit2.http.*

interface LoginService {
    @Headers("Content-Type:application/json") // response 415 error
    @POST("api/v1/login") // POST 통신
    fun requestLogin( // Input 정의
        @Body loginData: LoginRequest
    ): Call<LoginResponse> // Output 정의
}

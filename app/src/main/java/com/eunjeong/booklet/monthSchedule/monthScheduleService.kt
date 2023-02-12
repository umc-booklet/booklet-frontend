package com.eunjeong.booklet.monthSchedule

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface monthScheduleService {

    @GET("api/plan/user/{userId}/{month}")
    fun monthCheck(
        @Path("userId") userId: Int,
        @Path("month") month: Int
    ): Call<monthScheduleResponse>

}
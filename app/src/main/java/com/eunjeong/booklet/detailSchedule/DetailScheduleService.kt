package com.eunjeong.booklet.detailSchedule

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface DetailScheduleService {

    @GET("api/plan/{planId}") // GET 통신
    fun detailCheck( // Input
        @Path("planId") planId: Int
    ): Call<DetailScheduleResponse> // Output
}
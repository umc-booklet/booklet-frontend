package com.eunjeong.booklet

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface RetrofitPlanService {

    @POST("api/plan") // 성공
    fun addPlan(@Body addPlanInfo: PlanRequest): Call<PlanResponse>

    @GET("api/plan/user/{userId}") // 성공
    fun getPlanByUserId(@Path("userId") userId: String): Call<GetPlanByUserIdResponse>

    @GET("api/plan/user/{userId}/{month}") // 성공
    fun getMonthPlanByUserId(@Path("userId") userId: String, @Path("month") month: String): Call<GetPlanByUserIdResponse>

    @GET("api/plan/{planId}") // 성공
    fun getPlanByPlanId(@Path("planId") planId: Int): Call<GetPlanByPlanIdResponse>

    @PATCH("api/plan/{planId}")
    fun modifyPlanByPlanId(@Path("planId") planId: Int, @Body modifyPlanInfo: PlanModifyRequest): Call<ModifyPlanByPlanIdResponse>

    @DELETE("api/plan/{planId}") // 성공
    fun deletePlanByPlanId(@Path("planId") planId: Int): Call<PlanResponse>

}
package com.eunjeong.booklet.memberInfo

import com.eunjeong.booklet.memberInfo.MemberInfoResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
// 회원조회
interface MemberInfoService {
    @GET("api/v1/member/{id}") // GET 통신
    fun getCheck(
        @Path("id") id: Int
    ): Call<MemberInfoResponse>
}
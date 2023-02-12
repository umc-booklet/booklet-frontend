package com.eunjeong.booklet.friendSearch

import com.eunjeong.booklet.memberInfo.Info
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface FriendSearchService {

    @GET("api/v1/search") // GET 통신
    fun search(
        @Query("userId") userid : String
    ): Call<List<Info>>
}
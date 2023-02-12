package com.eunjeong.booklet.friendListCheck

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface FriendListCheckService {
    @GET("api/friends/{userId}") // GET 통신
    fun checkFriendList(
        @Path("userId") userId: Int
    ): Call<FriendListCheckResponse> // Output 정의
}
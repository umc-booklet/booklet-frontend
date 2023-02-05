package com.eunjeong.booklet.friendAdd

import retrofit2.Call
import retrofit2.http.POST
import retrofit2.http.Path

interface FriendAddService {

    @POST("api/friends/{userId}/{friendId}")
    fun friendRequest(
        @Path("userId") userId: Int,
        @Path("friendId") friendId: Int
    ): Call<friendAddResponse>
}
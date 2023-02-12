package com.eunjeong.booklet.friendAdd



data class FriendAddResponse(
    val isSuccess: Boolean,
    val code: Int,
    val message: String,
    val result: Add
)

data class Add(
    val friendsId : Int,
    val userId : Int,
    val friendId: Int,
    val status: String
)

package com.eunjeong.booklet.friendListCheck


data class FriendListCheckResponse(
    val isSuccess: Boolean,
    val code: Int,
    val message: String,
    val result: List<FriendID> // ex. "friendid" : 3
)

data class FriendID(
    val friendId: Int
)
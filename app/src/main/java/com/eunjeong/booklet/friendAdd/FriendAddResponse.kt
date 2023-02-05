package com.eunjeong.booklet.friendAdd



data class friendAddResponse(
    val isSuccess: Boolean,
    val code: Int,
    val message: String,
    val result: ArrayList<Add>
)

data class Add(
    val friendsId : Int,
    val userId : Int,
    val friendId: Int,
    val status: String
)

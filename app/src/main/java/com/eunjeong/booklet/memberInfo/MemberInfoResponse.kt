package com.eunjeong.booklet.memberInfo

data class MemberInfoResponse(
    val isSuccess: Boolean,
    val code: Int,
    val message: String,
    val result: ArrayList<Info>
)

data class Info(
    val id: Long,
    val userId: String,
    val name: String,
    val status: String,
    val profileImage: String
)

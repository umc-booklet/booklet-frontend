package com.eunjeong.booklet.login

// 로그인 Response
data class LoginResponse(
    val isSuccess: Boolean,
    val code: Int,
    val message: String,
    val result: List<ResultResponse>
)

data class ResultResponse(
    val id: Long,
    val userId: String,
    val name: String,
    val status: String,
    val profileImage: String
)

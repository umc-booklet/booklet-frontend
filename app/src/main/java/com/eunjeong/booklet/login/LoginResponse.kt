package com.eunjeong.booklet.login

// 로그인 Response
data class LoginResponse(
    val isSuccess: Boolean,
    val code: Int,
    val message: String,
    val results: List<ResultResponse>
)

data class ResultResponse(
    val id: Long,
    val name: String,
    val phoneNumber: String,
    val status: String,
    val profileImage: String
)

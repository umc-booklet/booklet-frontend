package com.eunjeong.booklet.login

import com.eunjeong.booklet.memberInfo.Info

// 로그인 Response
data class LoginResponse(
    val isSuccess: Boolean,
    val code: Int,
    val message: String,
    val result: Info
)


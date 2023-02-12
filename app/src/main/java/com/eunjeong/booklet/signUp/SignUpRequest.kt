package com.eunjeong.booklet.signUp

import com.google.gson.annotations.SerializedName

data class SignUpRequest (
    // @SerialzedName : JSON 에서 데이터에 매칭되는 이름 명시
    @SerializedName("userId") val userId: String,
    @SerializedName("name") val name: String,
    @SerializedName("password") val password: String
)
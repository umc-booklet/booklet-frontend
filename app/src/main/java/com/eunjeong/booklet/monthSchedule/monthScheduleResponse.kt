package com.eunjeong.booklet.monthSchedule

import com.eunjeong.booklet.detailSchedule.Detail

data class monthScheduleResponse(
    val isSuccess :Boolean,
    val code :Int,
    val message :String,
    val result : ArrayList<Detail>
)

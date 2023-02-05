package com.eunjeong.booklet.datas

data class Schedule(
    var Title: String,
    var Date: String,
    var Color: String,
    var currentStatus: Int //0: 일정이니까 달력 ImageView, 1: 하루종일이니까 아무것도 안뜨게, 2: 시간이니까 TextView
){}

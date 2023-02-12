package com.eunjeong.booklet

data class PlanRequest(
    val memberId: Int,
    val name: String,
    val text: String,
    val color: String,
    val startYear: Int,
    val startMonth: Int,
    val startDay: Int,
    val startHour: Int,
    val startMinute: Int,
    val endYear: Int,
    val endMonth: Int,
    val endDay: Int,
    val endHour: Int,
    val endMinute: Int
)

data class PlanModifyRequest(
    val memberId: Int,
    val name: String,
    val text: String,
    val color: String,
    val startYear: Int,
    val startMonth: Int,
    val startDay: Int,
    val startHour: Int,
    val startMinute: Int,
    val endYear: Int,
    val endMonth: Int,
    val endDay: Int,
    val endHour: Int,
    val endMinute: Int
)


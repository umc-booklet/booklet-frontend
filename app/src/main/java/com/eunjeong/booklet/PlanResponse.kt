package com.eunjeong.booklet

data class PlanResponse(
    val isSuccess: Boolean,
    val code: Int,
    val message: String,
    val result: Int
)

data class PlanResponseLayout(
    val id: Int,
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

data class GetPlanByPlanIdResponse(
    val isSuccess: Boolean,
    val code: Int,
    val message: String,
    val result: PlanResponseLayout
)

data class GetPlanByUserIdResponse(
    val isSuccess: Boolean,
    val code: Int,
    val message: String,
    val result: List<PlanResponseLayout>
)

data class ModifyPlanByPlanIdResponse(
    val isSuccess: Boolean,
    val code: Int,
    val message: String,
    val result: Int
)
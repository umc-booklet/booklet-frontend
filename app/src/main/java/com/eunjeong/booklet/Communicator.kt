package com.eunjeong.booklet

import com.eunjeong.booklet.detailSchedule.Detail
import java.util.ArrayList

interface Communicator {
    fun GoToTimeTableFragment(clickedDate: String, plan: ArrayList<Detail>)

    fun GoToDayScheduleFragment(clickedDate: String)
}
package com.eunjeong.booklet

interface Communicator {
    fun GoToTimeTableFragment(clickedDate: String)

    fun GoToDayScheduleFragment(clickedDate: String)
}
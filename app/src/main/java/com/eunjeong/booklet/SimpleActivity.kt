package com.eunjeong.booklet

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.eunjeong.booklet.databinding.ActivityCalendarBinding
import com.eunjeong.booklet.databinding.ActivitySimpleBinding
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.MaterialCalendarView
import java.util.*


class SimpleActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySimpleBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySimpleBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val calendarView = binding.calendarView // 타입 MaterialCalendarView

        changeWeekendColor() // 토요일은 파란색, 일요일은 빨간색으로 색상 변경
        makeDotToday() // 오늘 날짜 아래에 dot 생성


    }

    fun changeWeekendColor() {
        val materialCalendarView: MaterialCalendarView = binding.calendarView

        materialCalendarView.addDecorators(
            SaturdayDecorator(),
            SundayDecorator()
        )
    }

    fun makeDotToday() {
        val materialCalendarView: MaterialCalendarView = binding.calendarView
        materialCalendarView.setSelectedDate(CalendarDay.today())

        materialCalendarView.addDecorator(EventDotDecorator(Color.RED, Collections.singleton(CalendarDay.today())))
    }
}

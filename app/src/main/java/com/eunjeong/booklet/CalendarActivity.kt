package com.eunjeong.booklet

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.children
import com.eunjeong.booklet.databinding.ActivityCalendarBinding
import com.eunjeong.booklet.databinding.CalendarDayLayoutBinding
import com.kizitonwose.calendar.core.*
import com.kizitonwose.calendar.view.MonthDayBinder
import com.kizitonwose.calendar.view.MonthHeaderFooterBinder
import com.kizitonwose.calendar.view.ViewContainer
import java.time.LocalDate
import java.time.Month
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.*

class CalendarActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCalendarBinding
    private val titleRes: Int? = null
    private val today = LocalDate.now()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCalendarBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val daysOfWeek = daysOfWeek() // Available in the library
        val currentMonth = YearMonth.now()
        val startMonth = currentMonth.minusMonths(100)  // Adjust as needed
        val endMonth = currentMonth.plusMonths(100)  // Adjust as needed


        // Container Class
        class DayViewContainer(view: View) : ViewContainer(view) {
            lateinit var day: CalendarDay
            val textView = CalendarDayLayoutBinding.bind(view).calendarDayText
        }

        class MonthHeaderViewContainer(view: View): ViewContainer(view) {
            val binding = ActivityCalendarBinding.bind(view)
        }


        // CalendarView dayBinder, monthHeaderBinder, scrollListener
        binding.mainCalendarView.apply {
            dayBinder = object : MonthDayBinder<DayViewContainer> {
                override fun create(view: View) = DayViewContainer(view)
                override fun bind(container: DayViewContainer, data: CalendarDay) {
                    container.day = data
                }
            }

            monthHeaderBinder = object : MonthHeaderFooterBinder<MonthHeaderViewContainer> {
                override fun create(view: View) = MonthHeaderViewContainer(view)
                override fun bind(container: MonthHeaderViewContainer, data: CalendarMonth) {

                }
            }

            monthScrollListener = { updateTitle() }
            setup(startMonth, endMonth, daysOfWeek.first())
            scrollToMonth(currentMonth)
        }



        // dayBinder, 주말 & 이번달 아닌 day 텍스트 색상 변경
        binding.mainCalendarView.dayBinder = object : MonthDayBinder<DayViewContainer> {
            // Called only when a new container is needed.
            override fun create(view: View) = DayViewContainer(view)

            // Called every time we need to reuse a container.
            override fun bind(container: DayViewContainer, data: CalendarDay) {
                container.textView.text = data.date.dayOfMonth.toString()
                if (data.position == DayPosition.MonthDate && data.date.dayOfWeek.value == 6) {
                    container.textView.setTextColor(Color.BLUE)
                }
                else if (data.position == DayPosition.MonthDate && data.date.dayOfWeek.value == 7) {
                    container.textView.setTextColor(Color.RED)
                }
                else if (data.position != DayPosition.MonthDate) {
                    container.textView.setTextColor(Color.GRAY)
                }
                else {
                    container.textView.setTextColor(Color.BLACK)
                }

            }

        }


        // 달력 시작 요일 설정, 앱 실행 시 현재에 해당하는 월 보여주기
        val firstDayOfWeek = firstDayOfWeekFromLocale() // Available from the library
        binding.mainCalendarView.setup(startMonth, endMonth, firstDayOfWeek)
        binding.mainCalendarView.scrollToMonth(currentMonth)



        // titlesContainer (연, 월 표시)
        val titlesContainer = findViewById<ViewGroup>(R.id.yearMonthContainer)
        titlesContainer.children
            .map { it as TextView }
            .forEachIndexed { index, textView ->
//                val dayOfWeek = daysOfWeek[index]
//                val title = dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault())
//                textView.text = title
                currentMonth.month.displayText(short = false)
            }


        // monthHeaderBinder (일 ~ 월 표시)
        binding.mainCalendarView.monthHeaderBinder = object : MonthHeaderFooterBinder<MonthViewContainer> {
            override fun create(view: View) = MonthViewContainer(view)
            override fun bind(container: MonthViewContainer, data: CalendarMonth) {
                // Remember that the header is reused so this will be called for each month.
                // However, the first day of the week will not change so no need to bind
                // the same view every time it is reused.
                if (container.titlesContianer.tag == null) {
                    container.titlesContianer.tag = data.yearMonth
                    container.titlesContianer.children.map { it as TextView }
                        .forEachIndexed { index, textView ->
                            val dayOfWeek = daysOfWeek[index]
                            val title = dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault())
                            textView.text = title
                        }
                }
            }
        }


        // xml < > 버튼으로 달력 스크롤하기
        binding.btnGoPreviousMonth.setOnClickListener {
            binding.mainCalendarView.findFirstVisibleMonth()?.let {
                binding.mainCalendarView.smoothScrollToMonth(it.yearMonth.previousMonth)
            }
        }

        binding.btnGoNextMonth.setOnClickListener {
            binding.mainCalendarView.findFirstVisibleMonth()?.let {
                binding.mainCalendarView.smoothScrollToMonth(it.yearMonth.nextMonth)
            }
        }



    }


    // 달력 옮길 때 상단 title(연, 월) 업데이트
    private fun updateTitle() {
        val month = binding.mainCalendarView.findFirstVisibleMonth()?.yearMonth ?: return
        binding.yearText.text = month.year.toString()
        binding.monthText.text = month.month.displayText(short = false)
    }

    // text display, month 표시 한글로 바꾸기
    fun Month.displayText(short: Boolean = true): String {
        val style = if (short) TextStyle.SHORT else TextStyle.FULL
        return getDisplayName(style, Locale.getDefault())
    }
}
package com.eunjeong.booklet

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.children
import com.eunjeong.booklet.databinding.ActivityCalendarBinding
import com.eunjeong.booklet.databinding.CalendarDayLayoutBinding
import com.kizitonwose.calendar.core.*
import com.kizitonwose.calendar.view.MonthDayBinder
import com.kizitonwose.calendar.view.MonthHeaderFooterBinder
import com.kizitonwose.calendar.view.ViewContainer
import kotlinx.android.synthetic.main.activity_calendar.*
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.Month
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.*


class CalendarActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCalendarBinding
    private lateinit var tbinding: CalendarDayLayoutBinding
    private val titleRes: Int? = null
    private val today = LocalDate.now()
    private var selectedDate: LocalDate? = null
    var checkDaySelected: Boolean = true
    lateinit var activityResultLauncher: ActivityResultLauncher<Intent>

    var eventTitle = ""
    var switchChecked = ""
    var startDate = ""
    var endDate = ""
    var startTime = ""
    var endTime = ""
    var allDayEventDate = ""
    var eventColor = ""
    var nonStrStartDate = ""
    var nonStrEndDate = ""
    var nonStringStartDate: LocalDate? = null
    var nonStringEndDate: LocalDate? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCalendarBinding.inflate(layoutInflater)
        tbinding = CalendarDayLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val daysOfWeek = daysOfWeek() // Available in the library
        val currentMonth = YearMonth.now()
        val startMonth = currentMonth.minusMonths(100)  // Adjust as needed
        val endMonth = currentMonth.plusMonths(100)  // Adjust as needed


        // Container Class
        class DayViewContainer(view: View) : ViewContainer(view) {
            lateinit var day: CalendarDay
            val textView = CalendarDayLayoutBinding.bind(view).calendarDayText
            val event1 = CalendarDayLayoutBinding.bind(view).event1
            val event2 = CalendarDayLayoutBinding.bind(view).event2
            val event3 = CalendarDayLayoutBinding.bind(view).event3
            val event4 = CalendarDayLayoutBinding.bind(view).event4

            init {
                // 달력 한 칸 클릭했을 때 동작
                view.setOnClickListener {
                    // Check the day position as we do not want to select in or out dates.
                    if (day.position == DayPosition.MonthDate) {
                        // Keep a reference to any previous selection
                        // in case we overwrite it and need to reload it.
                        val currentSelection = selectedDate

                        if (currentSelection == day.date) {
                            // If the user clicks the same date, clear selection.
                            selectedDate = null
                            // Reload this date so the dayBinder is called
                            // and we can REMOVE the selection background.
                            mainCalendarView.notifyDateChanged(currentSelection)
                        } else {
                            selectedDate = day.date
                            // Reload the newly selected date so the dayBinder is
                            // called and we can ADD the selection background.
                            mainCalendarView.notifyDateChanged(day.date)
                            if (currentSelection != null) {
                                // We need to also reload the previously selected
                                // date so we can REMOVE the selection background.
                                mainCalendarView.notifyDateChanged(currentSelection)
                            }
                        }

                    }
                }
            }
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



        // dayBinder
        binding.mainCalendarView.dayBinder = object : MonthDayBinder<DayViewContainer> {
            // Called only when a new container is needed.
            override fun create(view: View) = DayViewContainer(view)

            // Called every time we need to reuse a container.
            override fun bind(container: DayViewContainer, data: CalendarDay) {
                container.textView.text = data.date.dayOfMonth.toString()

                // 주말, 이번 달 아닌 day 색상 변경
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

                // 오늘 날짜 원으로 표시 & single day select (square border)
                val day = data
                val textView = container.textView // 달력 속 text
                container.day = data
                textView.text = day.date.dayOfMonth.toString()
                val dayView = container.view // 달력 한 칸 view

                if (day.position == DayPosition.MonthDate) {
                    textView.visibility = View.VISIBLE
                    if (day.date == today) { // 오늘 날짜 고정으로 원 배경 표시
                        textView.setBackgroundResource(R.drawable.today_circle)
                    }

                    if (day.date == selectedDate) {
                        dayView.setBackgroundResource(R.drawable.selected_border)
                    }
                    else if (day.date != selectedDate) { // 한 번 더 누르면 border 제거
                        dayView.background = null
                    }
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


        // 우하단 + 버튼 누르면 일정 추가 화면으로 넘어가기 (+ 선택한 날짜도 같이 넘어가게?)
        binding.btnAddSchedule.setOnClickListener {
            val addIntent = Intent(this, AddScheduleActivity::class.java)

            if (selectedDate != null) { // 달력에서 요일을 한 번이라도 선택했을 경우 선택한 날짜에 일정 추가
                addIntent.putExtra("selectedMonth", selectedDate!!.month.value.toString())
                addIntent.putExtra("selectedDay", selectedDate!!.dayOfMonth.toString())
                addIntent.putExtra("selectedDayofWeek", selectedDate!!.dayOfWeek.toString())
                addIntent.putExtra("checkDaySelected", checkDaySelected.toString())
            }
            else { // 달력에서 요일을 선택하지 않고 일정 추가 버튼을 눌렀을 경우 오늘 날짜에 일정 추가
                addIntent.putExtra("todayMonth", today.month.toString())
                addIntent.putExtra("todayDay", today.dayOfMonth.toString())
                addIntent.putExtra("todayDayofWeek",today.dayOfWeek.toString() )
            }

            activityResultLauncher.launch(addIntent)

        }

        activityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                eventTitle = result.data?.getStringExtra("eventTitle").toString()
                switchChecked = result.data?.getStringExtra("switchChecked").toString()

                if (switchChecked == "false") { // 하루종일 아닌 일정
                    startDate = result.data?.getStringExtra("startDate").toString()
                    endDate = result.data?.getStringExtra("endDate").toString()
                    startTime = result.data?.getStringExtra("startTime").toString()
                    endTime = result.data?.getStringExtra("endTime").toString()
                    nonStrStartDate = result.data?.getStringExtra("nonStringStartDate").toString()
                    nonStrEndDate = result.data?.getStringExtra("nonStringEndDate").toString()

                }
                else { // 하루종일
                    allDayEventDate = result.data?.getStringExtra("allDayEventDate").toString()
                }

                eventColor = result.data?.getStringExtra("eventColor").toString()

                binding.mainCalendarView.dayBinder = object : MonthDayBinder<DayViewContainer> {
                    // Called only when a new container is needed.
                    override fun create(view: View) = DayViewContainer(view)

                    // Called every time we need to reuse a container.
                    override fun bind(container: DayViewContainer, data: CalendarDay) {
                        container.textView.text = data.date.dayOfMonth.toString()

                        // 오늘 날짜 원으로 표시 & single day select (square border)
                        val day = data
                        val textView = container.textView // 달력 속 text
                        val firstEvent = container.event1
                        val secondEvent = container.event2
                        val thirdEvent = container.event3
                        val fourthEvent = container.event4

                        container.day = data
                        textView.text = day.date.dayOfMonth.toString()
                        val dayView = container.view // 달력 한 칸 view

                        // 주말, 이번 달 아닌 day 색상 변경
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

                        if (day.position == DayPosition.MonthDate) {
                            textView.visibility = View.VISIBLE
                            if (day.date == today) { // 오늘 날짜 고정으로 원 배경 표시
                                textView.setBackgroundResource(R.drawable.today_circle)
                            }

                            if (day.date == selectedDate) {
                                dayView.setBackgroundResource(R.drawable.selected_border)
                            }
                            else if (day.date != selectedDate) { // 한 번 더 누르면 border 제거
                                dayView.background = null
                            }
                        }


                        val m = day.date.month.displayText()
                        val d = day.date.dayOfMonth.toString()
                        val dow = day.date.dayOfWeek.displayText()[0]
                        if (nonStrStartDate != "") {
                            nonStringStartDate = LocalDate.parse(nonStrStartDate, DateTimeFormatter.ISO_DATE)
                            nonStringEndDate = LocalDate.parse(nonStrEndDate, DateTimeFormatter.ISO_DATE)
                        }


                        // 단순 일정 추가 함수
                        fun addEvent(eventNum: TextView, selectedColor: String) {
                            if (selectedColor == "pink") {
                                // 시작 날짜와 끝 날짜가 다른 경우
                                if (nonStringStartDate != nonStringEndDate) {
                                    if (day.date == nonStringStartDate) {
                                        eventNum.text = eventTitle
                                        eventNum.setBackgroundResource(R.drawable.event_on_calendar_corner_pink_start)
                                        eventNum.setTextColor(ContextCompat.getColor(applicationContext, R.color.eventpinktext))
                                    }
                                    else if (day.date.isBefore(nonStringEndDate) && day.date.isAfter(nonStringStartDate)) {
                                        eventNum.setBackgroundResource(R.drawable.event_on_calendar_corner_pink_middle)
                                        eventNum.setTextColor(ContextCompat.getColor(applicationContext, R.color.eventpinktext))
                                    }
                                    else if (day.date == nonStringEndDate) {
                                        eventNum.setBackgroundResource(R.drawable.event_on_calendar_corner_pink_end)
                                        eventNum.setTextColor(ContextCompat.getColor(applicationContext, R.color.eventpinktext))
                                    }
                                    nonStringStartDate = null
                                    nonStringEndDate = null
                                }
                                // 시작 날짜 == 끝 날짜지만 시간만 다른 경우
                                else if (startDate == endDate && startDate == "${m} ${d}일 (${dow})") {
                                    eventNum.text = eventTitle
                                    eventNum.setBackgroundResource(R.drawable.event_on_calendar_corner_pink)
                                    eventNum.setTextColor(ContextCompat.getColor(applicationContext, R.color.eventpinktext))
                                    startDate = ""
                                    endDate = ""
                                }

                                // 하루종일 switch ON
                                if (switchChecked == "true" && allDayEventDate == "${m} ${d}일 (${dow})") {
                                    eventNum.text = eventTitle
                                    eventNum.setBackgroundResource(R.drawable.event_on_calendar_corner_pink)
                                    eventNum.setTextColor(ContextCompat.getColor(applicationContext, R.color.eventpinktext))
                                    allDayEventDate = ""
                                }
                            }
                            else if (selectedColor == "green") {
                                // 시작 날짜와 끝 날짜가 다른 경우
                                if (nonStringStartDate != nonStringEndDate) {
                                    if (day.date == nonStringStartDate) {
                                        eventNum.text = eventTitle
                                        eventNum.setBackgroundResource(R.drawable.event_on_calendar_corner_green_start)
                                        eventNum.setTextColor(ContextCompat.getColor(applicationContext, R.color.eventgreentext))
                                    }
                                    else if (day.date.isBefore(nonStringEndDate) && day.date.isAfter(nonStringStartDate)) {
                                        eventNum.setBackgroundResource(R.drawable.event_on_calendar_corner_green_middle)
                                        eventNum.setTextColor(ContextCompat.getColor(applicationContext, R.color.eventgreentext))
                                    }
                                    else if (day.date == nonStringEndDate) {
                                        eventNum.setBackgroundResource(R.drawable.event_on_calendar_corner_green_end)
                                        eventNum.setTextColor(ContextCompat.getColor(applicationContext, R.color.eventgreentext))
                                    }
                                    nonStringStartDate = null
                                    nonStringEndDate = null
                                }
                                // 시작 날짜 == 끝 날짜지만 시간만 다른 경우
                                else if (startDate == endDate && startDate == "${m} ${d}일 (${dow})") {
                                    eventNum.text = eventTitle
                                    eventNum.setBackgroundResource(R.drawable.event_on_calendar_corner_green)
                                    eventNum.setTextColor(ContextCompat.getColor(applicationContext, R.color.eventgreentext))
                                    startDate = ""
                                    endDate = ""
                                }

                                // 하루종일 switch ON
                                if (switchChecked == "true" && allDayEventDate == "${m} ${d}일 (${dow})") {
                                    eventNum.text = eventTitle
                                    eventNum.setBackgroundResource(R.drawable.event_on_calendar_corner_green)
                                    eventNum.setTextColor(ContextCompat.getColor(applicationContext, R.color.eventgreentext))
                                    allDayEventDate = ""
                                }
                            }
                            else if (selectedColor == "purple") {
                                // 시작 날짜와 끝 날짜가 다른 경우
                                if (nonStringStartDate != nonStringEndDate) {
                                    if (day.date == nonStringStartDate) {
                                        eventNum.text = eventTitle
                                        eventNum.setBackgroundResource(R.drawable.event_on_calendar_corner_purple_start)
                                        eventNum.setTextColor(ContextCompat.getColor(applicationContext, R.color.eventpurpletext))
                                    }
                                    else if (day.date.isBefore(nonStringEndDate) && day.date.isAfter(nonStringStartDate)) {
                                        eventNum.setBackgroundResource(R.drawable.event_on_calendar_corner_purple_middle)
                                        eventNum.setTextColor(ContextCompat.getColor(applicationContext, R.color.eventpurpletext))
                                    }
                                    else if (day.date == nonStringEndDate) {
                                        eventNum.setBackgroundResource(R.drawable.event_on_calendar_corner_purple_end)
                                        eventNum.setTextColor(ContextCompat.getColor(applicationContext, R.color.eventpurpletext))
                                    }
                                    nonStringStartDate = null
                                    nonStringEndDate = null
                                }
                                // 시작 날짜 == 끝 날짜지만 시간만 다른 경우
                                else if (startDate == endDate && startDate == "${m} ${d}일 (${dow})") {
                                    eventNum.text = eventTitle
                                    eventNum.setBackgroundResource(R.drawable.event_on_calendar_corner_purple)
                                    eventNum.setTextColor(ContextCompat.getColor(applicationContext, R.color.eventpurpletext))
                                    startDate = ""
                                    endDate = ""
                                }

                                // 하루종일 switch ON
                                if (switchChecked == "true" && allDayEventDate == "${m} ${d}일 (${dow})") {
                                    eventNum.text = eventTitle
                                    eventNum.setBackgroundResource(R.drawable.event_on_calendar_corner_purple)
                                    eventNum.setTextColor(ContextCompat.getColor(applicationContext, R.color.eventpurpletext))
                                    allDayEventDate = ""
                                }
                            }
                        }

                        // 현재 달력 한 칸에 있는 일정의 위치를 반영하여 일정 추가하는 함수
                        fun addEventOnPosition() {
                            if (firstEvent.background == null) {
                                addEvent(firstEvent, eventColor)
                            }
                            else if (secondEvent.background == null) {
                                addEvent(secondEvent, eventColor)
                            }
                            else if (thirdEvent.background == null) {
                                addEvent(thirdEvent, eventColor)
                            }
                            else if (fourthEvent.background == null) {
                                addEvent(fourthEvent, eventColor)
                            }

                        }

                        //dummy data
//                        if (day.date == today) {
//                            firstEvent.text = "일정"
//                            firstEvent.setBackgroundResource(R.drawable.event_on_calendar_corner_pink_start)
//                            firstEvent.setTextColor(ContextCompat.getColor(applicationContext, R.color.eventpinktext))
//
//                            secondEvent.text = "일정"
//                            secondEvent.setBackgroundResource(R.drawable.event_on_calendar_corner_green_start)
//                            secondEvent.setTextColor(ContextCompat.getColor(applicationContext, R.color.eventgreentext))
//                        }
//                        else if (day.date == today.plusDays(1)) {
//                            firstEvent.setBackgroundResource(R.drawable.event_on_calendar_corner_pink_middle)
//                            secondEvent.setBackgroundResource(R.drawable.event_on_calendar_corner_green_middle)
//                        }
//                        else if (day.date == today.plusDays(2)) {
//                            firstEvent.setBackgroundResource(R.drawable.event_on_calendar_corner_pink_end)
//                            secondEvent.setBackgroundResource(R.drawable.event_on_calendar_corner_green_end)
//                        }
//                        addEventOnPosition()




                    }



                    }

                }

            }
        }



    // 달력 옮길 때 상단 title(연, 월) 업데이트
    private fun updateTitle() {
        val month = binding.mainCalendarView.findFirstVisibleMonth()?.yearMonth ?: return
        binding.yearText.text = month.year.toString()
        binding.monthText.text = month.month.displayText(short = false)
    }

    // text display, month, 요일 표시 한글로 바꾸기
    fun Month.displayText(short: Boolean = true): String {
        val style = if (short) TextStyle.SHORT else TextStyle.FULL
        return getDisplayName(style, Locale.getDefault())
    }

    fun DayOfWeek.displayText(short: Boolean = true): String {
        val style = if (short) TextStyle.SHORT else TextStyle.FULL
        return getDisplayName(style, Locale.getDefault())
    }

}
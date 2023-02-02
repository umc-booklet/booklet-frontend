package com.eunjeong.booklet

import com.eunjeong.booklet.R
import android.app.usage.UsageEvents.Event.NONE
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.animation.TranslateAnimation
import android.widget.TextView
import android.widget.TimePicker
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.children
import androidx.core.view.isVisible
import com.eunjeong.booklet.databinding.ActivityAddScheduleBinding
import com.eunjeong.booklet.databinding.BottomSheetDialogLayoutBinding
import com.eunjeong.booklet.databinding.CalendarAddEventLayoutBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.kizitonwose.calendar.core.*
import com.kizitonwose.calendar.view.MonthDayBinder
import com.kizitonwose.calendar.view.MonthHeaderFooterBinder
import com.kizitonwose.calendar.view.ViewContainer
import kotlinx.android.synthetic.main.activity_add_schedule.*
import kotlinx.android.synthetic.main.activity_calendar.*
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.Month
import java.time.YearMonth
import java.time.format.TextStyle
import java.time.temporal.ChronoUnit
import java.util.*


class AddScheduleActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddScheduleBinding
    private lateinit var dialogbinding: BottomSheetDialogLayoutBinding
    private val titleRes: Int? = null
    private val today = LocalDate.now()
    private var selectedDate: LocalDate? = null
    var selected = 0
    var existingStartTime: String = ""
    var existingEndTime: String = ""
    var clicked: Int = 0
    var eventColor: String = "pink"

    var nonStringStartDate: LocalDate? = null
    var nonStringEndDate: LocalDate? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddScheduleBinding.inflate(layoutInflater)
        dialogbinding = BottomSheetDialogLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // x 버튼 동작
        binding.btnCloseAddSchedule.setOnClickListener {
            finish()
        }

        // 하루종일 switch checked면 밑에 시간 지우기
        binding.switchAllDay.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                binding.startTime.isVisible = false
                binding.endTime.isVisible = false
            }
            else {
                binding.startTime.isVisible = true
                binding.endTime.isVisible = true
            }
        }


        // text 색상 정하는 레이아웃 보여주고 숨기는 애니메이션
        binding.btnSelectColor.setOnClickListener {
            clicked += 1
            if (clicked == 1) {
                val anim = TranslateAnimation(scrollEventColor.width.toFloat(), 0f, 0f, 0f)
                anim.duration = 400
                anim.fillAfter = true
                scrollEventColor.visibility = View.VISIBLE
                scrollEventColor.animation = anim
            }
            else if (clicked == 2) {
                val anim = TranslateAnimation(0f, scrollEventColor.width.toFloat(), 0f, 0f)
                anim.duration = 400
                anim.fillAfter = false
                scrollEventColor.visibility = View.GONE
                scrollEventColor.animation = anim
                clicked = 0
            }
        }

        binding.btnColorPink.setOnClickListener {
            binding.btnSelectColor.setImageResource(R.drawable.event_color_pink)
            eventColor = "pink"
            val animpink = TranslateAnimation(0f, scrollEventColor.width.toFloat(), 0f, 0f)
            animpink.duration = 400
            animpink.fillAfter = false
            scrollEventColor.animation = animpink
            scrollEventColor.visibility = View.GONE
        }

        binding.btnColorGreen.setOnClickListener {
            binding.btnSelectColor.setImageResource(R.drawable.event_color_green)
            eventColor = "green"
            val animgreen = TranslateAnimation(0f, scrollEventColor.width.toFloat(), 0f, 0f)
            animgreen.duration = 400
            animgreen.fillAfter = false
            scrollEventColor.animation = animgreen
            scrollEventColor.visibility = View.GONE
        }

        binding.btnColorPurple.setOnClickListener {
            binding.btnSelectColor.setImageResource(R.drawable.event_color_purple)
            eventColor = "purple"
            val animpurple = TranslateAnimation(0f, scrollEventColor.width.toFloat(), 0f, 0f)
            animpurple.duration = 400
            animpurple.fillAfter = false
            scrollEventColor.animation = animpurple
            scrollEventColor.visibility = View.GONE
        }


        // bottom sheet with TimePicker (startDate)
        val bottomSheetView = layoutInflater.inflate(R.layout.bottom_sheet_dialog_layout, null)
        val bottomSheetDialog = BottomSheetDialog(this)
        bottomSheetDialog.setContentView(bottomSheetView)

        val endbottomSheetView = layoutInflater.inflate(R.layout.bottom_sheet_dialog_layout, null)
        val endbottomSheetDialog = BottomSheetDialog(this)
        endbottomSheetDialog.setContentView(endbottomSheetView)

        val timePicker: TimePicker = bottomSheetView.findViewById(R.id.timepicker)
        val endtimePicker: TimePicker = endbottomSheetView.findViewById(R.id.timepicker)
        val startTimeText: TextView = binding.startTime
        val endTimeText: TextView = binding.endTime

        // 최초로 timepicker을 호출했을 때 기본 시간 설정 (두번째부터는 지정한 시간으로 뜸)
        timePicker.hour = 8
        timePicker.minute = 0
        endtimePicker.hour = 9
        endtimePicker.minute = 0

        timePicker.setOnTimeChangedListener { timePicker, hourOfDay, minute ->
            if (hourOfDay > 12) {
                startTimeText.text = "오후 %d:%02d".format(hourOfDay-12, minute)
            }
            else if (hourOfDay == 12) {
                startTimeText.text = "오후 12:%02d".format(minute)
            }
            else {
                startTimeText.text = "오전 %d:%02d".format(hourOfDay, minute)
            }
        }

        endtimePicker.setOnTimeChangedListener { timePicker, hourOfDay, minute ->
            if (hourOfDay > 12) {
                endTimeText.text = "오후 %d:%02d".format(hourOfDay-12, minute)
            }
            else if (hourOfDay == 12) {
                endTimeText.text = "오후 12:%02d".format(minute)
            }
            else {
                endTimeText.text = "오전 %d:%02d".format(hourOfDay, minute)
            }
        }

        // bottomsheetdialog 호출
        binding.startTime.setOnClickListener {
            binding.startDate.setTextColor(resources.getColor(R.color.textBlue))
            existingStartTime = binding.startTime.text.toString()
            bottomSheetDialog.show()
        }

        // 확인 버튼 눌렀을 때
        bottomSheetView.findViewById<View>(R.id.setTime).setOnClickListener {
            binding.startDate.setTextColor(resources.getColor(R.color.black))
            bottomSheetDialog.dismiss()
        }

        // 취소 버튼 눌렀을 때
        bottomSheetView.findViewById<View>(R.id.closeTimePicker).setOnClickListener {
            binding.startDate.setTextColor(resources.getColor(R.color.black))
            binding.startTime.text = existingStartTime
            bottomSheetDialog.dismiss()
        }


        // bottom sheet with TimePicker (endDate)
        // 호출
        binding.endTime.setOnClickListener {
            binding.endDate.setTextColor(resources.getColor(R.color.textBlue))
            existingEndTime = binding.endTime.text.toString()
            endbottomSheetDialog.show()
        }

        // 확인 버튼
        endbottomSheetView.findViewById<View>(R.id.setTime).setOnClickListener {
            binding.endDate.setTextColor(resources.getColor(R.color.black))
            endbottomSheetDialog.dismiss()
        }

        // 취소 버튼
        endbottomSheetView.findViewById<View>(R.id.closeTimePicker).setOnClickListener {
            binding.endDate.setTextColor(resources.getColor(R.color.black))
            binding.endTime.text = existingEndTime
            endbottomSheetDialog.dismiss()
        }



        // 메인 캘린더에서 선택한 요일 받아서 startDay, endDay 반영
        val bundle = intent.extras
        val checkDaySelected = bundle!!.getString("checkDaySelected")
        if (checkDaySelected != null) { // 선택된 요일이 있는 경우
            val selectedMonth = bundle.getString("selectedMonth") // 월
            val selectedDay = bundle.getString("selectedDay") // 일
            var selectedDayofWeek = bundle.getString("selectedDayofWeek") // 요일
            when (selectedDayofWeek) {
                "SUNDAY" -> {
                    selectedDayofWeek = "일"
                }
                "MONDAY" -> {
                    selectedDayofWeek = "월"
                }
                "TUESDAY" -> {
                    selectedDayofWeek = "화"
                }
                "WEDNESDAY" -> {
                    selectedDayofWeek = "수"
                }
                "THURSDAY" -> {
                    selectedDayofWeek = "목"
                }
                "FRIDAY" -> {
                    selectedDayofWeek = "금"
                }
                "SATURDAY" -> {
                    selectedDayofWeek = "토"
                }
            } // 요일 한글로 변경

            binding.startDate.text = "%s월 %s일 (%s)".format(selectedMonth, selectedDay, selectedDayofWeek)
            binding.endDate.text = "%s월 %s일 (%s)".format(selectedMonth, selectedDay, selectedDayofWeek)
        }
        else { // 선택된 요일이 없는 경우 오늘 날짜
            var todayMonth = bundle.getString("todayMonth") // 월
            val todayDay = bundle.getString("todayDay") // 일
            var todayDayofWeek = bundle.getString("todayDayofWeek") // 요일

            when (todayMonth) {
                "JANUARY" -> {
                    todayMonth = "1"
                }
                "FEBRUARY" -> {
                    todayMonth = "2"
                }
                "MARCH" -> {
                    todayMonth = "3"
                }
                "APRIL" -> {
                    todayMonth = "4"
                }
                "MAY" -> {
                    todayMonth = "5"
                }
                "JUNE" -> {
                    todayMonth = "6"
                }
                "JULY" -> {
                    todayMonth = "7"
                }
                "AUGUST" -> {
                    todayMonth = "8"
                }
                "SEPTEMBER" -> {
                    todayMonth = "9"
                }
                "OCTOBER" -> {
                    todayMonth = "10"
                }
                "NOVEMBER" -> {
                    todayMonth = "11"
                }
                "DECEMBER" -> {
                    todayMonth = "12"
                }
            } // 월 한글로 변경
            when (todayDayofWeek) {
                "SUNDAY" -> {
                    todayDayofWeek = "일"
                }
                "MONDAY" -> {
                    todayDayofWeek = "월"
                }
                "TUESDAY" -> {
                    todayDayofWeek = "화"
                }
                "WEDNESDAY" -> {
                    todayDayofWeek = "수"
                }
                "THURSDAY" -> {
                    todayDayofWeek = "목"
                }
                "FRIDAY" -> {
                    todayDayofWeek = "금"
                }
                "SATURDAY" -> {
                    todayDayofWeek = "토"
                }
            } // 요일 한글로 변경

            binding.startDate.text = "%s월 %s일 (%s)".format(todayMonth, todayDay, todayDayofWeek)
            binding.endDate.text = "%s월 %s일 (%s)".format(todayMonth, todayDay, todayDayofWeek)
        }


        // 일정 선택 캘린더
        val daysOfWeek = daysOfWeek() // Available in the library
        val currentMonth = YearMonth.now()
        val startMonth = currentMonth.minusMonths(100)  // Adjust as needed
        val endMonth = currentMonth.plusMonths(100)  // Adjust as needed

        // Container Class
        class DayViewContainer(view: View) : ViewContainer(view) {
            lateinit var day: CalendarDay
            val textView = CalendarAddEventLayoutBinding.bind(view).calendarDayText

            init {

                // 달력 한 칸 클릭했을 때 동작
                view.setOnClickListener {

                    if (day.position == DayPosition.MonthDate) {
                        val currentSelection = selectedDate

                        if (currentSelection == day.date) {
                            selectedDate = null
                            scheduleCalendar.notifyDateChanged(currentSelection)
                        } else {
                            selectedDate = day.date
                            scheduleCalendar.notifyDateChanged(day.date)
                            if (currentSelection != null) {
                                scheduleCalendar.notifyDateChanged(currentSelection)
                            }
                        }

                    }

                }
            }


        }

        class MonthHeaderViewContainer(view: View): ViewContainer(view) {
            val binding = ActivityAddScheduleBinding.bind(view)
        }

        // CalendarView dayBinder, monthHeaderBinder, scrollListener
        binding.scheduleCalendar.apply {
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

        binding.scheduleCalendar.dayBinder = object : MonthDayBinder<DayViewContainer> {
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
                    if (day.date == selectedDate && selected < 2 && !binding.switchAllDay.isChecked) {
//                        if (data.date.isBefore(selectedDate)) {
//                            textView.setTextColor(Color.GRAY)
//                        }
                        selected += 1
                        when (selected) {
                            1 -> {
                                textView.setBackgroundResource(R.drawable.event_circle)
                                textView.setTextColor(Color.WHITE)
                                nonStringStartDate = selectedDate
                                binding.startDate.text = "%s %s일 (%s)".format(selectedDate!!.month.displayText(), selectedDate!!.dayOfMonth, selectedDate!!.dayOfWeek.displayText()[0])
                            }

                            2 -> {
                                textView.setBackgroundResource(R.drawable.event_circle)
                                textView.setTextColor(Color.WHITE)
                                nonStringEndDate = selectedDate
                                binding.endDate.text = "%s %s일 (%s)".format(selectedDate!!.month.displayText(), selectedDate!!.dayOfMonth, selectedDate!!.dayOfWeek.displayText()[0])

                            }
                        }
                    }

                    else if (day.date == selectedDate && selected < 1 && binding.switchAllDay.isChecked) {
                        textView.setBackgroundResource(R.drawable.event_circle)
                        textView.setTextColor(Color.WHITE)
                        binding.startDate.text = "%s %s일 (%s)".format(selectedDate!!.month.displayText(), selectedDate!!.dayOfMonth, selectedDate!!.dayOfWeek.displayText()[0])
                        binding.endDate.text = "%s %s일 (%s)".format(selectedDate!!.month.displayText(), selectedDate!!.dayOfMonth, selectedDate!!.dayOfWeek.displayText()[0])
                        selected += 1
                    }

                }






            }

        }


        // 달력 시작 요일 설정, 앱 실행 시 현재에 해당하는 월 보여주기
        val firstDayOfWeek = firstDayOfWeekFromLocale() // Available from the library
        binding.scheduleCalendar.setup(startMonth, endMonth, firstDayOfWeek)
        binding.scheduleCalendar.scrollToMonth(currentMonth)



        // titlesContainer (연, 월 표시)
        val titlesContainer = findViewById<ViewGroup>(R.id.scheduleTitleContainer)
        titlesContainer.children
            .map { it as TextView }
            .forEachIndexed { index, textView ->
                currentMonth.month.displayText(short = false)
            }


        // monthHeaderBinder (일 ~ 월 표시)
        binding.scheduleCalendar.monthHeaderBinder = object : MonthHeaderFooterBinder<MonthViewContainer> {
            override fun create(view: View) = MonthViewContainer(view)
            override fun bind(container: MonthViewContainer, data: CalendarMonth) {
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
        binding.btnGoPrevious.setOnClickListener {
            binding.scheduleCalendar.findFirstVisibleMonth()?.let {
                binding.scheduleCalendar.smoothScrollToMonth(it.yearMonth.previousMonth)
            }
        }

        binding.btnGoNext.setOnClickListener {
            binding.scheduleCalendar.findFirstVisibleMonth()?.let {
                binding.scheduleCalendar.smoothScrollToMonth(it.yearMonth.nextMonth)
            }
        }

        // 완료 버튼 동작 (데이터 넘기기)
        binding.btnAddComplete.setOnClickListener {
            val intent = Intent(this, CalendarActivity::class.java).apply {
                putExtra("eventTitle", binding.editScheduleTitle.text.toString()) // 일정 제목
                putExtra("switchChecked", binding.switchAllDay.isChecked.toString())
                // 하루종일 스위치가 체크인 경우 / 아닌 경우
                if (binding.switchAllDay.isChecked) {
                    putExtra("allDayEventDate", binding.startDate.text.toString())
                }
                else {
                    putExtra("startDate", binding.startDate.text.toString())
                    putExtra("endDate", binding.endDate.text.toString())
                    putExtra("startTime", binding.startTime.text.toString())
                    putExtra("endTime", binding.endTime.text.toString())
                    if (nonStringStartDate == null && nonStringEndDate == null) {
                        putExtra("nonStringStartDate", "")
                        putExtra("nonStringEndDate", "")
                    }
                    else {
                        putExtra("nonStringStartDate", nonStringStartDate.toString())
                        putExtra("nonStringEndDate", nonStringEndDate.toString())
                    }
                }
                putExtra("eventColor", eventColor) // 설정한 이벤트 표시 색상
            }

            setResult(RESULT_OK, intent)
            if (!isFinishing) finish()

        }


    }

    // 달력 옮길 때 상단 title(연, 월) 업데이트
    private fun updateTitle() {
        val month = binding.scheduleCalendar.findFirstVisibleMonth()?.yearMonth ?: return
        binding.scheduleYearText.text = month.year.toString()
        binding.scheduleMonthText.text = month.month.displayText(short = false)
    }

    // text display, month & 요일 표시 한글로 바꾸기
    fun Month.displayText(short: Boolean = true): String {
        val style = if (short) TextStyle.SHORT else TextStyle.FULL
        return getDisplayName(style, Locale.getDefault())
    }

    fun DayOfWeek.displayText(short: Boolean = true): String {
        val style = if (short) TextStyle.SHORT else TextStyle.FULL
        return getDisplayName(style, Locale.getDefault())
    }





}
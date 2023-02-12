package com.eunjeong.booklet

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.core.view.children
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import com.eunjeong.booklet.databinding.ActivityCalendarBinding
import com.eunjeong.booklet.databinding.CalendarDayLayoutBinding
import com.eunjeong.booklet.login.LoginActivity
import com.eunjeong.booklet.login.UserData
import com.google.android.material.navigation.NavigationView
import com.kizitonwose.calendar.core.*
import com.kizitonwose.calendar.view.MonthDayBinder
import com.kizitonwose.calendar.view.MonthHeaderFooterBinder
import com.kizitonwose.calendar.view.ViewContainer
import kotlinx.android.synthetic.main.activity_calendar.*
import kotlinx.android.synthetic.main.nav_header_setting.*
import kotlinx.android.synthetic.main.nav_header_setting.view.*
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.Month
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.*


class CalendarActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener,
    Communicator {

    private lateinit var binding: ActivityCalendarBinding
    private lateinit var tbinding: CalendarDayLayoutBinding
    private val titleRes: Int? = null
    private val today = LocalDate.now()
    private var selectedDate: LocalDate? = null
    var checkDaySelected: Boolean = true
    lateinit var activityResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var userInfo: UserData

    var preventRepetition = true
    var preventRepeat = true
    var p = 1

    var storeSelectedDate: LocalDate? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCalendarBinding.inflate(layoutInflater)
        tbinding = CalendarDayLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //deletePlanByPlanId(31)
//        for (i in 32..33) {
//            deletePlanByPlanId(i)
//        }
        //val modifyPlanInfo = PlanModifyRequest(2, "2", "수정됐나", "green", 2023, 2, 6, 10, 30, 2023, 2, 8, 11, 20)
        //modifyPlanByPlanId(5, modifyPlanInfo)
        //getPlanByPlanId(5)
        getPlanByUserId(UserInformation.userId)
        //getMonthPlanByUserId("2", "2")


        if (intent.hasExtra("UserInfo")) {
            userInfo = intent.getParcelableExtra<UserData>("UserInfo")!!
            Log.d("데이터 전달 성공 in Cal", userInfo?.name.toString() + userInfo?.userId.toString() + userInfo?.img.toString() + userInfo.id.toString())
        }

        binding.btnAddGroup.setOnClickListener {
            val intent = Intent(this, FriendListActivity::class.java)
            intent.putExtra("UserInfo", userInfo)
            startActivity(intent)
        }

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
                    if (day.position == DayPosition.MonthDate) {
                        val currentSelection = selectedDate

                        if (currentSelection == day.date) {
                            selectedDate = null
                            mainCalendarView.notifyDateChanged(currentSelection)
                        } else {
                            selectedDate = day.date
                            mainCalendarView.notifyDateChanged(day.date)
                            if (currentSelection != null) {
                                mainCalendarView.notifyDateChanged(currentSelection)
                            }
                        }
                    }
                    // 날짜 클릭하면 dayScheduleFragment 나오도록
                    supportFragmentManager
                        .beginTransaction()
                        .add(binding.frameFragment.id, DayScheduleFragment().apply {
                            arguments = Bundle().apply { // Activity에서 날짜 누르면 그 날짜를 fragment로 전달하기
                                putString("Date", "${day.date}")
                            }
                        })
                        .addToBackStack("daySchedule").commitAllowingStateLoss()

//                    dayofweek.displayText

                }
                // 요일 다시 찾기
                //Toast.makeText(this@CalendarActivity, "${ day.date.month.displayText(true)}", Toast.LENGTH_SHORT).show()
            }
        }


        class MonthHeaderViewContainer(view: View) : ViewContainer(view) {
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
                } else if (data.position == DayPosition.MonthDate && data.date.dayOfWeek.value == 7) {
                    container.textView.setTextColor(Color.RED)
                } else if (data.position != DayPosition.MonthDate) {
                    container.textView.setTextColor(Color.GRAY)
                } else {
                    container.textView.setTextColor(Color.BLACK)
                }

                // 오늘 날짜 원으로 표시 & single day select (square border)
                val day = data
                val textView = container.textView // 달력 속 text
                container.day = data
                textView.text = day.date.dayOfMonth.toString()
                val dayView = container.view // 달력 한 칸 view

                val firstEvent = container.event1
                val secondEvent = container.event2
                val thirdEvent = container.event3
                val fourthEvent = container.event4

                if (day.position == DayPosition.MonthDate) {
                    textView.visibility = View.VISIBLE
                    if (day.date == today) { // 오늘 날짜 고정으로 원 배경 표시
                        textView.setBackgroundResource(R.drawable.today_circle)
                    }

                    if (day.date == selectedDate) {
                        dayView.setBackgroundResource(R.drawable.selected_border)
                    } else if (day.date != selectedDate) { // 한 번 더 누르면 border 제거
                        dayView.background = null
                    }
                }


                // 단순 일정 추가 함수
                // 여기에 매개변수 더 추가. addevent 함수를 바로 아래 retrofit 함수에 넣고
                // startmonth/day, endmonth/day 다르면 으로 조건 바꾸기
                // 하루종일 스위치 -> 일정 추가 액티비티에서 스위치 켜져있으면 추가하는 데이터를 0~23:59로 조작
                fun addEvent(
                    eventNum: TextView,
                    eventTitle: String,
                    selectedColor: String,
                    startYear: Int,
                    startMonth: Int,
                    startDay: Int,
                    endYear: Int,
                    endMonth: Int,
                    endDay: Int
                ) {
                    val sdayString = "%d-%02d-%02d".format(startYear, startMonth, startDay)
                    val sdayLocalDate = LocalDate.parse(sdayString, DateTimeFormatter.ISO_DATE)
                    val edayString = "%d-%02d-%02d".format(endYear, endMonth, endDay)
                    val edayLocalDate = LocalDate.parse(edayString, DateTimeFormatter.ISO_DATE)

                    if (selectedColor == "pink") {
                        // 시작 날짜와 끝 날짜가 다른 경우
                        if (sdayLocalDate != edayLocalDate) {
                            if (day.date == sdayLocalDate) {
                                preventRepetition = true

                                if (firstEvent.background == null) {
                                    p = 1
                                } else if (secondEvent.background == null) {
                                    p = 2
                                } else if (thirdEvent.background == null) {
                                    p = 3
                                } else if (fourthEvent.background == null) {
                                    p = 4
                                }

                                eventNum.text = eventTitle
                                eventNum.setBackgroundResource(R.drawable.event_on_calendar_corner_pink_start)
                                eventNum.setTextColor(
                                    ContextCompat.getColor(
                                        applicationContext,
                                        R.color.eventpinktext
                                    )
                                )

                            } else if (day.date.isBefore(edayLocalDate) && day.date.isAfter(sdayLocalDate)) {
                                eventNum.setBackgroundResource(R.drawable.event_on_calendar_corner_pink_middle)
                                eventNum.setTextColor(
                                    ContextCompat.getColor(
                                        applicationContext,
                                        R.color.eventpinktext
                                    )
                                )
                            } else if (day.date == edayLocalDate) {
                                eventNum.setBackgroundResource(R.drawable.event_on_calendar_corner_pink_end)
                                eventNum.setTextColor(
                                    ContextCompat.getColor(
                                        applicationContext,
                                        R.color.eventpinktext
                                    )
                                )
                                p = 0
                                preventRepetition = false
                            }

                        }
                        // 시작 날짜 == 끝 날짜지만 시간만 다른 경우
                        else if (sdayLocalDate == edayLocalDate && day.date == sdayLocalDate) {
                            preventRepetition = true
                            eventNum.text = eventTitle
                            eventNum.setBackgroundResource(R.drawable.event_on_calendar_corner_pink)
                            eventNum.setTextColor(
                                ContextCompat.getColor(
                                    applicationContext,
                                    R.color.eventpinktext
                                )
                            )
                            preventRepetition = false
                        }

                    }
                    else if (selectedColor == "green") {
                        // 시작 날짜와 끝 날짜가 다른 경우
                        if (sdayLocalDate != edayLocalDate) {
                            if (day.date == sdayLocalDate) {
                                preventRepetition = true

                                if (firstEvent.background == null) {
                                    p = 1
                                } else if (secondEvent.background == null) {
                                    p = 2
                                } else if (thirdEvent.background == null) {
                                    p = 3
                                } else if (fourthEvent.background == null) {
                                    p = 4
                                }

                                eventNum.text = eventTitle
                                eventNum.setBackgroundResource(R.drawable.event_on_calendar_corner_green_start)
                                eventNum.setTextColor(
                                    ContextCompat.getColor(
                                        applicationContext,
                                        R.color.eventgreentext
                                    )
                                )

                            } else if (day.date.isBefore(edayLocalDate) && day.date.isAfter(
                                    sdayLocalDate
                                )
                            ) {
                                eventNum.setBackgroundResource(R.drawable.event_on_calendar_corner_green_middle)
                                eventNum.setTextColor(
                                    ContextCompat.getColor(
                                        applicationContext,
                                        R.color.eventgreentext
                                    )
                                )
                            } else if (day.date == edayLocalDate) {
                                eventNum.setBackgroundResource(R.drawable.event_on_calendar_corner_green_end)
                                eventNum.setTextColor(
                                    ContextCompat.getColor(
                                        applicationContext,
                                        R.color.eventgreentext
                                    )
                                )
                                p = 0
                                preventRepetition = false
                            }
                        }
                        // 시작 날짜 == 끝 날짜지만 시간만 다른 경우
                        else if (sdayLocalDate == edayLocalDate && day.date == sdayLocalDate) {
                            preventRepetition = true
                            eventNum.text = eventTitle
                            eventNum.setBackgroundResource(R.drawable.event_on_calendar_corner_green)
                            eventNum.setTextColor(
                                ContextCompat.getColor(
                                    applicationContext,
                                    R.color.eventgreentext
                                )
                            )
                            preventRepetition = false
                        }

                    }
                    else if (selectedColor == "purple") {
                        // 시작 날짜와 끝 날짜가 다른 경우
                        if (sdayLocalDate != edayLocalDate) {
                            if (day.date == sdayLocalDate) {
                                preventRepetition = true

                                if (firstEvent.background == null) {
                                    p = 1
                                } else if (secondEvent.background == null) {
                                    p = 2
                                } else if (thirdEvent.background == null) {
                                    p = 3
                                } else if (fourthEvent.background == null) {
                                    p = 4
                                }

                                eventNum.text = eventTitle
                                eventNum.setBackgroundResource(R.drawable.event_on_calendar_corner_purple_start)
                                eventNum.setTextColor(
                                    ContextCompat.getColor(
                                        applicationContext,
                                        R.color.eventpurpletext
                                    )
                                )

                            } else if (day.date.isBefore(edayLocalDate) && day.date.isAfter(sdayLocalDate)) {
                                eventNum.setBackgroundResource(R.drawable.event_on_calendar_corner_purple_middle)
                                eventNum.setTextColor(
                                    ContextCompat.getColor(
                                        applicationContext,
                                        R.color.eventpurpletext
                                    )
                                )
                            } else if (day.date == edayLocalDate) {
                                eventNum.setBackgroundResource(R.drawable.event_on_calendar_corner_purple_end)
                                eventNum.setTextColor(
                                    ContextCompat.getColor(
                                        applicationContext,
                                        R.color.eventpurpletext
                                    )
                                )
                                p = 0
                                preventRepetition = false
                            }

                        }
                        // 시작 날짜 == 끝 날짜지만 시간만 다른 경우
                        else if (sdayLocalDate == edayLocalDate && day.date == sdayLocalDate) {
                            preventRepetition = true
                            eventNum.text = eventTitle
                            eventNum.setBackgroundResource(R.drawable.event_on_calendar_corner_purple)
                            eventNum.setTextColor(
                                ContextCompat.getColor(
                                    applicationContext,
                                    R.color.eventpurpletext
                                )
                            )

                            preventRepetition = false
                        }

                    }
                }


                // 현재 달력 한 칸에 있는 일정의 위치를 반영하여 일정 추가하는 함수
                fun addEventOnPosition(
                    eventTitle: String,
                    selectedColor: String,
                    startYear: Int,
                    startMonth: Int,
                    startDay: Int,
                    endYear: Int,
                    endMonth: Int,
                    endDay: Int
                ) {
                    val sdayString = "%d-%02d-%02d".format(startYear, startMonth, startDay)
                    val sdayLocalDate = LocalDate.parse(sdayString, DateTimeFormatter.ISO_DATE)
                    val edayString = "%d-%02d-%02d".format(endYear, endMonth, endDay)
                    val edayLocalDate = LocalDate.parse(edayString, DateTimeFormatter.ISO_DATE)

                    if (sdayLocalDate != edayLocalDate) {
                        p = 0

                        when (p) {

                            0 -> {
                                if (firstEvent.background == null) {
                                    addEvent(firstEvent, eventTitle, selectedColor, startYear, startMonth, startDay, endYear, endMonth, endDay)
                                } else if (secondEvent.background == null) {
                                    addEvent(secondEvent, eventTitle, selectedColor, startYear, startMonth, startDay, endYear, endMonth, endDay)
                                } else if (thirdEvent.background == null) {
                                    addEvent(thirdEvent, eventTitle, selectedColor, startYear, startMonth, startDay, endYear, endMonth, endDay)
                                } else if (fourthEvent.background == null) {
                                    addEvent(fourthEvent, eventTitle, selectedColor, startYear, startMonth, startDay, endYear, endMonth, endDay)
                                }

                            }
                            1 -> addEvent(firstEvent, eventTitle, selectedColor, startYear, startMonth, startDay, endYear, endMonth, endDay)
                            2 -> addEvent(secondEvent, eventTitle, selectedColor, startYear, startMonth, startDay, endYear, endMonth, endDay)
                            3 -> addEvent(thirdEvent, eventTitle, selectedColor, startYear, startMonth, startDay, endYear, endMonth, endDay)
                            4 -> addEvent(fourthEvent, eventTitle, selectedColor, startYear, startMonth, startDay, endYear, endMonth, endDay)
                        }

                    }
                    else {
                        if (firstEvent.background == null) {
                            addEvent(firstEvent, eventTitle, selectedColor, startYear, startMonth, startDay, endYear, endMonth, endDay)
                        } else if (secondEvent.background == null) {
                            addEvent(secondEvent, eventTitle, selectedColor, startYear, startMonth, startDay, endYear, endMonth, endDay)
                        } else if (thirdEvent.background == null) {
                            addEvent(thirdEvent, eventTitle, selectedColor, startYear, startMonth, startDay, endYear, endMonth, endDay)
                        } else if (fourthEvent.background == null) {
                            addEvent(fourthEvent, eventTitle, selectedColor, startYear, startMonth, startDay, endYear, endMonth, endDay)
                        }
                    }

                }


                fun addUserPlanOnCalendar(userId: String) {
                    val clientBuilder = OkHttpClient.Builder()

                    val retrofit = Retrofit.Builder()
                        .baseUrl("http://3.35.217.34:8080")
                        .addConverterFactory(GsonConverterFactory.create())
                        .client(clientBuilder.build())
                        .build()

                    val retrofitPlanService =
                        retrofit.create(RetrofitPlanService::class.java)
                    retrofitPlanService.getPlanByUserId(userId)
                        .enqueue(object : Callback<GetPlanByUserIdResponse> {
                            override fun onResponse(
                                call: Call<GetPlanByUserIdResponse>,
                                response: Response<GetPlanByUserIdResponse>
                            ) {
                                val responseData = response.body()
                                if (responseData != null) {
                                    for (i in 0 until responseData.result.size) {
                                        val eventName = responseData.result[i].name
                                        val eventColor = responseData.result[i].color
                                        val sy = responseData.result[i].startYear
                                        val sm = responseData.result[i].startMonth
                                        val sd = responseData.result[i].startDay
                                        val ey = responseData.result[i].endYear
                                        val em = responseData.result[i].endMonth
                                        val ed = responseData.result[i].endDay
                                        addEventOnPosition(eventName, eventColor, sy, sm, sd, ey, em, ed)

                                    }
                                }
                                else {
                                    Log.d("thisistag", response.toString())
                                }
                            }

                            override fun onFailure(
                                call: Call<GetPlanByUserIdResponse>,
                                t: Throwable
                            ) {
                                Log.e("thisistag", t.toString())
                            }

                        })
                }


                if (preventRepeat && day.date != selectedDate) {
                    addUserPlanOnCalendar(UserInformation.userId)
                }
                else if (day.date == selectedDate) {
                    preventRepeat = false
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
        binding.mainCalendarView.monthHeaderBinder =
            object : MonthHeaderFooterBinder<MonthViewContainer> {
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

        binding.btnGroupChoice.setOnClickListener {
            val intent = Intent(this, GroupChoiceActivity::class.java)
            startActivity(intent)
        }

//        binding.btnAddFriend.setOnClickListener {
//            val intent = Intent(this, GroupChoiceActivity::class.java)
//            startActivity(intent)
//        }

        binding.btnMenu.setOnClickListener {
            binding.drawerLayout.openDrawer((GravityCompat.END))
        }


        // 네비게이션 메뉴 초기화
        initNavigationMenu()


        // 우하단 + 버튼 누르면 일정 추가 화면으로 넘어가기 (+ 선택한 날짜도 같이 넘어가게)
        binding.btnAddSchedule.setOnClickListener {
            val addIntent = Intent(this, AddScheduleActivity::class.java)

            if (selectedDate != null) { // 달력에서 요일을 한 번이라도 선택했을 경우 선택한 날짜에 일정 추가
                addIntent.putExtra("selectedYear", selectedDate!!.year.toString())
                addIntent.putExtra("selectedMonth", selectedDate!!.month.value.toString())
                addIntent.putExtra("selectedDay", selectedDate!!.dayOfMonth.toString())
                addIntent.putExtra("selectedDayofWeek", selectedDate!!.dayOfWeek.toString())
                addIntent.putExtra("checkDaySelected", checkDaySelected.toString())
            } else { // 달력에서 요일을 선택하지 않고 일정 추가 버튼을 눌렀을 경우 오늘 날짜에 일정 추가
                addIntent.putExtra("todayYear", today.year.toString())
                addIntent.putExtra("todayMonth", today.month.toString())
                addIntent.putExtra("todayDay", today.dayOfMonth.toString())
                addIntent.putExtra("todayDayofWeek", today.dayOfWeek.toString())
            }

            storeSelectedDate = selectedDate
            selectedDate = null
            activityResultLauncher.launch(addIntent)

        }

        activityResultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == RESULT_OK) {
                    preventRepeat = true
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
                                if (day.position == DayPosition.MonthDate) {
                                    val currentSelection = selectedDate

                                    if (currentSelection == day.date) {
                                        selectedDate = null
                                        mainCalendarView.notifyDateChanged(currentSelection)
                                    } else {
                                        selectedDate = day.date
                                        mainCalendarView.notifyDateChanged(day.date)
                                        if (currentSelection != null) {
                                            mainCalendarView.notifyDateChanged(currentSelection)
                                        }
                                    }
                                }
                                // 날짜 클릭하면 dayScheduleFragment 나오도록
                                supportFragmentManager
                                    .beginTransaction()
                                    .add(binding.frameFragment.id, DayScheduleFragment().apply {
                                        arguments = Bundle().apply { // Activity에서 날짜 누르면 그 날짜를 fragment로 전달하기
                                            putString("Date", "${day.date}")
                                        }
                                    })
                                    .addToBackStack("daySchedule").commitAllowingStateLoss()

//                    dayofweek.displayText

                            }
                            // 요일 다시 찾기
                            //Toast.makeText(this@CalendarActivity, "${ day.date.month.displayText(true)}", Toast.LENGTH_SHORT).show()
                        }
                    }


                    class MonthHeaderViewContainer(view: View) : ViewContainer(view) {
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
                            } else if (data.position == DayPosition.MonthDate && data.date.dayOfWeek.value == 7) {
                                container.textView.setTextColor(Color.RED)
                            } else if (data.position != DayPosition.MonthDate) {
                                container.textView.setTextColor(Color.GRAY)
                            } else {
                                container.textView.setTextColor(Color.BLACK)
                            }

                            // 오늘 날짜 원으로 표시 & single day select (square border)
                            val day = data
                            val textView = container.textView // 달력 속 text
                            container.day = data
                            textView.text = day.date.dayOfMonth.toString()
                            val dayView = container.view // 달력 한 칸 view

                            val firstEvent = container.event1
                            val secondEvent = container.event2
                            val thirdEvent = container.event3
                            val fourthEvent = container.event4

                            if (day.position == DayPosition.MonthDate) {
                                textView.visibility = View.VISIBLE
                                if (day.date == today) { // 오늘 날짜 고정으로 원 배경 표시
                                    textView.setBackgroundResource(R.drawable.today_circle)
                                }

                                if (day.date == selectedDate) {
                                    dayView.setBackgroundResource(R.drawable.selected_border)
                                } else if (day.date != selectedDate) { // 한 번 더 누르면 border 제거
                                    dayView.background = null
                                }
                            }

                            // 단순 일정 추가 함수
                            // 여기에 매개변수 더 추가. addevent 함수를 바로 아래 retrofit 함수에 넣고
                            // startmonth/day, endmonth/day 다르면 으로 조건 바꾸기
                            // 하루종일 스위치 -> 일정 추가 액티비티에서 스위치 켜져있으면 추가하는 데이터를 0~23:59로 조작
                            fun addEvent(
                                eventNum: TextView,
                                eventTitle: String,
                                selectedColor: String,
                                startYear: Int,
                                startMonth: Int,
                                startDay: Int,
                                endYear: Int,
                                endMonth: Int,
                                endDay: Int
                            ) {
                                val sdayString = "%d-%02d-%02d".format(startYear, startMonth, startDay)
                                val sdayLocalDate = LocalDate.parse(sdayString, DateTimeFormatter.ISO_DATE)
                                val edayString = "%d-%02d-%02d".format(endYear, endMonth, endDay)
                                val edayLocalDate = LocalDate.parse(edayString, DateTimeFormatter.ISO_DATE)

                                if (selectedColor == "pink") {
                                    // 시작 날짜와 끝 날짜가 다른 경우
                                    if (sdayLocalDate != edayLocalDate) {
                                        if (day.date == sdayLocalDate) {
                                            preventRepetition = true

                                            if (firstEvent.background == null) {
                                                p = 1
                                            } else if (secondEvent.background == null) {
                                                p = 2
                                            } else if (thirdEvent.background == null) {
                                                p = 3
                                            } else if (fourthEvent.background == null) {
                                                p = 4
                                            }

                                            eventNum.text = eventTitle
                                            eventNum.setBackgroundResource(R.drawable.event_on_calendar_corner_pink_start)
                                            eventNum.setTextColor(
                                                ContextCompat.getColor(
                                                    applicationContext,
                                                    R.color.eventpinktext
                                                )
                                            )

                                        } else if (day.date.isBefore(edayLocalDate) && day.date.isAfter(sdayLocalDate)) {
                                            eventNum.setBackgroundResource(R.drawable.event_on_calendar_corner_pink_middle)
                                            eventNum.setTextColor(
                                                ContextCompat.getColor(
                                                    applicationContext,
                                                    R.color.eventpinktext
                                                )
                                            )
                                        } else if (day.date == edayLocalDate) {
                                            eventNum.setBackgroundResource(R.drawable.event_on_calendar_corner_pink_end)
                                            eventNum.setTextColor(
                                                ContextCompat.getColor(
                                                    applicationContext,
                                                    R.color.eventpinktext
                                                )
                                            )
                                            p = 0
                                            preventRepetition = false
                                        }
//                                    nonStringStartDate = null
//                                    nonStringEndDate = null
                                    }
                                    // 시작 날짜 == 끝 날짜지만 시간만 다른 경우
                                    else if (sdayLocalDate == edayLocalDate && day.date == sdayLocalDate) {
                                        preventRepetition = true
                                        eventNum.text = eventTitle
                                        eventNum.setBackgroundResource(R.drawable.event_on_calendar_corner_pink)
                                        eventNum.setTextColor(
                                            ContextCompat.getColor(
                                                applicationContext,
                                                R.color.eventpinktext
                                            )
                                        )
                                        preventRepetition = false
                                    }

                                }
                                else if (selectedColor == "green") {
                                    // 시작 날짜와 끝 날짜가 다른 경우
                                    if (sdayLocalDate != edayLocalDate) {
                                        if (day.date == sdayLocalDate) {
                                            preventRepetition = true

                                            if (firstEvent.background == null) {
                                                p = 1
                                            } else if (secondEvent.background == null) {
                                                p = 2
                                            } else if (thirdEvent.background == null) {
                                                p = 3
                                            } else if (fourthEvent.background == null) {
                                                p = 4
                                            }

                                            eventNum.text = eventTitle
                                            eventNum.setBackgroundResource(R.drawable.event_on_calendar_corner_green_start)
                                            eventNum.setTextColor(
                                                ContextCompat.getColor(
                                                    applicationContext,
                                                    R.color.eventgreentext
                                                )
                                            )

                                        } else if (day.date.isBefore(edayLocalDate) && day.date.isAfter(
                                                sdayLocalDate
                                            )
                                        ) {
                                            eventNum.setBackgroundResource(R.drawable.event_on_calendar_corner_green_middle)
                                            eventNum.setTextColor(
                                                ContextCompat.getColor(
                                                    applicationContext,
                                                    R.color.eventgreentext
                                                )
                                            )
                                        } else if (day.date == edayLocalDate) {
                                            eventNum.setBackgroundResource(R.drawable.event_on_calendar_corner_green_end)
                                            eventNum.setTextColor(
                                                ContextCompat.getColor(
                                                    applicationContext,
                                                    R.color.eventgreentext
                                                )
                                            )
                                            p = 0
                                            preventRepetition = false
                                        }
//                                    nonStringStartDate = null
//                                    nonStringEndDate = null
                                    }
                                    // 시작 날짜 == 끝 날짜지만 시간만 다른 경우
                                    else if (sdayLocalDate == edayLocalDate && day.date == sdayLocalDate) {
                                        preventRepetition = true
                                        eventNum.text = eventTitle
                                        eventNum.setBackgroundResource(R.drawable.event_on_calendar_corner_green)
                                        eventNum.setTextColor(
                                            ContextCompat.getColor(
                                                applicationContext,
                                                R.color.eventgreentext
                                            )
                                        )
                                        preventRepetition = false
                                    }

                                }
                                else if (selectedColor == "purple") {
                                    // 시작 날짜와 끝 날짜가 다른 경우
                                    if (sdayLocalDate != edayLocalDate) {
                                        if (day.date == sdayLocalDate) {
                                            preventRepetition = true

                                            if (firstEvent.background == null) {
                                                p = 1
                                            } else if (secondEvent.background == null) {
                                                p = 2
                                            } else if (thirdEvent.background == null) {
                                                p = 3
                                            } else if (fourthEvent.background == null) {
                                                p = 4
                                            }

                                            eventNum.text = eventTitle
                                            eventNum.setBackgroundResource(R.drawable.event_on_calendar_corner_purple_start)
                                            eventNum.setTextColor(
                                                ContextCompat.getColor(
                                                    applicationContext,
                                                    R.color.eventpurpletext
                                                )
                                            )

                                        } else if (day.date.isBefore(edayLocalDate) && day.date.isAfter(sdayLocalDate)) {
                                            eventNum.setBackgroundResource(R.drawable.event_on_calendar_corner_purple_middle)
                                            eventNum.setTextColor(
                                                ContextCompat.getColor(
                                                    applicationContext,
                                                    R.color.eventpurpletext
                                                )
                                            )
                                        } else if (day.date == edayLocalDate) {
                                            eventNum.setBackgroundResource(R.drawable.event_on_calendar_corner_purple_end)
                                            eventNum.setTextColor(
                                                ContextCompat.getColor(
                                                    applicationContext,
                                                    R.color.eventpurpletext
                                                )
                                            )
                                            p = 0
                                            preventRepetition = false
                                        }

                                    }
                                    // 시작 날짜 == 끝 날짜지만 시간만 다른 경우
                                    else if (sdayLocalDate == edayLocalDate && day.date == sdayLocalDate) {
                                        preventRepetition = true
                                        eventNum.text = eventTitle
                                        eventNum.setBackgroundResource(R.drawable.event_on_calendar_corner_purple)
                                        eventNum.setTextColor(
                                            ContextCompat.getColor(
                                                applicationContext,
                                                R.color.eventpurpletext
                                            )
                                        )

                                        preventRepetition = false
                                    }

                                }
                            }


                            // 현재 달력 한 칸에 있는 일정의 위치를 반영하여 일정 추가하는 함수
                            fun addEventOnPosition(
                                eventTitle: String,
                                selectedColor: String,
                                startYear: Int,
                                startMonth: Int,
                                startDay: Int,
                                endYear: Int,
                                endMonth: Int,
                                endDay: Int
                            ) {
                                val sdayString = "%d-%02d-%02d".format(startYear, startMonth, startDay)
                                val sdayLocalDate = LocalDate.parse(sdayString, DateTimeFormatter.ISO_DATE)
                                val edayString = "%d-%02d-%02d".format(endYear, endMonth, endDay)
                                val edayLocalDate = LocalDate.parse(edayString, DateTimeFormatter.ISO_DATE)

                                if (sdayLocalDate != edayLocalDate) {

                                    when (p) {

                                        0 -> {
                                            if (firstEvent.background == null) {
                                                addEvent(firstEvent, eventTitle, selectedColor, startYear, startMonth, startDay, endYear, endMonth, endDay)
                                            } else if (secondEvent.background == null) {
                                                addEvent(secondEvent, eventTitle, selectedColor, startYear, startMonth, startDay, endYear, endMonth, endDay)
                                            } else if (thirdEvent.background == null) {
                                                addEvent(thirdEvent, eventTitle, selectedColor, startYear, startMonth, startDay, endYear, endMonth, endDay)
                                            } else if (fourthEvent.background == null) {
                                                addEvent(fourthEvent, eventTitle, selectedColor, startYear, startMonth, startDay, endYear, endMonth, endDay)
                                            }

                                        }
                                        1 -> addEvent(firstEvent, eventTitle, selectedColor, startYear, startMonth, startDay, endYear, endMonth, endDay)
                                        2 -> addEvent(secondEvent, eventTitle, selectedColor, startYear, startMonth, startDay, endYear, endMonth, endDay)
                                        3 -> addEvent(thirdEvent, eventTitle, selectedColor, startYear, startMonth, startDay, endYear, endMonth, endDay)
                                        4 -> addEvent(fourthEvent, eventTitle, selectedColor, startYear, startMonth, startDay, endYear, endMonth, endDay)
                                    }

                                }
                                else {
                                    if (firstEvent.background == null) {
                                        addEvent(firstEvent, eventTitle, selectedColor, startYear, startMonth, startDay, endYear, endMonth, endDay)
                                    } else if (secondEvent.background == null) {
                                        addEvent(secondEvent, eventTitle, selectedColor, startYear, startMonth, startDay, endYear, endMonth, endDay)
                                    } else if (thirdEvent.background == null) {
                                        addEvent(thirdEvent, eventTitle, selectedColor, startYear, startMonth, startDay, endYear, endMonth, endDay)
                                    } else if (fourthEvent.background == null) {
                                        addEvent(fourthEvent, eventTitle, selectedColor, startYear, startMonth, startDay, endYear, endMonth, endDay)
                                    }
                                }

                            }


                            fun addUserPlanOnCalendar(userId: String) {
                                val clientBuilder = OkHttpClient.Builder()

                                val retrofit = Retrofit.Builder()
                                    .baseUrl("http://3.35.217.34:8080")
                                    .addConverterFactory(GsonConverterFactory.create())
                                    .client(clientBuilder.build())
                                    .build()

                                val retrofitPlanService =
                                    retrofit.create(RetrofitPlanService::class.java)
                                retrofitPlanService.getPlanByUserId(userId)
                                    .enqueue(object : Callback<GetPlanByUserIdResponse> {
                                        override fun onResponse(
                                            call: Call<GetPlanByUserIdResponse>,
                                            response: Response<GetPlanByUserIdResponse>
                                        ) {
                                            val responseData = response.body()
                                            if (responseData != null) {
                                                for (i in 0 until responseData.result.size) {
                                                    val eventName = responseData.result[i].name
                                                    val eventColor = responseData.result[i].color
                                                    val sy = responseData.result[i].startYear
                                                    val sm = responseData.result[i].startMonth
                                                    val sd = responseData.result[i].startDay
                                                    val ey = responseData.result[i].endYear
                                                    val em = responseData.result[i].endMonth
                                                    val ed = responseData.result[i].endDay
                                                    addEventOnPosition(
                                                        eventName,
                                                        eventColor,
                                                        sy,
                                                        sm,
                                                        sd,
                                                        ey,
                                                        em,
                                                        ed
                                                    )
                                                }
                                                //preventRepetition = false
                                            }
                                            else {
                                                Log.d("thisistag", response.toString())
                                            }
                                        }

                                        override fun onFailure(
                                            call: Call<GetPlanByUserIdResponse>,
                                            t: Throwable
                                        ) {
                                            Log.e("thisistag", t.toString())
                                        }

                                    })
                            }

                            if (preventRepeat && day.date != selectedDate) {
                                addUserPlanOnCalendar(UserInformation.userId)
                            }
                            else if (day.date == selectedDate) {
                                preventRepeat = false
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
                    binding.mainCalendarView.monthHeaderBinder =
                        object : MonthHeaderFooterBinder<MonthViewContainer> {
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


    // retrofit
    private fun getPlanByPlanId(planId: Int) {
        val clientBuilder = OkHttpClient.Builder()

        val retrofit = Retrofit.Builder()
            .baseUrl("http://3.35.217.34:8080")
            .addConverterFactory(GsonConverterFactory.create())
            .client(clientBuilder.build())
            .build()

        val retrofitPlanService = retrofit.create(RetrofitPlanService::class.java)
        retrofitPlanService.getPlanByPlanId(planId)
            .enqueue(object : Callback<GetPlanByPlanIdResponse> {
                override fun onResponse(
                    call: Call<GetPlanByPlanIdResponse>,
                    response: Response<GetPlanByPlanIdResponse>
                ) {
                    val responseData = response.body()
                    if (responseData != null) {
                        //Log.d("thisistag", responseData.toString())
                        Log.d(
                            "thisistag",
                            "성공 여부: ${responseData.isSuccess}, message: ${responseData.message}, code: ${responseData.code}, result: ${responseData.result}"
                        )
                    } else {
                        Log.d("thisistag", response.toString())
                    }
                }

                override fun onFailure(call: Call<GetPlanByPlanIdResponse>, t: Throwable) {
                    Log.e("thisistag", t.toString())
                }

            })
    }

    private fun deletePlanByPlanId(planId: Int) {
        val clientBuilder = OkHttpClient.Builder()

        val retrofit = Retrofit.Builder()
            .baseUrl("http://3.35.217.34:8080")
            .addConverterFactory(GsonConverterFactory.create())
            .client(clientBuilder.build())
            .build()

        val retrofitPlanService = retrofit.create(RetrofitPlanService::class.java)
        retrofitPlanService.deletePlanByPlanId(planId).enqueue(object : Callback<PlanResponse> {
            override fun onResponse(
                call: Call<PlanResponse>,
                response: Response<PlanResponse>
            ) {
                val responseData = response.body()
                if (responseData != null) {
                    //Log.d("thisistag", responseData.toString())
                    Log.d(
                        "thisistag",
                        "성공 여부: ${responseData.isSuccess}, message: ${responseData.message}, code: ${responseData.code}, result: ${responseData.result}"
                    )
                } else {
                    Log.d("thisistag", response.toString())
                }
            }

            override fun onFailure(call: Call<PlanResponse>, t: Throwable) {
                Log.e("thisistag", t.toString())
            }

        })
    }

    private fun modifyPlanByPlanId(planId: Int, modifyPlanInfo: PlanModifyRequest) {
        val clientBuilder = OkHttpClient.Builder()

        val retrofit = Retrofit.Builder()
            .baseUrl("http://3.35.217.34:8080")
            .addConverterFactory(GsonConverterFactory.create())
            .client(clientBuilder.build())
            .build()

        val retrofitPlanService = retrofit.create(RetrofitPlanService::class.java)
        retrofitPlanService.modifyPlanByPlanId(planId, modifyPlanInfo)
            .enqueue(object : Callback<ModifyPlanByPlanIdResponse> {
                override fun onResponse(
                    call: Call<ModifyPlanByPlanIdResponse>,
                    response: Response<ModifyPlanByPlanIdResponse>
                ) {
                    val responseData = response.body()
                    if (responseData != null) {
                        Log.d(
                            "thisistag",
                            "성공 여부: ${responseData.isSuccess}, message: ${responseData.message}, code: ${responseData.code}, result: ${responseData.result}"
                        )
                    } else {
                        Log.d("thisistag", response.toString())
                    }
                }

                override fun onFailure(call: Call<ModifyPlanByPlanIdResponse>, t: Throwable) {
                    Log.e("thisistag", t.toString())
                }

            })
    }

    fun getPlanByUserId(userId: String) {
        val clientBuilder = OkHttpClient.Builder()

        val retrofit = Retrofit.Builder()
            .baseUrl("http://3.35.217.34:8080")
            .addConverterFactory(GsonConverterFactory.create())
            .client(clientBuilder.build())
            .build()

        val retrofitPlanService = retrofit.create(RetrofitPlanService::class.java)
        retrofitPlanService.getPlanByUserId(userId)
            .enqueue(object : Callback<GetPlanByUserIdResponse> {
                override fun onResponse(
                    call: Call<GetPlanByUserIdResponse>,
                    response: Response<GetPlanByUserIdResponse>
                ) {
                    val responseData = response.body()
                    if (responseData != null) {
                        Log.d("thisistag", responseData.toString())
                    } else {
                        Log.d("thisistag", response.toString())
                    }
                }

                override fun onFailure(call: Call<GetPlanByUserIdResponse>, t: Throwable) {
                    Log.e("thisistag", t.toString())
                }

            })
    }

    private fun getMonthPlanByUserId(userId: String, m: String) {
        val clientBuilder = OkHttpClient.Builder()

        val retrofit = Retrofit.Builder()
            .baseUrl("http://3.35.217.34:8080")
            .addConverterFactory(GsonConverterFactory.create())
            .client(clientBuilder.build())
            .build()

        val retrofitPlanService = retrofit.create(RetrofitPlanService::class.java)
        retrofitPlanService.getMonthPlanByUserId(userId, m)
            .enqueue(object : Callback<GetPlanByUserIdResponse> {
                override fun onResponse(
                    call: Call<GetPlanByUserIdResponse>,
                    response: Response<GetPlanByUserIdResponse>
                ) {
                    val responseData = response.body()
                    if (responseData != null) {
                        Log.d("thisistag", responseData.toString())
                    } else {
                        Log.d("thisistag", response.toString())
                    }
                }

                override fun onFailure(call: Call<GetPlanByUserIdResponse>, t: Throwable) {
                    Log.e("thisistag", t.toString())
                }

            })
    }


    // Navigation Drawer 메뉴를 초기화하는 함수
    private fun initNavigationMenu() {
        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView

        navView.setNavigationItemSelectedListener(this)

        // 메뉴(설정) 버튼 눌렀을 때 Navigation drawer 보이기
        binding.btnMenu.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.END)
        }

        // 로그 아웃 버튼 누르면 로그인 화면으로 돌아가기
        binding.signOutBtn.setOnClickListener {
            val intent = Intent(this@CalendarActivity, LoginActivity::class.java)
            startActivity(intent)
        }

        // Navigation Header 메뉴에 클릭 이벤트 연결
        val headerView = navView.getHeaderView(0)
        val back = headerView.findViewById<ImageButton>(R.id.backbtn)
        val edit = headerView.findViewById<Button>(R.id.editbtn)

        // 받아온 데이터로 내 정보 표출
        headerView.name.text = userInfo.name
        headerView.code.text = userInfo.userId
        //headerView.img.setImageResource(userInfo.img.toInt())

        back.setOnClickListener { // drawer 닫기
            drawerLayout.closeDrawer(GravityCompat.END) }

        edit.setOnClickListener {  // 편집 버튼
            val intent = Intent(this@CalendarActivity, ManageMyProfileActivity::class.java)
            startActivity(intent) }
    }

    // Drawer content 내 메뉴 클릭 이벤트 처리하는 함수
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        TODO("Not yet implemented")
    }


    // 달력에서 날짜 누르면 DayScheduleFragment가 나오고,
    // DayScheduleFragment에서 버튼을 누르면 TimeTableFragment로 가기 위한 함수
    // interface :  DayScheduleFragment -> timetableFragment
    override fun GoToTimeTableFragment(clickedDate: String) {
        val bundle = Bundle()
        bundle.putString("Date", clickedDate)

        val transaction = this.supportFragmentManager.beginTransaction()
        val timetablefragment = TimeTableFragment()
        timetablefragment.arguments = bundle

        transaction.replace(binding.frameFragment.id, timetablefragment)
        transaction.addToBackStack("timetableFragment")
        transaction.commit()

    }

    // interface : timetableFragment -> DayScheduleFragment
    override fun GoToDayScheduleFragment(clickedDate: String) {
        val bundle = Bundle()
        bundle.putString("Date", clickedDate)

        val transaction = this.supportFragmentManager.beginTransaction()
        val dayschedulefragment = DayScheduleFragment()
        dayschedulefragment.arguments = bundle

        transaction.replace(binding.frameFragment.id, dayschedulefragment)
        transaction.addToBackStack("dayscheduleFragment").commitAllowingStateLoss()
        transaction.commit()
    }


}





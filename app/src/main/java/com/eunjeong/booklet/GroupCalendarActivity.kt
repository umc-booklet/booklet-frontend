package com.eunjeong.booklet

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.core.content.ContextCompat
import androidx.core.view.children
import androidx.core.view.isVisible
import com.eunjeong.booklet.databinding.ActivityCalendarBinding
import com.eunjeong.booklet.databinding.ActivityGroupCalendarBinding
import com.eunjeong.booklet.databinding.BottomSheetShowMembersBinding
import com.eunjeong.booklet.databinding.CalendarDayLayoutBinding
import com.eunjeong.booklet.detailSchedule.DayScheduleFragment
import com.eunjeong.booklet.detailSchedule.Detail
import com.eunjeong.booklet.detailSchedule.TimeTableFragment
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.navigation.NavigationView
import com.kizitonwose.calendar.core.*
import com.kizitonwose.calendar.view.MonthDayBinder
import com.kizitonwose.calendar.view.MonthHeaderFooterBinder
import com.kizitonwose.calendar.view.ViewContainer
import kotlinx.android.synthetic.main.activity_group_calendar.*
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.Month
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.*

class GroupCalendarActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener,
    Communicator {
    private lateinit var binding: ActivityGroupCalendarBinding
    private lateinit var tbinding: BottomSheetShowMembersBinding
    private val titleRes: Int? = null
    private val today = LocalDate.now()
    private var selectedDate: LocalDate? = null
    var checkDaySelected: Boolean = true
    lateinit var activityResultLauncher: ActivityResultLauncher<Intent>

    var preventRepetition = true
    var preventRepeat = true
    var p = 1

    var storeSelectedDate: LocalDate? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGroupCalendarBinding.inflate(layoutInflater)
        tbinding = BottomSheetShowMembersBinding.inflate(layoutInflater)
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

        // 화면 상단 그룹 정보
        val bottomSheetView = layoutInflater.inflate(R.layout.bottom_sheet_show_members, null)
        val bottomSheetDialog = BottomSheetDialog(this)
        bottomSheetDialog.setContentView(bottomSheetView)


        if (UserInformation.groupMemberUserIdArray.size == 1) {
            binding.showSingleMember.isVisible = true
            binding.profileImg.setImageResource(R.drawable.img)

            // 레이아웃 클릭 시 그룹 인원 bottomsheetdialog 호출
            binding.showSingleMember.setOnClickListener {
                tbinding.pf1.isVisible = true
                // tbinding.pfImg1.setImageResource() -> 사용자 프사 설정
                tbinding.pfName1.text = UserInformation.groupMemberUserIdArray[0]
                bottomSheetDialog.show()
            }
        }
        else if (UserInformation.groupMemberUserIdArray.size == 2) {
            binding.showTwoMembers.isVisible = true
            //binding.profileImg1.setImageResource(R.drawable.img)
            binding.profileImg2.setImageResource(R.drawable.ex_profile2)

            // 레이아웃 클릭 시 그룹 인원 bottomsheetdialog 호출
            binding.showTwoMembers.setOnClickListener {
                bottomSheetDialog.show()
            }
        }
        else if (UserInformation.groupMemberUserIdArray.size > 2) {
            binding.showMoreMembers.isVisible = true
            binding.moreMembersCntText.text = "${UserInformation.groupMemberUserIdArray.size}명"
            binding.profileImage1.setImageResource(R.drawable.ex_profile1)
            binding.profileImage2.setImageResource(R.drawable.ex_profile2)
            binding.profileImage3.setImageResource(R.drawable.img)

            // 레이아웃 클릭 시 그룹 인원 bottomsheetdialog 호출
            binding.showMoreMembers.setOnClickListener {
                bottomSheetDialog.show()
            }
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
                            mainGroupCalendarView.notifyDateChanged(currentSelection)
                        } else {
                            selectedDate = day.date
                            mainGroupCalendarView.notifyDateChanged(day.date)
                            if (currentSelection != null) {
                                mainGroupCalendarView.notifyDateChanged(currentSelection)
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
        binding.mainGroupCalendarView.apply {
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
        binding.mainGroupCalendarView.dayBinder = object : MonthDayBinder<DayViewContainer> {
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

//                    if (day.date == selectedDate) {
//                        dayView.setBackgroundResource(R.drawable.selected_border)
//                    } else if (day.date != selectedDate) { // 한 번 더 누르면 border 제거
//                        dayView.background = null
//                    }
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

                                        val sdayString = "%d-%02d-%02d".format(sy, sm, sd)
                                        val sdayLocalDate = LocalDate.parse(sdayString, DateTimeFormatter.ISO_DATE)
                                        val edayString = "%d-%02d-%02d".format(ey, em, ed)
                                        val edayLocalDate = LocalDate.parse(edayString, DateTimeFormatter.ISO_DATE)

                                        if (day.date == sdayLocalDate) {
                                            dayView.setBackgroundColor(resources.getColor(R.color.eventpink))
                                        }
                                        else if (day.date.isBefore(edayLocalDate) && day.date.isAfter(sdayLocalDate)) {
                                            dayView.setBackgroundColor(resources.getColor(R.color.eventpink))
                                        }
                                        else if (day.date == edayLocalDate) {
                                            dayView.setBackgroundColor(resources.getColor(R.color.eventpink))
                                        }
                                        else if (day.date == today.plusDays(14)) {
                                            dayView.setBackgroundColor(resources.getColor(R.color.eventpink))
                                        }
//

                                    }
                                } else {
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


                // UserInformation 클래스에 선언한 arraylist 형태의 그룹 인원들 일정들 달력에 추가
                for (member in UserInformation.groupMemberUserIdArray) {

                    if (preventRepeat && day.date != selectedDate) {
                        addUserPlanOnCalendar(member)
                    } else if (day.date == selectedDate) {
                        preventRepeat = false
                    }

                }


            }

        }


        // 달력 시작 요일 설정, 앱 실행 시 현재에 해당하는 월 보여주기
        val firstDayOfWeek = firstDayOfWeekFromLocale() // Available from the library
        binding.mainGroupCalendarView.setup(startMonth, endMonth, firstDayOfWeek)
        binding.mainGroupCalendarView.scrollToMonth(currentMonth)


        // titlesContainer (연, 월 표시)
        val titlesContainer = findViewById<ViewGroup>(R.id.yearMonthContainer)
        titlesContainer.children
            .map { it as TextView }
            .forEachIndexed { index, textView ->
                currentMonth.month.displayText(short = false)
            }


        // monthHeaderBinder (일 ~ 월 표시)
        binding.mainGroupCalendarView.monthHeaderBinder =
            object : MonthHeaderFooterBinder<MonthViewContainer> {
                override fun create(view: View) = MonthViewContainer(view)
                override fun bind(container: MonthViewContainer, data: CalendarMonth) {
                    if (container.titlesContianer.tag == null) {
                        container.titlesContianer.tag = data.yearMonth
                        container.titlesContianer.children.map { it as TextView }
                            .forEachIndexed { index, textView ->
                                val dayOfWeek = daysOfWeek[index]
                                val title =
                                    dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault())
                                textView.text = title
                            }
                    }
                }
            }


        // <- 화살표 버튼 누르면 화면 종료
        binding.btnGoBack.setOnClickListener {
            finish()
        }


        // xml < > 버튼으로 달력 스크롤하기
        binding.btnGoPreviousMonth.setOnClickListener {
            binding.mainGroupCalendarView.findFirstVisibleMonth()?.let {
                binding.mainGroupCalendarView.smoothScrollToMonth(it.yearMonth.previousMonth)
            }
        }

        binding.btnGoNextMonth.setOnClickListener {
            binding.mainGroupCalendarView.findFirstVisibleMonth()?.let {
                binding.mainGroupCalendarView.smoothScrollToMonth(it.yearMonth.nextMonth)
            }
        }



        }




    // 달력 옮길 때 상단 title(연, 월) 업데이트
    private fun updateTitle() {
        val month = binding.mainGroupCalendarView.findFirstVisibleMonth()?.yearMonth ?: return
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


    // Drawer content 내 메뉴 클릭 이벤트 처리하는 함수
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        TODO("Not yet implemented")
    }


    // 달력에서 날짜 누르면 DayScheduleFragment가 나오고,
    // DayScheduleFragment에서 버튼을 누르면 TimeTableFragment로 가기 위한 함수
    // interface : timetableFragment -> DayScheduleFragment
    override fun GoToTimeTableFragment(clickedDate: String, plan: ArrayList<Detail>) {
        val bundle = Bundle()
        bundle.putString("Date", clickedDate)
        bundle.putParcelableArrayList("Plan", plan)

        val transaction = this.supportFragmentManager.beginTransaction()
        val timeTableFragment = TimeTableFragment()
        timeTableFragment.arguments = bundle

        transaction.replace(binding.frameFragment.id, timeTableFragment)
        transaction.addToBackStack("timeTableFragment")
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
        transaction.addToBackStack("dayscheduleFragment")
        transaction.commit()
    }
}
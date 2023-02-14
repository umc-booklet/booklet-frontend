package com.eunjeong.booklet.detailSchedule

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.akribase.timelineview.Event
import com.eunjeong.booklet.AddScheduleActivity
import com.eunjeong.booklet.CalendarActivity
import com.eunjeong.booklet.Communicator
import com.eunjeong.booklet.databinding.FragmentTimeTableBinding
import kotlinx.android.synthetic.main.activity_calendar.*
import kotlinx.android.synthetic.main.calendar_day_layout.*
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.DayOfWeek
import java.time.LocalDate
import java.util.*


class TimeTableFragment : Fragment() {
    private lateinit var viewBinding: FragmentTimeTableBinding
    private lateinit var communicator: Communicator

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): ConstraintLayout {
        viewBinding = FragmentTimeTableBinding.inflate(layoutInflater)

        // DayScheduleFragment 에서 Interface 함수를 통해 보낸 데이터 (날짜만)
        var clickedDate = arguments?.getString("Date") // 클릭한 년-월-일
        var clickDay = clickedDate?.substring(clickedDate.length - 2)?.toInt() // 일
        var clickMonth = clickedDate?.substring(5, clickedDate.length - 3)?.toInt() // 월
        var clickYear = clickedDate?.substring(0, 4)?.toInt() // 년
        var dayName = getDayName(clickYear,clickMonth,clickDay)// 요일
        val checkedPlan = arguments?.getParcelableArrayList<Detail>("Plan") // ArrayList<Detail>
        Log.d("checkedPlan", checkedPlan.toString())

        viewBinding.dayTextView.text = clickDay.toString()
        viewBinding.dayNameTextView.text = dayName

        // timeLine
        if (checkedPlan != null) {
            for (i in checkedPlan) {
                var name = i.name
                var eventList: ArrayList<Event> = arrayListOf()

                var startDateString = dateConvert(i.startYear, i.startMonth, i.startDay, i.startHour, i.startMinute)
                var endDateString = dateConvert(i.endYear, i.endMonth, i.endDay, i.endHour, i.endMinute)

                Log.d("dateString confirm", "startDateString: " + startDateString + " endDateString: " + endDateString)

                val timeconversion = Timeconversion()
                var startDate = timeconversion.timeConversion(startDateString)
                var endDate = timeconversion.timeConversion(endDateString)
                Log.d("unixTime", "startDate : $startDate\nendDate : $endDate")

                // 만약, EndDate - StartDate > 24*60*60sec 이라면,
//                var midDateString = String()
//                var midDate: Long = 0
//                var midDateString2 = String()
//                var midDate2: Long = 0

//                if (endDate - startDate >= 24*60*60) {
//                    midDateString = dateConvert(i.startYear, i.startMonth, i.startDay, 11,0)
//                    midDateString2 = dateConvert(i.endYear, i.endMonth, i.endDay, 1,0)
//                    midDate = timeconversion.timeConversion(midDateString)
//                    midDate2 = timeconversion.timeConversion(midDateString2)
                    eventList.apply {
                        add(Event("$name", startDate, endDate))
//                        add(Event("$name", midDate2, endDate))
                    }
//                } else {
//                    eventList.apply {
//                        add(Event("$name", startDate, endDate))
//                    }
//                }

                viewBinding.timeLineView.timelineEvents = eventList
                viewBinding.timeLineView.setBackgroundColor(Color.parseColor(i.color + "00"))
                viewBinding.timeLineView.eventBg = Color.parseColor(i.color)
                }
            }



//        viewBinding.fixScheduleTv.text = 하루 종일 일정 이름 넣기

        // ImageButton 누르면 DaySchedule fragment 로 넘어 가기
//        viewBinding.goDayScheduleBtn.setOnClickListener{
//            communicator = activity as Communicator
//            communicator.GoToDayScheduleFragment(clickedDate.toString())
//        }

        // 위 기능이 자꾸 오류. 대체 방안 백 버튼
        viewBinding.closeBtn.setOnClickListener {
            activity?.supportFragmentManager?.popBackStack()
        }

        return viewBinding.root
    }

    fun dateConvert(year : Int, mon: Int, day: Int, hour: Int, min: Int ): String {
        var str = String()
        var month = String()
        var days = String()
        var hours = String()
        var mins = String()

        if (mon < 10) {
            month = "0$mon" // mm
        } else { month = mon.toString() }

        if (day < 10) {
            days = "0$day" // dd
        } else { days = day.toString() }

        if (hour < 10) {
            hours = "0$hour" // hh
        } else { hours = hour.toString() }

        if (min < 10) {
            mins = "0$min" // mm
        } else { mins = min.toString() }

        str = year.toString() + month + days + hours + mins  // yyyyMMddhhmm

        return str
    }

    fun getDayName(year: Int?, month: Int?, day: Int?): String {
        var dayName = String()

        if (year != null && month != null && day != null) {
            // 1. LocalDate 생성
            val date: LocalDate = LocalDate.of(year, month, day)
            // LocalDateTime date = LocalDateTime.of(2021, 12, 25, 1, 15, 20);
            //System.out.println(date) // // 2021-12-25

            // 2. DayOfWeek 객체 구하기
            val dayOfWeek: DayOfWeek = date.getDayOfWeek()

            // 3. 숫자 요일 구하기
            val dayOfWeekNumber: Int = dayOfWeek.getValue()

            // 4. 요일 반환

            if (dayOfWeekNumber == 1) {
                dayName = "월요일"
            } else if (dayOfWeekNumber == 2) {
                dayName = "화요일"
            } else if (dayOfWeekNumber == 3) {
                dayName = "수요일"
            } else if (dayOfWeekNumber == 4) {
                dayName = "목요일"
            } else if (dayOfWeekNumber == 5) {
                dayName = "금요일"
            } else if (dayOfWeekNumber == 6) {
                dayName = "토요일"
            } else if (dayOfWeekNumber == 7) {
                dayName = "일요일"
            }
        }
        return dayName
    }
}


// 유닉스 시간 변환기
class Timeconversion {
    private val dateFormat: DateFormat = SimpleDateFormat("yyyyMMddHHmm", Locale.KOREA) //Specify your locale
    fun timeConversion(time: String?): Long {
        var unixTime: Long = 0
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT+9:00")) //Specify your timezone
        try {
            unixTime = dateFormat.parse(time).getTime()
            unixTime = unixTime / 1000
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return unixTime
    }
}

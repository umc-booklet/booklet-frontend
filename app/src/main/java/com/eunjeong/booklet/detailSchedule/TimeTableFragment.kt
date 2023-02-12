package com.eunjeong.booklet.detailSchedule

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.akribase.timelineview.Event
import com.eunjeong.booklet.Communicator
import com.eunjeong.booklet.databinding.FragmentTimeTableBinding
import kotlinx.android.synthetic.main.activity_calendar.*
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
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
        val checkedPlan = arguments?.getParcelableArrayList<Detail>("Plan") // ArrayList<Detail>
        var clickDay = clickedDate?.substring(clickedDate.length - 2) // 일
        viewBinding.dayTextView.text = clickDay

        // timeLine
        if (checkedPlan != null) {
            for (i in checkedPlan) {
                var name = i.name
                var eventList: ArrayList<Event> = arrayListOf()

                var startDateString = i.startYear.toString() + i.startMonth.toString() + i.startDay.toString() + i.startHour.toString() + i.startMinute.toString()
                var endDateString = i.endYear.toString() + i.endMonth.toString() + i.endDay.toString() + i.endHour.toString() + i.endMinute.toString()

                val timeconversion = Timeconversion()
                var startDate = timeconversion.timeConversion(startDateString)
                var endDate = timeconversion.timeConversion(endDateString)
                Log.d("unixTime", "startDate : $startDate\nendDate : $endDate")

                eventList.apply{
                    add(Event("$name",startDate, endDate )) }
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

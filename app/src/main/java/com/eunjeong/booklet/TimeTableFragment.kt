package com.eunjeong.booklet

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.graphics.toColor
import androidx.core.graphics.toColorInt
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.akribase.timelineview.Event
import com.akribase.timelineview.TimelineView
import com.eunjeong.booklet.adapters.DayScheduleRVAdapter
import com.eunjeong.booklet.databinding.ActivityCalendarBinding.inflate
import com.eunjeong.booklet.databinding.FragmentTimeTableBinding
import kotlinx.android.synthetic.main.fragment_time_table.*
import kotlinx.android.synthetic.main.nav_header_setting.*
import java.lang.reflect.Array.get

class TimeTableFragment : Fragment() {
    private lateinit var viewBinding: FragmentTimeTableBinding
    private lateinit var communicator: Communicator

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): ConstraintLayout {
        viewBinding = FragmentTimeTableBinding.inflate(layoutInflater)


        // DayScheduleFragment에서 Interface 함수를 통해 보낸 데이터 (날짜만)
        var clickedDate = arguments?.getString("Date")
        viewBinding.dayTextView.text = clickedDate

        // timeLine
        // 반복문?
        var name = "이이이"
        var eventList: ArrayList<Event> = arrayListOf()
        eventList.apply{
            add(Event("${name}",1636949888, 1636959000 ))
        }
        viewBinding.timeLineView.timelineEvents = eventList
//        viewBinding.timeLineView.eventBg = "#FFFFFF".toColorInt() //색깔 구현???
 //       viewBinding.timeLineView.setBackgroundColor("#E6E6E6".toColorInt())

        //        viewBinding.fixScheduleTv.text = 하루종일인 일정 이름 넣기


        // ImageButton 누르면 DaySchedule fragment로 넘어가기
//        viewBinding.goDayScheduleBtn.setOnClickListener{
//            communicator = activity as Communicator
//            communicator.GoToDayScheduleFragment(clickedDate.toString())
//        }

        return viewBinding.root
    }




    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }


}

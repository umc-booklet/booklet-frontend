package com.eunjeong.booklet

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.ActionBar
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager.VERTICAL
import com.eunjeong.booklet.adapters.DayScheduleRVAdapter
import com.eunjeong.booklet.databinding.FragmentDayScheduleBinding
import com.eunjeong.booklet.datas.Schedule
import java.util.*
import kotlin.collections.ArrayList


class DayScheduleFragment : Fragment() {
    private lateinit var viewBinding: FragmentDayScheduleBinding
    private val scheduleList: ArrayList<Schedule> = arrayListOf()
    private val scheduleRVAdapter = DayScheduleRVAdapter(scheduleList)
    private lateinit var communicator: Communicator


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewBinding = FragmentDayScheduleBinding.inflate(layoutInflater)

        val decoration = DividerItemDecoration(activity, VERTICAL)
        viewBinding.rvSchedule.addItemDecoration(decoration)

        // CalenderActivity에서 넘겨준 현재 날짜 데이터 받기
        var clickDate = arguments?.getString("Date")
        clickDate = clickDate?.substring(clickDate.length - 2) // 날짜만 빼기
        viewBinding.dayTextView.text = clickDate

        scheduleList.apply{
            add(Schedule("여행", "${clickDate}", "#F5E5FF", 0))
            add(Schedule("놀기", "2월7일 - 2월9일", "#F5E5FF", 0))
            add(Schedule("놀기2", "2월7일 - 2월9일", "#DDFFE4",1))
            add(Schedule("놀기3", "2월7일 - 2월9일", "#DDFFE4",1))
            add(Schedule("놀기4", "오전 8:00 - 오후 9:00", "#FFE4EE",2))
            add(Schedule("놀기5", "오전 8:00 - 오후 9:00", "#FFE4EE",2))
            add(Schedule("놀기6", "오전 8:00 - 오후 9:00", "#FFE4EE", 2))
        }

        scheduleRVAdapter.notifyDataSetChanged()

        viewBinding.rvSchedule.adapter = scheduleRVAdapter
        viewBinding.rvSchedule.layoutManager = LinearLayoutManager(activity)


        // ImageButton 누르면 TimeTable fragment로 넘어가기
        viewBinding.goTimeTableBtn.setOnClickListener{
            communicator = activity as Communicator

            communicator.GoToTimeTableFragment(clickDate.toString()) //??


        }



        return viewBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

    }


}




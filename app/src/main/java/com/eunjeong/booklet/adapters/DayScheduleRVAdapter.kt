package com.eunjeong.booklet.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.eunjeong.booklet.databinding.DayScheduleItemBinding
import com.eunjeong.booklet.datas.Schedule



class DayScheduleRVAdapter(var scheduleList: ArrayList<Schedule>):
RecyclerView.Adapter<DayScheduleRVAdapter.ScheduleViewHolder>(){
    lateinit var binding: DayScheduleItemBinding

    inner class ScheduleViewHolder(private val viewBinding: DayScheduleItemBinding): RecyclerView.ViewHolder(viewBinding.root){
        fun bind(schedule: Schedule){
            viewBinding.dayScheduleTitleTextView.text = schedule.Title  // 일정 제목
            viewBinding.dayScheduleDateTextView.text = schedule.Date  // 일정 시간 또는 날짜
            var temp_color = schedule.Color // 막대 색
            viewBinding.dayScheduleRectangle.setColorFilter(Color.parseColor(temp_color)) // 막대 색 바꾸기

            // 막대 색에 따라 글자 색도 바꾸기
            if (temp_color == "#F5E5FF"){
                viewBinding.dayScheduleTitleTextView.setTextColor(Color.parseColor("#B708F4"))
            } else if(temp_color == "#DDFFE4"){
                viewBinding.dayScheduleTitleTextView.setTextColor(Color.parseColor("#277E35"))
            } else{
                viewBinding.dayScheduleTitleTextView.setTextColor(Color.parseColor("#F22C2C"))
            }

            // 맨 왼쪽 아이템
            if (schedule.currentStatus == 0){ // 달력 Image 뜨게
                viewBinding.dayCalenderImage.isVisible = true
                viewBinding.dayScheduleTimeTextView.isVisible = false
            } else if (schedule.currentStatus == 1){ // 하루종일
                viewBinding.dayCalenderImage.isVisible = false
                viewBinding.dayScheduleTimeTextView.isVisible = false
            } else{ // 시간 지정
                viewBinding.dayCalenderImage.isVisible = false
                viewBinding.dayScheduleTimeTextView.isVisible = true
                var temp_string = schedule.Date
                temp_string = temp_string.substring(3 until 7)
                viewBinding.dayScheduleTimeTextView.text = temp_string
                }
            }
        }

    // ViewHolder 만들어질 때 실행할 동작
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScheduleViewHolder {
        val viewBinding = DayScheduleItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ScheduleViewHolder(viewBinding)
    }

    // VIewHolder가 실제로 데이터를 표시해야 할 떄 호출되는 함수
    override fun onBindViewHolder(holder: ScheduleViewHolder, position: Int) {
        holder.bind(scheduleList[position])
    }

    override fun getItemCount(): Int{
        return scheduleList.size
    }


}


package com.eunjeong.booklet.adapters

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getColor
import androidx.core.content.res.ResourcesCompat.getColor
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.load.engine.Resource
import com.eunjeong.booklet.R
import com.eunjeong.booklet.databinding.DayScheduleItemBinding
import com.eunjeong.booklet.detailSchedule.Detail
import com.google.android.material.color.MaterialColors.getColor


class DayScheduleRVAdapter(private var detailList: ArrayList<Detail>):
RecyclerView.Adapter<DayScheduleRVAdapter.ScheduleViewHolder>() {

    inner class ScheduleViewHolder(private val viewBinding: DayScheduleItemBinding) :
        RecyclerView.ViewHolder(viewBinding.root) {
        fun bind(detail: Detail) {

            // 시간, 제목 데이터
            viewBinding.tvDayScheduleTitle.text = detail.text  // 일정 제목
            viewBinding.tvStartDay.text = detail.startDay.toString() // 일정 시작 일
            viewBinding.tvStartHour.text = detail.startHour.toString() // 일정 시작 시
            viewBinding.tvStartMinute.text = detail.startMinute.toString() // 일정 시작 분
            viewBinding.tvEndDay.text = detail.endDay.toString() // 일정 마침 일
            viewBinding.tvEndHour.text = detail.endHour.toString() // 일정 마침 시
            viewBinding.tvEndMinute.text = detail.endMinute.toString() // 일정 마침 분

            // 막대 색
            var tempColor = detail.color
            viewBinding.dayScheduleRectangle.setColorFilter(R.color.eventpink) // 막대 색 바꾸기
            viewBinding.tvDayScheduleTitle.setTextColor(R.color.eventpinktext) // 막대 색에 따라 글자 색도 바꾸기

            // 오전 / 오후 설정
            if (detail.startHour <= 12) { viewBinding.tvBar1.text = "오전 "
            } else { viewBinding.tvBar1.text = "오후 " }

            if (detail.endHour <= 12) { viewBinding.tvBar5.text = "오전 "
            } else { viewBinding.tvBar5.text = "오후 " }

            // 맨 왼쪽 이미지뷰
//            if (schedule.currentStatus == 0){ // 달력 Image 뜨게
//                viewBinding.dayCalenderImage.isVisible = true
//                viewBinding.dayScheduleTimeTextView.isVisible = false
//            } else if (schedule.currentStatus == 1){ // 하루종일
//                viewBinding.dayCalenderImage.isVisible = false
//                viewBinding.dayScheduleTimeTextView.isVisible = false
//            } else{ // 시간 지정
//                viewBinding.dayCalenderImage.isVisible = false
//                viewBinding.dayScheduleTimeTextView.isVisible = true
//                var temp_string = schedule.Date
//                temp_string = temp_string.substring(3 until 7)
//                viewBinding.dayScheduleTimeTextView.text = temp_string
//                }
//            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScheduleViewHolder {
        val viewBinding = DayScheduleItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ScheduleViewHolder(viewBinding)
    }

    override fun onBindViewHolder(holder: ScheduleViewHolder, position: Int) {
        holder.bind(detailList[position])
    }

    override fun getItemCount(): Int = detailList.size
}

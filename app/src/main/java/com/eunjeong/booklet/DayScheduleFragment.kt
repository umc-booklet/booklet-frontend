package com.eunjeong.booklet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.eunjeong.booklet.databinding.FragmentDayScheduleBinding

class DayScheduleFragment : Fragment() {
    private lateinit var viewBinding: FragmentDayScheduleBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_day_schedule, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        // 여기서 Fragment 작업
        viewBinding.TextViewwww.text = "진짜로 Fragment 열렸음~~"
    }
}
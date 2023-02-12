package com.eunjeong.booklet.detailSchedule

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager.VERTICAL
import com.eunjeong.booklet.Communicator
import com.eunjeong.booklet.adapters.DayScheduleRVAdapter
import com.eunjeong.booklet.databinding.FragmentDayScheduleBinding
import com.eunjeong.booklet.monthSchedule.monthScheduleResponse
import com.eunjeong.booklet.monthSchedule.monthScheduleService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.time.DayOfWeek
import java.time.LocalDate
import java.util.*


class DayScheduleFragment : Fragment() {
    private lateinit var viewBinding: FragmentDayScheduleBinding
    private lateinit var communicator: Communicator


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewBinding = FragmentDayScheduleBinding.inflate(layoutInflater)

        // 아이템 구분선
        val decoration = DividerItemDecoration(activity, VERTICAL)
        viewBinding.rvSchedule.addItemDecoration(decoration)

        // client 객체
        val clientBuilder = OkHttpClient.Builder()
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        clientBuilder.addInterceptor(loggingInterceptor)
        clientBuilder.retryOnConnectionFailure(true)

        // retrofit 객체
        val retrofit = Retrofit.Builder()
            .baseUrl("http://3.35.217.34:8080")
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .client(clientBuilder.build()) // client 등록
            .build()

        // retrofit 객체에 Interface 연결
        val detailCheck = retrofit.create(DetailScheduleService::class.java)
        val monthCheck = retrofit.create(monthScheduleService::class.java)

        // CalenderActivity 에서 넘겨준 현재 날짜 데이터 받기
        var clickDate = arguments?.getString("Date") // 클릭한 2023-02-12
        var id = arguments?.getLong("Id")?.toInt() // userId
        var clickDay = clickDate?.substring(clickDate.length - 2)?.toInt() // 일
        var clickMonth = clickDate?.substring(5, clickDate.length - 3)?.toInt() // 월
        var clickYear = clickDate?.substring(0, 3)?.toInt() // 년
        var dayName = getDayName(clickYear,clickMonth,clickDay)// 요일

        Log.d("date confirm", "year: " + clickYear + "day: " + clickDay + "month: " + clickMonth)

        viewBinding.dayTextView.text = clickDay.toString()
        viewBinding.dayNameTextView.text = dayName
        // 하루에 여러 planId 가능
        // userId, Month 이용해 해당 월 planId 전부 조회
        // 그 중 clickDay 와 startDay 또는 endDay 가 같은 planId 만 추출
        // for 문 이용해 조회한 planId 반복해 세부 일정 조회
        // {planId} 로 세부 일정 조회 기능

        val planId = arrayListOf<Int>()
        val detailPlan = arrayListOf<Detail>()
        Log.d("planId1", planId.toString()) //*********
        if (id != null && clickMonth != null && clickDay != null) {
            monthCheck.monthCheck(id, clickMonth).enqueue(object: Callback<monthScheduleResponse>{
                override fun onResponse(call: Call<monthScheduleResponse>, response: Response<monthScheduleResponse>) {
                    val responseData = response.body()

                    if (responseData != null) {
                        if (responseData.result.isEmpty()) {
                            viewBinding.emptyTv.isVisible = true
                        } else {
                            for (i in responseData.result) {
                                viewBinding.emptyTv.isVisible = false
                                if (i.startDay == clickDay || clickDay == i.endDay) {
                                    planId.add(i.id)

                                    for (j in planId) {
                                        detailCheck.detailCheck(j).enqueue(object: Callback<DetailScheduleResponse> {
                                            override fun onResponse(call: Call<DetailScheduleResponse>, response: Response<DetailScheduleResponse>) {
                                                if (response.isSuccessful) {
                                                    val responseData2 = response.body()
                                                    if (responseData2 != null) {
                                                        Log.d("Retrofit","ResponseCode: ${responseData2.code} Message: ${responseData2.message}")
                                                        detailPlan.add(responseData2.result)
                                                        setAdapter(detailPlan) // Adapter 연결
                                                        Log.d("detailPlan2", detailPlan.toString())
                                                    }
                                                } else {
                                                    Log.w("Retrofit", "Response Not Successful ${response.code()}")
                                                }
                                            }
                                            override fun onFailure(call: Call<DetailScheduleResponse>, t: Throwable) {
                                                Log.e("Retrofit", "Error!", t)
                                                Toast.makeText(activity, "서버 통신 오류", Toast.LENGTH_SHORT)
                                            }
                                        })
                                    }
                                }
                            }
                        }
                    } else {
                        Log.w("Retrofit", "Response Not Successful ${response.code()}")
                    }
                }
                override fun onFailure(call: Call<monthScheduleResponse>, t: Throwable) {
                    Log.e("Retrofit", "Error!", t)
                    Toast.makeText(activity, "서버 통신 오류", Toast.LENGTH_SHORT)
                }
            })
        }

        // ImageButton 누르면 TimeTable fragment 로 넘어가기
        viewBinding.goTimeTableBtn.setOnClickListener{
            communicator = activity as Communicator
            communicator.GoToTimeTableFragment(clickDate.toString(), detailPlan)
        }
        return viewBinding.root
    } // ** end of onCreateView

    private fun setAdapter(list : ArrayList<Detail>){

        val mAdapter = DayScheduleRVAdapter(list)
        viewBinding.rvSchedule.adapter = mAdapter
        viewBinding.rvSchedule.layoutManager = LinearLayoutManager(activity)
    }


    fun getDayName(year: Int?, month: Int?, day: Int?): String {

        // 1. LocalDate 생성
        val date: LocalDate = LocalDate.of(2021, 12, 25)
        // LocalDateTime date = LocalDateTime.of(2021, 12, 25, 1, 15, 20);
        //System.out.println(date) // // 2021-12-25

        // 2. DayOfWeek 객체 구하기
        val dayOfWeek: DayOfWeek = date.getDayOfWeek()

        // 3. 숫자 요일 구하기
        val dayOfWeekNumber: Int = dayOfWeek.getValue()

        // 4. 요일 반환
        var dayName = String()
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
            dayName= "토요일"
        } else if (dayOfWeekNumber == 7) {
            dayName = "일요일"
        }

        return dayName
    }
}




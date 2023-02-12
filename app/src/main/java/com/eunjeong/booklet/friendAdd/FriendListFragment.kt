package com.eunjeong.booklet.friendAdd

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.DividerItemDecoration.VERTICAL
import androidx.recyclerview.widget.LinearLayoutManager
import com.eunjeong.booklet.adapters.FriendAddListRVAdapter
import com.eunjeong.booklet.databinding.FragmentFriendListBinding
import com.eunjeong.booklet.friendSearch.FriendSearchService
import com.eunjeong.booklet.memberInfo.Info
import okhttp3.OkHttpClient
//import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory

class FriendListFragment(id: Long) : Fragment() {
    private lateinit var viewBinding: FragmentFriendListBinding
    private val idr = id

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewBinding = FragmentFriendListBinding.inflate(layoutInflater)
        viewBinding.emptyView.isVisible = true

        // Item 구분선
        val decoration = DividerItemDecoration(activity, VERTICAL)
        viewBinding.rvData.addItemDecoration(decoration)

        // client 객체
//        val clientBuilder = OkHttpClient.Builder()
//        val loggingInterceptor = HttpLoggingInterceptor()
//        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
//        clientBuilder.addInterceptor(loggingInterceptor)

        // retrofit 객체
        val retrofit = Retrofit.Builder()
            .baseUrl("http://3.35.217.34:8080")
            .addConverterFactory(GsonConverterFactory.create())
           // .client(clientBuilder.build()) // client 등록
            .build()

        // retrofit 객체에 Interface 연결
        val searchService = retrofit.create(FriendSearchService::class.java)

        // {userId} 로 친구 검색
        viewBinding.searchBar.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
            androidx.appcompat.widget.SearchView.OnQueryTextListener{
            //검색 버튼 입력시 호출
            override fun onQueryTextSubmit(query: String): Boolean {
                searchService.search(query).enqueue(object : Callback<List<Info>>{
                    override fun onResponse(call: Call<List<Info>>, response: Response<List<Info>>) {
                        if (response.isSuccessful){
                            val responseData = response.body()
                            var searchList = ArrayList<Info>() // 새로 초기화
                            if (responseData != null){
                                for (i in responseData ) {
                                    searchList.add(i)

                                    if (searchList.size == 0) {
                                        viewBinding.emptyView.isVisible = true
                                    } else {
                                        viewBinding.emptyView.isVisible = false
                                        setAdapter(searchList)

                                    }
                                }
                            } else {
                                viewBinding.emptyView.isVisible = true
                            }
                        } else {
                            Log.w("Retrofit", "Response Not Successful ${response.code()}")
                        }
                    }

                    override fun onFailure(call: Call<List<Info>>, t: Throwable) {
                        Log.e("Retrofit", "Error!", t)
                        Toast.makeText(activity, "서버 통신 오류", Toast.LENGTH_SHORT)
                    }
                })
                return false
            }
            //텍스트 입력/수정시 호출
            override fun onQueryTextChange(newText: String): Boolean {
                return false
            }
        })
        return viewBinding.root
    }

    private fun setAdapter(list : ArrayList<Info>){

        Log.d("In FriendListFrag", idr.toString())
        val mAdapter = FriendAddListRVAdapter(list, idr)
        viewBinding.rvData.adapter = mAdapter
        viewBinding.rvData.layoutManager = LinearLayoutManager(activity)

        // 아이템 전체 클릭시 상세 정보
        mAdapter.setOnItemClickListener(object : FriendAddListRVAdapter.OnItemClickListener {
            override fun onItemClick(v: View, info: Info, pos: Int) {
                val detailInfoFragment = FriendDetailFragment(info)
                detailInfoFragment.show(parentFragmentManager, "TAG")
            }
        })


    }
}
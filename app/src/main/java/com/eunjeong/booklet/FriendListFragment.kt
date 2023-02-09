package com.eunjeong.booklet

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.Toast
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.DividerItemDecoration.VERTICAL
import androidx.recyclerview.widget.LinearLayoutManager
import com.eunjeong.booklet.friendListCheck.FriendListCheckService
import com.eunjeong.booklet.adapters.FriendAddListRVAdapter
import com.eunjeong.booklet.databinding.FragmentFriendListBinding
import com.eunjeong.booklet.friendListCheck.FriendListCheckResponse
import com.eunjeong.booklet.memberInfo.Info
import com.eunjeong.booklet.memberInfo.MemberInfoResponse
import com.eunjeong.booklet.memberInfo.MemberInfoService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory

// LoginActivity (userId) = String i
// Int i
class FriendListFragment(i: String) : Fragment() {
    private lateinit var viewBinding: FragmentFriendListBinding
    private val friendlist: ArrayList<Info> = arrayListOf()
    private val i = i.toInt()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewBinding = FragmentFriendListBinding.inflate(layoutInflater)


        // Item 구분선
        val decoration = DividerItemDecoration(activity, VERTICAL)
        viewBinding.rvData.addItemDecoration(decoration)

        // client 객체
        val clientBuilder = OkHttpClient.Builder()
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        clientBuilder.addInterceptor(loggingInterceptor)

        // retrofit 객체
        val retrofit = Retrofit.Builder()
            .baseUrl("http://3.35.217.34:8080")
            .addConverterFactory(GsonConverterFactory.create())
            .client(clientBuilder.build()) // client 등록
            .build()

        // retrofit 객체에 Interface 연결
        val friendListCheckService = retrofit.create(FriendListCheckService::class.java)
        val memberInfoService = retrofit.create(MemberInfoService::class.java)

        friendListCheckService.checkFriendList(i).enqueue(object: Callback<FriendListCheckResponse>{
            override fun onResponse(call: Call<FriendListCheckResponse>, response: Response<FriendListCheckResponse>) {
                if (response.isSuccessful){
                    val responseData = response.body()
                    if (responseData != null) {
                        Log.d("Retrofit","Response\nCode: ${responseData.code} Message: ${responseData.message}")

                        for ( i in responseData.result ) {
                            memberInfoService.getCheck(i.friendId).enqueue(object: Callback<MemberInfoResponse> {
                                override fun onResponse(call: Call<MemberInfoResponse>, response: Response<MemberInfoResponse>) {
                                    if (response.isSuccessful) {
                                        val responseData2 = response.body()
                                        if (responseData2 != null) {
                                            Log.d("Retrofit", "Response\nCode: ${responseData2.code} Message: ${responseData2.message}")
                                            friendlist.add(responseData2.result)
                                            Log.d("array1", friendlist.toString())
                                            setAdapter(friendlist)
                                            Log.d("array2", friendlist.toString())
                                        }
                                    } else {
                                        Log.w("Retrofit", "Response Not Successful ${response.code()}")
                                    }
                                }

                                override fun onFailure(call: Call<MemberInfoResponse>, t: Throwable) {
                                    Log.e("Retrofit", "Error!", t)
                                    Toast.makeText(activity, "서버와 통신에 실패했습니다.", Toast.LENGTH_SHORT)
                                }
                            })
                        }
                    }
                } else {
                    Log.w("Retrofit", "Response Not Successful ${response.code()}")
                }
            }

            override fun onFailure(call: Call<FriendListCheckResponse>, t: Throwable) {
                Log.e("Retrofit","Error!",t)
                Toast.makeText(activity, "서버와 통신에 실패했습니다.", Toast.LENGTH_SHORT)
            }
        })
        return viewBinding.root
    } // onCreateView 끝

    private fun setAdapter(list : ArrayList<Info>){
        val mAdapter = FriendAddListRVAdapter(list)
        Log.d("array3", friendlist.toString())
        Log.d("item", mAdapter.itemCount.toString())
        viewBinding.rvData.adapter = mAdapter
        viewBinding.rvData.layoutManager = LinearLayoutManager(activity)


        // 아이템 전체 클릭시 상세 정보
        mAdapter.setOnItemClickListener(object : FriendAddListRVAdapter.OnItemClickListener {
            override fun onItemClick(v: View, info: Info, pos: Int) {
                val detailInfoFragment = FriendDetailFragment(info)
                detailInfoFragment.show(parentFragmentManager, "TAG")
            }
        })

        // 검색창
        viewBinding.searchBar.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
            androidx.appcompat.widget.SearchView.OnQueryTextListener {

            val searchView = viewBinding.searchBar

            //검색버튼 입력시 호출
            override fun onQueryTextSubmit(s: String): Boolean {
                return false
            }

            //텍스트 입력/수정시에 호출
            override fun onQueryTextChange(s: String): Boolean {
                filter(s, mAdapter)
                return false
            }
        })
    }

    // Search View 필터 함수
    private fun filter(text: String, adapter : FriendAddListRVAdapter) {
        val filteredlist: ArrayList<Info> = ArrayList()
        for (item in friendlist) {
            if (item.name.contains(text.toLowerCase())) {
                filteredlist.add(item)
            } else if (item in filteredlist) {
                filteredlist.remove(item)
            }
        }
        if (filteredlist.isEmpty()) {
            adapter.filterList(filteredlist)
            Toast.makeText(activity, "No Data Found..", Toast.LENGTH_SHORT).show()
        } else {
            adapter.filterList(filteredlist)
        }
    }
}
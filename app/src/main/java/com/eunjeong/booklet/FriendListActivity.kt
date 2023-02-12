package com.eunjeong.booklet

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.SearchView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.eunjeong.booklet.adapters.FriendListAdapter
import com.eunjeong.booklet.databinding.ActivityFriendListBinding
import com.eunjeong.booklet.datas.Friend
import com.eunjeong.booklet.friendListCheck.FriendListCheckResponse
import com.eunjeong.booklet.friendListCheck.FriendListCheckService
import com.eunjeong.booklet.memberInfo.MemberInfoResponse
import com.eunjeong.booklet.memberInfo.MemberInfoService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.eunjeong.booklet.login.UserData
import kotlin.collections.ArrayList

class FriendListActivity : AppCompatActivity() {
    private lateinit var viewBinding: ActivityFriendListBinding
    private lateinit var userInfo: UserData
    lateinit var friendList: ArrayList<Friend>
    lateinit var friendRVAdapter: FriendListAdapter
    lateinit var friendRV: RecyclerView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityFriendListBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        if (intent.hasExtra("UserInfo")) {
            userInfo = intent.getParcelableExtra<UserData>("UserInfo")!!
            Log.d("데이터 전달 성공 in FriendListActivity", userInfo?.name.toString() + userInfo?.userId.toString() + userInfo?.img.toString() + userInfo.id.toString())
        }

        viewBinding.friendAddBtn.setOnClickListener {
            val intent = Intent(this, FriendAddActivity::class.java)
            intent.putExtra("UserInfo", userInfo)
            startActivity(intent)
        }

        friendRV = findViewById(R.id.friendRecyclerView)
        friendList = ArrayList()
        friendRVAdapter = FriendListAdapter(friendList)
        friendRV.adapter = friendRVAdapter


        val retrofit = Retrofit.Builder()
            .baseUrl("http://3.35.217.34:8080/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiService = retrofit.create(FriendListCheckService::class.java)
        val memberinfoService = retrofit.create(MemberInfoService::class.java)
        
        apiService.checkFriendList(22). enqueue(object: Callback<FriendListCheckResponse>{
            override fun onResponse(call: Call<FriendListCheckResponse>, response: retrofit2.Response<FriendListCheckResponse>
            ) {
                if(response.isSuccessful){
                    val responseData = response.body()
                    if(responseData != null){

                        for(i in responseData.result){
                            Log.d("Retrofit i", "${i.friendId}")
                            Log.d("Retrofit", "서버 연결 성공")
                            Log.d("Retrofit","Response\nCode: ${responseData.code} Message: ${responseData.message}")

                            val temp = i.friendId.toLong()
                            memberinfoService.getCheck(temp).enqueue(object: Callback<MemberInfoResponse>{
                                override fun onResponse(call: Call<MemberInfoResponse>,response: retrofit2.Response<MemberInfoResponse>
                                ) {
                                    if(response.isSuccessful){
                                        val responseData2 = response.body()
                                        if(responseData2 != null){
                                            Log.d("Retrofit", "Response\nCode: ${responseData2.code} Message: ${responseData2.message}")
                                            for(j in responseData2.result) {
                                                friendList.add(Friend(R.drawable.ex_profile1, j.name, j.userId)) // 첫번쨰 사진, 두번째 이름, 세번째 id 넣어야함
                                                Log.d("retrofit array", friendList.toString())
                                            }
                                        }
                                    }else {
                                        Log.w(
                                            "Retrofit",
                                            "Response Not Successful ${response.code()}"
                                        )
                                    }
                                }
                                override fun onFailure(call: Call<MemberInfoResponse>, t: Throwable) {
                                    Log.e("Retrofit", "Error!", t)
                                    Toast.makeText(this@FriendListActivity, "서버와 통신에 실패했습니다.", Toast.LENGTH_SHORT)
                                }

                            })
                        }
                    }
                    else {
                        Log.w("Retrofit", "Response Not Successful ${response.code()}")
                    }
                }

            }

            override fun onFailure(call: Call<FriendListCheckResponse>, t: Throwable) {
                Log.e("Retrofit Error", "서버 연결 실패", t)
            }

        })
//        friendList.add(Friend(R.drawable.ex_profile1, "이은정", "dkan9634"))
//        friendList.add(Friend(R.drawable.ex_profile2, "이은딩", "eunding"))
//        friendList.add(Friend(R.drawable.ex_profile1, "김니나", "ninanina"))
//        friendList.add(Friend(R.drawable.ex_profile1, "김현나", "hyunna"))
//        friendList.add(Friend(R.drawable.ex_profile2, "박최이", "choilee"))
//        friendList.add(Friend(R.drawable.ex_profile2, "박이최", "leechoi"))
//        friendList.add(Friend(R.drawable.ex_profile2, "방은솔", "eunsol"))
//        friendList.add(Friend(R.drawable.ex_profile1, "장우욱", "wuwuk"))
//        friendList.add(Friend(R.drawable.ex_profile1, "진부연", "buyeon"))
//        friendList.add(Friend(R.drawable.ex_profile1, "진진진", "jinjin"))
//        friendList.add(Friend(R.drawable.ex_profile2, "미미미", "meme"))
//        friendList.add(Friend(R.drawable.ex_profile2, "쿠쿠쿠", "cucucu"))
//        friendList.add(Friend(R.drawable.ex_profile2, "키키키", "kikiki"))
        friendList.add(Friend(R.drawable.ex_profile1, "콕콕콕", "cokcok"))

        friendRVAdapter.notifyDataSetChanged()

        viewBinding.friendRecyclerView.adapter = friendRVAdapter
        viewBinding.friendRecyclerView.layoutManager = LinearLayoutManager(this)

        viewBinding.friendListSearchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
            androidx.appcompat.widget.SearchView.OnQueryTextListener {

            val searchView = viewBinding.friendListSearchView

            // 검색 버튼 누를 때 호출
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }
            // 검색창에서 글자가 변경이 일어날 때마다 호출 (실시간)
            override fun onQueryTextChange(newText: String): Boolean {
                filter(newText)
                return false
            }
        })

        // 이미지 버튼 - 친구 추가 액티비티 연결
        viewBinding.addfriend.setOnClickListener {
            val intent = Intent(this, FriendAddActivity::class.java)
            startActivity(intent)
        }
    }

    private fun filter(text: String) {
        val filteredlist: ArrayList<Friend> = ArrayList()
        for(item in friendList){
            if(item.id.toLowerCase().contains(text.toLowerCase())){
                filteredlist.add(item)
            }
            else if(item in filteredlist){
                filteredlist.remove(item)
            }
        }
        if (filteredlist.isEmpty()) {
            friendRVAdapter.filterList(filteredlist)
            Toast.makeText(this, "No Data Found..", Toast.LENGTH_SHORT).show()
        } else {
            friendRVAdapter.filterList(filteredlist)
        }
    }





}


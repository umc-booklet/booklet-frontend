package com.eunjeong.booklet

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.CheckBox
import android.widget.CompoundButton
import android.widget.SearchView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.eunjeong.booklet.adapters.GroupChoiceAdapter
import com.eunjeong.booklet.databinding.ActivityGroupChoiceBinding
import com.eunjeong.booklet.datas.Friend
import com.eunjeong.booklet.datas.GroupFriend
import kotlinx.android.synthetic.main.activity_group_choice.*
import kotlinx.android.synthetic.main.activity_group_choice.view.*
import kotlinx.android.synthetic.main.group_choice_item.*
import kotlin.math.log

class GroupChoiceActivity : AppCompatActivity() {
    private lateinit var viewBinding: ActivityGroupChoiceBinding

    lateinit var friendList: ArrayList<GroupFriend>
    lateinit var friendRVAdapter: GroupChoiceAdapter
    lateinit var friendRV: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityGroupChoiceBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        friendRV = findViewById(R.id.friendRecyclerView)
        friendList = ArrayList()
        friendRVAdapter = GroupChoiceAdapter(friendList)
        friendRV.adapter = friendRVAdapter

        friendRVAdapter.setItem(friendList) // 지울까

        // 데이터
        friendList.add(GroupFriend(false, R.drawable.ex_profile1, "이은정", "dkan9634"))
        friendList.add(GroupFriend(false, R.drawable.ex_profile2, "이은딩", "eunding"))
        friendList.add(GroupFriend(false, R.drawable.ex_profile1, "김니나", "ninanina"))
        friendList.add(GroupFriend(false, R.drawable.ex_profile1, "김현나", "hyunna"))
        friendList.add(GroupFriend(false, R.drawable.ex_profile2, "박최이", "choilee"))
        friendList.add(GroupFriend(false, R.drawable.ex_profile2, "박이최", "leechoi"))
        friendList.add(GroupFriend(false, R.drawable.ex_profile2, "방은솔", "eunsol"))
        friendList.add(GroupFriend(false, R.drawable.ex_profile1, "장우욱", "wuwuk"))
        friendList.add(GroupFriend(false, R.drawable.ex_profile1, "진부연", "buyeon"))
        friendList.add(GroupFriend(false, R.drawable.ex_profile1, "진진진", "jinjin"))
        friendList.add(GroupFriend(false, R.drawable.ex_profile2, "미미미", "meme"))
        friendList.add(GroupFriend(false, R.drawable.ex_profile2, "쿠쿠쿠", "cucucu"))
        friendList.add(GroupFriend(false, R.drawable.ex_profile2, "키키키", "kikiki"))
        friendList.add(GroupFriend(false, R.drawable.ex_profile1, "콕콕콕", "cokcok"))

        friendRVAdapter.notifyDataSetChanged()

        viewBinding.friendRecyclerView.adapter = friendRVAdapter
        viewBinding.friendRecyclerView.layoutManager = LinearLayoutManager(this)

        // SearchView
        viewBinding.friendListSearchView.setOnQueryTextListener(object :
            SearchView.OnQueryTextListener,
            androidx.appcompat.widget.SearchView.OnQueryTextListener {
            val searchView = viewBinding.friendRecyclerView
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

        // 확인버튼
        viewBinding.okBtn.setOnClickListener {
            val groupList: ArrayList<GroupFriend> = ArrayList()
            for (item in friendList) {
                if(item.checked){
                    groupList.add(item)
                }
                else{
                    if(item in groupList){
                        groupList.remove(item)
                    }
                }
            }
            if (groupList.size > 10){
                Toast.makeText(this, "그룹 추가는 최대 10명까지 가능합니다.", Toast.LENGTH_SHORT).show()
            }
            else{ // 최대 10명 선택했으면 제대로 넘어가는 코드 작성
                //Toast.makeText(this, "${groupList.size}", Toast.LENGTH_SHORT).show()
                // 영상 촬영 위해 추가
                val intent = Intent(this, GroupCalendarActivity::class.java)
                startActivity(intent)
            }
        }
    }

    // Search View 필터 함수
    private fun filter(text: String) {
        val filteredlist: ArrayList<GroupFriend> = ArrayList()
        for (item in friendList) {
            if (item.id.toLowerCase().contains(text.toLowerCase())) {
                filteredlist.add(item)
            } else if (item in filteredlist) {
                filteredlist.remove(item)
            }
        }
        if (filteredlist.isEmpty()) {
            friendRVAdapter.filterList(filteredlist)
            Toast.makeText(this, "No Data Found..", Toast.LENGTH_SHORT).show()
        } else {
            friendRVAdapter.filterList(filteredlist)
            friendRVAdapter.notifyDataSetChanged()
        }
    }

}


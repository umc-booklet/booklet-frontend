package com.eunjeong.booklet

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.Toast
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.DividerItemDecoration.VERTICAL
import androidx.recyclerview.widget.LinearLayoutManager
import com.eunjeong.booklet.adapters.FriendAddListRVAdapter
import com.eunjeong.booklet.databinding.FragmentFriendListBinding
import com.eunjeong.booklet.datas.Friend

class FriendListFragment : Fragment() {
    private lateinit var viewBinding: FragmentFriendListBinding
    private val friendlist: ArrayList<Friend> = arrayListOf()
    private val friendaddlistRVAdapter = FriendAddListRVAdapter(friendlist)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewBinding = FragmentFriendListBinding.inflate(layoutInflater)

        val decoration = DividerItemDecoration(activity, VERTICAL)
        viewBinding.rvData.addItemDecoration(decoration)

        friendlist.apply {
            add(Friend(R.drawable.dog, "진부연", "AAAAAAAA"))
            add(Friend(R.drawable.dog, "진초연", "BBBBBBBB"))
            add(Friend(R.drawable.dog, "장욱", "CCCCCCCC"))
            add(Friend(R.drawable.dog, "세자", "DDDDDDDD"))
            add(Friend(R.drawable.dog, "서율", "FFFFFFFF"))
            add(Friend(R.drawable.dog, "박당구", "EEEEEEEE"))
            add(Friend(R.drawable.dog, "박총수", "GGGGGGGG"))
            add(Friend(R.drawable.dog, "허염", "AAAAAAAA"))
            add(Friend(R.drawable.dog, "진설란", "AAAAAAAA"))
            add(Friend(R.drawable.dog, "서경", "AAAAAAAA"))
            add(Friend(R.drawable.dog, "오내관", "AAAAAAAA"))
            add(Friend(R.drawable.dog, "진호경", "AAAAAAAA"))
            add(Friend(R.drawable.dog, "도화", "AAAAAAAA"))
            add(Friend(R.drawable.dog, "김도주", "AAAAAAAA"))
            add(Friend(R.drawable.dog, "진무", "AAAAAAAA"))
            add(Friend(R.drawable.dog, "살수", "AAAAAAAA"))
            add(Friend(R.drawable.dog, "낙수", "AAAAAAAA"))
            add(Friend(R.drawable.dog, "조영", "AAAAAAAA"))
            add(Friend(R.drawable.dog, "조충원", "AAAAAAAA"))
            add(Friend(R.drawable.dog, "당골네", "AAAAAAAA"))
        }

        friendaddlistRVAdapter.notifyDataSetChanged()

        viewBinding.rvData.adapter = friendaddlistRVAdapter
        viewBinding.rvData.layoutManager = LinearLayoutManager(activity)

        // 아이템 전체 클릭시 상세 정보
        friendaddlistRVAdapter.setOnItemClickListener(object : FriendAddListRVAdapter.OnItemClickListener {
            override fun onItemClick(v: View, Friend: Friend, pos: Int) {
                val detailInfoFragment = FriendDetailFragment(Friend)
                detailInfoFragment.show(parentFragmentManager, "TAG")
            }
        })

        viewBinding.searchBar.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
            androidx.appcompat.widget.SearchView.OnQueryTextListener {

            val searchView = viewBinding.searchBar

            //검색버튼 입력시 호출
            override fun onQueryTextSubmit(s: String): Boolean {
                return false
            }

            //텍스트 입력/수정시에 호출
            override fun onQueryTextChange(s: String): Boolean {
                filter(s)
                return false
            }
        })
        return viewBinding.root
    }

    // Search View 필터 함수
    private fun filter(text: String) {
        val filteredlist: ArrayList<Friend> = ArrayList()
        for (item in friendlist) {
            if (item.id.toLowerCase().contains(text.toLowerCase())) {
                filteredlist.add(item)
            } else if (item in filteredlist) {
                filteredlist.remove(item)
            }
        }
        if (filteredlist.isEmpty()) {
            friendaddlistRVAdapter.filterList(filteredlist)
            Toast.makeText(activity, "No Data Found..", Toast.LENGTH_SHORT).show()
        } else {
            friendaddlistRVAdapter.filterList(filteredlist)
        }
    }
}
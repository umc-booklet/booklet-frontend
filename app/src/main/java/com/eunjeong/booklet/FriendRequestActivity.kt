package com.eunjeong.booklet

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.databinding.BindingAdapter
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.DividerItemDecoration.VERTICAL
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.eunjeong.booklet.adapters.FriendRequestRVAdapter
import com.eunjeong.booklet.databinding.ActivityFriendRequestBinding
import com.eunjeong.booklet.datas.FriendRequest
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.activity_friend_request.*


class FriendRequestActivity : AppCompatActivity() {
    private lateinit var viewBinding: ActivityFriendRequestBinding
    private val requestlist: ArrayList<FriendRequest> = arrayListOf()
    private val requestlistRVAdapter = FriendRequestRVAdapter(requestlist)
    private lateinit var isemptyView: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityFriendRequestBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        isemptyView = findViewById(R.id.emptyView)

        // 앱바
        val toolbar: Toolbar = viewBinding.toolbar
        setSupportActionBar(toolbar)
        val actionBar: ActionBar? = supportActionBar
        supportActionBar?.setDisplayShowTitleEnabled(false)
        actionBar?.setDisplayHomeAsUpEnabled(true)

        // 어댑터
        val decoration = DividerItemDecoration(this, VERTICAL)
        viewBinding.requestList.addItemDecoration(decoration)


        requestlist.apply {
            add(FriendRequest(R.drawable.dog, "진부연", "AAAAAAAA"))
            add(FriendRequest(R.drawable.dog, "진초연", "BBBBBBBB"))
            add(FriendRequest(R.drawable.dog, "장욱", "CCCCCCCC"))
            add(FriendRequest(R.drawable.dog, "세자", "DDDDDDDD"))
        }

        //requestlistRVAdapter.notifyDataSetChanged()
        viewBinding.requestList.adapter = requestlistRVAdapter
        viewBinding.requestList.layoutManager = LinearLayoutManager(this)
        isemptyView.visibility = View.GONE
        requestlistRVAdapter.registerAdapterDataObserver(RVEmptyObserver(request_list, isemptyView))

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.getItemId()) {
            android.R.id.home -> {
                //툴바 뒤로가기버튼 눌렸을 때 동작
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
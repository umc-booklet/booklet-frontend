package com.eunjeong.booklet

import android.R
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.eunjeong.booklet.databinding.ActivityFriendAddBinding

class FriendAddActivity : AppCompatActivity() {
    private lateinit var viewBinding: ActivityFriendAddBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityFriendAddBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        val toolbar: Toolbar = viewBinding.toolbar
        setSupportActionBar(toolbar) //액티비티의 앱바(App Bar)로 지정
        val actionBar: ActionBar? = supportActionBar //앱바 제어를 위해 툴바 액세스
        supportActionBar?.setDisplayShowTitleEnabled(false) //기본 앱 제목 보이지 않게 하기
        actionBar?.setDisplayHomeAsUpEnabled(true) // 앱바에 뒤로가기 버튼 만들기

        // 데이터 받아 오기
        val myName = intent.getStringExtra("Name")
        val myId =  intent.getStringExtra("UserId")
        val myImg = intent.getStringExtra("Img")

        // 각 Fragment 인자로 넘겨 주기
        // 내 프로필 Fragment
        supportFragmentManager
            .beginTransaction()
            .replace(viewBinding.profilecontainer.id, MyProfileFragment(myName, myId, myImg))
            .commitAllowingStateLoss()

        // 친구 추가 속에 친구 목록 Fragment
        supportFragmentManager
            .beginTransaction()
            .replace(viewBinding.friendcontainer.id, FriendListFragment(myId))
            .commitAllowingStateLoss()

        // 친구 요청 목록 버튼 클릭시 액티비티 교체
        viewBinding.requestbtn.setOnClickListener {
            val intent = Intent(this, FriendRequestActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.getItemId()) {
            R.id.home -> {
                finish() //툴바 뒤로 가기 버튼 눌렸을 때 동작
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

}
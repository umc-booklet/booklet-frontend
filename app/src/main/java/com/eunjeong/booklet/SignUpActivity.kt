package com.eunjeong.booklet

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.eunjeong.booklet.databinding.ActivitySignUpBinding

class SignUpActivity : AppCompatActivity() {
    private lateinit var viewBinding: ActivitySignUpBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        // 아이디 중복 확인 버튼을 누르면
        viewBinding.signUpIdCheckBtn.setOnClickListener {

        }

        // 가입하기 버튼을 누르면, 다음 화면으로 넘어가기
        viewBinding.signUpGoBtn.setOnClickListener {

        }



    }
}
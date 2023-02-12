package com.eunjeong.booklet

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.CalendarContract.CalendarEntity
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.eunjeong.booklet.databinding.ActivitySignUpBinding
import com.eunjeong.booklet.login.LoginActivity
import com.eunjeong.booklet.signUp.SignUpRequest
import com.eunjeong.booklet.signUp.SignUpService
import com.google.gson.JsonElement
import kotlinx.android.synthetic.main.nav_header_setting.view.*
//import kotlinx.android.synthetic.main.nav_header_setting.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SignUpActivity : AppCompatActivity() {
    private lateinit var viewBinding: ActivitySignUpBinding
    private lateinit var name: String
    private lateinit var userId: String
    private lateinit var password: String
    private lateinit var password_again: String

    // retrofit 객체
    val retrofit = Retrofit.Builder()
        .baseUrl("http://3.35.217.34:8080")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    // retrofit 객체에 Interface 연결
    val signUpAPI = retrofit.create(SignUpService::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
    }

    override fun onStart() {
        super.onStart()

        // 아이디 중복 확인 버튼을 누르면
        viewBinding.signUpIdCheckBtn.setOnClickListener {

        }

        // 가입하기 버튼을 누르면, 다음 화면으로 넘어가기
        viewBinding.signUpGoBtn.setOnClickListener {
            userId = viewBinding.signUpIdEt.text.toString()
            name = viewBinding.signUpNameEt.text.toString()
            password = viewBinding.signUpPwEt.text.toString()
            password_again = viewBinding.signUpAgainPwEt.text.toString()
            // 비밀번호 입력 한 게 같으면 넘어가기
            if (password == password_again){
                signUpAPI.postSignUp(SignUpRequest(userId, name, password)).enqueue(object: Callback<Response<Void>>{
                    override fun onResponse(
                        call: Call<Response<Void>>,
                        response: Response<Response<Void>>
                    ) {
//                        if(response.body()?.code() ?: Int == 2019){
//                            val dialog = AlertDialog.Builder(this@SignUpActivity)
//                            dialog.setTitle("잠시만요!")
//                            dialog.setMessage("중복된 아이디가 있습니다ㅠㅠ")
//                            dialog.show()
//                        }

                        Log.d("retrofit id", "${userId}")
                        Log.d("retrofit name", "${name}")
                        Log.d("retrofit password", "${password}")
                        Log.d("retrofit password again", "${password_again}")
                    }

                    override fun onFailure(call: Call<Response<Void>>, t: Throwable) {
                        Log.e("Retrofit Error", "서버 연결 실패")
                    }
// 서버연결은 OK, 중복체크는 못함(response가 없어서 이 값을 어떻게 받아야할지 모르겠음)
                })

                // 로그인 화면으로 넘어가기(지금 login activity에서 에러나서 안넘어감, 해결하면 주석 풀면 됨)
//                val nextIntent = Intent(this@SignUpActivity, LoginActivity::class.java)
//                startActivity(nextIntent)

            }
            // 비밀번호 입력이 다르면
            else{
                val dialog = AlertDialog.Builder(this)
                dialog.setTitle("잠시만요!")
                dialog.setMessage("비밀번호가 다릅니다ㅠㅠ")
                dialog.show()
            }

        }

    }
}


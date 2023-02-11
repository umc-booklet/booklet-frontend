package com.eunjeong.booklet.login

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.eunjeong.booklet.*
import com.eunjeong.booklet.databinding.ActivityLoginBinding
import com.eunjeong.booklet.databinding.DialogLoginAlarmBinding
import com.google.gson.JsonObject
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory


class LoginActivity : AppCompatActivity(){
    private lateinit var viewBinding : ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

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
        val loginService = retrofit.create(LoginService::class.java)

        viewBinding.signinbtn.setOnClickListener {
            val userid =  viewBinding.idt.text.toString()
            val password = viewBinding.pwdt.text.toString()

            val login = JsonObject()
            login.addProperty("userId", userid)
            login.addProperty("password", password)

            loginService.requestLogin(login).enqueue(object: Callback<LoginResponse>{
                override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                    if (response.isSuccessful){
                        val responseData = response.body()
                        if (responseData != null) {
                            Log.d("Retrofit","ResponseCode: ${responseData.code} Message: ${responseData.message}") // 기록 남기기

                            if (responseData.code == 1000) { // 로그인 성공
                                // ** 메인 화면 이동
                                val intent = Intent(this@LoginActivity, CalendarActivity::class.java)
                                startActivity(intent)

                                // ** {result} 전달
                                val intent2 = Intent(this@LoginActivity, FriendAddActivity::class.java)
                                intent2.putExtra("Name", responseData.result.name)
                                intent2.putExtra("UserId", responseData.result.userId)
                                intent2.putExtra("Img", responseData.result.profileImage)

                            }

                            if (responseData.code != 1000) { // 로그인 실패
                                cuDialog(viewBinding.root, responseData.message)
                            }

                            if (responseData.code == 1000 && viewBinding.auto.isChecked) { // 자동 로그인 체크 & 로그인 성공
                            }
                        }
                    }
                    else {
                        Log.w("Retrofit", "Response Not Successful ${response.code()}")
                    }
                }

                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    Log.e("Retrofit","Error!",t)
                    val dialog = AlertDialog.Builder(this@LoginActivity)
                    dialog.setTitle("오류")
                    dialog.setMessage("서버와 통신에 실패했습니다.")
                    dialog.show()
                }
            })
        }

        // '회원가입' 버튼을 누르면
        viewBinding.signupbtn.setOnClickListener{
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }
    }

    // 로그인 알람 Dialog
    fun cuDialog(view: View, s: String) {
        val binding: DialogLoginAlarmBinding = DialogLoginAlarmBinding.inflate(layoutInflater)
        val build = AlertDialog.Builder(view.context).apply {
            setView(binding.root) }

        val dialog = build.create()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT)); // 배경 투명
        binding.message.text = s
        dialog.setCancelable(true) // 뒷 배경 클릭시 취소 X
        dialog.show()

        binding.ookBtn.setOnClickListener { // Ok 버튼 클릭하면 지우기
            dialog.dismiss() }
    }
}
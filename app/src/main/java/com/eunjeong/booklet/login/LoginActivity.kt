package com.eunjeong.booklet.login

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.eunjeong.booklet.*
import com.eunjeong.booklet.databinding.ActivityLoginBinding
import com.eunjeong.booklet.databinding.DialogLoginAlarmBinding
import kotlinx.android.synthetic.main.dialog_login_alarm.*
import kotlinx.android.synthetic.main.dialog_login_alarm.view.*
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class LoginActivity : AppCompatActivity() {
    private lateinit var viewBinding : ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

//        val pref = getSharedPreferences("userdata", MODE_PRIVATE)
//        val savedData =pref.getString("logindata", "").toString()
//        Log.d(TAG, savedData)
//
//        if (logindata.equals("")){
//        } else {
//            val intent = Intent(applicationContext, CalendarActivity::class.java)
//            startActivity(intent)
//            Toast.makeText(this, "로그인 하였습니다", Toast.LENGTH_SHORT).show()
//            finish()
//        }

        val id =  viewBinding.idt.text.toString()
        val password = viewBinding.pwdt.text.toString()

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
        val loginService = retrofit.create(LoginService::class.java)

        // 로그인 버튼 누르면
        viewBinding.signinbtn.setOnClickListener {
            loginService.requestLogin(LoginRequest(id, password)).enqueue(object: Callback<LoginResponse>{
                override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                    if (response.isSuccessful){
                        val responseData = response.body()
                        if (responseData != null) {
                            Log.d("Retrofit","Response\nCode: ${responseData.code} Message: ${responseData.message}") // 기록 남기기

                            if (responseData.code == 1000) { // 제대로 로그인 했을 때
                                val intent = Intent(this@LoginActivity, CalendarActivity::class.java)
                                startActivity(intent)
                            }

                            if (responseData.code != 1000) { // 제대로 로그인 안했을 때
                                cuDialog(viewBinding.root, responseData.message)
                            }

                            // 로그인 성공(code = 1000) & 자동 로그인 check, 둘 다 만족하면 정보 저장
                            // 그리고 다시 앱을 실행했을 때, loginData 자동 완성 (따로 구현)
                            if (responseData.code == 1000 && viewBinding.auto.isChecked) {
                                //saveData(loginData)
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
        dialog.setCancelable(true)
        dialog.show()

        binding.ookBtn.setOnClickListener { // Ok 버튼 클릭하면 지우기
            dialog.dismiss() }
    }

//    fun saveData(loginData: LoginRequest){
//        val pref = getSharedPreferences("userdata", MODE_PRIVATE)
//        val edit = pref.edit() // 수정
//        edit.putString("logindata", loginData.toString()) // 값 넣기
//        edit.apply() // 적용
//    }
}
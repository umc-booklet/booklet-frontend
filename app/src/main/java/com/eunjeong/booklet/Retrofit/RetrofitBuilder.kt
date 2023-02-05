package com.eunjeong.booklet.Retrofit

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitBuilder {
    var api: signUpAPI
    init{
        val retrofit = Retrofit.Builder()
            .baseUrl("http://3.35.217.34:8080/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        api = retrofit.create(signUpAPI::class.java)
    }
}
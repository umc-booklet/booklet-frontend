package com.eunjeong.booklet.adapters

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.eunjeong.booklet.FriendAddActivity
import com.eunjeong.booklet.databinding.ActivityFriendAddBinding
import com.eunjeong.booklet.databinding.DialogRecomfirmAcceptBinding
import com.eunjeong.booklet.memberInfo.Info
import com.eunjeong.booklet.databinding.FriendAddListItemBinding
import com.eunjeong.booklet.friendAdd.FriendAddResponse
import com.eunjeong.booklet.friendAdd.FriendAddService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class FriendAddListRVAdapter(private var friendList: ArrayList<Info>): RecyclerView.Adapter<FriendAddListRVAdapter.DataViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(v: View, friend: Info, pos: Int)
    }

    private lateinit var listener: OnItemClickListener
    fun setOnItemClickListener(itemClickListener: OnItemClickListener) {
        listener = itemClickListener
    }

    inner class DataViewHolder(private val viewBinding: FriendAddListItemBinding) :
        RecyclerView.ViewHolder(viewBinding.root) {

        fun bind(item: Info) {
            viewBinding.frName.text = item.name // 이름
            //viewBinding.frImg.setImageResource(item.profileImage.toInt()) // 이미지

            viewBinding.root.setOnClickListener {
                val pos = adapterPosition
                if (pos != RecyclerView.NO_POSITION) {
                    itemView.setOnClickListener {
                        listener.onItemClick(itemView, item, pos)
                    }
                }
            }

            viewBinding.frAddBtn.setOnClickListener {
                //var userId = // 로그인 후 나의 ID 획득
                var friendId = item.userId.toInt() // 검색 결과로 나온 친구 ID
                var friendName = item.name // 검색 결과로 나온 친구 이름
                cuDialog(viewBinding.root, friendName)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataViewHolder {
        val binding = FriendAddListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DataViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DataViewHolder, position: Int) {
        holder.bind(friendList[position])
    }

    override fun getItemCount(): Int = friendList.size

     // 친구 추가 기능 userId: Int, friendId: Int


    // 친구 추가 재확인 알람 Dialog
    fun cuDialog(view: View, s: String) {
        val binding: DialogRecomfirmAcceptBinding = DialogRecomfirmAcceptBinding.inflate(LayoutInflater.from(view.context))
        val build = AlertDialog.Builder(view.context).apply {
            setView(binding.root) }

        val dialog = build.create()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT)); // 배경 투명
        binding.message.text = s + "님에게\n친구 요청을 보낼까요?"
        dialog.setCancelable(true)
        dialog.show()

        binding.okBtn.setOnClickListener {
            addFriend()
            dialog.dismiss() }

        binding.noBtn.setOnClickListener {
            dialog.dismiss()
        }
    }
    private fun addFriend() {

        lateinit var a : FriendAddActivity

        // retrofit 객체
        val retrofit = Retrofit.Builder()
            .baseUrl("http://3.35.217.34:8080")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val friendAddService = retrofit.create(FriendAddService::class.java)

        // 로그인 해결시 변경
        friendAddService.friendRequest(2, 3).enqueue(object : Callback<FriendAddResponse>{
            override fun onResponse(call: Call<FriendAddResponse>, response: Response<FriendAddResponse>) {
                if (response.isSuccessful){
                    val responseData = response.body()
                    if (responseData != null) {
                        Log.d("Retrofit","Response\nCode: ${responseData.code} Message: ${responseData.message}")
                    }
                } else {
                    Log.w("Retrofit", "Response Not Successful ${response.code()}")
                }
            }

            override fun onFailure(call: Call<FriendAddResponse>, t: Throwable) {
                Log.e("Retrofit", "Error!", t)
                Toast.makeText(a.applicationContext, "서버 통신 오류", Toast.LENGTH_SHORT)
            }
        })
    }
}
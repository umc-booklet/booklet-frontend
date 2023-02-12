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
import com.eunjeong.booklet.databinding.DialogRecomfirmBinding
import com.eunjeong.booklet.databinding.DialogRequestResultBinding
import com.eunjeong.booklet.databinding.FriendAddListItemBinding
import com.eunjeong.booklet.friendAdd.FriendAddResponse
import com.eunjeong.booklet.friendAdd.FriendAddService
import com.eunjeong.booklet.memberInfo.Info
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class FriendAddListRVAdapter(private var friendList: ArrayList<Info>, id: Long): RecyclerView.Adapter<FriendAddListRVAdapter.DataViewHolder>() {
    private val myId = id?.toInt()

    interface OnItemClickListener {
        fun onItemClick(v: View, friend: Info, pos: Int)
    }

    private lateinit var listener: OnItemClickListener
    fun setOnItemClickListener(itemClickListener: OnItemClickListener) {
        listener = itemClickListener
    }

    inner class DataViewHolder(val viewBinding: FriendAddListItemBinding) :
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
                var friendId = item.id.toInt() // 검색 결과로 나온 친구 ID
                var friendName = item.name // 검색 결과로 나온 친구 이름
                if (myId != null) {
                    cuDialog(viewBinding.root, friendName, myId!!, friendId)
                } else {
                    Log.d("djgb", "null 이라네 ~!!!!")
                }
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

    // 친구 추가 재확인 알람 Dialog
    fun cuDialog(view: View, s: String, my: Int, you: Int) {
        val binding: DialogRecomfirmBinding = DialogRecomfirmBinding.inflate(LayoutInflater.from(view.context))
        val build = AlertDialog.Builder(view.context).apply {
            setView(binding.root) }

        val dialog = build.create()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT)); // 배경 투명
        binding.message.text = s + "님에게\n친구 요청을 하겠습니까?"
        dialog.setCancelable(true)
        dialog.show()

        binding.okBtn.setOnClickListener {
            addFriend(my, you, view)
            dialog.dismiss() }

        binding.noBtn.setOnClickListener {
            dialog.dismiss()
        }
    }
    private fun addFriend(userId: Int, friendId: Int, view : View) {

        // retrofit 객체
        val retrofit = Retrofit.Builder()
            .baseUrl("http://3.35.217.34:8080")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val friendAddService = retrofit.create(FriendAddService::class.java)

        friendAddService.friendRequest(userId, friendId).enqueue(object : Callback<FriendAddResponse>{
            override fun onResponse(call: Call<FriendAddResponse>, response: Response<FriendAddResponse>) {
                if (response.isSuccessful){
                    val responseData = response.body()
                    if (responseData != null) {
                        Log.d("Retrofit","Response\nCode: ${responseData.code} Message: ${responseData.message}")
                        cuDialog2(view ,responseData.message)
                    }
                } else {
                    Log.w("Retrofit", "Response Not Successful ${response.code()}")
                }
            }

            override fun onFailure(call: Call<FriendAddResponse>, t: Throwable) {
                Log.e("Retrofit", "Error!", t)
                Toast.makeText(view.context, "서버 통신 오류", Toast.LENGTH_SHORT)
            }
        })
    }

    // 친구 요청 결과 알림 Dialog
    fun cuDialog2(view: View, s: String) {
        val binding: DialogRequestResultBinding = DialogRequestResultBinding.inflate(LayoutInflater.from(view.context))
        val build = AlertDialog.Builder(view.context).apply {
            setView(binding.root) }

        val dialog = build.create()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT)); // 배경 투명
        binding.message.text = s
        dialog.setCancelable(true)
        dialog.show()

        binding.okBtn.setOnClickListener {
            dialog.dismiss() }
    }
}
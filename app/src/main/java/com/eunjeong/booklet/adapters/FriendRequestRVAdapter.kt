package com.eunjeong.booklet.adapters

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.eunjeong.booklet.databinding.FriendRequestItemBinding
import com.eunjeong.booklet.datas.FriendRequest

class FriendRequestRVAdapter(private var requestlist: ArrayList<FriendRequest>): RecyclerView.Adapter<FriendRequestRVAdapter.DataViewHolder>() {

    private var check = false

    interface OnItemClickListener {
        fun onItemClick(v: View, request: FriendRequest, pos: Int)
    }

    private lateinit var listener: FriendRequestRVAdapter.OnItemClickListener
    fun setOnItemClickListener(itemClickListener: FriendRequestRVAdapter.OnItemClickListener) {
        listener = itemClickListener
    }

    // ViewHolder 객체
    inner class DataViewHolder(private val viewBinding: FriendRequestItemBinding) :
        RecyclerView.ViewHolder(viewBinding.root) {

        fun bind(item: FriendRequest) {
            viewBinding.frImg.setImageResource(item.img)
            viewBinding.frId.text = item.id
            viewBinding.frName.text = item.name
            
            // '수락' 버튼 눌렀을 때
            viewBinding.okbtn.setOnClickListener {
                if (!check) {
                    viewBinding.okbtn.setBackgroundColor(Color.parseColor("#6792FF"))
                    check = true
                    //accept(viewBinding.root) // 실제 추가 기능

                } else {
                    viewBinding.okbtn.setBackgroundColor(Color.parseColor("#ABC2FB"))
                    check = false
                }

                // 친구 수락했으니 친구 목록에 추가되는 기능 구현 필요
            }
            // '거절' 버튼 눌렀을 때
            viewBinding.nobtn.setOnClickListener {
                //reject(position, viewBinding.root)

            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataViewHolder {
        val binding = FriendRequestItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DataViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DataViewHolder, position: Int) {
        holder.bind(requestlist[position])
    }

    override fun getItemCount(): Int = requestlist.size

    // 수락시 행동
//    fun accept(parent: View) {
//
//        val binding: DialogRecomfirmAcceptBinding = DialogRecomfirmAcceptBinding.inflate(LayoutInflater.from(parent.context))
//        val build = AlertDialog.Builder(parent.context).apply {
//            setView(binding.root)
//        }
//
//        val dialog = build.create()
//        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT)); // 배경 투명
//        dialog.setCancelable(true) // 뒷 배경 클릭시 취소 X
//        dialog.show()
//
//        binding.okBtn.setOnClickListener { // Ok 버튼 클릭시 지우기
//            // 친구 요청 수락 !
//            // 친구 목록에 넣어야 함.
//
//            dialog.dismiss()
//        }
//
//        binding.noBtn.setOnClickListener { // no 버튼 클릭시 지우기
//            dialog.dismiss()
//        }
//    }

    // 거절시 행동
//    fun reject(position: Int, parent: View) {
//
//        val binding: DialogReconfirmRejectBinding = DialogReconfirmRejectBinding.inflate(LayoutInflater.from(parent.context))
//        val build = AlertDialog.Builder(parent.context).apply {
//            setView(binding.root)
//        }
//
//        val dialog = build.create()
//        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT)); // 배경 투명
//        dialog.setCancelable(true) // 뒷 배경 클릭시 취소 X
//        dialog.show()
//
//        binding.okBtn.setOnClickListener { // Ok 버튼 클릭시 지우기
//            requestlist.removeAt(position)
//            notifyItemRemoved(position)
//            notifyDataSetChanged() // 반드시
//
//            //서버에서도 지워야 함.
//
//            dialog.dismiss()
//        }
//        binding.noBtn.setOnClickListener { // no 버튼 클릭시 지우기
//            dialog.dismiss()
//        }
//    }


}
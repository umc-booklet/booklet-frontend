package com.eunjeong.booklet.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.eunjeong.booklet.R
import com.eunjeong.booklet.databinding.FriendRequestItemBinding
import com.eunjeong.booklet.datas.FriendRequest
import de.hdodenhof.circleimageview.CircleImageView

class FriendRequestRVAdapter(private var requestlist: ArrayList<FriendRequest>): RecyclerView.Adapter<FriendRequestRVAdapter.DataViewHoler>() {

    private var check = false;


    interface OnItemClickListener {
        fun onItemClick(v: View, request: FriendRequest, pos: Int)
    }

    private lateinit var listener: FriendRequestRVAdapter.OnItemClickListener
    fun setOnItemClickListener(itemClickListener: FriendRequestRVAdapter.OnItemClickListener) {
        listener = itemClickListener
    }

    // ViewHodler 객체
    inner class DataViewHoler(private val viewBinding: FriendRequestItemBinding) :
        RecyclerView.ViewHolder(viewBinding.root) {

        fun bind(item: FriendRequest) {
            viewBinding.frImg.setImageResource(item.img)
            viewBinding.frId.text = item.id
            viewBinding.frName.text = item.name
            
            // '수락' 버튼 눌렀을 때
            viewBinding.okbtn.setOnClickListener {
                if (check == false) {
                    viewBinding.okbtn.setBackgroundColor(Color.parseColor("#6792FF"))
                    check = true
                } else {
                    viewBinding.okbtn.setBackgroundColor(Color.parseColor("#ABC2FB"))
                    check = false
                }

                // 친구 수락했으니 친구 목록에 추가되는 기능 구현 필요
            }
            // '거절' 버튼 눌렀을 때
            viewBinding.nobtn.setOnClickListener {
                // 진짜로 거절할지 다이얼로그 띄우던지
                // 목록에서 삭제하던지 어떤 기능 필요.
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataViewHoler {
        val binding = FriendRequestItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DataViewHoler(binding)
    }

    override fun onBindViewHolder(holder: DataViewHoler, position: Int) {
        holder.bind(requestlist[position])
    }

    override fun getItemCount(): Int = requestlist.size
}
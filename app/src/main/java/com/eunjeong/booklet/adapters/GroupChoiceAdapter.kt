package com.eunjeong.booklet.adapters

import android.annotation.SuppressLint
import android.media.Image
import android.text.Layout
import android.util.Log
import android.util.SparseBooleanArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import android.widget.AdapterView.OnItemClickListener
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.eunjeong.booklet.GroupChoiceActivity
import com.eunjeong.booklet.R
import com.eunjeong.booklet.databinding.GroupChoiceItemBinding
import com.eunjeong.booklet.datas.Friend
import com.eunjeong.booklet.datas.GroupFriend
import kotlinx.android.synthetic.main.group_choice_item.view.*
import org.w3c.dom.Text


class GroupChoiceAdapter(var friendList: ArrayList<GroupFriend>):
    RecyclerView.Adapter<GroupChoiceAdapter.GroupViewHolder>(), Filterable {

    var TAG = "FriendListAdpater"
    var filteredFriends = ArrayList<GroupFriend>()
    lateinit var binding: GroupChoiceItemBinding

    private var mSelectedItem = -1


    init {
        filteredFriends.addAll(friendList)
    }

    override fun getFilter(): Filter {
        return object: Filter(){
            override fun performFiltering(p0: CharSequence?): FilterResults {
                val filterString = p0.toString()
                val results = FilterResults()
                Log.d(TAG, "charSequence : $p0")

                //검색이 필요 없을 경우를 위해 원본 배열 복제
                val filteredList: ArrayList<GroupFriend> = ArrayList<GroupFriend>()
                //공백 제외 아무런 값이 없을 경우 -> 원본 배열
                if(filterString.trim{it <= ' '}.isEmpty()){
                    results.values = friendList
                    results.count = friendList.size
                    return results
                }
                else{
                    for(friend in friendList){
                        if(friend.id.contains(filterString)){
                            filteredList.add(friend)
                        }
                    }
                }
                results.values = filteredList
                results.count = filteredList.size
                return results
            }

            // @SuppressLint("NotifyDataSetChanged")
            override fun publishResults(p0: CharSequence?, filterResults: FilterResults?) {
                filteredFriends.clear()
                if (filterResults != null) {
                    filteredFriends.addAll(filterResults.values as ArrayList<GroupFriend>)
                }
                notifyDataSetChanged()
            }
        }
    }

// 아이템 레이아웃과 결합
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupViewHolder{
        binding = GroupChoiceItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return GroupViewHolder(binding)
    }

    //아이템 재사용 방지
    override fun getItemViewType(position: Int): Int {
        return position
    }

    fun filterList(filterList: ArrayList<GroupFriend>){
        friendList = filterList
        //notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return friendList.size
    }

    // view에 내용입력
    override fun onBindViewHolder(holder: GroupViewHolder, position: Int) {
        holder.bind(friendList[position])
        // item click시
//        holder.itemView.setOnClickListener{
//
//        }
    }

    // 레이아웃 내 view 연결
    inner class GroupViewHolder(binding: GroupChoiceItemBinding): RecyclerView.ViewHolder(binding.root){
        private val fimg: ImageView = binding.profileImageView
        private val fname: TextView = binding.nameTextView
        private val fid: TextView = binding.idTextView

        fun bind(recyclerViewItem: GroupFriend){
            val checkBox = binding.checkboxGroup
            checkBox.isChecked = position == mSelectedItem

            fimg.setImageResource(recyclerViewItem.img)
            fname.text = recyclerViewItem.name
            fid.text = recyclerViewItem.id

            // 재사용 문제 해결
            itemView.checkboxGroup.isChecked = friendList[adapterPosition].checked

            itemView.checkboxGroup.setOnClickListener {
                recyclerViewItem.checked = itemView.checkboxGroup.isChecked
                notifyItemChanged(adapterPosition)
                mSelectedItem = position
                //notifyItemRangeChanged(9, friendList.size)
            }
        }
    }

    fun setItem(item: List<GroupFriend>){
        friendList = item as ArrayList<GroupFriend>
        mSelectedItem = -1
        notifyDataSetChanged()
    }
}






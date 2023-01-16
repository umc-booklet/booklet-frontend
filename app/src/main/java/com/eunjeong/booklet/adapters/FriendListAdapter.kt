package com.eunjeong.booklet.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.eunjeong.booklet.R
import com.eunjeong.booklet.datas.Friend
import java.time.chrono.JapaneseEra.values


//class FriendListAdapter(private var friendList: ArrayList<Friend>) : RecyclerView.Adapter<FriendListAdapter.CustomViewHolder>()
//{
class FriendListAdapter (var friendList: ArrayList<Friend>) :
    RecyclerView.Adapter<FriendListAdapter.CustomViewHolder>(), Filterable{
    var TAG = "FriendListAdpater"
    var filteredFriends = ArrayList<Friend>()
    var itemFilter = ItemFilter()

    init {
        filteredFriends.addAll(friendList)
    }

    override fun getFilter(): Filter {
        return itemFilter
    }

    inner class ItemFilter : Filter() {
        override fun performFiltering(p0: CharSequence?): FilterResults {
            val filterString = p0.toString()
            val results = FilterResults()
            Log.d(TAG, "charSequence : $p0")

            //검색이 필요 없을 경우를 위해 원본 배열 복제
            val filteredList: ArrayList<Friend> = ArrayList<Friend>()
            //공백 제외 아무런 값이 없을 경우 -> 원본 배열
            if(filterString.trim{it <= ' '}.isEmpty()){
                results.values = friendList
                results.count = friendList.size

                return results
            }
            // 공백 제외 2글자 이하인 경우 -> 이름으로만 검색
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

        @SuppressLint("NotifyDataSetChanged")
        override fun publishResults(p0: CharSequence?, filterResults: FilterResults?) {
            filteredFriends.clear()
            if (filterResults != null) {
                filteredFriends.addAll(filterResults.values as ArrayList<Friend>)
            }
            notifyDataSetChanged()
        }
    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.friend_list_item, parent, false)
        return CustomViewHolder(view)
    }

    fun filterList(filterList: ArrayList<Friend>){
        friendList = filterList
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return friendList.size
    }

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        holder.img.setImageResource(friendList.get(position).img)
        holder.name.text = friendList.get(position).name
        holder.id.text = friendList.get(position).id
    }

    class CustomViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val img = itemView.findViewById<ImageView>(R.id.profileImageView) // 프로필 사진
        val name = itemView.findViewById<TextView>(R.id.nameTextView) // name
        val id = itemView.findViewById<TextView>(R.id.idTextView) // id
    }


}
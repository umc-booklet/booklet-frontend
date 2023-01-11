//package com.eunjeong.booklet.adapters
//
//import android.annotation.SuppressLint
//import android.util.Log
//import android.view.LayoutInflater
//import android.view.ViewGroup
//import android.widget.Filter
//import android.widget.Filterable
//import androidx.recyclerview.widget.RecyclerView
//import com.eunjeong.booklet.R
//import com.eunjeong.booklet.datas.Friend
//
//class GroupChoiceAdapter(var friendList: ArrayList<Friend>):
//    RecyclerView.Adapter<GroupChoiceAdapter.GroupViewHolder>(), Filterable {
//
//    var TAG = "FriendListAdpater"
//    var filteredFriends = ArrayList<Friend>()
//    var itemFilter = ItemFilter() /////
//
//    init {
//        filteredFriends.addAll(friendList)
//    }
//
//    override fun getFilter(): Filter {
//        return itemFilter
//    }
//
//    inner class ItemFilter : Filter() {
//        override fun performFiltering(p0: CharSequence?): FilterResults {
//            val filterString = p0.toString()
//            val results = FilterResults()
//            Log.d(TAG, "charSequence : $p0")
//
//            //검색이 필요 없을 경우를 위해 원본 배열 복제
//            val filteredList: ArrayList<Friend> = ArrayList<Friend>()
//            //공백 제외 아무런 값이 없을 경우 -> 원본 배열
//            if(filterString.trim{it <= ' '}.isEmpty()){
//                results.values = friendList
//                results.count = friendList.size
//                return results
//            }
//            // 공백 제외 2글자 이하인 경우 -> 이름으로만 검색
//            else{
//                for(friend in friendList){
//                    if(friend.id.contains(filterString)){
//                        filteredList.add(friend)
//                    }
//                }
//            }
//            results.values = filteredList
//            results.count = filteredList.size
//            return results
//        }
//
//        @SuppressLint("NotifyDataSetChanged")
//        override fun publishResults(p0: CharSequence?, filterResults: FilterResults?) {
//            filteredFriends.clear()
//            if (filterResults != null) {
//                filteredFriends.addAll(filterResults.values as ArrayList<Friend>)
//            }
//            notifyDataSetChanged()
//        }
//    }
//
//    //오류 ....
////    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupViewHolder{
////        val view = LayoutInflater.from(parent.context).inflate(R.layout.group_choice_item, parent, false)
////        return GroupViewHolder(view)
////    }
//
//
//
//}

package com.eunjeong.booklet.adapters

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.eunjeong.booklet.databinding.FriendAddListItemBinding
import com.eunjeong.booklet.datas.Friend

class FriendAddListRVAdapter(private var friendlist: ArrayList<Friend>): RecyclerView.Adapter<FriendAddListRVAdapter.DataViewHoler>(),
    Filterable {

    //---------------------------------------------
    //-- viewholder & clicklistener
    interface OnItemClickListener {
        fun onItemClick(v: View, friend: Friend, pos: Int)
    }

    private lateinit var listener: OnItemClickListener
    fun setOnItemClickListener(itemClickListener: OnItemClickListener) {
        listener = itemClickListener
    }

    // ViewHodler 객체
    inner class DataViewHoler(private val viewBinding: FriendAddListItemBinding) :
        RecyclerView.ViewHolder(viewBinding.root) {
        //만든 데이터 클래스를 인자로 받음.
        fun bind(item: Friend) {
            viewBinding.person = item
            viewBinding.root.setOnClickListener {
                val pos = adapterPosition
                if (pos != RecyclerView.NO_POSITION) {
                    itemView.setOnClickListener {
                        listener.onItemClick(itemView, item, pos)
                    }
                }
            }
        }
    }

    // ViewHolder 만들어질 때 실행할 동작
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataViewHoler {
        val fourBinding =
            FriendAddListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DataViewHoler(fourBinding)
    }

    // ViewHolder가 실제로 데이터를 표시해야 할 때 호출되는 함수
    override fun onBindViewHolder(holder: DataViewHoler, position: Int) {
        holder.bind(friendlist[position])
    }

    // 표현할 Item의 총 개수
    override fun getItemCount(): Int = friendlist.size

    //-------------------------
    //-- filter
    var TAG = "FriendAddListRVAdapter"
    var filteredPersons = ArrayList<Friend>()
    var itemFilter = ItemFilter()
    init {
        filteredPersons.addAll(friendlist)
    }
    inner class ItemFilter : Filter() {
        override fun performFiltering(constraint: CharSequence): FilterResults {
            val filterString = constraint.toString()
            val results = FilterResults()
            Log.d(TAG, "charSequence : $constraint")

            //검색이 필요없을 경우를 위해 원본 배열을 복제
            val filteredList: ArrayList<Friend> = ArrayList<Friend>()
            //공백제외 아무런 값이 없을 경우 -> 원본 배열
            if (filterString.trim { it <= ' ' }.isEmpty()) {
                results.values = friendlist
                results.count = friendlist.size

                return results

            } else {
                for (person in friendlist) {
                    if (person.id.contains(filterString)) {
                        filteredList.add(person)
                    }
                }
                results.values = filteredList
                results.count = filteredList.size

                return results
            }
        }
        @SuppressLint("NotifyDataSetChanged")
        override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
            filteredPersons.clear()
            if (results != null) {
                filteredPersons.addAll(results.values as ArrayList<Friend>)
            }
            notifyDataSetChanged()
        }
    }
    override fun getFilter(): Filter {
        return itemFilter
    }

    fun filterList(filterList: ArrayList<Friend>){
        friendlist = filterList
        notifyDataSetChanged()
    }
}
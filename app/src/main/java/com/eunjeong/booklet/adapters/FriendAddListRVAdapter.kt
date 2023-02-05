package com.eunjeong.booklet.adapters

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.eunjeong.booklet.R
import com.eunjeong.booklet.memberInfo.Info
import com.eunjeong.booklet.databinding.FriendAddListItemBinding
import com.eunjeong.booklet.friendAdd.FriendAddService
import com.eunjeong.booklet.login.LoginService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class FriendAddListRVAdapter(private var friendlist: ArrayList<Info>): RecyclerView.Adapter<FriendAddListRVAdapter.DataViewHoler>(),
    Filterable {


//    // retrofit 객체
//    val retrofit = Retrofit.Builder()
//        .baseUrl("http://3.35.217.34:8080")
//        .addConverterFactory(GsonConverterFactory.create())
//        .build()
//
//    // retrofit 객체에 Interface 연결
//    val friendRequest = retrofit.create(FriendAddService::class.java)

    //---------------------------------------------
    //-- ViewHolder & ClickListener
    interface OnItemClickListener {
        fun onItemClick(v: View, friend: Info, pos: Int)
    }

    private lateinit var listener: OnItemClickListener
    fun setOnItemClickListener(itemClickListener: OnItemClickListener) {
        listener = itemClickListener
    }

    // ViewHolder 객체
    inner class DataViewHoler(private val viewBinding: FriendAddListItemBinding) :
        RecyclerView.ViewHolder(viewBinding.root) {

        fun bind(item: Info) {
            viewBinding.frName.text = item.name // 이름

            viewBinding.root.setOnClickListener {
                val pos = adapterPosition
                if (pos != RecyclerView.NO_POSITION) {
                    itemView.setOnClickListener {
                        listener.onItemClick(itemView, item, pos)
                    }
                }
            }

            viewBinding.frAddBtn.setOnClickListener {
                // 친구 추가
                //friendRequest.friendRequest()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataViewHoler {
        val binding = FriendAddListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DataViewHoler(binding)
    }

    override fun onBindViewHolder(holder: DataViewHoler, position: Int) {
        holder.bind(friendlist[position])
    }

    override fun getItemCount(): Int = friendlist.size









    //-------------------------
    //-- filter
    var TAG = "FriendAddListRVAdapter"
    var filteredPersons = ArrayList<Info>()
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
            val filteredList: ArrayList<Info> = ArrayList<Info>()
            //공백제외 아무런 값이 없을 경우 -> 원본 배열
            if (filterString.trim { it <= ' ' }.isEmpty()) {
                results.values = friendlist
                results.count = friendlist.size

                return results

            } else {
                for (person in friendlist) {
                    if (person.id.toString().contains(filterString)) {
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
                filteredPersons.addAll(results.values as ArrayList<Info>)
            }
            notifyDataSetChanged()
        }
    }
    override fun getFilter(): Filter {
        return itemFilter
    }

    fun filterList(filterList: ArrayList<Info>){
        friendlist = filterList
        notifyDataSetChanged()
    }
}
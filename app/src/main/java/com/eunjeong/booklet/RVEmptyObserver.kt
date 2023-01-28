package com.eunjeong.booklet

import android.util.Log
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class RVEmptyObserver(private val recyclerView: RecyclerView, private val emptyView: View?) : RecyclerView.AdapterDataObserver() {

    init {
        checkIfEmpty()
    }

    private fun checkIfEmpty() {
        if (emptyView != null && recyclerView.adapter != null) {

            val emptyViewVisible = recyclerView.adapter!!.itemCount == 0
            Log.d(TAG.toString(), " Enabling empty view for list : No data found ")
            emptyView.visibility = if (emptyViewVisible) View.VISIBLE else View.GONE
            recyclerView.visibility = if (emptyViewVisible) View.GONE else View.VISIBLE
        }
    }

    override fun onChanged() {
        checkIfEmpty()
    }

    override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
        checkIfEmpty()
    }

    override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
        checkIfEmpty()
    }

    companion object {
        private val TAG = RVEmptyObserver::class.java
    }

}
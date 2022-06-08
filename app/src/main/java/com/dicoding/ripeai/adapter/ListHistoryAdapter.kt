package com.dicoding.ripeai.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.dicoding.ripeai.R

class ListHistoryAdapter(private val listHistory: List<String>) : RecyclerView.Adapter<ListHistoryAdapter.ViewHolder>() {
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int) =
        ViewHolder(LayoutInflater.from(viewGroup.context).inflate(R.layout.row_item_history, viewGroup, false))
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.tvItem.text = listHistory[position]
        viewHolder.tvDescription.text = listHistory[position]
    }
    override fun getItemCount() = listHistory.size
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvItem: TextView = view.findViewById(R.id.fruit_name)
        val tvDescription: TextView = view.findViewById(R.id.description)
    }

}
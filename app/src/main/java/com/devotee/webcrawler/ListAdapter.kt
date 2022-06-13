package com.devotee.webcrawler

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView


internal class ListAdapter(private val getData: List<String>):RecyclerView.Adapter<ListAdapter.ListAdapterViewHolder>()
{
    internal inner class ListAdapterViewHolder(view:View): RecyclerView.ViewHolder(view)
    {
        var listTv:TextView=view.findViewById(R.id.urlTv)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListAdapterViewHolder
    {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.recyclerview_row, parent, false)
        return ListAdapterViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ListAdapterViewHolder, position: Int)
    {
         holder.listTv.text=getData[position]

    }

    override fun getItemCount(): Int {

        return getData.size
    }

}
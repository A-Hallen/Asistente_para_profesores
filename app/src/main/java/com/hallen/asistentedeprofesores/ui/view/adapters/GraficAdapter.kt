package com.hallen.asistentedeprofesores.ui.view.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class GraficAdapter(private var dias: List<String> = emptyList()):  RecyclerView.Adapter<GraficAdapter.GraficViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GraficViewHolder {
        val view: TextView = LayoutInflater.from(parent.context).inflate(android.R.layout.simple_list_item_1, parent, false) as TextView
        return GraficViewHolder(view)
    }

    override fun onBindViewHolder(holder: GraficViewHolder, position: Int) {
        holder.view.setTextColor(Color.BLACK)
        holder.view.text = dias[position]
    }

    fun update(newList: List<String>){
        dias = newList
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = dias.size

    inner class GraficViewHolder(val view: TextView): RecyclerView.ViewHolder(view)
}
package com.hallen.asistentedeprofesores.ui.view.adapters

import android.view.ContextMenu
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.hallen.asistentedeprofesores.R
import com.hallen.asistentedeprofesores.domain.model.Day
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class AdapterDays(
    var days: List<Day> = listOf(),
    var total: Int = 0 // Representa el total de estudiantes del grupo
): RecyclerView.Adapter<AdapterDays.ViewHolder>() {
    private val dateFormat: DateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.US)

    interface OnItemLongClickListener {     fun onItemLongClick (day: Day, menu: ContextMenu)   }
    interface OnItemClickListener     {     fun onItemClick     (day: Day)                      }

    private lateinit var clickListener: OnItemClickListener
    private lateinit var longClickListener: OnItemLongClickListener

    fun setOnItemClickListener    (listener: OnItemClickListener)    {    clickListener     = listener }
    fun setOnItemLongClickListener(listener: OnItemLongClickListener){    longClickListener = listener }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_day, parent, false)
        return ViewHolder(view, clickListener, longClickListener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = days[position]
        val coolFecha = dateFormat.parse(currentItem.date)

        holder.fechaTextView.text = if (coolFecha == null) currentItem.date else {
            DateFormat.getDateInstance().format(coolFecha)
        }
        val presents = currentItem.presents
        val aucentes = total - currentItem.presents

        holder.presentes.text = "Presentes: $presents"
        holder.ausentes.text = "Ausentes: $aucentes"
    }

    override fun getItemCount(): Int = days.size
    inner class ViewHolder(view: View, itemClickListener: OnItemClickListener, itemLongClickListener: OnItemLongClickListener):
        RecyclerView.ViewHolder(view) {

        val fechaTextView: TextView = view.findViewById(R.id.fecha)
        val ausentes:      TextView = view.findViewById(R.id.aucentes)
        val presentes:     TextView = view.findViewById(R.id.presentes)

        init {
            view.setOnClickListener {
                itemClickListener.onItemClick(days[adapterPosition])
            }
            view.setOnCreateContextMenuListener { menu, _, _ ->
                itemLongClickListener.onItemLongClick(days[adapterPosition], menu)
            }
        }
    }
}
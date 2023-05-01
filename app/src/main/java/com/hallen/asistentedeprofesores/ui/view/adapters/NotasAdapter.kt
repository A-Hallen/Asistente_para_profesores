package com.hallen.asistentedeprofesores.ui.view.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.hallen.asistentedeprofesores.R
import com.hallen.asistentedeprofesores.domain.model.Nota
import com.hallen.asistentedeprofesores.ui.view.adapters.diffs.NotasDiffCallback
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class NotasAdapter(
    private val context: Context,
    private val itemClickListener: (Nota, Int) -> Unit,
    private val itemLongClickListener: (Nota, View) -> Unit
) : RecyclerView.Adapter<NotasAdapter.NotasViewHolder>() {
    private var notasList: List<Nota> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotasViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.notas_item, parent, false)
        return NotasViewHolder(view)
    }

    override fun onBindViewHolder(holder: NotasViewHolder, position: Int) {
        val currentItem = notasList[position]
        holder.titleView.text = currentItem.title
        holder.contentView.text = currentItem.content
    }

    override fun getItemCount(): Int = notasList.size

    fun updateNota(notas: List<Nota>, requireActivity: FragmentActivity){
        CoroutineScope(Dispatchers.IO).launch {
            val notasDiffCallback = NotasDiffCallback(notasList, notas)
            val diffResult = DiffUtil.calculateDiff(notasDiffCallback)
            notasList = notas
            requireActivity.runOnUiThread {
                diffResult.dispatchUpdatesTo(this@NotasAdapter)
            }
        }
    }

    fun getItemKey(actualItemPosition: Int): Int = notasList[actualItemPosition].id

    inner class NotasViewHolder(
        view: View
    ) : RecyclerView.ViewHolder(view) {
        val titleView: TextView = view.findViewById(R.id.nota_title)
        val contentView: TextView = view.findViewById(R.id.nota_content)

        init {
            view.setOnClickListener {
                itemClickListener(notasList[adapterPosition], adapterPosition)
            }
            view.setOnLongClickListener {
                itemLongClickListener(notasList[adapterPosition], it)
                true
            }
        }
    }
}
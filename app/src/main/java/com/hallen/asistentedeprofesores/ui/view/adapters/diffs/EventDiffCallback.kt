package com.hallen.asistentedeprofesores.ui.view.adapters.diffs

import androidx.recyclerview.widget.DiffUtil
import com.hallen.asistentedeprofesores.domain.model.Event

class EventDiffCallback(
    private val oldList: List<Event>,
    private val newList: List<Event>): DiffUtil.Callback() {

    override fun getOldListSize(): Int = oldList.size

    override fun getNewListSize(): Int = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].id == newList[newItemPosition].id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldNota = oldList[oldItemPosition]
        val newNota = newList[newItemPosition]
        return oldNota.details == newNota.details && oldNota.title == newNota.title
    }
}
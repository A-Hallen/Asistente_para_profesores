package com.hallen.asistentedeprofesores.ui.view.adapters.diffs

import androidx.recyclerview.widget.DiffUtil
import com.hallen.asistentedeprofesores.domain.model.StudentRegisters

class RegisterDiffCallback(
    private val oldList: List<StudentRegisters>,
    private val newList: List<StudentRegisters>
): DiffUtil.Callback() {
    override fun getOldListSize(): Int = oldList.size

    override fun getNewListSize(): Int = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].id == newList[newItemPosition].id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldAssistance = oldList[oldItemPosition]
        val newAssistance = newList[newItemPosition]
        return oldAssistance.assistance == newAssistance.assistance && oldAssistance.name == newAssistance.name
    }
}
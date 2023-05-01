package com.hallen.asistentedeprofesores.ui.view.adapters

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.hallen.asistentedeprofesores.R
import com.hallen.asistentedeprofesores.domain.model.StudentData
import com.hallen.asistentedeprofesores.ui.view.adapters.diffs.AssistanceDiffCallback

class AssistanceAdapter(var date: String, var students: List<StudentData> = arrayListOf()) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    interface OnItemCheckListener {
        fun onItemCheck(position: Int, checkBox: CheckBox, isChecked: Boolean)
    }

    interface OnChangeNoteListener {
        fun onChangeNote(
            position: Int,
            button: ImageView,
            textView: TextView,
            checked: Boolean
        )
    }

    private lateinit var cbListener: OnItemCheckListener
    private lateinit var nListener: OnChangeNoteListener
    fun setOnChangeNoteListener(nlistener: OnChangeNoteListener) {
        nListener = nlistener
    }

    fun setOnItemCheckListener(cblistener: OnItemCheckListener) {
        cbListener = cblistener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view: View
        return if (viewType == 0) {
            view = LayoutInflater.from(parent.context)
                .inflate(R.layout.student_item_assistance_preview, parent, false)
            AssistanceShimmerViewHolder(view)
        } else {
            view = LayoutInflater.from(parent.context)
                .inflate(R.layout.student_item_asistance, parent, false)
            AssistanceViewHolder(
                view,
                cbListener,
                nListener
            )
        }
    }

    override fun getItemViewType(position: Int): Int =
        if (students[position].name.isBlank()) 0 else 1

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val currentItem = students[position]
        if (currentItem.name.isBlank()) return

        (holder as AssistanceViewHolder).nameTextView.text = currentItem.name
        holder.checkBox.isChecked = false
        holder.notaTextView.text = ""
        holder.notaTextView.visibility = View.INVISIBLE
        holder.notaImageView.visibility = View.VISIBLE

        val asistencia = currentItem.asistencia
        if (asistencia != null) {
            val present = asistencia.assistance != 0
            if (asistencia.qualification > 1) {
                holder.notaImageView.visibility = View.INVISIBLE
                holder.notaTextView.text = asistencia.qualification.toString()
                holder.notaTextView.visibility = View.VISIBLE
            }
            holder.checkBox.isChecked = present
        }
        holder.indexText.text = (position + 1).toString()
    }

    fun updateAssistance(newStudents: List<StudentData>) {
        val assistanceDiffCallback = AssistanceDiffCallback(students, newStudents)
        val diffResult = DiffUtil.calculateDiff(assistanceDiffCallback)
        students = newStudents
        diffResult.dispatchUpdatesTo(this@AssistanceAdapter)
    }

    override fun getItemCount(): Int = students.size

    inner class AssistanceShimmerViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    }

    inner class AssistanceViewHolder(
        view: View,
        cblistener: OnItemCheckListener,
        nListener: OnChangeNoteListener
    ) : RecyclerView.ViewHolder(view) {
        val indexText: TextView = view.findViewById(R.id.index_edit)
        val nameTextView: TextView = view.findViewById(R.id.name)
        val checkBox: CheckBox = view.findViewById(R.id.checkbox)
        val notaImageView: ImageView = view.findViewById(R.id.nota_image_view)
        val notaTextView: TextView = view.findViewById(R.id.noteTextView)

        init {
            view.setOnClickListener {
                val isChecked = !checkBox.isChecked
                checkBox.isChecked = isChecked
                cblistener.onItemCheck(adapterPosition, checkBox, checkBox.isChecked)
            }
            notaTextView.setOnClickListener {
                nListener.onChangeNote(
                    adapterPosition,
                    notaImageView,
                    notaTextView,
                    checkBox.isChecked
                )
            }
            notaImageView.setOnClickListener {
                nListener.onChangeNote(
                    adapterPosition,
                    notaImageView,
                    notaTextView,
                    checkBox.isChecked
                )
            }
            nameTextView.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                override fun afterTextChanged(s: Editable?) {
                    indexText.text = (adapterPosition + 1).toString()
                    //students[adapterPosition].index = adapterPosition + 1
                }
            })
        }
    }
}
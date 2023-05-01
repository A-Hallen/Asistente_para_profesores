package com.hallen.asistentedeprofesores.ui.view.adapters

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.hallen.asistentedeprofesores.R
import com.hallen.asistentedeprofesores.domain.model.StudentRegisters
import java.math.RoundingMode
import java.text.DecimalFormat
import kotlin.math.roundToInt

class AdapterRegistro(
    private var studentsData: List<StudentRegisters> = listOf(),
    var total: Int = 0, // Representa el total de dias que deberian estar presentes
): RecyclerView.Adapter<AdapterRegistro.ViewHolder>() {
    private val deciMalFormat = DecimalFormat("#.##").apply {
        roundingMode = RoundingMode.DOWN
    }

    override fun getItemCount(): Int = studentsData.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_registro, parent, false)
        return ViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = studentsData[position]
        val absences = if (total > 0) 100 - 100f * currentItem.assistance / total else 0f// porcentaje de aucencias
        setDangerBg(holder.container, absences.toInt()) // Cambiamos el fondo en base a las aucencias

        val listOfViews = listOf(holder.indexView, holder.nameText, holder.notaPromedio, holder.promedioAusencias)
        if (absences > 40){ // Si hay muchas aucencias el fondo es muy oscuro y
            setTextColor(listOfViews, Color.WHITE) // lo cambiamos a blanco
        } else setTextColor(listOfViews, Color.BLACK) // o a negro

        // Asignamos valores a las vistas
        holder.nameText.text  = currentItem.name
        val notas = currentItem.qualification
        holder.notaPromedio.text   = deciMalFormat.format(notas).takeIf { it != "0" } ?: "?"
        holder.promedioAusencias.text = absences.roundToInt().toString()
        holder.indexView.text = (position + 1).toString()
    }

    fun updateRegister(newStudents: List<StudentRegisters>){
        studentsData = newStudents
        notifyDataSetChanged()
    }

    private fun setTextColor(textViewList: List<TextView>, color: Int) {
        for (view in textViewList){
            setTextColor(view, color)
        }
    }

    private fun setTextColor(textView: TextView, color: Int){
        textView.setTextColor(color)
    }

    private fun setDangerBg(view: LinearLayout, percent: Int) {
        val red   = 255
        val blue  = if (percent > 80) 0 else 255 - 3 * percent
        val green = if (percent > 80) 0 else 255 - 3 * percent
        val color = Color.argb(255, red, green, blue)
        view.backgroundTintList = ColorStateList.valueOf(color)
    }

    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val indexView:         TextView = view.findViewById(R.id.index_edit)
        val nameText:          TextView = view.findViewById(R.id.name)
        val notaPromedio:      TextView = view.findViewById(R.id.nota_promedio)
        val promedioAusencias: TextView = view.findViewById(R.id.ausencias)
        val container:     LinearLayout = view.findViewById(R.id.container_registro)
    }
}
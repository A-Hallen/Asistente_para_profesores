package com.hallen.asistentedeprofesores.ui.view.adapters

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.hallen.asistentedeprofesores.R
import com.hallen.asistentedeprofesores.domain.model.Student

class InfiniteAdapter(var data: ArrayList<Pair<Int?, Student>>) : RecyclerView.Adapter<InfiniteAdapter.MyViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        // Inflamos la vista
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_student, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = data[position] // Item actual
        holder.textView.setText(currentItem.second.name) // Agregamos el nombre del alumno en el TextView
        holder.indexText.text = if (currentItem.first == null) (position + 1).toString() else {
            currentItem.first.toString()
        }
    }

    override fun getItemCount(): Int = data.size // retornamos el tamaño de la variable data.

    fun addNewData(newData: List<Pair<Int?, Student>>){
        data = newData as ArrayList<Pair<Int?, Student>>
        notifyDataSetChanged()
    }
    // Esta funcion agrega nuevos datos a la variable data, aumentando el número de items.
    fun addData(newData: List<Pair<Int?, Student>>) {
        val oldSize = data.size
        data.addAll(newData) // Añadimos los datos a la variable data
        notifyItemRangeInserted(oldSize, newData.size) // Notificamos los cambios en el adapter
    }

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val textView: EditText = view.findViewById(R.id.name) // El editText para cambiar o agregar el nombre del estudiante
        val indexText:TextView = view.findViewById(R.id.index_edit) // el textView donde se va a mostrar su índice

        init {
            textView.addTextChangedListener(object : TextWatcher { // listener para observar los cambios en el editText
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {  }
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {     }
                override fun afterTextChanged(s: Editable?) {
                    val text = textView.text.toString() // Actualizamos los cambios del edit el la variable data
                    val index = adapterPosition + 1 // Hacemos lo mismo con el índice
                    data[adapterPosition] = index to Student(name = text)
                }

            })
            textView.setOnFocusChangeListener { _, hasFocus -> // Listener para hacer más grande el editText y sea más facil escribir
                if (hasFocus){ // Se ejecuta si el editText tiene le foco
                    indexText.visibility = View.GONE // Hacemos invisible el TextView del índice
                } else {
                    indexText.visibility = View.VISIBLE // Hacemos visible el TextView del índice.
                }
            }
        }
    }
}
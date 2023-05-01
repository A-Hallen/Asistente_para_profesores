package com.hallen.asistentedeprofesores.ui.view.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.hallen.asistentedeprofesores.R

class AdapterExamplePrompt : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val exampleText: List<Pair<String, String>> = listOf(
        "Realiza un resumen en dos párrafos del siguiente texto ..."  // Texto
                to "Corrige las faltas de ortografía del siguiente párrafo ...",
        "Explicame como ocurren los procesos de división celular ..." // Célula
                to "realiza una comparación entre mitosis y meiosis.",
        "¿Cómo puedo mejorar el aprendizaje de mis alumnos?"          // Pedagogía
                to "¿Qué recursos pedagógicos me recomiendas para mi asignatura?",
        "¿Qué puedes y no puedes hacer?"                              // AI
                to "¿Qué ventajas tiene integrar a las IAs al proceso de enseñanza?"
    )

    interface OnItemClickListener {
        fun onItemClick(texts: Pair<String, String>)
    }

    private lateinit var clickListener: OnItemClickListener
    fun setOnItemClickListener(listener: OnItemClickListener) {
        clickListener = listener
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_example_openai_prompt, parent, false)
        return ExamplePromptViewHolder(view, clickListener)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val text1 = exampleText[position].first
        val text2 = exampleText[position].second
        val icon: Int = when (position) {
            0 -> R.drawable.openai_edit
            1 -> R.drawable.ic_openai_microscopio
            2 -> R.drawable.ic_openai_pedagogia
            else -> R.drawable.ic_openai_robot
        }

        (holder as ExamplePromptViewHolder).header.text = text1
        holder.iconImageView.background = ContextCompat.getDrawable(holder.itemView.context, icon)
        holder.content.text = text2
    }

    override fun getItemCount(): Int = exampleText.size
    inner class ExamplePromptViewHolder(view: View, clickListener: OnItemClickListener) :
        RecyclerView.ViewHolder(view) {
        val iconImageView: ImageView = view.findViewById(R.id.promptExampleIcon)
        val header: TextView = view.findViewById(R.id.promptExampleHeader)
        val content: TextView = view.findViewById(R.id.promptExampleText)

        init {
            view.setOnClickListener {
                clickListener.onItemClick(exampleText[adapterPosition])
            }
        }
    }
}
package com.hallen.asistentedeprofesores.ui.view.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.hallen.asistentedeprofesores.R
import com.hallen.asistentedeprofesores.domain.model.Event
import com.hallen.asistentedeprofesores.ui.view.adapters.diffs.EventDiffCallback
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class EventAdapter(
    private val itemLongClickListener: (Event, View) -> Unit,
    private val itemClickListener: (Event) -> Unit
): RecyclerView.Adapter<EventAdapter.EventViewHolder>() {
    private var listEvents: List<Event> = emptyList()
    private val littleFormat: DateFormat = SimpleDateFormat("hh:mm a", Locale.US)
    private val littleFormat2: DateFormat = SimpleDateFormat("hh:mm-a", Locale.US)

    private val colorTemplates = mapOf("Alta" to Color.parseColor("#ff0000"),
        "Media" to Color.parseColor("#0000ff"), "Baja" to Color.parseColor("#43AC00"))


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.event_list_item, parent, false)
        return EventViewHolder(view)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        val currentItem = listEvents[position]

        // Agregamos el contenido a las vistas
        val time = littleFormat2.parse(currentItem.hora) ?: return
        val color = colorTemplates[currentItem.priority] ?: return
        with(holder){
            hora.text        = littleFormat.format(time)
            titleView.text   = currentItem.title
            contentView.text = currentItem.details
            titleView.setTextColor(color)
        }
    }

    override fun getItemCount(): Int = listEvents.size

    fun updateEvent(events: List<Event>, requireActivity: FragmentActivity){
        CoroutineScope(Dispatchers.IO).launch {
            val eventDiffCallback = EventDiffCallback(listEvents, events)
            val diffResult = DiffUtil.calculateDiff(eventDiffCallback)
            requireActivity.runOnUiThread {
                listEvents = events
                diffResult.dispatchUpdatesTo(this@EventAdapter)
            }
        }
    }

    inner class EventViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val titleView: TextView = view.findViewById(R.id.event_title)
        val contentView: TextView = view.findViewById(R.id.event_content)
        val hora: TextView = view.findViewById(R.id.hora_event_item)

        init {
            view.setOnLongClickListener {
                itemLongClickListener(listEvents[adapterPosition], it)
                true
            }
            view.setOnClickListener {
                itemClickListener(listEvents[adapterPosition])
            }
        }
    }
}

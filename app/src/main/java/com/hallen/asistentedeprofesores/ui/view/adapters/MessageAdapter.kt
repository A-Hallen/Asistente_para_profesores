package com.hallen.asistentedeprofesores.ui.view.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.hallen.asistentedeprofesores.R
import com.hallen.asistentedeprofesores.domain.model.Message
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class MessageAdapter(var messageList: ArrayList<Message> = arrayListOf()): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var format = SimpleDateFormat("hh:mm:aa", Locale.US)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view: View
        return when (viewType){
            0 -> {
                view = LayoutInflater.from(parent.context).inflate(R.layout.sender, parent, false)
                UserMessageViewHolder(view)
            }
            1 -> {
                view = LayoutInflater.from(parent.context).inflate(R.layout.receiver, parent, false)
                BotMessageViewHolder(view)
            }
            else -> {
                view = LayoutInflater.from(parent.context).inflate(R.layout.item_chat_error, parent, false)
                ErrorMessageViewHolder(view)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val currentItem = messageList[position]
        when (currentItem.sender){
            "user" -> {
                (holder as UserMessageViewHolder).userMessageTv.text = messageList[position].message
                val color = if (currentItem.send) R.color.septimo else R.color.soft_gray
                holder.checkMark.setColorFilter(ContextCompat.getColor(holder.itemView.context, color))
                holder.timeTv.text = format.format(currentItem.time.toLong())
                val cardColor = if (currentItem.error) R.color.error else R.color.sender
                holder.container.setCardBackgroundColor(ContextCompat.getColor(holder.itemView.context, cardColor))
            }
            "bot" -> {
                (holder as BotMessageViewHolder).botMsgTv.text = messageList[position].message
                holder.timeTv.text = format.format(currentItem.time.toLong())
                val cardColor = if (currentItem.error) R.color.error else R.color.receiver
                holder.container.setCardBackgroundColor(ContextCompat.getColor(holder.itemView.context, cardColor))
            }
            "error" -> {
                (holder as ErrorMessageViewHolder).errorView.text = currentItem.message
            }
        }
    }

    override fun getItemCount(): Int = messageList.size

    override fun getItemViewType(position: Int): Int {
        return when(messageList[position].sender){
            "user" -> 0
            "bot" -> 1
            else -> 2
        }
    }

    fun update(mensajes: ArrayList<Message>) {
        messageList.clear()
        messageList.addAll(mensajes)
        notifyDataSetChanged()
    }

    fun updateMessage(message: Message) {
        val index = messageList.indexOfFirst { it.time == message.time }
        if (index >= 0){
            messageList[index] = message
            notifyItemChanged(index)
        }
    }

    fun addMessage(errorMessage: Message) {
        messageList.add(errorMessage)
        notifyItemInserted(itemCount)
    }

    class UserMessageViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val userMessageTv: TextView  = itemView.findViewById(R.id.text_content)
        val timeTv:        TextView  = itemView.findViewById(R.id.text_time)
        val checkMark:     ImageView = itemView.findViewById(R.id.checkMark)
        val container:      CardView = itemView.findViewById(R.id.lyt_parent)
    }

    class BotMessageViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val botMsgTv: TextView = itemView.findViewById(R.id.text_content )
        val timeTv:        TextView  = itemView.findViewById(R.id.text_time)
        val container:      CardView = itemView.findViewById(R.id.lyt_parent)
    }

    class ErrorMessageViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val errorView: TextView = itemView.findViewById(R.id.text_content)
    }
}
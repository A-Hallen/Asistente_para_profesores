package com.hallen.asistentedeprofesores.data.infraestructure.system

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val service1 = Intent(context, NotificationService::class.java)

        val date        = intent.getLongExtra("date", 0L)
        val titulo      = intent.getStringExtra("title")
        val descripcion = intent.getStringExtra("details")

        service1.putExtra("date", date)
        service1.putExtra("title", titulo)
        service1.putExtra("details", descripcion)

        ContextCompat.startForegroundService(context, service1)
    }
}
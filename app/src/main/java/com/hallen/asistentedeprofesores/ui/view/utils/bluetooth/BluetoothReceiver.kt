package com.hallen.asistentedeprofesores.ui.view.utils.bluetooth

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class BluetoothReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val intent1 = Intent(context, BluetoothService::class.java)
        if (intent == null || context == null) return

        if (intent.action != null) {
            intent1.putExtra("SEND", intent.action)
            context.startService(intent1)
        }
    }
}
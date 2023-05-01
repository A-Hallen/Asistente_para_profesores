package com.hallen.asistentedeprofesores.ui.view.utils.bluetooth

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import javax.inject.Inject

class BluetoothUtils @Inject constructor(private val context: Context) {
    operator fun invoke(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel1 = NotificationChannel(
                "CHANNEL_BLUETOOTH_1","ChannelBluetooth", NotificationManager.IMPORTANCE_HIGH)
            notificationChannel1.description = "Canal para peticiones bluetooth"
            val notificationManager: NotificationManager = context.getSystemService(
                NotificationManager::class.java)
            notificationManager.createNotificationChannel(notificationChannel1)
        }
    }
}

package com.hallen.asistentedeprofesores

import android.app.Application
import com.hallen.asistentedeprofesores.data.infraestructure.persistent.Prefs
import com.hallen.asistentedeprofesores.ui.view.utils.bluetooth.BluetoothUtils
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.Logger
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class Aplication: Application(){
    companion object {
        lateinit var prefs: Prefs
    }
    override fun onCreate() {
        super.onCreate()
        prefs = Prefs(applicationContext)
        BluetoothUtils(applicationContext)
        Logger.addLogAdapter(AndroidLogAdapter())
    }
}

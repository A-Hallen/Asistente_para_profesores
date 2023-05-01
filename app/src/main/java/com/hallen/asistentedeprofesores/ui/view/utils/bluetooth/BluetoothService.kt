package com.hallen.asistentedeprofesores.ui.view.utils.bluetooth

import android.annotation.SuppressLint
import android.app.Service
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothServerSocket
import android.bluetooth.BluetoothSocket
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import com.orhanobut.logger.Logger
import java.io.IOException
import java.lang.reflect.InvocationTargetException
import java.util.*

class BluetoothService: Service() {
    private val serviceBinder: IBinder = ServiceBinder() // Member
    private val uuid: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB") // UUID genérico para SPP
    private val nombre: String = "com.hallen.asistentedeprofesores.ui.view.utils.bluetooth.BluetoothService"
    private var serverSocket: BluetoothServerSocket? = null
    private var socket: BluetoothSocket? = null
    private var actionPlaying: ActionPlaying? = null // interface to handle click events in mainactivity and fragment
    private lateinit var bluetoothManager: BluetoothManager
    private lateinit var adapter:  BluetoothAdapter

    // class binder for clients
    inner class ServiceBinder : Binder() {
        fun getBluetoothService(): BluetoothService {
            return this@BluetoothService
        }
    }

    override fun onBind(intent: Intent?): IBinder{
        return serviceBinder
    }

    override fun onCreate() {
        super.onCreate()

        bluetoothManager = getSystemService(BLUETOOTH_SERVICE) as BluetoothManager
        adapter = bluetoothManager.adapter
    }

    @SuppressLint("MissingPermission")
    private fun startServer(){
        try {
            Logger.i("service starting...")
            serverSocket = adapter.listenUsingRfcommWithServiceRecord(nombre, uuid)
            println("Esperando conexión...")

            socket = serverSocket?.accept()
            println("Conexión establecida con ${socket?.remoteDevice?.name}")
            socket!!.outputStream.write("Hola cliente".toByteArray())
            if (actionPlaying != null) actionPlaying!!.showConnected()
            // Aquí puedes hacer lo que necesites con la conexión establecida

        } catch (e: IOException) {
            e.printStackTrace()
            stopSelf() // Paramos el proceso
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val actionName = intent?.getStringExtra("SEND") ?: return START_STICKY
        try {
            val outputStream = socket?.outputStream
            outputStream?.write(actionName.toByteArray())
        } catch (e: IOException){
            e.printStackTrace()
            cancelar()
            if (actionPlaying != null) actionPlaying!!.showDisconnected()
            stopSelf() // Paramos el proceso
        } catch (e: InvocationTargetException){
            e.printStackTrace()
            cancelar()
            if (actionPlaying != null) actionPlaying!!.showDisconnected()
            stopSelf() // Paramos el proceso
        }
        return START_STICKY
    }


    private fun cancelar() {
        try {
            socket?.close()
            serverSocket?.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun setCallback(actionPlaying: ActionPlaying?) {
        this.actionPlaying = actionPlaying
        object: Thread(){
            override fun run() {
                super.run()
                startServer()
            }
        }.start()
    }

}
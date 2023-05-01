package com.hallen.asistentedeprofesores.ui.view.utils.bluetooth

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothServerSocket
import android.bluetooth.BluetoothSocket
import java.io.IOException
import java.lang.reflect.InvocationTargetException
import java.util.*

@SuppressLint("MissingPermission")
class BluetoothServer(private val adapter: BluetoothAdapter, private val showConnected: () -> Unit, private val showDisconnected: () -> Unit) : Thread() {

    private val uuid: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB") // UUID genérico para SPP
    private val nombre: String = "com.hallen.asistentedeprofesores.ui.view.utils.bluetoot.BluetoothServer"
    private var serverSocket: BluetoothServerSocket? = null
    private var socket: BluetoothSocket? = null

    override fun run() {

        try {
            serverSocket = adapter.listenUsingRfcommWithServiceRecord(nombre, uuid)
            println("Esperando conexión...")

            socket = serverSocket?.accept()
            println("Conexión establecida con ${socket?.remoteDevice?.name}")
            socket!!.outputStream.write("Hola cliente".toByteArray())
            showConnected()
            // Aquí puedes hacer lo que necesites con la conexión establecida

        } catch (e: IOException) {
            e.printStackTrace()

        }
    }

    fun cancel() {
        try {
            socket?.close()
            serverSocket?.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun send(data: String) {
        if (socket != null && socket!!.isConnected){
            try {
                val outputStream = socket?.outputStream
                outputStream?.write(data.toByteArray())
            } catch (e: IOException){
                e.printStackTrace()
                cancel()
                showDisconnected()
            } catch (e: InvocationTargetException){
                e.printStackTrace()
                cancel()
                showDisconnected()
            }
        }
    }
}
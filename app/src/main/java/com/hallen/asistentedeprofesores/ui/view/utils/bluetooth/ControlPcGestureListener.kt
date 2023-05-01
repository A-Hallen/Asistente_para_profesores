package com.hallen.asistentedeprofesores.ui.view.utils.bluetooth

import android.content.Context
import android.content.Intent
import android.view.GestureDetector
import android.view.MotionEvent
import android.widget.ImageView
import androidx.core.content.ContextCompat
import com.hallen.asistentedeprofesores.R

class ControlPcGestureListener(private val context: Context, private val touch: ImageView): GestureDetector.OnGestureListener, GestureDetector.OnDoubleTapListener {
    var actionPointer2Triggered = false
    var actionShowPressTrigerred = false
    private lateinit var motionEventLongPress: MotionEvent

    override fun onDown(p0: MotionEvent): Boolean {
        // intent to broadcast receiver
        val clickIntent = Intent(context, BluetoothReceiver::class.java).setAction("DOWN, 0, 0")
        context.sendBroadcast(clickIntent) // Now send the
        return true
    }

    override fun onShowPress(p0: MotionEvent) {    }

    override fun onSingleTapUp(p0: MotionEvent): Boolean = true

    override fun onScroll(motionEvent: MotionEvent, motionEvent2: MotionEvent, x_: Float, y_: Float): Boolean {
        if (actionPointer2Triggered) return true
        return motion(motionEvent2, motionEvent)
    }

    override fun onLongPress(motionEvent: MotionEvent) {
        actionShowPressTrigerred = true
        motionEventLongPress = motionEvent
        val data = "START_DRAG,0,0" // Data to be send to PC
        // intent to broadcast receiver
        val clickIntent = Intent(context, BluetoothReceiver::class.java).setAction(data)
        context.sendBroadcast(clickIntent) // Now send the
        touch.background = ContextCompat.getDrawable(context, R.drawable.control_pc_touch_drag_bg)
    }

    override fun onFling(p0: MotionEvent, p1: MotionEvent, p2: Float, p3: Float): Boolean {
        if (!actionPointer2Triggered) return true

        val velocity = p3.toInt() / 1000 * 3

        val data = "SCROLL,$velocity,"

        // intent to broadcast receiver
        val clickIntent = Intent(context, BluetoothReceiver::class.java).setAction(data)
        context.sendBroadcast(clickIntent) // Now send the broadcast

        return true
    }

    override fun onSingleTapConfirmed(motionEvent: MotionEvent): Boolean {
        val percents = getPercent(motionEvent)
        val data = "CLICK,${percents.first},${percents.second}" // Data to be send to PC

        // intent to broadcast receiver
        val clickIntent = Intent(context, BluetoothReceiver::class.java).setAction(data)
        context.sendBroadcast(clickIntent) // Now send the broadcast
        return true
    }

    override fun onDoubleTap(motionEvent: MotionEvent): Boolean {
        val percents = getPercent(motionEvent)
        val data = "DOUBLE_CLICK,${percents.first},${percents.second}" // Data to be send to PC

        // intent to broadcast receiver
        val clickIntent = Intent(context, BluetoothReceiver::class.java).setAction(data)
        context.sendBroadcast(clickIntent) // Now send the broadcast
        return true
    }

    override fun onDoubleTapEvent(p0: MotionEvent): Boolean = true

    fun drag(motionEvents: MotionEvent): Boolean {
        return motion(motionEvents, motionEventLongPress)
    }

    private fun getPercent(motionEvent: MotionEvent): Pair<Double, Double> {
        // Click detected
        val x = motionEvent.x // x chords of the event
        val y = motionEvent.y // y chords of the event

        val percentX = 100.0 * x / touch.width  // Percent of x in the view
        val percentY = 100.0 * y / touch.height // Percent of y in the view
        return Pair(percentX, percentY)
    }

    private fun motion(motionEvent2: MotionEvent, motionEvent1: MotionEvent): Boolean{

        val x = motionEvent2.x - motionEvent1.x
        val y = motionEvent2.y - motionEvent1.y

        val percentX = x / touch.width  // Percent of x in the view
        val percentY = y / touch.height // Percent of y in the view

        val data = "MOTION,$percentX,$percentY" // Data to be send to PC

        // intent to broadcast receiver
        val clickIntent = Intent(context, BluetoothReceiver::class.java).setAction(data)
        context.sendBroadcast(clickIntent) // Now send the
        return true
    }
}
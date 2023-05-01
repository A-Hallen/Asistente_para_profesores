package com.hallen.asistentedeprofesores.ui.view.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.graphics.*
import android.graphics.drawable.Drawable
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.RectShape
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.view.*
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.hallen.asistentedeprofesores.R
import com.hallen.asistentedeprofesores.databinding.FragmentControlPcBinding
import com.hallen.asistentedeprofesores.ui.view.utils.bluetooth.ActionPlaying
import com.hallen.asistentedeprofesores.ui.view.utils.bluetooth.BluetoothReceiver
import com.hallen.asistentedeprofesores.ui.view.utils.bluetooth.BluetoothService
import com.hallen.asistentedeprofesores.ui.view.utils.bluetooth.ControlPcGestureListener
import com.orhanobut.logger.Logger

class ControlPc : Fragment(), ServiceConnection, ActionPlaying {
    private lateinit var binding: FragmentControlPcBinding
    private lateinit var gestureDetector: GestureDetector
    private var bluetoothService: BluetoothService? = null
    private val gestureListener by lazy {
        ControlPcGestureListener(requireContext(), binding.touch)
    }
    private var drawableState = false
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentControlPcBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val context = requireContext()
        binding.imageView.setOnClickListener {
            drawableState = !drawableState
            if (drawableState) {
                changeImageState(drawableState)
                connectWithPermission()
                startAnimation()
            } else stopSearch()
        }
        binding.left.setOnClickListener  {
            val deleteIntent = Intent(context, BluetoothReceiver::class.java).setAction("left")
            context.sendBroadcast(deleteIntent)
        }

        binding.right.setOnClickListener {
            val rightIntent = Intent(context, BluetoothReceiver::class.java).setAction("right")
            context.sendBroadcast(rightIntent)
        }
        val bitmap = getBitmapFromVectorDrawable(R.drawable.control_pc_touchpad_texture)
        val shader: Shader = BitmapShader(bitmap, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT)
        val paint = Paint()
        paint.shader = shader
        val shapeDrawable = ShapeDrawable(RectShape())
        shapeDrawable.paint.set(paint)
        binding.container.background = shapeDrawable
        setListener()
    }

    private fun stopSearch() {
        drawableState = false
        changeImageState(false)
        bluetoothService?.stopSelf()
        binding.imageView.visibility = View.VISIBLE
        binding.animatedShadow.visibility = View.VISIBLE
        stopAnimation()
        binding.container.visibility = View.GONE

    }

    private fun error(){
        Toast.makeText(requireContext(), "A ocurrido un error", Toast.LENGTH_SHORT).show()
        stopSearch()
    }

    private fun stopAnimation(){
        binding.animatedShadow.clearAnimation()
    }

    private fun startAnimation() {
        val context = requireContext()
        val animation = AnimationUtils.loadAnimation(context, R.anim.animacion_luz)
        binding.animatedShadow.animation = animation
        val layoutParams = binding.animatedShadow.layoutParams
        binding.animatedShadow.layoutParams = layoutParams
        animation.interpolator = AnimationUtils.loadInterpolator(context, android.R.anim.accelerate_decelerate_interpolator)
        animation.start()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setListener() {
        gestureDetector = GestureDetector(gestureListener)
        val context = requireContext()
        binding.touch.setOnTouchListener { _, motionEvent ->
            if (gestureListener.actionShowPressTrigerred){
                if (motionEvent.action == MotionEvent.ACTION_MOVE){
                    return@setOnTouchListener gestureListener.drag(motionEvent)
                }
                if (motionEvent.action == MotionEvent.ACTION_UP){
                    val clickIntent = Intent(context, BluetoothReceiver::class.java).setAction("END_DRAG, 0, 0")
                    context.sendBroadcast(clickIntent) // Now send the
                    binding.touch.setBackgroundColor(ContextCompat.getColor(context, R.color.transparent))
                }
            }
            if (motionEvent.action == MotionEvent.ACTION_POINTER_2_DOWN){
                gestureListener.actionPointer2Triggered = true
            }
            val result = gestureDetector.onTouchEvent(motionEvent)
            if (motionEvent.action == MotionEvent.ACTION_UP){
                gestureListener.actionPointer2Triggered = false; gestureListener.actionShowPressTrigerred = false
            }
            return@setOnTouchListener result
        }
    }

    private fun getBitmapFromVectorDrawable(touchpadTexture: Int): Bitmap {
        val drawable: Drawable? = ContextCompat.getDrawable(requireContext(), touchpadTexture)
        val bitmap = Bitmap.createBitmap(drawable!!.intrinsicWidth, drawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return bitmap
    }

    private var requestBluetooth = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == AppCompatActivity.RESULT_OK) {
            startTheService() //granted
        }else{
            //deny
        }
    }
    private fun connectWithPermission(){
        val context = requireContext()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(requireActivity(), arrayOf(
                    Manifest.permission.BLUETOOTH_CONNECT,
                    Manifest.permission.BLUETOOTH_SCAN
                ), 1)
            } else startTheService()

        }else{
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            requestBluetooth.launch(enableBtIntent)
        }
    }

    private fun startTheService() {
        val context = requireContext()
        val intent = Intent(context, BluetoothService::class.java)
        val intentForMcuService = Intent()
        val canonicalName = BluetoothService::class.java.name
        intentForMcuService.component = ComponentName(canonicalName,
            "$canonicalName.BluetoothService"
        )

        val bind = context.bindService(intent, this, 0)
        if (!bind){
            context.bindService(intent, this, 0)
        } else {
            context.startService(intent)
        }
    }

    private fun changeImageState(state: Boolean){
        binding.imageView.text = if (state) "Stop" else "Start"
    }

    override fun onServiceConnected(p0: ComponentName?, service: IBinder?) {
        Logger.i("onServiceConnected")
        val binder = service as BluetoothService.ServiceBinder
        bluetoothService = binder.getBluetoothService()
        bluetoothService?.setCallback(this)
    }

    override fun onServiceDisconnected(p0: ComponentName?) {
        println("onServiceDisconnected")
    }

    override fun showConnected() {
        requireActivity().runOnUiThread {
            binding.animatedShadow.visibility = View.GONE
            binding.imageView.visibility = View.GONE
            binding.container.visibility = View.VISIBLE
        }
    }

    override fun showDisconnected() = error()
}
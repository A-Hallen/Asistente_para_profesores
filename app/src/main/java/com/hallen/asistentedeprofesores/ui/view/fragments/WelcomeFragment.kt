package com.hallen.asistentedeprofesores.ui.view.fragments

import android.content.Intent
import android.graphics.LinearGradient
import android.graphics.Shader
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.hallen.asistentedeprofesores.Aplication.Companion.prefs
import com.hallen.asistentedeprofesores.R
import com.hallen.asistentedeprofesores.databinding.FragmentWelcome1Binding
import com.hallen.asistentedeprofesores.databinding.FragmentWelcome2Binding
import com.hallen.asistentedeprofesores.databinding.FragmentWelcome3Binding
import com.hallen.asistentedeprofesores.databinding.FragmentWelcome4Binding
import com.hallen.asistentedeprofesores.ui.view.activities.MainActivity

class WelcomeFragment : Fragment() {
    private val position: Int by lazy {
        arguments?.getInt("position") ?: 0
    }
    private var textView: TextView? = null
    private var button: TextView? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return when(position){
            0 -> {
                val binding = FragmentWelcome1Binding.inflate(inflater, container, false)
                binding.root
            }
            1 -> {
                val binding = FragmentWelcome2Binding.inflate(inflater, container, false)
                textView = binding.horarioHeader
                binding.root
            }
            2 -> {
                val binding = FragmentWelcome3Binding.inflate(inflater, container, false)
                textView = binding.eventsHeader
                binding.root
            }
            else -> {
                val binding = FragmentWelcome4Binding.inflate(inflater, container, false)
                textView = binding.notasHeader
                button = binding.welcomeStart
                binding.root
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (textView != null) setupTextView()
        button?.setOnClickListener{
            prefs.setFirstStart()
            startActivity(Intent(requireContext(), MainActivity::class.java))
            try {
                requireActivity().finish()
            } catch (e: Exception){}
        }
    }

    private fun setupTextView() {
        val paint = textView!!.paint
        val context = requireContext()
        val width = paint.measureText(textView!!.text.toString())
        val textShader = LinearGradient(
            0f, 0f, width, textView!!.textSize,
            intArrayOf(
                ContextCompat.getColor(context, R.color.primero),
                ContextCompat.getColor(context, R.color.octavo),
                ContextCompat.getColor(context, R.color.segundo)
            ), null, Shader.TileMode.CLAMP
        )
        textView!!.paint.shader = textShader
    }
}
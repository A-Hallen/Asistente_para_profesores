package com.hallen.asistentedeprofesores.ui.view.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.hallen.asistentedeprofesores.databinding.ActivityWelcomeBinding
import com.hallen.asistentedeprofesores.ui.view.adapters.WelcomeAdapter

class WelcomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityWelcomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWelcomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        loadWelcome()
    }

    private fun loadWelcome() {
        val homeAdapter = WelcomeAdapter(supportFragmentManager, lifecycle) //Crea un adaptador para el pager
        binding.welcomePager.adapter = homeAdapter
    }
}
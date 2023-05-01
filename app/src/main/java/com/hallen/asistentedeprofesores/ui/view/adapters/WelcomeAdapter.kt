package com.hallen.asistentedeprofesores.ui.view.adapters

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.hallen.asistentedeprofesores.ui.view.fragments.WelcomeFragment

class WelcomeAdapter (
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle

): FragmentStateAdapter(fragmentManager, lifecycle) {

    override fun getItemCount(): Int = 4

    override fun createFragment(position: Int): Fragment {
        val bundle = Bundle()
        bundle.putInt("position", position)
        return WelcomeFragment().also { it.arguments = bundle }
    }


}
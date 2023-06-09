package com.hallen.asistentedeprofesores.ui.view.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.hallen.asistentedeprofesores.ui.view.fragments.FragmentEvents
import com.hallen.asistentedeprofesores.ui.view.fragments.FragmentHorario
import com.hallen.asistentedeprofesores.ui.view.fragments.FragmentNotas

class PagerAdapter(
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle

): FragmentStateAdapter(fragmentManager, lifecycle) {


    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment {
        return when(position){
            0 -> FragmentHorario()
            1 -> FragmentEvents()
            2 -> FragmentNotas()
            else -> FragmentHorario()
        }

    }


}
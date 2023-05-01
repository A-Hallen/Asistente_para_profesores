package com.hallen.asistentedeprofesores.ui.view.adapters

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.hallen.asistentedeprofesores.ui.view.fragments.AssistanceFragment
import com.hallen.asistentedeprofesores.ui.view.fragments.FragmentGrafic
import com.hallen.asistentedeprofesores.ui.view.fragments.FragmentRegisters

class GroupPagerAdapter(
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle,
    private val groupId: Int
): FragmentStateAdapter(fragmentManager, lifecycle) {
    override fun getItemCount(): Int = 3
    private val bundle  by lazy {
        Bundle().also { it.putInt("groupId", groupId) }
    }
    override fun createFragment(position: Int): Fragment = when(position){
        0 -> AssistanceFragment()
        1 -> FragmentRegisters()
        else -> FragmentGrafic()
    }.apply { arguments = bundle }

}

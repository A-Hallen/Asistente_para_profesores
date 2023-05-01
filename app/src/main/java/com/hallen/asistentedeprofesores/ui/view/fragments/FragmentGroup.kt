package com.hallen.asistentedeprofesores.ui.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayoutMediator
import com.hallen.asistentedeprofesores.R
import com.hallen.asistentedeprofesores.databinding.FragmentGroupBinding
import com.hallen.asistentedeprofesores.ui.view.adapters.GroupPagerAdapter

class FragmentGroup : Fragment() {
    private lateinit var binding: FragmentGroupBinding
    private val groupId: Int by lazy {
        arguments?.getInt("groupId") ?: 0
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentGroupBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViewPager()
    }

    private fun setupViewPager() {
        val fragmentsArray = arrayOf("Grupo", "Registro", "Gráficas")
        val pagerAdapter = GroupPagerAdapter(requireActivity().supportFragmentManager, lifecycle, groupId)
        binding.pagerGroup.offscreenPageLimit = 2
        binding.pagerGroup.adapter = pagerAdapter


        TabLayoutMediator(binding.tabLayoutGroup, binding.pagerGroup){  tab, position ->
            tab.setCustomView(R.layout.tab_custom_view)
            tab.text = fragmentsArray[position]
        }.attach()
    }
}
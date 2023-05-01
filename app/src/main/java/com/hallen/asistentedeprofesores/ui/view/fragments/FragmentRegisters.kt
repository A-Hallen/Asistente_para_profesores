package com.hallen.asistentedeprofesores.ui.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.hallen.asistentedeprofesores.databinding.FragmentRegistersBinding
import com.hallen.asistentedeprofesores.domain.model.StudentRegisters
import com.hallen.asistentedeprofesores.ui.view.adapters.AdapterRegistro
import com.hallen.asistentedeprofesores.ui.viewmodel.GroupViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FragmentRegisters : Fragment() {
    private lateinit var binding: FragmentRegistersBinding
    private val groupViewModel: GroupViewModel by activityViewModels()
    private lateinit var adapter: AdapterRegistro

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRegistersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()

        groupViewModel.groupRegisterModel.observe(viewLifecycleOwner){ register ->
            val registros = groupViewModel.studentDataModel.value?.map { student ->
                register.find { it.id == student.id } ?: StudentRegisters(
                    student.id,
                    student.name
                )
            }
            if (registros != null) adapter.updateRegister(registros)
        }
        groupViewModel.totalDays.observe(viewLifecycleOwner){
            adapter.total = it
        }
    }

    private fun setupRecyclerView() {
        adapter = AdapterRegistro()
        binding.registroRecyclerView.layoutManager = LinearLayoutManager(context)
        binding.registroRecyclerView.adapter = adapter
    }
}
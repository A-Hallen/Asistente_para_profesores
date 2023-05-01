package com.hallen.asistentedeprofesores.ui.view.fragments

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import android.widget.FrameLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hallen.asistentedeprofesores.R
import com.hallen.asistentedeprofesores.databinding.DialogGroupNameBinding
import com.hallen.asistentedeprofesores.databinding.DialogWarningBinding
import com.hallen.asistentedeprofesores.databinding.FragmentCreateGroupBinding
import com.hallen.asistentedeprofesores.domain.model.Student
import com.hallen.asistentedeprofesores.ui.view.adapters.InfiniteAdapter
import com.hallen.asistentedeprofesores.ui.viewmodel.GroupViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class FragmentCreateGroup : Fragment() {
    private lateinit var binding: FragmentCreateGroupBinding
    private val groupViewModel: GroupViewModel by activityViewModels()
    private var data: ArrayList<Pair<Int?, Student>> = arrayListOf()
    private val groupId: Int by lazy {
        arguments?.getInt("groupId") ?: 0
    }
    private lateinit var adapter: InfiniteAdapter
    private var isLoading = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        if (groupId != 0){
            groupViewModel.studentDataModel.observe(viewLifecycleOwner){ dataStudents ->
                adapter.addNewData(dataStudents.map { null to  Student(it.id, it.name, it.id_group) })
                adapter.notifyDataSetChanged()
            }
        }
        binding = FragmentCreateGroupBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (groupId == 0)  for (i in 0..10) data.add(null to Student()) else {
            requireActivity().title = "Editar " + groupViewModel.groupNameModel.value
        }
        adapter = InfiniteAdapter(data)
        val layoutManager = LinearLayoutManager(context)
        binding.recyclerView.layoutManager = layoutManager
        binding.recyclerView.adapter = adapter
        binding.recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val visibleItemCount = layoutManager.childCount
                val totalItemCount = layoutManager.itemCount
                val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()

                if (!isLoading) {
                    if (visibleItemCount + firstVisibleItemPosition >= totalItemCount && firstVisibleItemPosition >= 0) {
                        loadMoreItems()
                    }
                }
            }
        })
        binding.guardar.setOnClickListener {
            val dialog = Dialog(requireContext())
            val dialogBinding = DialogGroupNameBinding.inflate(dialog.layoutInflater)
            dialog.apply {
                setContentView(dialogBinding.root)
                window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                val layoutParams = WindowManager.LayoutParams()
                layoutParams.apply {
                    copyFrom(window!!.attributes)
                    width = WindowManager.LayoutParams.MATCH_PARENT
                    height = WindowManager.LayoutParams.WRAP_CONTENT
                    gravity = Gravity.CENTER
                }
                window!!.attributes = layoutParams
                dialogBinding.dialogGroupName
                if (groupId != 0) dialogBinding.dialogGroupName.setText(groupViewModel.groupNameModel.value ?: "")
                dialogBinding.cancelButton.setOnClickListener {     dialog.dismiss()    }
                dialogBinding.okButton.setOnClickListener {
                    if (dialogBinding.dialogGroupName.text.isBlank()){
                        Toast.makeText(requireContext(), "El nombre del grupo no debe estar vacío", Toast.LENGTH_SHORT).show()
                        return@setOnClickListener
                    }
                    updateData(dialogBinding.dialogGroupName.text.toString())
                    dialog.dismiss()
                }
                dialog.show()
            }
        }
    }

    private fun updateData(name: String) {
        //Guardar en la base de datos
        val students = adapter.data.filter { it.second.name.isNotBlank() }
            .map { it.second.copy(name = it.second.name.replaceFirstChar { first -> first.uppercase() }) }
            .sortedBy { it.name }
        with(groupViewModel){
            if (groupId == 0) {
                saveGroup(students, name)
                findNavController().navigate(R.id.homeViewPagerFragment)
            } else {
                val dialog = Dialog(requireContext())
                val dialogBinding = DialogWarningBinding.inflate(dialog.layoutInflater)
                dialog.apply {
                    setContentView(dialogBinding.root)
                    window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                    dialogBinding.root.layoutParams = FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT)
                    dialogBinding.warningHeader.text = "Estás seguro de editar el grupo: $name?"
                    dialogBinding.cancelButton.setOnClickListener {     dialog.dismiss()    }
                    dialogBinding.okButton.setOnClickListener {
                        updateGroup(students, name, groupId)
                        dialog.dismiss()
                        findNavController().navigate(R.id.homeViewPagerFragment)
                    }
                    dialog.show()
                }
            }
        }
    }

    private fun loadMoreItems() {
        isLoading = true

        // Aquí se llamaría a una función que carga más datos en background
        // Una vez que se cargan los nuevos datos, se llamaría a la función "onLoadMoreItemsComplete"
        // con los nuevos datos.
        CoroutineScope(Dispatchers.IO).launch {
            val data = arrayListOf<Pair<Int?, Student>>()
            for (i in 0..10){
                data.add(null to Student())
            }
            withContext(Dispatchers.Main) {
                adapter.addData(data)
                isLoading = false
            }
        }
    }


}
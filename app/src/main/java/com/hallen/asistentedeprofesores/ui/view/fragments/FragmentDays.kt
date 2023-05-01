package com.hallen.asistentedeprofesores.ui.view.fragments

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.ContextMenu
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.FrameLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.hallen.asistentedeprofesores.R
import com.hallen.asistentedeprofesores.databinding.FragmentDaysBinding
import com.hallen.asistentedeprofesores.databinding.YesOrNoDialogBinding
import com.hallen.asistentedeprofesores.domain.model.Day
import com.hallen.asistentedeprofesores.ui.view.adapters.AdapterDays
import com.hallen.asistentedeprofesores.ui.viewmodel.GroupViewModel
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class FragmentDays : Fragment() {
    private lateinit var binding: FragmentDaysBinding
    private val groupViewModel: GroupViewModel by activityViewModels()
    private val dateFormat: DateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.US)
    private lateinit var adapter: AdapterDays
    private val groupId: Int? by lazy {  arguments?.getInt("groupId")  }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentDaysBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        binding.newDayButton.setOnClickListener {   addNewDay()   }
        groupViewModel.daysInDatabase.observe(viewLifecycleOwner){ days ->
            val sortedDays = days.sortedBy { dateFormat.parse(it.date) }
            adapter.days = sortedDays
            adapter.total = groupViewModel.studentDataModel.value?.size ?: 0
            adapter.notifyDataSetChanged()
        }
        groupViewModel.getDays(groupId!!)
    }

    private fun addNewDay() {
        val dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.date_picker_dialog)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        val cancelButton: TextView   = dialog.findViewById(R.id.cancel_button)
        val datePicker: DatePicker = dialog.findViewById(R.id.date_picker)
        val acceptButton:  TextView   = dialog.findViewById(R.id.ok_button)

        cancelButton.setOnClickListener {   dialog.dismiss()    }
        acceptButton.setOnClickListener {
            val fecha = "${datePicker.dayOfMonth}-${datePicker.month + 1}-${datePicker.year}"
            val date = dateFormat.parse(fecha) ?: return@setOnClickListener
            groupViewModel.date.value = dateFormat.format(date)
            findNavController().navigateUp()
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun setupRecyclerView() {
        binding.daysRecyclerView.layoutManager = LinearLayoutManager(context)
        adapter = AdapterDays()
        binding.daysRecyclerView.adapter = adapter

        adapter.setOnItemClickListener(object : AdapterDays.OnItemClickListener{
            override fun onItemClick(day: Day) {
                groupViewModel.getStudentData(groupId!!, day.date)
                groupViewModel.date.value = day.date; findNavController().navigateUp()
            }
        })
//
        adapter.setOnItemLongClickListener(object : AdapterDays.OnItemLongClickListener{
            override fun onItemLongClick(day: Day, menu: ContextMenu) {
                menu.add(0, 0, 0, "Eliminar")
                menu.findItem(0).setOnMenuItemClickListener {
                    deleteDay(day)
                    true
                }
            }

            private fun deleteDay(day: Day) {
                val dialog = Dialog(requireContext())
                val dialogBinding = YesOrNoDialogBinding.inflate(dialog.layoutInflater)
                dialog.apply {
                    setContentView(dialogBinding.root)
                    window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                    dialogBinding.root.layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT)
                    groupViewModel.date.value = day.date
                    dialogBinding.dialogTextWarning.text =
                        "Seguro que deseas eliminar los registros del día ${day.date}?"
                    dialogBinding.dialogSubtitle.text = "Se eliminaran permanentemente los registros de este día para el grupo ${groupViewModel.groupNameModel.value}"
                    dialogBinding.cancelButton.setOnClickListener { dismiss() }
                    dialogBinding.okButton.setOnClickListener {
                        groupViewModel.deleteDate(day.date, groupId!!); dismiss()
                    }
                    show()
                }
            }
        })
    }


}
package com.hallen.asistentedeprofesores.ui.view.fragments

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.hallen.asistentedeprofesores.R
import com.hallen.asistentedeprofesores.databinding.FragmentAssistanceBinding
import com.hallen.asistentedeprofesores.databinding.YesOrNoDialogBinding
import com.hallen.asistentedeprofesores.domain.model.Assistance
import com.hallen.asistentedeprofesores.domain.model.StudentData
import com.hallen.asistentedeprofesores.ui.view.adapters.AssistanceAdapter
import com.hallen.asistentedeprofesores.ui.view.customviews.BubblePopupMenu
import com.hallen.asistentedeprofesores.ui.viewmodel.GroupViewModel
import com.orhanobut.logger.Logger
import dagger.hilt.android.AndroidEntryPoint
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*


@AndroidEntryPoint
class AssistanceFragment : Fragment() {
    private lateinit var binding: FragmentAssistanceBinding
    private val groupViewModel: GroupViewModel by activityViewModels()
    private lateinit var adapter: AssistanceAdapter
    private val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.US)
    private lateinit var date: String
    private val groupId: Int by lazy {
        arguments?.getInt("groupId") ?: 0
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val navController = findNavController()
        navController.addOnDestinationChangedListener(object : NavController.OnDestinationChangedListener{
            override fun onDestinationChanged(
                controller: NavController,
                destination: NavDestination,
                arguments: Bundle?
            ) {
                if (arguments?.getInt("groupId") != groupId){
                    groupViewModel.date.removeObservers(viewLifecycleOwner)
                    navController.removeOnDestinationChangedListener(this)
                }
            }
        })
        date = arguments?.getString("date") ?: dateFormat.format(Date())
        groupViewModel.date.value = date
        binding = FragmentAssistanceBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        groupViewModel.date.observe(viewLifecycleOwner){
            Logger.i("DATE: $groupId")
            adapter.date = it
            date = it
            binding.topFecha.text = DateFormat.getDateInstance().format(dateFormat.parse(date)!!)
            groupViewModel.getStudentData(groupId, date)
        }

        val exampleData = ArrayList<StudentData>()
        for (i in 0..20) exampleData.add(StudentData())
        setUpRecyclerView(exampleData)

        groupViewModel.studentDataModel.observe(viewLifecycleOwner){
            if (it.isEmpty()){
                adapter.updateAssistance(exampleData)
                showShimmer()
            } else {
                hideShimmer()
                adapter.updateAssistance(it)
            }
        }
        configMenu()

    }

    private fun showShimmer() {
        binding.assistanceShimmer.showShimmer(true)
        binding.assistanceShimmer.startShimmer()
    }

    private fun hideShimmer(){
        binding.assistanceShimmer.hideShimmer()
        binding.assistanceShimmer.stopShimmer()
    }

    private fun setUpRecyclerView(exampleData: ArrayList<StudentData>) {
        adapter = AssistanceAdapter(date, exampleData)
        val layoutManager = LinearLayoutManager(context)
        binding.asistanceRecyclerView.layoutManager = layoutManager
        binding.asistanceRecyclerView.adapter = adapter
        binding.assistanceShimmer.startShimmer()
        binding.topFecha.setOnClickListener {
            val bundle = Bundle()
            bundle.putInt("groupId", groupId)
            findNavController().navigate(R.id.fragmentDays, bundle)
        }

        adapter.setOnItemCheckListener(object : AssistanceAdapter.OnItemCheckListener {
            override fun onItemCheck(position: Int, checkBox: CheckBox, isChecked: Boolean) {
                val student = adapter.students[position]
                val present = if (isChecked) 1 else 0
                val newAsistencia = Assistance(
                    id = student.asistencia?.id ?: 0,
                    id_student = student.id,
                    date = adapter.date,
                    assistance = present,
                    qualification = student.asistencia?.qualification ?: 0,
                )
                adapter.students[position].asistencia = newAsistencia
                groupViewModel.setAssistance(student.id, date, present, student.asistencia?.qualification ?: 0)
            }
        })
        adapter.setOnChangeNoteListener(object : AssistanceAdapter.OnChangeNoteListener{
            override fun onChangeNote(
                position: Int,
                button: ImageView,
                textView: TextView,
                checked: Boolean
            ) {
                val popupMenu = BubblePopupMenu(requireContext(), button, object : BubblePopupMenu.PopupMenuCustomOnClickListener {
                    override fun onClick(index: Int, view: View) {
                        val nota = if (index == 0){
                            textView.visibility = View.GONE; button.visibility = View.VISIBLE
                            0
                        } else {
                            textView.visibility = View.VISIBLE; button.visibility = View.GONE
                            (view as TextView).text.also {
                                textView.text = it
                            }.toString().toInt()
                        }
                        val student = adapter.students[position]
                        val present = if (checked) 1 else 0
                        val newAsistencia = Assistance(
                            id = student.asistencia?.id ?: 0,
                            id_student = student.id,
                            date = adapter.date,
                            assistance = present,
                            qualification = nota
                        )
                        adapter.students[position].asistencia = newAsistencia
                        groupViewModel.setAssistance(student.id, date, present, nota)
                    }
                })
                popupMenu.show()
            }
        })
    }

    private val menuProvider = object : MenuProvider {
        override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
            menuInflater.inflate(R.menu.menu_group_options, menu)
        }
        override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
            val n = findNavController()
            if (menuItem.itemId == R.id.menu_edit) editGroup(n) else deleteGroup(n)
            return true
        }
    }
    private val destinationListener = object : NavController.OnDestinationChangedListener{
        override fun onDestinationChanged(
            controller: NavController,
            destination: NavDestination,
            arguments: Bundle?
        ) {
            if (destination.id != groupId && destination.id != R.id.fragmentCreateGroup && destination.id != R.id.fragmentDays){
                controller.removeOnDestinationChangedListener(this)
                requireActivity().removeMenuProvider(menuProvider)
            }
        }
    }
    private fun configMenu(){
        requireActivity().addMenuProvider(menuProvider)
        findNavController().addOnDestinationChangedListener(destinationListener)
    }
    private fun editGroup(navController: NavController) {
        val bundle = Bundle().apply { putInt("groupId", groupId) }
        navController.navigate(R.id.fragmentCreateGroup, bundle)
    }
    private fun deleteGroup(navController: NavController) {
        // Lógica para eliminar el elemento
        val groupName = groupViewModel.groupNameModel.value
        val context = requireContext()
        val dialog = Dialog(context)
        val dialogBinding = YesOrNoDialogBinding.inflate(dialog.layoutInflater)
        dialog.apply {
            setContentView(dialogBinding.root)
            with(dialogBinding){
                window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                root.layoutParams = FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT)
                dialogTextWarning.text = getString(R.string.eliminar_template, groupName)
                dialogSubtitle.text = getString(R.string.warning_delete_group)
                cancelButton.setOnClickListener {   dismiss()   }
                okButton.setOnClickListener{ // El grupo a sido eliminado correctamente
                    groupViewModel.deleteGroup(groupId)
                    Toast.makeText(context, "$groupName a sido eliminado",
                        Toast.LENGTH_SHORT).show()
                    dismiss()
                    navController.navigate(navController.graph.id)
                }
            }
        }.show()
    }
}
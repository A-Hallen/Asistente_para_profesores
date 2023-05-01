package com.hallen.asistentedeprofesores.ui.view.fragments

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.hallen.asistentedeprofesores.R
import com.hallen.asistentedeprofesores.databinding.FragmentGraficBinding
import com.hallen.asistentedeprofesores.domain.model.Assistance
import com.hallen.asistentedeprofesores.domain.model.StudentData
import com.hallen.asistentedeprofesores.ui.view.adapters.GraficAdapter
import com.hallen.asistentedeprofesores.ui.view.adapters.GraficSpinnerAdapter
import com.hallen.asistentedeprofesores.ui.view.utils.LineChartXAxisFormater
import com.hallen.asistentedeprofesores.ui.view.utils.MyValueFormatter
import com.hallen.asistentedeprofesores.ui.viewmodel.GroupViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.text.DateFormat
import java.text.SimpleDateFormat

@AndroidEntryPoint
class FragmentGrafic : Fragment() {
    private lateinit var binding: FragmentGraficBinding
    private val groupViewModel: GroupViewModel by activityViewModels()
    private lateinit var spinnerAdapter: GraficSpinnerAdapter
    private var registers:  List<Pair<Int, List<Assistance>>> = listOf()
    private var totalDays: List<String> = listOf()
    private val dateFormat: DateFormat = SimpleDateFormat("dd-MM-yyyy")
    private var estudiantesMap: Map<Int, StudentData> = mapOf()
    private lateinit var adapter: GraficAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        spinnerAdapter = GraficSpinnerAdapter(requireContext(), R.layout.spinner_item)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding = FragmentGraficBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()

        groupViewModel.totalAssistanceModel.observe(viewLifecycleOwner){ asistencia ->
            estudiantesMap = groupViewModel.studentDataModel.value?.associateBy {it.id} ?: emptyMap()
            registers = asistencia

            val listaDeAsistencia: List<List<Assistance>> = asistencia.map { it.second } // extrae las sublistas de asistencia
            totalDays = listaDeAsistencia.flatten().map { it.date }.distinct() // elimina las fechas de asistencia duplicadas

            spinnerAdapter.clear()
            for (i in estudiantesMap){
                spinnerAdapter.add(i.key to i.value.name)
            }
            spinnerAdapter.notifyDataSetChanged()
            val student = estudiantesMap[registers.firstOrNull()?.first] ?: return@observe
            val firstStudent = registers.firstOrNull()?.second ?: return@observe
            createGrafic(student.name, firstStudent)
        }

        binding.studentChart.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long,
            ) {
                val value = spinnerAdapter.values[position]
                val name = value.second
                val asistencia = registers.find { it.first == value.first }?.second ?: emptyList()
                createGrafic(name, asistencia)
            }
            override fun onNothingSelected(p0: AdapterView<*>?) {   }
        }
        binding.studentChart.adapter = spinnerAdapter // Agregamos el adapter al spinner
    }

    private fun setupRecyclerView() {
        adapter = GraficAdapter()
        binding.graficaRv.layoutManager = LinearLayoutManager(context)
        binding.graficaRv.adapter = adapter
    }

    private fun createGrafic(name: String, asistencias: List<Assistance>) {
        val lineList: ArrayList<Entry> = ArrayList()
        val lineChartXAxisFormater = LineChartXAxisFormater()

        val fechas = asistencias.map { it.date }
        val aucencias: ArrayList<String>  = totalDays.filter { it !in fechas } as ArrayList<String>
        val precencias = asistencias.filter { it.date in totalDays }

        for (asistencia in precencias){
            val note = asistencia.qualification.toString().toFloat()
            val date = asistencia.date
            if (asistencia.assistance == 0) aucencias.add(date)
            if (note < 2) continue
            val xnew = dateFormat.parse(date)!!.time - lineChartXAxisFormater.originalTimestamp.toFloat()
            lineList.add(Entry(xnew, note))
        }

        val coolFormat = DateFormat.getDateInstance()
        val dates = aucencias.map { dateFormat.parse(it) }.sorted()
        val coolasistencias = dates.map { coolFormat.format(it) }
        binding.absencesHeader.visibility = if (coolasistencias.isNotEmpty()) View.VISIBLE else View.GONE
        binding.absencesHeader.text = "Aucencias: ${coolasistencias.size}"
        adapter.update(coolasistencias)
        lineList.sortBy { it.x } // Ordena los elementos de la lista.
        val lineDataSet = LineDataSet(lineList, "Notas")
        val lineData = LineData(lineDataSet)
        lineData.setValueFormatter(MyValueFormatter())
        binding.grafica.data = lineData
        binding.grafica.axisLeft.granularity = 1.0f
        binding.grafica.axisRight.granularity = 1.0f
        binding.grafica.axisLeft.axisMaximum = 6f
        binding.grafica.axisRight.axisMaximum = 6f
        binding.grafica.axisLeft.axisMinimum = 0f
        binding.grafica.axisRight.axisMinimum = 0f
        binding.grafica.extraLeftOffset = 10f
        binding.grafica.extraRightOffset = 10f
        binding.grafica.extraTopOffset = 10f
        binding.grafica.description = Description().also {  it.text = name  }
        binding.grafica.xAxis.valueFormatter = lineChartXAxisFormater

        //lineDataSet.setColors(*ColorTemplate.JOYFUL_COLORS)
        lineDataSet.valueTextColor = Color.BLUE
        lineDataSet.valueTextSize = 20f
        lineDataSet.setDrawFilled(true)
        lineDataSet.cubicIntensity = 0.2f
        lineDataSet.lineWidth = 2f
        lineDataSet.fillDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.grafic_gradient)
        binding.grafica.invalidate()
    }
}
package com.hallen.asistentedeprofesores.ui.view.fragments

import android.app.Dialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Shader
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.TypedValue
import android.view.*
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.github.dhaval2404.colorpicker.MaterialColorPickerDialog
import com.github.dhaval2404.colorpicker.listener.ColorListener
import com.hallen.asistentedeprofesores.R
import com.hallen.asistentedeprofesores.databinding.DialogHorarioBinding
import com.hallen.asistentedeprofesores.databinding.FragmentHorarioBinding
import com.hallen.asistentedeprofesores.domain.model.Clase
import com.hallen.asistentedeprofesores.ui.view.customviews.ClaseCell
import com.hallen.asistentedeprofesores.ui.view.customviews.HeaderCell
import com.hallen.asistentedeprofesores.ui.viewmodel.ClaseViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class FragmentHorario : Fragment() {
    private lateinit var binding: FragmentHorarioBinding
    private val claseViewModel: ClaseViewModel by viewModels()
    private val dias = listOf("lunes", "martes", "miercoles", "jueves", "viernes")

    // Definir un formato de hora reutilizable
    private val formatter = SimpleDateFormat("hh:mm", Locale.getDefault())

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentHorarioBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        claseViewModel.clasesModel.observe(viewLifecycleOwner) { clases ->
            if (clases.isNotEmpty()) crearTabla() else setupPreview()
        }
        claseViewModel.getClases()
        binding.newClase.setOnClickListener {
            editHora(Clase())
        }
    }

    private fun setupPreview() {
        binding.horarioPreview.visibility = View.VISIBLE
        binding.tablaHorarioId.visibility = View.GONE
        val textView = binding.horarioHeader
        val paint = textView.paint
        val context = requireContext()
        val width = paint.measureText("Horario")
        val textShader = LinearGradient(
            0f, 0f, width, textView.textSize,
            intArrayOf(
                ContextCompat.getColor(context, R.color.primero),
                ContextCompat.getColor(context, R.color.octavo),
                ContextCompat.getColor(context, R.color.segundo)
            ), null, Shader.TileMode.CLAMP
        )
        textView.paint.shader = textShader
    }

    private fun showTimePickerDialog(context: Context, button: TextView) {
        val calendar = Calendar.getInstance()
        val listener = TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
            calendar.set(Calendar.MINUTE, minute)
            button.text = formatter.format(calendar.time)
        }
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)
        val timePickerDialog =
            TimePickerDialog(context, R.style.TimePickerTheme, listener, hour, minute, false)

        timePickerDialog.show()
    }

    private fun editHora(clase: Clase, indexOfDia: Int = 0) {

        // inicializamos algunas variables
        val blueLight = ContextCompat.getColor(requireContext(), R.color.blue_light)

        val dialog = Dialog(requireContext()) // Creamos el dialogo

        // Creamos el dialogBinding
        val dialogBinding = DialogHorarioBinding.inflate(dialog.layoutInflater)
        dialog.apply {
            setContentView(dialogBinding.root)
            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            val layoutParams = WindowManager.LayoutParams()
            layoutParams.apply {
                copyFrom(window?.attributes)
                width = WindowManager.LayoutParams.MATCH_PARENT
                height = WindowManager.LayoutParams.WRAP_CONTENT
                gravity = Gravity.CENTER
            }
            window?.attributes = layoutParams
        }
        // Adapter for the spinner
        val arrayAdapter = ArrayAdapter(requireContext(), R.layout.spinner_item,
            dias.map { it -> it.replaceFirstChar { it.uppercase() } }) // mayusculas ;)
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        // extraemos el color de la clase
        val color = if (clase.color.isBlank() || clase.color == "#FFFFFF") {
            blueLight
        } else Color.parseColor(clase.color)

        // nos permite identificar la clase a editar por las horas de inicio y fin
        val startEnd: List<String> = listOf(clase.horaInicio, clase.horaFin)


        // agregamos los datos a las vistas
        with(dialogBinding) {
            horarioSpinner.adapter = arrayAdapter
            horarioSpinner.setSelection(indexOfDia) // por defecto el spinner va a estar en el dia selecionado.
            horarioLugar.setText(clase.local)
            horarioTimeStart.text = clase.horaInicio.ifBlank { "08:00" }
            horarioTimeStart.setOnClickListener {
                showTimePickerDialog(
                    requireContext(),
                    it as TextView
                )
            }
            horarioTimeEnd.text = clase.horaFin.ifBlank { "08:00" }
            horarioTimeEnd.setOnClickListener {
                showTimePickerDialog(
                    requireContext(),
                    it as TextView
                )
            }
            horarioAsignatura.setText(clase.asignatura)
            colorArea.setBackgroundColor(color)
            grupo.setText(clase.grupo)
        }

        // el dialogo de color
        dialogBinding.colorArea.setOnClickListener {
            MaterialColorPickerDialog
                .Builder(requireContext())
                .setTitle("Escoge un color para la clase")
                .setColorRes(resources.getIntArray(R.array.themeColors))
                .setColorListener(object : ColorListener {
                    override fun onColorSelected(color: Int, colorHex: String) {
                        dialogBinding.colorArea.setBackgroundColor(color); clase.color = colorHex
                    }
                }).show()

        }

        // Los listeners
        dialogBinding.cancelButton.setOnClickListener { dialog.dismiss() }

        // ok
        dialogBinding.okButton.setOnClickListener {
            val startText = dialogBinding.horarioTimeStart.text.toString().trim()
            val endText = dialogBinding.horarioTimeEnd.text.toString().trim()

            val timeInicio = formatter.parse(startText) ?: return@setOnClickListener
            val timeEnd = formatter.parse(endText) ?: return@setOnClickListener

            if (dialogBinding.grupo.text.isBlank()) {
                Toast.makeText(requireContext(), "Falta el nombre del grupo", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }

            with(clase) {
                local = dialogBinding.horarioLugar.text.toString()
                dia = dias[dialogBinding.horarioSpinner.selectedItemPosition]
                horaInicio = formatter.format(timeInicio)
                horaFin = formatter.format(timeEnd)
                asignatura = dialogBinding.horarioAsignatura.text.toString()
                grupo = dialogBinding.grupo.text.toString()
            }

            claseViewModel.saveChanges(
                clase,
                startEnd
            ) // Si esta ok, actualizamos los datos en la bd
            dialog.dismiss()
        }
        dialog.show() // Por ultimo mostramos el dialogo
    }

    private fun crearTabla() {
        binding.horarioPreview.visibility = View.GONE
        binding.tablaHorarioId.visibility = View.VISIBLE
        val context = requireContext()
        val tableLayout = binding.tablaHorarioId
        tableLayout.removeAllViews()

        // Determine el rango de horas de las clases
        //val horas = claseViewModel.clasesModel.value!!.map { it.horaInicio to it.horaFin }.sortedBy { it.first }
        val horas = claseViewModel.clasesModel.value?.sortedBy { it.horaInicio } ?: return

        // Agrega la fila de encabezado con los días de la semana
        val encabezadoRow = TableRow(context)
        val headerHora = HeaderCell(context)
        headerHora.text = context.getString(R.string.hora)
        headerHora.backgroundTintList =
            ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.segundo))
        headerHora.background =
            ContextCompat.getDrawable(requireContext(), R.drawable.horario_top_left_corner)
        encabezadoRow.addView(headerHora)
        for (dia in dias) {
            val headerDay = HeaderCell(context)
            headerDay.text = dia.replaceFirstChar { it.uppercase() }
            if (dia == dias.last()) {
                headerDay.background =
                    ContextCompat.getDrawable(context, R.drawable.horario_top_right_corner)
                headerDay.backgroundTintList = ColorStateList.valueOf(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.steel_blue
                    )
                )
            }
            encabezadoRow.addView(headerDay)
        }
        tableLayout.addView(encabezadoRow)
        // Agrega las filas de datos con las clases
        for (hora in horas.distinctBy { it.horaInicio }) {
            val row = TableRow(context).apply {
                layoutParams = TableLayout.LayoutParams(
                    TableLayout.LayoutParams.MATCH_PARENT,
                    TableLayout.LayoutParams.MATCH_PARENT
                )
            }
            row.addView(TextView(context)
                .apply {
                    text = getString(R.string.hora_template, hora.horaInicio, hora.horaFin)
                    setTextColor(Color.BLACK)
                    if (hora == horas.last()) {
                        background = ContextCompat.getDrawable(
                            context,
                            R.drawable.horario_bottom_left_corner
                        )
                        backgroundTintList = ColorStateList.valueOf(
                            ContextCompat.getColor(
                                context,
                                R.color.steel_blue_light
                            )
                        )
                    } else setBackgroundColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.steel_blue_light
                        )
                    )
                    setPadding(5.dpToPx(context), 0, 5.dpToPx(context), 0)
                    setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f)
                    layoutParams = TableRow.LayoutParams(
                        TableRow.LayoutParams.MATCH_PARENT,
                        TableRow.LayoutParams.MATCH_PARENT
                    )
                    gravity = Gravity.CENTER
                })
            for (dia in dias) {

                val clasesDeLaHora =
                    horas.filter { it.dia == dia && it.horaInicio == hora.horaInicio }
                val (grupoText, localText, color) = Triple(
                    clasesDeLaHora.joinToString { it.grupo },
                    clasesDeLaHora.joinToString { it.local },
                    clasesDeLaHora.joinToString { it.color }
                )

                val claseCell = ClaseCell(context).apply {
                    grupo.text = grupoText
                    local.text = localText
                    background = when {
                        dia == dias.last() && hora == horas.last() -> ContextCompat.getDrawable(
                            context,
                            R.drawable.horario_bottom_right_corner
                        )
                        else -> ContextCompat.getDrawable(
                            context,
                            R.drawable.horario_no_border_no_corner
                        )
                    }

                    val colorBg = if (color.isNotBlank()) {
                        try {
                            Color.parseColor(color)
                        } catch (e: IllegalArgumentException) {
                            ContextCompat.getColor(context, R.color.blue_light)
                        }
                    } else ContextCompat.getColor(context, R.color.blue_light)

                    backgroundTintList = ColorStateList.valueOf(colorBg)
                    setTextColor(Color.WHITE)
                }
                claseCell.setOnClickListener {
                    if (clasesDeLaHora.isNotEmpty()) editHora(
                        clasesDeLaHora[0],
                        dias.indexOf(dia)
                    ) else {
                        editHora(
                            Clase(horaInicio = hora.horaInicio, horaFin = hora.horaFin),
                            dias.indexOf(dia)
                        )
                    }
                }
                claseCell.setOnLongClickListener {
                    val popupMenu = PopupMenu(it.context, it)
                    popupMenu.menuInflater.inflate(R.menu.simple_delete_menu, popupMenu.menu)
                    popupMenu.show()
                    popupMenu.setOnMenuItemClickListener {
                        // nos permite identificar la clase a editar por las horas de inicio y fin
                        deleteHorario(clasesDeLaHora[0])
                        true
                    }
                    true
                }
                row.addView(claseCell)
            }
            tableLayout.addView(row)
        }
    }

    private fun deleteHorario(clase: Clase) {
        claseViewModel.deleteClase(clase)
    }
}

private fun Int.dpToPx(context: Context): Int {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        this.toFloat(),
        context.resources.displayMetrics
    ).toInt()
}
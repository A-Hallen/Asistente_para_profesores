package com.hallen.asistentedeprofesores.ui.view.fragments

import android.app.Dialog
import android.graphics.LinearGradient
import android.graphics.Shader
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.hallen.asistentedeprofesores.R
import com.hallen.asistentedeprofesores.databinding.FragmentNotasBinding
import com.hallen.asistentedeprofesores.databinding.NotasDialogBinding
import com.hallen.asistentedeprofesores.ui.view.adapters.NotasAdapter
import com.hallen.asistentedeprofesores.ui.viewmodel.NotaViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FragmentNotas : Fragment() {
    private lateinit var binding: FragmentNotasBinding
    private val notaViewModel: NotaViewModel by viewModels()
    private lateinit var adapter: NotasAdapter
    private var actualItemPosition: Int? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentNotasBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()

        notaViewModel.notasModel.observe(viewLifecycleOwner) { notas ->
            if (notas.isEmpty()) setupPreview() else {
                binding.notasPreview.visibility = View.GONE
                adapter.updateNota(notas, requireActivity())
            }
        }

        notaViewModel.getNotas()

        listeners() // load all the listeners
    }

    private fun setupPreview() {
        binding.notasPreview.visibility = View.VISIBLE
        val textView = binding.notasHeader
        val context = requireContext()
        val textShader = LinearGradient(
            0f, 0f, textView.width.toFloat(), textView.textSize,
            intArrayOf(
                ContextCompat.getColor(context, R.color.primero),
                ContextCompat.getColor(context, R.color.octavo),
                ContextCompat.getColor(context, R.color.segundo)
            ), null, Shader.TileMode.CLAMP
        )
        textView.paint.shader = textShader
    }

    private fun setupRecyclerView() {
        adapter = NotasAdapter(requireContext(),
            itemClickListener = { nota, position ->
                actualItemPosition = position
                loadNotasEdit(nota.title, nota.content)

            }, itemLongClickListener = { nota, view ->
                val popupMenu = PopupMenu(view.context, view)
                popupMenu.menuInflater.inflate(R.menu.simple_delete_menu, popupMenu.menu)
                popupMenu.show()
                popupMenu.setOnMenuItemClickListener {
                    notaViewModel.deleteNota(nota.id)
                    true
                }
            })

        binding.notasRecyclerView.layoutManager = LinearLayoutManager(context)
        binding.notasRecyclerView.adapter = adapter
    }

    /**
     * Set all the listeners of the fragment
     */
    private fun listeners() {
        // Create new note
        binding.newNote.setOnClickListener { actualItemPosition = null; loadNotasEdit() }
    }

    /**
     *  shows the edit mode of the notes
     */
    private fun loadNotasEdit(title: String = "", content: String = "") {
        // Guarda la nueva nota

        val context = requireContext()
        val dialog = Dialog(context, R.style.NoteDialog)
        val dialogBinding = NotasDialogBinding.inflate(dialog.layoutInflater)

        dialog.apply {
            setContentView(dialogBinding.root)
            window?.setBackgroundDrawable(null)
        }
        with(dialogBinding) {
            editNoteTitle.setText(title)
            editNoteContent.setText(content)
            editNoteContent.requestFocus()
            notasBack.setOnClickListener { dialog.dismiss() }
            guardarNota.setOnClickListener {
                editNoteTitle.text.toString().let { titulo ->
                    editNoteContent.text.toString().let { contenido ->
                        if (contenido.isBlank() && titulo.isBlank()){
                            Toast.makeText(
                                requireContext(),
                                "La nota no debe estar en blanco",
                                Toast.LENGTH_SHORT
                            ).show()
                            return@setOnClickListener
                        }
                        // If the title is blank, set the title to the first 20 characters of the content
                        val newTitle = titulo.takeIf { it.isNotBlank() } ?: contenido.take(20)
                        // Get the id of the note if already exists
                        val id = actualItemPosition?.let { adapter.getItemKey(it) }
                        notaViewModel.saveNota(id, newTitle, contenido)  // Save the note
                        dialog.dismiss()
                    }
                }
            }
        }
        dialog.show()
    }
}
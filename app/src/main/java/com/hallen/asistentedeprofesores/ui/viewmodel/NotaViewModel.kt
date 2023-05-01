package com.hallen.asistentedeprofesores.ui.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hallen.asistentedeprofesores.domain.usecase.GetNotaUseCase
import com.hallen.asistentedeprofesores.domain.usecase.SetNotaUseCase
import com.hallen.asistentedeprofesores.domain.model.Nota
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotaViewModel  @Inject constructor(
    private val getNotaUseCase: GetNotaUseCase,
    private val setNotaUseCase: SetNotaUseCase
): ViewModel() {

    val notasModel = MutableLiveData<List<Nota>>()

    fun getNotas(){
        CoroutineScope(Dispatchers.IO).launch {
            val notas = getNotaUseCase()
            notasModel.postValue(notas)
        }
    }

    fun saveNota(id: Int?, title: String, content: String) {
        CoroutineScope(Dispatchers.IO).launch {
            when (id) {
                null -> setNotaUseCase.insertNota(Nota(title = title, content = content))
                else -> setNotaUseCase.updateNota(Nota(id = id, index = id, title = title, content = content))
            }
                getNotas()
        }
    }

    fun deleteNota(id: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            setNotaUseCase.deleteNota(id)
            getNotas()
        }
    }

}

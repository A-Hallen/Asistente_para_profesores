package com.hallen.asistentedeprofesores.ui.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hallen.asistentedeprofesores.domain.usecase.GetClaseUseCase
import com.hallen.asistentedeprofesores.domain.usecase.SetClaseUseCase
import com.hallen.asistentedeprofesores.domain.model.Clase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.ArrayList
import javax.inject.Inject

@HiltViewModel
class ClaseViewModel @Inject constructor(
    private val getClaseUseCase: GetClaseUseCase,
    private val setClaseUseCase: SetClaseUseCase
): ViewModel() {

    val clasesModel = MutableLiveData<List<Clase>>()

    fun getClases(){
        CoroutineScope(Dispatchers.IO).launch {
            val clases = getClaseUseCase()
            clasesModel.postValue(clases)
        }
    }

    fun saveChanges(clase: Clase, startEnd: List<String>){
        CoroutineScope(Dispatchers.IO).launch {
            val clases = clasesModel.value ?: return@launch
            val semana: ArrayList<Clase> = clases.filter { it.horaInicio != startEnd[0] && it.horaFin != startEnd[1] } as ArrayList<Clase>
            semana.add(clase)
            setClaseUseCase.insertClases(semana)
            getClases()
        }
    }

    fun deleteClase(clase: Clase){
        CoroutineScope(Dispatchers.IO).launch {
            setClaseUseCase.deleteClase(clase)
            val startEnd: List<String> = listOf(clase.horaInicio, clase.horaFin)
            getClases()
        }
    }

}
package com.hallen.asistentedeprofesores.data.infraestructure.persistent

import android.content.Context
import javax.inject.Inject

class Prefs @Inject constructor(context: Context) {
    private val SHARED_NAME = "asistenteDatabase"
    private val storage = context.getSharedPreferences(SHARED_NAME, 0)

    fun getOpenAIKey(): String {
        return storage.getString("openai_key", "")!!
    }
    fun saveOpenAIKey(key: String){
        storage.edit().putString("openai_key", key).apply()
    }

    fun getAllowContext(): Boolean {
        return storage.getBoolean("context", false)
    }
    fun setAllowContext(allowContext: Boolean){
        storage.edit().putBoolean("context", allowContext).apply()
    }

    fun isFirstStart(): Boolean = storage.getBoolean("first_start", true)

    fun setFirstStart(){
        storage.edit().putBoolean("first_start", false).apply()
    }
}
package com.hallen.asistentedeprofesores

import org.junit.Test
import kotlin.math.roundToInt

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        val hora = "08:00-AM"
        val newHora = hora.dropLast(3)
        val pmAm = hora.substring(6) // extrae los últimos dos caracteres ("-AM")
        println("HALLEN $pmAm")
    }

    @Test
    fun test(){
        val res = 83.50f
        println("HALLEN ROUND: ${res.roundToInt()}, EXACT: $res")
    }
}
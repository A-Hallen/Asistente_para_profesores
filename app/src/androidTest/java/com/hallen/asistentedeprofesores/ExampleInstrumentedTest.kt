package com.hallen.asistentedeprofesores

import android.content.Intent
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.hallen.asistentedeprofesores.data.infraestructure.system.AlarmReceiver
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.hallen.asistentedeprofesores", appContext.packageName)
    }


    @Test
    fun test(){
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext

        val alarmIntent1 = Intent(appContext, AlarmReceiver::class.java)
        alarmIntent1.putExtra("date", 100000)
        alarmIntent1.putExtra("title", "title test")
        alarmIntent1.putExtra("details", "details test")

        val alarmIntent2 = Intent(appContext, AlarmReceiver::class.java)
        alarmIntent2.putExtra("date", 100000)
        alarmIntent2.putExtra("title", "title test")
        alarmIntent2.putExtra("details", "details test")

        alarmIntent1.filterEquals(alarmIntent2)
    }
}
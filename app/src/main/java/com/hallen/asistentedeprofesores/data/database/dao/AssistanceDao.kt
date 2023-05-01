package com.hallen.asistentedeprofesores.data.database.dao

import androidx.room.*
import com.hallen.asistentedeprofesores.data.database.entities.AssistanceEntity
import com.hallen.asistentedeprofesores.data.database.entities.toDataBase
import com.hallen.asistentedeprofesores.domain.model.Assistance
import com.hallen.asistentedeprofesores.domain.model.Day

@Dao
interface AssistanceDao {

    @Transaction
    suspend fun setAssistance(
        id_student: Int,
        date: String,
        present: Int,
        qualification: Int = 0
    ) {
        val assistance = findAssistanceByStudentId(id_student, date)
        if (assistance != null) {
            update(assistance.copy(assistance = present, qualification = qualification))
        } else insert(
            Assistance(
                id_student = id_student,
                date = date,
                assistance = present,
                qualification = qualification
            ).toDataBase()
        )
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(assistance: AssistanceEntity)

    @Update
    suspend fun update(assistance: AssistanceEntity)

    @Query("SELECT * FROM assistance_table WHERE id_student = :id_student AND date = :date")
    suspend fun findAssistanceByStudentId(id_student: Int, date: String): AssistanceEntity?

    @Query("SELECT * FROM assistance_table WHERE date = :date")
    suspend fun getAllAssistanceByDate(date: String): List<AssistanceEntity>

    @Query("SELECT * FROM assistance_table WHERE date = :date AND id_student IN (SELECT id_student FROM group_table WHERE id = :id_group)")
    suspend fun getAllAssistanceByGroup(id_group: Int, date: String): List<AssistanceEntity>

    @Query("SELECT date, SUM(CASE WHEN assistance = 1 THEN 1 ELSE 0 END)" +
            " AS presents FROM assistance_table " +
            "WHERE id_student IN (SELECT id FROM students_table WHERE id_group = :id_group)" +
            " GROUP BY date")
    suspend fun getAllDays(id_group: Int): List<Day>

    @Query("DELETE FROM assistance_table WHERE date = :date AND id_student IN (SELECT id FROM students_table WHERE id_group = :id_group)")
    suspend fun deleteDay(date: String, id_group: Int)

    @Query("SELECT COUNT(DISTINCT date) FROM assistance_table WHERE" +
            " id_student IN (SELECT id FROM students_table WHERE id_group = :id_group)")
    suspend fun getTotalDays(id_group: Int): Int


    @Query("SELECT * FROM assistance_table WHERE id_student IN (SELECT " +
            "id FROM students_table WHERE id_group = :id_group)")
    suspend fun getAllDaysFromDatabase(id_group: Int): List<AssistanceEntity>
}
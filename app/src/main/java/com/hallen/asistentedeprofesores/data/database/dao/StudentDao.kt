package com.hallen.asistentedeprofesores.data.database.dao

import androidx.room.*
import com.hallen.asistentedeprofesores.data.database.entities.StudentEntity
import com.hallen.asistentedeprofesores.domain.model.StudentRegisters

@Dao
interface StudentDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(student: StudentEntity)

    @Update
    suspend fun update(student: StudentEntity)

    @Query("DELETE FROM students_table WHERE id = :id")
    suspend fun delete(id: Int)

    @Query("SELECT * FROM students_table WHERE id = :id")
    suspend fun getStudent(id: Int): StudentEntity?

    @Query("SELECT * FROM students_table WHERE id_group = :id_group")
    suspend fun getStudents(id_group: Int): List<StudentEntity>

    @Query("DELETE FROM students_table WHERE id_group = :id_group")
    suspend fun deleteStudentsByGroupId(id_group: Int)

    // | students_table | id_group | assistance_table | id_student | qualification | assistance |

        @Query("SELECT AVG(CASE WHEN assistance_table.qualification > 0 THEN assistance_table.qualification ELSE NULL END) AS qualification, " +
            "assistance_table.id_student AS id, students_table.name, " +
                    "SUM(assistance_table.assistance) AS assistance FROM assistance_table" +
                    " JOIN students_table ON students_table.id = assistance_table.id_student" +
                    " WHERE students_table.id_group = :id_group" +
                    " GROUP BY id_student")
    suspend fun getStudentRegisters(id_group: Int): List<StudentRegisters>

   /* @Query("SELECT students_table.name AS name, assistance_table FROM assistance_table " +
            "JOIN students_table ON students_table.id = assistance_table.id_student " +
            "WHERE id_student IN (SELECT id_student FROM group_table WHERE id = :id_group)")
    suspend fun getAllStudentRegisters(id_group: Int): List<StudentAllRegisters>
*/

}
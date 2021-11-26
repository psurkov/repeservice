package com.github.psurkov.repeservice.repository

import com.github.psurkov.repeservice.model.user.CreateStudentModel
import com.github.psurkov.repeservice.model.user.StudentModel
import com.github.psurkov.repeservice.table.StudentTable
import com.github.psurkov.repeservice.table.dbQuery
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Isolation
import org.springframework.transaction.annotation.Transactional

@Repository
@Transactional(isolation = Isolation.SERIALIZABLE)
class StudentRepository {
    private fun ResultRow.fromRow(): StudentModel =
        StudentModel(
            this[StudentTable.id],
            this[StudentTable.username],
            this[StudentTable.password],
        )

    suspend fun findById(id: Long) = dbQuery {
        StudentTable.select {
            StudentTable.id eq id
        }.singleOrNull()?.fromRow()
    }

    suspend fun findByUsername(username: String) = dbQuery {
        StudentTable.select {
            StudentTable.username eq username
        }.singleOrNull()?.fromRow()
    }

    suspend fun insert(createStudentModel: CreateStudentModel): StudentModel = dbQuery {
        StudentTable.insert {
            it[username] = createStudentModel.username
            it[password] = createStudentModel.password
        }.resultedValues!!.first().fromRow()
    }
}
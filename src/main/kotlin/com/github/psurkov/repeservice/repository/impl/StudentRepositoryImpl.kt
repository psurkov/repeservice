package com.github.psurkov.repeservice.repository.impl

import com.github.psurkov.repeservice.model.user.CreateStudentModel
import com.github.psurkov.repeservice.model.user.StudentModel
import com.github.psurkov.repeservice.repository.StudentRepository
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Isolation
import org.springframework.transaction.annotation.Transactional

@Repository
@Transactional(isolation = Isolation.SERIALIZABLE)
class StudentRepositoryImpl : StudentRepository {
    private fun ResultRow.fromRow(): StudentModel =
        StudentModel(
            this[StudentTable.id],
            this[StudentTable.username],
            this[StudentTable.password],
        )

    override suspend fun findById(id: Long) = dbQuery {
        StudentTable.select {
            StudentTable.id eq id
        }.singleOrNull()?.fromRow()
    }

    override suspend fun findByUsername(username: String) = dbQuery {
        StudentTable.select {
            StudentTable.username eq username
        }.singleOrNull()?.fromRow()
    }

    override suspend fun insert(createStudentModel: CreateStudentModel): StudentModel = dbQuery {
        StudentTable.insert {
            it[username] = createStudentModel.username
            it[password] = createStudentModel.password
        }.resultedValues!!.first().fromRow()
    }

    object StudentTable : AppTable() {
        val id = long("id").autoIncrement()
        val username = text("username")
        val password = text("password")

        override val primaryKey = PrimaryKey(id)
    }
}
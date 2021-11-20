package com.github.psurkov.repeservice.repository

import com.github.psurkov.repeservice.model.user.CreateTutorModel
import com.github.psurkov.repeservice.model.user.TutorModel
import com.github.psurkov.repeservice.table.TutorTable
import com.github.psurkov.repeservice.table.dbQuery
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Isolation
import org.springframework.transaction.annotation.Transactional

@Repository
@Transactional(isolation = Isolation.SERIALIZABLE)
class TutorRepository {
    private fun ResultRow.fromRow() =
        TutorModel(
            this[TutorTable.id],
            this[TutorTable.username],
            this[TutorTable.password],
        )

    suspend fun findById(id: Long) = dbQuery {
        TutorTable.select {
            TutorTable.id eq id
        }.singleOrNull()?.fromRow()
    }

    suspend fun findByUsername(username: String) = dbQuery {
        TutorTable.select {
            TutorTable.username eq username
        }.singleOrNull()?.fromRow()
    }

    suspend fun insert(createTutorModel: CreateTutorModel): TutorModel = dbQuery {
        TutorTable.insert {
            it[username] = createTutorModel.username
            it[password] = createTutorModel.password
        }.resultedValues!!.first().fromRow()
    }
}
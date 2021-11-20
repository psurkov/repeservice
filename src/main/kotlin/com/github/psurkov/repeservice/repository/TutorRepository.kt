package com.github.psurkov.repeservice.repository

import com.github.psurkov.repeservice.model.Tutor
import com.github.psurkov.repeservice.table.TutorTable
import com.github.psurkov.repeservice.table.dbQuery
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
@Transactional
class TutorRepository {
    private fun fromRow(r: ResultRow) =
        Tutor(
            r[TutorTable.id],
            r[TutorTable.username],
            r[TutorTable.password],
        )

    suspend fun findById(id: Long): Tutor? = dbQuery {
        TutorTable.select {
            TutorTable.id eq id
        }.singleOrNull()?.let { fromRow(it) }
    }

    suspend fun findByUsername(username: String) = dbQuery {
        TutorTable.select {
            TutorTable.username eq username
        }.singleOrNull()?.let { fromRow(it) }
    }

    suspend fun findAll(): List<Tutor> = dbQuery {
        TutorTable.selectAll().map { fromRow(it) }
    }
}
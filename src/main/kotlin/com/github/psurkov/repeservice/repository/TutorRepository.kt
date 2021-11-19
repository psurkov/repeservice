package com.github.psurkov.repeservice.repository

import com.github.psurkov.repeservice.model.Tutor
import com.github.psurkov.repeservice.table.Tutors
import com.github.psurkov.repeservice.table.dbQuery
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.selectAll
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
@Transactional
class TutorRepository {
    private fun fromRow(r: ResultRow) =
        Tutor(
            r[Tutors.id]
        )

    suspend fun findAll(): List<Tutor> = dbQuery {
        Tutors.selectAll().map { fromRow(it) }
    }
}
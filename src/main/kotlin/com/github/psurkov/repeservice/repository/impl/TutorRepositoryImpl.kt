package com.github.psurkov.repeservice.repository.impl

import com.github.psurkov.repeservice.model.user.CreateTutorModel
import com.github.psurkov.repeservice.model.user.TutorModel
import com.github.psurkov.repeservice.repository.TutorRepository
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Isolation
import org.springframework.transaction.annotation.Transactional

@Repository
@Transactional(isolation = Isolation.SERIALIZABLE)
class TutorRepositoryImpl : TutorRepository {
    private fun ResultRow.fromRow() =
        TutorModel(
            this[TutorTable.id],
            this[TutorTable.username],
            this[TutorTable.password],
        )

    override suspend fun findById(id: Long) = dbQuery {
        TutorTable.select {
            TutorTable.id eq id
        }.singleOrNull()?.fromRow()
    }

    override suspend fun findByUsername(username: String) = dbQuery {
        TutorTable.select {
            TutorTable.username eq username
        }.singleOrNull()?.fromRow()
    }

    override suspend fun insert(createTutorModel: CreateTutorModel): TutorModel = dbQuery {
        TutorTable.insert {
            it[username] = createTutorModel.username
            it[password] = createTutorModel.password
        }.resultedValues!!.first().fromRow()
    }

    object TutorTable : AppTable() {
        val id = long("id").autoIncrement()
        val username = text("username")
        val password = text("password")

        override val primaryKey = PrimaryKey(id)
    }
}
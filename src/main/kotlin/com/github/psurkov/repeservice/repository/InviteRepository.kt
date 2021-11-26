package com.github.psurkov.repeservice.repository

import com.github.psurkov.repeservice.model.invite.InviteModel
import com.github.psurkov.repeservice.model.invite.InviteStatus
import com.github.psurkov.repeservice.table.InviteTable
import com.github.psurkov.repeservice.table.dbQuery
import org.jetbrains.exposed.sql.*
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Isolation
import org.springframework.transaction.annotation.Transactional

@Repository
@Transactional(isolation = Isolation.SERIALIZABLE)
class InviteRepository {

    private fun ResultRow.fromRow() =
        InviteModel(
            this[InviteTable.id],
            this[InviteTable.studyGroupId],
            this[InviteTable.studentId],
            this[InviteTable.status],
        )

    suspend fun findPendingForStudent(studentId: Long): List<InviteModel> = dbQuery {
        InviteTable.select {
            (InviteTable.studentId eq studentId) and (InviteTable.status eq InviteStatus.PENDING)
        }.map { it.fromRow() }
    }

    suspend fun insertPendingInvite(studyGroupId: Long, studentId: Long): InviteModel = dbQuery {
        InviteTable.insert {
            it[InviteTable.studyGroupId] = studyGroupId
            it[InviteTable.studentId] = studentId
            it[status] = InviteStatus.PENDING
        }.resultedValues!!.first().fromRow()
    }

    suspend fun findById(id: Long): InviteModel? = dbQuery {
        InviteTable.select {
            InviteTable.id eq id
        }.singleOrNull()?.fromRow()
    }

    fun updateInviteStatus(id: Long, status: InviteStatus) {
        InviteTable.update({ InviteTable.id eq id }) { it[InviteTable.status] = status }
    }
}
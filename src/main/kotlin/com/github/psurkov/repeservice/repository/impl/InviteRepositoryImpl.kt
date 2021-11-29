package com.github.psurkov.repeservice.repository.impl

import com.github.psurkov.repeservice.model.invite.InviteModel
import com.github.psurkov.repeservice.model.invite.InviteStatus
import com.github.psurkov.repeservice.repository.InviteRepository
import org.jetbrains.exposed.sql.*
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Isolation
import org.springframework.transaction.annotation.Transactional

@Repository
@Transactional(isolation = Isolation.SERIALIZABLE)
class InviteRepositoryImpl : InviteRepository {

    private fun ResultRow.fromRow() =
        InviteModel(
            this[InviteTable.id],
            this[InviteTable.studyGroupId],
            this[InviteTable.studentId],
            this[InviteTable.status],
        )

    override suspend fun findPendingForStudent(studentId: Long): List<InviteModel> = dbQuery {
        InviteTable.select {
            (InviteTable.studentId eq studentId) and (InviteTable.status eq InviteStatus.PENDING)
        }.map { it.fromRow() }
    }

    override suspend fun insertPendingInvite(studyGroupId: Long, studentId: Long): InviteModel = dbQuery {
        InviteTable.insert {
            it[InviteTable.studyGroupId] = studyGroupId
            it[InviteTable.studentId] = studentId
            it[status] = InviteStatus.PENDING
        }.resultedValues!!.first().fromRow()
    }

    override suspend fun findById(id: Long): InviteModel? = dbQuery {
        InviteTable.select {
            InviteTable.id eq id
        }.singleOrNull()?.fromRow()
    }

    override fun updateInviteStatus(id: Long, status: InviteStatus) {
        InviteTable.update({ InviteTable.id eq id }) { it[InviteTable.status] = status }
    }


    object InviteTable : AppTable() {
        val id = long("id").autoIncrement()
        val studyGroupId = long("studyGroupId").references(StudyGroupRepositoryImpl.StudyGroupTable.id)
        val studentId = long("studentId").references(StudentRepositoryImpl.StudentTable.id)
        val status = enumeration("status", InviteStatus::class)

        override val primaryKey = PrimaryKey(id)
    }
}
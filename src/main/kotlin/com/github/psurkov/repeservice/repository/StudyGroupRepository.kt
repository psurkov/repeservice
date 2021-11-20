package com.github.psurkov.repeservice.repository

import com.github.psurkov.repeservice.model.studygroup.CreateStudyGroupModel
import com.github.psurkov.repeservice.model.studygroup.StudyGroupModel
import com.github.psurkov.repeservice.table.StudyGroupParticipantTable
import com.github.psurkov.repeservice.table.StudyGroupTable
import com.github.psurkov.repeservice.table.dbQuery
import org.jetbrains.exposed.sql.*
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Isolation
import org.springframework.transaction.annotation.Transactional

@Repository
@Transactional(isolation = Isolation.SERIALIZABLE)
class StudyGroupRepository {
    private fun ResultRow.fromRow(participants: List<Long>) =
        StudyGroupModel(
            this[StudyGroupTable.id],
            this[StudyGroupTable.name],
            this[StudyGroupTable.ownerTutorId],
            participants
        )

    suspend fun findById(id: Long) = dbQuery {
        StudyGroupTable.select { StudyGroupTable.id eq id }
            .firstOrNull()
            ?.let {
                val participants = StudyGroupParticipantTable
                    .select { StudyGroupParticipantTable.studyGroupId eq id }
                    .map { participant -> participant[StudyGroupParticipantTable.studentId] }
                it.fromRow(participants)
            }
    }

    suspend fun insert(createStudyGroupModel: CreateStudyGroupModel): StudyGroupModel = dbQuery {
        StudyGroupTable.insert {
            it[ownerTutorId] = createStudyGroupModel.ownerTutorId
            it[name] = createStudyGroupModel.name
        }.resultedValues!!.first().fromRow(emptyList())
    }

    fun addParticipant(studyGroupId: Long, studentId: Long) {
        StudyGroupParticipantTable.insert {
            it[StudyGroupParticipantTable.studyGroupId] = studyGroupId
            it[StudyGroupParticipantTable.studentId] = studentId
        }
    }

    fun deleteParticipant(studyGroupId: Long, studentId: Long) {
        StudyGroupParticipantTable.deleteWhere {
            (StudyGroupParticipantTable.studyGroupId eq studyGroupId) and
                    (StudyGroupParticipantTable.studentId eq studentId)
        }
    }
}
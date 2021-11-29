package com.github.psurkov.repeservice.repository.impl

import com.github.psurkov.repeservice.model.studygroup.CreateStudyGroupModel
import com.github.psurkov.repeservice.model.studygroup.StudyGroupModel
import com.github.psurkov.repeservice.repository.StudyGroupRepository
import org.jetbrains.exposed.sql.*
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Isolation
import org.springframework.transaction.annotation.Transactional

@Repository
@Transactional(isolation = Isolation.SERIALIZABLE)
class StudyGroupRepositoryImpl : StudyGroupRepository {
    private fun ResultRow.fromRow(participants: List<Long>) =
        StudyGroupModel(
            this[StudyGroupTable.id],
            this[StudyGroupTable.name],
            this[StudyGroupTable.ownerTutorId],
            participants
        )

    override suspend fun findById(id: Long) = dbQuery {
        StudyGroupTable.select { StudyGroupTable.id eq id }
            .firstOrNull()
            ?.let {
                val participants = StudyGroupParticipantTable
                    .select { StudyGroupParticipantTable.studyGroupId eq id }
                    .map { participant -> participant[StudyGroupParticipantTable.studentId] }
                it.fromRow(participants)
            }
    }

    override suspend fun insert(createStudyGroupModel: CreateStudyGroupModel): StudyGroupModel = dbQuery {
        StudyGroupTable.insert {
            it[ownerTutorId] = createStudyGroupModel.ownerTutorId
            it[name] = createStudyGroupModel.name
        }.resultedValues!!.first().fromRow(emptyList())
    }

    override fun addParticipant(studyGroupId: Long, studentId: Long) {
        StudyGroupParticipantTable.insert {
            it[StudyGroupParticipantTable.studyGroupId] = studyGroupId
            it[StudyGroupParticipantTable.studentId] = studentId
        }
    }

    override fun deleteParticipant(studyGroupId: Long, studentId: Long) {
        StudyGroupParticipantTable.deleteWhere {
            (StudyGroupParticipantTable.studyGroupId eq studyGroupId) and
                    (StudyGroupParticipantTable.studentId eq studentId)
        }
    }

    object StudyGroupTable : AppTable() {
        val id = long("id").autoIncrement()
        val name = text("name")
        val ownerTutorId = long("ownerTutorId").references(TutorRepositoryImpl.TutorTable.id)
        override val primaryKey = PrimaryKey(id)
    }

    object StudyGroupParticipantTable : AppTable() {
        val studyGroupId = long("studyGroupId").references(StudyGroupTable.id)
        val studentId = long("studentId").references(StudentRepositoryImpl.StudentTable.id)
    }
}
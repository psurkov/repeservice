package com.github.psurkov.repeservice.repository.impl

import com.github.psurkov.repeservice.model.homework.CreateHomeworkModel
import com.github.psurkov.repeservice.model.homework.HomeworkModel
import com.github.psurkov.repeservice.repository.HomeworkRepository
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.kotlin.datetime.datetime
import org.jetbrains.exposed.sql.select
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Isolation
import org.springframework.transaction.annotation.Transactional

@Repository
@Transactional(isolation = Isolation.SERIALIZABLE)
class HomeworkRepositoryImpl : HomeworkRepository {
    private fun ResultRow.fromRow() =
        HomeworkModel(
            this[HomeworkTable.id],
            this[HomeworkTable.name],
            this[HomeworkTable.studyGroupId],
            this[HomeworkTable.taskFileId],
            this[HomeworkTable.deadline],
        )

    override suspend fun findById(homeworkId: Long): HomeworkModel? =
        HomeworkTable.select {
            HomeworkTable.id eq homeworkId
        }.singleOrNull()?.fromRow()

    override suspend fun findByFileId(taskFileId: String): HomeworkModel? =
        HomeworkTable.select {
            HomeworkTable.taskFileId eq taskFileId
        }.singleOrNull()?.fromRow()

    override suspend fun insert(createHomeworkModel: CreateHomeworkModel, taskFileId: String): HomeworkModel =
        HomeworkTable.insert {
            it[name] = createHomeworkModel.name
            it[studyGroupId] = createHomeworkModel.studyGroupId
            it[HomeworkTable.taskFileId] = taskFileId
            it[deadline] = createHomeworkModel.deadline
        }.resultedValues!!.first().fromRow()

    object HomeworkTable : AppTable() {
        val id = long("id").autoIncrement()
        val name = text("name")
        val studyGroupId = long("studyGroupId").references(StudyGroupRepositoryImpl.StudyGroupTable.id)
        val taskFileId = text("taskFileId")
        val deadline = datetime("deadline")
        override val primaryKey = PrimaryKey(id)
    }
}
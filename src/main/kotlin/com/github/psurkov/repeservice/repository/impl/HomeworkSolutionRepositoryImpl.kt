package com.github.psurkov.repeservice.repository.impl

import com.github.psurkov.repeservice.model.homework.HomeworkSolutionModel
import com.github.psurkov.repeservice.repository.HomeworkSolutionRepository
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Isolation
import org.springframework.transaction.annotation.Transactional

@Repository
@Transactional(isolation = Isolation.SERIALIZABLE)
class HomeworkSolutionRepositoryImpl : HomeworkSolutionRepository {
    private fun ResultRow.fromRow() =
        HomeworkSolutionModel(
            this[HomeworkSolutionTable.id],
            this[HomeworkSolutionTable.studentId],
            this[HomeworkSolutionTable.homeworkId],
            this[HomeworkSolutionTable.solutionFileId],
        )

    override suspend fun findByHomework(homeworkId: Long): List<HomeworkSolutionModel> =
        HomeworkSolutionTable.select {
            HomeworkSolutionTable.homeworkId eq homeworkId
        }.map { it.fromRow() }.toList()

    override suspend fun findByStudentAndHomework(studentId: Long, homeworkId: Long): HomeworkSolutionModel? =
        HomeworkSolutionTable.select {
            (HomeworkSolutionTable.studentId eq studentId) and (HomeworkSolutionTable.homeworkId eq homeworkId)
        }.singleOrNull()?.fromRow()

    override suspend fun findByFileId(solutionFileId: String): HomeworkSolutionModel? =
        HomeworkSolutionTable.select {
            HomeworkSolutionTable.solutionFileId eq solutionFileId
        }.singleOrNull()?.fromRow()

    override suspend fun insert(studentId: Long, homeworkId: Long, solutionFileId: String) {
        HomeworkSolutionTable.insert {
            it[HomeworkSolutionTable.studentId] = studentId
            it[HomeworkSolutionTable.homeworkId] = homeworkId
            it[HomeworkSolutionTable.solutionFileId] = solutionFileId
        }.resultedValues!!.first().fromRow()
    }

    object HomeworkSolutionTable : AppTable() {
        val id = long("id").autoIncrement()
        val studentId = long("studentId").references(StudentRepositoryImpl.StudentTable.id)
        val homeworkId = long("homeworkId").references(HomeworkRepositoryImpl.HomeworkTable.id)
        val solutionFileId = text("solutionFileId").uniqueIndex()
        override val primaryKey = PrimaryKey(id)

        init {
            uniqueIndex(studentId, homeworkId)
        }
    }
}
package com.github.psurkov.repeservice.repository

import com.github.psurkov.repeservice.model.homework.HomeworkSolutionModel

interface HomeworkSolutionRepository {
    suspend fun findByHomework(homeworkId: Long): List<HomeworkSolutionModel>
    suspend fun findByStudentAndHomework(studentId: Long, homeworkId: Long): HomeworkSolutionModel?
    suspend fun findByFileId(solutionFileId: String): HomeworkSolutionModel?
    suspend fun insert(studentId: Long, homeworkId: Long, solutionFileId: String)
}
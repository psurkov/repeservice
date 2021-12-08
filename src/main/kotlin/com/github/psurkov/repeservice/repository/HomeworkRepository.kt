package com.github.psurkov.repeservice.repository

import com.github.psurkov.repeservice.model.homework.CreateHomeworkModel
import com.github.psurkov.repeservice.model.homework.HomeworkModel

interface HomeworkRepository {
    suspend fun findById(homeworkId: Long): HomeworkModel?
    suspend fun findByFileId(taskFileId: String): HomeworkModel?
    suspend fun insert(createHomeworkModel: CreateHomeworkModel, taskFileId: String): HomeworkModel

}
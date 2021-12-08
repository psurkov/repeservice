package com.github.psurkov.repeservice.service

import com.github.psurkov.repeservice.model.homework.CreateHomeworkModel
import com.github.psurkov.repeservice.model.homework.HomeworkModel
import com.github.psurkov.repeservice.model.homework.HomeworkSolutionModel
import org.springframework.core.io.Resource
import org.springframework.web.multipart.MultipartFile

interface HomeworkService {
    suspend fun createHomework(createHomeworkModel: CreateHomeworkModel, task: MultipartFile): HomeworkModel
    suspend fun submitSolution(studentId: Long, homeworkId: Long, homeworkSolution: MultipartFile)
    suspend fun listSolutions(homeworkId: Long): List<HomeworkSolutionModel>
    suspend fun getTaskResource(taskFileId: String): Resource
    suspend fun getSolutionResource(solutionFileId: String): Resource
}
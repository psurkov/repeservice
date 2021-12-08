package com.github.psurkov.repeservice.service.impl

import com.github.psurkov.repeservice.exception.*
import com.github.psurkov.repeservice.model.homework.CreateHomeworkModel
import com.github.psurkov.repeservice.model.homework.HomeworkModel
import com.github.psurkov.repeservice.model.homework.HomeworkSolutionModel
import com.github.psurkov.repeservice.repository.*
import com.github.psurkov.repeservice.service.HomeworkService
import kotlinx.datetime.toKotlinLocalDateTime
import org.springframework.core.io.Resource
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Isolation
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile

@Service
@Transactional(isolation = Isolation.SERIALIZABLE)
class HomeworkServiceImpl(
    private val studyGroupRepository: StudyGroupRepository,
    private val studentRepository: StudentRepository,
    private val fileStorageRepository: FileStorageRepository,
    private val homeworkRepository: HomeworkRepository,
    private val homeworkSolutionRepository: HomeworkSolutionRepository,
) : HomeworkService {
    override suspend fun createHomework(createHomeworkModel: CreateHomeworkModel, task: MultipartFile): HomeworkModel {
        studyGroupRepository.findById(createHomeworkModel.studyGroupId) ?: throw NotFoundStudyGroup()
        val taskFileId = fileStorageRepository.store(task)
        return homeworkRepository.insert(createHomeworkModel, taskFileId)
    }

    override suspend fun submitSolution(studentId: Long, homeworkId: Long, homeworkSolution: MultipartFile) {
        studentRepository.findById(studentId) ?: throw NotFoundStudent()
        val homeworkModel = homeworkRepository.findById(homeworkId) ?: throw NotFoundHomework()
        if (homeworkSolutionRepository.findByStudentAndHomework(studentId, homeworkId) != null) {
            throw AlreadyExistsHomeworkSolution()
        }
        if (homeworkModel.deadline < java.time.LocalDateTime.now().toKotlinLocalDateTime()) {
            throw SolutionAfterDeadline()
        }
        val solutionFileId = fileStorageRepository.store(homeworkSolution)
        homeworkSolutionRepository.insert(studentId, homeworkId, solutionFileId)
    }

    override suspend fun listSolutions(homeworkId: Long): List<HomeworkSolutionModel> =
        homeworkSolutionRepository.findByHomework(homeworkId)

    override suspend fun getTaskResource(taskFileId: String): Resource {
        homeworkRepository.findByFileId(taskFileId) ?: throw NotFoundTaskFile()
        return fileStorageRepository.load(taskFileId)
    }

    override suspend fun getSolutionResource(solutionFileId: String): Resource {
        homeworkSolutionRepository.findByFileId(solutionFileId) ?: throw NotFoundTaskSolutionFile()
        return fileStorageRepository.load(solutionFileId)

    }
}
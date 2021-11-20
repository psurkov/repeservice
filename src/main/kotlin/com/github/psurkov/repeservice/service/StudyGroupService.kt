package com.github.psurkov.repeservice.service

import com.github.psurkov.repeservice.exception.NotFoundTutor
import com.github.psurkov.repeservice.model.studygroup.CreateStudyGroupModel
import com.github.psurkov.repeservice.model.studygroup.StudyGroupModel
import com.github.psurkov.repeservice.repository.StudyGroupRepository
import com.github.psurkov.repeservice.repository.TutorRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Isolation
import org.springframework.transaction.annotation.Transactional
import kotlin.jvm.Throws

@Service
@Transactional(isolation = Isolation.SERIALIZABLE)
class StudyGroupService(
    private val tutorRepository: TutorRepository,
    private val studyGroupRepository: StudyGroupRepository
) {

    @Throws(NotFoundTutor::class)
    suspend fun createNewStudyGroup(createModel: CreateStudyGroupModel): StudyGroupModel {
        tutorRepository.findById(createModel.ownerTutorId) ?: throw NotFoundTutor()
        return studyGroupRepository.insert(createModel)
    }

}
package com.github.psurkov.repeservice.service

import com.github.psurkov.repeservice.exception.*
import com.github.psurkov.repeservice.model.invite.InviteModel
import com.github.psurkov.repeservice.model.studygroup.CreateStudyGroupModel
import com.github.psurkov.repeservice.model.studygroup.StudyGroupModel
import com.github.psurkov.repeservice.repository.InviteRepository
import com.github.psurkov.repeservice.repository.StudentRepository
import com.github.psurkov.repeservice.repository.StudyGroupRepository
import com.github.psurkov.repeservice.repository.TutorRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Isolation
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(isolation = Isolation.SERIALIZABLE)
class StudyGroupService(
    private val tutorRepository: TutorRepository,
    private val studentRepository: StudentRepository,
    private val studyGroupRepository: StudyGroupRepository,
    private val inviteRepository: InviteRepository,
) {

    @Throws(NotFoundTutor::class)
    suspend fun createNewStudyGroup(createModel: CreateStudyGroupModel): StudyGroupModel {
        tutorRepository.findById(createModel.ownerTutorId) ?: throw NotFoundTutor()
        return studyGroupRepository.insert(createModel)
    }

    @Throws(
        NotFoundStudyGroup::class,
        NotFoundStudent::class,
        StudentAlreadyInStudyGroup::class,
        AlreadyPendingInvite::class
    )
    suspend fun invite(
        studyGroupId: Long,
        studentId: Long,
    ): InviteModel {
        studentRepository.findById(studentId) ?: throw NotFoundStudent()
        val studyGroup = studyGroupRepository.findById(studyGroupId) ?: throw NotFoundStudyGroup()
        if (studyGroup.participantIds.contains(studentId)) {
            throw StudentAlreadyInStudyGroup()
        }
        if (inviteRepository.findPendingForStudent(studentId).any { it.studyGroupId == studyGroupId }) {
            throw AlreadyPendingInvite()
        }
        return inviteRepository.insertPendingInvite(studyGroupId, studentId)
    }
}
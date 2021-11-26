package com.github.psurkov.repeservice.service.impl

import com.github.psurkov.repeservice.exception.*
import com.github.psurkov.repeservice.model.invite.InviteModel
import com.github.psurkov.repeservice.model.invite.InviteStatus
import com.github.psurkov.repeservice.model.studygroup.CreateStudyGroupModel
import com.github.psurkov.repeservice.model.studygroup.StudyGroupModel
import com.github.psurkov.repeservice.repository.InviteRepository
import com.github.psurkov.repeservice.repository.StudentRepository
import com.github.psurkov.repeservice.repository.StudyGroupRepository
import com.github.psurkov.repeservice.repository.TutorRepository
import com.github.psurkov.repeservice.service.StudyGroupService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Isolation
import org.springframework.transaction.annotation.Transactional

@Transactional(isolation = Isolation.SERIALIZABLE)
@Service
class StudyGroupServiceImpl(
    private val tutorRepository: TutorRepository,
    private val studentRepository: StudentRepository,
    private val studyGroupRepository: StudyGroupRepository,
    private val inviteRepository: InviteRepository,
) : StudyGroupService {

    override suspend fun createNewStudyGroup(createModel: CreateStudyGroupModel): StudyGroupModel {
        tutorRepository.findById(createModel.ownerTutorId) ?: throw NotFoundTutor()
        return studyGroupRepository.insert(createModel)
    }

    override suspend fun inviteToStudyGroup(
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

    override suspend fun excludeFromStudyGroup(
        studyGroupId: Long,
        studentId: Long,
    ) {
        studentRepository.findById(studentId) ?: throw NotFoundStudent()
        val studyGroup = studyGroupRepository.findById(studyGroupId) ?: throw NotFoundStudyGroup()
        if (!studyGroup.participantIds.contains(studentId)) {
            throw StudentAbsentInStudyGroup()
        }
        studyGroupRepository.deleteParticipant(studyGroupId, studentId)
    }

    override suspend fun acceptInviteToStudyGroup(
        id: Long
    ) {
        val invite = inviteRepository.findById(id) ?: throw NotFoundInvite()
        inviteRepository.updateInviteStatus(id, InviteStatus.ACCEPTED)
        studyGroupRepository.addParticipant(invite.studyGroupId, invite.studentId)
    }

    override suspend fun rejectInviteToStudyGroup(
        id: Long
    ) {
        inviteRepository.findById(id) ?: throw NotFoundInvite()
        inviteRepository.updateInviteStatus(id, InviteStatus.REJECTED)
    }
}
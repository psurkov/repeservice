package com.github.psurkov.repeservice.service

import com.github.psurkov.repeservice.exception.AlreadyExistsStudent
import com.github.psurkov.repeservice.exception.NotFoundInvite
import com.github.psurkov.repeservice.model.invite.InviteStatus
import com.github.psurkov.repeservice.model.user.CreateStudentModel
import com.github.psurkov.repeservice.model.user.StudentModel
import com.github.psurkov.repeservice.repository.InviteRepository
import com.github.psurkov.repeservice.repository.StudentRepository
import com.github.psurkov.repeservice.repository.StudyGroupRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Isolation
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(isolation = Isolation.SERIALIZABLE)
class StudentService(
    private val studentRepository: StudentRepository,
    private val inviteRepository: InviteRepository,
    private val studyGroupRepository: StudyGroupRepository,
) {

    @Throws(AlreadyExistsStudent::class)
    suspend fun createNew(createStudentModel: CreateStudentModel): StudentModel {
        if (studentRepository.findByUsername(createStudentModel.username) != null) {
            throw AlreadyExistsStudent()
        }
        return studentRepository.insert(createStudentModel)
    }

    @Throws(NotFoundInvite::class)
    suspend fun acceptInvite(
        id: Long
    ) {
        val invite = inviteRepository.findById(id) ?: throw NotFoundInvite()
        inviteRepository.updateInviteStatus(id, InviteStatus.ACCEPTED)
        studyGroupRepository.addParticipant(invite.studyGroupId, invite.studentId)
    }

    @Throws(NotFoundInvite::class)
    suspend fun rejectInvite(
        id: Long
    ) {
        inviteRepository.findById(id) ?: throw NotFoundInvite()
        inviteRepository.updateInviteStatus(id, InviteStatus.REJECTED)
    }

}
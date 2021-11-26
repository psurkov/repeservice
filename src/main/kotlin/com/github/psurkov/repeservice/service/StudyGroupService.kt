package com.github.psurkov.repeservice.service

import com.github.psurkov.repeservice.model.invite.InviteModel
import com.github.psurkov.repeservice.model.studygroup.CreateStudyGroupModel
import com.github.psurkov.repeservice.model.studygroup.StudyGroupModel
import org.springframework.stereotype.Service

interface StudyGroupService {
    suspend fun createNewStudyGroup(createModel: CreateStudyGroupModel): StudyGroupModel

    suspend fun inviteToStudyGroup(
        studyGroupId: Long,
        studentId: Long,
    ): InviteModel

    suspend fun excludeFromStudyGroup(
        studyGroupId: Long,
        studentId: Long,
    )

    suspend fun acceptInviteToStudyGroup(
        id: Long
    )

    suspend fun rejectInviteToStudyGroup(
        id: Long
    )
}
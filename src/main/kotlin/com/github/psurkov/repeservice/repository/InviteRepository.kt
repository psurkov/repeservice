package com.github.psurkov.repeservice.repository

import com.github.psurkov.repeservice.model.invite.InviteModel
import com.github.psurkov.repeservice.model.invite.InviteStatus

interface InviteRepository {
    suspend fun findPendingForStudent(studentId: Long): List<InviteModel>

    suspend fun insertPendingInvite(studyGroupId: Long, studentId: Long): InviteModel

    suspend fun findById(id: Long): InviteModel?
    fun updateInviteStatus(id: Long, status: InviteStatus)
}
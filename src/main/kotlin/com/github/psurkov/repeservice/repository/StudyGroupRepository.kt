package com.github.psurkov.repeservice.repository

import com.github.psurkov.repeservice.model.studygroup.CreateStudyGroupModel
import com.github.psurkov.repeservice.model.studygroup.StudyGroupModel

interface StudyGroupRepository {
    suspend fun findById(id: Long): StudyGroupModel?

    suspend fun insert(createStudyGroupModel: CreateStudyGroupModel): StudyGroupModel
    fun addParticipant(studyGroupId: Long, studentId: Long)
    fun deleteParticipant(studyGroupId: Long, studentId: Long)
}
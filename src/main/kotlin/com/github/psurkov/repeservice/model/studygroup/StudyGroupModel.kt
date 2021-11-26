package com.github.psurkov.repeservice.model.studygroup

data class StudyGroupModel(
    val id: Long,
    val name: String,
    val ownerTutorId: Long,
    val participantIds: List<Long>
)
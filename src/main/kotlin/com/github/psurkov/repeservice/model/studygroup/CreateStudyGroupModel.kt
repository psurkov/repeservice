package com.github.psurkov.repeservice.model.studygroup

data class CreateStudyGroupModel(
    val ownerTutorId: Long,
    val name: String,
)
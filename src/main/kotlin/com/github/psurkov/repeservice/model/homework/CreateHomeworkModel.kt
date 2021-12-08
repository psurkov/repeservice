package com.github.psurkov.repeservice.model.homework

import kotlinx.datetime.LocalDateTime

data class CreateHomeworkModel(
    val name: String,
    val studyGroupId: Long,
    val deadline: LocalDateTime,
)
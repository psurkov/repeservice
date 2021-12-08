package com.github.psurkov.repeservice.model.homework

import kotlinx.datetime.LocalDateTime

data class HomeworkModel(
    val id: Long,
    val name: String,
    val studyGroupId: Long,
    val taskFileId: String,
    val deadline: LocalDateTime,
)
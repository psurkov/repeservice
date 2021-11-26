package com.github.psurkov.repeservice.model.invite

data class InviteModel(
    val id: Long,
    val studyGroupId: Long,
    val studentId: Long,
    val status: InviteStatus,
)
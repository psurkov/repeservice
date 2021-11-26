package com.github.psurkov.repeservice.model.calendar.groupevent

data class CreateGroupEventModel(
    val groupId: Long,
    val name: String,
    val description: String,
    val link: String,
)
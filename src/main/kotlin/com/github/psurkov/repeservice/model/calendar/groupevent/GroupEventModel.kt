package com.github.psurkov.repeservice.model.calendar.groupevent

data class GroupEventModel(
    val eventId: Long,
    val groupId: Long,
    val name: String,
    val description: String,
    val link: String,
)
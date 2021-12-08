package com.github.psurkov.repeservice.service

import com.github.psurkov.repeservice.model.calendar.groupevent.CreateGroupEventModel
import com.github.psurkov.repeservice.model.calendar.groupevent.CreateGroupEventTimeModel
import com.github.psurkov.repeservice.model.calendar.groupevent.GroupEventModel
import com.github.psurkov.repeservice.model.calendar.groupevent.GroupEventTimeModel
import kotlinx.datetime.LocalDateTime

interface CalendarService {
    suspend fun createGroupEvent(createGroupModel: CreateGroupEventModel): GroupEventModel
    suspend fun updateGroupModel(groupEventModel: GroupEventModel)
    suspend fun deleteGroupEvent(eventId: Long)

    suspend fun findStartMomentsOfEventBetween(
        eventId: Long,
        from: LocalDateTime,
        to: LocalDateTime
    ): List<LocalDateTime>

    suspend fun addEventTime(createGroupEventTimeModel: CreateGroupEventTimeModel): GroupEventTimeModel
    suspend fun deleteGroupEventTime(groupEventTimeId: Long)
}
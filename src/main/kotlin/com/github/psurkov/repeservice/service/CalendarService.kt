package com.github.psurkov.repeservice.service

import com.github.psurkov.repeservice.model.calendar.groupevent.CreateGroupEventModel
import com.github.psurkov.repeservice.model.calendar.groupevent.CreateGroupEventTimeModel
import com.github.psurkov.repeservice.model.calendar.groupevent.GroupEventModel
import com.github.psurkov.repeservice.model.calendar.groupevent.GroupEventTimeModel

interface CalendarService {
    suspend fun createGroupEvent(createGroupModel: CreateGroupEventModel): GroupEventModel
    suspend fun updateGroupModel(groupEventModel: GroupEventModel)
    suspend fun deleteGroupEvent(eventId: Long)

    suspend fun addEventTime(createGroupEventTimeModel: CreateGroupEventTimeModel): GroupEventTimeModel
    suspend fun deleteGroupEventTime(groupEventTimeId: Long)
}
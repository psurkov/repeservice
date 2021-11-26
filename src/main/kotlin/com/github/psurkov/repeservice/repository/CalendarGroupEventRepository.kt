package com.github.psurkov.repeservice.repository

import com.github.psurkov.repeservice.model.calendar.groupevent.CreateGroupEventModel
import com.github.psurkov.repeservice.model.calendar.groupevent.CreateGroupEventTimeModel
import com.github.psurkov.repeservice.model.calendar.groupevent.GroupEventModel
import com.github.psurkov.repeservice.model.calendar.groupevent.GroupEventTimeModel

interface CalendarGroupEventRepository {
    suspend fun findGroupEventById(groupEventId: Long): GroupEventModel?
    suspend fun insertGroupEvent(createGroupEventModel: CreateGroupEventModel): GroupEventModel
    suspend fun updateGroupEvent(groupEventModel: GroupEventModel)
    suspend fun deleteGroupEvent(groupEventId: Long)

    suspend fun findGroupEventTimeById(groupEventTimeId: Long): GroupEventTimeModel?
    suspend fun findEventTimesOfEvent(eventId: Long): List<GroupEventTimeModel>
    suspend fun insertGroupEventTime(createGroupEventTimeModel: CreateGroupEventTimeModel): GroupEventTimeModel
    suspend fun deleteGroupEventTime(groupEventTimeId: Long)
}
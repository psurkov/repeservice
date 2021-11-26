package com.github.psurkov.repeservice.service.impl

import com.github.psurkov.repeservice.exception.NotFoundEvent
import com.github.psurkov.repeservice.exception.NotFoundEventTime
import com.github.psurkov.repeservice.exception.NotFoundStudyGroup
import com.github.psurkov.repeservice.model.calendar.groupevent.CreateGroupEventModel
import com.github.psurkov.repeservice.model.calendar.groupevent.CreateGroupEventTimeModel
import com.github.psurkov.repeservice.model.calendar.groupevent.GroupEventModel
import com.github.psurkov.repeservice.model.calendar.groupevent.GroupEventTimeModel
import com.github.psurkov.repeservice.repository.CalendarGroupEventRepository
import com.github.psurkov.repeservice.repository.StudyGroupRepository
import com.github.psurkov.repeservice.service.CalendarService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Isolation
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(isolation = Isolation.SERIALIZABLE)
class CalendarServiceImpl(
    private val calendarGroupEventRepository: CalendarGroupEventRepository,
    private val studyGroupRepository: StudyGroupRepository,
) : CalendarService {
    override suspend fun createGroupEvent(createGroupModel: CreateGroupEventModel): GroupEventModel {
        studyGroupRepository.findById(createGroupModel.groupId) ?: throw NotFoundStudyGroup()
        return calendarGroupEventRepository.insertGroupEvent(createGroupModel)
    }

    override suspend fun updateGroupModel(groupEventModel: GroupEventModel) {
        calendarGroupEventRepository.findGroupEventById(groupEventModel.eventId) ?: throw NotFoundEvent()
        studyGroupRepository.findById(groupEventModel.groupId) ?: throw NotFoundStudyGroup()
        calendarGroupEventRepository.updateGroupEvent(groupEventModel)
    }

    override suspend fun deleteGroupEvent(eventId: Long) {
        calendarGroupEventRepository.findGroupEventById(eventId) ?: throw NotFoundEvent()
        calendarGroupEventRepository.deleteGroupEvent(eventId)
    }

    override suspend fun addEventTime(createGroupEventTimeModel: CreateGroupEventTimeModel): GroupEventTimeModel {
        calendarGroupEventRepository.findGroupEventById(createGroupEventTimeModel.eventId) ?: throw NotFoundEvent()
        return calendarGroupEventRepository.insertGroupEventTime(createGroupEventTimeModel)
    }

    override suspend fun deleteGroupEventTime(groupEventTimeId: Long) {
        calendarGroupEventRepository.findGroupEventTimeById(groupEventTimeId) ?: throw NotFoundEventTime()
        calendarGroupEventRepository.deleteGroupEventTime(groupEventTimeId)
    }
}
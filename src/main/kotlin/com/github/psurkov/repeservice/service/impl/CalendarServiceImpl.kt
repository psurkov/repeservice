package com.github.psurkov.repeservice.service.impl

import com.github.psurkov.repeservice.exception.NotFoundEvent
import com.github.psurkov.repeservice.exception.NotFoundEventTime
import com.github.psurkov.repeservice.exception.NotFoundStudyGroup
import com.github.psurkov.repeservice.model.calendar.RepeatType
import com.github.psurkov.repeservice.model.calendar.groupevent.CreateGroupEventModel
import com.github.psurkov.repeservice.model.calendar.groupevent.CreateGroupEventTimeModel
import com.github.psurkov.repeservice.model.calendar.groupevent.GroupEventModel
import com.github.psurkov.repeservice.model.calendar.groupevent.GroupEventTimeModel
import com.github.psurkov.repeservice.repository.CalendarGroupEventRepository
import com.github.psurkov.repeservice.repository.StudyGroupRepository
import com.github.psurkov.repeservice.service.CalendarService
import kotlinx.datetime.*
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Isolation
import org.springframework.transaction.annotation.Transactional
import kotlin.time.Duration.Companion.days

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

    override suspend fun findStartMomentsOfEventBetween(
        eventId: Long,
        from: LocalDateTime,
        to: LocalDateTime
    ): List<LocalDateTime> = calendarGroupEventRepository.findEventTimesOfEvent(eventId)
        .flatMap { eventTime ->
            generateTimeSequence(eventTime)
                .dropWhile { it < from }
                .takeWhile { it <= to }
        }.sorted()

    private fun generateTimeSequence(eventTime: GroupEventTimeModel) = when (eventTime.repeatType) {
        RepeatType.SINGLE -> sequenceOf(eventTime.startTime)
        RepeatType.EVERY_DAY -> generateSequence(eventTime.startTime) {
            it.toInstant(TimeZone.UTC).plus(1.days).toLocalDateTime(TimeZone.UTC)
        }
        RepeatType.EVERY_WEEK -> generateSequence(eventTime.startTime) {
            it.toInstant(TimeZone.UTC).plus(7.days).toLocalDateTime(TimeZone.UTC)
        }
        RepeatType.EVERY_MONTH -> generateSequence(eventTime.startTime) {
            it.toInstant(TimeZone.UTC).plus(1, DateTimeUnit.MONTH, TimeZone.UTC).toLocalDateTime(TimeZone.UTC)
        }
    }

}
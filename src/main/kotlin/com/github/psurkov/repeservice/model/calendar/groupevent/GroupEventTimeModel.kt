package com.github.psurkov.repeservice.model.calendar.groupevent

import com.github.psurkov.repeservice.model.calendar.RepeatType
import kotlinx.datetime.LocalDateTime
import kotlin.time.Duration

class GroupEventTimeModel(
    val eventTimeId: Long,
    val eventId: Long,
    val startTime: LocalDateTime,
    val duration: Duration,
    val repeatType: RepeatType
)
package com.github.psurkov.repeservice.controller

import com.github.psurkov.repeservice.model.calendar.groupevent.CreateGroupEventModel
import com.github.psurkov.repeservice.model.calendar.groupevent.CreateGroupEventTimeModel
import com.github.psurkov.repeservice.model.calendar.groupevent.GroupEventModel
import com.github.psurkov.repeservice.model.calendar.groupevent.GroupEventTimeModel
import com.github.psurkov.repeservice.service.CalendarService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
class CalendarController(private val calendarService: CalendarService) {

    @PostMapping("/calendar/group/event")
    suspend fun createNewGroupEvent(
        @RequestBody createGroupEventModel: CreateGroupEventModel,
    ): ResponseEntity<GroupEventModel> {
        val groupEventModel = calendarService.createGroupEvent(createGroupEventModel)
        return ResponseEntity(groupEventModel, HttpStatus.CREATED)
    }

    @PutMapping("/calendar/group/event")
    suspend fun updateGroupEvent(
        @RequestBody groupEventModel: GroupEventModel,
    ): ResponseEntity<Unit> {
        calendarService.updateGroupModel(groupEventModel)
        return ResponseEntity(null, HttpStatus.OK)
    }

    @DeleteMapping("/calendar/group/event")
    suspend fun deleteGroupEvent(
        @RequestParam eventId: Long,
    ): ResponseEntity<Unit> {
        calendarService.deleteGroupEvent(eventId)
        return ResponseEntity(null, HttpStatus.OK)
    }

    @PostMapping("/calendar/eventtime")
    suspend fun addEventTime(
        @RequestBody createGroupEventTimeModel: CreateGroupEventTimeModel,
    ): ResponseEntity<GroupEventTimeModel> {
        val eventTimeModel = calendarService.addEventTime(createGroupEventTimeModel)
        return ResponseEntity(eventTimeModel, HttpStatus.CREATED)
    }

    @DeleteMapping("/calendar/eventtime")
    suspend fun deleteEventTime(
        @RequestParam eventId: Long,
    ): ResponseEntity<Unit> {
        calendarService.deleteGroupEvent(eventId)
        return ResponseEntity(null, HttpStatus.OK)
    }
}
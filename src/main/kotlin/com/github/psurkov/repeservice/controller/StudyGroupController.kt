package com.github.psurkov.repeservice.controller

import com.github.psurkov.repeservice.model.invite.InviteModel
import com.github.psurkov.repeservice.model.studygroup.CreateStudyGroupModel
import com.github.psurkov.repeservice.model.studygroup.StudyGroupModel
import com.github.psurkov.repeservice.service.StudyGroupService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class StudyGroupController(
    private val studyGroupService: StudyGroupService
) {

    @PostMapping("/create/")
    suspend fun createNewStudyGroup(
        @RequestBody createStudyGroupModel: CreateStudyGroupModel
    ): ResponseEntity<StudyGroupModel> {
        val studyGroup = studyGroupService.createNewStudyGroup(createStudyGroupModel)
        return ResponseEntity(studyGroup, HttpStatus.OK)
    }

    @PostMapping("/invite/")
    suspend fun invite(
        @RequestParam studyGroupId: Long,
        @RequestParam studentId: Long,
    ): ResponseEntity<InviteModel> {
        val invite = studyGroupService.invite(studentId, studentId)
        return ResponseEntity(invite, HttpStatus.OK)
    }

    @PostMapping("/exclude/")
    suspend fun exclude(
        @RequestParam studyGroupId: Long,
        @RequestParam studentId: Long,
    ): ResponseEntity<Void> {
        studyGroupService.exclude(studentId, studentId)
        return ResponseEntity(null, HttpStatus.OK)
    }
}
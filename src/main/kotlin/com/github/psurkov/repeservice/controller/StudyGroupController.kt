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

    @PostMapping("studygroup/create/")
    suspend fun createNewStudyGroup(
        @RequestBody createStudyGroupModel: CreateStudyGroupModel
    ): ResponseEntity<StudyGroupModel> {
        val studyGroup = studyGroupService.createNewStudyGroup(createStudyGroupModel)
        return ResponseEntity(studyGroup, HttpStatus.CREATED)
    }

    @PostMapping("studygroup/invite/")
    suspend fun invite(
        @RequestParam studyGroupId: Long,
        @RequestParam studentId: Long,
    ): ResponseEntity<InviteModel> {
        val invite = studyGroupService.inviteToStudyGroup(studentId, studentId)
        return ResponseEntity(invite, HttpStatus.OK)
    }

    @PostMapping("studygroup/exclude/")
    suspend fun exclude(
        @RequestParam studyGroupId: Long,
        @RequestParam studentId: Long,
    ): ResponseEntity<Void> {
        studyGroupService.excludeFromStudyGroup(studentId, studentId)
        return ResponseEntity(null, HttpStatus.OK)
    }

    @PostMapping("studygroup/invite/accept")
    suspend fun acceptInvite(
        @RequestParam inviteId: Long,
    ): ResponseEntity<Void> {
        studyGroupService.acceptInviteToStudyGroup(inviteId)
        return ResponseEntity(null, HttpStatus.OK)
    }

    @PostMapping("studygroup/invite/reject")
    suspend fun rejectInvite(
        @RequestParam inviteId: Long,
    ): ResponseEntity<Void> {
        studyGroupService.rejectInviteToStudyGroup(inviteId)
        return ResponseEntity(null, HttpStatus.OK)
    }
}
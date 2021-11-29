package com.github.psurkov.repeservice.controller

import com.github.psurkov.repeservice.model.user.CreateTutorModel
import com.github.psurkov.repeservice.model.user.TutorModel
import com.github.psurkov.repeservice.service.TutorService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class TutorController(private val tutorService: TutorService) {

    @PostMapping("/tutor/create")
    suspend fun createNewTutor(
        @RequestBody createTutorModel: CreateTutorModel
    ): ResponseEntity<TutorModel> {
        val tutorModel = tutorService.createNew(createTutorModel)
        return ResponseEntity(tutorModel, HttpStatus.CREATED)
    }
}
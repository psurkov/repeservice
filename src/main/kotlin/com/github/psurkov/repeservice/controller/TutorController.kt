package com.github.psurkov.repeservice.controller

import com.github.psurkov.repeservice.model.Tutor
import com.github.psurkov.repeservice.repository.TutorRepository
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class TutorController(private val tutorRepository: TutorRepository) {

    @GetMapping("/tutor/all")
    suspend fun findAll(): ResponseEntity<List<Tutor>> {
        return ResponseEntity(tutorRepository.findAll(), HttpStatus.OK)
    }
}
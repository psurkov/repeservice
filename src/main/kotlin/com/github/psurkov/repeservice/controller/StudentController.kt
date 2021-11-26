package com.github.psurkov.repeservice.controller

import com.github.psurkov.repeservice.model.user.CreateStudentModel
import com.github.psurkov.repeservice.model.user.StudentModel
import com.github.psurkov.repeservice.service.StudentService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class StudentController(private val studentService: StudentService) {

    @PostMapping("/student/create")
    suspend fun createNewStudent(
        @RequestBody createStudentModel: CreateStudentModel
    ): ResponseEntity<StudentModel> {
        val studentModel = studentService.createNew(createStudentModel)
        return ResponseEntity(studentModel, HttpStatus.CREATED)
    }
}
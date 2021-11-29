package com.github.psurkov.repeservice.service

import com.github.psurkov.repeservice.model.user.CreateStudentModel
import com.github.psurkov.repeservice.model.user.StudentModel
import org.springframework.stereotype.Service

interface StudentService {
    suspend fun createNew(createStudentModel: CreateStudentModel): StudentModel
}
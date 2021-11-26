package com.github.psurkov.repeservice.repository

import com.github.psurkov.repeservice.model.user.CreateStudentModel
import com.github.psurkov.repeservice.model.user.StudentModel

interface StudentRepository {
    suspend fun findById(id: Long): StudentModel?

    suspend fun findByUsername(username: String): StudentModel?

    suspend fun insert(createStudentModel: CreateStudentModel): StudentModel
}
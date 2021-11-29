package com.github.psurkov.repeservice.repository

import com.github.psurkov.repeservice.model.user.CreateTutorModel
import com.github.psurkov.repeservice.model.user.TutorModel

interface TutorRepository {
    suspend fun findById(id: Long): TutorModel?

    suspend fun findByUsername(username: String): TutorModel?

    suspend fun insert(createTutorModel: CreateTutorModel): TutorModel
}
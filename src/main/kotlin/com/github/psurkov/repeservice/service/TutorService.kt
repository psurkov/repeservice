package com.github.psurkov.repeservice.service

import com.github.psurkov.repeservice.model.user.CreateTutorModel
import com.github.psurkov.repeservice.model.user.TutorModel

interface TutorService {
    suspend fun createNew(createTutorModel: CreateTutorModel): TutorModel
}
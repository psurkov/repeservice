package com.github.psurkov.repeservice.service

import com.github.psurkov.repeservice.model.CreateTutorModel
import com.github.psurkov.repeservice.model.TutorModel
import com.github.psurkov.repeservice.repository.TutorRepository
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Isolation
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.ResponseStatus

@Service
class TutorService(
    private val tutorRepository: TutorRepository
) {
    @ResponseStatus(value = HttpStatus.CONFLICT, reason = "Tutor already exists")
    class AlreadyExistsTutor : IllegalArgumentException()

    @Throws(AlreadyExistsTutor::class)
    @Transactional(isolation = Isolation.SERIALIZABLE)
    suspend fun createNew(createTutorModel: CreateTutorModel): TutorModel {
        if (tutorRepository.findByUsername(createTutorModel.username) != null) {
            throw AlreadyExistsTutor()
        }
        return tutorRepository.insert(createTutorModel)
    }

}
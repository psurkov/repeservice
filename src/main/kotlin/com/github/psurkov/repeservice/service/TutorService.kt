package com.github.psurkov.repeservice.service

import com.github.psurkov.repeservice.exception.AlreadyExistsTutor
import com.github.psurkov.repeservice.model.user.CreateTutorModel
import com.github.psurkov.repeservice.model.user.TutorModel
import com.github.psurkov.repeservice.repository.TutorRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Isolation
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(isolation = Isolation.SERIALIZABLE)
class TutorService(
    private val tutorRepository: TutorRepository
) {

    @Throws(AlreadyExistsTutor::class)
    suspend fun createNew(createTutorModel: CreateTutorModel): TutorModel {
        if (tutorRepository.findByUsername(createTutorModel.username) != null) {
            throw AlreadyExistsTutor()
        }
        return tutorRepository.insert(createTutorModel)
    }

}
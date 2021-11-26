package com.github.psurkov.repeservice.service.impl

import com.github.psurkov.repeservice.exception.AlreadyExistsTutor
import com.github.psurkov.repeservice.model.user.CreateTutorModel
import com.github.psurkov.repeservice.model.user.TutorModel
import com.github.psurkov.repeservice.repository.TutorRepository
import com.github.psurkov.repeservice.service.TutorService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Isolation
import org.springframework.transaction.annotation.Transactional

@Transactional(isolation = Isolation.SERIALIZABLE)
@Service
class TutorServiceImpl(
    private val tutorRepository: TutorRepository
) : TutorService {

    override suspend fun createNew(createTutorModel: CreateTutorModel): TutorModel {
        if (tutorRepository.findByUsername(createTutorModel.username) != null) {
            throw AlreadyExistsTutor()
        }
        return tutorRepository.insert(createTutorModel)
    }

}
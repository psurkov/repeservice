package com.github.psurkov.repeservice.service

import com.github.psurkov.repeservice.exception.AlreadyExistsStudent
import com.github.psurkov.repeservice.model.user.CreateStudentModel
import com.github.psurkov.repeservice.model.user.StudentModel
import com.github.psurkov.repeservice.repository.StudentRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Isolation
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(isolation = Isolation.SERIALIZABLE)
class StudentService(
    private val studentRepository: StudentRepository
) {

    @Throws(AlreadyExistsStudent::class)
    suspend fun createNew(createStudentModel: CreateStudentModel): StudentModel {
        if (studentRepository.findByUsername(createStudentModel.username) != null) {
            throw AlreadyExistsStudent()
        }
        return studentRepository.insert(createStudentModel)
    }

}
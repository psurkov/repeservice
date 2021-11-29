package com.github.psurkov.repeservice.service.impl

import com.github.psurkov.repeservice.exception.AlreadyExistsStudent
import com.github.psurkov.repeservice.model.user.CreateStudentModel
import com.github.psurkov.repeservice.model.user.StudentModel
import com.github.psurkov.repeservice.repository.StudentRepository
import com.github.psurkov.repeservice.service.StudentService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Isolation
import org.springframework.transaction.annotation.Transactional

@Transactional(isolation = Isolation.SERIALIZABLE)
@Service
class StudentServiceImpl(
    private val studentRepository: StudentRepository,
) : StudentService {

    override suspend fun createNew(createStudentModel: CreateStudentModel): StudentModel {
        if (studentRepository.findByUsername(createStudentModel.username) != null) {
            throw AlreadyExistsStudent()
        }
        return studentRepository.insert(createStudentModel)
    }

}
package com.github.psurkov.repeservice.exception

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(value = HttpStatus.NOT_FOUND)
class NotFoundTutor : Exception()

@ResponseStatus(value = HttpStatus.NOT_FOUND)
class NotFoundStudent : Exception()

@ResponseStatus(value = HttpStatus.NOT_FOUND)
class NotFoundStudyGroup : Exception()

@ResponseStatus(value = HttpStatus.NOT_FOUND)
class NotFoundInvite : Exception()

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
class AlreadyExistsTutor : Exception()

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
class AlreadyExistsStudent : Exception()

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
class StudentAlreadyInStudyGroup : Exception()

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
class AlreadyPendingInvite : Exception()

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
class StudentAbsentInStudyGroup : Exception()
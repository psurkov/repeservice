package com.github.psurkov.repeservice.exception

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
class NotFoundTutor : Exception()

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
class NotFoundStudent : Exception()

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
class AlreadyExistsTutor : Exception()

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
class AlreadyExistsStudent : Exception()
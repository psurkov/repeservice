package com.github.psurkov.repeservice.exception

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(value = HttpStatus.NOT_FOUND)
class NotFoundTutor : RuntimeException()

@ResponseStatus(value = HttpStatus.NOT_FOUND)
class NotFoundStudent : RuntimeException()

@ResponseStatus(value = HttpStatus.NOT_FOUND)
class NotFoundStudyGroup : RuntimeException()

@ResponseStatus(value = HttpStatus.NOT_FOUND)
class NotFoundInvite : RuntimeException()

@ResponseStatus(value = HttpStatus.NOT_FOUND)
class NotFoundEvent : RuntimeException()

@ResponseStatus(value = HttpStatus.NOT_FOUND)
class NotFoundEventTime : RuntimeException()

@ResponseStatus(value = HttpStatus.NOT_FOUND)
class NotFoundPayment : RuntimeException()

@ResponseStatus(value = HttpStatus.NOT_FOUND)
class NotFoundBasicPrice : RuntimeException()

@ResponseStatus(value = HttpStatus.NOT_FOUND)
class NotFoundHomework : RuntimeException()

@ResponseStatus(value = HttpStatus.NOT_FOUND)
class NotFoundTaskFile : RuntimeException()

@ResponseStatus(value = HttpStatus.NOT_FOUND)
class NotFoundTaskSolutionFile : RuntimeException()

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
class AlreadyExistsTutor : RuntimeException()

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
class AlreadyExistsStudent : RuntimeException()

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
class StudentAlreadyInStudyGroup : RuntimeException()

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
class AlreadyPendingInvite : RuntimeException()

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
class AlreadyPaid : RuntimeException()

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
class AlreadyExistsBasicPrice : RuntimeException()

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
class AlreadyExistsHomeworkSolution : RuntimeException()

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
class StudentAbsentInStudyGroup : RuntimeException()

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
class SolutionAfterDeadline : RuntimeException()
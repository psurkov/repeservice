package com.github.psurkov.repeservice.model.homework

data class HomeworkSolutionModel(
    val id: Long,
    val studentId: Long,
    val homeworkId: Long,
    val solutionFileId: String,
)
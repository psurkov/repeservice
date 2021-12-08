package com.github.psurkov.repeservice.controller

import com.github.psurkov.repeservice.model.homework.CreateHomeworkModel
import com.github.psurkov.repeservice.model.homework.HomeworkModel
import com.github.psurkov.repeservice.model.homework.HomeworkSolutionModel
import com.github.psurkov.repeservice.service.HomeworkService
import org.springframework.core.io.Resource
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@RestController
class HomeworkController(private val homeworkService: HomeworkService) {

    @PostMapping("/homework/create")
    suspend fun createHomework(
        @RequestBody createHomeworkModel: CreateHomeworkModel,
        @RequestParam task: MultipartFile,
    ): ResponseEntity<HomeworkModel> {
        val homeworkModel = homeworkService.createHomework(createHomeworkModel, task)
        return ResponseEntity(homeworkModel, HttpStatus.CREATED)
    }

    @PostMapping("/homework/submit")
    suspend fun submitSolution(
        @RequestParam studentId: Long,
        @RequestParam homeworkId: Long,
        @RequestParam homeworkSolution: MultipartFile,
    ): ResponseEntity<Unit> {
        homeworkService.submitSolution(studentId, homeworkId, homeworkSolution)
        return ResponseEntity(Unit, HttpStatus.CREATED)
    }


    @GetMapping("/homework/list-solutions")
    suspend fun listSolutions(
        @RequestParam homeworkId: Long,
    ): ResponseEntity<List<HomeworkSolutionModel>> {
        val homeworkModel = homeworkService.listSolutions(homeworkId)
        return ResponseEntity(homeworkModel, HttpStatus.OK)
    }

    @GetMapping("/homework/task")
    @ResponseBody
    suspend fun downloadTask(
        @RequestParam taskFileId: String
    ): ResponseEntity<Resource> {
        val homeworkModel = homeworkService.getTaskResource(taskFileId)
        return ResponseEntity(homeworkModel, HttpStatus.OK)
    }

    @GetMapping("/homework/solution")
    @ResponseBody
    suspend fun downloadSolution(
        @RequestParam solutionFileId: String
    ): ResponseEntity<Resource> {
        val homeworkModel = homeworkService.getSolutionResource(solutionFileId)
        return ResponseEntity(homeworkModel, HttpStatus.OK)
    }

}
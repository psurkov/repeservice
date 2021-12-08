package com.github.psurkov.repeservice.repository

import org.springframework.core.io.Resource
import org.springframework.web.multipart.MultipartFile

interface FileStorageRepository {
    fun store(file: MultipartFile): String
    fun load(fileId: String): Resource
    fun init()
}
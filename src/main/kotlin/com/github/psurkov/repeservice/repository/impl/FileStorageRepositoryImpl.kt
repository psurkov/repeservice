package com.github.psurkov.repeservice.repository.impl

import com.github.psurkov.repeservice.repository.FileStorageRepository
import org.springframework.core.io.FileSystemResource
import org.springframework.core.io.Resource
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Isolation
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import java.nio.file.Paths
import java.util.*
import kotlin.io.path.createDirectories
import kotlin.io.path.createDirectory

@Repository
@Transactional(isolation = Isolation.SERIALIZABLE)
class FileStorageRepositoryImpl : FileStorageRepository {
    override fun store(file: MultipartFile): String {
        val id = UUID.randomUUID().toString()
        val filepath = path.resolve(id)
            .createDirectory()
            .resolve(file.name)
        file.transferTo(filepath)
        return id
    }

    override fun load(fileId: String): Resource {
        val file = path.resolve(fileId).toFile().listFiles()!!.first()
        return FileSystemResource(file)
    }

    override fun init() {
        path.toFile().deleteRecursively()
        path.createDirectories()
    }

    companion object {
        private val path = Paths.get("file-storage/")
    }
}
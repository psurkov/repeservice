package com.github.psurkov.repeservice

import com.github.psurkov.repeservice.repository.FileStorageRepository
import com.github.psurkov.repeservice.repository.impl.initDatabase
import org.jetbrains.exposed.spring.SpringTransactionManager
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.boot.runApplication
import org.springframework.context.ApplicationListener
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.EnableTransactionManagement
import javax.sql.DataSource

@SpringBootApplication
@EnableTransactionManagement
class RepeserviceApplication {
    @Bean
    fun transactionManager(dataSource: DataSource) = SpringTransactionManager(dataSource)
}

@Component
class ApplicationStartup(
    @Autowired val fileStorageRepository: FileStorageRepository
) : ApplicationListener<ApplicationReadyEvent> {
    override fun onApplicationEvent(event: ApplicationReadyEvent) {
        fileStorageRepository.init()
        initDatabase()
    }
}


fun main(args: Array<String>) {
    runApplication<RepeserviceApplication>(*args)
}

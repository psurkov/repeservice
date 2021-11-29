package com.github.psurkov.repeservice

import com.github.psurkov.repeservice.repository.impl.initDatabase
import org.jetbrains.exposed.spring.SpringTransactionManager
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
class ApplicationStartup : ApplicationListener<ApplicationReadyEvent> {
    override fun onApplicationEvent(event: ApplicationReadyEvent) {
        initDatabase()
    }
}


fun main(args: Array<String>) {
    runApplication<RepeserviceApplication>(*args)
}

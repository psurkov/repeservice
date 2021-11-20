import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.springframework.boot.gradle.tasks.bundling.BootBuildImage

plugins {
    id("org.springframework.boot") version "2.6.0"
    id("io.spring.dependency-management") version "1.0.11.RELEASE"
    kotlin("jvm") version "1.6.0"
    kotlin("plugin.spring") version "1.6.0"
    id("org.springframework.experimental.aot") version "0.11.0-RC1"
}

group = "com.github.psurkov"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_11

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    maven { url = uri("https://repo.spring.io/milestone") }
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web:2.6.0")
    implementation("org.springframework.boot:spring-boot-starter-webflux:2.6.0")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa:2.6.0")
    implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
    implementation("org.jetbrains.exposed:exposed-core:0.36.2")
    implementation("org.jetbrains.exposed:spring-transaction:0.36.2")
    implementation("org.springdoc:springdoc-openapi-kotlin:1.5.12")
    implementation("org.springdoc:springdoc-openapi-ui:1.5.12")
    developmentOnly("org.springframework.boot:spring-boot-devtools:2.6.0")
    runtimeOnly("org.postgresql:postgresql:42.3.1")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor:2.6.0")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("io.projectreactor:reactor-test")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "11"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.withType<BootBuildImage> {
    builder = "paketobuildpacks/builder:tiny"
    environment = mapOf("BP_NATIVE_IMAGE" to "true")
}

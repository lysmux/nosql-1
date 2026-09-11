plugins {
    kotlin("jvm") version "2.4.20"
    kotlin("plugin.spring") version "2.4.20"
    kotlin("plugin.jpa") version "2.4.20"
    id("org.springframework.boot") version "4.1.1"
    id("io.spring.dependency-management") version "1.1.7"
    id("org.openapi.generator") version "7.14.0"
}

group = "ru.itmo"
version = "1.0.0"

kotlin {
    jvmToolchain(21)
    compilerOptions {
        freeCompilerArgs.add("-Xjsr305=strict")
    }
}

allOpen {
    annotation("jakarta.persistence.Entity")
    annotation("jakarta.persistence.MappedSuperclass")
    annotation("jakarta.persistence.Embeddable")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("tools.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")

    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-liquibase")
    runtimeOnly("org.postgresql:postgresql")

    implementation("org.springframework.boot:spring-boot-starter-actuator")
    runtimeOnly("io.micrometer:micrometer-registry-prometheus")
    runtimeOnly("org.hibernate.orm:hibernate-micrometer")

    // Unpublished, built from github.com/OpenRiak/riak-java-client@73d87d1 (`./gradlew jar`).
    // A plain jar has no POM, so its dependencies are listed by hand; protobuf stays on 3.x as the client was built against it
    implementation(files("libs/riak-client-73d87d1.jar"))
    runtimeOnly("com.google.protobuf:protobuf-java:3.25.9")
    runtimeOnly("io.netty:netty-all")
    runtimeOnly("com.fasterxml.jackson.core:jackson-databind")
    runtimeOnly("com.fasterxml.jackson.datatype:jackson-datatype-joda")
    runtimeOnly("commons-codec:commons-codec")
    runtimeOnly("org.erlang.otp:jinterface:1.6.1")
    runtimeOnly("javax.xml.bind:jaxb-api:2.3.1")
}

val openApiSpec = layout.projectDirectory.file("openapi/generated/openapi.yaml")
val openApiOutput = layout.buildDirectory.dir("generated/openapi")

openApiGenerate {
    generatorName = "kotlin-spring"
    inputSpec = openApiSpec.asFile.invariantSeparatorsPath
    outputDir = openApiOutput.map { it.asFile.invariantSeparatorsPath }

    apiPackage = "ru.itmo.notifications.entrypoint.api"
    modelPackage = "ru.itmo.notifications.entrypoint.api.model"
    modelNameSuffix = "Dto"

    configOptions = mapOf(
        "interfaceOnly" to "true",
        "useSpringBoot3" to "true",
        "useBeanValidation" to "false",
        "documentationProvider" to "none",
        "annotationLibrary" to "none",
        "enumPropertyNaming" to "UPPERCASE",
    )

    typeMappings = mapOf("DateTime" to "Instant")
    importMappings = mapOf("Instant" to "java.time.Instant")

    // Только интерфейсы и модели: вспомогательные файлы генератора тянут свой
    // обработчик ошибок и jakarta.validation, которые здесь не нужны
    globalProperties = mapOf("apis" to "", "models" to "")
    cleanupOutput = true
}

sourceSets.main {
    kotlin.srcDir(openApiOutput.map { it.dir("src/main/kotlin") })
}

tasks.compileKotlin {
    dependsOn(tasks.openApiGenerate)
}

tasks.register<Exec>("tspCompile") {
    workingDir = layout.projectDirectory.dir("openapi").asFile
    val npm = if (System.getProperty("os.name").startsWith("Windows")) "npm.cmd" else "npm"
    commandLine(npm, "run", "build")
}

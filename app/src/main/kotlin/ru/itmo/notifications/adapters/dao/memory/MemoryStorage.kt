package ru.itmo.notifications.adapters.dao.memory

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@ConditionalOnProperty(name = ["app.storage"], havingValue = "memory")
annotation class MemoryStorage

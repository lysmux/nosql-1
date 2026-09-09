package ru.itmo.notifications.adapters.riak

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@ConditionalOnProperty(name = ["app.storage"], havingValue = "riak")
annotation class RiakStorage

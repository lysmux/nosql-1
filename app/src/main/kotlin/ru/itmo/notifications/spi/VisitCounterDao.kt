package ru.itmo.notifications.spi

interface VisitCounterDao {
    fun increment(pageKey: String): Long
}

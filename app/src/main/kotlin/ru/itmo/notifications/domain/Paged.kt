package ru.itmo.notifications.domain

data class Paged<T>(
    val items: List<T>,
    val page: Int,
    val size: Int,
    val totalItems: Long,
) {
    val totalPages: Int = ((totalItems + size - 1) / size).toInt()
}

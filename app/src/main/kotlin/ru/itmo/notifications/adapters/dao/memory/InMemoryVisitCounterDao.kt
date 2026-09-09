package ru.itmo.notifications.adapters.dao.memory

import org.springframework.stereotype.Repository
import ru.itmo.notifications.spi.VisitCounterDao
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicLong

@Repository
@MemoryStorage
class InMemoryVisitCounterDao : VisitCounterDao {
    private val counters = ConcurrentHashMap<String, AtomicLong>()

    override fun increment(pageKey: String): Long =
        counters.computeIfAbsent(pageKey) { AtomicLong() }.incrementAndGet()
}

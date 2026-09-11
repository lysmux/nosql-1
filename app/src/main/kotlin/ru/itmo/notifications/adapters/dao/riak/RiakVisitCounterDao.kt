package ru.itmo.notifications.adapters.dao.riak

import org.springframework.stereotype.Repository
import ru.itmo.notifications.adapters.riak.RiakStorage
import ru.itmo.notifications.adapters.riak.RiakTemplate
import ru.itmo.notifications.spi.VisitCounterDao

@Repository
@RiakStorage
class RiakVisitCounterDao(
    private val riak: RiakTemplate,
) : VisitCounterDao {
    override fun increment(pageKey: String): Long = riak.incrementCounter(BUCKET, pageKey)

    private companion object {
        const val BUCKET = "page_counters"
    }
}

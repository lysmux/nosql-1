package ru.itmo.notifications.adapters.riak

import com.basho.riak.client.api.RiakClient
import com.basho.riak.client.api.RiakCommand
import com.basho.riak.client.api.commands.datatypes.CounterUpdate
import com.basho.riak.client.api.commands.datatypes.FetchCounter
import com.basho.riak.client.api.commands.datatypes.UpdateCounter
import com.basho.riak.client.api.commands.kv.DeleteValue
import com.basho.riak.client.api.commands.kv.FetchValue
import com.basho.riak.client.api.commands.kv.StoreValue
import com.basho.riak.client.core.query.Location
import com.basho.riak.client.core.query.Namespace
import com.basho.riak.client.core.query.RiakObject
import com.basho.riak.client.core.util.BinaryValue
import jakarta.annotation.PostConstruct
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import ru.itmo.notifications.shared.metrics.MetricsAdapter
import tools.jackson.databind.json.JsonMapper
import java.time.Duration
import java.util.concurrent.ExecutionException
import java.util.concurrent.TimeUnit

@Component
class RiakTemplate(
    private val client: RiakClient,
    private val json: JsonMapper,
    @Value("\${app.riak.timeout}") private val timeout: Duration,
    private val metrics: MetricsAdapter,
) {
    @PostConstruct
    fun verifyBucketTypes() {
        requireBucketType(COUNTER_BUCKET_TYPE, "counter") {
            execute(BUCKET_TYPE_PROBE, FetchCounter.Builder(it).build())
        }
    }

    final inline fun <reified T : Any> get(bucket: String, key: String): T? = get(bucket, key, T::class.java)

    fun <T : Any> get(bucket: String, key: String, type: Class<T>): T? {
        val response = execute(bucket, FetchValue.Builder(objectLocation(bucket, key)).build())
        return if (response.isNotFound) null else decode(response, type)
    }

    fun put(bucket: String, key: String, value: Any) {
        val riakObject = RiakObject()
            .setContentType(JSON_CONTENT_TYPE)
            .setValue(BinaryValue.create(json.writeValueAsBytes(value)))
        execute(bucket, StoreValue.Builder(riakObject).withLocation(objectLocation(bucket, key)).build())
    }

    fun delete(bucket: String, key: String) {
        execute(bucket, DeleteValue.Builder(objectLocation(bucket, key)).build())
    }

    fun incrementCounter(bucket: String, key: String, amount: Long = 1): Long {
        val location = Location(Namespace(COUNTER_BUCKET_TYPE, bucket), key)
        val command = UpdateCounter.Builder(location, CounterUpdate(amount)).withReturnDatatype(true).build()
        return execute(bucket, command).datatype.view()
    }

    private fun <T, S> execute(bucket: String, command: RiakCommand<T, S>): T =
        metrics.timed(REQUEST_METRIC, "operation" to command.javaClass.simpleName, "bucket" to bucket) {
            client.execute(command, timeout.toMillis(), TimeUnit.MILLISECONDS)
        }

    private fun <T : Any> decode(response: FetchValue.Response, type: Class<T>): T =
        json.readValue(response.getValue(RiakObject::class.java).value.value, type)

    private fun objectLocation(bucket: String, key: String) = Location(Namespace(bucket), key)

    private fun requireBucketType(name: String, datatype: String, fetch: (Location) -> Unit) {
        try {
            fetch(Location(Namespace(name, BUCKET_TYPE_PROBE), BUCKET_TYPE_PROBE))
        } catch (exception: ExecutionException) {
            throw IllegalStateException(
                "Riak bucket type '$name' with datatype=$datatype is missing, create it on any node: " +
                    "riak admin bucket-type create $name '{\"props\":{\"datatype\":\"$datatype\"}}' " +
                    "&& riak admin bucket-type activate $name",
                exception,
            )
        }
    }

    private companion object {
        const val JSON_CONTENT_TYPE = "application/json"
        const val COUNTER_BUCKET_TYPE = "counters"
        const val BUCKET_TYPE_PROBE = "bucket_type_probe"
        const val REQUEST_METRIC = "riak.requests"
    }
}

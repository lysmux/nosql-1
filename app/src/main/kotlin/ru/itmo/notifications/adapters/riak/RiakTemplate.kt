package ru.itmo.notifications.adapters.riak

import com.basho.riak.client.api.RiakClient
import com.basho.riak.client.api.RiakCommand
import com.basho.riak.client.api.commands.datatypes.CounterUpdate
import com.basho.riak.client.api.commands.datatypes.FetchCounter
import com.basho.riak.client.api.commands.datatypes.FetchSet
import com.basho.riak.client.api.commands.datatypes.SetUpdate
import com.basho.riak.client.api.commands.datatypes.UpdateCounter
import com.basho.riak.client.api.commands.datatypes.UpdateSet
import com.basho.riak.client.api.commands.kv.DeleteValue
import com.basho.riak.client.api.commands.kv.FetchValue
import com.basho.riak.client.api.commands.kv.MultiFetch
import com.basho.riak.client.api.commands.kv.StoreValue
import com.basho.riak.client.core.query.Location
import com.basho.riak.client.core.query.Namespace
import com.basho.riak.client.core.query.RiakObject
import com.basho.riak.client.core.util.BinaryValue
import jakarta.annotation.PostConstruct
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import tools.jackson.databind.json.JsonMapper
import java.time.Duration
import java.util.concurrent.ExecutionException
import java.util.concurrent.TimeUnit

@Component
@RiakStorage
class RiakTemplate(
    private val client: RiakClient,
    private val json: JsonMapper,
    @Value("\${app.riak.timeout}") private val timeout: Duration,
) {
    @PostConstruct
    fun verifyBucketTypes() {
        requireBucketType(COUNTER_BUCKET_TYPE, "counter") { execute(FetchCounter.Builder(it).build()) }
        requireBucketType(SET_BUCKET_TYPE, "set") { execute(FetchSet.Builder(it).build()) }
    }

    final inline fun <reified T : Any> get(bucket: String, key: String): T? = get(bucket, key, T::class.java)

    fun <T : Any> get(bucket: String, key: String, type: Class<T>): T? {
        val response = execute(FetchValue.Builder(objectLocation(bucket, key)).build())
        return if (response.isNotFound) null else decode(response, type)
    }

    final inline fun <reified T : Any> getAll(bucket: String, keys: Collection<String>): List<T> =
        getAll(bucket, keys, T::class.java)

    fun <T : Any> getAll(bucket: String, keys: Collection<String>, type: Class<T>): List<T> {
        if (keys.isEmpty()) return emptyList()
        val command = MultiFetch.Builder().addLocations(keys.map { objectLocation(bucket, it) }).build()
        return execute(command).responses
            .map { it.get() }
            .filterNot { it.isNotFound }
            .map { decode(it, type) }
    }

    fun put(bucket: String, key: String, value: Any) {
        val riakObject = RiakObject()
            .setContentType(JSON_CONTENT_TYPE)
            .setValue(BinaryValue.create(json.writeValueAsBytes(value)))
        execute(StoreValue.Builder(riakObject).withLocation(objectLocation(bucket, key)).build())
    }

    fun delete(bucket: String, key: String) {
        execute(DeleteValue.Builder(objectLocation(bucket, key)).build())
    }

    fun incrementCounter(bucket: String, key: String, amount: Long = 1): Long {
        val location = Location(Namespace(COUNTER_BUCKET_TYPE, bucket), key)
        val command = UpdateCounter.Builder(location, CounterUpdate(amount)).withReturnDatatype(true).build()
        return execute(command).datatype.view()
    }

    fun addToSet(bucket: String, key: String, member: String) {
        val update = SetUpdate().apply { add(member) }
        execute(UpdateSet.Builder(Location(Namespace(SET_BUCKET_TYPE, bucket), key), update).build())
    }

    fun fetchSet(bucket: String, key: String): Set<String> {
        val response = execute(FetchSet.Builder(Location(Namespace(SET_BUCKET_TYPE, bucket), key)).build())
        return response.datatype.view().mapTo(mutableSetOf()) { it.toStringUtf8() }
    }

    private fun <T, S> execute(command: RiakCommand<T, S>): T =
        client.execute(command, timeout.toMillis(), TimeUnit.MILLISECONDS)

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
        const val SET_BUCKET_TYPE = "sets"
        const val BUCKET_TYPE_PROBE = "bucket_type_probe"
    }
}

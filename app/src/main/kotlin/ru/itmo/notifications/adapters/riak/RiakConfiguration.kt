package ru.itmo.notifications.adapters.riak

import com.basho.riak.client.api.RiakClient
import com.basho.riak.client.core.RiakNode
import com.basho.riak.client.core.util.HostAndPort
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.time.Duration

@Configuration
class RiakConfiguration {
    @Bean(destroyMethod = "shutdown")
    fun riakClient(
        @Value("\${app.riak.nodes}") nodes: String,
        @Value("\${app.riak.timeout}") timeout: Duration,
    ): RiakClient = RiakClient.newClient(
        HostAndPort.hostsFromString(nodes, RiakNode.Builder.DEFAULT_REMOTE_PORT),
        RiakClient.createDefaultNodeBuilder().withConnectionTimeout(timeout.toMillis().toInt()),
    )
}

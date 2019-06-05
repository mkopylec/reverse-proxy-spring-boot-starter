package com.github.mkopylec.charon.forwarding;

import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import reactor.netty.http.client.HttpClient;

import org.springframework.http.client.reactive.ClientHttpConnector;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;

import static com.github.mkopylec.charon.forwarding.Utils.toMillis;
import static io.netty.channel.ChannelOption.CONNECT_TIMEOUT_MILLIS;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static reactor.netty.http.client.HttpClient.create;

class ReactorConnectorCreator implements ClientHttpConnectorCreator {

    @Override
    public ClientHttpConnector createConnector(TimeoutConfiguration configuration) {
        HttpClient httpClient = create()
                .followRedirect(false)
                .tcpConfiguration(client ->
                        client.option(CONNECT_TIMEOUT_MILLIS, toMillis(configuration.getConnection()))
                                .doOnConnected(connection -> connection
                                        .addHandlerLast(new ReadTimeoutHandler(configuration.getRead().toMillis(), MILLISECONDS))
                                        .addHandlerLast(new WriteTimeoutHandler(configuration.getWrite().toMillis(), MILLISECONDS))));
        return new ReactorClientHttpConnector(httpClient);
    }
}
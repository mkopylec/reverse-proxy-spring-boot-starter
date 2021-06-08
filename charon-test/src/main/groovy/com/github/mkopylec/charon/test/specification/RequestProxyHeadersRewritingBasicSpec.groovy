package com.github.mkopylec.charon.test.specification

import spock.lang.Unroll

import static com.github.mkopylec.charon.test.assertions.Assertions.assertThat
import static com.github.mkopylec.charon.test.assertions.Assertions.assertThatServers
import static com.github.mkopylec.charon.test.stubs.OutgoingServersStubs.outgoingServers
import static org.springframework.http.HttpMethod.GET
import static org.springframework.http.HttpStatus.OK

abstract class RequestProxyHeadersRewritingBasicSpec extends BasicSpec {

    @Unroll
    def "Should rewrite request proxy headers from #originalHeaders to #rewritenHeaders by default"() {
        when:
        def response = http.sendRequest(GET, '/default', originalHeaders)

        then:
        assertThat(response)
                .hasStatus(OK)
        assertThatServers(localhost8080, localhost8081)
                .haveReceivedRequest(GET, '/default', rewritenHeaders)

        where:
        originalHeaders                                                           | rewritenHeaders
        ['Host': 'example.com:666', 'X-Forwarded-For': 'another-example.com:123'] | ['X-Forwarded-For': 'another-example.com:123, example.com:666']
        ['Host': 'example.com', 'X-Forwarded-For': 'another-example.com']         | ['X-Forwarded-For': 'another-example.com, example.com']
        ['Host': 'example.com']                                                   | ['X-Forwarded-For': 'example.com']
        ['Host': 'example.com']                                                   | ['X-Forwarded-Proto': 'http']
        ['Host': 'example.com']                                                   | ['X-Forwarded-Host': 'example.com']
        ['Host': 'example.com:666']                                               | ['X-Forwarded-Host': 'example.com:666']
        ['Host': 'example.com']                                                   | ['X-Forwarded-Port': '80']
        ['Host': 'example.com:666']                                               | ['X-Forwarded-Port': '666']
    }

    def "Should not rewrite request proxy headers when proper interceptor is unset"() {
        when:
        def response = http.sendRequest(GET, '/request/proxy/headers', ['Host': 'example.com', 'X-Forwarded-For': 'another-example.com'])

        then:
        assertThat(response)
                .hasStatus(OK)
        assertThatServers(localhost8080, localhost8081)
                .haveReceivedRequest(GET, '/request/proxy/headers', ['Host': 'example.com', 'X-Forwarded-For': 'another-example.com'])
    }

    void setup() {
        outgoingServers(localhost8080, localhost8081)
                .stubResponse(OK)
    }
}

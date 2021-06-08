package com.github.mkopylec.charon.forwarding.interceptors.rewrite;

import com.github.mkopylec.charon.configuration.Valid;
import com.github.mkopylec.charon.forwarding.interceptors.RequestForwardingInterceptorType;
import org.slf4j.Logger;
import org.springframework.http.HttpHeaders;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static com.github.mkopylec.charon.forwarding.Utils.copyHeaders;
import static com.github.mkopylec.charon.forwarding.interceptors.RequestForwardingInterceptorType.REQUEST_PROXY_HEADERS_REWRITER;
import static java.lang.String.valueOf;
import static org.apache.commons.collections4.CollectionUtils.emptyIfNull;

abstract class CommonRequestProxyHeadersRewriter implements Valid {

    private static final String X_FORWARDED_FOR = "X-Forwarded-For";
    private static final String X_FORWARDED_PROTO = "X-Forwarded-Proto";
    private static final String X_FORWARDED_HOST = "X-Forwarded-Host";
    private static final String X_FORWARDED_PORT = "X-Forwarded-Port";

    private Logger log;

    CommonRequestProxyHeadersRewriter(Logger log) {
        this.log = log;
    }

    public RequestForwardingInterceptorType getType() {
        return REQUEST_PROXY_HEADERS_REWRITER;
    }

    void rewriteHeaders(HttpHeaders headers, URI uri, Consumer<HttpHeaders> headersSetter) {
        HttpHeaders rewrittenHeaders = copyHeaders(headers);
        List<String> forwardedFor = new ArrayList<>(emptyIfNull(rewrittenHeaders.get(X_FORWARDED_FOR)));
        forwardedFor.add(uri.getAuthority());
        rewrittenHeaders.put(X_FORWARDED_FOR, forwardedFor);
        rewrittenHeaders.set(X_FORWARDED_PROTO, uri.getScheme());

        if (uri.getPort() == -1) {
            rewrittenHeaders.set(X_FORWARDED_HOST, uri.getHost());
        } else {
            rewrittenHeaders.set(X_FORWARDED_HOST, uri.getHost() + ":" + uri.getPort());
        }

        rewrittenHeaders.set(X_FORWARDED_PORT, resolvePort(uri));
        headersSetter.accept(rewrittenHeaders);
        log.debug("Request headers rewritten from {} to {}", headers, rewrittenHeaders);
    }

    void logStart(String mappingName) {
        log.trace("[Start] Request proxy headers rewriting for '{}' request mapping", mappingName);
    }

    void logEnd(String mappingName) {
        log.trace("[End] Request proxy headers rewriting for '{}' request mapping", mappingName);
    }

    private String resolvePort(URI uri) {
        int port = uri.getPort();
        if (port < 0) {
            port = uri.getScheme().equals("https") ? 443 : 80;
        }
        return valueOf(port);
    }
}

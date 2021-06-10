package com.github.mkopylec.charon.forwarding;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.github.mkopylec.charon.configuration.RequestMappingConfiguration;
import org.slf4j.Logger;

import static com.github.mkopylec.charon.forwarding.RequestForwardingException.requestForwardingErrorIf;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static org.slf4j.LoggerFactory.getLogger;

class RequestMappingResolver {

    private static final Logger log = getLogger(RequestMappingResolver.class);

    private List<RequestMappingConfiguration> requestMappingConfigurations;

    RequestMappingResolver(List<RequestMappingConfiguration> requestMappingConfigurations) {
        this.requestMappingConfigurations = requestMappingConfigurations;
    }

    RequestMappingConfiguration resolveRequestMapping(HttpServletRequest request) {
        final String serverName = request.getServerName();
        String requestURI = request.getRequestURI();
        List<RequestMappingConfiguration> configurations = requestMappingConfigurations.stream()
                .filter(configuration ->
                        configuration.getHostRegex().matcher(serverName).matches() &&
                        configuration.getPathRegex().matcher(requestURI).matches())
                .collect(toList());
        if (configurations.isEmpty()) {
            log.debug("No request mapping matches {} incoming request", requestURI);
            return null;
        }
        requestForwardingErrorIf(configurations.size() > 1, () -> "More than one request mapping matches "
                + requestURI + " incoming request. Matching mappings are "
                + configurations.stream().map(RequestMappingConfiguration::toString).collect(joining(", ")));
        RequestMappingConfiguration configuration = configurations.get(0);
        log.debug("Request mapping {} matches {} incoming request", configuration, requestURI);
        return configuration;
    }
}

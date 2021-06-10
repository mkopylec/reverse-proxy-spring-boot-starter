package com.github.mkopylec.charon.configuration;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import com.github.mkopylec.charon.forwarding.RestTemplateConfiguration;
import com.github.mkopylec.charon.forwarding.interceptors.RequestForwardingInterceptor;
import com.github.mkopylec.charon.forwarding.interceptors.RequestForwardingInterceptorType;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import static java.util.Collections.sort;
import static java.util.Collections.unmodifiableList;
import static java.util.regex.Pattern.compile;
import static java.util.stream.Collectors.toList;
import static org.springframework.util.Assert.hasText;

public class RequestMappingConfiguration implements Valid {

    private String name;
    private Pattern hostRegex;
    private Pattern pathRegex;
    private RestTemplateConfiguration restTemplateConfiguration;
    private List<RequestForwardingInterceptor> requestForwardingInterceptors;
    private List<RequestForwardingInterceptorType> unsetRequestForwardingInterceptors;
    private RequestMappingConfigurer requestMappingConfigurer;

    RequestMappingConfiguration(String name) {
        this.name = name;
        hostRegex = compile(".*");
        pathRegex = compile("/.*");
        requestForwardingInterceptors = new ArrayList<>();
        unsetRequestForwardingInterceptors = new ArrayList<>();
    }

    @Override
    public void validate() {
        hasText(name, "No request forwarding name set");
    }

    public String getName() {
        return name;
    }

    public Pattern getHostRegex() {
        return hostRegex;
    }

    void setHostRegex(String hostRegex) {
        this.hostRegex = compile(hostRegex);
    }

    public Pattern getPathRegex() {
        return pathRegex;
    }

    void setPathRegex(String pathRegex) {
        this.pathRegex = compile(pathRegex);
    }

    public RestTemplateConfiguration getRestTemplateConfiguration() {
        return restTemplateConfiguration;
    }

    void setRestTemplateConfiguration(RestTemplateConfiguration restTemplateConfiguration) {
        this.restTemplateConfiguration = restTemplateConfiguration;
    }

    void mergeRestTemplateConfiguration(RestTemplateConfiguration restTemplateConfiguration) {
        if (this.restTemplateConfiguration == null) {
            this.restTemplateConfiguration = restTemplateConfiguration;
        }
    }

    public List<RequestForwardingInterceptor> getRequestForwardingInterceptors() {
        return unmodifiableList(requestForwardingInterceptors);
    }

    void addRequestForwardingInterceptor(RequestForwardingInterceptor requestForwardingInterceptor) {
        removeRequestForwardingInterceptor(requestForwardingInterceptors, requestForwardingInterceptor.getType());
        unsetRequestForwardingInterceptors.remove(requestForwardingInterceptor.getType());
        requestForwardingInterceptors.add(requestForwardingInterceptor);
    }

    void removeRequestForwardingInterceptor(RequestForwardingInterceptorType requestForwardingInterceptorType) {
        removeRequestForwardingInterceptor(requestForwardingInterceptors, requestForwardingInterceptorType);
        unsetRequestForwardingInterceptors.add(requestForwardingInterceptorType);
    }

    void mergeRequestForwardingInterceptors(List<RequestForwardingInterceptor> requestForwardingInterceptors) {
        List<RequestForwardingInterceptor> additionalInterceptors = requestForwardingInterceptors.stream()
                .filter(interceptor -> this.requestForwardingInterceptors.stream().noneMatch(i -> i.getType().equals(interceptor.getType())))
                .filter(interceptor -> unsetRequestForwardingInterceptors.stream().noneMatch(i -> i.equals(interceptor.getType())))
                .collect(toList());
        this.requestForwardingInterceptors.addAll(additionalInterceptors);
        sort(this.requestForwardingInterceptors);
    }

    RequestMappingConfigurer getRequestMappingConfigurer() {
        return requestMappingConfigurer;
    }

    void setRequestMappingConfigurer(RequestMappingConfigurer requestMappingConfigurer) {
        this.requestMappingConfigurer = requestMappingConfigurer;
    }

    private void removeRequestForwardingInterceptor(List<RequestForwardingInterceptor> requestForwardingInterceptors, RequestForwardingInterceptorType requestForwardingInterceptorType) {
        requestForwardingInterceptors.removeIf(interceptor -> interceptor.getType().equals(requestForwardingInterceptorType));
    }

    @Override
    public String toString() {
        return "'" + name + "'";
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (obj.getClass() != getClass()) {
            return false;
        }
        RequestMappingConfiguration rhs = (RequestMappingConfiguration) obj;
        return new EqualsBuilder()
                .append(this.name, rhs.name)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(name)
                .toHashCode();
    }
}

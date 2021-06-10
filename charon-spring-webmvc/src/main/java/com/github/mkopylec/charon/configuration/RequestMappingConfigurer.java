package com.github.mkopylec.charon.configuration;

import com.github.mkopylec.charon.forwarding.RestTemplateConfigurer;
import com.github.mkopylec.charon.forwarding.interceptors.RequestForwardingInterceptorConfigurer;
import com.github.mkopylec.charon.forwarding.interceptors.RequestForwardingInterceptorType;

public class RequestMappingConfigurer extends Configurer<RequestMappingConfiguration> {

    private RequestMappingConfigurer(String name) {
        super(new RequestMappingConfiguration(name));
    }

    public static RequestMappingConfigurer requestMapping(String name) {
        return new RequestMappingConfigurer(name);
    }

    public RequestMappingConfigurer hostRegex(String hostRegex) {
        configuredObject.setHostRegex(hostRegex);
        return this;
    }

    public RequestMappingConfigurer pathRegex(String pathRegex) {
        configuredObject.setPathRegex(pathRegex);
        return this;
    }

    public RequestMappingConfigurer set(RestTemplateConfigurer restTemplateConfigurer) {
        configuredObject.setRestTemplateConfiguration(restTemplateConfigurer.configure());
        return this;
    }

    public RequestMappingConfigurer set(RequestForwardingInterceptorConfigurer<?> requestForwardingInterceptorConfigurer) {
        configuredObject.addRequestForwardingInterceptor(requestForwardingInterceptorConfigurer.configure());
        return this;
    }

    public RequestMappingConfigurer unset(RequestForwardingInterceptorType requestForwardingInterceptorType) {
        configuredObject.removeRequestForwardingInterceptor(requestForwardingInterceptorType);
        return this;
    }

    @Override
    protected RequestMappingConfiguration configure() {
        configuredObject.setRequestMappingConfigurer(this);
        return super.configure();
    }
}

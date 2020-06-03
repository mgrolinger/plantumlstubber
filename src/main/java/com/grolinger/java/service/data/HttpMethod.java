package com.grolinger.java.service.data;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public enum HttpMethod {
    GET,
    HEAD,
    PUT,
    POST,
    DELETE,
    TRACE,
    OPTIONS,
    CONNECT,
    PATCH;

    public static HttpMethod match(final String method) {
        HttpMethod result = null;
        try {
            result = valueOf(method);
        } catch (IllegalArgumentException ignore) {
            //ignore
            log.warn("No value found for {}", method);
        }
        return result;
    }
}


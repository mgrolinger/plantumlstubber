package com.grolinger.java.service.data;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

/**
 * HTTP methods to display methods in component diagrams
 */
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
        if (StringUtils.isEmpty(method))
            return result;
        try {
            result = valueOf(method);
        } catch (IllegalArgumentException ignore) {
            //ignore
            log.warn("No value found for {}", method);
        }
        return result;
    }
}


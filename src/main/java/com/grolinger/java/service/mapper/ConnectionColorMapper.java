package com.grolinger.java.service.mapper;

import java.util.HashMap;
import java.util.Map;

import static com.grolinger.java.service.mapper.DomainColorMapper.*;

public enum ConnectionColorMapper {
    //@formatter:off
    CUSTOMER_DOMAIN_COLOR_CONNECTION(       CUSTOMER_DOMAIN_COLOR.getValue()),
    EXTERNAL_DOMAIN_COLOR_CONNECTION(       EXTERNAL_DOMAIN_COLOR.getValue()),
    FINANCIAL_DOMAIN_COLOR_CONNECTION(      FINANCIAL_DOMAIN_COLOR.getValue()),
    INTEGRATION_DOMAIN_COLOR_CONNECTION(    INTEGRATION_DOMAIN_COLOR.getValue()),
    RESOURCE_DOMAIN_COLOR_CONNECTION(       RESOURCE_DOMAIN_COLOR.getValue());
    //@formatter:on

    private static final Map<String, ConnectionColorMapper> reverseLookup = new HashMap<>();
    private final String type;


    static {
        for (ConnectionColorMapper c : ConnectionColorMapper.values()) {
            reverseLookup.put(c.getValue(), c);
        }

    }

    ConnectionColorMapper(final String type) {
        this.type = type;
    }

    public static ConnectionColorMapper getByType(final String type) {
        return reverseLookup.getOrDefault(type.toLowerCase(), EXTERNAL_DOMAIN_COLOR_CONNECTION);
    }

    public String getValue() {
        return this.type;
    }


}

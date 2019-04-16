package com.grolinger.java.controller;

import java.util.HashMap;
import java.util.Map;

public enum ConnectionColorMapper {
    INTEGRATION_COLOR_CONNECTION("integration"),
    RESOURCE_DOMAIN_COLOR_CONNECTION("resource"),
    FINANCIAL_DOMAIN_COLOR_CONNECTION("financial"),
    CUSTOMER_DOMAIN_COLOR_CONNECTION("customer"),
    EXTERNAL_INTERFACE_COLOR_CONNECTION("external");

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
        return reverseLookup.getOrDefault(type.toLowerCase(), EXTERNAL_INTERFACE_COLOR_CONNECTION);
    }

    public String getValue() {
        return this.type;
    }


}

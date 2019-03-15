package com.grolinger.java.controller;

import java.util.HashMap;
import java.util.Map;

public enum ComponentColorMapper {
    INTEGRATION_COLOR("integration"),
    RESOURCE_DOMAIN_COLOR("resource"),
    FINANCIAL_DOMAIN_COLOR("financial"),
    CUSTOMER_DOMAIN_COLOR("customer"),
    EXTERNAL_INTERFACE_COLOR("external");

    private static Map<String, ComponentColorMapper> reverseLookup = new HashMap<>();
    private String type;

    static {
        for (ComponentColorMapper c : ComponentColorMapper.values()) {
            reverseLookup.put(c.getValue(), c);
        }

    }

    ComponentColorMapper(String type) {

        this.type = type;
    }

    public static ComponentColorMapper getByType(final String value) {
        return reverseLookup.getOrDefault(value, EXTERNAL_INTERFACE_COLOR);
    }

    public String getValue() {
        return this.type;
    }
}

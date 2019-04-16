package com.grolinger.java.controller;

import java.util.HashMap;
import java.util.Map;

public enum ComponentColorMapper {
    INTEGRATION_COLOR("integration", "<<integration>>"),
    RESOURCE_DOMAIN_COLOR("resource", "<<resource>>"),
    FINANCIAL_DOMAIN_COLOR("financial", "<<financial>>"),
    CUSTOMER_DOMAIN_COLOR("customer", "<<customer>>"),
    EXTERNAL_INTERFACE_COLOR("external", "<<external>>");

    private static Map<String, ComponentColorMapper> reverseLookup = new HashMap<>();
    private String type;
    private final String stereotype;

    static {
        for (ComponentColorMapper c : ComponentColorMapper.values()) {
            reverseLookup.put(c.getValue(), c);
        }

    }

    ComponentColorMapper(String type, final String stereotype) {
        this.type = type;
        this.stereotype = stereotype;
    }

    public static ComponentColorMapper getByType(final String type) {
        return reverseLookup.getOrDefault(type.toLowerCase(), EXTERNAL_INTERFACE_COLOR);
    }

    public String getValue() {
        return this.type;
    }


    public String getStereotype() {
        return this.stereotype;
    }
}

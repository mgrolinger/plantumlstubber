package com.grolinger.java.controller;

import java.util.HashMap;
import java.util.Map;

public enum DomainColorMapper {
    INTEGRATION_DOMAIN_COLOR("integration", "<<integration>>"),
    RESOURCE_DOMAIN_COLOR("resource", "<<resource>>"),
    FINANCIAL_DOMAIN_COLOR("financial", "<<financial>>"),
    CUSTOMER_DOMAIN_COLOR("customer", "<<customer>>"),
    EXTERNAL_DOMAIN_COLOR("external", "<<external>>");

    private static Map<String, DomainColorMapper> reverseLookup = new HashMap<>();
    private String type;
    private final String stereotype;

    static {
        for (DomainColorMapper c : DomainColorMapper.values()) {
            reverseLookup.put(c.getValue(), c);
        }

    }

    DomainColorMapper(String type, final String stereotype) {
        this.type = type;
        this.stereotype = stereotype;
    }

    public static DomainColorMapper getByType(final String type) {
        return reverseLookup.getOrDefault(type.toLowerCase(), EXTERNAL_DOMAIN_COLOR);
    }

    public String getValue() {
        return this.type;
    }


    public String getStereotype() {
        return this.stereotype;
    }
}

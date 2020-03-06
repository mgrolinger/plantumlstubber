package com.grolinger.java.service.data;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

/**
 * Container for application definitions.
 */
@Getter
@Builder
public class ApplicationDefinition {
    private String name;
    private String label;
    private String alias;
    private List<ServiceDefinition> serviceDefinitions;
}

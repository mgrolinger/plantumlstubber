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
    private String customAlias;
    private List<ServiceDefinition> serviceDefinitions;
}

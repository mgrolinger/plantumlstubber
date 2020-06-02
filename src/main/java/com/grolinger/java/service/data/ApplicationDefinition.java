package com.grolinger.java.service.data;

import com.grolinger.java.service.data.mapper.SystemType;
import lombok.Builder;
import lombok.Getter;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * Container for application definition
 */
@Getter
@Builder
public class ApplicationDefinition {
    private final String name;
    private final String label;
    private final SystemType systemType;
    private final String alias;
    @Builder.Default
    private final int orderPrio = 1;
    private final List<ServiceDefinition> serviceDefinitions;

    /**
     * Override systemType builder method.
     */
    public static class ApplicationDefinitionBuilder {
        public ApplicationDefinitionBuilder systemType(final String importedSystemType) {
            if (StringUtils.isEmpty(importedSystemType)) {
                this.systemType = SystemType.COMPONENT;
            } else {
                this.systemType = SystemType.getFrom(importedSystemType.toLowerCase());
            }
            return this;
        }
    }
}

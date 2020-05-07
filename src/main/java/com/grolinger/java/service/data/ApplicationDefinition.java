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
    private String name;
    private String label;
    private SystemType systemType;
    private String alias;
    @Builder.Default
    private int orderPrio=1;
    private List<ServiceDefinition> serviceDefinitions;

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

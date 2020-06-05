package com.grolinger.java.service.data;

import com.grolinger.java.controller.templatemodel.Constants;
import com.grolinger.java.service.data.mapper.SystemType;
import lombok.Builder;
import lombok.Getter;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Container for application definition
 */
@Getter
@Builder
public class ApplicationDefinition implements CommonRootPathHandler, PathHandler {
    private final List<String> nameParts;
    private final boolean endsWithSlash = false;
    private final boolean isInterface = false;
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
        public ApplicationDefinitionBuilder name(final String name) {
            this.name = name;
            if (name.contains(Constants.SLASH.getValue())) {
                this.nameParts = Arrays.asList(name.split(Constants.SLASH.getValue()));
            } else {
                this.nameParts = Collections.singletonList(name);
            }
            return this;
        }

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

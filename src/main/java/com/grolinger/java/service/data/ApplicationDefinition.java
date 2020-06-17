package com.grolinger.java.service.data;

import com.grolinger.java.controller.templatemodel.Constants;
import com.grolinger.java.service.NameConverter;
import com.grolinger.java.service.data.mapper.SystemType;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Container for application definition
 */
@Getter
@Builder
@Slf4j
public class ApplicationDefinition implements CommonRootPathHandler, PathHandler {
    private final List<String> nameParts;
    private final String name;
    private final String label;
    private final SystemType systemType;
    private final String alias;
    @Builder.Default
    private final int orderPrio = 1;
    private final List<ServiceDefinition> serviceDefinitions;

    @Override
    public boolean isInterface() {
        return false;
    }

    /**
     * Override systemType builder method.
     */
    public static class ApplicationDefinitionBuilder {
        public ApplicationDefinitionBuilder name(final String name) {
            if (name.contains(Constants.SLASH.getValue())) {
                // Clean the application name for the path variable
                this.nameParts = Arrays.stream(name.split(Constants.SLASH.getValue()))
                        .map(NameConverter::replaceUnwantedPlantUMLCharactersForPath)
                        .collect(Collectors.toList());
            } else {
                this.nameParts = Collections.singletonList(name);
            }
            // save the clean name
            this.name = NameConverter.replaceUnwantedPlantUMLCharacters(name, false);
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

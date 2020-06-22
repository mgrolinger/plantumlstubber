package com.grolinger.java.service.data;

import com.grolinger.java.service.NameConverter;
import lombok.Builder;
import lombok.Getter;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import static com.grolinger.java.controller.templatemodel.Constants.*;

/**
 * Container for a service definition.
 * Usually SOAP service endpoints are defined here but not the actual method,
 * which is defined in {@link InterfaceDefinition}.
 * If there are rest services, usually the common part goes here, e.g.
 * /api/v1/foo and
 * /api/v1/bar
 * have the "/api/common" in common, so this would go into the  {@link ServiceDefinition}, whereas
 * "foo" and "bar" will go into the  {@link InterfaceDefinition}
 */
@Builder
@Getter
public class ServiceDefinition implements CommonRootPathHandler, PathHandler {
    private final List<String> nameParts;
    // which domain is the application-service
    private final String domainColor;
    // path to the root directory of the diagrams, from which we can descent into the common/ directory
    // the label is displayed in the diagrams
    private final String serviceLabel;
    @Builder.Default
    private final List<InterfaceDefinition> interfaceDefinitions = new LinkedList<>();

    /**
     * The callName is used for the functions and should contain no special chars but _
     *
     * @return the service parts connected by _
     */
    public String getServiceCallName() {
        List<String> names = new LinkedList<>();
        for (String nP : this.nameParts) {
            names.add(NameConverter.replaceUnwantedPlantUMLCharacters(nP, false));
        }
        return String.join(NAME_SEPARATOR.getValue(), names);
    }

    @Override
    public boolean isInterface() {
        return false;
    }

    /**
     * Override Builder method to set servicePath, serviceLabel and serviceCallName
     */
    public static class ServiceDefinitionBuilder {
        public ServiceDefinition.ServiceDefinitionBuilder serviceName(final String serviceName) {
            if (StringUtils.isEmpty(serviceName) || EMPTY.getValue().equalsIgnoreCase(serviceName)) {
                this.nameParts = new ArrayList<>();
                this.serviceLabel = DEFAULT_ROOT_SERVICE_NAME.getValue();
            } else {
                /*this.nameParts = Arrays.stream(NameConverter
                        .replaceUnwantedPlantUMLCharactersForPath(serviceName)
                        .split(SLASH.getValue()))
                        .collect(Collectors.toList());

                 */
                this.nameParts = Arrays.stream(serviceName.split("[/|\\.]"))
                        .filter(s -> !StringUtils.isEmpty(s.trim()))
                        .map(NameConverter::replaceUnwantedPlantUMLCharactersForPath)
                        .collect(Collectors.toList());
                // Remove everything what's not necessary
                this.nameParts.removeIf(String::isEmpty);
                // Set if not yet set by builder method
                if (StringUtils.isEmpty(this.serviceLabel)) {
                    this.serviceLabel = serviceName;
                }
            }
            return this;
        }
    }
}

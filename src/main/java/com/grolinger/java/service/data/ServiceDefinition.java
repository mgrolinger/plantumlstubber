package com.grolinger.java.service.data;

import com.grolinger.java.controller.templatemodel.Constants;
import com.grolinger.java.service.NameConverter;
import lombok.Builder;
import lombok.Getter;
import org.springframework.util.StringUtils;

import java.util.LinkedList;
import java.util.List;

import static com.grolinger.java.controller.templatemodel.Constants.*;
import static com.grolinger.java.service.NameConverter.replaceUnwantedPlantUMLCharacters;

/**
 * Container for a service definition.
 * Usually SOAP service endpoints are defined here but not the actual method,
 * which is definied in {@link InterfaceDefinition}
 */
@Builder
@Getter
public class ServiceDefinition {
    private final String servicePath;
    private final String domainColor;
    private final String commonPath;
    private final String serviceLabel;
    private final String serviceCallName;
    @Builder.Default
    private final List<InterfaceDefinition> interfaceDefinitions = new LinkedList<>();

    /**
     * Override Builder method to set servicePath, serviceLabel and serviceCallName
     */
    public static class ServiceDefinitionBuilder {
        public ServiceDefinition.ServiceDefinitionBuilder serviceName(final String serviceName) {
            if (StringUtils.isEmpty(serviceName) || EMPTY.getValue().equalsIgnoreCase(serviceName)) {
                this.servicePath = "";
                this.serviceLabel = DEFAULT_ROOT_SERVICE_NAME.getValue();
                this.serviceCallName = DEFAULT_ROOT_SERVICE_NAME.getValue();
                commonPath = DIR_UP.getValue();
            } else {
                this.servicePath = replaceUnwantedPlantUMLCharacters(serviceName, true);
                if (!servicePath.endsWith(SLASH.getValue())) {
                    this.servicePath = this.servicePath + SLASH.getValue();
                }
                // Set if not yet set by builder method
                if (StringUtils.isEmpty(this.serviceLabel)) {
                    this.serviceLabel = serviceName;
                }
                this.serviceCallName = NameConverter.replaceUnwantedPlantUMLCharacters(serviceName, false);
                commonPath = getRelativeCommonPath(serviceName);
            }
            return this;
        }

        private String getRelativeCommonPath(final String serviceName) {
            StringBuilder cp = new StringBuilder();
            if (serviceName.contains(Constants.SLASH.getValue())) {
                for (int i = 0; i <= serviceName.split(Constants.SLASH.getValue()).length; i++) {
                    cp.append(DIR_UP.getValue());
                }
            } else {
                cp.append(DIR_UP.getValue())
                        .append(DIR_UP.getValue());
            }
            return cp.toString();
        }

    }


}

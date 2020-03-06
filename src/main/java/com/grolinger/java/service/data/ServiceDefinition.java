package com.grolinger.java.service.data;

import com.grolinger.java.controller.templatemodel.Constants;
import com.grolinger.java.service.NameService;
import com.grolinger.java.service.data.mapper.SystemType;
import lombok.Getter;
import org.springframework.util.StringUtils;

import java.util.LinkedList;
import java.util.List;

import static com.grolinger.java.controller.templatemodel.Constants.*;
import static com.grolinger.java.service.NameService.replaceUnwantedCharacters;

/**
 * Container for a service definition.
 * Usually SOAP service endpoints are defined here but not the actual method,
 * which is definied in {@link InterfaceDefinition}
 */
@Getter
public class ServiceDefinition {
    private SystemType systemType;
    private String servicePath;
    private int orderPrio;
    private String domainColor;
    private String commonPath;
    private String serviceCallName;

    private List<InterfaceDefinition> interfaceDefinitions = new LinkedList<>();

    public ServiceDefinition(final String serviceName, final String systemType, final String domainColor, final int orderPrio) {
        //this.applicationLabel = applicationName;
        //this.applicationName = StringUtils.capitalize(replaceUnwantedCharacters(applicationName, false));
        if (StringUtils.isEmpty(systemType)) {
            this.systemType = SystemType.COMPONENT;
        } else {
            this.systemType = SystemType.getFrom(systemType.toLowerCase());
        }
        this.orderPrio = orderPrio;
        this.domainColor = domainColor;

        if (StringUtils.isEmpty(serviceName) || EMPTY.getValue().equalsIgnoreCase(serviceName)) {
            this.servicePath = "";
            this.serviceCallName = DEFAULT_ROOT_SERVICE_NAME.getValue();
            commonPath = DIR_UP.getValue();
        } else {
            this.servicePath = replaceUnwantedCharacters(serviceName, true);
            if (!servicePath.endsWith(SLASH.getValue())) {
                this.servicePath = this.servicePath + SLASH.getValue();
            }
            this.serviceCallName = NameService.replaceUnwantedCharacters(serviceName, false);
            commonPath = getRelativeCommonPath(serviceName);
        }

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

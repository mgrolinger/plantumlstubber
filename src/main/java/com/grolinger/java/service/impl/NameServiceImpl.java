package com.grolinger.java.service.impl;

import com.grolinger.java.controller.Constants;
import com.grolinger.java.service.NameService;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import static com.grolinger.java.controller.Constants.*;

@Component
public class NameServiceImpl implements NameService {

    public String getReplaceUnwantedCharacters(final String name, final boolean replaceDotsOnly) {
        String newName = name.replace(DOT_SEPARATOR.getFirstChar(), NAME_SEPARATOR.getFirstChar());
        if (!replaceDotsOnly) {
            newName = newName.replace(PATH_SEPARATOR.getFirstChar(), NAME_SEPARATOR.getFirstChar());
        }
        return newName;
    }

    public String getServiceCallName(final String applicationName, final String serviceName) {
        if (null == serviceName || Constants.EMPTY.getValue().equalsIgnoreCase(serviceName)) {
            return applicationName;
        } else {
            return applicationName + NAME_SEPARATOR.getValue() + serviceName;
        }
    }

    public String formatServiceName(final String serviceName, final boolean isRest) {
        if (StringUtils.isEmpty(serviceName) || EMPTY.getValue().equalsIgnoreCase(serviceName)) {
            return "";
        } else {
            String formattedServiceName = isRest ? serviceName : StringUtils.capitalize(serviceName);

            if (serviceName.contains(Constants.SLASH.getValue())) {
                return getReplaceUnwantedCharacters(formattedServiceName, false);
            } else {
                return formattedServiceName;
            }
        }
    }

    public String getServicePathPrefix(final String serviceName) {
        return (Constants.EMPTY.getValue().equalsIgnoreCase(serviceName)) ? DEFAULT_ROOT_SERVICE_NAME.getValue() : serviceName + Constants.PATH_SEPARATOR.getValue();
    }


}

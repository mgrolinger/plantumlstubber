package com.grolinger.java.service.data;

import java.util.List;

import static com.grolinger.java.controller.templatemodel.Constants.DEFAULT_ROOT_SERVICE_NAME;
import static com.grolinger.java.controller.templatemodel.Constants.SLASH;

public interface PathHandler {
    default String getPath() {
        if (getNameParts().isEmpty()) {
            return DEFAULT_ROOT_SERVICE_NAME.getValue();
        }
        // Adds a / to the end of the path if we are not handling an interface
        // the last part of the interface is the file itself
        String suffix = (isInterface()) ? "" : SLASH.getValue();
        return String.join(SLASH.getValue(), getNameParts()) + suffix;
    }

    List<String> getNameParts();

    boolean isInterface();
}

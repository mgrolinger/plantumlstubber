package com.grolinger.java.service.data;

import java.util.List;

import static com.grolinger.java.controller.templatemodel.Constants.DEFAULT_ROOT_SERVICE_NAME;
import static com.grolinger.java.controller.templatemodel.Constants.SLASH;

/**
 * PathHandler that specifies methods {@link ApplicationDefinition}, {@link ServiceDefinition} and
 * {@link InterfaceDefinition} required for all kinds of path information.
 */
public interface PathHandler {
    /**
     * Default implementation of nameParts joined to a path.
     * This ends with a / if not an interface or with the last namePart if this is an interface.
     *
     * @return the nameParts joined to a path
     */
    default String getPath() {
        if (getNameParts().isEmpty()) {
            return DEFAULT_ROOT_SERVICE_NAME.getValue();
        }
        // Adds a / to the end of the path if we are not handling an interface
        // the last part of the interface is the file itself
        final String suffix = (isInterface()) ? "" : SLASH.getValue();
        return String.join(SLASH.getValue(), getNameParts()) + suffix;
    }

    /**
     * Returns the parts of the path as a list  of string.
     *
     * @return path as a list of strings
     */
    List<String> getNameParts();

    /**
     * @return {true} if this is an interface or {false} if this is not an interface
     */
    boolean isInterface();
}

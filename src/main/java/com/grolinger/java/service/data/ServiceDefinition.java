package com.grolinger.java.service.data;

import com.grolinger.java.controller.templatemodel.Constants;
import com.grolinger.java.service.NameConverter;
import lombok.Builder;
import lombok.Getter;
import org.springframework.util.StringUtils;

import java.util.LinkedList;
import java.util.List;

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
public class ServiceDefinition {
    // This variable is later used to export the generated files to the correct directory
    private final String servicePath;
    // which domain is the application-service
    private final String domainColor;
    // path to the root directory of the diagrams, from which we can descent into the common/ directory
    private final String commonPath;
    // the label is displayed in the diagrams
    private final String serviceLabel;
    // the callName is used for the functions and should contain no special chars but _
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
                // Default: one up just for the Application
                commonPath = DIR_UP.getValue();
            } else {
                this.servicePath = NameConverter.replaceUnwantedPlantUMLCharactersForPath(serviceName);
                if (!servicePath.endsWith(SLASH.getValue())) {
                    this.servicePath = this.servicePath + SLASH.getValue();
                }
                // save the path to the root directory of the diagrams, this needs the variable servicePath to end
                // with a slash in order to calculate the correct number of ../
                commonPath = getRelativeCommonPath(servicePath);

                // Set if not yet set by builder method
                if (StringUtils.isEmpty(this.serviceLabel)) {
                    this.serviceLabel = serviceName;
                }
                this.serviceCallName = NameConverter.replaceUnwantedPlantUMLCharacters(serviceName, false);
            }
            return this;
        }

        /**
         * "Calculates" the path to the root directory to be able to descent into the common directory.
         * All generated iuml files load the common.iuml file that contains a number of commonly used
         * !functions and !procedures and also loads participants or components depending on the typ of
         * diagram
         *
         * @param serviceName name of the current service
         * @return {@code ../../} default | a number of {@code ../} depending on how many slashes are in the service's name
         */
        private String getRelativeCommonPath(final String serviceName) {
            // Default has already on dir up (../) because of the application's directory itself
            StringBuilder cp = new StringBuilder(DIR_UP.getValue());

            for (String part : serviceName.split(Constants.SLASH.getValue())) {
                System.out.println("YYYY" + part);
                cp.append(DIR_UP.getValue());
            }
            return cp.toString();
        }
    }
}

package com.grolinger.java.controller;

import com.grolinger.java.config.Loggable;
import org.springframework.util.StringUtils;
import org.thymeleaf.context.Context;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static com.grolinger.java.controller.Constants.EMPTY;

class ContextSpec {
    private static final String APPLICATION_NAME = "applicationName";
    private static final String SERVICE_NAME = "serviceName";
    private static final String REST = "REST";
    private static final String IS_ROOT_SERVICE = "isRootService";
    private static final String IS_REST_SERVICE = "isRestService";
    private static final String APPLICATION_NAME_SHORT = "applicationNameShort";
    private static final String INTERFACE_NAME = "interfaceName";

    static OrderPrioBuilder builder() {
        return new ContextBuilderImpl(new Context());
    }

    interface OrderPrioBuilder {
        ColorBuilder withOrderPrio(final Integer orderNumber);
    }

    interface ColorBuilder {
        IntegrationTypeBuilder withColorName(final DomainColorMapper colorName);
    }

    interface IntegrationTypeBuilder {
        ApplicationNameBuilder withIntegrationType(final String integrationType);
    }

    interface ApplicationNameBuilder {
        ContextBuilder withApplicationName(final String applicationName);
    }

    interface ContextBuilder {

        ContextBuilder withServiceName(final String serviceName);

        ContextBuilder withInterfaceName(final String interfaceName);

        ContextBuilder withCommonPath(final String commonPath);

        Context getContext();
    }

    private static class ContextBuilderImpl implements ContextBuilder, OrderPrioBuilder, ColorBuilder, IntegrationTypeBuilder, ApplicationNameBuilder, Loggable {
        private Context context;
        private Map<String, String> aliasMapper = new HashMap<>();

        {
            aliasMapper.put("esb", "atlas");
        }

        ContextBuilderImpl(Context context) {
            this.context = context;
            context.setVariable("dateCreated", LocalDate.now());
            // set the default to true to prevent NullPointer, so called root services have only the applicationName and interfaceName set
            context.setVariable(IS_ROOT_SERVICE, true);
        }

        @Override
        public ColorBuilder withOrderPrio(final Integer orderWithinSequence) {
            context.setVariable("sequenceOrderPrio", orderWithinSequence);
            return this;
        }

        @Override
        public IntegrationTypeBuilder withColorName(final DomainColorMapper color) {
            context.setVariable("colorType", color.getStereotype());
            context.setVariable("colorName", color.getValue());
            context.setVariable("connectionColor", ConnectionColorMapper.getByType(color.getValue()));
            return this;
        }

        @Override
        public ApplicationNameBuilder withIntegrationType(final String integrationType) {
            String formattedIntegrationType = "";
            if (!StringUtils.isEmpty(integrationType)) {
                formattedIntegrationType = "INTEGRATION_TYPE(" + integrationType + ")";
            }
            context.setVariable("componentIntegrationType", formattedIntegrationType);
            if (!StringUtils.isEmpty(integrationType)) {
                context.setVariable(IS_REST_SERVICE, integrationType.toUpperCase().contains(REST));
            }
            return this;
        }

        @Override
        public ContextBuilder withApplicationName(final String applicationName) {
            context.setVariable(APPLICATION_NAME, applicationName);
            String applicationNameShort = aliasMapper.getOrDefault(applicationName.toLowerCase(), applicationName.toLowerCase());
            context.setVariable(APPLICATION_NAME_SHORT, applicationNameShort);
            return this;
        }

        @Override
        public ContextBuilder withServiceName(final String serviceName) {
            if (StringUtils.isEmpty(serviceName) || EMPTY.getValue().equalsIgnoreCase(serviceName)) {
                context.setVariable(IS_ROOT_SERVICE, true);
            } else {
                context.setVariable(SERVICE_NAME, serviceName);
                context.setVariable(IS_ROOT_SERVICE, false);
            }
            return this;
        }

        @Override
        public ContextBuilder withInterfaceName(final String interfaceName) {
            context.setVariable(INTERFACE_NAME, interfaceName);
            String applicationName = (String) context.getVariable(APPLICATION_NAME);
            String serviceName = (String) context.getVariable(SERVICE_NAME);
            boolean isRoot = (boolean) context.getVariable(IS_ROOT_SERVICE);
            context.setVariable("COMPLETE_INTERFACE_PATH", StringUtils.capitalize(applicationName) + (isRoot ? "" : capitalizePathParts(serviceName)) + StringUtils.capitalize(interfaceName) + "Int");
            context.setVariable("API_CREATED", applicationName.toUpperCase() + "_API" + (isRoot ? "" : "_" + serviceName.toUpperCase()) + "_" + interfaceName.toUpperCase() + "_CREATED");
            return this;
        }

        @Override
        public ContextBuilder withCommonPath(final String commonPath) {
            context.setVariable("commonPath", commonPath);
            return this;
        }


        public Context getContext() {
            return this.context;
        }
        //TODO add test
        private static String capitalizePathParts(final String pathToCapitalize) {
            return capitalizeStringParts(capitalizeStringParts(pathToCapitalize, Constants.SLASH.getValue()), Constants.NAME_SEPARATOR.getValue());
        }

        private static String capitalizeStringParts(final String pathToCapitalize, final String splitChar) {
            StringBuilder result = new StringBuilder();
            if (pathToCapitalize != null) {
                if (pathToCapitalize.contains(splitChar)) {
                    String[] parts = pathToCapitalize.split(splitChar);
                    for (String part : parts) {
                        result.append(StringUtils.capitalize(part.replace(splitChar, "")));
                    }
                } else {
                    result.append(StringUtils.capitalize(pathToCapitalize));
                }
            }
            return result.toString();
        }

    }
}

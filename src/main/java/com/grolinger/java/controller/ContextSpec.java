package com.grolinger.java.controller;

import com.grolinger.java.config.Loggable;
import com.grolinger.java.controller.templateModel.Constants;
import com.grolinger.java.service.DecisionService;
import com.grolinger.java.service.NameService;
import com.grolinger.java.service.mapper.ConnectionColorMapper;
import com.grolinger.java.service.mapper.DomainColorMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.thymeleaf.context.Context;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Map;

import static com.grolinger.java.controller.templateModel.Constants.EMPTY;
import static com.grolinger.java.controller.templateModel.ContextVariables.*;

final class ContextSpec {


    ColorBuilder builder() {
        return new ContextBuilderImpl(new Context());
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
        ContextBuilder withOrderPrio(final Integer orderNumber);

        ContextBuilder withPreformattedServiceName(final String serviceName);

        ContextBuilder withInterfaceName(final String interfaceName);

        ContextBuilder withCommonPath(final String commonPath);

        Context getContext();
    }

    private class ContextBuilderImpl implements ContextBuilder, ColorBuilder, IntegrationTypeBuilder, ApplicationNameBuilder, Loggable {
        private Context context;
       private Map<String, String> aliasMapper = Collections.singletonMap("esb", "atlas");

        ContextBuilderImpl(Context context) {
            this.context = context;
            context.setVariable(DATE_CREATED.getName(), LocalDate.now());
            // set the default to true to prevent NullPointer, so called root services have only the applicationName and interfaceName set
            context.setVariable(IS_ROOT_SERVICE.getName(), true);
            // default is the same directory
            context.setVariable(PATH_TO_COMMON_FILE.getName(), "");
        }

        @Override
        public IntegrationTypeBuilder withColorName(final DomainColorMapper color) {
            context.setVariable(COLOR_TYPE.getName(), color.getStereotype());
            context.setVariable(COLOR_NAME.getName(), color.getValue());
            context.setVariable(CONNECTION_COLOR.getName(), ConnectionColorMapper.getByType(color.getValue()));
            return this;
        }

        @Override
        public ApplicationNameBuilder withIntegrationType(final String integrationType) {
            String formattedIntegrationType = "";
            if (!StringUtils.isEmpty(integrationType)) {
                formattedIntegrationType = "INTEGRATION_TYPE(" + integrationType + ")";
            }
            context.setVariable(COMPONENT_INTEGRATION_TYPE.getName(), formattedIntegrationType);
            if (!StringUtils.isEmpty(integrationType)) {
                context.setVariable(IS_REST_SERVICE.getName(), integrationType.toUpperCase().contains(REST.getName()));
            }
            return this;
        }

        @Override
        public ContextBuilder withApplicationName(final String applicationName) {
            context.setVariable(APPLICATION_NAME.getName(), StringUtils.capitalize(applicationName));
            String applicationNameShort = aliasMapper.getOrDefault(applicationName.toLowerCase(), applicationName.toLowerCase());
            context.setVariable(APPLICATION_NAME_SHORT.getName(), applicationNameShort);
            return this;
        }

        @Override
        public ContextBuilder withPreformattedServiceName(final String serviceName) {
            if (StringUtils.isEmpty(serviceName) || EMPTY.getValue().equalsIgnoreCase(serviceName)) {
                context.setVariable(IS_ROOT_SERVICE.getName(), true);
            } else {
                context.setVariable(SERVICE_NAME.getName(), serviceName);
                context.setVariable(IS_ROOT_SERVICE.getName(), false);
            }
            return this;
        }

        @Override
        public ContextBuilder withInterfaceName(final String interfaceName) {
            context.setVariable(INTERFACE_NAME.getName(), interfaceName);
            String applicationName = (String) context.getVariable(APPLICATION_NAME.getName());
            String serviceName = (String) context.getVariable(SERVICE_NAME.getName());
            boolean isRoot = (boolean) context.getVariable(IS_ROOT_SERVICE.getName());
            context.setVariable(COMPLETE_INTERFACE_NAME.getName(), StringUtils.capitalize(applicationName) + (isRoot ? "" : capitalizePathParts(serviceName)) + StringUtils.capitalize(interfaceName.replaceAll(Constants.NAME_SEPARATOR.getValue(), "")) + "Int");
            context.setVariable(API_CREATED.getName(), applicationName.toUpperCase() + "_API" + (isRoot ? "" : "_" + serviceName.toUpperCase().replaceAll(Constants.SLASH.getValue(), Constants.NAME_SEPARATOR.getValue()) + "_" + interfaceName.toUpperCase() + "_CREATED"));
            return this;
        }

        @Override
        public ContextBuilder withOrderPrio(final Integer orderWithinSequence) {
            if (orderWithinSequence != null) {
                context.setVariable(SEQUENCE_PARTICIPANT_ORDER.getName(), orderWithinSequence);
            }
            return this;
        }

        @Override
        public ContextBuilder withCommonPath(final String commonPath) {
            context.setVariable(PATH_TO_COMMON_FILE.getName(), commonPath);
            return this;
        }


        public Context getContext() {
            return this.context;
        }

        //TODO add test
        private String capitalizePathParts(final String pathToCapitalize) {
            return capitalizeStringParts(capitalizeStringParts(pathToCapitalize, Constants.SLASH.getValue()), Constants.NAME_SEPARATOR.getValue());
        }

        private String capitalizeStringParts(final String pathToCapitalize, final String splitChar) {
            StringBuilder result = new StringBuilder();
            if (!StringUtils.isEmpty(pathToCapitalize)) {
                if (pathToCapitalize.contains(splitChar)) {
                    String[] parts = pathToCapitalize.split(splitChar);
                    for (String part : parts) {
                        result.append(StringUtils.capitalize(part.replaceAll(splitChar, "")));
                    }
                } else {
                    result.append(StringUtils.capitalize(pathToCapitalize));
                }
            }
            return result.toString();
        }

    }
}

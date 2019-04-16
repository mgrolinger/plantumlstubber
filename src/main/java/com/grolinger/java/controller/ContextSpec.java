package com.grolinger.java.controller;

import com.grolinger.java.config.Loggable;
import org.springframework.util.StringUtils;
import org.thymeleaf.context.Context;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

class ContextSpec {
    private static final String SERVICE_NAME = "serviceName";
    private static final String EMPTY = "EMPTY";
    private static final String REST = "REST";

    static OrderPrioBuilder Builder() {
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
        private static final String APPLICATION_NAME = "applicationName";
        private Context context;
        private Map<String, String> aliasMapper = new HashMap<>();

        {
            aliasMapper.put("esb", "atlas");
        }

        ContextBuilderImpl(Context context) {
            this.context = context;
            context.setVariable("dateCreated", LocalDate.now());
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
                context.setVariable("isRestService", integrationType.toUpperCase().contains(REST));
            }
            return this;
        }

        @Override
        public ContextBuilder withApplicationName(final String applicationName) {
            context.setVariable("applicationName", applicationName);
            String applicationNameShort = aliasMapper.getOrDefault(applicationName.toLowerCase(), applicationName.toLowerCase());
            context.setVariable("applicationNameShort", applicationNameShort);
            return this;
        }

        @Override
        public ContextBuilder withServiceName(final String serviceName) {
            context.setVariable(SERVICE_NAME, StringUtils.isEmpty(serviceName) ? EMPTY : serviceName);
            context.setVariable("isRootService", StringUtils.isEmpty(serviceName) || EMPTY.equalsIgnoreCase(serviceName));
            return this;
        }

        @Override
        public ContextBuilder withInterfaceName(final String interfaceName) {
            context.setVariable("interfaceName", interfaceName);
            String applicationName = (String) context.getVariable(APPLICATION_NAME);
            String serviceName = (String) context.getVariable(SERVICE_NAME);
//FIXME            context.setVariable("COMPLETE_INTERFACE_PATH", StringUtils.capitalize(applicationName) + (isRoot ? "" : capitalizePathParts(serviceName)) + StringUtils.capitalize(interfaceName) + "Int");
//FIXME            context.setVariable("API_CREATED", applicationName.toUpperCase() + "_API" + (isRoot ? "" : "_" + capitalizePathParts(serviceName).toUpperCase()) + "_" + interfaceName.toUpperCase() + "_CREATED");

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
    }
}

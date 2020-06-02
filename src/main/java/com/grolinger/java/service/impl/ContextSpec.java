package com.grolinger.java.service.impl;

import com.grolinger.java.controller.templatemodel.Constants;
import com.grolinger.java.service.NameConverter;
import com.grolinger.java.service.data.ApplicationDefinition;
import com.grolinger.java.service.data.InterfaceDefinition;
import com.grolinger.java.service.data.ServiceDefinition;
import com.grolinger.java.service.data.mapper.ColorMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.thymeleaf.context.Context;

import java.time.LocalDate;

import static com.grolinger.java.controller.templatemodel.Constants.EMPTY;
import static com.grolinger.java.controller.templatemodel.ContextVariables.*;
import static com.grolinger.java.service.NameConverter.replaceUnwantedPlantUMLCharacters;

@Slf4j
public final class ContextSpec {


    public ColorBuilder builder() {
        return new ContextBuilderImpl(new Context());
    }


    interface ColorBuilder {
        ApplicationNameBuilder withColorName(final String colorName);
    }

    interface ApplicationNameBuilder {
        ContextBuilder withApplication(final ApplicationDefinition applicationDefinition);
    }

    public interface ContextBuilder {
        ContextBuilder withOrderPrio(final int orderNumber);

        ContextBuilder withServiceDefinition(final ServiceDefinition serviceDefinition);

        ContextBuilder withInterfaceDefinition(final InterfaceDefinition interfaceDefinition);

        ContextBuilder withCommonPath(final String commonPath);

        ContextBuilder addToCommonPath(final String commonPath);

        Context getContext();
    }

    public class ContextBuilderImpl implements ContextBuilder, ColorBuilder, ApplicationNameBuilder {
        private final Context context;

        ContextBuilderImpl(Context context) {
            this.context = context;
            context.setVariable(DATE_CREATED, LocalDate.now());
            // set the default to true to prevent NullPointer, so called root services have only the applicationName and interfaceName set
            context.setVariable(IS_ROOT_SERVICE, true);
            // default is the same directory
            context.setVariable(PATH_TO_COMMON_FILE, "");
        }

        @Override
        public ApplicationNameBuilder withColorName(final String color) {
            String localColor;
            if (StringUtils.isEmpty(color)) {
                localColor = "undefined";
            } else {
                localColor = color;
            }
            context.setVariable(COLOR_TYPE, ColorMapper.getStereotype(localColor));
            context.setVariable(COLOR_NAME, ColorMapper.getDomainColor(localColor));
            context.setVariable(CONNECTION_COLOR, ColorMapper.getConnectionColor(localColor));
            return this;
        }

        private void setIntegrationType(final String integrationType) {
            String formattedIntegrationType = "";
            context.setVariable(IS_SOAP_SERVICE, false);
            context.setVariable(IS_REST_SERVICE, false);
            if (!StringUtils.isEmpty(integrationType)) {
                formattedIntegrationType = "$INTEGRATION_TYPE(" + integrationType + ")";
                context.setVariable(IS_SOAP_SERVICE, integrationType.toUpperCase().contains(SOAP.toUpperCase()));
                context.setVariable(IS_REST_SERVICE, integrationType.toUpperCase().contains(REST.toUpperCase()));
            }
            context.setVariable(COMPONENT_INTEGRATION_TYPE, formattedIntegrationType);
        }

        @Override
        public ContextBuilder withApplication(final ApplicationDefinition application) {
            // Set the name of the component/application
            String name;
            if (StringUtils.isEmpty(application.getName())) {
                name = "Undefined";
            } else {
                name = StringUtils.capitalize(application.getName());
            }
            context.setVariable(APPLICATION_NAME, name);

            // set an alias
            String alias = application.getAlias();
            // Set alias only if not yet defined, e.g. by the method withCustomAlias
            //Todo: awefull
            if (StringUtils.isEmpty(alias)) alias = NameConverter
                    .replaceUnwantedPlantUMLCharacters(name.toLowerCase(), false)
                    .replaceAll("_", "");

            context.setVariable(ALIAS, alias);

            // Set a custom label
            String label = application.getLabel();
            // Set a label to default if not defined
            if (StringUtils.isEmpty(label)) label = name;
            context.setVariable(APPLICATION_LABEL, label);
            return this;
        }

        @Override
        public ContextBuilder withInterfaceDefinition(InterfaceDefinition interfaceDefinition) {
            context.setVariable(INTERFACE_LABEL, interfaceDefinition.getName());
            this.withInterfaceName(interfaceDefinition.getName());

            if (interfaceDefinition.containsCallStack()) {
                context.setVariable(CALL_STACK, interfaceDefinition.getCallStack());
                context.setVariable(CALL_STACK_INCLUDES, interfaceDefinition.getCallStackForIncludes());
            } else {
                context.removeVariable(CALL_STACK);
                context.removeVariable(CALL_STACK_INCLUDES);
            }
            setIntegrationType(interfaceDefinition.getIntegrationType());
            // the functions are usually defined in common.iuml and look like this $restCall()
            context.setVariable(CALL_INTERFACE_BY, "$" + interfaceDefinition.getPumlFunctionType().toLowerCase() + "Call");
            context.setVariable(INTERFACE_RESPONSE_TYPE, interfaceDefinition.getResponseType());
            context.setVariable(IS_LINKED, interfaceDefinition.isLinked());
            context.setVariable(LINKED_TO_COMPONENT, interfaceDefinition.getLinkToComponent());
            context.setVariable(LINK_TO_CUSTOM_ALIAS, interfaceDefinition.getLinkToCustomAlias());
            if (!interfaceDefinition.getMethodDefinition().getMethods().isEmpty()) {
                context.setVariable(HTTP_METHODS, "$INDIVIDUAL_METHODS(" + interfaceDefinition.getMethodDefinition().getMethods() + ")");
            } else {
                context.setVariable(HTTP_METHODS, "");
            }

            log.info("Methods: {}", interfaceDefinition.getMethodDefinition().getMethods());
            return this;
        }

        private void setServiceName(final ServiceDefinition serviceDefinition) {
            String serviceName = serviceDefinition.getServiceCallName();
            if (StringUtils.isEmpty(serviceName) || EMPTY.getValue().equalsIgnoreCase(serviceName)) {
                context.setVariable(SERVICE_NAME, "");
                context.setVariable(SERVICE_LABEL, "");
                context.setVariable(IS_ROOT_SERVICE, true);
            } else {
                //ServiceName containing dot (.) seems to cause syntax error in generated iuml file
                log.info("Service Name:{}, Label:{}", serviceName, serviceDefinition.getServiceLabel());
                context.setVariable(SERVICE_NAME, serviceName);
                context.setVariable(SERVICE_LABEL, serviceDefinition.getServiceLabel());
                context.setVariable(IS_ROOT_SERVICE, false);
            }
        }

        private ContextBuilder withInterfaceName(final String interfaceName) {
            final String cleanedInterfaceName = replaceUnwantedPlantUMLCharacters(interfaceName, false);
            log.info("Interface Name b4:{},after:{}", interfaceName, cleanedInterfaceName);
            context.setVariable(INTERFACE_NAME, cleanedInterfaceName);
            String applicationName = (String) context.getVariable(APPLICATION_NAME);

            String serviceName = (String) context.getVariable(SERVICE_NAME);
            boolean isRoot = (boolean) context.getVariable(IS_ROOT_SERVICE);

            String completeInterfaceNAME = StringUtils.capitalize(applicationName) +
                    (isRoot ? "" : capitalizePathParts(serviceName)) + StringUtils.capitalize(cleanedInterfaceName) + "Int";
            context.setVariable(COMPLETE_INTERFACE_NAME, completeInterfaceNAME.replaceAll(Constants.NAME_SEPARATOR.getValue(), ""));
            context.setVariable(API_CREATED, applicationName.toUpperCase() +
                    "_API" + (isRoot ? "" : "_" + serviceName.toUpperCase().replaceAll(Constants.SLASH.getValue(), Constants.NAME_SEPARATOR.getValue())) +
                    "_" + cleanedInterfaceName.toUpperCase() +
                    "_CREATED");
            return this;
        }

        @Override
        public ContextBuilder withOrderPrio(final int orderWithinSequence) {
            context.setVariable(SEQUENCE_PARTICIPANT_ORDER, orderWithinSequence);
            return this;
        }

        @Override
        public ContextBuilder withServiceDefinition(ServiceDefinition serviceDefinition) {
            setServiceName(serviceDefinition);
            withCommonPath(serviceDefinition.getCommonPath());
            return this;
        }


        @Override
        public ContextBuilder withCommonPath(final String commonPath) {
            context.setVariable(PATH_TO_COMMON_FILE, commonPath);
            return this;
        }

        @Override
        public ContextBuilder addToCommonPath(final String commonPath) {
            String existingPath = (String) context.getVariable(PATH_TO_COMMON_FILE);
            if (!StringUtils.isEmpty(existingPath)) {
                existingPath = existingPath + commonPath;
            } else {
                existingPath = commonPath;
            }
            context.setVariable(PATH_TO_COMMON_FILE, existingPath);
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

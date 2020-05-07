package com.grolinger.java.service.data;

import com.grolinger.java.controller.templatemodel.Constants;
import lombok.Builder;
import lombok.Getter;
import org.springframework.util.StringUtils;

import java.util.*;

import static com.grolinger.java.controller.templatemodel.Constants.SLASH;
import static com.grolinger.java.service.NameConverter.replaceUnwantedPlantUMLCharacters;


/**
 * Container for interface definitions. Everything that is connected
 * to an interface and may change from one to another is put here.
 * Usually it is the last part of a REST interface such as
 * /api/rest/interface -> interface or
 * or the service method of a SOAP service.
 */
public class InterfaceDefinition {
    private static Map<String, String> DEFAULT_MAPPER_INTEGRATION = new HashMap<>();
    private static Map<String, String> DEFAULT_MAPPER_RESPONSE = new HashMap<>();

    static {
        // e.g. SOAP -> SOAP::XML
        DEFAULT_MAPPER_INTEGRATION.put("DB", "::JDBC");
        DEFAULT_MAPPER_INTEGRATION.put("REST", "::JSON");
        DEFAULT_MAPPER_INTEGRATION.put("SOAP", "::XML");
        // e.g. a SOAP request gets a XML response, a db call a result set
        DEFAULT_MAPPER_RESPONSE.put("DB", "Resultset");
        DEFAULT_MAPPER_RESPONSE.put("SOAP", "XML");
        DEFAULT_MAPPER_RESPONSE.put("REST", "JSON");
        DEFAULT_MAPPER_RESPONSE.put("HTTP", "HTML");
    }

    private String originalInterface;
    @Getter
    private String methodName;
    @Getter
    private String name;
    @Getter
    private String customAlias;
    @Getter
    private String interfacePath = "";
    @Getter
    private String formattedName;
    @Getter
    private String[] callStack;
    @Getter
    private String[] callStackForIncludes;
    @Getter
    private String relativeCommonPath = "";
    @Getter
    private String linkToComponent;
    @Getter
    private String linkToCustomAlias;
    @Getter
    private boolean isLinked;
    @Getter
    private String integrationType;
    @Getter
    private String pumlFunctionType;
    @Getter
    private String responseType;
    @Getter
    private MethodDefinition methodDefinition;

    /**
     * Hidden constructor is used by the Builder below.
     * @param originalInterfaceName The original interface name, such as doSomething() or a resource such as /person/id
     * @param customAlias the a
     * @param integrationType which kind of integration provides the interface, such as SOAP::XML or Rest::JSON
     * @param linkToComponent generates a link to this component, a visible line in the resulting diagram
     * @param linkToCustomAlias specifies the custom alias name of the linked component
     */
    @Builder()
    public InterfaceDefinition(final String originalInterfaceName,
                                final String customAlias,
                                final String integrationType,
                                final String linkToComponent,
                                final String linkToCustomAlias) {
        this.originalInterface = originalInterfaceName;
        this.name = extractInterfaceName(originalInterfaceName);
        this.methodName = getMethodName(name);
        this.formattedName = replaceUnwantedPlantUMLCharacters(name, false);
        this.methodDefinition = extractMethods(originalInterfaceName);
        StringBuilder relativeCommonPathBuilder = new StringBuilder();
        for (int i = 0; i < name.chars().filter(ch -> ch == SLASH.getFirstChar()).count(); i++) {
            // counts the parts of the interface so that we can generate the correct
            // number of ../ to traverse out of the current directory to reach the common.iuml
            relativeCommonPathBuilder.append(Constants.DIR_UP.getValue());
        }
        relativeCommonPath = relativeCommonPathBuilder.toString();

        this.customAlias = customAlias;
        this.integrationType = getIntegrationType(integrationType);
        this.pumlFunctionType = getFunctionType(integrationType);
        this.responseType = getResponseType(integrationType);
        // this might be empty
        this.isLinked = !StringUtils.isEmpty(linkToComponent);
        this.linkToComponent = linkToComponent;
        if (isLinked) {
            this.linkToCustomAlias = StringUtils.isEmpty(linkToCustomAlias) ?
                    linkToComponent.toLowerCase() :
                    linkToCustomAlias;
        }

    }

    private MethodDefinition extractMethods(final String originalInterfaceName) {
        List<String> currentMethods = new LinkedList<>();
        if (containsIndividualMethods(originalInterfaceName)) {
            // interface::POST:GET
            String[] singleMethod = originalInterfaceName
                    .split(Constants.INTERFACE_INTEGRATION_SEPARATOR.getValue())[1]
                    .split("->")[0]
                    .split(Constants.INTERFACE_METHOD_SEPARATOR.getValue());

            // ignore call stack information
            currentMethods.addAll(Arrays.asList(singleMethod));
        }
        return MethodDefinition.builder().methods(currentMethods).build();
    }

    public boolean containsPath() {
        return name.contains(SLASH.getValue());
    }

    public boolean hasRelativeCommonPath() {
        return relativeCommonPath.length() > 0;
    }

    public boolean containsCallStack() {
        return originalInterface.contains("->") &&
                null != callStack &&
                0 < callStack.length;
    }

    /**
     * This allows the definition of individual (Rest) Methods , such as
     * interface::POST:GET, in the yaml
     *
     * @param originalInterfaceName The interface name from yaml
     * @return true if the name contains something like interface::POST:GET or interface::DELETE
     */
    private boolean containsIndividualMethods(final String originalInterfaceName) {
        return originalInterfaceName.contains(Constants.INTERFACE_INTEGRATION_SEPARATOR.getValue()) &&
                2 == originalInterfaceName.split(Constants.INTERFACE_INTEGRATION_SEPARATOR.getValue()).length &&
                !(StringUtils.isEmpty(originalInterfaceName.split(Constants.INTERFACE_INTEGRATION_SEPARATOR.getValue())[1]));
    }

    /**
     * Uses the given interface name and strips of:
     * <li>the call stack</li>: specifying subsequent calls which this interface will do on its own
     * <li>the specific rest methods</li>: specifying the methods this interface will provide
     * An interfaceName might look like this: interface::POST:GET->SomeApplication_Service_Method()
     *
     * @param interfaceName The name of the interface, which may contain a call stack as well as some specific Rest methods
     * @return The stripped name
     */
    private String extractInterfaceName(final String interfaceName) {
        String calledInterfaceName;
        if (interfaceName.contains("->")) {
            String[] currentCallStack = interfaceName.split("->");
            calledInterfaceName = currentCallStack[0];
            if (interfaceName.contains("::")) {
                //Fixme
                calledInterfaceName = interfaceName.split("::")[0];
            }
            this.callStack = Arrays.copyOfRange(currentCallStack, 1, currentCallStack.length);
            int i = 0;
            callStackForIncludes = new String[currentCallStack.length - 1];
            for (String call : callStack) {
                callStackForIncludes[i] = call;
                callStack[i] = call.replaceAll("[/\\.]", "_");
                callStackForIncludes[i] = callStackForIncludes[i].replaceAll("[_\\.]", "/");
                i++;
            }
        } else {
            if (interfaceName.contains("::")) {
                //Fixme
                calledInterfaceName = interfaceName.split("::")[0];
            } else {
                calledInterfaceName = interfaceName;
            }
        }
        return calledInterfaceName;
    }

    private String getMethodName(final String currentInterfaceName) {
        if (currentInterfaceName.contains(SLASH.getValue())) {
            char[] currentInterfacePath = new char[currentInterfaceName.length()];
            currentInterfaceName.getChars(0, currentInterfaceName.lastIndexOf(SLASH.getValue()), currentInterfacePath, 0);
            char[] endpoint = new char[currentInterfaceName.length()];
            currentInterfaceName.getChars(currentInterfaceName.lastIndexOf(SLASH.getValue()) + 1, currentInterfaceName.length(), endpoint, 0);
            methodName = new String(endpoint).trim();
        } else {
            methodName = currentInterfaceName;
        }
        return methodName;
    }

    /**
     * Uses the definition of the integration type in the yaml. This method checks
     * whether the integration type contains already a {INTERFACE_TYPE}::{DATA_TYPE/KIND_OF_INTEGRATION}
     *
     * @param type something like SOAP or REST:JSON or DB::JDBC
     * @return returns the polished integration type, such as SOAP becomes SOAP::XML, REST::JSON remains REST::JSON
     */
    private String getIntegrationType(final String type) {
        String result;

        if (type.contains(Constants.INTERFACE_INTEGRATION_SEPARATOR.getValue())) {
            // e.g. REST::JSON remains REST::JSON
            result = type;
        } else {
            String dataType = "";
            // Search through know types, such as SOAP, REST
            if (DEFAULT_MAPPER_INTEGRATION.containsKey(type)) {
                // this is already combined with :: in the map
                dataType = DEFAULT_MAPPER_INTEGRATION.get(type);
            }
            // e.g. SOAP -> SOAP::XML
            result = type + dataType;
        }
        return result;
    }

    /**
     * This method tries to guess from the integration type the correct type of response
     * that is later used in sequence diagrams for the response part
     *
     * @param integrationType if the respinse type
     * @return the hopefully correct response
     */
    private String getResponseType(final String integrationType) {
        String result = "ToDo";
        if (integrationType == null) {
            return result;
        }
        String[] split = integrationType.split(Constants.INTERFACE_INTEGRATION_SEPARATOR.getValue());
        if (split.length > 1) {
            result = split[1];
        } else {
            String intType = split[0].trim().toUpperCase();
            if (DEFAULT_MAPPER_RESPONSE.containsKey(intType)) {
                result = DEFAULT_MAPPER_RESPONSE.get(intType);
            }
        }
        return result;
    }

    private String getFunctionType(final String responseType) {
        String result = "toDo";
        if (responseType == null) {
            return result;
        }
        String[] split = responseType.split(Constants.INTERFACE_INTEGRATION_SEPARATOR.getValue());
        if (split.length > 0) {
            result = split[0];
        }
        return result.toLowerCase();
    }

}

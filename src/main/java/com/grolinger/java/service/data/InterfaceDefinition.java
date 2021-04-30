package com.grolinger.java.service.data;

import com.grolinger.java.controller.templatemodel.Constants;
import com.grolinger.java.service.NameConverter;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import javax.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.grolinger.java.controller.templatemodel.Constants.SLASH;
import static com.grolinger.java.service.NameConverter.replaceUnwantedPlantUMLCharacters;


/**
 * Container for interface definitions. Everything that is connected
 * to an interface and may change from one to another is put here.
 * Usually it is the last part of a REST interface such as
 * /api/rest/interface -> interface or
 * or the service method of a SOAP service.
 */
@Slf4j
public class InterfaceDefinition implements CommonRootPathHandler, PathHandler {
    private static final Map<String, String> DEFAULT_MAPPER_INTEGRATION = new HashMap<>();
    private static final Map<String, String> DEFAULT_MAPPER_RESPONSE = new HashMap<>();
    public static final String TO_DO = "ToDo";

    static {
        // e.g. SOAP -> SOAP::XML
        DEFAULT_MAPPER_INTEGRATION.put("DB", "::JDBC");
        DEFAULT_MAPPER_INTEGRATION.put("REST", "::JSON");
        DEFAULT_MAPPER_INTEGRATION.put("SOAP", "::XML");
        // e.g. a SOAP request gets a XML response, a db call a result set
        DEFAULT_MAPPER_RESPONSE.put("DB", "ResultSet");
        DEFAULT_MAPPER_RESPONSE.put("SOAP", "XML");
        DEFAULT_MAPPER_RESPONSE.put("REST", "JSON");
        DEFAULT_MAPPER_RESPONSE.put("HTTP", "HTML");
    }

    @Getter
    private final boolean isInterface = true;
    @Getter
    private final List<String> nameParts;
    @Getter
    private final String name;
    @Getter
    private final String customAlias;
    @Getter
    private final String callName;
    @Getter
    private final String linkToComponent;
    @Getter
    private final boolean isLinked;
    @Getter
    private final String integrationType;
    @Getter
    private final String pumlFunctionType;
    @Getter
    private final String responseType;
    @Getter
    private final MethodDefinition methodDefinition;
    @Getter
    private final InterfaceCallStack callStack;
    @Getter
    private final String domainColor;
    @Getter
    private String methodName;
    @Getter
    private String linkToCustomAlias;


    /**
     * Hidden constructor is used by the Builder below.
     *
     * @param originalInterfaceName The original interface name, such as doSomething() or a resource such as /person/id
     * @param customAlias           a custom alias that is later used in plantUML to address e.g. a component
     * @param integrationType       which kind of integration provides the interface, such as SOAP::XML or Rest::JSON
     * @param linkToComponent       generates a link to this component, a visible line in the resulting diagram
     * @param linkToCustomAlias     specifies the custom alias name of the linked component
     */
    @Builder()
    public InterfaceDefinition(final String originalInterfaceName,
                               final String customAlias,
                               final String integrationType,
                               final String applicationDomainColor,
                               final String linkToComponent,
                               final String linkToCustomAlias) {

        // interfaceDefinition:  /api/int<<Domain>>::GET:POST->Call_stack extract: <<Domain>>
        this.domainColor = InterfaceDomain.extractDomainColor(originalInterfaceName, applicationDomainColor);
        // interfaceDefinition:  /api/int::GET:POST->Call_stack extract: ::GET:POST
        this.methodDefinition = new MethodDefinition(originalInterfaceName);
        // interfaceDefinition:  /api/int::GET:POST->Call_stack extract: ->Call_stack
        this.callStack = new InterfaceCallStack(originalInterfaceName);
        // save the name, which was cleaned from all other values, such as call stack, domain, methods
        this.name = callStack.getInterfaceName();

        this.methodName = getMethodName(this.name);

        // split names is required to know the path to subdirectories to the interface file, the for the name of  the procedure in plantuml etc.
        final String[] splitName = NameConverter.replaceUnwantedPlantUMLCharactersForPath(this.name).split(SLASH.getValue());
        this.nameParts = Arrays.stream(splitName)
                .filter(StringUtils::hasText)
                .collect(Collectors.toList());

        this.callName = extractFormattedName(this.name);

        this.customAlias = customAlias;

        // if specified, such as SOAP, Rest etc.first part in the yaml
        this.integrationType = getIntegrationType(integrationType);
        // if specified, such as XML, JSON etc. second part in the yaml
        this.pumlFunctionType = getFunctionType(integrationType);
        //
        this.responseType = getResponseType(integrationType);
        // this might be empty

        this.isLinked = StringUtils.hasText(linkToComponent);
        this.linkToComponent = NameConverter.replaceUnwantedPlantUMLCharacters(linkToComponent, false);
        if (isLinked) {
            this.linkToCustomAlias = !StringUtils.hasText(linkToCustomAlias) ?
                    linkToComponent.toLowerCase() :
                    linkToCustomAlias;
        }

    }

    /**
     * Makes the name usable for PlantUML !functions and !procedures,
     * which only supports a number of special chars.
     *
     * @param name the resulting name
     * @return the new name
     */
    private String extractFormattedName(final String name) {
        String fName = replaceUnwantedPlantUMLCharacters(name, false);

        if (fName.startsWith(Constants.NAME_SEPARATOR.getValue())) {
            fName = fName.substring(1);
        }
        if (fName.endsWith(Constants.NAME_SEPARATOR.getValue())) {
            fName = fName.substring(0, fName.length() - 1);
        }
        return fName;
    }

    /**
     * Checks if the  interface contains a path.
     *
     * @return {true} if the interface contains a path
     */
    public boolean containsPath() {
        return !nameParts.isEmpty();
    }

    /**
     * Checks if the original interface contains a call stack.
     *
     * @return {true} if the interface contains a call stack
     */
    public boolean containsCallStack() {
        return callStack.containsCallStack();
    }

    /**
     * Returns the name of the method
     *
     * @param currentInterfaceName the name of the current interface
     * @return the current method name, for rest its the last part of the interface
     */
    private String getMethodName(@NotNull final String currentInterfaceName) {
        if (currentInterfaceName.contains(SLASH.getValue())) {
            char[] currentInterfacePath = new char[currentInterfaceName.length()];
            currentInterfaceName.getChars(0, currentInterfaceName.lastIndexOf(SLASH.getValue()), currentInterfacePath, 0);
            char[] endpoint = new char[currentInterfaceName.length()];
            currentInterfaceName.getChars(currentInterfaceName.lastIndexOf(SLASH.getValue()) + 1, currentInterfaceName.length(), endpoint, 0);
            methodName = new String(endpoint).trim();
        } else {
            methodName = currentInterfaceName;
        }
        return NameConverter.replaceUnwantedPlantUMLCharacters(methodName, false);
    }

    /**
     * Uses the definition of the integration type in the yaml. This method checks
     * whether the integration type contains already a {INTERFACE_TYPE}::{DATA_TYPE/KIND_OF_INTEGRATION}
     *
     * @param integrationType something like SOAP or REST:JSON or DB::JDBC
     * @return returns the polished integration type, such as SOAP becomes SOAP::XML, REST::JSON remains REST::JSON
     */
    private String getIntegrationType(@NotNull final String integrationType) {
        String result;
        String type = integrationType.toUpperCase();
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
     * - e.g. REST::JSON -> JSON
     * REST -> JSON
     * SOAP::XML -> XML
     * FOO -> ToDo
     *
     * @param integrationType if the response type
     * @return the hopefully correct response, if nothing could be identified this method returns {ToDo}
     */
    private String getResponseType(final String integrationType) {
        String result = TO_DO;
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

    /**
     * Determines the functionType which is the first part of the identifier below the services in the yaml
     * - e.g REST::JSON -> REST
     * -     SOAP::XML -> SOAP
     *
     * @param responseType Type
     * @return functionType or toDo; default is toDO
     */
    private String getFunctionType(final String responseType) {
        String result = TO_DO;
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

package com.grolinger.java.service.data;

import com.grolinger.java.controller.templatemodel.Constants;
import lombok.Getter;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static com.grolinger.java.controller.templatemodel.Constants.SLASH;
import static com.grolinger.java.service.NameService.replaceUnwantedCharacters;


/**
 * Container for interface definitions. Everything that is connected
 * to an interface and may change from one to another is put here.
 * Usually it is the last part of a REST interface such as
 * /api/rest/interface -> interface or
 * or the service method of a SOAP service.
 */
public class InterfaceDefinition {
    private static Map<String, String> DEFAULT_MAPPER_RESPONSE = new HashMap<>();
    private static Map<String, String> DEFAULT_MAPPER_INTEGRATION = new HashMap<>();

    static {
        // e.g. a SOAP request gets a XML response, a db call a resultset
        DEFAULT_MAPPER_RESPONSE.put("DB", "Resultset");
        DEFAULT_MAPPER_RESPONSE.put("SOAP", "XML");
        DEFAULT_MAPPER_RESPONSE.put("REST", "JSON");
        // e.g. SOAP -> SOAP::XML
        DEFAULT_MAPPER_INTEGRATION.put("DB", "::JDBC");
        DEFAULT_MAPPER_INTEGRATION.put("REST", "::JSON");
        DEFAULT_MAPPER_INTEGRATION.put("SOAP", "::XML");
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

    public InterfaceDefinition(final String originalInterfaceName, final String customAlias, final String integrationType, final String linkToComponent, final String linkToCustomAlias) {
        this.originalInterface = originalInterfaceName;
        this.name = extractInterfaceName(originalInterfaceName);
        this.methodName = getMethodName(name);
        this.formattedName = replaceUnwantedCharacters(name, false);
        StringBuilder relativeCommonPathBuilder = new StringBuilder();
        for (int i = 0; i < name.chars().filter(ch -> ch == SLASH.getFirstChar()).count(); i++) {
            relativeCommonPathBuilder.append(Constants.DIR_UP.getValue());
        }
        relativeCommonPath = relativeCommonPathBuilder.toString();
        this.customAlias = customAlias;
        this.isLinked = !StringUtils.isEmpty(linkToComponent);
        this.linkToComponent = linkToComponent;
        if (isLinked) {
            this.linkToCustomAlias = StringUtils.isEmpty(linkToCustomAlias) ?
                    linkToComponent.toLowerCase() :
                    linkToCustomAlias;
        }
        this.integrationType = getIntegrationType(integrationType);
        this.pumlFunctionType = getFunctionType(integrationType);
        this.responseType = getResponseType(integrationType);
    }


    public boolean containsPath() {
        return name.contains(SLASH.getValue());
    }

    public boolean hasRelativeCommonPath() {
        return relativeCommonPath.length() > 0;
    }

    public boolean containsCallStack() {
        return originalInterface.contains("->") && callStack != null && callStack.length > 0;
    }

    private String extractInterfaceName(final String interfaceName) {
        String calledInterfaceName;
        if (interfaceName.contains("->")) {
            String[] currentCallStack = interfaceName.split("->");
            calledInterfaceName = currentCallStack[0];
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
            calledInterfaceName = interfaceName;
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

    private String getIntegrationType(final String type) {
        String result;

        if (type.contains(Constants.INTERFACE_INTEGRATION_SEPARATOR.getValue())) {
            // e.g. REST::JSON remains REST::JSON
            result = type;
        } else {
            String dataType = "";
            if (DEFAULT_MAPPER_INTEGRATION.containsKey(type)) {
                // this is already combined with :: in the map
                dataType = DEFAULT_MAPPER_INTEGRATION.get(type);
            }
            // e.g. SOAP -> SOAP::XML
            result = type + dataType;
        }
        return result;
    }

    private String getResponseType(final String responseType) {
        String result = "ToDo";
        if (responseType == null) {
            return result;
        }
        String[] split = responseType.split(Constants.INTERFACE_INTEGRATION_SEPARATOR.getValue());
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

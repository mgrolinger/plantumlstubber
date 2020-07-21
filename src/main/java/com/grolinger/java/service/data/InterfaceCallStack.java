package com.grolinger.java.service.data;

import com.grolinger.java.controller.templatemodel.Constants;
import lombok.Getter;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Contains CallStack information of an interface
 * defined by -> in the yaml
 */
public class InterfaceCallStack {
    @Getter
    private final String interfaceName;
    @Getter
    private final String[] callStackMethods;
    @Getter
    private final String[] callStackIncludes;
    private boolean containsCallStack = false;

    /**
     * Uses the given interface name and strips of:
     * <li>the call stack</li>: specifying subsequent calls which this interface will do on its own
     * <li>the specific rest methods</li>: specifying the methods this interface will provide
     * An interfaceName might look like this: /api/interface::POST:GET->SomeApplication_Service_Method()
     *
     * @param originalInterfaceName The name of the interface, which may contain a call stack as well as some specific Rest methods
     */
    public InterfaceCallStack(final String originalInterfaceName) {
        // extract call stack(s) defined by ->
        if (originalInterfaceName.contains(Constants.CALL_STACK_SEPARATOR.getValue())) {
            String[] currentCallStack = MethodDefinition.getInterfaceNameWithoutMethods(InterfaceDomain.removeDomainColorFromName(originalInterfaceName))
                    .split(Constants.CALL_STACK_SEPARATOR.getValue());

            this.callStackMethods = removeOtherSpecifications(Arrays.copyOfRange(currentCallStack, 1, currentCallStack.length));
            int i = 0;
            interfaceName = currentCallStack[0];
            callStackIncludes = new String[currentCallStack.length - 1];
            for (String call : callStackMethods) {
                callStackIncludes[i] = call;
                callStackMethods[i] = call.replaceAll("[/\\.]", "_");
                callStackIncludes[i] = callStackIncludes[i].replaceAll("[_\\.]", "/");
                i++;
            }
            containsCallStack = true;
        } else {
            interfaceName = MethodDefinition.getInterfaceNameWithoutMethods(InterfaceDomain.removeDomainColorFromName(originalInterfaceName));
            callStackMethods = null;
            callStackIncludes = null;
        }
    }

    /**
     * If the original interface definition contains a call stack defined by "->"
     *
     * @return {@code true} if the interface definition contains a callstack or not {@code false}
     */
    public boolean containsCallStack() {
        return containsCallStack;
    }

    private String[] removeOtherSpecifications(String[] callStackMethods) {
        int position = 0;
        for (String callStackMethod : callStackMethods) {
            callStackMethods[position] = callStackMethod.split(Constants.INTERFACE_INTEGRATION_SEPARATOR.getValue())[0];
            position++;
        }
        return callStackMethods;
    }

    /**
     * Removes the domainColorDefinition from a String
     * @param originalInterfaceName interface definition
     * @return originalInterfaceName without <<string>>, e.g. /api/interface<<color>> -> /api/interface
     */
    public String removeDomainColorFromName(String originalInterfaceName) {
        if (!StringUtils.isEmpty(originalInterfaceName)) {
            Pattern pattern = Pattern.compile("([<]{2})([a-z]+)([>]{2})", Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(originalInterfaceName);

            if (matcher.find()) {
                return originalInterfaceName.replaceAll(matcher.group(), "");
            }
        }
        return originalInterfaceName;
    }
}

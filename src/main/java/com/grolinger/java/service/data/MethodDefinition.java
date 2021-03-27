package com.grolinger.java.service.data;

import com.grolinger.java.controller.templatemodel.Constants;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


/**
 * Class that contains method definitions.
 */
@Slf4j
public class MethodDefinition {
    @Getter
    private List<HttpMethod> methods;

    /**
     * Extracts the HttpMethods from the originalInterfaceName that may look like this:
     * /api/v2/resource::GET->Call_stack
     *
     * @param originalInterfaceName the name of the interface defined in the yaml
     */
    public MethodDefinition(final String originalInterfaceName) {
        if (containsIndividualMethods(originalInterfaceName)) {
            Pattern pattern = Pattern.compile(CommonPattern.METHOD_PATTERN, Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(originalInterfaceName);
            if (matcher.find()) {
                String[] singleMethod = matcher.group(0)
                        .split(Constants.INTERFACE_INTEGRATION_SEPARATOR.getValue())[1]
                        .split(Constants.INTERFACE_METHOD_SEPARATOR.getValue());

                // ignore call stack information, just save the methods
                methods = Arrays.stream(singleMethod)
                        .map(HttpMethod::match)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList());
            }

        } else {
            methods = new LinkedList<>();
        }
    }

    /**
     * Gets the interfaceName without method definitions, such as (GET, POST,...)
     *
     * @param name original name
     * @return interface name
     */
    public static String getInterfaceNameWithoutMethods(final String name) {
        if (containsIndividualMethods(name)) {
            Pattern pattern = Pattern.compile(CommonPattern.METHOD_PATTERN, Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(name);
            if (matcher.find()) {
                return name.replaceAll(matcher.group(), "");
            }
        }
        return name;
    }


    /**
     * This allows the definition of individual (Rest) Methods , such as
     * interface::POST:GET, in the yaml, but not interface:POST
     *
     * @param originalInterfaceName The interface name from yaml
     * @return true if the name contains something like interface::POST:GET or interface::DELETE
     */
    private static boolean containsIndividualMethods(final String originalInterfaceName) {
        if (!StringUtils.hasText(originalInterfaceName))
            return false;
        return originalInterfaceName.contains(Constants.INTERFACE_INTEGRATION_SEPARATOR.getValue()) &&
                2 == originalInterfaceName.split(Constants.INTERFACE_INTEGRATION_SEPARATOR.getValue()).length &&
                (StringUtils.hasText(originalInterfaceName.split(Constants.INTERFACE_INTEGRATION_SEPARATOR.getValue())[1]));
    }
}

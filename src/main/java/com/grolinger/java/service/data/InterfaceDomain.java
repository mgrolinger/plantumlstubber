package com.grolinger.java.service.data;

import lombok.experimental.UtilityClass;
import org.springframework.util.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class that helps to extract the domain information of an interface
 */
@UtilityClass
public class InterfaceDomain {

    /**
     * Extracts the domain from the yaml definition, e.g.
     * /api/interface<<domain>>
     *
     * @param originalInterfaceName  the interface name from the yaml
     * @param applicationDomainColor default if no other "domain" is defined
     * @return the domain name of the interface, {default}
     */
    public String extractDomainColor(String originalInterfaceName, String applicationDomainColor) {
        if (StringUtils.hasText(originalInterfaceName)) {
            Pattern pattern = Pattern.compile(CommonPattern.DOMAIN_DEFINITION_PATTERN, Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(originalInterfaceName);

            if (matcher.find()) {
                return matcher.group(2).trim().toLowerCase();
            }
        }
        return StringUtils.hasText(applicationDomainColor) ? applicationDomainColor.trim().toLowerCase() : "default";
    }

    /**
     * Removes the domainColorDefinition from a String
     *
     * @param originalInterfaceName interface definition
     * @return originalInterfaceName without <<string>>, e.g. /api/interface<<color>> -> /api/interface
     */
    public String removeDomainColorFromName(String originalInterfaceName) {
        if (StringUtils.hasText(originalInterfaceName)) {
            Pattern pattern = Pattern.compile(CommonPattern.DOMAIN_DEFINITION_PATTERN, Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(originalInterfaceName);

            if (matcher.find()) {
                return originalInterfaceName.replaceAll(matcher.group(), "");
            }
        }
        return originalInterfaceName;
    }

}

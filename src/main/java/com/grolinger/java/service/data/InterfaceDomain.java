package com.grolinger.java.service.data;

import lombok.experimental.UtilityClass;
import org.springframework.util.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@UtilityClass
public class InterfaceDomain {

    /**
     * Extracts the domain from the yaml definition, e.g.
     * /api/interface<<domain>>
     *
     * @param originalInterfaceName  the interface name from the yaml
     * @param applicationDomainColor default if no other "domain" is defined
     * @return the domain name of the interface
     */
    public String extractDomainColor(String originalInterfaceName, String applicationDomainColor) {
        if (!StringUtils.isEmpty(originalInterfaceName)) {
            Pattern pattern = Pattern.compile("([<]{2})([a-z]+)([>]{2})", Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(originalInterfaceName);

            if (matcher.find()) {
                return matcher.group(2).trim().toLowerCase();
            }
        }
        return StringUtils.isEmpty(applicationDomainColor) ? "default" : applicationDomainColor.trim().toLowerCase();
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

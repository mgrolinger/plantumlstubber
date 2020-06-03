package com.grolinger.java.service;

import com.grolinger.java.controller.templatemodel.Constants;
import org.springframework.util.StringUtils;

import static com.grolinger.java.controller.templatemodel.Constants.NAME_SEPARATOR;
import static com.grolinger.java.controller.templatemodel.Constants.SLASH;

public class NameConverter {

    private static final String PARENTHESIS = "([{}])";
    private static final String REGEX_DOT_MINUS = "(\\.|\\n|\\s|-)";
    private static final String SLASH_BACKSLASH = "([\\\\/])";

    public static String replaceUnwantedPlantUMLCharacters(final String name, final boolean replaceDotsOnly) {
        String newName = "";
        if (!StringUtils.isEmpty(name)) {
            newName = replaceStringsByNameSeparator(name, NAME_SEPARATOR);
            newName = removeParenthesisFromName(newName);
            if (!replaceDotsOnly) {
                newName = newName.replaceAll(SLASH_BACKSLASH, NAME_SEPARATOR.getValue());
            }
        }
        return newName;
    }

    public static String replaceUnwantedPlantUMLCharactersForPath(final String name) {
        String newName = name;
        if (!StringUtils.isEmpty(newName)) {
            newName = removeParenthesisFromName(newName);
            newName = replaceStringsByNameSeparator(newName, SLASH);

        }
        return newName;
    }

    /**
     * Removes specific characters in a name by NAME_SEPARATOR "_"
     *
     * @param name the original name, such as name1/name2
     * @return the name with replaced chars, such as name1_name2
     */
    private static String removeParenthesisFromName(final String name) {
        return name.replaceAll(PARENTHESIS, "");
    }

    /**
     * Replaces specific characters in a name by NAME_SEPARATOR "_"
     *
     * @param name the original name, such as name1/name2
     * @return the name with replaced chars, such as name1_name2
     */
    private static String replaceStringsByNameSeparator(final String name, final Constants constants) {
        return name.replaceAll(REGEX_DOT_MINUS, constants.getValue());
    }
}
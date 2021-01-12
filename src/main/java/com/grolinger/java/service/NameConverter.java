package com.grolinger.java.service;

import com.grolinger.java.controller.templatemodel.Constants;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

import static com.grolinger.java.controller.templatemodel.Constants.NAME_SEPARATOR;
import static com.grolinger.java.controller.templatemodel.Constants.SLASH;

@NoArgsConstructor
public class NameConverter {

    private static final String PARENTHESIS = "[{}]";
    private static final String REGEX_DOT_MINUS = "[\\.|\\n|\\s|\\-|\\\\]";
    private static final String SLASH_BACKSLASH = "([\\\\|/])";
    private static final String DUPLICATES = "\\{2,}|_{2,}|/{2,}";

    /**
     * Removes characters from the given name that may cause problems in plantuml if used in !function definitions
     *
     * @param name            Name that should be cleaned
     * @param replaceDotsOnly
     * @return the new name where all kind of special characters are replaced by _
     */
    public static String replaceUnwantedPlantUMLCharacters(final String name, final boolean replaceDotsOnly) {
        String newName = "";
        if (StringUtils.hasText(name)) {
            newName = removeParenthesisFromName(name);
            newName = replaceStringsByNameSeparator(newName, NAME_SEPARATOR);
            if (!replaceDotsOnly) {
                newName = newName.replaceAll(SLASH_BACKSLASH, NAME_SEPARATOR.getValue());
            }
        }
        // replace all duplicate name separators by single
        return newName.replaceAll(DUPLICATES, NAME_SEPARATOR.getValue());
    }

    /**
     * Removes characters from the given name that may cause problems in plantuml
     * if used in the directory or file name
     *
     * @param name name to be processed
     * @return the new name where all kind of special characters are replaced by /
     */
    public static String replaceUnwantedPlantUMLCharactersForPath(final String name) {
        String newName = "";
        if (StringUtils.hasText(name)) {
            newName = removeParenthesisFromName(name);
            newName = replaceStringsByNameSeparator(newName, SLASH);
        }
        return newName.replaceAll(DUPLICATES, SLASH.getValue());
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
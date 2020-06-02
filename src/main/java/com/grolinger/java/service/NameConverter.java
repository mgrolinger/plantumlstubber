package com.grolinger.java.service;

import org.springframework.util.StringUtils;

import static com.grolinger.java.controller.templatemodel.Constants.NAME_SEPARATOR;

public class NameConverter {

    public static String replaceUnwantedPlantUMLCharacters(final String name, final boolean replaceDotsOnly) {
        String newName = "";
        if (!StringUtils.isEmpty(name)) {
            newName = replaceStringsByNameSeparator(name);
            newName = removeCharsFromName(newName);
            if (!replaceDotsOnly) {
                String stringsToReplaceAdditionally = "([\\\\/])";
                newName = newName.replaceAll(stringsToReplaceAdditionally, NAME_SEPARATOR.getValue());
            }
        }
        return newName;
    }

    /**
     * Removes specific characters in a name by NAME_SEPARATOR "_"
     *
     * @param name the original name, such as name1/name2
     * @return the name with replaced chars, such as name1_name2
     */
    private static String removeCharsFromName(final String name) {
        String stringsToRemove = "([{}])";
        return name.replaceAll(stringsToRemove, "");
    }

    /**
     * Replaces specific characters in a name by NAME_SEPARATOR "_"
     *
     * @param name the original name, such as name1/name2
     * @return the name with replaced chars, such as name1_name2
     */
    private static String replaceStringsByNameSeparator(final String name) {
        String stringsToReplace = "(\\.|\\n|\\s|-)";
        return name.replaceAll(stringsToReplace, NAME_SEPARATOR.getValue());
    }
}
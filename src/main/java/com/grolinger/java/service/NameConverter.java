package com.grolinger.java.service;

import org.springframework.util.StringUtils;

import static com.grolinger.java.controller.templatemodel.Constants.NAME_SEPARATOR;

public class NameConverter {

    public static String replaceUnwantedPlantUMLCharacters(final String name, final boolean replaceDotsOnly) {
        String newName = "";
        if (!StringUtils.isEmpty(name)) {
            String stringsToReplace = "(\\.|\\n|\\s|-)";
            newName = name.replaceAll(stringsToReplace, NAME_SEPARATOR.getValue());
            if (!replaceDotsOnly) {
                String stringsToReplaceAdditionally = "([\\\\/])";
                newName = newName.replaceAll(stringsToReplaceAdditionally, NAME_SEPARATOR.getValue());
            }
        }
        return newName;
    }
}
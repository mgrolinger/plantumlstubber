package com.grolinger.java.service;

import org.springframework.util.StringUtils;

import static com.grolinger.java.controller.templatemodel.Constants.*;
import static com.grolinger.java.controller.templatemodel.Constants.NAME_SEPARATOR;

public class NameService {

    public static String replaceUnwantedCharacters(final String name, final boolean replaceDotsOnly) {
        String newName = "";
        if (!StringUtils.isEmpty(name)) {
            newName = name.replace(DOT_SEPARATOR.getFirstChar(), NAME_SEPARATOR.getFirstChar());
            newName = newName.replace(HYPHEN.getFirstChar(), NAME_SEPARATOR.getFirstChar());
            if (!replaceDotsOnly) {
                newName = newName.replace(PATH_SEPARATOR.getFirstChar(), NAME_SEPARATOR.getFirstChar());
                newName = newName.replace(SLASH.getFirstChar(), NAME_SEPARATOR.getFirstChar());
            }
        }
        return newName;
    }
}
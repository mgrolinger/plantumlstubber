package com.grolinger.java.service;

import com.grolinger.java.controller.templatemodel.TemplateContent;
import org.springframework.util.StringUtils;

import static com.grolinger.java.controller.templatemodel.Constants.*;
import static com.grolinger.java.controller.templatemodel.Constants.NAME_SEPARATOR;

public class NameService {

    public static String replaceUnwantedCharacters(final String name, final boolean replaceDotsOnly) {
        String newName = "";
        if (!StringUtils.isEmpty(name)) {
            String stringsToReplace= "(\\.|\\n|\\s|-)";
            newName = name.replaceAll(stringsToReplace, NAME_SEPARATOR.getValue());
            if (!replaceDotsOnly) {
                String stringsToReplaceAdditionally = "([\\\\/])";
                newName = newName.replaceAll(stringsToReplaceAdditionally, NAME_SEPARATOR.getValue());
            }
        }
        return newName;
    }
}
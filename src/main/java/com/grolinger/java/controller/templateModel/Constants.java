package com.grolinger.java.controller.templatemodel;

import java.io.File;

/**
 * Bunch of Constants that are used while processing *Definitions and creating the plantuml services.
 */
public enum Constants {
    EMPTY("EMPTY"),
    SLASH("/"),
    HYPHEN("-"),
    NAME_SEPARATOR("_"),
    PATH_SEPARATOR(File.separator),
    DOT_SEPARATOR("."),
    INTERFACE_INTEGRATION_SEPARATOR("::"),
    INTERFACE_METHOD_SEPARATOR(":"),
    DIR_UP("../"),
    DEFAULT_ROOT_SERVICE_NAME(""),
    DEFINE_FUNCTION_PREFIX("!$"),
    FUNCTION_V2_PREFIX("$"),
    COMPONENT_SUFFIX("_COMPONENT"),
    DATABASE_SUFFIX("_DATABASE"),
    PARTICIPANT_SUFFIX("_PARTICIPANT");

    private final String value;

    Constants(final String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }

    public char getFirstChar() {
        return this.value.length() > 0 ? this.value.charAt(0) : Character.MIN_VALUE;
    }
}

package com.grolinger.java.controller;

public enum Constants {
    EMPTY("EMPTY"),
    SLASH("/"),
    NAME_SEPARATOR("_"),
    PATH_SEPARATOR("/"),
    DOT_SEPARATOR("."),
    DIR_UP("../"),
    DEFAULT_ROOT_SERVICE_NAME(""),
    FILE_TYPE_IUML(".iuml");

    private String value;

    Constants(final String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }

    public char getFirstChar() {
        return this.value.charAt(0);
    }
}

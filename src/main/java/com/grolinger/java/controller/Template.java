package com.grolinger.java.controller;

public enum Template {
    COMPONENT("componentExport.html"),
    SEQUENCE("sequenceExport.html");

    private String type;

    Template(final String type) {
        this.type = type;
    }

    public String getTemplateURL() {
        return this.type;
    }
}

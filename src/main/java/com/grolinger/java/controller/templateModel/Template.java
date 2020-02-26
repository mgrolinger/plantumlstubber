package com.grolinger.java.controller.templatemodel;

public enum Template {
    COMPONENT_V2("componentExportV2.html"),
    SEQUENCE_V2("sequenceExportV2.html");

    private String type;

    Template(final String type) {
        this.type = type;
    }

    public String getTemplateURL() {
        return this.type;
    }
}

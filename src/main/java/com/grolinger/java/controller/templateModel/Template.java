package com.grolinger.java.controller.templatemodel;

public enum Template {
    //1.2019.6 new preprocessor
    COMPONENT_V1_2019_6("componentExport_V1_2019_6.html"),
    SEQUENCE_V1_2019_6("sequenceExport_V1_2019_6.html"),
    //1.2020.7 new procedures in preprocessor
    COMPONENT_V1_2020_7("componentExport_V1_2020_7.html"),
    SEQUENCE_V1_2020_7("sequenceExport_V1_2020_7.html");

    private String type;

    Template(final String type) {
        this.type = type;
    }

    public String getTemplateURL() {
        return this.type;
    }
}

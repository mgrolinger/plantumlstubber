package com.grolinger.java.controller.templatemodel;

public enum TemplateContent {
    START("@startuml\n"),
    END("@enduml"),
    DATE("' generated on "),
    CARRIAGE_RETURN("\n"),
    //Todo replace by static import from common.iuml
    COMMONV2_FILE("'This file is intentionally blank or consists of elements that are necessary to display generated files\n" +
            "!if (%not(%variable_exists('$DETAILED')))\n" +
            "!$DETAILED = %false()\n" +
            "!endif\n" +
            "!$SIMPLE=%false()\n" +
            "!$SHOW_EXCEPTION=%false()\n\n" +
            "!$INTEGRATION_DOMAIN_COLOR_CONNECTION='red'\n" +
            "!$RESOURCE_DOMAIN_COLOR_CONNECTION='red'\n" +
            "!$CUSTOMER_DOMAIN_COLOR_CONNECTION='red'\n" +
            "!$FINANCIAL_DOMAIN_COLOR_CONNECTION='red'\n" +
            "!$EXTERNAL_DOMAIN_COLOR_CONNECTION='red'\n" +
            "!$AUTHENTIFIZIERUNG_DOMAIN_COLOR_CONNECTION='red'\n" +
            "!$SUCHE_DOMAIN_COLOR_CONNECTION='red'\n" +
            "!$UEBERMITTLUNG_DOMAIN_COLOR_CONNECTION='red'\n" +
            "!$BESTAND_DOMAIN_COLOR_CONNECTION='red'\n" +
            "!$TAA_DOMAIN_COLOR_CONNECTION='red'\n" +
            "!$RISIKODATEN_DOMAIN_COLOR_CONNECTION='red'\n" +
            "!$SPEZIFISCHE_DOMAIN_COLOR_CONNECTION='red'\n" +
            "!if (%not(%variable_exists('$UML_STRICT')))\n" +
            "!$UML_STRICT = %false()\n" +
            "!endif"),
    INCLUDE("!include "),
    SEQUENCE_HEADER_VARIABLES("'!$DETAILED=%true()\n" +
            "'!$SIMPLE=%true()\n" +
            "'!$SHOW_SQL=%false()\n" +
            "'!$SHOW_DOCUMENT_LINK=%false()\n" +
            "'!$SHOW_EXCEPTION=%false()\n\n"),
    COMPONENT_HEADER_VARIABLES("'!$DETAILED=%true()\n" +
            "'!$UML_STRICT=%false()\n" +
            "!$SHOW_TODO=%false()\n\n\n");

    private final String content;

    TemplateContent(final String content) {
        this.content = content;
    }

    public String getContent() {
        return this.content;
    }
}

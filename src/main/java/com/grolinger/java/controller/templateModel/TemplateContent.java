package com.grolinger.java.controller.templatemodel;

public enum TemplateContent {
    START("@startuml\n"),
    END("@enduml"),
    DATE("' generated on "),
    CARRIAGE_RETURN("\n"),
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

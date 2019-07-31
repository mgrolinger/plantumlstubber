package com.grolinger.java.controller.templateModel;

public enum TemplateContent {
    START("@startuml\n"),
    END("@enduml"),
    DATE("' generated on "),
    EOL("\n"),
    COMMON_FILE("'This file is intentionally blank or consists of elements that are necessary to display generated files\n!define LUPDATE(_TEXT) :_TEXT\n!define INTEGRATION_DOMAIN_COLOR_CONNECTION red\n!define RESOURCE_DOMAIN_COLOR_CONNECTION red\n!define CUSTOMER_DOMAIN_COLOR_CONNECTION red\n!define FINANCIAL_DOMAIN_COLOR_CONNECTION red\n!define EXTERNAL_DOMAIN_COLOR_CONNECTION red"),
    INCLUDE("!include "),
    SEQUENCE_HEADER("'!define TECHNICAL\n'!define SIMPLE\n'!define SHOW_SQL\n'!define SHOW_DOCUMENT_LINK\n'!define SHOW_EXCEPTIONS\n\n"),
    COMPONENT_HEADER("!define DETAILED\n'!define UML_STRICT\n!define SHOW_TODO\n\n\n");

    private String content;

    TemplateContent(final String content) {
        this.content = content;
    }

    public String getContent() {
        return this.content;
    }
}

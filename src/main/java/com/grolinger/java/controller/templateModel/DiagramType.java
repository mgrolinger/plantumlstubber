package com.grolinger.java.controller.templatemodel;

import lombok.AllArgsConstructor;
import lombok.Getter;

import static com.grolinger.java.controller.templatemodel.Template.*;
import static com.grolinger.java.controller.templatemodel.TemplateContent.COMPONENT_HEADER_VARIABLES;
import static com.grolinger.java.controller.templatemodel.TemplateContent.SEQUENCE_HEADER_VARIABLES;

/**
 * Which plantUML diagram is processed
 */
@Getter
@AllArgsConstructor
public enum DiagramType {
    COMPONENT_V1_2019_6_DIAGRAM_BASE("Component/", COMPONENT_V1_2019_6, COMPONENT_HEADER_VARIABLES),
    SEQUENCE_V1_2019_6_DIAGRAM_BASE("Sequence/", SEQUENCE_V1_2019_6,    SEQUENCE_HEADER_VARIABLES),
    COMPONENT_V1_2020_7_DIAGRAM_BASE("Component/", COMPONENT_V1_2020_7, COMPONENT_HEADER_VARIABLES),
    SEQUENCE_V1_2020_7_DIAGRAM_BASE("Sequence/", SEQUENCE_V1_2020_7,    SEQUENCE_HEADER_VARIABLES);

    private String basePath;
    private Template template;
    private TemplateContent templateContent;

}

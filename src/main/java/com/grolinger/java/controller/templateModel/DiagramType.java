package com.grolinger.java.controller.templatemodel;

import lombok.AllArgsConstructor;
import lombok.Getter;

import static com.grolinger.java.controller.templatemodel.Template.COMPONENT_V2;
import static com.grolinger.java.controller.templatemodel.Template.SEQUENCE_V2;
import static com.grolinger.java.controller.templatemodel.TemplateContent.COMPONENTV2_HEADER;
import static com.grolinger.java.controller.templatemodel.TemplateContent.SEQUENCEV2_HEADER;

/**
 * Which plantUML diagram is processed
 */
@Getter
@AllArgsConstructor
public enum DiagramType {
    COMPONENT_DIAGRAM_BASE("Component/", COMPONENT_V2, COMPONENTV2_HEADER),
    SEQUENCE_DIAGRAM_BASE("Sequence/", SEQUENCE_V2, SEQUENCEV2_HEADER);

    private String basePath;
    private Template template;
    private TemplateContent templateContent;

}

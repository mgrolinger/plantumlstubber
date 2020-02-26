package com.grolinger.java.controller.templatemodel;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Which plantUML diagram is processed
 */
@AllArgsConstructor
public enum DiagramType {
    COMPONENT_DIAGRAM_BASE("Component/"),
    SEQUENCE_DIAGRAM_BASE("Sequence/");

    @Getter
    private String basePath;

}

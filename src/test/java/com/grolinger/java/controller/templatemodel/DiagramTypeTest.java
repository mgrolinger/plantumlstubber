package com.grolinger.java.controller.templatemodel;

import org.testng.annotations.Test;

import static com.grolinger.java.controller.templatemodel.DiagramType.*;
import static com.grolinger.java.controller.templatemodel.Template.*;
import static org.assertj.core.api.Assertions.assertThat;

/**
 *
 */
public class DiagramTypeTest {

    @Test
    public void getBasePath() {
        assertThat(COMPONENT_V1_2020_7_DIAGRAM_BASE.getBasePath())
                .isEqualTo(COMPONENT_V1_2019_6_DIAGRAM_BASE.getBasePath());
        assertThat(SEQUENCE_V1_2020_7_DIAGRAM_BASE.getBasePath())
                .isEqualTo(SEQUENCE_V1_2019_6_DIAGRAM_BASE.getBasePath());
    }

    @Test
    public void getTemplate() {
        assertThat(COMPONENT_V1_2020_7_DIAGRAM_BASE.getTemplate())
                .isEqualTo(COMPONENT_V1_2020_7);
        assertThat(COMPONENT_V1_2019_6_DIAGRAM_BASE.getTemplate())
                .isEqualTo(COMPONENT_V1_2019_6);
        assertThat(SEQUENCE_V1_2019_6_DIAGRAM_BASE.getTemplate())
                .isEqualTo(SEQUENCE_V1_2019_6);
        assertThat(SEQUENCE_V1_2020_7_DIAGRAM_BASE.getTemplate())
                .isEqualTo(SEQUENCE_V1_2020_7);
    }

    @Test
    public void getTemplateContent() {
        assertThat(COMPONENT_V1_2020_7_DIAGRAM_BASE.getTemplateContent())
                .isEqualTo(COMPONENT_V1_2019_6_DIAGRAM_BASE.getTemplateContent());
        assertThat(SEQUENCE_V1_2020_7_DIAGRAM_BASE.getTemplateContent())
                .isEqualTo(SEQUENCE_V1_2019_6_DIAGRAM_BASE.getTemplateContent());
    }
}
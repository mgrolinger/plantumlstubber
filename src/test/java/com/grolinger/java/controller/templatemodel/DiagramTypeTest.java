package com.grolinger.java.controller.templatemodel;

import org.testng.annotations.Test;

import static com.grolinger.java.controller.templatemodel.DiagramType.*;
import static com.grolinger.java.controller.templatemodel.Template.COMPONENT_V1_2020_7;
import static com.grolinger.java.controller.templatemodel.Template.SEQUENCE_V1_2020_7;
import static org.assertj.core.api.Assertions.assertThat;

/**
 *
 */
public class DiagramTypeTest {

    @Test
    public void getBasePath() {
        assertThat(COMPONENT_V1_2020_7_DIAGRAM_BASE.getBasePath())
                .isEqualTo("Component/");
        assertThat(SEQUENCE_V1_2020_7_DIAGRAM_BASE.getBasePath())
                .isEqualTo("Sequence/");
    }

    @Test
    public void getTemplate() {
        assertThat(COMPONENT_V1_2020_7_DIAGRAM_BASE.getTemplate())
                .isEqualTo(COMPONENT_V1_2020_7);
        assertThat(SEQUENCE_V1_2020_7_DIAGRAM_BASE.getTemplate())
                .isEqualTo(SEQUENCE_V1_2020_7);
    }

    @Test
    public void getTemplateContent() {
        assertThat(COMPONENT_V1_2020_7_DIAGRAM_BASE.getTemplateContent()).isNotNull();
        assertThat(SEQUENCE_V1_2020_7_DIAGRAM_BASE.getTemplateContent()).isNotNull();;
    }
}
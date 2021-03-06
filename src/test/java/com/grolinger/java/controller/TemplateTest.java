package com.grolinger.java.controller;

import com.grolinger.java.controller.templatemodel.Template;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class TemplateTest {

    @Test
    public void testGetContent() {

        assertThat(Template.COMPONENT_V1_2020_7.getTemplateURL()).isEqualTo("componentExport_V1_2020_7.html");
        assertThat(Template.SEQUENCE_V1_2020_7.getTemplateURL()).isEqualTo("sequenceExport_V1_2020_7.html");
    }
}
package com.grolinger.java.controller;

import com.grolinger.java.controller.templatemodel.Template;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class TemplateTest {

    @Test
    public void testGetContent() {

        assertThat(Template.COMPONENT_V2.getTemplateURL()).isEqualTo("componentExportV2.html");
        assertThat(Template.SEQUENCE_V2.getTemplateURL()).isEqualTo("sequenceExportV2.html");
    }
}
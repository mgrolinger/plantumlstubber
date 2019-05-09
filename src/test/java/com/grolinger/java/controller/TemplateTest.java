package com.grolinger.java.controller;

import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class TemplateTest {

    @Test
    public void testGetContent() {
        assertThat(Template.COMPONENT.getTemplateURL()).isEqualTo("componentExport.html");
    }
}
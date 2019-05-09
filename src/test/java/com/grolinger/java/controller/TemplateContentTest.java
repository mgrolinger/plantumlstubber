package com.grolinger.java.controller;

import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class TemplateContentTest {

    @Test
    public void testGetContent() {
        assertThat(TemplateContent.EOL.getContent()).isEqualTo("\n");
    }
}
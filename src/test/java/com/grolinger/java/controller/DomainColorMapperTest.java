package com.grolinger.java.controller;

import com.grolinger.java.service.mapper.DomainColorMapper;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class DomainColorMapperTest {
    private static final String INTEGRATION = "integration";

    @Test
    public void getByType() {
        assertThat(DomainColorMapper.getByType(INTEGRATION)).isEqualTo(DomainColorMapper.INTEGRATION_DOMAIN_COLOR);
    }

    @Test
    public void getValue() {
        assertThat(DomainColorMapper.INTEGRATION_DOMAIN_COLOR.getValue()).isEqualTo(INTEGRATION);
    }

    @Test
    public void getStereotype() {
        assertThat(DomainColorMapper.INTEGRATION_DOMAIN_COLOR.getStereotype()).isEqualTo("<<"+INTEGRATION+">>");
    }
}
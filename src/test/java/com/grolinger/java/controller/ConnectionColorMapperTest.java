package com.grolinger.java.controller;

import com.grolinger.java.service.mapper.ConnectionColorMapper;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ConnectionColorMapperTest {
    private static final String INTEGRATION = "integration";

    @Test
    public void getByType() {
        assertThat(ConnectionColorMapper.getByType(INTEGRATION)).isEqualTo(ConnectionColorMapper.INTEGRATION_DOMAIN_COLOR_CONNECTION);
    }

    @Test
    public void getValue() {
        assertThat(ConnectionColorMapper.INTEGRATION_DOMAIN_COLOR_CONNECTION.getValue()).isEqualTo(INTEGRATION);
    }
}
package com.grolinger.java.controller;

import com.grolinger.java.service.mapper.DomainColorMapper;
import org.springframework.util.StringUtils;
import org.testng.annotations.Test;
import org.thymeleaf.context.Context;

import java.time.LocalDate;


import static com.grolinger.java.controller.templateModel.ContextVariables.*;
import static com.grolinger.java.service.mapper.ConnectionColorMapper.INTEGRATION_DOMAIN_COLOR_CONNECTION;
import static org.assertj.core.api.Assertions.assertThat;

public class ContextSpecTest {

    @Test
    public void testBuilder() {

        final String applicationName = "testApplication";
        final String serviceName = "testServiceName/withSeparator";
        final String serviceNameAfterConversion = "TestServiceNameWithSeparator";
        final String colorName = "integration";
        final String integrationType = "TestIntegration";
        final String interfaceName = "testInterfaceName";
        final String commonPath = "../testCommonPath";

        Context result = new ContextSpec().builder().withColorName(DomainColorMapper.getByType(colorName))
                .withIntegrationType(integrationType)
                .withApplicationName(applicationName)
                .withPreformattedServiceName(serviceName)
                .withInterfaceName(interfaceName)
                .withCommonPath(commonPath)
                .withOrderPrio(1)
                .getContext();

        assertThat(result.getVariable(DATE_CREATED.getName())).isEqualTo(LocalDate.now());
        assertThat(result.getVariable(COLOR_NAME.getName())).isEqualTo("integration");
        assertThat(result.getVariable(COLOR_TYPE.getName())).isEqualTo("<<integration>>");
        assertThat(result.getVariable(CONNECTION_COLOR.getName())).isEqualTo(INTEGRATION_DOMAIN_COLOR_CONNECTION);
        assertThat(result.getVariable(COMPONENT_INTEGRATION_TYPE.getName())).isEqualTo("INTEGRATION_TYPE(" + integrationType + ")");
        assertThat(result.getVariable(IS_ROOT_SERVICE.getName())).isEqualTo(false);
        assertThat(result.getVariable(IS_REST_SERVICE.getName())).isEqualTo(false);
        assertThat(result.getVariable(APPLICATION_NAME.getName())).isEqualTo(StringUtils.capitalize(applicationName));
        assertThat(result.getVariable(APPLICATION_NAME_SHORT.getName())).isEqualTo(applicationName.toLowerCase());
        assertThat(result.getVariable(SERVICE_NAME.getName())).isEqualTo(serviceName);
        assertThat(result.getVariable(INTERFACE_NAME.getName())).isEqualTo(interfaceName);
        assertThat(result.getVariable(SEQUENCE_PARTICIPANT_ORDER.getName())).isEqualTo(1);
        assertThat(result.getVariable(PATH_TO_COMMON_FILE.getName())).isEqualTo(commonPath);
        assertThat(result.getVariable(SEQUENCE_PARTICIPANT_ORDER.getName())).isEqualTo(1);
        assertThat(result.getVariable(COMPLETE_INTERFACE_NAME.getName())).isEqualTo(StringUtils.capitalize(applicationName) + serviceNameAfterConversion + StringUtils.capitalize(interfaceName) + "Int");
        assertThat(result.getVariable(API_CREATED.getName())).isEqualTo((applicationName + "_API_" + serviceName.replace("/", "_") + "_" + interfaceName + "_CREATED").toUpperCase());

    }
}
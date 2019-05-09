package com.grolinger.java.controller;

import org.springframework.util.StringUtils;
import org.testng.annotations.Test;
import org.thymeleaf.context.Context;

import java.time.LocalDate;

import static com.grolinger.java.controller.ConnectionColorMapper.INTEGRATION_DOMAIN_COLOR_CONNECTION;
import static com.grolinger.java.controller.ContextVariables.*;
import static org.assertj.core.api.Assertions.assertThat;

public class ContextSpecTest {

    @Test
    public void testBuilder() {
        ContextSpec.OrderPrioBuilder cut = new ContextSpec().builder();
        assertThat(cut).isNotNull();

        final String applicationName = "testApplication";
        final String serviceName = "testServiceName/withSeparator";
        final String serviceNameAfterConversion = "TestServiceNameWithSeparator";
        final String colorName = "integration";
        final String integrationType = "TestIntegration";
        final String interfaceName = "testInterfaceName";
        final String commonPath = "../testCommonPath";

        Context result = cut.withOrderPrio(1).withColorName(DomainColorMapper.getByType(colorName))
                .withIntegrationType(integrationType)
                .withApplicationName(applicationName)
                .withPreformattedServiceName(serviceName)
                .withInterfaceName(interfaceName)
                .withCommonPath(commonPath).getContext();
        // FIXME
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
        assertThat(result.getVariable(COMPLETE_INTERFACE_NAME.getName())).isEqualTo(StringUtils.capitalize(applicationName) + serviceNameAfterConversion + StringUtils.capitalize(interfaceName) + "Int");
        assertThat(result.getVariable(API_CREATED.getName())).isEqualTo((applicationName + "_API_" + serviceName.replace("/", "_") + "_" + interfaceName + "_CREATED").toUpperCase());

    }
}
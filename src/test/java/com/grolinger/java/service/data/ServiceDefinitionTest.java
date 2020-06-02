package com.grolinger.java.service.data;

import com.grolinger.java.controller.templatemodel.Constants;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static com.grolinger.java.controller.templatemodel.Constants.DEFAULT_ROOT_SERVICE_NAME;
import static org.assertj.core.api.Assertions.assertThat;

public class ServiceDefinitionTest {

    @DataProvider
    public Object[][] getServiceCallNameDataProvider() {
        return new Object[][]{
                //@formatter:off
                {"serviceName", "serviceName"},
                {"serviceName/service", "serviceName_service"},
                {"serviceName/{service}", "serviceName_service"},
                {Constants.EMPTY.getValue(), DEFAULT_ROOT_SERVICE_NAME.getValue()}
                //@formatter:on
        };
    }

    @Test(dataProvider = "getServiceCallNameDataProvider")
    public void testGetServiceCallNameDataProvider(final String serviceName,
                                                   final String expectedValue) {
        ServiceDefinition cut = ServiceDefinition.builder()
                .serviceName(serviceName).domainColor("integration").build();
        assertThat(cut.getServiceCallName()).isEqualTo(expectedValue);
    }

    @DataProvider
    public Object[][] testGetFormattedServiceNameDataProvider() {
        final String serviceName = "serviceName";
        final String serviceNameWithUnwantedChars = "restServiceName/{subservice}/interface";
        final String soapServiceNameWithUnwantedChars = "soapServiceName.subservice.interface";
        return new Object[][]{
                //@formatter:off
                {null, DEFAULT_ROOT_SERVICE_NAME.getValue()},
                {null, DEFAULT_ROOT_SERVICE_NAME.getValue()},
                {"", DEFAULT_ROOT_SERVICE_NAME.getValue()},
                {"", DEFAULT_ROOT_SERVICE_NAME.getValue()},
                {serviceName, serviceName},
                {serviceNameWithUnwantedChars, "restServiceName/{subservice}/interface"},
                {soapServiceNameWithUnwantedChars, "soapServiceName.subservice.interface"}
                //@formatter:on
        };
    }

    @Test(dataProvider = "testGetFormattedServiceNameDataProvider")
    public void testGetFormattedServiceName(final String serviceName, final String expectedResult) {
        // FixmE
        ServiceDefinition cut = ServiceDefinition.builder()
                .serviceName(serviceName).domainColor("integration").build();
        String result = cut.getServiceLabel();
        assertThat(result).isNotNull().isEqualTo(expectedResult);
    }
}
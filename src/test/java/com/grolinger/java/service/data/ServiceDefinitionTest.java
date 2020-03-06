package com.grolinger.java.service.data;

import com.grolinger.java.controller.templatemodel.Constants;
import org.springframework.util.StringUtils;
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
                {Constants.EMPTY.getValue(), DEFAULT_ROOT_SERVICE_NAME.getValue()}
                //@formatter:on
        };
    }

    @Test(dataProvider = "getServiceCallNameDataProvider")
    public void testGetServiceCallNameDataProvider(final String serviceName,
                                                   final String expectedValue) {
        ServiceDefinition cut = new ServiceDefinition(serviceName, "", "integration", 0);
        String result = cut.getServiceCallName();
        assertThat(result).isEqualTo(expectedValue);
    }

    @DataProvider
    public Object[][] testGetFormattedServiceNameDataProvider() {
        final String application = "applicationName";
        final String serviceName = "serviceName";
        final String serviceNameWithUnwantedChars = "restServiceName/subservice/interface";
        final String soapServiceNameWithUnwantedChars = "soapServiceName/subservice/interface";
        return new Object[][]{
                //@formatter:off
                {null, DEFAULT_ROOT_SERVICE_NAME.getValue()},
                {null, DEFAULT_ROOT_SERVICE_NAME.getValue()},
                {"", DEFAULT_ROOT_SERVICE_NAME.getValue()},
                {"", DEFAULT_ROOT_SERVICE_NAME.getValue()},
                {serviceName, serviceName},
                {serviceNameWithUnwantedChars, "restServiceName_subservice_interface"},
                {soapServiceNameWithUnwantedChars, "soapServiceName_subservice_interface"}
                //@formatter:on
        };
    }

    @Test(dataProvider = "testGetFormattedServiceNameDataProvider")
    public void testGetFormattedServiceName(final String serviceName, final String expectedResult) {
        // FixmE
        ServiceDefinition cut = new ServiceDefinition(serviceName, "", "integration", 0);
        String result = cut.getServiceCallName();
        assertThat(result).isNotNull().isEqualTo(expectedResult);

    }
}
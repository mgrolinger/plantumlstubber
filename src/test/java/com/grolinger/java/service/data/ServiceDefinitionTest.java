package com.grolinger.java.service.data;

import com.grolinger.java.controller.templatemodel.Constants;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static com.grolinger.java.controller.templatemodel.Constants.DEFAULT_ROOT_SERVICE_NAME;
import static com.grolinger.java.controller.templatemodel.Constants.SLASH;
import static org.assertj.core.api.Assertions.assertThat;

public class ServiceDefinitionTest {

    @DataProvider
    public Object[][] serviceNameDataProvider() {
        final String simpleServiceName = "serviceName";
        final String restServiceName = "restServiceName/{subservice}/subs";
        final String soapServiceNameWithUnwantedChars = "soapServiceName.subservice.subs";
        final String defaultServiceName = DEFAULT_ROOT_SERVICE_NAME.getValue();
        return new Object[][]{
                //@formatter:off
                {null,                          defaultServiceName ,                defaultServiceName},
                {"",                            defaultServiceName,                 defaultServiceName},
                {Constants.EMPTY.getValue(),    defaultServiceName,                 defaultServiceName},
                {simpleServiceName,             simpleServiceName,                  simpleServiceName},
                {restServiceName,               "restServiceName_subservice_subs" , restServiceName},
                {soapServiceNameWithUnwantedChars,"soapServiceName_subservice_subs",soapServiceNameWithUnwantedChars}
                //@formatter:on
        };
    }

    @Test(dataProvider = "serviceNameDataProvider")
    public void testServiceName(final String serviceName, final String expectedServiceCall, final String expectedName) {
        // given
        final String domainColorDefault = "domainColorDefault";
        ServiceDefinition cut = ServiceDefinition.builder()
                .serviceName(serviceName)
                .domainColor(domainColorDefault)
                .build();

        // when - then
        assertThat(cut.getServiceCallName()).isEqualTo(expectedServiceCall);
        assertThat(cut.getServiceLabel()).isEqualTo(expectedName);
        assertThat(cut.getDomainColor()).isEqualTo(domainColorDefault);
    }

    @DataProvider
    public Object[][] servicePathDataProvider() {
        final String simpleServiceName = "serviceName";
        final String restServiceName = "restServiceName/{subservice}/subs";
        final String soapServiceNameWithUnwantedChars = "soapServiceName.subservice.subs";
        final String defaultServiceName = DEFAULT_ROOT_SERVICE_NAME.getValue();
        return new Object[][]{
                //@formatter:off
                {null,                              defaultServiceName, ""},
                {"",                                defaultServiceName, ""},
                {" ",                               defaultServiceName, ""},
                {Constants.EMPTY.getValue(),        defaultServiceName, ""},
                {simpleServiceName,                 simpleServiceName+SLASH.getValue(),"../"},
                {restServiceName,                   "restServiceName/subservice/subs/","../../../" },
                {soapServiceNameWithUnwantedChars,  "soapServiceName/subservice/subs/","../../../" }
                //@formatter:on
        };
    }

    @Test(dataProvider = "servicePathDataProvider")
    public void testservicePath(final String serviceName, final String expServicePath, final String expCommonPath) {
        // given
        ServiceDefinition cut = ServiceDefinition.builder()
                .serviceName(serviceName)
                .build();

        // when - then
        assertThat(cut.getPath()).isEqualTo(expServicePath);
        assertThat(cut.getPathToRoot()).isEqualTo(expCommonPath);
    }
}
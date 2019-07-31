package com.grolinger.java.service;

import com.grolinger.java.controller.templateModel.Constants;
import com.grolinger.java.service.impl.NameServiceImpl;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.util.StringUtils;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

@Test
@ContextConfiguration(locations = {"classpath:spring-test-config.xml"})
public class NameServiceTest {
    @InjectMocks
    private NameService cut = new NameServiceImpl();

    @BeforeTest
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @DataProvider
    public Object[][] testGetReplaceUnwantedCharactersDataProvider() {
        String pathWitDots = "test.test";
        final boolean REPLACEDOTSONLY = true;
        final boolean REPLACEALL = false;
        return new Object[][]{
                //@formatter:off
                {pathWitDots,       REPLACEDOTSONLY, "test_test"},
                {pathWitDots,       REPLACEALL,      "test_test"},
                {pathWitDots+"/",   REPLACEDOTSONLY, "test_test/"},
                {pathWitDots+"/",   REPLACEALL,      "test_test_"},
                {pathWitDots+"_",   REPLACEALL,      "test_test_"},
                {"",                REPLACEALL,      ""},
                {null,              REPLACEALL,      ""}
                //@formatter:on
        };
    }

    @Test(dataProvider = "testGetReplaceUnwantedCharactersDataProvider")
    public void testGetReplaceUnwantedCharacters(final String startValue, final boolean dots,
                                                 final String expectedValue) {
        String result = cut.replaceUnwantedCharacters(startValue, dots);
        assertThat(result).isEqualTo(expectedValue);
    }

    @DataProvider
    public Object[][] getServiceCallNameDataProvider() {
        return new Object[][]{
                //@formatter:off
                {"applicationName", "serviceName",              "applicationName_serviceName"},
                {"applicationName", Constants.EMPTY.getValue(), "applicationName"}
                //@formatter:on
        };
    }

    @Test(dataProvider = "getServiceCallNameDataProvider")
    public void testGetServiceCallNameDataProvider(final String applicationName, final String serviceName,
                                                   final String expectedValue) {
        String result = cut.getServiceCallName(applicationName, serviceName);
        assertThat(result).isEqualTo(expectedValue);
    }

    @DataProvider
    public Object[][] testGetServiceNameSuffixDataProvider() {
        final String fullPath = "applicationName/serviceName";
        final String serviceName = "serviceName";
        final String empty = "emPTy";
        return new Object[][]{
                //@formatter:off
                //No separator for root services
                {null,          ""      },
                {empty,         ""      },
                // everything else should have a separator at the end
                {fullPath,      fullPath+"/" },
                {serviceName,   serviceName+"/"   }
                //@formatter:on
        };
    }

    @Test(dataProvider = "testGetServiceNameSuffixDataProvider")
    public void testGetServiceNameSuffix(final String pathName, final String expectedResult) {
        String result = cut.getServiceNameSuffix(pathName);
        assertThat(result).isEqualTo(expectedResult);

    }

    @DataProvider
    public Object[][] testformatServiceNameDataProvider() {
        final String serviceName = "serviceName";
        final String capServiceName = StringUtils.capitalize(serviceName);
        final String serviceNameWithUnwantedChars = "serviceName/subservice_interface";
        final boolean ISREST = true;
        final boolean NOTREST = false;
        return new Object[][]{
                //@formatter:off
                { null,         ISREST,    Constants.DEFAULT_ROOT_SERVICE_NAME.getValue() },
                { null,         NOTREST,   Constants.DEFAULT_ROOT_SERVICE_NAME.getValue() },
                { "",           ISREST,    Constants.DEFAULT_ROOT_SERVICE_NAME.getValue() },
                { "",           NOTREST,   Constants.DEFAULT_ROOT_SERVICE_NAME.getValue() },
                { serviceName,  ISREST,    serviceName },
                { serviceName,  NOTREST,   capServiceName },
                { serviceNameWithUnwantedChars,  ISREST,    "serviceName_subservice_interface" },
                { serviceNameWithUnwantedChars,  NOTREST,   "ServiceName_subservice_interface" }
                //@formatter:on
        };
    }

    @Test(dataProvider = "testformatServiceNameDataProvider")
    public void testformatServiceName(final String pathName, final boolean isRest, final String expectedResult) {
        String result = cut.formatServiceName(pathName,isRest);
        assertThat(result).isEqualTo(expectedResult);

    }
}
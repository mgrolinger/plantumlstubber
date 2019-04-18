package com.grolinger.java.service;

import com.grolinger.java.controller.Constants;
import com.grolinger.java.service.impl.NameServiceImpl;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.ContextConfiguration;
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
    public Object[][] getReplaceUnwantedCharactersDataProvider() {
        return new Object[][]{
                {"test.test", true, "test_test"},
                {"test.test", false, "test_test"},
                {"test.test/", true, "test_test_"},
                {"test.test/", false, "test_test/"}
        };
    }

    @Test(dataProvider = "getReplaceUnwantedCharactersDataProvider")
    public void testGetReplaceUnwantedCharacters(final String startValue, final boolean dots, final String expectedValue) {
        String result = cut.getReplaceUnwantedCharacters(startValue, dots);
        assertThat(result).isEqualTo(expectedValue);
    }

    @DataProvider
    public Object[][] getServiceCallNameDataProvider() {
        return new Object[][]{
                {"applicationName", "serviceName", "applicationName_serviceName"},
                {"applicationName", Constants.EMPTY.getValue(), "applicationName"}
        };
    }

    @Test(dataProvider = "getServiceCallNameDataProvider")
    public void testGetServiceCallNameDataProvider(final String applicationName, final String serviceName, final String expectedValue) {
        String result = cut.getServiceCallName(applicationName, serviceName);
        assertThat(result).isEqualTo(expectedValue);
    }

}
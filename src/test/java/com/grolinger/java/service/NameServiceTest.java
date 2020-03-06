package com.grolinger.java.service;

import org.mockito.MockitoAnnotations;
import org.springframework.test.context.ContextConfiguration;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

@Test
@ContextConfiguration(locations = {"classpath:spring-test-config.xml"})
public class NameServiceTest {

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
                {"test \n test",   REPLACEALL,      "test___test"},
                {"test \\ test",   REPLACEALL,      "test___test"},
                {"",                REPLACEALL,      ""},
                {null,              REPLACEALL,      ""}
                //@formatter:on
        };
    }

    @Test(dataProvider = "testGetReplaceUnwantedCharactersDataProvider")
    public void testGetReplaceUnwantedCharacters(final String startValue, final boolean dots,
                                                 final String expectedValue) {
        String result = NameService.replaceUnwantedCharacters(startValue, dots);
        assertThat(result).isEqualTo(expectedValue);
    }

}
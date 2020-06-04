package com.grolinger.java.service;

import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

@Test
public class NameConverterTest {

    @BeforeTest
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @DataProvider
    public Object[][] testGetReplaceUnwantedCharactersDataProvider() {
        String pathWithDots = "test.test";
        String restPath = "/api/resource/{id}/subresource";
        final boolean REPLACEDOTSONLY = true;
        final boolean REPLACEALL = false;
        return new Object[][]{
                //@formatter:off
                {pathWithDots,     REPLACEDOTSONLY, "test_test"},
                {pathWithDots,     REPLACEALL,      "test_test"},
                {pathWithDots+"/", REPLACEDOTSONLY, "test_test/"},
                {pathWithDots+"/", REPLACEALL,      "test_test_"},
                {pathWithDots+"_", REPLACEALL,      "test_test_"},
                {"test \n test",   REPLACEALL,      "test_test"},
                {"test \\ test",   REPLACEALL,      "test_test"},
                {restPath,         REPLACEALL,      "_api_resource_id_subresource"},
                {restPath,         REPLACEDOTSONLY, "/api/resource/id/subresource"},
                {"",               REPLACEALL,      ""},
                {null,             REPLACEALL,      ""}
                //@formatter:on
        };
    }

    @Test(dataProvider = "testGetReplaceUnwantedCharactersDataProvider")
    public void testGetReplaceUnwantedCharacters(final String startValue, final boolean dots,
                                                 final String expectedValue) {
        String result = NameConverter.replaceUnwantedPlantUMLCharacters(startValue, dots);
        assertThat(result).isEqualTo(expectedValue);
    }

    @DataProvider
    public Object[][] testReplaceUnwantedPlantUMLCharactersForPathDataProvider() {
        String pathWithDots = "test.test";
        String restPath = "/api/resource/{id}/subresource";
        return new Object[][]{
                //@formatter:off
                {pathWithDots,      "test/test"},
                {pathWithDots+"/", "test/test/"},
                {"test \n test",   "test/test"},
                {"test \\ test",   "test/test"},
                {"test//test",     "test/test"},
                {restPath,         "/api/resource/id/subresource"},
                {"",               ""},
                {null,             ""}
                //@formatter:on
        };
    }

    @Test(dataProvider = "testReplaceUnwantedPlantUMLCharactersForPathDataProvider")
    public void testReplaceUnwantedPlantUMLCharactersForPath(final String startValue, final String expectedValue) {
        String result = NameConverter.replaceUnwantedPlantUMLCharactersForPath(startValue);
        assertThat(result).isEqualTo(expectedValue);
    }

}
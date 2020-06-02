package com.grolinger.java.service.data.mapper;

import javax.validation.constraints.NotNull;

/**
 * Color mapper that will generate the three different types used in a template
 * - Domain color is mainly be used for the <i>skinparam</i> section
 * - Connection color is used for connections between components
 * - Stereotypes are used for component definitions
 */
public final class ColorMapper {

    public static String getDomainColor(@NotNull final String colorName) {
        return colorName.toUpperCase() + "_DOMAIN_COLOR";
    }

    public static String getConnectionColor(@NotNull final String colorName) {
        return colorName.toUpperCase() + "_DOMAIN_COLOR_CONNECTION";
    }

    public static String getStereotype(final String colorName) {
        return "<<" + colorName + ">>";
    }
}

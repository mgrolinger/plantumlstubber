package com.grolinger.java.service;

public interface NameService {

    String replaceUnwantedCharacters(final String name, final boolean replaceDotsOnly);

    String getServiceCallName(final String applicationName, final String serviceName);

    String formatServiceName(final String serviceName, final boolean isRest);

    String getServiceNameSuffix(final String serviceName);
}
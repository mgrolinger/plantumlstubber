package com.grolinger.java.service;

public interface NameService {

    String getReplaceUnwantedCharacters(final String name, final boolean replaceDotsOnly);

    String getServiceCallName(final String applicationName, final String serviceName);

    String formatServiceName(final String serviceName, final boolean isRest);

    String getServicePathPrefix(final String serviceName);
}
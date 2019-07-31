package com.grolinger.java.service;

import com.grolinger.java.config.Services;

public interface DecisionService {
    boolean isCurrentServiceARestService(final Services services);

    boolean isCurrentServiceARestService(final String IntegrationType);
}
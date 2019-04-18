package com.grolinger.java.service.impl;

import com.grolinger.java.config.Services;
import com.grolinger.java.service.DecisionService;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class DecisionServiceImpl implements DecisionService {

    public boolean isCurrentServiceARestService(final Services services) {
        boolean isRestService = false;
        if (services != null && !StringUtils.isEmpty(services.getIntegrationType())) {
            isRestService = this.isCurrentServiceARestService(services.getIntegrationType());
        }
        return isRestService;
    }

    public boolean isCurrentServiceARestService(final String integrationType) {
        boolean isRestService = false;
        if (!StringUtils.isEmpty(integrationType)) {
            isRestService = integrationType.toUpperCase().contains("REST");
        }
        return isRestService;
    }
}

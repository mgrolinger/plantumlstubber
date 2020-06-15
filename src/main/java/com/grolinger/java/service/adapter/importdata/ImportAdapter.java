package com.grolinger.java.service.adapter.importdata;

import com.grolinger.java.service.data.ApplicationDefinition;

import java.util.List;

public interface ImportAdapter {
    /**
     * Searches for *.yaml definitions of applications. If found it will hierarchically map the files into {@link ApplicationDefinition},
     * {@link com.grolinger.java.service.data.ServiceDefinition} and {@link com.grolinger.java.service.data.InterfaceDefinition}
     *
     * @return hierarchical definitions of all found application definitions
     */
    List<ApplicationDefinition> findAllServiceEndpoints();
}
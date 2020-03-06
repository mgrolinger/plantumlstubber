package com.grolinger.java.service.adapter.importdata;

import lombok.*;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Class that is used in the context of {@link com.grolinger.java.controller.MultiExportController}
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ImportedServices {
    private String application;
    private String customLabel;
    private String customAlias;
    @Builder.Default
    private String systemType = "application";
    private String domainColor;
    private String orderPrio;
    private LinkedHashMap<String, LinkedHashMap<String, String[]>> services;
    private String linkToComponent;
    private String linkToCustomAlias;
}

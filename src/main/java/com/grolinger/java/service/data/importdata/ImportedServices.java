package com.grolinger.java.service.data.importdata;

import lombok.*;

import java.util.LinkedHashMap;

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

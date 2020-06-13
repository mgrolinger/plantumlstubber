package com.grolinger.java.controller;

import com.grolinger.java.service.DataProcessorService;
import com.grolinger.java.service.adapter.importdata.ImportAdapter;
import com.grolinger.java.service.data.ApplicationDefinition;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import static com.grolinger.java.controller.templatemodel.DiagramType.*;

@Slf4j
@RestController
public class MultiExportController {
    private final DataProcessorService dataProcessorService;
    private final ImportAdapter importAdapter;

    private MultiExportController(@Autowired DataProcessorService dataProcessorService, @Autowired ImportAdapter importAdapter) {
        this.dataProcessorService = dataProcessorService;
        this.importAdapter = importAdapter;
    }

    @ApiOperation(
            value = "Exports component stubs and example files to the local filesystem."
    )
    @GetMapping("/export/components")
    public void component(Model model, @ApiParam(allowableValues = "1.2019.6,1.2020.7") @RequestParam(required = false, defaultValue = "1.2020.7") String preprocessorVersion) throws IOException {
        // Find all yaml files
        List<ApplicationDefinition> pumlComponents = new LinkedList<>(importAdapter.findAllServiceEndpoints());
        if ("1.2020.7".equalsIgnoreCase(preprocessorVersion)) {
            dataProcessorService.processApplication(pumlComponents, COMPONENT_V1_2020_7_DIAGRAM_BASE);
        } else {
            dataProcessorService.processApplication(pumlComponents, COMPONENT_V1_2019_6_DIAGRAM_BASE);
        }
        log.info("Processing components completed. Using preprocessor version {} for export.", preprocessorVersion);
        if (pumlComponents.isEmpty()) {
            log.warn("No config was found, exporting template.");
            dataProcessorService.exportTemplate();
        }
    }

    @ApiOperation(
            value = "Exports component stubs and example files to the local filesystem."
    )
    @GetMapping("/export/sequences")
    public void sequence(Model model, @ApiParam(allowableValues = "1.2019.6,1.2020.7") @RequestParam(required = false, defaultValue = "1.2020.7") String preprocessorVersion) throws IOException {
        // Find all yaml files
        List<ApplicationDefinition> pumlComponents = new LinkedList<>(importAdapter.findAllServiceEndpoints());
        if ("1.2020.7".equalsIgnoreCase(preprocessorVersion)) {
            dataProcessorService.processApplication(pumlComponents, SEQUENCE_V1_2020_7_DIAGRAM_BASE);
        } else {
            dataProcessorService.processApplication(pumlComponents, SEQUENCE_V1_2019_6_DIAGRAM_BASE);
        }
        log.info("Processing sequences completed. Using preprocessor version {} for export.", preprocessorVersion);
    }


    @ApiOperation(
            value = "Exports an example template for application definitions to the local filesystem."
    )
    @GetMapping("/export/template")
    public void template() {
        dataProcessorService.exportTemplate();
    }
}
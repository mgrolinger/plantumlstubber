package com.grolinger.java.controller;

import com.grolinger.java.service.DataProcessorService;
import com.grolinger.java.service.adapter.importdata.ImportAdapter;
import com.grolinger.java.service.data.ApplicationDefinition;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import static com.grolinger.java.controller.templatemodel.DiagramType.COMPONENT_V1_2020_7_DIAGRAM_BASE;
import static com.grolinger.java.controller.templatemodel.DiagramType.SEQUENCE_V1_2020_7_DIAGRAM_BASE;

@RequiredArgsConstructor
@Slf4j
@RestController
public class MultiExportController {
    private final DataProcessorService dataProcessorService;
    private final ImportAdapter importAdapter;

    @Operation(
            summary = "Exports component stubs and example files to the local filesystem."
    )
    @GetMapping("/export/components")
    public void component(Model model) throws IOException {
        // Find all yaml files
        List<ApplicationDefinition> pumlComponents = new LinkedList<>(importAdapter.findAllServiceEndpoints());
        dataProcessorService.processApplication(pumlComponents, COMPONENT_V1_2020_7_DIAGRAM_BASE);

        log.info("Processing components completed. Using preprocessor version {} for export.", "1.2020.07");
        if (pumlComponents.isEmpty()) {
            log.warn("No config was found, exporting template.");
            dataProcessorService.exportTemplate();
        }
    }

    @Operation(
            summary = "Exports component stubs and example files to the local filesystem."
    )
    @GetMapping("/export/sequences")
    public void sequence(Model model) throws IOException {
        // Find all yaml files
        List<ApplicationDefinition> pumlComponents = new LinkedList<>(importAdapter.findAllServiceEndpoints());
        dataProcessorService.processApplication(pumlComponents, SEQUENCE_V1_2020_7_DIAGRAM_BASE);
        log.info("Processing sequences completed. Using preprocessor version {} for export.", "1.2020.07");
    }


    @Operation(
            summary = "Exports an example template for application definitions to the local filesystem."
    )
    @GetMapping("/export/template")
    public void template() {
        dataProcessorService.exportTemplate();
    }
}
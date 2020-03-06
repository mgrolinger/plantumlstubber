package com.grolinger.java.controller;

import com.grolinger.java.config.Loggable;
import com.grolinger.java.service.DataProcessorService;
import com.grolinger.java.service.adapter.ImportService;
import com.grolinger.java.service.data.ApplicationDefinition;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import static com.grolinger.java.controller.templatemodel.DiagramType.COMPONENT_DIAGRAM_BASE;
import static com.grolinger.java.controller.templatemodel.DiagramType.SEQUENCE_DIAGRAM_BASE;

@RestController
public class MultiExportController implements Loggable {
    private DataProcessorService dataProcessorService;
    private ImportService importService;

    private MultiExportController(@Autowired DataProcessorService dataProcessorService, @Autowired ImportService importService) {
        this.dataProcessorService = dataProcessorService;
        this.importService = importService;
    }

    @ApiOperation(
            value = "Exports component stubs and example files to the local filesystem."
    )
    @GetMapping("/export/components")
    public void component(Model model) throws IOException {
        // Find all yaml files
        List<ApplicationDefinition> pumlComponents = new LinkedList<>(importService.findAllServiceEndpoints());

        dataProcessorService.processApplication(pumlComponents, COMPONENT_DIAGRAM_BASE);
        logger().info("Processing components completed.");
    }

    @ApiOperation(
            value = "Exports component stubs and example files to the local filesystem."
    )
    @GetMapping("/export/sequences")
    public void sequence(Model model) throws IOException {
        // Find all yaml files
        List<ApplicationDefinition> pumlComponents = new LinkedList<>(importService.findAllServiceEndpoints());

        dataProcessorService.processApplication(pumlComponents, SEQUENCE_DIAGRAM_BASE);
        logger().info("Processing sequences completed.");
    }

}
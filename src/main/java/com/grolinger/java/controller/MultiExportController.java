package com.grolinger.java.controller;

import com.grolinger.java.config.Loggable;
import com.grolinger.java.service.DataProcessorService;
import com.grolinger.java.service.adapter.ImportService;
import com.grolinger.java.service.data.ApplicationDefinition;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import static com.grolinger.java.controller.templatemodel.DiagramType.*;

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
    public void component(Model model, @ApiParam(allowableValues = "1.2019.6,1.2020.7") @RequestParam(required = false, defaultValue = "1.2020.7") String preprocessorVersion) throws IOException {
        // Find all yaml files
        List<ApplicationDefinition> pumlComponents = new LinkedList<>(importService.findAllServiceEndpoints());
        if ("1.2020.7".equalsIgnoreCase(preprocessorVersion)) {
            dataProcessorService.processApplication(pumlComponents, COMPONENT_V1_2020_7_DIAGRAM_BASE);
        } else {
            dataProcessorService.processApplication(pumlComponents, COMPONENT_V1_2019_6_DIAGRAM_BASE);
        }
        logger().info("Processing components completed. Using preprocessor version {} for export.", preprocessorVersion);
    }

    @ApiOperation(
            value = "Exports component stubs and example files to the local filesystem."
    )
    @GetMapping("/export/sequences")
    public void sequence(Model model, @ApiParam(allowableValues = "1.2019.6,1.2020.7") @RequestParam(required = false, defaultValue = "1.2020.7") String preprocessorVersion) throws IOException {
        // Find all yaml files
        List<ApplicationDefinition> pumlComponents = new LinkedList<>(importService.findAllServiceEndpoints());
        if ("1.2020.7".equalsIgnoreCase(preprocessorVersion)) {
            dataProcessorService.processApplication(pumlComponents, SEQUENCE_V1_2020_7_DIAGRAM_BASE);
        } else {
            dataProcessorService.processApplication(pumlComponents, SEQUENCE_V1_2019_6_DIAGRAM_BASE);
        }
        logger().info("Processing sequences completed. Using preprocessor version {} for export.", preprocessorVersion);
    }

}
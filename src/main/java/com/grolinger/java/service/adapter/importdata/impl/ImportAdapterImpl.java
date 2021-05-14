package com.grolinger.java.service.adapter.importdata.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.grolinger.java.service.adapter.importdata.ImportAdapter;
import com.grolinger.java.service.data.ApplicationDefinition;
import com.grolinger.java.service.data.InterfaceDefinition;
import com.grolinger.java.service.data.ServiceDefinition;
import com.grolinger.java.service.data.importdata.ImportedServices;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.grolinger.java.service.NameConverter.replaceUnwantedPlantUMLCharacters;

/**
 * This service maps information from the yaml files to internal *definitions.
 */
@Slf4j
@Service
public class ImportAdapterImpl implements ImportAdapter {
    private static final String GLOBAL_FILE_EXPORT_PATH = System.getProperty("user.dir") + File.separator + "target" + File.separator;

    @Override
    public List<ApplicationDefinition> findAllServiceEndpoints() {
        List<Path> collect = new LinkedList<>();
        // TODO either open file chooser or use args from console

        collect = getFileList(collect);

        if (collect.isEmpty()) {
            log.warn("No Yaml file found. Please check working directory in run configuration.");
        }

        Map<String, ApplicationDefinition> app = mapFilesToDefinitions(collect);
        // Unwrap from map
        return new LinkedList<>(app.values());
    }

    /**
     * Reads the files listed in collect from the filesystem path and maps it to the internal data modek
     * @param collect File List
     * @return map with application_service as key and the definitions as value
     */
    private Map<String, ApplicationDefinition> mapFilesToDefinitions(List<Path> collect) {
        List<ImportedServices> services = new LinkedList<>();
        for (Path filesToMap : collect) {
            services.addAll(readYamlFilesFromFilesystem(filesToMap));
        }

        Map<String, ApplicationDefinition> app = new HashMap<>();

        for (ImportedServices importedServices : services) {
            if (importedServices == null || importedServices.getServices() == null) {
                continue;
            }
            int orderPrio = Integer.parseInt(importedServices.getOrderPrio());
            log.debug("{}, {}, {}", importedServices.getApplication(), importedServices.getSystemType(), importedServices.getOrderPrio());
            ApplicationDefinition pumlComponent;
            // Do we know this application already from before, reuse it.
            if (app.containsKey(importedServices.getApplication())) {
                pumlComponent = app.get(importedServices.getApplication());
            } else {
                // we use the specified alias or the application name cleaned from some special characters that make problems in plantuml
                String alias = StringUtils.isEmpty(importedServices.getCustomAlias()) ?
                        replaceUnwantedPlantUMLCharacters(importedServices.getApplication().toLowerCase(), false)
                                .replaceAll("_", "") :
                        importedServices.getCustomAlias();
                // we use the specified label or the application name cleaned from some special characters that make problems in plantuml
                String label = StringUtils.isEmpty(importedServices.getCustomLabel()) ?
                        replaceUnwantedPlantUMLCharacters(importedServices.getApplication(), false) :
                        importedServices.getCustomLabel();

                pumlComponent = ApplicationDefinition.builder()
                        .name(importedServices.getApplication())
                        .label(label)
                        .alias(alias)
                        .systemType(importedServices.getSystemType())
                        .orderPrio(orderPrio)
                        .serviceDefinitions(new LinkedList<>())
                        .build();
            }
            mapIntegrationTypes(importedServices, pumlComponent);
            app.put(importedServices.getApplication(), pumlComponent);
        }
        return app;
    }

    /**
     * Walks the target/ directory and searches for yaml-files containing definitions of plantuml services
     */
    private List<Path> getFileList(List<Path> collect) {
        try (Stream<Path> input = Files.walk(Paths.get(GLOBAL_FILE_EXPORT_PATH))) {
            collect = input
                    .filter(Objects::nonNull)
                    .filter(Files::isRegularFile)
                    .filter(YamlPredicate::isYamlFile)
                    .collect(Collectors.toList());
        } catch (IOException ioe) {
            log.error("Failed to find yaml files", ioe);
        }
        return collect;
    }

    private void mapIntegrationTypes(ImportedServices importedServices, ApplicationDefinition pumlComponent) {
        // Iterate over Services.<REST|SOAP|...>
        for (String interfacesIntegrationType : importedServices.getServices().keySet()) {
            Map<String, String[]> serviceList = importedServices.getServices().get(interfacesIntegrationType);
            // Iterate over the services itself
            List<ServiceDefinition> serviceDefinitions = mapServiceDefinitions(importedServices, interfacesIntegrationType, serviceList);
            pumlComponent.getServiceDefinitions().addAll(serviceDefinitions);
        }
    }

    /**
     * Extracts the services from yaml file and maps them hierarchically to internal structures.
     *
     * @param importedServices          service definitions from the yaml
     * @param interfacesIntegrationType type of integration
     * @param serviceList               list of services
     * @return list of mapped services
     */
    private List<ServiceDefinition> mapServiceDefinitions(ImportedServices importedServices, String interfacesIntegrationType, Map<String, String[]> serviceList) {
        List<ServiceDefinition> serviceDefinitions = new LinkedList<>();
        for (Map.Entry<String, String[]> serviceName : serviceList.entrySet()) {
            String[] interfaceNames = serviceName.getValue();
            String clearServiceName = serviceName.getKey();
            if (clearServiceName.startsWith("_") || clearServiceName.startsWith("/")) {
                clearServiceName = clearServiceName.substring(1);
            }
            ServiceDefinition serviceDefinition = ServiceDefinition.builder()
                    .serviceLabel(serviceName.getKey())
                    .serviceName(clearServiceName)
                    .domainColor(importedServices.getDomainColor())
                    .build();
            //Interfaces
            List<InterfaceDefinition> interfaceDefinitionsList = mapInterfaces(importedServices, interfacesIntegrationType, interfaceNames, importedServices.getDomainColor());
            serviceDefinition.getInterfaceDefinitions().addAll(interfaceDefinitionsList);
            serviceDefinitions.add(serviceDefinition);
        }
        return serviceDefinitions;
    }

    /**
     * Extracts the interfaces from the yaml file
     *
     * @param importedServices          Imported services from yaml
     * @param interfacesIntegrationType integration such as REST::JSON or SOAP::XML
     * @param interfaceNames            a number of interface names
     * @param domainColor               application domain color
     * @return list of interfaces
     */
    private List<InterfaceDefinition> mapInterfaces(ImportedServices importedServices, String interfacesIntegrationType, String[] interfaceNames, String domainColor) {
        List<InterfaceDefinition> interfaceDefinitions = new LinkedList<>();
        for (String interfaceName : interfaceNames) {
            InterfaceDefinition interfaceDefinition = InterfaceDefinition.builder()
                    .originalInterfaceName(interfaceName)
                    .customAlias(importedServices.getCustomAlias())
                    .integrationType(interfacesIntegrationType)
                    .applicationDomainColor(domainColor)
                    .linkToComponent(importedServices.getLinkToComponent())
                    .linkToCustomAlias(importedServices.getLinkToCustomAlias())
                    .build();
            // ignore call stack information
            log.debug("Extracted interface: {}", interfaceDefinition.getName());
            interfaceDefinitions.add(interfaceDefinition);
        }
        return interfaceDefinitions;
    }

    /**
     * Imports yaml and maps them to internal types
     *
     * @param path in filesystem from where the yamls are loaded
     * @return list of mapped services/applications
     */
    private List<ImportedServices> readYamlFilesFromFilesystem(final Path path) {
        List<ImportedServices> importedServicesList = new LinkedList<>();
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        try {
            ImportedServices importedServices = mapper.readValue(path.toFile(), ImportedServices.class);
            if (log.isInfoEnabled()) {
                log.info(ReflectionToStringBuilder.toString(importedServices, ToStringStyle.MULTI_LINE_STYLE));
            }
            importedServicesList.add(importedServices);
        } catch (Exception e) {
            //Do nothing
            log.error("mapYamls exception: {}", e.getMessage());
        }
        return importedServicesList;
    }

}

package com.grolinger.java.service.adapter.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.grolinger.java.service.adapter.ImportService;
import com.grolinger.java.service.adapter.importdata.ImportedServices;
import com.grolinger.java.service.data.ApplicationDefinition;
import com.grolinger.java.service.data.InterfaceDefinition;
import com.grolinger.java.service.data.ServiceDefinition;
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
@Service
public class ImportServiceImpl implements ImportService {
    private static final String GLOBAL_FILE_EXPORT_PATH = System.getProperty("user.dir") + File.separator + "target" + File.separator;

    @Override
    public List<ApplicationDefinition> findAllServiceEndpoints() {
        List<Path> collect = new LinkedList<>();
        // TODO either open file chooser or use args from console

        try (Stream<Path> input = Files.walk(Paths.get(GLOBAL_FILE_EXPORT_PATH))) {
            collect = input
                    .filter(Files::isRegularFile)
                    .filter(YamlPredicate::isYamlFile)
                    .collect(Collectors.toList());
        } catch (IOException ioe) {
            logger().error("Failed to find yaml files", ioe);
        }

        if (collect.isEmpty()) {
            logger().warn("No Yaml file found. Please check working directory in run configuration.");
        }

        List<ImportedServices> services = new LinkedList<>();
        for (Path filesToMap : collect) {
            services.addAll(mapYamls(filesToMap));
        }
        Map<String, ApplicationDefinition> app = new HashMap<>();

        for (ImportedServices importedServices : services) {
            if (importedServices == null || importedServices.getServices() == null) {
                continue;
            }
            int orderPrio = Integer.parseInt(importedServices.getOrderPrio());
            logger().debug("{}, {}, {}", importedServices.getApplication(), importedServices.getSystemType(), importedServices.getOrderPrio());
            final String applicationName = replaceUnwantedPlantUMLCharacters(importedServices.getApplication(), false);
            ApplicationDefinition pumlComponent;
            // Do we know this application already from before, reuse it.
            if (app.containsKey(applicationName)) {
                pumlComponent = app.get(applicationName);
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
                        .name(applicationName)
                        .label(label)
                        .alias(alias)
                        .systemType(importedServices.getSystemType())
                        .orderPrio(orderPrio)
                        .serviceDefinitions(new LinkedList<>())
                        .build();
            }
            mapIntegrationTypes(importedServices, pumlComponent);
            app.put(applicationName, pumlComponent);
        }
        // Unwrap from map
        return new LinkedList<>(app.values());
    }

    private void mapIntegrationTypes(ImportedServices importedServices, ApplicationDefinition pumlComponent) {
        // Iterate over Services.<REST|SOAP|...>
        for (String interfacesIntegrationType : importedServices.getServices().keySet()) {
            LinkedHashMap<String, String[]> serviceList = importedServices.getServices().get(interfacesIntegrationType);
            // Iterate over the services itself
            List<ServiceDefinition> serviceDefinitions = mapServiceDefinitions(importedServices, interfacesIntegrationType, serviceList);
            pumlComponent.getServiceDefinitions().addAll(serviceDefinitions);
        }
    }

    /**
     * Extracts the services from yaml file
     * @param importedServices
     * @param interfacesIntegrationType
     * @param serviceList
     * @return
     */
    private List<ServiceDefinition> mapServiceDefinitions(ImportedServices importedServices, String interfacesIntegrationType, LinkedHashMap<String, String[]> serviceList) {
        List<ServiceDefinition> serviceDefinitions = new LinkedList<>();
        for (Map.Entry<String, String[]> serviceName : serviceList.entrySet()) {
            String[] interfaceNames = serviceName.getValue();
            String clearServiceName= serviceName.getKey();
            if (clearServiceName.startsWith("_") || clearServiceName.startsWith("/")) {
                clearServiceName = clearServiceName.substring(1);
            }
            ServiceDefinition serviceDefinition = ServiceDefinition.builder()
                    .serviceLabel(serviceName.getKey())
                    .serviceName(clearServiceName)
                    .domainColor(importedServices.getDomainColor())
                    .build();
            //Interfaces
            List<InterfaceDefinition> interfaceDefinitionsList = mapInterfaces(importedServices, interfacesIntegrationType, interfaceNames);
            serviceDefinition.getInterfaceDefinitions().addAll(interfaceDefinitionsList);
            serviceDefinitions.add(serviceDefinition);
        }
        return serviceDefinitions;
    }

    /**
     * Extracts the interfaces from the yaml file
     * @param importedServices
     * @param interfacesIntegrationType
     * @param interfaceNames
     * @return
     */
    private List<InterfaceDefinition> mapInterfaces(ImportedServices importedServices, String interfacesIntegrationType, String[] interfaceNames) {
        List<InterfaceDefinition> interfaceDefinitions = new LinkedList<>();
        for (String interfaceName : interfaceNames) {
            InterfaceDefinition interfaceDefinition = InterfaceDefinition.builder()
                    .originalInterfaceName(interfaceName)
                    .customAlias(importedServices.getCustomAlias())
                    .integrationType(interfacesIntegrationType)
                    .linkToComponent(importedServices.getLinkToComponent())
                    .linkToCustomAlias(importedServices.getLinkToCustomAlias())
                    .build();
            // ignore call stack information
            logger().debug("Extracted interface: {}", interfaceDefinition.getName());
            interfaceDefinitions.add(interfaceDefinition);
        }
        return interfaceDefinitions;
    }

    private List<ImportedServices> mapYamls(final Path path) {
        List<ImportedServices> importedServicesList = new LinkedList<>();
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        try {
            ImportedServices importedServices = mapper.readValue(path.toFile(), ImportedServices.class);
            if (logger().isInfoEnabled()) {
                logger().info(ReflectionToStringBuilder.toString(importedServices, ToStringStyle.MULTI_LINE_STYLE));
            }
            importedServicesList.add(importedServices);
        } catch (Exception e) {
            //Do nothing
            logger().error("mapYamls exception: {}", e.getMessage());
        }
        return importedServicesList;
    }

}

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

import static com.grolinger.java.service.NameService.replaceUnwantedCharacters;

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
            final String applicationName = replaceUnwantedCharacters(importedServices.getApplication(), false);
            ApplicationDefinition pumlComponent;
            if (app.containsKey(applicationName)) {
                pumlComponent = app.get(applicationName);
            } else {
                String alias = StringUtils.isEmpty(importedServices.getCustomAlias()) ?
                        replaceUnwantedCharacters(importedServices.getApplication().toLowerCase(), false) :
                        importedServices.getCustomAlias();
                String label = StringUtils.isEmpty(importedServices.getCustomLabel()) ?
                        replaceUnwantedCharacters(importedServices.getApplication(), false) :
                        importedServices.getCustomLabel();
                pumlComponent = ApplicationDefinition.builder().name(applicationName).label(label).alias(alias)
                        .serviceDefinitions(new LinkedList<>())
                        .build();
            }

            List<ServiceDefinition> serviceDefinitions = new LinkedList<>();
            // Iterate over Services.<REST|SOAP|...>
            for (String interfacesIntegrationType : importedServices.getServices().keySet()) {
                String path = "";
                LinkedHashMap<String, String[]> serviceList = importedServices.getServices().get(interfacesIntegrationType);
                // Iterate over the services itself
                for (Map.Entry<String, String[]> serviceName : serviceList.entrySet()) {
                    String[] services1 = serviceName.getValue();
                    ServiceDefinition serviceDefinition = new ServiceDefinition(serviceName.getKey(), importedServices.getSystemType(), importedServices.getDomainColor(), orderPrio);
                    //Interfaces
                    logger().info("Current path: {}", path);
                    for (String interfaceName : services1) {
                        InterfaceDefinition interfaceDefinition = new InterfaceDefinition(interfaceName, importedServices.getCustomAlias(), interfacesIntegrationType, importedServices.getLinkToComponent(), importedServices.getLinkToCustomAlias());
                        // ignore call stack information
                        logger().info("Extracted interface: {}", interfaceDefinition.getName());
                        serviceDefinition.getInterfaceDefinitions().add(interfaceDefinition);
                    }
                    serviceDefinitions.add(serviceDefinition);
                }
            }
            pumlComponent.getServiceDefinitions().addAll(serviceDefinitions);
            app.put(applicationName, pumlComponent);
        }
        // Unwrap from map
        return new LinkedList<>(app.values());
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

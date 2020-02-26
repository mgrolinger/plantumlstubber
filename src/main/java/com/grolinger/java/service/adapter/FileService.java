package com.grolinger.java.service.adapter;

import com.grolinger.java.config.Loggable;
import com.grolinger.java.controller.templatemodel.DiagramType;
import com.grolinger.java.service.data.InterfaceDefinition;
import com.grolinger.java.service.data.ServiceDefinition;
import com.grolinger.java.service.data.export.ComponentFile;
import com.grolinger.java.service.data.export.ExampleFile;
import org.thymeleaf.context.Context;

import java.io.IOException;
import java.util.Map;

/**
 * Interface for all file based processes, e.g. to export each iuml file
 */
public interface FileService extends Loggable {
    /**
     * Creates a directory for a ervice
     *
     * @param basePath          the path from where the repository is starting
     * @param serviceDefinition the service definition
     * @return returns the path of the created directory
     * @throws IOException
     */
    String createServiceDirectory(final String basePath, final ServiceDefinition serviceDefinition) throws IOException;

    void writeDefaultCommonFile(final String basePath, final DiagramType diagramType) throws IOException;

    /**
     * Writes the example file after all interface files are exported
     *
     * @param basePath        the common base path
     * @param applicationName the name of the application for which the exmple file should be written
     * @param exampleFile     the content
     */
    void writeExampleFile(final String basePath, final String applicationName, final String exampleFile);

    ExampleFile writeInterfaceFile(final String currentPath, final ServiceDefinition serviceDefinition, final InterfaceDefinition currentInterface, Context context, ExampleFile exampleFile);

    /**
     * Method that creates a directory in the local file system.
     *
     * @param fullPath Path to the directory to be created
     */
    void createParentDir(final String fullPath);

    /**
     * Creates a specified directory
     *
     * @param basePath        The absolute base path from which the repository is created
     * @param path            which
     * @param dirsCreate      a set of already created directories to prevent directories created multiple times
     * @param applicationName for which application should the directory be created
     * @return path to the newly created directory
     */
    String createDirectory(final String basePath, String path, Map<String, String> dirsCreate, final String applicationName);

    /**
     * Calculate the relative common path which is later used in the templates and the
     * resulting plantuml files to relatively include other files
     *
     * @param applicationName the name of the application
     * @param serviceName     the name of the service
     * @param interfaceName   the name of the interface
     * @return the complete path
     */
    String getRelativeCommonPath(final String applicationName, final String serviceName, final String interfaceName);

    /**
     * Writes a single file for an application_service_interface
     *
     * @param diagramType   either component|sequence
     * @param componentFile the content of the file
     * @throws IOException
     */
    void writeComponentFile(final DiagramType diagramType, ComponentFile componentFile) throws IOException;
}

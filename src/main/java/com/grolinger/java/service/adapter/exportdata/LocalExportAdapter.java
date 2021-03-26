package com.grolinger.java.service.adapter.exportdata;

import com.grolinger.java.controller.templatemodel.DiagramType;
import com.grolinger.java.service.data.ApplicationDefinition;
import com.grolinger.java.service.data.InterfaceDefinition;
import com.grolinger.java.service.data.ServiceDefinition;
import com.grolinger.java.service.data.exportdata.ComponentFile;
import com.grolinger.java.service.data.exportdata.ExampleFile;
import org.thymeleaf.context.Context;

import java.io.IOException;

public interface LocalExportAdapter {
    /**
     * Creates a directory for a service
     *
     * @param basePath          the path from where the repository is starting
     * @param applicationDefinition the definition for the application
     * @param serviceDefinition the service definition
     * @return returns the path of the created directory
     * @throws IOException the reason why creating the directory didn't work
     */
    String createServiceDirectory(final String basePath, final ApplicationDefinition applicationDefinition, final ServiceDefinition serviceDefinition) throws IOException;

    /**
     * Writes the example file after all interface files are exported
     *
     * @param basePath        the common base path
     * @param currentApplication the current application for which the example file should be written
     * @param exampleFile     the content
     */
    void writeExampleFile(final String basePath, final ApplicationDefinition currentApplication, final String exampleFile);

    /**
     * Writes a iuml file that represents an interface
     *
     * @param currentPath        the current root path for this application
     * @param currentApplication the current application
     * @param serviceDefinition  the current service
     * @param currentInterface   the current interface
     * @param context            the context for the application, service, interface
     * @param exampleFile        the current example file for this application
     * @return the example file with all interfaces for later export
     */
    ExampleFile writeInterfaceFile(final String currentPath, final ApplicationDefinition currentApplication, final ServiceDefinition serviceDefinition, final InterfaceDefinition currentInterface, Context context, ExampleFile exampleFile);

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
     * @param application for which application the directory should be created
     * @return path to the newly created directory
     */
    String createDirectory(final String basePath, String path, final ApplicationDefinition application);

    /**
     * Writes a single file for an application_service_interface
     *
     * @param diagramType   either component|sequence
     * @param componentFile the content of the file
     * @throws IOException the reason why the component file couldn't be written
     */
    void writeComponentFile(final DiagramType diagramType, ComponentFile componentFile) throws IOException;
}

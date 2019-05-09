package com.grolinger.java.service;

import com.grolinger.java.config.Loggable;
import com.grolinger.java.config.Services;
import com.grolinger.java.controller.Template;
import org.thymeleaf.context.Context;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;

public interface FileService extends Loggable {
    String createServiceDirectory(final String basePath, final Set<String> dirsCreate, final String applicationName, final String serviceName) throws IOException;

    void writeEmptyCommonFile(final String basePath) throws IOException;

    void writeExampleFile(String basePath, String applicationName, StringBuilder exampleFile);

    StringBuilder writeInterfaceFile(final Template template, String currentPath, Context context);

    void createParentDir(String fullPath);

    String createDirectory(String basePath, String path, Set<String> dirsCreate, String applicationName);

    String getRelativeCommonPath(String serviceName);

    List<Services> findAllYamlFiles();

    List<Services> mapYamls(Path path);
}

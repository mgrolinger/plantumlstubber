package com.grolinger.java.service.adapter.exportdata;

import com.grolinger.java.controller.templatemodel.DiagramType;

import java.io.IOException;
import java.nio.file.Path;

public interface LocalStaticAdapter {
    void copyFile(final String folder, final String currentFileName);

    void exportTemplate();

    void readFile(final String inputFileName, final Path outputFile);

    /**
     * Writes the important common.iuml file that contains a lot of the common definitions, such as functions or skinparams
     *
     * @param basePath    To where the file(s) will be exported
     * @param diagramType For which diagram type (component/sequence) the specific common.iuml is exported
     */
    void writeDefaultCommonFile(final String basePath, final DiagramType diagramType) throws IOException;

    /**
     * Writes skin files to the export directory.
     *
     * @throws IOException
     */
    void writeDefaultSkinFiles() throws IOException;
}

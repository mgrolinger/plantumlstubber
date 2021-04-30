package com.grolinger.java.service.data;

import java.util.List;

import static com.grolinger.java.controller.templatemodel.Constants.DIR_UP;

public interface CommonRootPathHandler {
    /**
     * "Calculates" the path to the root directory to be able to descent into the common directory.
     * All generated iuml files load the common.iuml file that contains a number of commonly used
     * !functions and !procedures and also loads participants or components depending on the type of
     * diagram
     *
     * @return {@code ../../} default | a number of {@code ../} depending on how many slashes are in the service's name
     */
    default String getPathToRoot() {
        // the default value is one directory up (../) because of the application's directory itself
        StringBuilder cp = new StringBuilder();
        int size = (getNameParts() == null) ? 0 : getNameParts().size();
        // The last part of the interface is a file and therefore we need to subtract
        // 1 from number of directories to go up in the hierarchy
        if (isInterface()) size--;

        cp.append(String.valueOf(DIR_UP.getValue()).repeat(Math.max(0, size)));
        return cp.toString();
    }

    List<String> getNameParts();

    boolean isInterface();
}

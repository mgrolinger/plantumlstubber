package com.grolinger.java.service.adapter.importdata.impl;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.nio.file.Path;

/**
 * Predicate to check if this is a yaml file or not.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
class YamlPredicate {
    static boolean isYamlFile(Path path) {
        return path.toAbsolutePath().toString().toLowerCase().endsWith(".yaml") ||
                path.toAbsolutePath().toString().toLowerCase().endsWith(".yml");
    }
}
package com.grolinger.java.service.data.mapper;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * System type is the mapper to mediate between the yaml property systemType
 * the component diagram definitions from component_definition.iuml (either fontAwesome or uml visual style)
 * and the sequence diagram definitions from participant_definition.iuml
 */
@Getter
@AllArgsConstructor
public enum SystemType {
    COMPONENT("component", "FA_SERVER", "participant"),
    SPRINGBOOT("component", "FA_SPRINGBOOT", "participant"),
    SOLR("component", "FA_SOLR", "participant"),
    ARCHIVE("component", "FA_ARCHIVE", "participant"),
    ZIP("component", "FA_FILE_ZIP_O", "entity"),
    DATABASE("database", "FA_DATABASE", "database"),
    USERS("actor", "FA_USERS", "actor"),
    ADDRESS_BOOK("component", "FA_ADDRESS_BOOK", "participant"),
    EMAIL("component", "FA_ENVELOPE", "entity"),
    FOLDER("folder", "FA_FOLDER", "participant"),
    DOCUMENTS("collections", "FA_FILE_O", "collections"),
    JENKINS("component", "DEV_JENKINS", "participant");

    private final String umlStrict;
    private final String fontAwesome;
    private final String sequenceName;

    public static SystemType getFrom(final String systemType) {
        // first compare the names
        for (SystemType type : SystemType.values()) {
            if (type.name().equalsIgnoreCase(systemType)) {
                return type;
            }
        }
        // last resort
        switch (systemType) {
            case "application":
                return COMPONENT;
            default:
                return COMPONENT;
        }
    }
}

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
    //TYPE      what's used in case                         what def. is used in sequence diagrams
    //          UML_STRICT=true     what def. is used in default
    //                              case for component diagrams
    COMPONENT(Constants.PUML_COMPONENT_NAME,    "FA_SERVER",        Constants.PUML_PARTICIPANT_NAME),
    SPRINGBOOT(Constants.PUML_COMPONENT_NAME,   "FA_SPRINGBOOT",    Constants.PUML_PARTICIPANT_NAME),
    SOLR(Constants.PUML_COMPONENT_NAME,         "FA_SOLR",          Constants.PUML_PARTICIPANT_NAME),
    ARCHIVE(Constants.PUML_COMPONENT_NAME,      "FA_ARCHIVE",       Constants.PUML_PARTICIPANT_NAME),
    ZIP(Constants.PUML_COMPONENT_NAME,          "FA_FILE_ZIP_O",    Constants.PUML_ENTITY_NAME),
    DATABASE(Constants.PUML_DATABASE_NAME,      "FA_DATABASE",      Constants.PUML_DATABASE_NAME),
    USERS(Constants.PUML_ACTOR_NAME,            "FA_USERS",         Constants.PUML_ACTOR_NAME),
    ADDRESS_BOOK(Constants.PUML_COMPONENT_NAME, "FA_ADDRESS_BOOK",  Constants.PUML_PARTICIPANT_NAME),
    EMAIL(Constants.PUML_COMPONENT_NAME,        "FA_ENVELOPE",      Constants.PUML_ENTITY_NAME),
    FOLDER(Constants.PUML_FOLDER_NAME,          "FA_FOLDER",        Constants.PUML_PARTICIPANT_NAME),
    DOCUMENTS(Constants.PUML_COLLECTIONS_NAME,  "FA_FILE_O",        Constants.PUML_COLLECTIONS_NAME),
    JENKINS(Constants.PUML_COMPONENT_NAME,      "DEV_JENKINS",      Constants.PUML_PARTICIPANT_NAME),
    TERMINAL(Constants.PUML_COMPONENT_NAME,     "DEV_TERMINAL",     Constants.PUML_PARTICIPANT_NAME);

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
        return COMPONENT;
    }

    private static class Constants {
        private static final String PUML_COMPONENT_NAME = "component";
        private static final String PUML_PARTICIPANT_NAME = "participant";
        private static final String PUML_ENTITY_NAME = "entity";
        private static final String PUML_DATABASE_NAME = "database";
        private static final String PUML_COLLECTIONS_NAME = "collections";
        private static final String PUML_FOLDER_NAME = "folder";
        private static final String PUML_ACTOR_NAME = "actor";
    }
}

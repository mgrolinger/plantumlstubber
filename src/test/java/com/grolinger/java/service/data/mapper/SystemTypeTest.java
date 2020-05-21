package com.grolinger.java.service.data.mapper;

import org.testng.annotations.Test;

import static com.grolinger.java.service.data.mapper.SystemType.*;
import static org.assertj.core.api.Assertions.assertThat;

public class SystemTypeTest {

    @Test
    public void testGetFrom() {
        // component -> COMPONENT (Name matches, like component, database, etc.)
        assertThat(SystemType.getFrom("component")).isEqualTo(COMPONENT);
        assertThat(SystemType.getFrom("fOlDeR")).isEqualTo(FOLDER);

        // springboot -> SPRINGBOOT
        assertThat(SystemType.getFrom("springBoot")).isEqualTo(SPRINGBOOT);
        // folder -> FOLDER
        assertThat(SystemType.getFrom("fOlDeR")).isEqualTo(FOLDER);
        // DEFAULT
        // Unknown -> COMPONENT
        assertThat(SystemType.getFrom("unknown")).isEqualTo(COMPONENT);
        // application -> component
        assertThat(SystemType.getFrom("application")).isEqualTo(COMPONENT);
    }

    @Test
    public void testGetUmlStrict() {
        // component -> COMPONENT (Name matches, like component, database, etc.)
        assertThat(COMPONENT.getUmlStrict()).isEqualTo("component");
        // USERS -> actor
        assertThat(USERS.getUmlStrict()).isEqualTo("actor");
        // DATABASE -> database
        assertThat(DATABASE.getUmlStrict()).isEqualTo("database");
        // FOLDER -> folder
        assertThat(FOLDER.getUmlStrict()).isEqualTo("folder");
        // DOCUMENTS -> collections
        assertThat(DOCUMENTS.getUmlStrict()).isEqualTo("collections");
    }

    @Test
    public void testGetFontAwesome() {
        // component -> FA_SERVER
        assertThat(COMPONENT.getFontAwesome()).isEqualTo("FA_SERVER");
        // springboot has its own type
        assertThat(SPRINGBOOT.getFontAwesome()).isEqualTo("FA_SPRINGBOOT");
        // USERS -> FA_USERS
        assertThat(USERS.getFontAwesome()).isEqualTo("FA_USERS");
        // DATABASE -> FA_DATABASE
        assertThat(DATABASE.getFontAwesome()).isEqualTo("FA_DATABASE");
        // FOLDER -> FA_FOLDER
        assertThat(FOLDER.getFontAwesome()).isEqualTo("FA_FOLDER");
        // DOCUMENTS -> FA_FILE_O
        assertThat(DOCUMENTS.getFontAwesome()).isEqualTo("FA_FILE_O");
        // JENKINS -> DEV_JENKINS
        assertThat(JENKINS.getFontAwesome()).isEqualTo("DEV_JENKINS");
        // ZIP -> FA_FILE_ZIP_O
        assertThat(ZIP.getFontAwesome()).isEqualTo("FA_FILE_ZIP_O");
    }

    @Test
    public void testGetSequenceName() {
        // component -> participant
        assertThat(COMPONENT.getSequenceName()).isEqualTo("participant");
        // springboot -> participant
        assertThat(SPRINGBOOT.getSequenceName()).isEqualTo("participant");
        // FOLDER -> participant
        assertThat(FOLDER.getSequenceName()).isEqualTo("participant");
        // USERS -> FA_USERS
        assertThat(USERS.getSequenceName()).isEqualTo("actor");
        // DATABASE -> database
        assertThat(DATABASE.getSequenceName()).isEqualTo("database");
        // DOCUMENTS -> collections
        assertThat(DOCUMENTS.getSequenceName()).isEqualTo("collections");
        // JENKINS -> participant
        assertThat(JENKINS.getSequenceName()).isEqualTo("participant");
        // ZIP -> entity
        assertThat(ZIP.getSequenceName()).isEqualTo("entity");
    }
}
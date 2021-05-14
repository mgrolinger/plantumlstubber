# Release Notes

## 0.5.7 (unreleased)

### New + Improved

### Fixes

### Removed

### Updates

## 0.5.6 (2021-05-14)

### New + Improved
* moved Code from Java 8 -> 11
* migrated from springfox 2 -> springdoc-openapi-ui
* added password to route /shutdown 

### Fixes
* case-sensitive packages where not imported correctly

### Removed
* Removed the support for old 1.2019.6 preprocessor templates 

### Updates
* spring-boot             2.4.1 -> 2.4.5
* commons-lang3           3.10 -> 3.12.0
* testng                  7.3.0 -> 7.4.0
* lombok                  1.18.12 -> 1.18.20
* springdoc               1.5.5 -> 1.5.8
* jackson-dataformat-yaml 2.12.1 -> 2.12.3

## 0.5.5 (2021-01-12)

### New + Improved
* documentation improved
* introduced maven prerequisite version

### Fixes
* fixed usage of Spring obsolete methods
* removed old non-working SingleController
* the project is now buildable with jdk 1.8 or 15
* excluded jackson dependency from databind as it is coming with spring-boot

### Updates
* spring-boot             2.3.2 -> 2.4.1
* springfox-swagger-ui    2.9.2 -> 3.0.0
* springfox-swagger2      2.9.2 -> 3.0.0
* testng                  7.1.0 -> 7.3.0
* assertj-core            3.16.1 -> 3.18.1
* jackson                 2.11.3 -> 2.12.1

## 0.5.4 (2020-08-07)

### New + Improved
* new _domain_ definition that allows an interface to have a different domain (and therefore color) than the main application
* more tests

### Fixes
* Depending on the position of the definition methods could end up in filenames or procedure names  

### Updates
- Spring boot Version 2.3.2.RELEASE

## 0.5.3 (2020-07-10)

### New + Improved

- it is now possible to assign a different domain to an interface, which makes sense in larger applications that contain more than one domain
- moved package _importdata_
- renamed package export to _exportdata_
- added numerous tests
- multiple refactorings for readability
- assertj for readability

### Fixes

- major refactoring finally solves the common path problem or to little or to many ../ 
- console runner works again and has the preprocessor version as new argument
- Export of skin and common files also works when started as jar

### Updates


## 0.5.2 (2020-05-20)

### New + Improved
- RFC-7807 problem detail function for sequence diagrams
- New function for component diagrams to create self links, e.g. to show timed actions
- removed own Logger interface in favor for lombok
- use plugin maven-release-plugin
- use plugin versions-maven-plugin 
- new REST route to export the template to specify an application

### Fixes
- solved distorted display of individual rest methods
- removed obsolete procedures
- removed picoli that was never used

### Updates
- Update spring-boot 2.3.0.RELEASE
- Update spring-boot-parent 2.3.0.RELEASE

## 0.5.1 (2020-05-08)

### New + Improved
- Support for the new preprocessor feature "procedure", which can be used by selecting preprocessor version 1.2020.7 in the swagger ui
- new static includes, such as the common.iuml, can be put into the resource folder and will be written to the export directory
- common.iuml that contain already a lot of functions for component or sequence diagrams
- http status code definitions as helper in sequence diagrams

### Fixes
- alias and application name run apart, which leads to misbehavior of components
- the component template had wrong variable definition 
- banner.txt updated

## unreleased (yyyy-MM-dd)

### New + Improved

### Fixes

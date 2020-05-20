# Release Notes

## 0.5.2 (2020-05-20)

### New + Improved
- RFC-7807 problem detail function for sequence diagrams
- New function for component diagrams to create self links, e.g. to show timed actions
- removed own Logger interface in favor for lombok
- use plugin maven-release-plugin
- use plugin versions-maven-plugin 

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
- component template had wrong variable definition 
- banner.txt updated

## unreleased (yyyy-MM-dd)

### New + Improved

### Fixes

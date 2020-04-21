# PlantUMLStubber for Generating PlantUML Repositories

This project can be used to generate stubs for component diagrams and sequence diagrams.
The generator provides a swagger ui on http://localhost:19191/swagger-ui.html#/

## YAML configuration

There is an example file that shows how a yaml file needs to be configured.
Use _template_newApplication.yaml as starting point and copy it.

## Intellij Configuration

You need to configure the working directory  (Java `user.dir`) in Run/Debug of the `PlantUMLStubber` 
(formerly known as`ServiceGenerator`) to the root directory of the module so that the service 
configuration yaml can be found, 
e.g. `$MODULE_WORKING_DIR$` in Intellij: ![](Intellij_Config.png)


## Todo

* finish Readme 
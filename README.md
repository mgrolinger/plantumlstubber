# PlantUMLStubber for Generating PlantUML stubs

This project can be used to generate stubs for component diagrams and sequence diagrams.
The stubber will generate files in the target/ directory. These files are either puml or iuml files. The iuml-files contain "todo" marker that are supposed to be filled in by you.
I use it to build a repository of re-usable files (services/application) that come in handy if I need to discuss about software architecture.

### Basic notation
That is how I do it:

*.iuml are includible files that contain a re-usable service definition and may be used with !include
*.puml are plantuml files that are self-sufficient and should not be !included in other files. These files, however, may include .iuml files

This maybe your understanding as well or not, but the stubber will mostly generate .iuml files. 

## Swagger UI
The generator provides a swagger ui on http://localhost:19191/swagger-ui.html#/

## YAML configuration

There is an example file that shows how a yaml file needs to be configured.
Use _template_newApplication.yaml as starting point.

## Output

You can generate two types of diagrams:
* **Component**: Services as component diagrams, that may be used for system context diagrams (Kontextdiagramm). This diagram consist of building blocks that describe how systems interact with each other and what is in scope of an implementation and what not.

* **Sequence**: A number of sequence diagrams that are useful as addition to system context diagrams. In later stages of architectural specifications these are helpful to show how systems interact.

## Requirements for using the PlantUML stubs
* The generated files from the repository need a plantuml version >= 1.2019.6 because it uses the new V2 preprocessor or >= 1.2020.7 if you choose the newer version in the swagger ui
* Plantuml itself needs a graphviz 2.38 installation
* The common files may need some manual clean up if you go with the old version of plantuml due to the fact that they introduced procedures in 1.2020.7 that helps with the backwards compatibility but made my life a bit more difficult 

## Intellij Configuration

You need to configure the working directory  (Java `user.dir`) in Run/Debug of the `PlantUMLStubber` 
(formerly known as`ServiceGenerator`) to the root directory of the module so that the service 
configuration yaml can be found, 
e.g. `$MODULE_WORKING_DIR$` in Intellij: ![](Intellij_Config.png)

_last update 12.05.2020_